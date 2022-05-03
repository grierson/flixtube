(ns video-stream.main
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [juxt.clip.core :as clip])
  (:gen-class))

(defn -main [& args]
  (let [system-config (aero/read-config (io/resource "config.edn"))]
    (clip/start system-config)))
