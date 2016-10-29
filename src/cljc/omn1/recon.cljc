(ns omn1.recon
  (:require [om.next :as om]))

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

