(ns omn1.core
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom]
   [goog.dom :as gdom]))

(defui HelloWorld
  Object
  (render [this] (dom/div nil "Hello World")))

(om/add-root!
 (om/reconciler {})
 HelloWorld
 (gdom/getElement "app"))
