(ns omn1.recon
  (:require [om.next :as om :refer-macros [defui]]))

;; (defonce my-state
;;   (atom {:current-user {:email "bob.smith@gmail.com"}
;;          :items [{:id 0 :title "Foo"}
;;                  {:id 1 :title "Bar"}
;;                  {:id 2 :title "Baz"}]}))

(def my-state (atom {:title "Hello World!"}))

(defmulti readr om/dispatch)

(defmethod readr :default
  [{:keys [query state]} k _]
  (let [st @state]
    ;; (.log js/console (str st))
    {:value (om/db->tree query (get st k) st)}))

;; (defmethod readr :default
;;   [env keyz parms]
;;   (let [state (:state env)
;;         val (keyz @state)]
;;     {:value val}))

(def parser (om/parser {:read readr}))

(def reconciler
  (om/reconciler
   {:state my-state
    :parser parser}))

;; (defui Blah
;;   static om/IQuery
;;   (query [this] [:me-mail])
;;   Object (render [this]))
