(ns omn1.data
  (:require [om.next :as om]))

(declare sq)

(def app-state
  (atom {:current/user {:user/name "Fenton"}
         :my-cars [{:id 1 :make "Toyota" :model "Tacoma" :year "2013"}
                   {:id 2 :make "BMW" :model "325xi" :year "2001"}]}))

(defmulti reader om/dispatch)
(defmethod reader :default
  [{st :state} key _]
  {:value (key (sq [key] @st))})

(def parser (om/parser {:read reader}))

(def reconciler
  (om/reconciler {:state app-state :parser parser}))

(defn p [query]
  (parser {:state app-state} query))

(defn sq [query-dat st]
  (->> (str "q: " query-dat)
      #?(:cljs (.log js/console)
         :clj println))
  (om/db->tree query-dat st st))

(defn go []
  [(parser {:state app-state} [:current/user])
   (parser {:state app-state} [:my-cars])])
