(ns clj-datavec.records.jackson
  (:require [clj-datavec.utils :refer [->file-split]]
            [clojure.java.io :as io])
  (:import [org.datavec.api.records.reader RecordReader]
           [org.datavec.api.writable Writable Text LongWritable DoubleWritable BooleanWritable]
           [org.datavec.api.records.reader.impl.jackson FieldSelection FieldSelection$Builder JacksonRecordReader JacksonLineRecordReader]
           [org.datavec.api.split FileSplit]
           [org.nd4j.shade.jackson.databind ObjectMapper]))

;; Jackson => JSON, YML and YAML

(def sample
  [:a [:b [:d :e] :f]])

(def sample-bis
  {:a {:b {:d 2.0 , :e 1.0} :f 4.0}})

(def sample-ter
  [[:a :b :d] [:a :b :e] [:a :f]])


(defn string-array
  [obj]
  (into-array String obj))

;; V2

(defn ->writable
  ^Writable
  [input]
  (cond (string? input)
          (Text. input)
        (integer? input)
          (LongWritable. (long input))
        (number? input)
          (DoubleWritable. (double input))
        (boolean? input)
          (BooleanWritable. input)
        :else
          (throw (Exception. (str "Input : " input " is not automatically coercible to a Writable for now. You can use however existing builders if you want")))))

;; The issue there is that map schema is unordered
;; therefore labels indexes are not accessible
;; Find a way to return an order
(defn- field-selection-from-map-schema
  [^FieldSelection$Builder builder base order schema]
  (loop [queue schema
         fields order]
    (if (empty? queue)
      {:builder builder :positions fields}
      (let [[k v] (first queue)]
        (if (map? v)
          (let [str-k (name k)
                new-base (if base (conj base str-k) [str-k])
                {:keys [positions]} (field-selection-from-map-schema builder new-base fields v)]
            (recur (rest queue) positions))
          (let [str-k (name k)
                writable (when (not (nil? v))
                           (->writable v))
                current-base (if base (conj base str-k) [str-k])
                _ (if writable
                    (.addField ^FieldSelection$Builder builder ^Writable writable (string-array current-base))
                    (.addField ^FieldSelection$Builder builder (string-array current-base)))]
            (recur (rest queue) (conj fields k))))))))


(defn- field-selection-from-regular-schema
  [^FieldSelection$Builder builder schema]
  (loop [queue schema
         fields []]
    (if (empty? queue)
      {:builder builder :positions fields}
      (let [next-entry (first queue)
            keyword-entry? (keyword? next-entry)
            next-field (if keyword-entry?
                         next-entry
                         (last next-entry))
            next-path (if keyword-entry?
                        [(name next-entry)]
                        (mapv name next-entry))]
        (.addField ^FieldSelection$Builder builder (string-array next-path))
        (recur (rest queue) (conj fields next-field))))))

(defn field-selection
  "Builds a FieldSelection object from
   a Clojure schema. Three formats are allowed
   and must be hinted :
   - map representation : {:a {:b {:d false , :e 1.0} :f 'koala'}}, there keys are default values
                          (only numbers, text and boolean are supported for now, nil meaning
                          no default value)
   - regular representation : DL4J input format [[:a :b :d] [:a :b :e] [:a :f]]
   The statements above might evolve in more mature iterations"
  ^FieldSelection
  ([type-hint schema]
   (field-selection {:type-hint type-hint , :schema schema}))
  ([{:keys [type-hint schema]}]
   (let [init-builder (FieldSelection$Builder.)
         {:keys [builder positions]} (case type-hint
                                           :regular (field-selection-from-regular-schema init-builder schema)
                                           :map (field-selection-from-map-schema init-builder nil [] schema))]
     {:selection (.build ^FieldSelection$Builder builder)
      :positions (reduce-kv
                   (fn [agg idx key-fn]
                     (assoc agg key-fn idx))
                   {} positions)})))

(defn field-selection?
  [obj]
  (instance? FieldSelection obj))

(defn ->field-selection
  "Same a field-selection but
   also behave as identity when
   input is already a FieldSelection object"
  [{:keys [selection positions] :as spec}]
  (if (field-selection? selection)
    spec
    (field-selection spec)))

(defn jackson-line-record-reader
  ([{:keys [type-hint schema object-mapper] :as options}]
   (let [{:keys [selection positions]} (->field-selection options)
         object-mapper (if object-mapper
                         object-mapper
                         (ObjectMapper.))]
     {:record-reader (JacksonLineRecordReader. ^FieldSelection selection ^ObjectMapper object-mapper)
      :positions positions})))

(defn initialize!
  [^JacksonLineRecordReader reader use-nd4j? io-coercible]
  (.initialize reader (->file-split use-nd4j? io-coercible))
  reader)






