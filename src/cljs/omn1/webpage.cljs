(ns omn1.webpage
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom :refer [div ul li table tr td th thead tbody span]]
   [goog.dom :as gdom]
   [omn1.data :as dat]))

(defui CarRoot
  static om/IQuery
  (query [_] [:current/car])

  Object
  (render
   [this]
   (let [{{make :car/make} :current/car} (om/props this)]
     (div
      nil
      (div nil (str "Make: ") (dom/b nil make))))))

(om/add-root! dat/reconciler CarRoot (gdom/getElement "app"))
