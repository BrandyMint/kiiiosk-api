(ns workers.yandex-market
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.basic :as lb]
            [langohr.consumers :as lcons]
            [generators.yandex-market.core :refer [generate]]
            [config :refer [ymarket-qname ymarket-output-path]]))

(defn notify-generation-start
  [vendor-id output-path]
  (-> (format "[start] Generate Yandex.Market catalog (vendorID %d, directory %s)"
              vendor-id
              output-path)
      println))

(defn notify-generation-finish
  [vendor-id output-path]
  (-> (format "[finish] Generate Yandex.Market catalog (vendorID %d, directory %s)"
              vendor-id
              output-path)
      println))

(defn handle-generate-task
  [ch metadata ^bytes payload]
  (let [vendor-id (Long. (String. payload "UTF-8"))
        output-path (ymarket-output-path vendor-id)]
    (future
      (notify-generation-start vendor-id output-path)
      (generate vendor-id output-path)
      (notify-generation-finish vendor-id output-path))))

(defn -main
  [& args]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lq/declare ch ymarket-qname {:durable true :auto-delete false})
      (lb/qos ch 1)
      (println " [*] Waiting for messages. To exit press CTRL+C")
      (lcons/blocking-subscribe ch ymarket-qname handle-generate-task {:auto-ack true}))))