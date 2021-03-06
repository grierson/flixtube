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

(def HISORY-HOST (System/getenv "HISTORY_HOST"))
(def HISORY-PORT (System/getenv "HISTORY_PORT"))
(def VIDEO-STORAGE-HOST (System/getenv "VIDEO_STORAGE_HOST"))
(def VIDEO-STORAGE-PORT (System/getenv "VIDEO_STORAGE_PORT"))
(def DB-HOST (System/getenv "DB_HOST"))
(def DB-COLLECTION "videos")
(def VIDEO-STORAGE-URL (str "http://" VIDEO-STORAGE-HOST ":" VIDEO-STORAGE-PORT "/video?path="))
(def RABBIT_URI (System/getenv "RABBIT"))

(defn connect [exchange]
  (try
    (let [factory (ConnectionFactory.)
          _ (.setUri factory RABBIT_URI)
          connection (.newConnection factory)
          channel (.createChannel connection)
          _  (.exchangeDeclare channel exchange "fanout")]
      channel)
    (catch Exception e
      (prn "Failed to connect to rabbit")
      (prn e))))

(defn get-video [id]
  (let [{:keys [db]} (mg/connect-via-uri DB-HOST)
        video-id (ObjectId. id)
        _video (mc/find-one-as-map db DB-COLLECTION {:_id video-id})]
    {:videoPath "bunny_video.mp4"}))

(defn sendVideoMessage
  ([path]
   (client/post
    (str "http://" HISORY-HOST ":" HISORY-PORT "/viewed?video=" path)
    {}))
  ([channel exchange path]
   (.basicPublish channel exchange "" nil (.getBytes path))))

(defn app []
  (let [exchange "hello-world"
        channel (connect exchange)]
    (ring/ring-handler
     (ring/router
      [["/video"
        {:get
         {:parameters {:query {:id string?}}
          :handler
          (fn [{{{:keys [_]} :query} :parameters}]
            (let [video-path "bunny_video.mp4"
                  url (str VIDEO-STORAGE-URL video-path)
                  response (client/get url {:as :stream})]
              (do
                (sendVideoMessage channel exchange video-path)
                {:status  200
                 :headers {"Content-Type" "video/mp4"}
                 :body    (io/input-stream (:body response))})))}}]]
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


