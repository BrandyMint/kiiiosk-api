(ns generators.libs.categories
  (:require [generators.libs.queries :as q]))

(defn- ancestors-count
  [{:keys [ancestry]}]
  (count (when ancestry (re-seq #"[\d]*[\d]" ancestry))))

(defn category-branch
  [{:keys [id name ancestry]}]
  (let [parent-id (when ancestry (re-find #"\d*$" ancestry))]
    [:category {:id id :parentId parent-id} name]))

(defn categories-branch
  [vendor-id]
  (let [categories (q/get-vendor-categories vendor-id)]
    [:categories {}
     (map category-branch (sort-by ancestors-count categories))]))