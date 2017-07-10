(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]))

(defui SimpleUI
  static om/IQuery
  (query [_] [:greeting])
  
  Object
  (render
   [this]
   (div nil (str (om/props this)))))

(def app-state
  (atom {:greeting "Hello World"}))

(defn my-reader
  [env kee parms]
  (.log js/console (:target env))
  {:value "abc"})

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
