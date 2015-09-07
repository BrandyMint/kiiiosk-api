(ns workers.yandex-market
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.basic :as lb]
            [langohr.consumers :as lcons]
            [generate-yml.core :refer [generate-yml]]
            [config]))

(def ^{:const true} default-exchange-name "")
(def ^{:const true} qname "yandex_market_queue")

(defn notify-generation-start
  [vendor-id output-path]
  (println
    (format "[consumer] Generating YML-catalog for vendor with ID %d into %s directory has been started"
            vendor-id
            output-path)))

(defn notify-generation-finish
  [vendor-id]
  (println
    (format "[consumer] Generating YML-catalog for vendor with ID %d has been finished"
            vendor-id)))

(defn handle-generate-task
  [ch metadata ^bytes payload]
  (let [vendor-id (Long. (String. payload "UTF-8"))]
    (future
      (notify-generation-start vendor-id config/yandex-yml-output-path)
      (generate-yml vendor-id config/yandex-yml-output-path)
      (notify-generation-finish vendor-id))))

(defn -main
  [& args]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lq/declare ch qname {:durable true :auto-delete false})
      (lb/qos ch 1)
      (println " [*] Waiting for messages. To exit press CTRL+C")
      (lcons/blocking-subscribe ch qname handle-generate-task {:auto-ack true}))))

;; Publisher example

; (def ^{:const true} default-exchange-name "")
; (def ^{:const true} qname "yandex_market_queue")

; (defn -main
;   [vendor-id]
;   (with-open [conn (lc/connect)]
;     (let [ch (lch/open conn)]
;       (lb/publish ch default-exchange-name qname vendor-id)
;       (println " [x] Sent " vendor-id))))

; (-main "5")