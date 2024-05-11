(ns common.api-routes
  (:require #?(:clj [api.movies.handlers :as movies-handlers])))


(defn api-routes
  "API routes with handlers and specs."
  [_]
  ["/api"
   ["/v1"
    ["/movies" {:name ::movies
                :get {:responses {200 {:body [:vector
                                              [:map
                                               [:id pos-int?]
                                               [:title string?]
                                               [:year pos-int?]
                                               [:director string?]]]}}
                      #?@(:clj [:handler movies-handlers/get-movies-list])}}]]])
