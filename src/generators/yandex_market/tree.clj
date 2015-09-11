(ns generators.yandex-market.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generators.utils.money :as m]
            [generators.utils.queries :as q]
            [config]))

(def ^:private doctype
  {:yandex-market "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

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
   (when (:is_sale offer)
     [:oldprice {} (m/minor-units->major-units (:price_currency offer)
                                               (:price_kopeks offer))])
   (params-branch (:data offer) vendor-id)])

(defn offers-branch
  [vendor-id]
  (let [offers (q/get-vendor-offers vendor-id)]
    [:offers {}
     (map #(offer-branch % vendor-id) offers)]))

(defn delivery-branch
  [{:keys [price_currency price_kopeks]}]
    [:option {:cost (m/minor-units->major-units price_currency price_kopeks)}])

(defn deliveries-branch
  [vendor-id]
  (let [deliveries (q/get-vendor-not-pickup-deliveries vendor-id)]
    [:delivery-options {} (map delivery-branch deliveries)]))

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
     [:platform {} config/platform]
     [:version {} config/version]
     [:agency {} config/agency]
     [:email {} config/email]
     (currencies-branch (:currency_iso_code vendor))
     (categories-branch vendor-id)
     (deliveries-branch vendor-id)
     (offers-branch vendor-id)]))

(defn yml-catalog-branch
  [vendor-id]
  (let [date (.format (java.text.SimpleDateFormat. "YYYY-MM-dd hh:mm")
                      (java.util.Date.))]
    [:yml_catalog {:date date} (shop-branch vendor-id)]))

(defn generate-tree
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (:yandex-market doctype)
       (html (yml-catalog-branch vendor-id))))