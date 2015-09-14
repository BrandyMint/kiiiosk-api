(ns generators.libs.params
  (:require [libs.queries :as queries]))

(defn params-nodes
  [custom-attributes vendor-id]
  (for [custom-attribute custom-attributes
        :let [property (queries/get-vendor-property vendor-id (key custom-attribute))
              property-value (if (= (:type property) "PropertyDictionary")
                               (:name (queries/get-vendor-dictionary-entity vendor-id (val custom-attribute)))
                               (val custom-attribute))]]
    [:param {:name (:title property)} property-value]))