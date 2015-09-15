(ns generators.libs.currencies
  (:require [libs.money :as money]))

(defn currencies
  "Принимает код валюты и возвращает hiccup-представление currencies."
  [currency_iso_code]
  (let [rate (money/get-currency-rate currency_iso_code)]
    [:currencies {}
     [:currency {:id currency_iso_code :rate rate}]]))