(ns omn1.webpage
  (:require
   [omn1.coms :refer [send]]
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div input p button]]
   [goog.dom :as gdom]))

(defn log [msg & args]
  (apply js/console.log (conj args (str "[" msg "]:"))))

(defui Login
  static om/IQuery
  (query  [_] [:authenticated])
  Object
  (initLocalState [this] {:username "abc" :password "123"})

  (render
   [this]
   (log "props" (om/props this))
   (let [{:keys [username password]} (om.next/get-state this)]
     (if (:authenticated (om/props this))
       (div nil "You are logged in!")
       (div
        nil
        (input #js {:name "uname" :type "text" :placeholder "Enter Username"
                    :required true :value username
                    :onChange
                    (fn [ev]
                      (let [value (.. ev -target -value)]
                        (om/update-state! this assoc :username value)))})
        (p nil)
      
        (input #js {:name "psw" :type "password" :placeholder "Enter Password"
                    :required true :value password 
                    :onChange
                    (fn [ev]
                      (let [value (.. ev -target -value)]
                        (om/update-state! this assoc :password value)))})
        (p nil)
    
        (button #js {:onClick
                     (fn [e]
                       (let [state (om.next/get-state this)]
                         (om/transact! this `[(user/login {:user/name ~(:username state)
                                                           :user/password ~(:password state)})])))} "Login"))))))

(defmulti reader om/dispatch)

(defmulti mutate om/dispatch)

(defmethod mutate 'user/login
  [{state :state} ky params]
  {:value {:keys (keys params)}
   :remote true
   :action #(swap! state merge params)})

(defmethod reader :default
  [{st :state} key _]
  (log "default reader" key)
  {:value (key (om/db->tree [key] @st @st))
   :remote true})

(defn my-cb [cb data]
  (log "got data back" data)
  ;; (cb data)
  (cb {:name "Fred"}))

(defn make-remote-req
  [qry cb]
  (send (partial my-cb cb) qry)
  (log "qry type" (str qry))
  (log "qry keys" (keys qry))
  ;; (log "qry vals" (type (first (ffirst (vals qry)))))
  (cb {:name "Fred"}))

(def parser (om/parser {:read reader :mutate mutate}))

(defonce app-state (atom {:authenticated false}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send make-remote-req}))

(om/add-root! reconciler Login (gdom/getElement "app"))
