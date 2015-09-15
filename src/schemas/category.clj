(ns schemas.category
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Category
  {:id s/Int
   :name s/Str
   :ancestry (s/maybe s/Str)})

(defn DBCategory->Category
  [{:keys [id name ancestry]}]
  {:id id
   :name name
   :ancestry ancestry})

(def DBCategory->Category-coercer
  (coerce/coercer Category {Category DBCategory->Category}))