(ns omn1.core2-test
  (:require [omn1.core2 :as c2]
            [om.next :as om]))


(c2/parser {:state c2/app-state} [:curr-user])
(om/db->tree (or query [k]) (get st k) st)
