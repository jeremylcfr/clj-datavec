(ns clj-datavec.utils
  (:require [clj-nd4j.datavec.files :as dio]
            [clojure.java.io :as io])
  (:import [org.datavec.api.split FileSplit]))

(defn ->file-split
  ^FileSplit
  [use-nd4j? io-coercible]
  (FileSplit. ^File (if use-nd4j? (dio/->file-from-jar io-coercible) (io/file io-coercible))))



