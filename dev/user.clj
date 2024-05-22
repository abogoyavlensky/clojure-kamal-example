(ns user
  (:require [clojure.tools.namespace.repl :as repl]
            [clojure.test :as test]
            [integrant.repl :as ig-repl]
            [automigrate.core :as automigrate]
            [api.util.system :as system-util]))


(repl/set-refresh-dirs "dev" "src" "test")


(defn- dev-config
  [& _]
  (system-util/config :dev))


(defn- integrant-prep!
  []
  (ig-repl/set-prep! dev-config))


(defn reset
  "Restart system."
  []
  (integrant-prep!)
  (ig-repl/reset))


(defn stop
  "Stop system."
  []
  (ig-repl/halt))


(defn run-all-tests
  "Run all tests for the project."
  []
  (reset)
  (test/run-all-tests #"api.*-test"))

(comment
  ; Manage migrations from REPL
  (automigrate/list {})
  (automigrate/make {})
  (automigrate/migrate {})
  (automigrate/migrate {:number 0})
  (automigrate/explain {:number 1 :direction :backward}))
