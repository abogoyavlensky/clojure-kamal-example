(ns api.handler
  (:require [integrant.core :as ig]
            [muuntaja.core :as muuntaja-core]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as ring-coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.spec :as ring-spec]
            [ring.middleware.gzip :as gzip]
            [ring.util.response :as response]
            [api.util.handler :as util-handler]
            [api.util.middlewares :as util-middlewares]
            [common.api-routes :as routes]))


(defn router
  "Return application router."
  [context]
  (ring/router
    ; TODO: uncomment api routes!
    [;(routes/api-routes context)
     ["/health" {:name ::health-check
                 :get {:handler (fn [_] (response/response "OK"))}}]]
    {:validate ring-spec/validate
     :exception pretty/exception
     :data {:muuntaja muuntaja-core/instance
            :coercion coercion-spec/coercion
            :middleware [gzip/wrap-gzip
                         ; add handler options to request
                         [util-middlewares/wrap-handler-context context]
                         ; parse any request params
                         params/parameters-middleware
                         ; negotiate request and response
                         muuntaja/format-middleware
                         ; handle exceptions
                         exception/exception-middleware
                         ; coerce request and response to spec
                         ring-coercion/coerce-request-middleware
                         ring-coercion/coerce-response-middleware]}}))


(defn- handler
  "Return main application handler."
  [{:keys [options] :as context}]
  (ring/ring-handler
    (router context)
    (ring/routes
      (util-handler/create-resource-handler-cached {:path "/assets/"
                                                    :cached? (:cache-assets? options)})
      (util-handler/create-index-handler)
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler))))


(defmethod ig/init-key ::handler
  [_ {:keys [options] :as context}]
  (let [create-handler-fn #(handler context)]
    (if (true? (:reloaded? options))
      (util-middlewares/reloading-ring-handler create-handler-fn)
      (create-handler-fn))))
