(ns commands.yandex-market
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [config :refer [default-exchange-name ymarket-qname]]))

(defn -main
  [vendor-id]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lb/publish ch default-exchange-name ymarket-qname vendor-id)
      (-> (format "[x] Sent command for generating Yandex.Market catalog with vendor ID %s"
                  vendor-id)
          println))))