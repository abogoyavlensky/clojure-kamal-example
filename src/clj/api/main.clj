(ns api.main
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [automigrate.core :as automigrate]
            [api.util.system :as system-util]))


(defn- run-system
  [profile]
  (let [profile-name-kw profile
        config (system-util/config profile-name-kw)]
    (log/info "[SYSTEM] System is starting with profile:" profile-name-kw)
    (ig/load-namespaces config)
    (-> config
        (ig/init)
        (system-util/at-shutdown))
    (log/info "[SYSTEM] System has been started successfully.")))


(defn -main
  "Run application system in production env."
  [& args]
  (case (first args)
    "migrations" (automigrate/migrate)
    (run-system :prod)))
