(ns generators.libs.categories
  (:require [generators.libs.queries :as q]))

(defn- ancestors-count
  [ancestry]
  (count (when ancestry (re-seq #"[\d]*[\d]" ancestry))))

(defn category
  [{:keys [id name ancestry]}]
  (let [parent-id (when ancestry (re-find #"\d*$" ancestry))]
    [:category {:id id :parentId parent-id} name]))

(defn categories
  [vendor-id]
  (let [categories (q/get-vendor-categories vendor-id)]
    [:categories {}
     (map category (sort-by #(ancestors-count (:ancestry %)) categories))]))