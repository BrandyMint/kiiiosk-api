(ns kiosk-open-api.handler
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [kiosk-open-api.schemas :refer [ProductCard]]
            [kiosk-open-api.utils :refer :all]
            ))


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
