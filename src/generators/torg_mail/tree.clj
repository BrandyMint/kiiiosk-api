(ns generators.torg-mail.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [libs.date :as date]
            [libs.money :as money]
            [libs.queries :as queries]
            [libs.deliveries :as deliveries]
            [generators.libs.params :refer [params-nodes]]
            [generators.libs.currencies :refer [currencies-tree]]
            [generators.libs.categories :refer [categories-tree]]
            [config]))

;; Описание формата и тонкости:
;;   http://torg.mail.ru/info/122/
;; Пример заполненного XML:
;;   http://dem.mymerchium.ru/users/8539/files/1/export_mailru.xml

;; Отсутствуют:
;; - offer[typePrefix]
;; - offer[vendor]
;; - local_delivery_cost

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
   (params-nodes (:custom-attributes product) vendor-id)])

(defn offers-tree
  [vendor-id]
  (let [products (queries/get-vendor-products vendor-id)]
    [:offers {}
     (map #(offer-tree % vendor-id) products)]))

(defn deliveries-nodes
  [vendor-id]
  (let [deliveries (queries/get-vendor-deliveries vendor-id)]
    (seq [[:delivery {} (deliveries/has-deliveries? deliveries)]
          [:pickup {} (deliveries/has-pickup? deliveries)]])))

(defn shop-tree
  [vendor-id]
  (let [vendor (queries/get-vendor vendor-id)]
    [:shop {}
     [:name     {} (:name vendor)]
     [:company  {} (:company-name vendor)]
     [:url      {} (:url vendor)]
     (currencies-tree (:currency-iso-code vendor))
     (categories-tree vendor-id)
     (deliveries-nodes vendor-id)
     (offers-tree vendor-id)]))

(defn torg-price-tree
  [vendor-id]
  [:torg_price {:date (date/format-date)} (shop-tree vendor-id)])

(defn generate-tree
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (html (torg-price-tree vendor-id))))