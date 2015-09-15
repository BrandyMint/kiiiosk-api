(ns schemas.delivery
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [schemas.money :refer [Money]]))

(s/defschema Delivery
  {:id s/Int
   :price Money
   :delivery-agent-type s/Str})

(defn DBDelivery->Delivery
  [{:keys [id price_currency price_kopeks delivery_agent_type]}]
  {:id id
   :price {:kopeks price_kopeks
           :currency price_currency}
   :delivery-agent-type delivery_agent_type})

(def DBDelivery->Delivery-coercer
  (coerce/coercer Delivery {Delivery DBDelivery->Delivery}))