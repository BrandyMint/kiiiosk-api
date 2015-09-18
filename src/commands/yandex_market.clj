(ns commands.yandex-market
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [clojure.tools.logging :as log]
            [config :refer [default-exchange-name ymarket-qname]]))

(defn notify-command-sent
  [vendor-id]
  (-> (format "[x] Sent command for generating Yandex.Market catalog with vendor ID %s"
              vendor-id)
      log/info))

(defn start-generate
  [vendor-id]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lb/publish ch default-exchange-name ymarket-qname vendor-id)
      (notify-command-sent vendor-id))))

(defn -main
  [vendor-id]
  (start-generate vendor-id))