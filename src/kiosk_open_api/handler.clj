(ns kiosk-open-api.handler
  (:require [clojure.tools.logging :as log]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clj-bugsnag.ring :refer [wrap-bugsnag]]
            [commands.torg-mail :as torg-mail]
            [commands.yandex-market :as yandex-market]))

(defn -main [& args])

;;
;; Routes
;;

(defapi app
  (swagger-ui)
  (swagger-docs {:info {:title "kiiiosk open api"}})

  (context* "/vendors" []
    :tags ["vendors"]

    (POST* "/:vendor-id/yandex-market" [vendor-id]
      :summary "Generate yandex-market YML-catalog"
      (accepted (yandex-market/start-generate vendor-id)))

    (POST* "/:vendor-id/torg-mail" [vendor-id]
      :summary "Generate torg-mail XML-catalog"
      (accepted (torg-mail/start-generate vendor-id)))))

(wrap-bugsnag
  app
  {:api-key "50af506d5ce22190e37b64ff72726576"
   ;; Defaults to "production"
   :environment "production"
   ;; Project namespace prefix, used to hide irrelevant stack trace elements
   :project-ns "kiosk-open-api"
   ;; A optional function to extract a user object from a ring request map
   ;; Used to count how many users are affected by a crash
   :user-from-request (constantly {:id "shall return a map"})})