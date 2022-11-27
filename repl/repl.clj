(ns repl
  (:require [clj-nd4j.ndarray :as nda]
            [clj-datavec.records :as nrec]
            [clj-datavec.records [csv :as ncsv]
                                 [jackson :as njack]]
            [clj-java-commons.core :refer :all]
            [clj-java-commons.coerce :refer [->clj]])
  (:refer-clojure :exclude [/]))