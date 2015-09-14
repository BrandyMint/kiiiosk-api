(ns libs.queries
  (:require [config :refer [query]]))

(defn get-vendor
  [vendor-id]
  (first (query [(str "select * from vendors where id = " vendor-id)])))

(defn get-vendor-categories
  [vendor-id]
  (query [(str "select * from categories
                         where deleted_at is null
                         and vendor_id = " vendor-id)]))

(defn get-vendor-deliveries
  [vendor-id]
  (query [(str "select * from vendor_deliveries
                         where deleted_at is null
                         and vendor_id = " vendor-id)]))

(defn get-vendor-not-pickup-deliveries
  [vendor-id]
  (query [(str "select * from vendor_deliveries
                         where deleted_at is null
                         and delivery_agent_type != 'OrderDeliveryPickup'
                         and vendor_id = " vendor-id)]))

(defn get-vendor-offers
  [vendor-id]
  (query [(str "select * from products
                         where deleted_at is null
                         and type != 'ProductUnion'
                         and price_kopeks is not null
                         and vendor_id = " vendor-id)]))

(defn get-vendor-property
  [vendor-id property-id]
  (first (query [(str "select * from vendor_properties
                                where deleted_at is null
                                and id = " property-id "
                                and vendor_id = " vendor-id)])))

(defn get-vendor-dictionary-entity
  [vendor-id entity-id]
  (first (query [(str "select * from dictionary_entities
                                where deleted_at is null
                                and id = " entity-id "
                                and vendor_id = " vendor-id)])))