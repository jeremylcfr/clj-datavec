(ns clj-datavec.utils
  (:require [clj-nd4j.datavec.files :as dio]
            [clojure.java.io :as io])
  (:import [org.datavec.api.split FileSplit NumberedFileInputSplit]))

(defn ->file-split
  ^FileSplit
  [use-nd4j? io-coercible]
  (FileSplit. ^File (if use-nd4j? (dio/->file-from-jar io-coercible) (io/file io-coercible))))
  
 
(defn ->numbered-file-input-split
  ^NumberedFileInputSplit
  ([path-template max-idx]
   (->numbered-file-input-split path-template 0 max-idx))
  ([path-template min-idx max-idx]
   (NumberedFileInputSplit. ^String (str path-template) ^int (int min-idx) ^int (int max-idx))))  


