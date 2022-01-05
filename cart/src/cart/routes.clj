(ns cart.routes
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli :as mcoercion]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [clojure.java.io :as io]
            [cart.datastore :as data]
            [cart.domain :as domain]
            [cart.events :as events]
            [cart.catalog-client :as catalog-client]))

(defn app [cartStore eventStore]
  (ring/ring-handler
    (ring/router
      [["/cart"
        ["/:user-id"
         ["" {:get {:parameters {:path [:map [:user-id :int]]}
                    :handler    (fn [{{{:keys [user-id]} :path} :parameters}]
                                  (let [cart (data/fetch-by-id cartStore user-id)]
                                    {:status 200
                                     :body   cart}))}}]

         ["/items" {:post   {:parameters {:path [:map [:user-id :int]]
                                          :body [:vector :int]}
                             :handler    (fn [{{{:keys [user-id]} :path
                                                productIds        :body} :parameters}]
                                           (let [cart (data/fetch-by-id cartStore user-id)
                                                 products (catalog-client/get-products productIds)
                                                 new-cart (domain/add-items cart products)
                                                 _ (data/save cartStore new-cart)
                                                 _ (events/raise eventStore "ItemsAdded" new-cart)]
                                             {:status 200
                                              :body   new-cart}))}
                    :delete {:parameters {:path [:map [:user-id :int]]
                                          :body [:vector :int]}
                             :handler    (fn [{{{:keys [user-id]} :path
                                                productIds        :body} :parameters}]
                                           (let [cart (data/fetch-by-id cartStore user-id)
                                                 new-cart (domain/remove-items cart productIds)
                                                 _ (data/save cartStore new-cart)
                                                 _ (events/raise eventStore "ItemsRemoved" new-cart)]

                                             {:status 200
                                              :body   new-cart}))}}]]]
       ["/events" {:get {:parameters {:query [:map
                                              [:start {:optional? true
                                                       :default   1} :int]
                                              [:end {:optional? true
                                                     :default   Integer/MAX_VALUE} :int]]}
                         :handler    (fn [{{{:keys [start end]} :query} :parameters}]
                                       (let [events (events/get-events eventStore start end)]
                                         {:status 200
                                          :body   events}))}}]]
      {:data {:coercion   mcoercion/coercion
              :muuntaja   m/instance
              :middleware [muuntaja/format-negotiate-middleware ;; Content-Type + Accept headers
                           muuntaja/format-response-middleware
                           muuntaja/format-request-middleware
                           rrc/coerce-response-middleware
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