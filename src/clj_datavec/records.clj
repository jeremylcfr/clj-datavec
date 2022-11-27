(ns clj-datavec.records
  (:import [org.nd4j.linalg.dataset.api.iterator DataSetIterator] ;; see if truly required
           [org.deeplearning4j.datasets.datavec RecordReaderDataSetIterator RecordReaderDataSetIterator$Builder]
           [org.datavec.api.records.reader RecordReader]
           [org.nd4j.linalg.dataset.api DataSetPreProcessor]
           [org.datavec.api.io WritableConverter]))
                                                  
(defn record-reader-dataset-iterator
  ^RecordReaderDataSetIterator
  ([^RecordReader record-reader]
   (record-reader-dataset-iterator nil))
  ([{:keys [batch-size max-num-batches regression classification pre-processor writable-converter collect-metadata?] :or {batch-size 1}} ^RecordReader record-reader]
   (let [^RecordReaderDataSetIterator$Builder builder (RecordReaderDataSetIterator$Builder. ^RecordReader record-reader ^int (int batch-size))]
     (cond-> builder 
             writable-converter (.writableConverter ^WritableConverter writable-converter)
             max-num-batches (.maxNumBatches ^int (int max-num-batches))
             (number? regression) (.regression ^int (int regression))
             (sequential? regression) (.regression ^int (int (first regression)) ^int (int (second regression)))
             (map? regression) (.regression ^int (int (:from regression)) ^int (int (:to regression)))
             (sequential? classification) (.classification ^int (int (first classification)) ^int (int (second classification)))
             (map? classification) (.classification ^int (int (:index classification)) ^int (int (:num-classes classification)))
             pre-processor (.preProcessor ^DataSetPreProcessor pre-processor)
             (boolean? collect-metadata?) (.collectMetaData ^boolean collect-metadata?) 
             true (.build))))
  ([options ^RecordReader record-reader batch-size]
   (record-reader-dataset-iterator (assoc options :batch-size batch-size) record-reader)))

(defn record-reader-dataset-iterator?
  [obj]
  (instance? RecordReaderDataSetIterator obj))

(defn ->record-reader-dataset-iterator
  ^RecordReaderDataSetIterator
  ([obj]
   (if (record-reader-dataset-iterator? obj)
     obj
     (record-reader-dataset-iterator obj)))
  ([options record-reader]
   (record-reader-dataset-iterator options record-reader))
  ([options record-reader batch-size]
   (record-reader-dataset-iterator options record-reader batch-size)))
            

