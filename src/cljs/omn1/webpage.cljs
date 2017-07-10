(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]))

(defui SimpleUI
  static om/IQuery
  (query [_] '[(:user/authenticated {:user/name ?name :user/password ?pword})])

  static om/IQueryParams
  (params [this]
          {:name "" :pword ""})

  Object
  (render
   [this]
   (div nil (str (om/props this)))))

(def app-state
  (atom {:user/authenticated false}))

(defn my-reader
  [env kee parms]
  (.log js/console parms)
  (let [st (:state env)]
    {:value (get @st kee)}))

(def parser
  (om/parser {:read my-reader}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(om/add-root!
 reconciler
 SimpleUI
 (gdom/getElement "app"))
