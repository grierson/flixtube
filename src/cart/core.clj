(ns cart.core
  (:require [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli]
            [malli.core :as s]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.exception :as exception]))

(defn add-cart [db {:keys [userid] :as cart}]
  (assoc db userid cart))

(defn get-cart [db userid]
  (get db userid))

(def db {42 {:userid 42
             :items  [{:id    1
                       :name  "t-shirt"
                       :price {:currency "eur"
                               :amount   40}}]}})



(defrecord Money [currency amount])
(defrecord Cart [userid items])
(defrecord CartItem [id name description price])

(defn make-cart [id]
  (->Cart id []))

(defn add-items [cart items]
  (update cart :items into items))

(defn remove-items [cart itemIds]
  (update cart :items #(remove (fn [{:keys [id]}] ((set itemIds) id)) %)))

(def app
  (ring/ring-handler
    (ring/router
      ["/cart"
       ["/:userid"
        ["" {:get {:parameters {:path [:map [:userid :int]]}
                   :handler    (fn [{{{:keys [userid]} :path} :parameters}]
                                 {:status 200
                                  :body   (get db userid)})}}]
        ["/items" {:post {:parameters {:path [:map [:userid :int]]
                                       :body [:vector :int]}
                          :handler    (fn [{{{:keys [userid]} :path
                                             items            :body} :parameters}]
                                        {:status 200
                                         :body   {:userid userid
                                                  :items  items}})}}]]]
      {:data {:coercion   reitit.coercion.malli/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-response-middleware
                           exception/exception-middleware
                           muuntaja/format-request-middleware
                           rrc/coerce-response-middleware
                           rrc/coerce-request-middleware]}})))



(defn run [port]
  (jetty/run-jetty #'app {:port port :join? false}))

(comment
  (def dev-instance (run 3000))
  (.stop dev-instance))