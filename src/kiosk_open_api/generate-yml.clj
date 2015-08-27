(ns generate-yml.core
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as sql]
            [clj-postgresql.core :as pg]))

; TODO: Transfer constants into the config

(def db (pg/spec))
(def platform "Kiiiosk")
(def agency "Kiiiosk.ru")
(def email "support@kiiiosk.ru")
(def version "1.0")

(declare generate-tags generate-shop-tags)

(defn generate-yml
  [vendor-id output-path]
  (let [tags (generate-tags vendor-id)]
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (xml/emit tags out-file :encoding "windows-1251"))))

(defn generate-tags
  [vendor-id]
  (xml/element :yml_catalog {:date (java.util.Date.)}
    (generate-shop-tags vendor-id)))

(defn generate-shop-tags
  [vendor-id]
  (let [vendor (first (sql/query db [(str "select * from vendors where id = " vendor-id)]))
        vendor-categories (sql/query db [(str "select * from categories where vendor_id = " vendor-id)])]
    (xml/sexp-as-element
      [:shop {}
        [:name {} (:name vendor)]
        [:company {} (:company_name vendor)]
        [:url {} (:cached_home_url vendor)]
        [:platform {} platform]
        [:version {} version]
        [:agency {} agency]
        [:email {} email]])))

; (xml/element :currencies {} ())
;       (xml/element :categories {} ())
;       (xml/element :delivery-options {} ())
;       (xml/element :offers {} ())

; (generate-yml 355 "/tmp/bar.xml")

; (defn generate-yml-by-vendor-id
;   [output-path]
;   (let [tags (xml/element :yml_catalog {:date (java.util.Date.)}
;                         (xml/element :shop {}
;                                      (xml/element :name {} "BestShop")))]
;   (with-open [out-file (clojure.java.io/writer output-path :encoding "UTF-8")]
;     (xml/emit tags out-file :encoding "windows-1251"))))

; (generate-yaml "/tmp/bar.xml")