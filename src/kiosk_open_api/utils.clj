(ns kiosk-open-api.utils
  (:require [clojurewerkz.elastisch.rest.document :as doc]
            [kiosk-open-api.coercion :refer :all]
            [config]))

(defn get-esd [resource id]
  (println config/esr-conn)
  (doc/get config/esr-conn config/esr-index-name resource (str id)))

(defn get-coerced-esd [resource schema id]
  (coerce-elastic-resource (get-esd resource id) schema))