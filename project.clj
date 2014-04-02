(defproject madona "1.0.0-SNAPSHOT"
  :description "Persistence Demo"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [shiroko "0.1.0-SNAPSHOT"]
                 [aleph "0.3.2"]
;                 [http-kit "2.1.16"]
                 [clj-time "0.6.0"]]
;  :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5010"]
;  :aliases { "debug" ["with-profile" "dev" "run"] }
  :main madona.core)
