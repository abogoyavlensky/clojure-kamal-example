(ns api.handler
  (:require [integrant.core :as ig]
            [muuntaja.core :as muuntaja-core]
            ; TODO: remove!
            ;[reitit.coercion :as coercion]
            [reitit.coercion.malli :as coercion-malli]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as ring-coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring.middleware.exception :as exception]
            [ring.middleware.gzip :as gzip]
            [ring.util.response :as response]
            [api.util.handler :as handler-util]
            [api.util.middlewares :as middlewares-util]
            [api.util.system :as system-util]
            [common.api-routes :as routes]))


(defn- handler
  "Return main application handler."
  [{:keys [options] :as context}]
  (ring/ring-handler
    (ring/router
      [(routes/api-routes context)
       ["/health" {:name ::health-check
                   :get {:handler (fn [_] (response/response "OK"))}}]]
      {:exception pretty/exception
       ; TODO: try to uncomment!
       ;:compile coercion/compile-request-coercers
       :data {:muuntaja muuntaja-core/instance
              :coercion coercion-malli/coercion
              :middleware [gzip/wrap-gzip
                           ; add handler options to request
                           [middlewares-util/wrap-handler-context context]
                           ; parse any request params
                           params/parameters-middleware
                           ; negotiate request and response
                           muuntaja/format-middleware
                           ; handle exceptions
                           exception/exception-middleware
                           ; coerce request and response to spec
                           ring-coercion/coerce-request-middleware
                           ring-coercion/coerce-response-middleware]}})
    (ring/routes
      ; TODO: try to use just default handler for index.html and assets
      (handler-util/create-resource-handler-cached {:path "/assets/"
                                                    :cached? (:cache-assets? options)})
      (handler-util/create-index-handler)
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler))))


(defmethod ig/assert-key ::handler
  [_ params]
  (system-util/validate-schema!
    {:schema [:map
              [:db :some]
              [:options [:map
                         [:reloaded? boolean?]
                         [:cache-assets? boolean?]]]]
     :data params
     :error-message "Invalid handler params"}))


(defmethod ig/init-key ::handler
  [_ {:keys [options] :as context}]
  (let [create-handler-fn #(handler context)]
    (if (true? (:reloaded? options))
      (middlewares-util/reloading-ring-handler create-handler-fn)
      (create-handler-fn))))
