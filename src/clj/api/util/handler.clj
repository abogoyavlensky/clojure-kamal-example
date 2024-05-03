(ns api.util.handler
  (:require [ring.util.response :as response]
            [reitit.core :as reitit]
            [reitit.ring :as ring]
            [ring.middleware.gzip :as gzip]))


(defn create-index-handler
  "Create handler for render index.html on any request."
  ([]
   (create-index-handler {}))
  ([{:keys [index-file root]
     :or {index-file "index.html"
          root "public"}}]
   (letfn [(index-handler-fn
             [_request]
             (-> index-file
                 (response/resource-response {:root root})
                 (response/content-type "text/html")))]
     (fn
       ([request]
        (index-handler-fn request))
       ([request respond _]
        (respond (index-handler-fn request)))))))


(def ^:private cache-30d "public,max-age=2592000,immutable")


(defn- cache-control
  [resp]
  (response/header resp "Cache-Control" cache-30d))


(defn- resource-response-cached
  ([path]
   (resource-response-cached path {}))
  ([path options]
   (-> (response/resource-response path options)
       (cache-control))))


(defn create-resource-handler-cached
  "Return resource handler with optional Cache-Control header."
  [{:keys [cached?] :as opts}]
  (let [response-fn (if cached?
                      resource-response-cached
                      response/resource-response)]
    (-> response-fn
        (ring/-create-file-or-resource-handler opts)
        (gzip/wrap-gzip))))


(defn get-route
  "Return api route by its name, path and query params."
  ([router route-name]
   (get-route router route-name {}))
  ([router route-name {:keys [path query]}]
   (-> router
       (reitit/match-by-name route-name path)
       (reitit/match->path query))))
