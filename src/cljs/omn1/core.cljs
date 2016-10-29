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
        val (keyz @state)]
    {:value val}))

(def parser (om/parser {:read readr}))

(def reconciler
  (om/reconciler
   {:state my-state
    :parser parser}))

(defui HelloWorld
  static om/IQuery
  (query [this] [:title])
  Object
  (render
   [this]
   (let [title (:title (om/props this))]
     (dom/div nil title))))

(om/add-root!
 reconciler
 HelloWorld
 (gdom/getElement "app"))
