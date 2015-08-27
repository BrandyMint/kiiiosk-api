(ns kiosk-open-api.coercion
  (:require [clojure.set]
            [schema.coerce :as coerce]))

(defn excluded-keys [schema datum]
  (clojure.set/difference 
    (set (keys datum))
    (set (keys schema))))

(defn coercion-matcher [schema] 
  (fn [datum] 
    (if (instance? clojure.lang.PersistentHashMap schema)
      (apply dissoc datum (excluded-keys schema datum))
      datum)))

(defn coerce-elastic-resource [resource schema] 
  ((coerce/coercer schema coercion-matcher) (resource :_source)))