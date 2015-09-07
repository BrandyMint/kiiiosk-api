(ns config
  "Globally used configuration data"
  (:require [clojurewerkz.elastisch.rest :refer [connect]]
            [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as sql]))

; JDBC
(def db (pg/spec))
(def query (partial sql/query db))

; Elasticsearch
(def esr-index-name "kiiosk_dev_goods")
(def esr-conn (connect "http://127.0.0.1:9200"))

(def platform "Kiiiosk")
(def agency "Kiiiosk.ru")
(def email "support@kiiiosk.ru")
(def version "1.0")

; RabbitMQ
(def ^{:const true} default-exchange-name "")
(def ^{:const true} ymarket-qname "yandex_market_queue")
(def ^{:const true} ymarket-yml-output-path "./:vendor_id/yandex_market.yml")
