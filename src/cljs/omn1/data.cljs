(ns omn1.data
  #?(:cljs
     (:require
      [om.next :as om]
      [omn1.utils :refer [log]]
      [cljs.core.async :refer [timeout <! >!]]
      [websocket-client.core :refer [async-websocket]]
      [taoensso.timbre :refer-macros [info]])
     :clj
     (:require
      [clojure.core.async :refer [go <! >! timeout]]
      [om.next :as om]
      [omn1.utils :refer [log]]
      ))

  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]])))

(declare sq)

;; (def app-state
;;   (atom
;;    {:user/email "fenton.travers@gmail.com"
;;     :user/age 55
;;     :user/cars
;;     [{:id 1 :car/make "Subaru" :car/model "Forester" :year 2001}]}))

(def app-state
  (atom {:curr-user {"fenton.travers@gmail.com" {:age 21 :height 183}}
         :user/cars [{:id 1 :car/make "Subaru" :car/model "Forester" :year 2001}]
         :app-owner [:curr-user "fenton.travers@gmail.com"]}))

#?(:cljs (swap! app-state assoc :async-websocket (async-websocket "ws://localhost:7890")))

(defmulti reader om/dispatch)

(defmethod reader :default
  [{st :state ast :ast :as env} key _]
  {:value (key (sq [key] @st))
   :remote ast})

(def parser (om/parser {:read reader}))

(def req1 [:user/email :user/age {:user/cars [:id :car/make :car/model :year]}])

;; not sure why response is in works-resp format verusus actu-resp format!!!

(def works-resp {:current/userr {:user/email "fenton.travers@gmail.com"
                                :user/age 43}
                 :user/cars [{:id 1 :car/make "Toyota" :car/model "Tacoma" :year "2013"}
                             {:id 2 :car/make "BMWWW" :car/model "325xi" :year "2001"}]})

(def actu-resp {:user/email "fenton.travers@gmail.com", :user/age 43,
                :user/cars
                [{:car/make "Toyota", :car/model "Tacomaaaa", :year 2013}
                 {:car/make "BMW", :car/model "325xi", :year 2001}]})

(defn get-my-cars-remote [qry cb]
  (log "remote query: " qry)
  (let [aws (:async-websocket @app-state)
        req {:email (:user/email @app-state)
             :query (:remote qry)}]
    (log "remote request: " req)
    (log "aws: " aws)
    (go (>! aws req)
        (let [resp (<! aws)]
          (log "response: " resp)
          ;; hmmmmm..... !!!!!
          (cb works-resp)))))

(def reconciler
  (om/reconciler
   {:state app-state
    :parser parser
    :send get-my-cars-remote}))

(defn p [query]
  (parser {:state app-state} query))

(defn sq [query-dat st]
  (log "q: " query-dat)
  (om/db->tree query-dat st st))

;; ----------------- testing functions ----------------

(defn qry1 []
  (go))

(defn rn []
  [(parser {:state app-state} [:current/user] :remote)
   (parser {:state app-state} [:my-cars] :remote)])

;; (defn wst []
;;   (let [url "ws://192.168.0.98:7890" aws (async-websocket url)]
;;     (go (>! aws "{ \"type\": \"list_connected_devices\" }"))
;;     (go (js/console.log (<! aws)))))

(defn wst []
  (let [aws (async-websocket "ws://localhost:7890")]
    (go (>! aws "bala"))
    (go (.log js/console (str (<! aws))))))
