(ns clj-datavec.records
  (:require [clj-datavec.split :as split])
  (:import [org.datavec.api.records.reader RecordReader]
           [org.datavec.api.split InputSplit FileSplit]))

(defn initialize!
  ([^RecordReader rdr io-bundle]
   (let [split-bundle (get io-bundle :split io-bundle)
         io-coercible (get io-bundle :path (get io-bundle :path-template))]
     (initialize! rdr split-bundle io-coercible)))
  ([^RecordReader rdr options io-coercible]
   (.initialize ^RecordReader rdr ^InputSplit (split/->input-split options io-coercible))))

(defn initialize
  ([rdr io-bundle]
   (initialize! rdr io-bundle)
   rdr)
  ([rdr options io-coercible]
   (initialize! rdr options io-coercible)
   rdr))



