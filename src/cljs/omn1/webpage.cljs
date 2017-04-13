(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]))

(defn log [msg & args]
  (apply js/console.log (conj args (str "[" msg "]:"))))

(defui Greeter
  static om/IQuery
  (query [_] [:name])
  Object
  (render
   [this]
   (div nil "Hello " (:name (om/props this)))))

(defmulti reader om/dispatch)
(defmulti mutate om/dispatch)

(defmethod mutate 'new-name
  [{state :state} ky params]
  {:value {:keys (keys params)}
   :action #(swap! state merge params)})

(defmethod reader :default
  [{st :state} key _]
  (log "default reader" key)
  {:value (key (om/db->tree [key] @st @st))
   :remote true})

(defn make-remote-req
  [qry cb]
  (log "remote reader qry" qry)
  (cb {:name "Fred"}))

(def parser (om/parser {:read reader :mutate mutate}))

(defonce app-state (atom {:name "Bobby"}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send make-remote-req}))

(om/add-root! reconciler Greeter (gdom/getElement "app"))
