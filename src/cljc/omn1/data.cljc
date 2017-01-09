(ns omn1.data
  (:require
   #?@(:cljs
       [[om.next :as om]
        [omn1.utils :refer [log wait]]
        [cljs.core.async :refer [timeout <!]]]
       :clj
       [[clojure.core.async :refer [go <! timeout]]
        [om.next :as om]
        [omn1.utils :refer [log wait]]]))

  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]])))

(declare sq)

(def app-state
  (atom {:current/user {:user/name "Fenton"}
         :my-cars [{:id 1 :make "Toyota" :model "Tacoma" :year "2013"}]}))

(defmulti reader om/dispatch)
(defmethod reader :default
  [{st :state ast :ast} key _]
  {:value (key (sq [key] @st))
   :remote ast})

(def parser (om/parser {:read reader}))

(defn get-my-cars-remote [qry cb]
  (log "remote query: " qry)
  (go
    (log "before")
    (<! (timeout (* 2 1000)))
    (log "after")
    (cb {:current/user {:user/name "Fenton"}
         :my-cars [{:id 1 :make "Toyota" :model "Tacoma" :year "2013"}
                   {:id 2 :make "BMW" :model "325xi" :year "2001"}]})))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send get-my-cars-remote }))

(defn p [query]
  (parser {:state app-state} query))

(defn sq [query-dat st]
  (log "q: " query-dat)
  (om/db->tree query-dat st st))

(defn rn []
  [(parser {:state app-state} [:current/user])
   (parser {:state app-state} [:my-cars])])

;; (defmethod read :search/results
;;   [{:keys [state ast] :as env} k {:keys [query]}]
;;   (merge
;;    {:value (get @state k [])}
;;    (when-not (or (string/blank? query)
;;                  (< (count query) 3))
;;      {:search ast})))
