(ns omn1.core
  (:require
   [om.next :as om :refer-macros [defui]]
   [omn1.recon :as recon]
   [om.dom :as dom]
   [goog.dom :as gdom]))

(defui HelloWorld
  static om/IQuery
  (query [this] [:title])
  Object
  (render
   [this]
   (let [title (:title (om/props this))]
     (dom/div nil title))))

(om/add-root!
 recon/reconciler
 HelloWorld
 (gdom/getElement "app"))
