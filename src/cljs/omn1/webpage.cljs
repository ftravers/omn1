(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]))

(defn log [msg & args]
  (apply js/console.log (conj args (str "[" msg "]:"))))

(defonce app-state (atom {:some-param "not much"}))

(defui Basic
  static om/IQuery
  (query  [_] [:some-param])
  
  Object
  (render
   [this]
   (let [props (om/props this)]
     (log "props" props)
     (div nil "Some Param Value: " (:some-param props)))))

(defmulti reader om/dispatch)

(defmethod reader :default
  [{st :state :as env} key _]
  (log "default reader" key "env:target" (:target env))
  {:value (key (om/db->tree [key] @st @st))
   :remote true})

(defn my-remoter
  [qry cb]
  (log "remote query" (str qry))
  (log "app state" @app-state)
  (cb {:some-param "value gotten from remote!"})
  (log "app state" @app-state))

(def parser (om/parser {:read reader}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send my-remoter}))

(om/add-root! reconciler Basic (gdom/getElement "app"))
