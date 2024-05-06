(ns api.movies.queries
  (:require [api.util.db :as db-util]))


(defn get-all-movies
  "Return all movies."
  [db]
  (db-util/exec! db
                 {:select [:m.id :m.title :m.year [:d.name :director]]
                  :from [[:movie :m]]
                  :join [[:director :d] [:= :d.id :m.director-id]]
                  :order-by [[:m.year :desc]]}))
