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

(defn connect [queue]
  (try
    (let [_ (prn "Before new connection")
          factory (ConnectionFactory.)
          _ (prn "Before set host")
          new-factory (.setUri factory RABBIT_URI)
          _ (prn new-factory)
          _ (prn factory)
          _ (prn "Before new connection")
          connection (.newConnection factory)
          _ (prn "Before created channel")
          channel (.createChannel connection)
          _ (prn "Before declare queue")
          _  (.queueDeclare channel queue false false false nil)
          _ (prn "Connection complete")]
      channel)
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
      (.basicConsume channel queue cbfn ecbfn)
      (catch Exception e
        (prn "Error consume from channel")
        (prn e)))))

(comment
  (def queue "hello-world")
  (def channel (connect queue))

  (.basicPublish channel "" queue nil (.getBytes "this"))
  (.basicConsume channel queue cbfn ecbfn))

(defn app []
  (let [queue "hello-world"
        channel (connect queue)
        _ (consume channel queue)]
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
