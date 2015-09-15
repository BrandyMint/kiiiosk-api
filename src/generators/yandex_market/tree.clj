(ns generators.yandex-market.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [libs.date :as date]
            [libs.money :as money]
            [libs.queries :as queries]
            [generators.libs.params :refer [params]]
            [generators.libs.currencies :refer [currencies]]
            [generators.libs.categories :refer [categories]]
            [config]))

(def ^:private doctype
  {:yandex-market "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

(defn offer
  "Принимает сущность типа Product и идентификатор продавца (vendor-id)
  и возвращает hiccup-представление offer"
  [product vendor-id]
  [:offer {:id (:id product) :available true}
   [:url         {} (:url product)]
   [:picture     {} (:picture-url product)]
   [:name        {} (:title product)]
   [:description {} (:description product)]
   [:categoryId  {} (first (:categories-ids product))]
   [:currencyId  {} (:price-currency product)]
   [:price       {} (money/minor-units->major-units (:price product))]
   [:oldprice    {} (money/minor-units->major-units (:oldprice product))]
   (params-nodes (:custom-attributes product) vendor-id)])

(defn offers
  "Принимает идентификатор продавца (vendor-id) получает список его продуктов,
  и возвращает hiccup-представление offers"
  [vendor-id]
  (let [products (queries/get-vendor-products vendor-id)]
    [:offers {}
     (map #(offer % vendor-id) products)]))

(defn delivery
  "Принимает сущность типа Delivery, и возвращает hiccup-представление элемента
  option"
  [{:keys [price]}]
    [:option {:cost (money/minor-units->major-units price)}])

(defn deliveries
  "Принимает идентификатор продавца (vendor-id) получает список его доставок,
  и возвращает hiccup-представление элементов delivery и pickup"
  [vendor-id]
  (let [deliveries (queries/get-vendor-not-pickup-deliveries vendor-id)]
    [:delivery-options {} (map delivery deliveries)]))

(defn shop
  "Принимает идентификатор продавца (vendor-id) получает данные о нём, и возвращает
  hiccup-представление shop"
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
     (currencies (:currency-iso-code vendor))
     (categories vendor-id)
     (deliveries vendor-id)
     (offers vendor-id)]))

(defn yml-catalog
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  yml_catalog"
  [vendor-id]
  [:yml_catalog {:date (date/format-date)} (shop vendor-id)])

(defn generate-tree
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  всего дерева тегов"
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (:yandex-market doctype)
       (html (yml-catalog vendor-id))))