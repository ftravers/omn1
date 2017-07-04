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
  (query  [_] [:user/authenticated])
  Object
  (initLocalState [this] {:username "abc" :password "123"})
  (render
   [this]
   (log "props" (om/props this))
   (let [{:keys [username password]} (om.next/get-state this)]
     (if (:user/authenticated (om/props this))
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
  (let [read-data (cljs.reader/read-string data)]
    (log "got data back" (str read-data))
    (cb read-data)))

(defn make-remote-req
  [qry cb]
  (log "qry" (str qry))
  (send (partial my-cb cb) (:remote qry))
  ;; (cb {:user/login {:keys (:username :password), :valid-user true}})
  )

(def parser (om/parser {:read reader :mutate mutate}))

(defonce app-state (atom {:user/authenticated false}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send make-remote-req}))

(om/add-root! reconciler Login (gdom/getElement "app"))

;; (defn api
;;   [args]
;;   (if-let [{{user :user/name pass :user/password} :params} args]
;;     (str "user:" user "pass:" pass ".")
;;     "user-NOT-defined"))
(defn api
  [args]
  (let [{params :params} args]
    (if params
      (str "params:" params ".")
      "NOT-defined")))
