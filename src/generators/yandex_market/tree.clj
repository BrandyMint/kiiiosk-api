(ns generators.yandex-market.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [libs.date :as date]
            [libs.money :as money]
            [libs.queries :as queries]
            [generators.libs.params :refer [params-nodes]]
            [generators.libs.currencies :refer [currencies-tree]]
            [generators.libs.categories :refer [categories-tree]]
            [config]))

(def ^:private doctype
  {:yandex-market "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

(defn offer-tree
  [product vendor-id]
  [:offer {:id (:id product) :available true}
   [:url         {} (:url product)]
   [:picture     {} (:picture-url product)]
   [:name        {} (:title product)]
   [:description {} (:description product)]
   [:categoryId  {} (first (:categories-ids product))]
   [:currencyId  {} (:price-currency product)]
   [:price       {} (money/minor-units->major-units (:price-currency product)
                                                    (:price-kopeks product))]
   [:oldprice    {} (money/minor-units->major-units (:oldprice-currency product)
                                                    (:oldprice-kopeks product))]
   (params-nodes (:custom-attributes product) vendor-id)])

(defn offers-tree
  [vendor-id]
  (let [products (queries/get-vendor-products vendor-id)]
    [:offers {}
     (map #(offer-tree % vendor-id) products)]))

(defn delivery-node
  [{:keys [price-currency price-kopeks]}]
    [:option {:cost (money/minor-units->major-units price-currency price-kopeks)}])

(defn deliveries-tree
  [vendor-id]
  (let [deliveries (queries/get-vendor-not-pickup-deliveries vendor-id)]
    [:delivery-options {} (map delivery-node deliveries)]))

(defn shop-tree
  [vendor-id]
  (let [vendor (queries/get-vendor vendor-id)]
    [:shop {}
     [:name     {} (:name vendor)]
     [:company  {} (:company-name vendor)]
     [:url      {} (:url vendor)]
     [:platform {} config/platform]
     [:version  {} config/version]
     [:agency   {} config/agency]
     [:email    {} config/email]
     (currencies-tree (:currency-iso-code vendor))
     (categories-tree vendor-id)
     (deliveries-tree vendor-id)
     (offers-tree vendor-id)]))

(defn yml-catalog-tree
  [vendor-id]
  [:yml_catalog {:date (date/format-date)} (shop-tree vendor-id)])

(defn generate-tree
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (:yandex-market doctype)
       (html (yml-catalog-tree vendor-id))))