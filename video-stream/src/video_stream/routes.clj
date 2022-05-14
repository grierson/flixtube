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
   [taoensso.timbre :as log]
   [clojure.java.io :as io])
  (:import org.bson.types.ObjectId))

(def VIDEO-STORAGE-HOST (System/getenv "VIDEO_STORAGE_HOST"))
(def VIDEO-STORAGE-PORT (System/getenv "VIDEO_STORAGE_PORT"))

(def DB-HOST (System/getenv "DB_HOST"))
(def DB-PORT (Integer/parseInt (System/getenv "DB_PORT")))
(def DB-NAME (System/getenv "DB_NAME"))

;; (def DB-HOST "localhost")
;; (def DB-PORT (Integer/parseInt "4000"))
;; (def DB-NAME "video-streaming")

(def DB-COLLECTION "videos")

(defn get-video [id]
  (let [_ (log/debug ">>> START DATA")
        _ (log/debug id)
        _ (log/debug DB-HOST)
        _ (log/debug DB-PORT)
        _ (log/debug ">>> END DATA")
        conn (mg/connect)
        _ (log/debug ">>>> START Connection")
        _ (log/debug conn)
        _ (log/debug ">>>> END Connection")
        _ (log/debug ">>>> START Get Db")
        db (mg/get-db conn DB-NAME)
        _ (log/debug db)
        _ (log/debug ">>>> END Get Db")
        _ (log/debug ">>>> START Get Id")
        video-id (ObjectId. id)
        _ (log/debug video-id)
        _ (log/debug ">>>> END Get Id")
        _ (log/debug ">>>> START Video")
        video (mc/find-one-as-map db DB-COLLECTION {:_id video-id})
        _ (log/debug video)
        _ (log/debug ">>>> End Video")]
    video))

;; (get-video "5d9e690ad76fe06a3d7ae416")

(defn app []
  (ring/ring-handler
   (ring/router
    [["/video" {:get {:parameters {:query {:id string?}}
                      :handler (fn [{{{:keys [id]} :query} :parameters}]
                                 (let [video (get-video "5d9e690ad76fe06a3d7ae416")
                                       video-path (:videoPath video)
                                       url (str "http://" VIDEO-STORAGE-HOST ":" VIDEO-STORAGE-PORT "/video?path=" video-path)
                                       response (client/get url {:as :stream})]
                                   {:status  200
                                    :headers {"Content-Type" "video/mp4"}
                                    :body    (io/input-stream (:body response))}))}}]
     {:data       {:coercion mcoercion/coercion}
      :muuntaja   m/instance
      :middleware [parameters/parameters-middleware
                   muuntaja/format-negotiate-middleware
                   muuntaja/format-request-middleware
                   muuntaja/format-response-middleware
                   rrc/coerce-request-middleware
                   rrc/coerce-response-middleware]}])))

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


