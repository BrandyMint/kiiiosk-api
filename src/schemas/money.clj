(ns schemas.money
  (:require [schema.core :as s]
            [schema.coerce :as coerce]))

(s/defschema Money
  {:kopeks (s/maybe s/Int)
   :currency (s/maybe s/Str)})