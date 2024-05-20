(ns api.util.build
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.security MessageDigest]
           [java.math BigInteger]
           [java.nio.file Paths]))


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


(defn hash-css
  "Rename css file in html with hash of the file."
  {:shadow.build/stage :flush}
  [build-state
   {:keys [output-file-path file-name-in-html html-file-path]
    :or {html-file-path "resources/public/index.html"}}]
  (let [output-file (io/file output-file-path)
        new-file-name (-> (slurp output-file)
                          (md5)
                          (file-name-with-hash file-name-in-html))
        new-file-path (join-path (.getParent output-file) new-file-name)
        html (slurp html-file-path)]
    (.renameTo output-file (io/file new-file-path))
    (->> (str/replace html file-name-in-html new-file-name)
         (spit html-file-path)))
  build-state)
