(ns config
  (:require
    [clojurewerkz.elastisch.rest :as esr]
    )
  )

; Elasticsearch
(def esr-index-name "kiiosk_dev_goods")
(def esr-conn (esr/connect "http://127.0.0.1:9200"))
