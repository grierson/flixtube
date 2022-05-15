(ns history.routes
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]))

(defn app []
  (ring/ring-handler
   (ring/router
    [["/health" {:get {:handler (fn [_]
                                  {:status  200
                                   :body    "world"})}}]]
    {:data       {:coercion mcoercion/coercion}
     :muuntaja   m/instance
     :middleware [parameters/parameters-middleware
                  muuntaja/format-negotiate-middleware
                  muuntaja/format-request-middleware
                  muuntaja/format-response-middleware
                  rrc/coerce-request-middleware
                  rrc/coerce-response-middleware]})))

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
