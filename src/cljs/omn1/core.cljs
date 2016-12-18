(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom]
   [goog.dom :as gdom]
   [omn1.data :as dat]))

(defui Blah
  static om/IQuery
  (query [this] [:currrent/user])
  Object
  (render
   [this]
   (let [data (om/props this)]
     (dom/div nil (str "Curr User: " data)))))

(om/add-root! dat/reconciler Blah (gdom/getElement "app"))
