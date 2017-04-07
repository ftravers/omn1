(ns omn1.utils
  (:require [clojure.string :as st]))

(defmacro log [msg & args]
  `(.log js/console ~(str "[" (st/upper-case msg) "]:") ~@args))



