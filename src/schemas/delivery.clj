(ns schemas.delivery
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Delivery
  {:id s/Int
   :price-kopeks (s/maybe s/Int)
   :price-currency (s/maybe s/Str)
   :delivery-agent-type s/Str})

(defn DBDelivery->Delivery
  [{:keys [id price_currency price_kopeks delivery_agent_type]}]
  {:id id
   :price-kopeks price_kopeks
   :price-currency price_currency
   :delivery-agent-type delivery_agent_type})

(def DBDelivery->Delivery-coercer
  (coerce/coercer Delivery {Delivery DBDelivery->Delivery}))