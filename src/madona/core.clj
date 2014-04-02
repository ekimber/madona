(ns madona.core)
(require '[shiroko.core :as s])
(use 'lamina.core 'aleph.http)
(require '[clj-time.core :as t])
(require '[clj-time.format :as f])
(require '[clj-time.local :as l])

(def iso-date (f/formatter "hh:mm:ss"))

;REFS
(def msgs (ref []))

(defn write [name msg time]
  (ref-set msgs (conj @msgs [name msg time]))
)

;; Core

;; Current time as string
(defn now-str [] (f/unparse iso-date (l/local-now)))

(defn persist-message [[name msg]]
  (s/apply-transaction write name msg (now-str))
)



(def broadcast-channel (permanent-channel))
(def persistence-channel (permanent-channel))
(receive-all persistence-channel persist-message)

(defn chat-handler [ch handshake]
  (receive ch
    (fn [name]
      (siphon (map* #(str name ": " %) ch) broadcast-channel)
      (siphon broadcast-channel ch)
      (siphon (map* #(vector name %) ch) persistence-channel)
    )
  )
)


(defn -main 
  "Main function" 
  [& args]
  (s/init-db :ref-list [msgs])
  (start-http-server chat-handler {:port 9090 :websocket true})
  (println (str "Server started. listen at 0.0.0.0@9090"))
)

