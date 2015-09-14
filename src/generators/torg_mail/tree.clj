(ns generators.torg-mail.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generators.libs.money :as m]
            [generators.libs.queries :as q]
            [generators.libs.params :as params]
            [generators.libs.categories :as categories]
            [generators.libs.currencies :as currencies]
            [config]))

;; Описание формата и тонкости:
;;   http://torg.mail.ru/info/122/
;; Пример заполненного XML:
;;   http://dem.mymerchium.ru/users/8539/files/1/export_mailru.xml

;; Отсутствуют:
;; - offer[typePrefix]
;; - offer[vendor]
;; - local_delivery_cost

(defn offer-branch
  [offer vendor-id]
  [:offer {:id (:id offer) :available true}
   [:url {} (:cached_public_url offer)]
   [:picture {} (:cached_image_url offer)]
   [:name {} (or (:title offer) (:stock_title offer))]
   [:description {} (or (:description offer) (:stock_description offer))]
   [:categoryId {} (first (:categories_ids offer))]
   [:currencyId (:price_currency offer)]
   (if (:is_sale offer)
     [:price {} (m/minor-units->major-units (:sale_price_currency offer)
                                            (:sale_price_kopeks offer))]
     [:price {} (m/minor-units->major-units (:price_currency offer)
                                            (:price_kopeks offer))])
   (params/params-branch (:data offer) vendor-id)])

(defn offers-branch
  [vendor-id]
  (let [offers (q/get-vendor-offers vendor-id)]
    [:offers {}
     (map #(offer-branch % vendor-id) offers)]))

(defn- has-deliveries?
  [deliveries]
  (-> deliveries count pos?))

(defn- has-pickup?
  [deliveries]
  (some #(= (:delivery_agent_type %) "OrderDeliveryPickup")
        deliveries))

(defn deliveries-branch
  [vendor-id]
  (let [deliveries (q/get-vendor-deliveries vendor-id)]
    (seq [[:delivery {} (has-deliveries? deliveries)]
          [:pickup {} (has-pickup? deliveries)]])))

(defn shop-branch
  [vendor-id]
  (let [vendor (q/get-vendor vendor-id)]
    [:shop {}
     [:name {} (:name vendor)]
     [:company {} (:company_name vendor)]
     [:url {} (:cached_home_url vendor)]
     (currencies/currencies-branch (:currency_iso_code vendor))
     (categories/categories-branch vendor-id)
     (deliveries-branch vendor-id)
     (offers-branch vendor-id)]))

(defn torg-price-branch
  [vendor-id]
  (let [date (.format (java.text.SimpleDateFormat. "YYYY-MM-dd hh:mm")
                      (java.util.Date.))]
    [:torg_price {:date date} (shop-branch vendor-id)]))

(defn generate-tree
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (html (torg-price-branch vendor-id))))