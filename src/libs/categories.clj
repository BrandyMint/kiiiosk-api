(ns libs.categories)

(defn ancestors-count
  [ancestry]
  (count (when ancestry (re-seq #"[\d]*[\d]" ancestry))))