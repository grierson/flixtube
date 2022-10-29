(ns history.routes
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io])
  (:import (com.rabbitmq.client
            ConnectionFactory
            DeliverCallback
            CancelCallback)))

(def RABBIT_URI (System/getenv "RABBIT"))
(def VIEWED_EXCHANGE (System/getenv "VIEWED_EXCHANGE"))

(defn connect [exchange]
  (try
    (let [factory (ConnectionFactory.)
          _ (.setUri factory RABBIT_URI)
          connection (.newConnection factory)
          channel (.createChannel connection)
          _  (.exchangeDeclare channel exchange "fanout")
          queueName (.getQueue (.queueDeclare channel))
          _ (.queueBind channel queueName exchange "")]
      {:channel channel
       :queueName queueName})
    (catch Exception e
      (prn "Failed to connect to rabbit")
      (prn e))))

(def cbfn
  (reify DeliverCallback
    (handle [_ _ delivery]
      (do
        (prn "call back consumer")
        (prn (String. (.getBody delivery)))))))

(def ecbfn
  (reify CancelCallback
    (handle [_ error]
      (prn error))))

(defn consume [channel queue]
  (try
    (.basicConsume channel queue true cbfn ecbfn)
    (catch Exception e
      (prn "Error consume from channel")
      (prn e))))

(defn app []
  (let [{:keys [channel queueName]} (connect VIEWED_EXCHANGE)]
    (consume channel queueName)))

(comment
  (do
    (require
     '[juxt.clip.repl :refer [start stop set-init!]])
    (def system-config (aero/read-config (io/resource "config.edn")))
    (set-init! (constantly system-config))
    (start))

  (do
    (stop)
    (start))

  (+ 1 1))
