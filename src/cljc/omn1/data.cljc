(ns omn1.data
  (:require [om.next :as om]))

(declare simple-query)

(def app-state
  (atom {:current/user {:user/name "Fenton"}
         :my-cars [{:id 1 :make "Toyota" :model "Tacoma" :year "2013"}
                   {:id 2 :make "BMW" :model "325xi" :year "2001"}]}))
(defn reader
  [{q :query st :state trgt :target :as env} key _]
  (if-not trgt
    (do
      #?(:cljs (.log js/console (str (apply dissoc env [:ast :parser :state]))))
      {:value (simple-query (or q [key]))})))

(def parser (om/parser {:read reader}))

(def reconciler
  (om/reconciler {:state app-state :parser parser}))

(defn simple-query [query-dat]
  #?(:cljs (.log js/console (str "q: " query-dat)))
  ;; db->tree returns a map with the key being 
  (om/db->tree query-dat @app-state @app-state))

(defn go []
  (parser {:state app-state} [:current/user]))
