(defproject kiosk-open-api "0.1.0-SNAPSHOT"
  :description "Kiosk public API"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [clj-time "0.9.0"] ; required due to bug in lein-ring
                 [clj-http "1.0.1"] ; https://github.com/dakrone/clj-http/issues/238
                 [clj-bugsnag "0.2.3"]
                 [clj-postgresql "0.4.0"]
                 [clojurewerkz/elastisch "2.2.0-beta4"]
                 [hiccup "1.0.5"]
                 [prismatic/schema "0.4.4"]
                 [metosin/compojure-api "0.23.1"]
                 [environ "1.0.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :ring {:handler kiosk-open-api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev [:development {:dependencies [[javax.servlet/servlet-api "2.5"]
                                                [org.clojure/tools.trace "0.7.5"]
                                                [com.novemberain/langohr "3.3.0"]
                                                [cheshire "5.3.1"]
                                                [ring-mock "0.1.5"]
                                                [ring/ring-jetty-adapter "1.4.0"]]
                                 :plugins [[lein-ring "0.9.6"]]}]
             :repl {:dependencies [[org.clojure/tools.nrepl "0.2.10"]
                                   [clojure-complete "0.2.4"]]}})