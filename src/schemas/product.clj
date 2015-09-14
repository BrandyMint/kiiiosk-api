(ns schemas.product
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Product
  {:id s/Int
   :url (s/maybe s/Str)
   :title s/Str
   :description (s/maybe s/Str)
   :price-kopeks s/Int
   :price-currency s/Str
   :oldprice-kopeks (s/maybe s/Int)
   :oldprice-currency (s/maybe s/Str)
   :categories-ids [s/Int]
   :picture-url (s/maybe s/Str)
   :custom-attributes s/Any})

(defn DBProduct->Product
  [{:keys [id is_sale price_currency sale_price_currency price_kopeks sale_price_kopeks cached_public_url
           categories_ids cached_image_url title stock_title description stock_description data]}]
  {:id id
   :url cached_public_url
   :title (or title stock_title)
   :description (or description stock_description)
   :price-kopeks (if is_sale sale_price_kopeks price_kopeks)
   :price-currency (if is_sale sale_price_currency price_currency)
   :oldprice-kopeks price_kopeks
   :oldprice-currency price_currency
   :categories-ids categories_ids
   :picture-url cached_image_url
   :custom-attributes data})

(def DBProduct->Product-coercer
  (coerce/coercer Product {Product DBProduct->Product}))