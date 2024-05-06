(ns api.server
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [ring.adapter.jetty :as ring]
            [api.util.system :as system-util]))


(defmethod ig/assert-key ::server
  [_ params]
  (system-util/validate-schema!
    {:schema [:map
              [:handler fn?]
              [:options [:map
                         [:port pos-int?]]]]
     :data params
     :error-message "Invalid server params"}))


(defmethod ig/init-key ::server
  [_ {:keys [handler options]}]
  (log/info (str "[SERVER] Starting server on http://localhost:" (:port options)))
  (ring/run-jetty handler (assoc options :join? false)))


(defmethod ig/halt-key! ::server
  [_ server]
  (log/info "[SERVER] Stopping server...")
  (.stop server))
