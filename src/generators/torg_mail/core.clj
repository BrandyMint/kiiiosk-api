(ns generators.torg-mail.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [generators.torg-mail.tree :as tree]))

(defn notify-generation-start
  [vendor-id output-path]
  (-> (format "[start] Generating Torg.Mail (vendorID %d, directory %s)"
              vendor-id
              output-path)
      log/info))

(defn notify-generation-finish
  [vendor-id output-path]
  (-> (format "[finish] Generating Torg.Mail (vendorID %d, directory %s)"
              vendor-id
              output-path)
      log/info))

(defn generate
  [vendor-id output-path]
  (notify-generation-start vendor-id output-path)
  (let [tree (tree/generate-tree vendor-id)]
    (when-not (.exists (io/file output-path))
      (io/make-parents output-path))
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (.write out-file tree)))
  (notify-generation-finish vendor-id output-path))