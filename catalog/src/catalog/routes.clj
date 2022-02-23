(ns catalog.routes
  (:require
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.dev.pretty :as pretty]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]))

(def app
  (ring/ring-handler
    (ring/router
      [["/products" {:get {:parameters {:query [:map [:productIds [:sequential int?]]]}
                           :handler    (fn [{{productIds :query} :parameters}]
                                         {:status 200
                                          :body   (:productIds productIds)})}}]]
      {:data {:coercion   mcoercion/coercion
              :exception  pretty/exception
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           exception/exception-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-response-middleware
                           muuntaja/format-request-middleware
                           rrc/coerce-exceptions-middleware
                           rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})))
