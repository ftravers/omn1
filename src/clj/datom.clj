(ns omn1.datom
  (:require [datomic.api :as d]
            [clojure.core.async :refer [go <! timeout]]
            [clojure.tools.logging :refer [debug]]))

(def db-conn (atom nil))

(def db-url "datomic:free://127.0.0.1:4334/omn-dev")

(def schema
  [{:db/doc "Uniquely identify a user by EMAIL"
    :db/id #db/id[:db.part/db]
    :db/ident :user/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/value
    :db.install/_attribute :db.part/db}
   {:db/doc "a ref to a user, points to an entity uniquely identified by email"
    :db/id #db/id[:db.part/db]
    :db/ident :user/email-ref
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/doc "List of cars a user owns"
    :db/id #db/id[:db.part/db]
    :db/ident :user/cars
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}
   {:db/doc "Car make"
    :db/id #db/id[:db.part/db]
    :db/ident :car/make
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/doc "Car model"
    :db/id #db/id[:db.part/db]
    :db/ident :car/model
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/doc "Year"
    :db/id #db/id[:db.part/db]
    :db/ident :year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/doc "Person age"
    :db/id #db/id[:db.part/db]
    :db/ident :user/age
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

(def test-data
  [{:db/id #db/id[:db.part/user -1]
    :user/email "fenton.travers@gmail.com"
    :user/age 43}
   {:db/id #db/id[:db.part/user -2]
    :car/make "Toyota"
    :car/model "Tacoma"
    :year 2013
    :user/email-ref {:db/id #db/id[:db.part/user -1]}}
   {:db/id #db/id[:db.part/user -3]
    :car/make "BMW"
    :car/model "325xi"
    :year 2001
    :user/email-ref {:db/id #db/id[:db.part/user -1]}}
   {:db/id #db/id[:db.part/user -1]
    :user/cars [{:db/id #db/id[:db.part/user -2]}
                {:db/id #db/id[:db.part/user -3]}]}])

(defn reload-dbs
  ([]
   (reload-dbs db-url))
  ([db-url]
   (debug (str "DB URL: " db-url))
   (debug "Deleting DB.") (d/delete-database db-url)
   (debug "Creating DB.") (d/create-database db-url)
   (reset! db-conn (d/connect db-url))
   (d/transact @db-conn schema)
   (d/transact @db-conn test-data)))

(defn q1 []
  (d/q '[:find (pull ?e [:car/make :car/model :year])
         :where
         [?e :user/email-ref [:user/email "fenton.travers@gmail.com"]]]
       (d/db (d/connect db-url))))

(defn q2 [email query]
  (first (d/q
    '[:find
      [(pull
        ?e

        ;; [:user/age :user/email {:user/cars [:car/make]}]
        [:user/email :user/age #:user{:cars [:id :car/make :car/model :year]}]

        ) ...]
      :where
      [?e :user/email "fenton.travers@gmail.com"]]
    (d/db (d/connect db-url)))))

(defn dq1 []
  (q1))
;; [:user/email :user/age #:user{:cars [:id :car/make :car/model :year]}]

;; [:current/user {:user/cars [:id :car/make :car/model :year]}]
