(ns omn1.core
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom]
   [goog.dom :as gdom]))

(defonce my-state (atom {:title "Hello World 2!"}))

(defmulti readr om/dispatch)

(defmethod readr :default
  [env keyz parms]
  (let [state (:state env)
        val (:title @state)]
    {:value val}))

(def parser (om/parser {:read readr}))

(def reconciler
  (om/reconciler
   {:state my-state
    :parser parser}))

(defui HelloWorld
  static om/IQuery
  (query [this] [:blah])
  Object
  (render
   [this]
   (let [title (:blah (om/props this))]
     (dom/div nil title))))

(om/add-root!
 reconciler
 HelloWorld
 (gdom/getElement "app"))
