(ns api.movies-handlers-test
  (:require [clojure.test :refer :all]
            [api.movies.queries :as queries]
            [api.test-utils :as test-utils]))


(use-fixtures :once
  (test-utils/with-system))


(use-fixtures :each
  (test-utils/with-db-migrations))


(deftest a-test
  (let [db (:api.db/db test-utils/*test-system*)]
    #p (queries/get-all-movies db)
    (is (= 1 1))))
