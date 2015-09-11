(ns generators.yandex-market.core
  (:require [clojure.java.io :as io]
            [generators.yandex-market.tree :as tree]))

(defn generate
  [vendor-id output-path]
  (let [tree (tree/generate-tree vendor-id)]
    (when-not (.exists (io/file output-path))
      (io/make-parents output-path))
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (.write out-file tree))))