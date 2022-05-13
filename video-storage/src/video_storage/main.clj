(ns video_storage.main
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [juxt.clip.core :as clip])
  (:gen-class))

(defn -main [& _]
  (let [system-config (aero/read-config (io/resource "config.edn"))]
    (clip/start system-config)))
