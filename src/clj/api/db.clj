(ns api.db
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [hikari-cp.core :as cp]))


; TODO: move to malli!
(s/def ::jdbc-url string?)
(s/def ::minimum-pool-size pos-int?)
(s/def ::maximum-pool-size pos-int?)


(s/def ::options
  (s/keys
    :req-un [::jdbc-url
             ::minimum-pool-size
             ::maximum-pool-size]))


(defmethod ig/pre-init-spec ::db
  [_]
  (s/keys
    :req-un [::options]))


(defmethod ig/init-key ::db
  [_ {:keys [options]}]
  (log/info (str "[DB] Starting database connection pool..."))
  (let [datasource (cp/make-datasource options)]
    (log/info (str "[DB] Database connection pool has been started."))
    datasource))


(defmethod ig/halt-key! ::db
  [_ datasource]
  (log/info (str "[DB] Closing database connection pool..."))
  (cp/close-datasource datasource)
  (log/info (str "[DB] Database connection pool has been closed.")))
