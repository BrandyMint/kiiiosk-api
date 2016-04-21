(ns libs.queries
  (:require [schemas.vendor :refer [DBVendor->Vendor-coercer]]
            [schemas.product :refer [DBProduct->Product-coercer]]
            [schemas.category :refer [DBCategory->Category-coercer]]
            [schemas.delivery :refer [DBDelivery->Delivery-coercer]]
            [schemas.property :refer [DBProperty->Property-coercer]]
            [schemas.dictionary-entity :refer [DBDictionaryEntity->DictionaryEntity-coercer]]
            [config :refer [query]]))

(defn get-vendor
  [vendor-id]
  (DBVendor->Vendor-coercer
    (first (query [(str "select * from vendors where id = " vendor-id)]))))

(defn get-vendor-categories
  [vendor-id]
  (map DBCategory->Category-coercer
       (query [(str "select * from categories
                              where deleted_at is null
                              and vendor_id = " vendor-id)])))

(defn get-vendor-deliveries
  [vendor-id]
  (map DBDelivery->Delivery-coercer
       (query [(str "select * from vendor_deliveries
                              where deleted_at is null
                              and vendor_id = " vendor-id)])))

(defn get-vendor-not-pickup-deliveries
  [vendor-id]
  (map DBDelivery->Delivery-coercer
       (query [(str "select * from vendor_deliveries
                              where deleted_at is null
                              and delivery_agent_type != 'OrderDeliveryPickup'
                              and vendor_id = " vendor-id)])))

(defn get-vendor-products
  [vendor-id]
  (map DBProduct->Product-coercer
       (query [(str "select * from products as p
                              left join product_items as pi ON pi.product_id = p.id
                              where
                              (pi.quantity IS NULL OR (pi.quantity IS NOT NULL AND pi.quantity > 0))
                              and p.deleted_at is null
                              and p.type != 'ProductUnion'
                              and p.price_kopeks is not null
                              and p.is_published = true
                              and
                              (
                                p.quantity IS NULL OR (p.quantity IS NOT NULL AND p.quantity > 0)
                              )
                              and p.has_ordering_goods = true
                              and p.vendor_id = " vendor-id)])))

(defn get-vendor-property
  [vendor-id property-id]
  (DBProperty->Property-coercer
    (first (query [(str "select * from vendor_properties
                                  where deleted_at is null
                                  and id = " property-id "
                                  and vendor_id = " vendor-id)]))))

(defn get-vendor-dictionary-entity
  [vendor-id entity-id]
  (DBDictionaryEntity->DictionaryEntity-coercer
    (first (query [(str "select * from dictionary_entities
                                  where deleted_at is null
                                  and id = " entity-id "
                                  and vendor_id = " vendor-id)]))))
