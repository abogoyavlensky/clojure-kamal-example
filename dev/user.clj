(ns user
  (:require [clojure.tools.namespace.repl :as repl]
            [integrant.repl :as ig-repl]
            [api.util.system :as system-util]
            [clojure.test :as test]))


(repl/set-refresh-dirs "dev" "src" "test")


(defn- dev-config
  [& _]
  (-> (system-util/config :dev)
    ; Uncomment for running system without components.
    #_(select-keys [])))


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
