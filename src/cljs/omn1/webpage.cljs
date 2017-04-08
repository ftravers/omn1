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
   (log "Greeter Component Props" (om/props this))
   (div nil "Hello " (:name (om/props this)))))

(defmulti reader om/dispatch)

(defmethod reader :default
  [{st :state} key _]
  (log "Default Reader Key" key)
  (log "State" @st)
  (let [omdb-tree (om/db->tree [key] @st @st)
        resp {:value (key omdb-tree)}]
    (log "Om DB->tree" omdb-tree)
    (log "Responding With" resp)
    resp))

(def parser (om/parser {:read reader}))

(def app-state (atom {:name "Bob"}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(om/add-root! reconciler Greeter (gdom/getElement "app"))
