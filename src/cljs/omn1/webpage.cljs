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
          {:name "fenton" :pword "passwErd"})

  Object
  (render
   [this]
   (div nil (str (om/props this)))))

(def app-state
  (atom {:user/authenticated false}))

(defn my-reader
  [env kee parms]
  (let [st (:state env)]
    {:value (get @st kee)
     :remote true}))

(def parser
  (om/parser {:read my-reader}))

(defn remote-connection
  [qry cb]
  (.log js/console (str (:remote qry)))
  (cb {:user/authenticated true}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send remote-connection}))

(om/add-root!
 reconciler
 SimpleUI
 (gdom/getElement "app"))
