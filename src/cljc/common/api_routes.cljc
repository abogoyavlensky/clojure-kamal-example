(ns common.api-routes
  (:require #?(:clj [api.movies.handlers :as movies-handlers])))
  ;(:require #?(:cljs [reitit.core :as reitit])
  ;          #?(:cljs [reitit.coercion.spec :as reitit-spec])
  ;          #?(:cljs [reitit.frontend :as reitit-front])))


(defn api-routes
  "API routes with handlers and specs."
  [_]
  ["/api"
   ["/v1"
    ["/movies" {:name ::movies
                :get {:responses {200 {:body [{:id pos-int?
                                               :title string?
                                               :year pos-int?
                                               :director string?}]}}
                      #?@(:clj [:handler movies-handlers/get-movies-list])}}]]])

; TODO: move to cljs dir
;#?(:cljs
;   (def api-router
;     "Fake router of backend api for using in frontend."
;     (reitit-front/router
;       [(api-routes {})]
;       {:data {:coercion reitit-spec/coercion}})))
;
;
;#?(:cljs
;   (defn api-route-path
;     "Return api route path by its name for using in frontend."
;     ([route-name]
;      (api-route-path route-name {}))
;     ([route-name {:keys [path query]}]
;      (-> api-router
;        (reitit/match-by-name route-name path)
;        (reitit/match->path query)))))
