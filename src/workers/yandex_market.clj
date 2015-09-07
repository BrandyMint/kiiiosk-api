(ns workers.yandex-market
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.basic :as lb]
            [langohr.consumers :as lcons]
            [generate-yml.core :refer [generate-yml]]
            [config :refer [ymarket-qname ymarket-yml-output-path]]))

(defn notify-generation-start
  [vendor-id output-path]
  (println
    (format "[start] Generate YML-catalog (vendorID %d, directory %s)" vendor-id output-path)))

(defn notify-generation-finish
  [vendor-id output-path]
  (println
    (format "[finish] Generate YML-catalog (vendorID %d, directory %s)" vendor-id output-path)))

(defn handle-generate-task
  [ch metadata ^bytes payload]
  (let [vendor-id (Long. (String. payload "UTF-8"))]
    (future
      (notify-generation-start vendor-id ymarket-yml-output-path)
      (generate-yml vendor-id ymarket-yml-output-path)
      (notify-generation-finish vendor-id ymarket-yml-output-path))))

(defn -main
  [& args]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lq/declare ch ymarket-qname {:durable true :auto-delete false})
      (lb/qos ch 1)
      (println " [*] Waiting for messages. To exit press CTRL+C")
      (lcons/blocking-subscribe ch ymarket-qname handle-generate-task {:auto-ack true}))))