(ns build
  (:require [clojure.tools.build.api :as b]))

(def ^:private TARGET-DIR "target")
(def ^:private CLASS-DIR (format "%s/classes" TARGET-DIR))
(def ^:private UBER-FILE (format "%s/standalone.jar" TARGET-DIR))
(def ^:private MAIN-NS 'api.main)

; Delay to defer side effects (artifact downloads)
(def ^:private basis
  (delay (b/create-basis {:project "deps.edn"})))

(defn- clean []
  (b/delete {:path TARGET-DIR}))

(defn- uber
  [{:keys [class-dir uber-file main-ns]}]
  (b/copy-dir {:src-dirs ["src/clj" "src/cljc" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis @basis
                  :ns-compile [main-ns]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis @basis
           :main main-ns}))

(defn build
  "Build an uberjar."
  [_]
  (clean)
  (uber {:uber-file UBER-FILE
         :class-dir CLASS-DIR
         :main-ns MAIN-NS}))
