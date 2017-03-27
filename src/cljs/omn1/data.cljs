(ns omn1.data
  (:require
   [om.next :as om]
   [omn1.utils :refer [log]]
   [cljs.core.async :refer [timeout <! >!]]
   [websocket-client.core :refer [async-websocket]]
   [taoensso.timbre :refer-macros [info]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn sq [query-dat st]
  (log "q: " query-dat)
  (om/db->tree query-dat st st))

(defmulti reader om/dispatch)

(defmethod reader :default
  [{st :state ast :ast :as env} key _]
  {:value (key (sq [key] @st))
   :remote ast})

(def parser (om/parser {:read reader}))

(def app-state
  (atom {:curr-user {"fenton.travers@gmail.com" {:age 21 :height 183}}
         :user/cars [{1 { :car/make "Subaru" :car/model "Forester" :year 2001}}]
         :app-owner [:curr-user "fenton.travers@gmail.com"]}))

(def resp1 {:curr-user {"fenton.travers@gmail.com" {:age 44 :height 184}}
            :user/cars [{3 {:car/make "Toyota" :car/model "Tacoma" :year 2013}}
                        {2 {:car/make "BMW" :car/model "325xi" :year 2001}}]})

(defn get-my-cars-remote [qry cb]
  (log "remote query: " qry)
  (go (<! (timeout (* 2 1000)))
      (let [query (:remote qry)]
        (cb resp1))))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send get-my-cars-remote}))

;; ----------------- testing functions ----------------

(defn rn []
  [(parser {:state app-state} [:current/user] :remote)
   (parser {:state app-state} [:my-cars] :remote)])

