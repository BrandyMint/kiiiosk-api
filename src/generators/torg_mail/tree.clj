(ns generators.torg-mail.tree
  (:require [clojure.tools.logging :as log]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [libs.date :as date]
            [libs.money :as money]
            [libs.queries :as queries]
            [libs.deliveries :refer [delivery-has-pickup? delivery-has-deliveries?]]
            [generators.libs.params :refer [params]]
            [generators.libs.currencies :refer [currencies]]
            [generators.libs.categories :refer [categories]]
            [config]))

;; Описание формата и тонкости:
;;   http://torg.mail.ru/info/122/
;; Пример заполненного XML:
;;   http://dem.mymerchium.ru/users/8539/files/1/export_mailru.xml

;; Отсутствуют:
;; - offer[typePrefix]
;; - offer[vendor]
;; - local_delivery_cost

(defn offer
  "Принимает сущность типа Product и идентификатор продавца (vendor-id)
  и возвращает hiccup-представление offer"
  [product vendor-id]
  (log/info (str "Processing offer with ID " (:id product)))
  [:offer {:id (:id product) :available true}
   [:url         {} (:url product)]
   [:picture     {} (:picture-url product)]
   [:name        {} (:title product)]
   [:description {} (:description product)]
   [:categoryId  {} (first (:categories-ids product))]
   [:currencyId  {} (:price-currency product)]
   [:price       {} (money/minor-units->major-units (:price product))]
   (params (:custom-attributes product) vendor-id)])

(defn offers
  "Принимает идентификатор продавца (vendor-id) получает список его продуктов,
  и возвращает hiccup-представление offers"
  [vendor-id]
  (log/info "Processing offers")
  (let [products (queries/get-vendor-products vendor-id)]
    [:offers {}
     (map #(offer % vendor-id) products)]))

(defn deliveries
  "Принимает идентификатор продавца (vendor-id) получает список его доставок,
  и возвращает hiccup-представление элементов delivery и pickup"
  [vendor-id]
  (log/info "Processing deliveries")
  (let [deliveries (queries/get-vendor-deliveries vendor-id)]
    (seq [[:delivery {} (delivery-has-deliveries? deliveries)]
          [:pickup   {} (delivery-has-pickup? deliveries)]])))

(defn shop
  "Принимает идентификатор продавца (vendor-id) получает данные о нём, и возвращает
  hiccup-представление shop"
  [vendor-id]
  (log/info (str "Processing shop with ID " vendor-id))
  (let [vendor (queries/get-vendor vendor-id)]
    [:shop {}
     [:name    {} (:name vendor)]
     [:company {} (:company-name vendor)]
     [:url     {} (:url vendor)]
     (currencies (:currency-iso-code vendor))
     (categories vendor-id)
     (deliveries vendor-id)
     (offers vendor-id)]))

(defn torg-price
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  torg_price"
  [vendor-id]
  (log/info "Processing torg_price")
  [:torg_price {:date (date/format-date)} (shop vendor-id)])

(defn generate-tree
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  всего дерева тегов"
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (html (torg-price vendor-id))))