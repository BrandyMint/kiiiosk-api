(ns kiosk-open-api.schemas
  (:require [schema.core :as s]
            ))

;;
;; Schemas
;;

(s/defschema ProductCard
  {:id Integer
   ;;:type String
   :title String
   ;; :categories [{:name String :id Long}]
   }
  )

(s/defschema Thingie
  {:id Long
   :hot Boolean
   :tag (s/enum :kikka :kukka)
   :chief [{:name String :id Long}]
   })
