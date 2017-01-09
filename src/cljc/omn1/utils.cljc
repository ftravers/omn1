(ns omn1.utils
  (:require
   #?@(:cljs
       [[cljs.core.async :refer [timeout <!]]]
       :clj
       [[clojure.core.async :refer [timeout <! go]]]))
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]])))

(defn log
  ([msg]
   (->> msg #?(:cljs (.log js/console)
               :clj println)))
  ([title msg]
   (log (str title msg))))


