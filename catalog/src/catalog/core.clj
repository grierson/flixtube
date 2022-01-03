(ns catalog.core
  (:require
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]))


(def app
  (ring/ring-handler
    (ring/router
      [["/products" {:get {:parameters {:query [:map [:productIds [:sequential int?]]]}
                           :handler    (fn [{{productIds :query} :parameters}]
                                         {:status 200
                                          :body   productIds})}}]]
      {:data {:coercion   mcoercion/coercion
              :middleware [rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})))

;; Coerce =

(comment
  (app {:request-method :get
        :uri            "/products"
        :query-params   {"productIds" [1 2]}}))