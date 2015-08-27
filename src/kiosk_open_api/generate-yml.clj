(ns generate-yml.core
  "Functions for generation Yandex-compatible YML files"
  (:require [clojure.java.io :as io]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [config :refer [query platform version agency email]]))

(def doctype
  {:yandex-market
    "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

(defn generate-currencies-tags
  [currency-iso-code]
  [:currency {:id currency-iso-code :rate 1}])

(defn generate-categories-tags
  [categories]
  (map (fn [{:keys [id name]}] [:category {:id id} name]) categories))

;; TODO: :delivery-options, :offers
(defn generate-shop-tags
  [vendor-id]
  (let [vendor (first (query [(str "select * from vendors where id = " vendor-id)]))
        vendor-categories (query [(str "select * from categories where vendor_id = " vendor-id)])]
    [:shop {}
      [:name {} (:name vendor)]
      [:company {} (:company_name vendor)]
      [:url {} (:cached_home_url vendor)]
      [:platform {} platform]
      [:version {} version]
      [:agency {} agency]
      [:email {} email]
      [:currencies {} (generate-currencies-tags (:currency_iso_code vendor))]
      [:categories {} (generate-categories-tags vendor-categories)]]))

(defn generate-tags
  [vendor-id]
  (let [date (.format (java.text.SimpleDateFormat. "YYYY-MM-dd hh:mm") (java.util.Date.))]
    (str (xml-declaration "windows-1251")
         (doctype :yandex-market)
         (html [:yml_catalog {:date date}
                (generate-shop-tags vendor-id)]))))

(defn generate-yml
  [vendor-id output-path]
  (let [tags (generate-tags vendor-id)]
    (with-open [out-file (io/writer output-path :encoding "UTF-8")]
      (.write out-file tags))))

; (generate-yml 355 "/tmp/bar.xml")