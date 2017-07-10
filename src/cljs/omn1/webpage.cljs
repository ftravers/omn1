(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div]]
   [goog.dom :as gdom]))

(defui SimpleUI
  Object
  (render
   [this]
   (div nil "Hello World")))

(om/add-root!
 (om/reconciler {})
 SimpleUI
 (gdom/getElement "app"))
