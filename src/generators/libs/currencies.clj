(ns generators.libs.currencies
  (:require [clojure.tools.logging :as log]
            [libs.money :as money]))

(defn currencies-markup
  "Принимает код валюты и возвращает hiccup-представление currencies."
  [currency_iso_code]
  (log/info "Processing categories")
  (let [rate (money/get-currency-rate currency_iso_code)]
    [:currencies
     [:currency {:id currency_iso_code :rate rate}]]))