(ns kiosk-open-api.handler
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer :all]
            [clj-bugsnag.ring :refer [wrap-bugsnag]]
            [generators.yandex-market.core :refer [generate]]
            [kiosk-open-api.schemas :refer [ProductCard]]
            [kiosk-open-api.utils :refer :all]
            [config :refer [ymarket-qname ymarket-output-path]]))

(defn -main [& args])

;;
;; Routes
;;

(defapi app
  (swagger-ui)
  (swagger-docs
    {:info {:title "kiiiosk open api"}})

  (GET* "/" []
    :no-doc true
    (ok "hello world"))

  (GET* "/ping" []
    :return {:response String}
    :summary "Ping test"
    (ok {:response "Ok. Pong.."}))

  (context* "/yandex-market" []

    (POST* "/:vendor-id" []
      :path-params [vendor-id :- Long]
      :summary "Makes YML-file"
      (let [output-path (ymarket-output-path vendor-id)]
        (future
          (try (generate vendor-id output-path)
            (catch Exception e (log/error e))))
        (ok "Generation started"))))

  (context* "/products" []
    :tags ["products"]

    (GET* "/:id" []
      :return ProductCard
      :path-params [id :- Long]
      :summary "Gets product"
      (ok (get-coerced-esd "product" ProductCard id)))))

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