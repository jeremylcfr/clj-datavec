(defproject io.github.jeremylcfr/clj-datavec "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.github.jeremylcfr/clj-nd4j "0.1.0-SNAPSHOT"]
                 [org.deeplearning4j/deeplearning4j-datavec-iterators "1.0.0-M2.1"]] 
  :profiles {:dev {:source-paths ["repl"]}})
