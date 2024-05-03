(ns ui.util.api-routes
  (:require [reitit.core :as reitit]
            [reitit.coercion.spec :as reitit-spec]
            [reitit.frontend :as reitit-front]
            [common.api-routes :as api-routes]))


(def api-router
  "Fake router of backend api for using in frontend."
  (reitit-front/router
    [(api-routes/api-routes {})]
    {:data {:coercion reitit-spec/coercion}}))


(defn api-route-path
  "Return api route path by its name for using in frontend."
  ([route-name]
   (api-route-path route-name {}))
  ([route-name {:keys [path query]}]
   (-> api-router
       (reitit/match-by-name route-name path)
       (reitit/match->path query))))
