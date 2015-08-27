(defproject kiosk-open-api "0.1.0-SNAPSHOT"
  :description "Kiosk public API"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-time "0.9.0"] ; required due to bug in lein-ring
                 [clj-http "1.0.1"] ; https://github.com/dakrone/clj-http/issues/238
                 [clj-bugsnag "0.2.3"]
                 [clojurewerkz/elastisch "2.2.0-beta4"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [metosin/compojure-api "0.22.0"]]
  :ring {:handler kiosk-open-api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [org.clojure/java.jdbc "0.4.1"]
                                  [org.clojure/tools.trace "0.7.5"]
                                  [hiccup "1.0.5"]
                                  [postgresql "9.3-1102.jdbc41"]
                                  [clj-postgresql "0.4.0"]
                                  [clojurewerkz/elastisch "2.2.0-beta4"]
                                  [cheshire "5.3.1"]
                                  [ring-mock "0.1.5"]]
                   :plugins [[lein-ring "0.9.6"]]}})
