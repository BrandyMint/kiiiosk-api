(ns schemas.category
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Category
  {:id s/Int
   :name s/Str
   :ancestry (s/maybe s/Str)})

(defn DBCategory->Category
  [{:keys [id cached_title ancestry]}]
  {:id id
   :name cached_title
   :ancestry ancestry})

(def DBCategory->Category-coercer
  (coerce/coercer Category {Category DBCategory->Category}))
