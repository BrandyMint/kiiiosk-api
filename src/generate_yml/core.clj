(ns generate-yml.core
  "Functions for generation Yandex-compatible YML files"
  (:require [clojure.java.io :as io]
            [generate-yml.tags :refer [generate-tags]]))

; (generate-yml 355 "/tmp/bar.xml")
(defn generate-yml
  [vendor-id output-path]
  (let [tags (generate-tags vendor-id)]
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (.write out-file tags))))