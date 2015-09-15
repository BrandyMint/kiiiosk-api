(ns libs.money
  "Functions for processing funding operations")

;; TODO: Migrate to https://github.com/clojurewerkz/money
(defn minor-units->major-units
  [{:keys [currency kopeks]}]
  (if (= "RUB" currency)
    (/ kopeks 100)
    (throw (Exception. "Only \"RUB\" currency supported!"))))

(defn get-currency-rate
  [currency]
  (if (= "RUB" currency)
    1
    (throw (Exception. "Only \"RUB\" currency supported!"))))