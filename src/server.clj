(ns server
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]))

(def app
  (ring/ring-handler
    (ring/router
      ["/api"
       ["/math" {:get {:handler (fn [_]
                                  {:status 200
                                   :body   "hello"})}}]]
      {})))


(defn start []
  (jetty/run-jetty #'app {:port 3001})
  (println "server running in port 3000"))

(comment
  (start))