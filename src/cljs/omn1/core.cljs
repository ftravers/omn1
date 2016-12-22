(ns omn1.core
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]))

(defui MyComponent
  static om/IQuery
  (query [this] [:user])
  Object
  (render
   [this]
   (let [data (om/props this)]
     (div nil (str data)))))

(def app-state (atom {:user {:name "Fenton"}}))

(defn reeder [{q :query st :state} _ _]
  {:value (om/db->tree q @app-state @app-state)})

(def parser (om/parser {:read reeder}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(om/add-root! reconciler MyComponent (gdom/getElement "app"))

