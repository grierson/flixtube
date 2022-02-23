(ns catalog.routes
  (:require
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [clojure.string :as str]
    [muuntaja.core :as m]))

(defn app []
  (ring/ring-handler
    (ring/router
      [["/products" {:get {:parameters {:query [:map [:productIds [:sequential {:decode/string (fn [s] (str/split s #","))} :int]]]}
                           :handler    (fn [{{productIds :query} :parameters}]
                                         {:status 200
                                          :body   (:productIds productIds)})}}]]
      {:data {:coercion   mcoercion/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-request-middleware
                           muuntaja/format-response-middleware
                           rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})))
