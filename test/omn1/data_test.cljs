(ns omn1.data-test
  (:require
   [omn1.data :as dat]
   [om.next :as om]
   [cljs.test :refer-macros [deftest is testing run-tests]]))

(enable-console-print!)

(deftest test-numbers
  (let [data {:current/user {:user/name "Fenton"}
              :my-cars [{:id 1 :make "Toyota" :model "Tacoma" :year "2013"}
                        {:id 2 :make "BMW" :model "325xi" :year "2001"}]}
        query [:current/user]]
    (is (=
         {:current/user {:user/name "Fenton"}}
         (om/db->tree [:current/user] data data)))))



