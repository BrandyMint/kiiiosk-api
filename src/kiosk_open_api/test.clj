(ns kiosk-open-api.some
  (:require [schema.core :as s]
            [clojure.set]
            [schema.coerce :as coerce]
            [kiosk-open-api.schemas :refer :all]
            [clojurewerkz.elastisch.rest :as esr]
            ))

;(require '[clojure.java.jdbc :as sql])
;(require '[clj-postgresql.core :as pg])

;(def db (pg/spec))

;(sql/query db ["select * from products"])

(def id "32139")
(def conn (esr/connect "http://127.0.0.1:9200"))
(def product
  (esd/get conn "kiiosk_dev_goods" "product" id))

(defn excluded-keys [schema datum]
  (clojure.set/difference 
    (set (keys datum))
    (set (keys schema))
    )
  )

(defn my-coercion-matcher [schema] 
  (fn [datum] 
    (if (instance? clojure.lang.PersistentHashMap schema)
      (apply dissoc datum (excluded-keys schema datum))
      datum)
    )
  )

(def coerced-product ((coerce/coercer ProductCard my-coercion-matcher) (product :_source)))

;(defn my-coercion-matcher [schema] (coerce/keyword-enum-matcher schema))

;(def coerce-product (coerce/coercer ProductCard coerce/json-coercion-matcher))
;(def coerce-product (coerce/coercer ProductCard my-coercion-matcher))
;(coerce-product (product :_source))


(s/validate ProductCard coerced-product)
