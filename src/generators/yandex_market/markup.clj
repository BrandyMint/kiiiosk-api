(ns generators.yandex-market.markup
  (:require [clojure.tools.logging :as log]
            [hiccup.core :refer [html]]
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

(defn offer-markup
  "Принимает сущность типа Product и идентификатор продавца (vendor-id)
  и возвращает hiccup-представление offer"
  [product vendor-id]
  (log/info (str "Processing offer with ID " (:id product)))
  [:offer {:id (:id product) :available "true"}
   [:url        {} (:url product)]
   [:name       {} (:title product)]
   [:categoryId {} (first (:categories-ids product))]
   [:price      {} (money/minor-units->major-units (:price product))]
   [:currencyId {} (money/get-currency (:price product))]
   (when (:picture-url product)
     [:picture {} (:picture-url product)])
   (when (:description product)
     [:description {} (:description product)])
   (when (money/has-different-values? (:price product)
                                      (:oldprice product))
     [:oldprice {} (money/minor-units->major-units (:oldprice product))])
   (params (:custom-attributes product) vendor-id)])

(defn offers-markup
  "Принимает идентификатор продавца (vendor-id) получает список его продуктов,
  и возвращает hiccup-представление offers"
  [vendor-id]
  (log/info "Processing offers")
  (let [products (queries/get-vendor-products vendor-id)]
    [:offers {}
     (map #(offer-markup % vendor-id) products)]))

(defn delivery-markup
  "Принимает сущность типа Delivery, и возвращает hiccup-представление элемента
  option"
  [{:keys [id price]}]
  (log/info (str "Processing delivery with ID " id))
  [:option {:cost (money/minor-units->major-units price)}])

(defn deliveries-markup
  "Принимает идентификатор продавца (vendor-id) получает список его доставок,
  и возвращает hiccup-представление элементов delivery и pickup"
  [vendor-id]
  (log/info "Processing deliveries")
  (let [deliveries (queries/get-vendor-not-pickup-deliveries vendor-id)]
    [:delivery-options {} (map delivery-markup deliveries)]))

(defn shop-markup
  "Принимает идентификатор продавца (vendor-id) получает данные о нём, и возвращает
  hiccup-представление shop"
  [vendor-id]
  (log/info (str "Processing shop with ID " vendor-id))
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
     (deliveries-markup vendor-id)
     (offers-markup vendor-id)]))

(defn yml-catalog-markup
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  yml_catalog"
  [vendor-id]
  (log/info "Processing yml_catalog")
  [:yml_catalog {:date (date/format-date)} (shop-markup vendor-id)])

(defn generate-markup
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  всего дерева тегов"
  [vendor-id]
  (str (xml-declaration "UTF-8")
       (:yandex-market doctype)
       (html (yml-catalog-markup vendor-id))))