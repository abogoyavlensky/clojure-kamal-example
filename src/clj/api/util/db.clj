(ns api.util.db
  (:require [honey.sql :as honey]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as jdbc-rs]
            ; Import for converting timestamp fields
            [next.jdbc.date-time]))


(defn exec!
  "Send query to db and return vector of result items."
  [db query]
  (as-> query q
        (honey/format q)
        (jdbc/execute! db q {:builder-fn jdbc-rs/as-unqualified-kebab-maps
                             :return-keys [:id]})))
