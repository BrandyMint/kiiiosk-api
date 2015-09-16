(ns generators.torg-mail.markup
  (:require [clojure.tools.logging :as log]
            [hiccup.core :refer [html h]]
            [hiccup.page :refer [xml-declaration]]
            [libs.date :as date]
            [libs.money :as money]
            [libs.queries :as queries]
            [libs.deliveries :refer [delivery-has-pickup? delivery-has-deliveries?]]
            [generators.libs.params :refer [params-markup]]
            [generators.libs.currencies :refer [currencies-markup]]
            [generators.libs.categories :refer [categories-markup]]
            [config]))

;; Описание формата и тонкости:
;;   http://torg.mail.ru/info/122/
;; Пример заполненного XML:
;;   http://dem.mymerchium.ru/users/8539/files/1/export_mailru.xml

;; Отсутствуют:
;; - offer[typePrefix]
;; - offer[vendor]
;; - local_delivery_cost

(defn offer-markup
  "Принимает сущность типа Product и идентификатор продавца (vendor-id)
  и возвращает hiccup-представление offer"
  [product vendor-id]
  (log/info (str "Processing offer with ID " (:id product)))
  [:offer {:id (:id product) :available "true"}
   [:url (:url product)]
   [:name (h (:title product))]
   [:categoryId (first (:categories-ids product))]
   [:price (money/minor-units->major-units (:price product))]
   [:currencyId (money/get-currency (:price product))]
   [:picture (:picture-url product)]
   (when (:description product)
     [:description (h (:description product))])
   (params-markup (:custom-attributes product) vendor-id)])

(defn offers-markup
  "Принимает идентификатор продавца (vendor-id) получает список его продуктов,
  и возвращает hiccup-представление offers"
  [vendor-id]
  (log/info "Processing offers")
  (let [products (queries/get-vendor-products vendor-id)]
    [:offers (map #(offer-markup % vendor-id) products)]))

(defn deliveries-markup
  "Принимает идентификатор продавца (vendor-id) получает список его доставок,
  и возвращает hiccup-представление элементов delivery и pickup"
  [vendor-id]
  (log/info "Processing deliveries")
  (let [deliveries (queries/get-vendor-deliveries vendor-id)]
    (seq [[:pickup (delivery-has-pickup? deliveries)]
          [:delivery (delivery-has-deliveries? deliveries)]])))

(defn shop-markup
  "Принимает идентификатор продавца (vendor-id) получает данные о нём, и возвращает
  hiccup-представление shop"
  [vendor-id]
  (log/info (str "Processing shop with ID " vendor-id))
  (let [vendor (queries/get-vendor vendor-id)]
    [:shop
     [:url (:url vendor)]
     [:name (h (:name vendor))]
     [:company (h (:company-name vendor))]
     (currencies-markup (:currency-iso-code vendor))
     (categories-markup vendor-id)
     (deliveries-markup vendor-id)
     (offers-markup vendor-id)]))

(defn torg-price-markup
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  torg_price"
  [vendor-id]
  (log/info "Processing torg_price")
  [:torg_price {:date (date/format-date)} (shop-markup vendor-id)])

(defn generate-markup
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  всего дерева тегов"
  [vendor-id]
  (str (xml-declaration "UTF-8")
       (html (torg-price-markup vendor-id))))