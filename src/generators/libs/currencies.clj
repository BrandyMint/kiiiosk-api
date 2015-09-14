(ns generators.libs.currencies
  (:require [generators.libs.money :as m]))

(defn currencies
  [currency_iso_code]
  (let [rate (m/get-currency-rate currency_iso_code)]
    [:currencies {}
     [:currency {:id currency_iso_code :rate rate}]]))