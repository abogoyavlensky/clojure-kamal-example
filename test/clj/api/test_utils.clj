(ns api.test-utils
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [automigrate.core :as automigrate]
            [api.util.db :as db-util]
            [api.util.system :as system-util]))


(def ^:dynamic *test-system*
  "Testing system."
  nil)


(defn with-system
  "Run the whole system before tests."
  []
  (fn [test-fn]
    (let [test-config (system-util/config :test)]
      (ig/load-namespaces test-config)
      (binding [*test-system* (ig/init test-config)]
        (try
          (test-fn)
          (finally
            (ig/halt! *test-system*)))))))


(defn- drop-all-tables
  "Remove all tables with data from database for public schema."
  [db]
  (let [all-tables (->> {:select [:tablename]
                         :from [:pg_tables]
                         :where [:= :schemaname "public"]}
                        (db-util/exec! db)
                        (mapv #(-> % :tablename keyword)))]
    (when (seq all-tables)
      (db-util/exec! db {:drop-table all-tables}))))


(defn with-db-migrations
  "Apply migrations in a test db."
  []
  (fn [f]
    (let [db (:api.db/db *test-system*)
          jdbc-url (.getJdbcUrl db)]
      (drop-all-tables db)
      (automigrate/migrate {:jdbc-url jdbc-url})
      (f))))


(defn get-server-url
  "Return full url from jetty server object."
  [server]
  (let [port (.getLocalPort (first (.getConnectors server)))]
    (str "http://localhost:" port)))
