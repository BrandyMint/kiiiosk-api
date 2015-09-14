(ns schemas.vendor
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Vendor
  {:id s/Int
   :name s/Str
   :company-name (s/maybe s/Str)
   :url (s/maybe s/Str)
   :currency-iso-code s/Str})

(defn DBVendor->Vendor
  [{:keys [id name company_name currency_iso_code cached_home_url]}]
  {:id id
   :name name
   :company-name company_name
   :url cached_home_url
   :currency-iso-code currency_iso_code})

(def DBVendor->Vendor-coercer
  (coerce/coercer Vendor {Vendor DBVendor->Vendor}))