(ns video.routes
  (:require
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]
    [clojure.java.io :as io]))

(defn app []
  (ring/ring-handler
    (ring/router
      [["/" {:get {:handler (fn [_]
                              {:status 200
                               :body   "hello"})}}]
       ["/video" {:get {:handler (fn [_]
                                   (let [video (io/resource "bunny_video.mp4")]
                                     {:status  200
                                      :headers {"Content-Type" "video/mp4"}
                                      :body    (io/input-stream video)}))}}]]
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
    (def system-config (clojure.edn/read-string (slurp (io/resource "config.edn"))))
    (set-init! (constantly system-config))
    (start))

  (do
    (stop)
    (start))

  (+ 1 1))
