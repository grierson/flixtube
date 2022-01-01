(ns cart.core
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli :as mcoercion]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [clojure.java.io :as io]
            [cart.datastore :as data]))

(defrecord Money [currency amount])
(defrecord Cart [userid items])
(defrecord CartItem [id name description price])

(defn make-cart [id]
  (->Cart id []))

(def catalog {1 {:id   1
                 :name "apples"}
              2 {:id   2
                 :name "oranges"}})

(defn get-catalog-items [ids]
  (vals (select-keys catalog ids)))

(defn app [db]
  (ring/ring-handler
    (ring/router
      ["/cart"
       ["/:userid"
        ["" {:get {:parameters {:path [:map [:userid :int]]}
                   :handler    (fn [{{{:keys [userid]} :path} :parameters}]
                                 (if-let [cart (data/fetch-by-id db userid)]
                                   {:status 200
                                    :body   cart}
                                   {:status 200
                                    :body   (data/add-cart db (make-cart userid))}))}}]
        ["/items" {:post   {:parameters {:path [:map [:userid :int]]
                                         :body [:vector :int]}
                            :handler    (fn [{{{:keys [userid]} :path
                                               productIds       :body} :parameters}]
                                          (let [products (get-catalog-items productIds)
                                                new-cart (data/add-items db userid products)]
                                            {:status 200
                                             :body   new-cart}))}
                   :delete {:parameters {:path [:map [:userid :int]]
                                         :body [:vector :int]}
                            :handler    (fn [{{{:keys [userid]} :path
                                               productIds       :body} :parameters}]
                                          (do
                                            (data/remove-items db userid productIds)
                                            {:status 200}))}}]]]
      {:data {:coercion   mcoercion/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware ;; Query string
                           muuntaja/format-negotiate-middleware ;; Content-Type + Accept headers
                           muuntaja/format-response-middleware ;;
                           muuntaja/format-request-middleware
                           rrc/coerce-response-middleware   ;; coerce for request + response
                           rrc/coerce-request-middleware]}})))


(comment
  (do
    (require
      '[juxt.clip.repl :refer [start stop set-init! system]])
    (def system-config (clojure.edn/read-string (slurp (io/resource "config.edn"))))
    (set-init! (constantly system-config))
    (start))

  (do
    (stop)
    (start)),)