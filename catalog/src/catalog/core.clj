(ns catalog.core
  (:require
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]))


(def app
  (ring/ring-handler
    (ring/router
      [["/product/:z" {:name ::plus
                       :post {:parameters {:query [:map [:x int?]]
                                           :body  [:map [:y int?]]
                                           :path  [:map [:z int?]]}
                              :responses  {200 {:body [:map [:total pos-int?]]}}
                              :handler    (fn [{:keys [parameters] :as request}]
                                            (clojure.pprint/pprint request)
                                            (prn (-> parameters :query :x))
                                            (prn (-> parameters :body :y))
                                            (prn (-> parameters :path :z))
                                            (let [total (+ (-> parameters :query :x)
                                                           (-> parameters :body :y)
                                                           (-> parameters :path :z))]
                                              {:status 200
                                               :body   {:total total}}))}}]]
      {:data {:coercion   mcoercion/coercion
              :middleware [rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})))

;; Coerce =

(comment
  (app {:request-method :post
        :uri            "/api/plus/3"
        :query-params   {"x" "1"}
        :body-params    {:y 2}}))