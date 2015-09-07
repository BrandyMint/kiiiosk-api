(ns generate-yml.core
  "Functions for generation Yandex-compatible YML files"
  (:require [clojure.java.io :as io]
            [generate-yml.tree :as tree]))

; (generate-yml 355 "/tmp/bar.xml")
(defn generate-yml
  [vendor-id output-path]
  (let [yml-tree (tree/generate-tree vendor-id)]
    (io/make-parents output-path)
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (.write out-file yml-tree))))