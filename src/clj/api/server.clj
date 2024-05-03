(ns api.server
  (:require [clojure.tools.logging :as log]
            [clojure.spec.alpha :as s]
            [integrant.core :as ig]
            [ring.adapter.jetty :as ring]))


(s/def ::join? boolean?)
(s/def ::port pos-int?)
(s/def ::handler fn?)


(s/def ::options
  (s/keys
    :req-un [::join?
             ::port]))


(defmethod ig/pre-init-spec ::server
  [_]
  (s/keys
    :req-un [::options
             ::handler]))


(defmethod ig/init-key ::server
  [_ {:keys [handler options]}]
  (log/info "[SERVER] Starting server...")
  ; TODO: rename handler to router!
  (let [server (ring/run-jetty handler options)]
    (log/info (format
                "[SERVER] Server has been started on: http://localhost:%s"
                (:port options)))
    server))


(defmethod ig/halt-key! ::server
  [_ server]
  (log/info "[SERVER] Stopping server...")
  (.stop server)
  (log/info "[SERVER] Server has been stopped."))
