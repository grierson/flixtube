(ns history.routes
  (:require
   [reitit.ring :as ring]))

(defn app []
  (ring/ring-handler
   (ring/router
    [["/health" {:get {:handler (fn [_]
                                  {:status  200
                                   :body    "Hello"})}}]])))
