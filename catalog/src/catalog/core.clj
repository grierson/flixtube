(ns catalog.core
  (:require [reitit.core :as r]))

(def router
  (r/router
    [["/api/ping" ::ping]
     ["/api/orders/:id" ::order]]))


(defn run [_]
  (println "Catalog"))
