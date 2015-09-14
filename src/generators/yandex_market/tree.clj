(ns generators.yandex-market.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generators.libs.money :as m]
            [generators.libs.queries :as q]
            [generators.libs.params :as params]
            [generators.libs.categories :as categories]
            [generators.libs.currencies :as currencies]
            [config]))

(def ^:private doctype
  {:yandex-market "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

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
   (params/params-branch (:data offer) vendor-id)])

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
     (currencies/currencies-branch (:currency_iso_code vendor))
     (categories/categories-branch vendor-id)
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