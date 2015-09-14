(ns libs.deliveries)

(defn has-deliveries?
  [deliveries]
  (-> deliveries count pos?))

(defn has-pickup?
  [deliveries]
  (some #(= (:delivery-agent-type %) "OrderDeliveryPickup")
        deliveries))