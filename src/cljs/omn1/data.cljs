(ns omn1.data
  (:require
   [om.next :as om]
   [devtools.core :as devtools]
   [cljs.core.async :refer [timeout <! >!]]
   [websocket-client.core :refer [async-websocket]])
  (:require-macros
   [omn1.utils :refer [log]]
   [cljs.core.async.macros :refer [go]]))

(declare sq make-remote-car-query)

(defmulti reader om/dispatch)

(defmethod reader :default
  [{st :state :as env} key _]
  ;; (log "Default Reader Key" key)
  (let [resp {:value (key (sq [key] @st))
              :remote true}]
    ;; (log "Default Resp" resp)
    resp))

(defn sq [query-dat st]
  ;; (log "q: " query-dat)
  (om/db->tree query-dat st st))

(defmethod reader :current/car
  [{st :state :as env} key _]
  ;; (log ":current/car query" env)
  (let [curr-car (key (sq [key] @st))
        resp {:value curr-car
              :remote true}]
    (log  ":current/car Resp" resp)
    resp))

(defn make-remote-car-query
  [qry cb]
  (log "remote query" (:remote qry))
  (let [modl-qry (into [] (remove #{key} qry))
        resp {:current/car {:car/make "BMW" :make/models [{:model "325xi"} {:model "X5"}]}}]
    (log "REM QRY Resp" resp)
    (cb resp)))

(def parser (om/parser {:read reader}))

(def app-state
  (atom {:current/car nil}))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send make-remote-car-query}))
