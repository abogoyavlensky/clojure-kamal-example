(ns api.movies-handlers-test
  (:require [clojure.test :refer :all]
            [hato.client :as client]
            [malli.core :as m]
            [api.util.db :as db-util]
            [api.test-utils :as test-utils]))


(use-fixtures :once
  (test-utils/with-system))


(use-fixtures :each
  (test-utils/with-db-migrations))


(deftest test-get-movies-list-endpoint-ok
  (let [{server :api.server/server} test-utils/*test-system*
        server-url (test-utils/get-server-url server)
        movies-api-url (str server-url "/api/v1/movies")
        response (client/get movies-api-url {:as :json})]
    (is (= 200 (:status response)))
    (is (nil? (m/explain
                [:vector
                 {:min 10
                  :max 10}
                 [:map
                  [:id :int]
                  [:title :string]
                  [:year :int]
                  [:director :string]]]
                (:body response))))))


(deftest test-get-movies-list-endpoint-empty-table-ok
  (let [{db :api.db/db
         server :api.server/server} test-utils/*test-system*
        _ (db-util/exec! db {:truncate :movie})
        server-url (test-utils/get-server-url server)
        movies-api-url (str server-url "/api/v1/movies")
        response (client/get movies-api-url {:as :json})]
    (is (= 200 (:status response)))
    (is (= [] (:body response)))))
