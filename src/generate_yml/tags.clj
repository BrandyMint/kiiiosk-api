(ns generate-yml.tags
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generate-yml.money :refer :all]
            [generate-yml.queries :refer :all]
            [config :refer [platform version agency email]]))

(def doctype
  {:yandex-market
    "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

(defn generate-currencies-tags
  [currency]
  (let [rate (get-currency-rate currency)]
    [:currency {:id currency :rate rate}]))

(defn ancestry-count? [{:keys [ancestry]}]
  (if (nil? ancestry)
    0
    (count (re-seq #"[\d]*[\d]" ancestry))))

(defn generate-categories-tags
  [categories]
  (let [sorted-by-ancestry (sort-by ancestry-count? categories)]
    (map (fn [{:keys [id name ancestry]}]
           (let [parentId (if (nil? ancestry)
                            nil
                            (re-find #"\d*$" ancestry))]
             [:category {:id id :parentId parentId} name]))
         sorted-by-ancestry)))

(defn generate-delivery-options-tags
  [deliveries]
  (map (fn [{:keys [price_currency price_kopeks]}]
           [:option {:cost (minor-units->major-units price_currency price_kopeks)}])
       deliveries))

(defn generate-param-tags
  [vendor-id properties]
  (for [property-data properties]
    (let [property (get-vendor-property vendor-id (key property-data))
          property-value (if (= (:type property) "PropertyDictionary")
                           (:name (get-vendor-dictionary-entity vendor-id (val property-data)))
                           (val property-data))]
      [:param {:name (:title property)} property-value])))

(defn generate-offers-tags
  [vendor-id offers]
  (map (fn [offer]
         [:offer {:id (:id offer) :available true }
           [:url {} (:cached_public_url offer)]
           (if (:is_sale offer)
             [:price {} (minor-units->major-units (:sale_price_currency offer) (:sale_price_kopeks offer))]
             [:price {} (minor-units->major-units (:price_currency offer) (:price_kopeks offer))])
           (when (:is_sale offer)
             [:oldprice {} (minor-units->major-units (:price_currency offer) (:price_kopeks offer))])
           [:currencyId (:price_currency offer)]
           [:categoryId {} (first (:categories_ids offer))]
           [:picture {} (:cached_image_url offer)]
           [:name {} (or (:title offer) (:stock_title offer))]
           [:description {} (or (:description offer) (:stock_description offer))]
           (generate-param-tags vendor-id (:data offer))
         ])
       offers))

(defn generate-shop-tags
  [vendor-id]
  (let [vendor (get-vendor vendor-id)
        vendor-categories (get-vendor-categories vendor-id)
        vendor-deliveries (get-vendor-deliveries vendor-id)
        vendor-offers (get-vendor-offers vendor-id)]
    [:shop {}
      [:name {} (:name vendor)]
      [:company {} (:company_name vendor)]
      [:url {} (:cached_home_url vendor)]
      [:platform {} platform]
      [:version {} version]
      [:agency {} agency]
      [:email {} email]
      [:currencies {} (generate-currencies-tags (:currency_iso_code vendor))]
      [:categories {} (generate-categories-tags vendor-categories)]
      [:delivery-options {} (generate-delivery-options-tags vendor-deliveries)]
      [:offers {} (generate-offers-tags vendor-id vendor-offers)]]))

(defn generate-tags
  [vendor-id]
  (let [date (.format (java.text.SimpleDateFormat. "YYYY-MM-dd hh:mm") (java.util.Date.))]
    (str (xml-declaration "windows-1251")
         (doctype :yandex-market)
         (html [:yml_catalog {:date date}
                (generate-shop-tags vendor-id)]))))