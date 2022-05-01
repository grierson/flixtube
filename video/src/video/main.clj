(ns video.main
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [juxt.clip.core :as clip])
  (:gen-class))

(defn -main [& args]
  (let [system-config (edn/read-string (slurp (io/resource "config.edn")))]
    (clip/start system-config)))
