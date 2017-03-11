(ns omn1.utils
  (:require
   [cljs.core.async :refer [timeout <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn log
  ([msg]
   (->> (str msg) (.log js/console)))
  ([title msg]
   (log (str title msg))))


