(ns api.movies.handlers
  (:require [ring.util.response :as ring]
            [api.movies.queries :as queries]))


(defn get-movies-list
  "Return all movies."
  [{:keys [context]}]
  (-> (:db context)
      (queries/get-all-movies)
      (ring/response)))
