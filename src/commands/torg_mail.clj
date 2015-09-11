(ns commands.torg-mail
  (:require [langohr.core :as lc]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [config :refer [default-exchange-name tmail-qname]]))

(defn -main
  [vendor-id]
  (with-open [conn (lc/connect)]
    (let [ch (lch/open conn)]
      (lb/publish ch default-exchange-name tmail-qname vendor-id)
      (-> (format "[x] Sent command for generating Torg.Mail catalog with vendor ID %s"
                  vendor-id)
          println))))