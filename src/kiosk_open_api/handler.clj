(ns kiosk-open-api.handler
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [kiosk-open-api.schemas :refer [ProductCard]]
            [clj-bugsnag.core :as bugsnag]
            [clj-bugsnag.ring :as bugsnag.ring]
            [kiosk-open-api.utils :refer :all]))

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
        :return {:response java.lang.String}
        :summary "Ping test"
        (ok {:response "Ok. Pong.."})
        )

  (context* "/products" []
            :tags ["products"]

            (GET* "/:id" []
                  :return       ProductCard
                  :path-params [id :- Long]
                  :summary      "Gets product"
                  (ok 
                    (get-coerced-esd "product" ProductCard id)
                    )
                  )
            )
  )


(bugsnag.ring/wrap-bugsnag
  app
  {:api-key "50af506d5ce22190e37b64ff72726576"
   ;; Defaults to "production"
   :environment "production"
   ;; Project namespace prefix, used to hide irrelevant stack trace elements
   :project-ns "kiosk-open-api"
   ;; A optional function to extract a user object from a ring request map
   ;; Used to count how many users are affected by a crash
   :user-from-request (constantly {:id "shall return a map"})})


