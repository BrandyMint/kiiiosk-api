(ns generators.yandex-market.tree
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [generators.libs.money :as m]
            [generators.libs.queries :as q]
            [generators.libs.params :refer [params]]
            [generators.libs.categories :refer [categories]]
            [generators.libs.currencies :refer [currencies]]
            [config]))

(def ^:private doctype
  {:yandex-market "<!DOCTYPE yml_catalog SYSTEM \"shops.dtd\">\n"})

(defn offer
  [offer vendor-id]
  [:offer {:id (:id offer) :available true}
   [:url         {} (:url offer)]
   [:name        {} (:title offer)]
   [:picture     {} (:picture-url offer)]
   [:description {} (:description offer)]
   [:categoryId  {} (:category-id offer)]
   [:currencyId  {} (:currency-iso-code offer)]
   [:price       {} (:price offer)]
   [:oldprice    {} (:oldprice offer)]
   (params (:custom-attributes offer) vendor-id)])

(defn offers
  [vendor-id]
  (let [offers (q/get-vendor-offers vendor-id)]
    [:offers {}
     (map #(offer % vendor-id) offers)]))

(defn delivery
  [{:keys [cost]}]
  [:option {:cost cost}])

(defn deliveries
  [vendor-id]
  (let [deliveries (q/get-vendor-not-pickup-deliveries vendor-id)]
    [:delivery-options {} (map delivery deliveries)]))

(defn shop
  [vendor-id]
  (let [vendor (q/get-vendor vendor-id)]
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
     (deliveries vendor-id)
     (offers vendor-id)]))

(defn yml-catalog
  [vendor-id]
  (let [date (.format (java.text.SimpleDateFormat. "YYYY-MM-dd hh:mm")
                      (java.util.Date.))]
    [:yml_catalog {:date date} (shop-branch vendor-id)]))

(defn generate-tree
  [vendor-id]
  (str (xml-declaration "windows-1251")
       (:yandex-market doctype)
       (html (yml-catalog vendor-id))))