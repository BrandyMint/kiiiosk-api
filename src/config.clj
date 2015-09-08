(ns config
  "Globally used configuration data"
  (:require [clojurewerkz.elastisch.rest :refer [connect]]
            [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as sql]
            [clojure.string :as s]
            [environ.core :refer [env]]))

;; Database settings
(def db-dev {:dbtype "postgresql"
             :dbname "kiiiosk_developments"
             :user nil ;; Используется системное имя
             :password nil})

(def db-dev {:dbtype "postgresql"
             :dbname "kiiiosk_developments"
             :user nil ;; Используется системное имя
             :password nil})

(defn db-config [cfg]
  (->> cfg seq flatten (apply pg/spec)))

;; JDBC
(def db (apply pg/spec db-dev))
(def query (partial sql/query db))

;; Elasticsearch
(def esr-index-name "kiiosk_dev_goods")
(def esr-conn (connect "http://127.0.0.1:9200"))

(def platform "Kiiiosk")
(def agency "Kiiiosk.ru")
(def email "support@kiiiosk.ru")
(def version "1.0")

;; RabbitMQ
(def ^{:const true} default-exchange-name "")
(def ^{:const true} ymarket-qname "yandex_market_queue")
(def ^{:const true} ymarket-yml-output-pattern "./yml_catalogs/:vendor-id/yandex_market.yml")

;; Helpers
(defn ymarket-yml-output-path
  [vendor-id]
  (s/replace ymarket-yml-output-pattern #":vendor-id" (str vendor-id)))