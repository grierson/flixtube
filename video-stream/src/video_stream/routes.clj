(ns video_stream.routes
  (:require
   [aero.core :as aero]
   [reitit.ring :as ring]
   [reitit.coercion.malli :as mcoercion]
   [reitit.ring.coercion :as rrc]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m]
   [clj-http.client :as client]
   [monger.core :as mg]
   [monger.collection :as mc]
   [clojure.java.io :as io])
  (:import (org.bson.types ObjectId)
           (com.rabbitmq.client ConnectionFactory)))

(def VIDEO-STORAGE-HOST (System/getenv "VIDEO_STORAGE_HOST"))
(def VIDEO-STORAGE-PORT (System/getenv "VIDEO_STORAGE_PORT"))
(def VIDEO-STORAGE-URL
  (str "http://" VIDEO-STORAGE-HOST ":" VIDEO-STORAGE-PORT "/video?path="))

(def DB-HOST (System/getenv "DBHOST"))
(def DB-COLLECTION (System/getenv "DBCOLLECTION"))

(def RABBIT_URI (System/getenv "RABBIT"))
(def VIEWED_EXCHANGE (System/getenv "VIEWED_EXCHANGE"))

(defn create-channel
  []
  (try
    (let [factory (ConnectionFactory.)
          _ (.setUri factory RABBIT_URI)
          connection (.newConnection factory)
          channel (.createChannel connection)
          _  (.exchangeDeclare channel VIEWED_EXCHANGE "fanout")]
      channel)
    (catch Exception e
      (prn "Failed to connect to rabbit")
      (prn e))))

(defn get-video-path
  [id]
  (let  [{:keys [db]} (mg/connect-via-uri DB-HOST)
         video-id (ObjectId. id)
         {:keys [videoPath]} (mc/find-one-as-map
                              db
                              DB-COLLECTION
                              {:_id video-id})]
    videoPath))

(defn viewed
  [channel video-path]
  (.basicPublish channel VIEWED_EXCHANGE "" nil (.getBytes video-path)))

(defn app
  []
  (let [channel (create-channel)]
    (ring/ring-handler
     (ring/router
      [["/health" {:get {:handler (fn [_]
                                    {:status 200
                                     :body "healthy"})}}]
       ["/video"
        {:get
         {:parameters {:query {:id string?}}
          :handler
          (fn [{{{:keys [id]} :query} :parameters}]
            (let [video-path (get-video-path id)
                  video-url (str VIDEO-STORAGE-URL video-path)
                  response (client/get video-url {:as :stream})]
              (do
                (viewed channel video-path)
                {:status  200
                 :headers {"Content-Type" "video/mp4"}
                 :body    (io/input-stream (:body response))})))}}]]
      {:data       {:coercion mcoercion/coercion
                    :muuntaja   m/instance
                    :middleware [parameters/parameters-middleware
                                 muuntaja/format-negotiate-middleware
                                 muuntaja/format-request-middleware
                                 muuntaja/format-response-middleware
                                 rrc/coerce-request-middleware
                                 rrc/coerce-response-middleware]}}))))

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


