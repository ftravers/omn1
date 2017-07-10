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
   (div nil (om/props this))))

(def app-state
  (atom {:greeting "Hello World"}))

(defn my-reader
  [env kee parms]
  (.log js/console env kee)
  (let [st (:state env)]
    (get-in kee st)))

(def parser
  (om/parser {:reader my-reader}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser}))

(om/add-root!
 reconciler
 SimpleUI
 (gdom/getElement "app"))
