(ns api.util.middlewares)


(defn wrap-handler-context
  "Add system dependencies of handler to request as a context key."
  [handler context]
  (fn [request]
    (-> request
        (assoc :context context)
        (handler))))


(defn reloading-ring-handler
  "Reload ring handler on each request in dev mode."
  [f]
  ; Require reloader locally to exclude dev dependency from prod build
  (let [reload! ((requiring-resolve 'ring.middleware.reload/reloader) ["src"] true)]
    (fn
      ([request]
       (reload!)
       ((f) request))
      ([request respond raise]
       (reload!)
       ((f) request respond raise)))))
