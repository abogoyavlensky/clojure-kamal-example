(ns api.db
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [hikari-cp.core :as cp]
            [api.util.system :as system-util]))


(defmethod ig/assert-key ::db
  [_ params]
  (system-util/validate-schema!
    {:schema [:map
              [:options [:map
                         [:jdbc-url string?]]]]
     :data params
     :error-message "Invalid db options"}))


(defmethod ig/init-key ::db
  [_ {:keys [options]}]
  (log/info (str "[DB] Starting database connection pool..."))
  (cp/make-datasource options))


(defmethod ig/halt-key! ::db
  [_ datasource]
  (log/info (str "[DB] Closing database connection pool..."))
  (cp/close-datasource datasource))
