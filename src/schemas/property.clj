(ns schemas.property
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Property
  {:id s/Int
   :type s/Str
   :title s/Str})

(defn DBProperty->Property
  [{:keys [id type title]}]
  {:id id
   :type type
   :title title})

(def DBProperty->Property-coercer
  (coerce/coercer Property {Property DBProperty->Property}))