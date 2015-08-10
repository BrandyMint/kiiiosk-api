(defproject kiosk-open-api "0.1.0-SNAPSHOT"
  :description "Kiosk public API"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"] ; required due to bug in lein-ring
                 [org.clojure/tools.trace "0.7.5"]
                 [metosin/compojure-api "0.22.0"]]
  :ring {:handler kiosk-open-api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [org.clojure/java.jdbc "0.3.2"]
                                  [postgresql "9.3-1102.jdbc41"]
                                  [clj-postgresql "0.4.0"]
                                  [clojurewerkz/elastisch "2.2.0-beta4"]
                                  [cheshire "5.3.1"]
                                  [ring-mock "0.1.5"]]
                   :plugins [[lein-ring "0.9.6"]]}})
