(ns libs.deliveries)

(defn delivery-has-deliveries?
  [deliveries]
  (-> deliveries count pos?))

(defn delivery-has-pickup?
  [deliveries]
  (some #(= (:delivery-agent-type %) "OrderDeliveryPickup")
        deliveries))