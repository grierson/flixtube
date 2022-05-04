(ns video_storage.routes
  (:require
    [aero.core :as aero]
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]
    [clojure.java.io :as io])
  (:import (com.azure.storage.blob BlobClientBuilder)
           (java.io ByteArrayOutputStream)))


(def connection-string (System/getenv "STORAGE_CONNECTION_STRING"))
(def container "videos")
(def blob "bunny_video.mp4")

(defn app []
  (ring/ring-handler
    (ring/router
      [["/video" {:get {:handler (fn [_]
                                   (let [client (-> (BlobClientBuilder.)
                                                    (.connectionString connection-string)
                                                    (.containerName container)
                                                    (.blobName blob)
                                                    (.buildClient))
                                         properties (.getProperties client)
                                         contentType (.getContentType properties)
                                         stream (ByteArrayOutputStream.)
                                         _ (.downloadStream client stream)]
                                     {:status  200
                                      :headers {"Content-Type" contentType}
                                      :body    (io/input-stream (.toByteArray stream))}))}}]]
      {:data {:coercion   mcoercion/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-request-middleware
                           muuntaja/format-response-middleware
                           rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})))

(comment
  (do
    (require
      '[juxt.clip.repl :refer [start stop set-init! system]])
    (def system-config (aero/read-config (io/resource "config.edn")))
    (set-init! (constantly system-config))
    (start))

  (do
    (stop)
    (start))

  (+ 1 1))
