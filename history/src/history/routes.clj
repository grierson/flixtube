(ns history.routes
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [reitit.ring :as ring]
    [reitit.coercion.malli :as mcoercion]
    [reitit.ring.coercion :as rrc]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]
    [langohr.core :as rmq]
    [langohr.core :as rmq]
    [langohr.channel :as lch]
    [langohr.consumers :as lc]
    [langohr.queue :as lq]))

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type)))

(defn app []
  (let [conn (rmq/connect)
        channel (lch/open conn)
        queue "langohr.examples.hello-world"
        _ (lq/declare channel queue {:exclusive false :auto-delete true})
        _ (lc/subscribe channel queue message-handler)]
    (ring/ring-handler
      (ring/router
        [["/health" {:get {:handler (fn [_]
                                      {:status 200}
                                      :body "world")}}]]
        {:data       {:coercion mcoercion/coercion}
         :muuntaja   m/instance
         :middleware [parameters/parameters-middleware
                      muuntaja/format-negotiate-middleware
                      muuntaja/format-request-middleware
                      muuntaja/format-response-middleware
                      rrc/coerce-request-middleware
                      rrc/coerce-response-middleware]}))))

(comment
  (do
    (require
      '[juxt.clip.repl :refer [start stop set-init! system]])
    (def system-config (aero/read-config (io/resource "config.edn")))
    (set-init! (constantly system-config))
    (start))

  (do
    (stop)
    (start))

  (+ 1 1))
