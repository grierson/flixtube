(ns video-stream.routes
  (:require
    [aero.core :as aero]
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]
    [clj-http.client :as client]
    [clojure.java.io :as io]))

(defn app []
  (ring/ring-handler
    (ring/router
      [["/video" {:get {:handler (fn [_]
                                   (let [host "http://localhost:"
                                         port 4000
                                         response (client/get (str host port "/video"))]
                                     {:status  200
                                      :headers {"Content-Type" contentType}
                                      :body    (io/input-stream (.toByteArray response))}))}}]]
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
