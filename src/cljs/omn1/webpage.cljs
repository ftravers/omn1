(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom])
  (:require-macros
   [omn1.utils :refer [log]]))

(defui Greeter
  static om/IQuery
  (query [_] [:name])
  Object
  (render
   [this]
   (div nil "Hello " (:name (om/props this)))))

(defmulti reader om/dispatch)
(defmulti mutate om/dispatch)

(defmethod reader :default
  [{st :state} key _]
  {:value (key (om/db->tree [key] @st @st))})

(defmethod mutate 'new-name
  [{state :state} ky params]
  (log "key" ky)
  (log "params" params)
  (log "state" @state)
  {:value {:keys (keys params)}
   :action #(swap! state merge params)})

(def parser (om/parser {:read reader :mutate mutate}))

(def app-state (atom {:name "Bob"}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(om/add-root! reconciler Greeter (gdom/getElement "app"))


