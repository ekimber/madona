(ns madona.core)
(require '[persister.core :as p])
(require '[org.httpkit.server :as http])
(require '[clj-time.core :as t])
(require '[clj-time.format :as f])
(require '[clj-time.local :as l])

(def iso-date (f/formatter "hh:mm:ss"))

;REFS
(def msgs (ref []))

;TRANSACTIONS
(defn write [msg time]
  (println (str "recv: " msg " " time))
  (ref-set msgs (conj @msgs [msg time]))
)

;; Core

;; Current time as string
(defn now-str [] (f/unparse iso-date (l/local-now)))

;; http-kit handler
(defn handler [request]
  (http/with-channel request channel
    (http/on-close channel (fn [status] (println "channel closed: " status)))
    (http/on-receive channel
      (fn [data] ;; echo it back
        (http/send! channel data)
        (p/apply-transaction-and-block madona.core/write "foo" (now-str))
      )
    )
  )
)

(defn -main 
  "Main function" 
  [& args]
  (println "Madona!")
  (p/init-db)
  (http/run-server handler {:port 9090})
  (println (str "Server started. listen at 0.0.0.0@9090"))
  (println msgs)
  (shutdown-agents)
)

