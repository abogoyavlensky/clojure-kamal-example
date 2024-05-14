(ns api.util.build
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.security MessageDigest]
           [java.math BigInteger]
           [java.nio.file Paths]))


(defn- md5
  [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))


(defn- join-path
  "Join multiple pieces into single file path.
  Origin implementation: https://clojureverse.org/t/how-to-join-file-paths/814"
  [p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))


(defn- get-file-path-with-hash
  [output-file-in-html content-hash]
  (let [output-file (io/file output-file-in-html)
        file-name (.getName output-file)
        file-name-vec (str/split file-name #"\.")
        file-name-hash-vec (concat (drop-last file-name-vec)
                                   [content-hash (last file-name-vec)])]
    (str/join "." file-name-hash-vec)))


(defn- rename-output-file!
  [output-file-path output-file-name-hash]
  (let [output-file (io/file output-file-path)
        parent-path (.getParent output-file)
        output-file-path-hash (join-path parent-path output-file-name-hash)]
    (.renameTo (io/file output-file-path) (io/file output-file-path-hash))))


(defn- replace-file-name-in-html!
  [html-file-path file-name-in-html output-file-name-hash]
  (let [html (-> html-file-path
                 (slurp)
                 (str/replace file-name-in-html output-file-name-hash))]
    (spit html-file-path html)))


(defn- rename-file!
  [{:keys [html-file-path output-file-path file-name-in-html]}]
  (let [output-file-name-hash (->> output-file-path
                                   (slurp)
                                   (md5)
                                   (get-file-path-with-hash file-name-in-html))]
    ; Update file name and html
    (rename-output-file! output-file-path output-file-name-hash)
    (replace-file-name-in-html!
      html-file-path
      file-name-in-html
      output-file-name-hash)))


(defn hash-css
  "Rename css file in html with hash of the file."
  {:shadow.build/stage :flush}
  [build-state
   {:keys [output-file-path
           file-name-in-html
           html-file-path]
    :or {html-file-path "resources/public/index.html"}}]
  ; Rename output file with hash
  (rename-file! {:html-file-path html-file-path
                 :output-file-path output-file-path
                 :file-name-in-html file-name-in-html})
  build-state)
