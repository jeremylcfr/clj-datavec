(ns clj-datavec.records.csv
  (:require [clj-datavec.utils :refer [->file-split]]
            [clojure.java.io :as io])
  (:import [org.datavec.api.records.reader RecordReader]
           [org.datavec.api.records.reader.impl.csv CSVRecordReader CSVSequenceRecordReader]
           [org.datavec.api.split FileSplit]))

(defn- ->csv-record-reader-with-defaults
  ^CSVRecordReader
  [{:keys [sep to-skip quote] :or {sep \, , to-skip 0 , quote \"}}]
  (CSVRecordReader. ^int (int to-skip) ^char (char sep) ^char (char quote)))

(defn ->csv-record-reader
  ^CSVRecordReader
  ([]
   (->csv-record-reader-with-defaults {}))
  ([single-arg]
   (cond (map? single-arg)
           (->csv-record-reader-with-defaults single-arg)
         (number? single-arg)
           (->csv-record-reader-with-defaults {:to-skip single-arg})
         (char? single-arg)
           (->csv-record-reader-with-defaults {:sep single-arg})
         (string? single-arg)
           (->csv-record-reader-with-defaults {:sep (.charAt ^String single-arg 0)})
         :else
           (throw (Exception. (str "Datavec CSV - Unsupported options : " single-arg)))))
  ([sep to-skip]
   (->csv-record-reader-with-defaults {:sep sep , :to-skip to-skip}))
  ([sep to-skip quote]
   (->csv-record-reader-with-defaults {:sep sep , :to-skip to-skip , :quote quote})))

(defn initialize!
  ([^CSVRecordReader rdr io-coercible]
   (initialize! rdr false io-coercible))
  ([^CSVRecordReader rdr use-nd4j? io-coercible]
   (.initialize ^CSVRecordReader rdr ^FileSplit (->file-split use-nd4j? io-coercible))))
   
   
(defn- ->csv-sequence-record-reader-with-defaults
  ^CSVSequenceRecordReader
  [{:keys [sep to-skip] :or {sep \, , to-skip 0 }}]
  (CSVSequenceRecordReader. ^int (int to-skip) ^String (str sep)))
  
(defn ->csv-sequence-record-reader
  ^CSVSequenceRecordReader
  ([]
   (->csv-sequence-record-reader-with-defaults {}))
  ([single-arg]
   (cond (map? single-arg)
           (->csv-sequence-record-reader-with-defaults single-arg)
         (number? single-arg)
           (->csv-sequence-record-reader-with-defaults {:to-skip single-arg})
         (char? single-arg)
           (->csv-sequence-record-reader-with-defaults {:sep (str single-arg)})
         (string? single-arg)
           (->csv-sequence-record-reader-with-defaults {:sep single-arg})
         :else
           (throw (Exception. (str "Datavec CSV - Unsupported options : " single-arg)))))
  ([sep to-skip]
   (->csv-sequence-record-reader-with-defaults {:sep sep , :to-skip (str to-skip)})))



