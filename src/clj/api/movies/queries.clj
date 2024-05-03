(ns api.movies.queries
  (:require [api.util.db :as util-db]))


(defn get-all-movies
  "Return all movies."
  [db]
  (util-db/exec! db
    {:select [:m.id :m.title :m.year [:d.name :director]]
     :from [[:movie :m]]
     :join [[:director :d] [:= :d.id :m.director-id]]
     :order-by [[:m.year :desc]]}))
