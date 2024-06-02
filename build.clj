(ns build
  (:require [clojure.tools.build.api :as b]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.security MessageDigest]
           [java.math BigInteger]
           [java.nio.file Paths]))

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

(defn- md5
  [^String s]
  (->> (.getBytes s)
       (.digest (MessageDigest/getInstance "MD5"))
       (BigInteger. 1)
       (format "%032x")))


(defn- join-path
  [p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))


(defn- file-name-with-hash
  [content-hash file-name-in-html]
  (let [file-name-split (str/split file-name-in-html #"\.")
        new-file-name-split (-> file-name-split
                                (butlast)
                                (concat [content-hash (last file-name-split)]))]
    (str/join "." new-file-name-split)))


(defn- hash-css
  "Rename css file in html with hash of the file."
  [{:keys [output-file-path file-name-in-html html-file-path]
    :or {html-file-path "resources/public/index.html"}}]
  (let [output-file (io/file output-file-path)
        new-file-name (-> (slurp output-file)
                          (md5)
                          (file-name-with-hash file-name-in-html))
        new-file-path (join-path (.getParent output-file) new-file-name)
        html (slurp html-file-path)]
    (.renameTo output-file (io/file new-file-path))
    (->> (str/replace html file-name-in-html new-file-name)
         (spit html-file-path))))

(defn build
  "Build an uberjar."
  [_]
  (clean)
  (hash-css {:output-file-path "resources/public/css/output-prod.css"
             :file-name-in-html "output.css"})
  (uber {:uber-file UBER-FILE
         :class-dir CLASS-DIR
         :main-ns MAIN-NS}))
