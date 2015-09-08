(ns kiosk-open-api.handler
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer :all]
            [clj-bugsnag.ring :refer [wrap-bugsnag]]
            [generate-yml.core :refer [generate-yml]]
            [kiosk-open-api.schemas :refer [ProductCard]]
            [kiosk-open-api.utils :refer :all]
            [config :refer [ymarket-qname ymarket-yml-output-path]]))

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
      (let [output-path (ymarket-yml-output-path vendor-id)]
        (future (generate-yml vendor-id output-path))
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