(ns history.main
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [juxt.clip.core :as clip])
  (:import [java.net Socket SocketException])
  (:gen-class))

(defn wait-for-port
  "Waits for TCP connection to be available on host and port. Options map
  supports `:timeout` and `:pause`. If `:timeout` is provided and reached,
  `:default`'s value (if any) is returned. The `:pause` option determines
  the time waited between retries."
  ([host port]
   (wait-for-port host port nil))
  ([^String host ^long port {:keys [:default :timeout :pause] :as opts}]
   (let [opts (merge {:host host
                      :port port}
                     opts)
         t0 (System/currentTimeMillis)]
     (loop []
       (let [v (try (.close (Socket. host port))
                    (- (System/currentTimeMillis) t0)
                    (catch Exception _e
                      (let [took (- (System/currentTimeMillis) t0)]
                        (if (and timeout (>= took timeout))
                          :wait-for-port.impl/timed-out
                          :wait-for-port.impl/try-again))))]
         (cond (identical? :wait-for-port.impl/try-again v)
               (do (Thread/sleep (or pause 100))
                   (prn "try again")
                   (recur))
               (identical? :wait-for-port.impl/timed-out v)
               default
               :else
               (assoc opts :took v)))))))

(defn -main [& args]
  (wait-for-port "localhost" 5672)
  (let [system-config (aero/read-config (io/resource "config.edn"))]
    (clip/start system-config)))
