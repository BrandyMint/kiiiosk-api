(ns generators.libs.params
  (:require [generators.libs.queries :as q]))

(defn params-branch
  [params-data vendor-id]
  (for [param-data params-data
        :let [param (q/get-vendor-property vendor-id (key param-data))
              param-value (if (= (:type param) "PropertyDictionary")
                            (:name (q/get-vendor-dictionary-entity vendor-id (val param-data)))
                            (val param-data))]]
    [:param {:name (:title param)} param-value]))