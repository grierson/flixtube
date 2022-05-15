(ns history.main
  (:require
   [aero.core :as aero]
   [hawk.core :as hawk]
   [juxt.clip.repl :refer [start stop set-init!]]
   [clojure.java.io :as io])
  (:gen-class))

(def system-config (aero/read-config (io/resource "config.edn")))

(defn -main [& _]
  (println "RUNNING")
  (set-init! (constantly system-config))
  (start)
  (try
    (hawk/watch! [{:paths ["src/"]
                   :handler (fn [_]
                              (stop)
                              (start))}])
    (catch Exception e
      (println e))))
