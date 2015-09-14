(ns generators.libs.categories
  (:require [libs.queries :as queries]
            [libs.categories :as categories]))

(defn- category-node
  [{:keys [id name ancestry]}]
  (let [parent-id (when ancestry (re-find #"\d*$" ancestry))]
    [:category {:id id :parentId parent-id} name]))

(defn categories-tree
  [vendor-id]
  (let [categories (queries/get-vendor-categories vendor-id)]
    [:categories {}
     (map category-node
          (sort-by #(categories/ancestors-count (:ancestry %))
                   categories))]))