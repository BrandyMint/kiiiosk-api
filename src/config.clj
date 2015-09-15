(ns config
  "Globally used configuration data"
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as s]
            [clojurewerkz.elastisch.rest :refer [connect]]
            [clj-postgresql.core :as pg]
            [langohr.core :as lc]
            [environ.core :refer [env]]))

;; JDBC
(def db (pg/spec :host (env :pghost)
                 :port (env :pgport)
                 :user (env :pguser)
                 :password (env :pgpassword)
                 :dbname (env :pgdatabase)))
(def query (partial sql/query db))

;; Elasticsearch
(def esr-index-name "kiiosk_dev_goods")
(def esr-conn (connect "http://127.0.0.1:9200"))

(def platform "Kiiiosk")
(def agency "Kiiiosk.ru")
(def email "support@kiiiosk.ru")
(def version "1.0")

;; RabbitMQ
(def rb-params {:uri (env :rabbitmq-uri)})
(def rb-connect (partial lc/connect rb-params))

(def ^{:const true} default-exchange-name "")
(def ^{:const true} ymarket-qname "yandex_market_queue")
(def ^{:const true} tmail-qname "torg_mail_queue")
(def ^{:const true} vendor-assets-path (env :vendor-assets-path))
(def ^{:const true} ymarket-filename (env :ymarket-filename))
(def ^{:const true} tmail-filename (env :tmail-filename))

;; Helpers
(defn ymarket-output-path
  [vendor-id]
  (str (s/replace vendor-assets-path #":vendor-id" (str vendor-id))
       "/"
       ymarket-filename))

(defn tmail-output-path
  [vendor-id]
  (str (s/replace vendor-assets-path #":vendor-id" (str vendor-id))
       "/"
       tmail-filename))