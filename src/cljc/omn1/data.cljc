(ns omn1.data
  (:require
   #?@(:cljs
       [[om.next :as om]
        [omn1.utils :refer [log]]
        [cljs.core.async :refer [timeout <!]]]
       :clj
       [[clojure.core.async :refer [go <! timeout]]
        [om.next :as om]
        [omn1.utils :refer [log]]]))

  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]])))

(declare sq)

(def app-state
  (atom {:current/user {:user/name "Tim"}
         :my-cars []}))

(defmulti reader om/dispatch)

(defmethod reader :my-cars
  [{st :state ast :ast :as env} key _]
  (log "blah")
  {:value (key (sq [key] @st))
   :remote ast})

(defmethod reader :default
  [{st :state ast :ast :as env} key _]
  {:value (key (sq [key] @st))
   :remote ast})

(def parser (om/parser {:read reader}))

(defn get-my-cars-remote [qry cb]
  (log "remote query: " qry)
  (go
    (<! (timeout 2000))
    (cb {:current/user {:user/name "Fenton"}
         :my-cars [{:id 1 :make "Toyota" :model "Tacoma" :year "2013"}
                   {:id 2 :make "BMW" :model "325xi" :year "2001"}]})))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send get-my-cars-remote}))

;; ------------ test functions --------------

(defn p [query]
  (parser {:state app-state} query))

(defn sq [query-dat st]
  (log "q: " query-dat)
  (om/db->tree query-dat st st))

(defn rn []
  [(parser {:state app-state} [:current/user] :remote)
   (parser {:state app-state} [:my-cars] :remote)])

