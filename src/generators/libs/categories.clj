(ns generators.libs.categories
  (:require [libs.queries :as queries]
            [libs.categories :as categories]))

(defn- category
  "Принимает сущность типа Category и возвращает hiccup-представление category"
  [{:keys [id name ancestry]}]
  (let [parent-id (when ancestry (re-find #"\d*$" ancestry))]
    [:category {:id id :parentId parent-id} name]))

(defn categories
  "Принимает идентификатор продавца (vendor-id) и возвращает hiccup-представление
  categories."
  [vendor-id]
  (let [categories (queries/get-vendor-categories vendor-id)]
    [:categories {}
     (map category
          (sort-by #(categories/ancestors-count (:ancestry %))
                   categories))]))