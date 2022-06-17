(ns history.routes
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [reitit.ring :as ring]
   [reitit.coercion.malli :as mcoercion]
   [reitit.ring.coercion :as rrc]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m])
  (:import (com.rabbitmq.client ConnectionFactory DeliverCallback CancelCallback)))

(def RABBIT_URI (System/getenv "RABBIT"))

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
  (do
    (prn "try consume")
    (prn "channel" channel)
    (try
      (.basicConsume channel queue true cbfn ecbfn)
      (catch Exception e
        (prn "Error consume from channel")
        (prn e)))))

(comment
  (def queue "hello-world")
  (def channel (connect queue))

  (.basicPublish channel "" queue nil (.getBytes "this"))
  (.basicConsume channel queue cbfn ecbfn))

(defn app []
  (let [exchange "hello-world"
        {:keys [channel queueName]} (connect exchange)
        _ (consume channel queueName)]
    (ring/ring-handler
     (ring/router
      [["/viewed" {:post {:handler (fn [_]
                                     (do
                                       (prn "Something viewed")
                                       {:status 200
                                        :body "viewed"}))}}]
       ["/health" {:get {:handler (fn [_]
                                    {:status 200
                                     :body "world"})}}]]
      {:data       {:coercion mcoercion/coercion}
       :muuntaja   m/instance
       :middleware [parameters/parameters-middleware
                    muuntaja/format-negotiate-middleware
                    muuntaja/format-request-middleware
                    muuntaja/format-response-middleware
                    rrc/coerce-request-middleware
                    rrc/coerce-response-middleware]}))))

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
