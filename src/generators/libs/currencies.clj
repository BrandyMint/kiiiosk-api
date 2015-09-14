(ns generators.libs.currencies
  (:require [generators.libs.money :as m]))

(defn currencies-branch
  [currency]
  (let [rate (m/get-currency-rate currency)]
    [:currencies {}
     [:currency {:id currency :rate rate}]]))