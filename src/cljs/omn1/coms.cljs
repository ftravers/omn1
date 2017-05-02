(ns omn1.coms
  (:require [cljs.core.async :refer [<! >!]]
            [websocket-client.core :refer [async-websocket]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def conn (async-websocket "ws://localhost:7890"))

(defn send [cb data]
  (go (>! conn data)
      (cb (<! conn))))

