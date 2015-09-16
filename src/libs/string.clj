(ns libs.string
  (:require [clojure.string :as s]))

(defn escape-double-quotes [str]
  (s/replace str #"\"" "'"))