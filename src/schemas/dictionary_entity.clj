(ns schemas.dictionary-entity
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema DictionaryEntity
  {:id s/Int
   :name s/Str})

(defn DBDictionaryEntity->DictionaryEntity
  [{:keys [id name]}]
  {:id id
   :name name})

(def DBDictionaryEntity->DictionaryEntity-coercer
  (coerce/coercer DictionaryEntity {DictionaryEntity DBDictionaryEntity->DictionaryEntity}))