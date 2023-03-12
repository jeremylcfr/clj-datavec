(ns clj-datavec.split
  (:require [clj-nd4j.datavec.files :as dio]
            [clojure.java.io :as io])
  (:import [org.datavec.api.split InputSplit BaseInputSplit FileSplit NumberedFileInputSplit]))

(defn ->file-split
  ^FileSplit
  ([options-or-use-nd4j? io-coercible]
   (let [use-nd4j? (if (map? options-or-use-nd4j?)
                     (get options-or-use-nd4j? :use-nd4j? false)
                     options-or-use-nd4j?)]
     (FileSplit. ^File (if use-nd4j? (dio/->file-from-jar io-coercible) (io/file io-coercible))))))
  
 
(defn ->numbered-file-input-split
  ^NumberedFileInputSplit
  ([{:keys [min-idx max-idx] :or {min-idx 0}} path-template]
   (->numbered-file-input-split path-template min-idx max-idx))
  ([path-template min-idx max-idx]
   (NumberedFileInputSplit. ^String (str path-template) ^int (int min-idx) ^int (int max-idx))))


(def input-split-builders
  {:file ->file-split
   :numbered-file ->numbered-file-input-split})

;; coalesce-keys
(defn ->input-split
  ^InputSplit
  ([options io-coercible]
   (let [split-type (get options :split-type (:type options))]
     (->input-split split-type options io-coercible)))
  ([split-type options io-coercible]
   (let [builder (get input-split-builders split-type)]
     (builder options io-coercible))))
   


