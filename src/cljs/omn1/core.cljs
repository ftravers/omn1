(ns omn1.core
  (:require
   [om.next :as om :refer-macros [defui]]
   [om.dom :as dom]
   [goog.dom :as gdom]))

(def my-state
  (atom {:curr-user {:email "f.t@g.com"
                     :age 21}
         :grades [{:id 1 :name "math" :grade 55}
                  {:id 2 :name "chemistry" :grade 65}]}))

(defmulti readr om/dispatch)

(defmethod readr :default
  [{:keys [query state]} k _]
  (let [st @state]
    {:value (om/db->tree query (get st k) st)}))

(def parser (om/parser {:read readr}))

(def reconciler (om/reconciler {:state my-state :parser parser}))

(defui Grade
  static om/IQuery
  (query [this] [:id :name :grade])
  Object
  (render
   [this]
   (let [{:keys [id name grade]} (om/props this)]
     (dom/li
      nil
      (dom/div nil id)
      (dom/div nil name)
      (dom/div nil grade)))))

(def grade (om/factory Grade))

(defui Grades
  static om/IQuery
  (query [this] [{:curr-user [:email :age]}
                 {:grades (om/get-query Grade)}])
  Object
  (render
   [this]
   (let [{:keys [curr-user]} (om/props this)
         email (:email curr-user)
         age (:age curr-user)]
     (dom/div
      nil
      (dom/div nil (str "Hello: " email ", you are: " age " years old."))
      (dom/ul nil (map grade (-> this om/props :grades)))))))

(om/add-root! reconciler Grades (gdom/getElement "app"))
