(ns omn1.webpage-test
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [om.next :as om]
   [omn1.webpage :as wp]))

(enable-console-print!)

(deftest test-numbers
  (is
   (=
    true
    (om/component? wp/MyCars))))
