(ns generators.libs.params
  (:require [clojure.tools.logging :as log]
            [libs.queries :as queries]))

(defn params
  "Принимает хеш custom-attributes и идентификатор продавца (vendor-id) и
  возвращает hiccup-представление param."
  [custom-attributes vendor-id]
  (log/info "Processing params")
  (for [custom-attribute custom-attributes
        :let [property (queries/get-vendor-property vendor-id (key custom-attribute))
              property-value (if (= (:type property) "PropertyDictionary")
                               (:name (queries/get-vendor-dictionary-entity vendor-id (val custom-attribute)))
                               (val custom-attribute))]]
    [:param {:name (:title property)} property-value]))