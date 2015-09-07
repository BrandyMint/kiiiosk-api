(ns kiosk-open-api.schemas
  (:require [schema.core :refer [defschema enum]]))

;;
;; Schemas
;;

(defschema ProductCard
  {:id Integer
   :title String})

(defschema Thingie
  {:id Long
   :hot Boolean
   :tag (enum :kikka :kukka)
   :chief [{:name String :id Long}]})