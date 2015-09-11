(ns generators.torg-mail.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generators.utils.money :as m]
            [generators.utils.queries :as q]
            [config]))

;; Описание формата и тонкости:
;;   http://torg.mail.ru/info/122/
;; Пример заполненного XML:
;;   http://dem.mymerchium.ru/users/8539/files/1/export_mailru.xml

;; Отсутствуют:
;; - offer[typePrefix]
;; - offer[vendor]
;; - local_delivery_cost

(defn params-branch
  [params-data vendor-id]
  (for [param-data params-data
        :let [param (q/get-vendor-property vendor-id (key param-data))
              param-value (if (= (:type param) "PropertyDictionary")
                            (:name (q/get-vendor-dictionary-entity vendor-id (val param-data)))
                            (val param-data))]]
    [:param {:name (:title param)} param-value]))

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
   (params-branch (:data offer) vendor-id)])

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

(defn- ancestors-count
  [{:keys [ancestry]}]
  (count (when ancestry (re-seq #"[\d]*[\d]" ancestry))))

(defn category-branch
  [{:keys [id name ancestry]}]
  (let [parent-id (when ancestry (re-find #"\d*$" ancestry))]
    [:category {:id id :parentId parent-id} name]))

(defn categories-branch
  [vendor-id]
  (let [categories (q/get-vendor-categories vendor-id)]
    [:categories {}
     (map category-branch (sort-by ancestors-count categories))]))

(defn currencies-branch
  [currency]
  (let [rate (m/get-currency-rate currency)]
    [:currencies {}
     [:currency {:id currency :rate rate}]]))

(defn shop-branch
  [vendor-id]
  (let [vendor (q/get-vendor vendor-id)]
    [:shop {}
     [:name {} (:name vendor)]
     [:company {} (:company_name vendor)]
     [:url {} (:cached_home_url vendor)]
     (currencies-branch (:currency_iso_code vendor))
     (categories-branch vendor-id)
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