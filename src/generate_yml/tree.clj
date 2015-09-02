(ns generate-yml.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generate-yml.queries :as queries]
            [generate-yml.money :as money]
            [config]))

(def ^:private doctype
  {:yandex-market
    "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

(defn ancestors-count
  [{:keys [ancestry]}]
  (count (when ancestry (re-seq #"[\d]*[\d]" ancestry))))

(defn generate-currencies-nodes
  [currency]
  (let [rate (money/get-currency-rate currency)]
    [:currencies {}
     [:currency {:id currency :rate rate}]]))

(defn generate-categories-nodes
  [categories]
  [:currencies {}
   (map (fn [{:keys [id name ancestry]}]
          (let [parentId (when ancestry (re-find #"\d*$" ancestry))]
            [:category {:id id :parentId parentId} name]))
        (sort-by ancestors-count categories))])

(defn generate-delivery-options-nodes 
  [delivery-options]
  [:delivery-options {}
   (map (fn [{:keys [price_currency price_kopeks]}]
          [:option {:cost (money/minor-units->major-units price_currency price_kopeks)}])
        delivery-options)])

(defn generate-param-nodes
  [vendor-id properties]
  (for [data properties
        :let [property (queries/get-vendor-property vendor-id (key data))
              property-value (if (= (:type property) "PropertyDictionary")
                               (:name (queries/get-vendor-dictionary-entity vendor-id (val data)))
                               (val data))]]
    [:param {:name (:title property)} property-value]))

(defn generate-offers-nodes
  [vendor-id offers]
  [:offers {}
   (map (fn [offer]
          [:offer {:id (:id offer) :available true}
           [:url {} (:cached_public_url offer)]
           (if (:is_sale offer)
             [:price {} (money/minor-units->major-units (:sale_price_currency offer) (:sale_price_kopeks offer))]
             [:price {} (money/minor-units->major-units (:price_currency offer) (:price_kopeks offer))])
           (when (:is_sale offer)
             [:oldprice {} (money/minor-units->major-units (:price_currency offer) (:price_kopeks offer))])
           [:currencyId (:price_currency offer)]
           [:categoryId {} (first (:categories_ids offer))]
           [:picture {} (:cached_image_url offer)]
           [:name {} (or (:title offer) (:stock_title offer))]
           [:description {} (or (:description offer) (:stock_description offer))]
           (generate-param-nodes vendor-id (:data offer))])
        offers)])

(defn generate-shop-nodes
  [vendor-id]
  (let [vendor (queries/get-vendor vendor-id)
        vendor-categories (queries/get-vendor-categories vendor-id)
        vendor-deliveries (queries/get-vendor-not-pickup-deliveries vendor-id)
        vendor-offers (queries/get-vendor-offers vendor-id)]
    [:shop {}
     [:name {} (:name vendor)]
     [:company {} (:company_name vendor)]
     [:url {} (:cached_home_url vendor)]
     [:platform {} config/platform]
     [:version {} config/version]
     [:agency {} config/agency]
     [:email {} config/email]
     (generate-currencies-nodes (:currency_iso_code vendor))
     (generate-categories-nodes vendor-categories)
     (generate-delivery-options-nodes vendor-deliveries)
     (generate-offers-nodes vendor-id vendor-offers)]))

(defn generate-tree
  [vendor-id]
  (let [date (.format (java.text.SimpleDateFormat. "YYYY-MM-dd hh:mm") (java.util.Date.))]
    (str (xml-declaration "windows-1251")
         (:yandex-market doctype)
         (html [:yml_catalog {:date date}
                (generate-shop-nodes vendor-id)]))))