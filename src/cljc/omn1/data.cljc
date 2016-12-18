(ns omn1.data
  (:require [om.next :as om :refer-macros [defui]]))

(def app-state (atom {:current/user {:user/name "Fenton"}
                      :my-cars [{:make "Toyota"
                                 :model "Tacoma"
                                 :year "2013"}
                                {:make "BMW"
                                 :model "325xi"
                                 :year "2001"}]}))

(defn reader
  [{:keys [query state]} _ _]
  (let [st @state]
    {:value (om/db->tree query st st)}))

(def parser (om/parser {:read reader}))

(def reconciler
  (om/reconciler {:state app-state :parser parser}))

