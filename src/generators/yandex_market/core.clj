(ns generators.yandex-market.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [generators.yandex-market.markup :as markup]))

(defn notify-generation-start
  [vendor-id output-path]
  (-> (format "[start] Generating Yandex.Market (vendorID %d, directory %s)"
              vendor-id
              output-path)
      log/info))

(defn notify-generation-finish
  [vendor-id output-path]
  (-> (format "[finish] Generating Yandex.Market (vendorID %d, directory %s)"
              vendor-id
              output-path)
      log/info))

(defn generate
  [vendor-id output-path]
  (notify-generation-start vendor-id output-path)
  (let [markup (markup/generate-markup vendor-id)]
    (when-not (.exists (io/file output-path))
      (io/make-parents output-path))
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (.write out-file markup)))
  (notify-generation-finish vendor-id output-path))