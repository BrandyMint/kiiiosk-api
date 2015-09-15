(ns workers.yandex-market
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.basic :as lb]
            [langohr.consumers :as lcons]
            [clojure.tools.logging :as log]
            [generators.yandex-market.core :refer [generate]]
            [config :refer [ymarket-qname ymarket-output-path]]))

(defn handle-generate-task
  [ch metadata ^bytes payload]
  (let [vendor-id (Long. (String. payload "UTF-8"))
        output-path (ymarket-output-path vendor-id)]
    (future
      (try (generate vendor-id output-path)
        (catch Exception e (log/error e))))))

(defn -main
  [& args]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lq/declare ch ymarket-qname {:durable true :auto-delete false})
      (lb/qos ch 1)
      (log/debug "[*] Waiting for messages. To exit press CTRL+C")
      (lcons/blocking-subscribe ch ymarket-qname handle-generate-task {:auto-ack true}))))