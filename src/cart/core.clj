(ns cart.core
  (:require [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli]
            [malli.core :as s]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.exception :as exception]
            [expound.alpha :as expound]))

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

(def catalog {1 {:id 1
                 :name "apples"}
              2 {:id 2
                 :name "oranges"}})

(defn get-catalog-items [ids]
  (vals (select-keys catalog ids)))

(defn coercion-error-handler [status]
  (let [printer (expound/custom-printer {:theme :figwheel-theme, :print-specs? false})
        handler (exception/create-coercion-handler status)]
    (fn [exception request]
      (printer (-> exception ex-data :problems))
      (handler exception request))))

(def app
  (ring/ring-handler
    (ring/router
      ["/cart"
       ["/:userid"
        ["" {:get {:parameters {:path [:map [:userid :int]]}
                   :handler    (fn [{{{:keys [userid]} :path} :parameters}]
                                 {:status 200
                                  :body   (get db userid)})}}]
        ["/items" {:post   {:parameters {:path [:map [:userid :int]]
                                         :body [:vector :int]}
                            :handler    (fn [{{{:keys [userid]} :path
                                               productIds       :body} :parameters}]
                                          (let [cart (get-cart db userid)
                                                products (get-catalog-items productIds)
                                                new-cart (add-items cart products)]
                                            {:status 200
                                             :body   new-cart}))}
                   :delete {:parameters {:path [:map [:userid :int]]
                                         :body [:vector :int]}
                            :handler    (fn [{{{:keys [userid]} :path
                                               productIds       :body} :parameters}]
                                          (let [cart (get-cart db userid)]
                                            {:status 200
                                             :body (remove-items cart productIds)}))}}]]]
      {:data {:coercion   reitit.coercion.malli/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware ;; Query string
                           muuntaja/format-negotiate-middleware ;; Content-Type + Accept headers
                           muuntaja/format-response-middleware ;;
                           muuntaja/format-request-middleware
                           (exception/create-exception-middleware
                             (merge
                               exception/default-handlers
                               {:reitit.coercion/request-coercion (coercion-error-handler 400)
                                :reitit.coercion/response-coercion (coercion-error-handler 500)}))
                           rrc/coerce-response-middleware ;; coerce for request + response
                           rrc/coerce-request-middleware]}})))



(defn run [port]
  (jetty/run-jetty #'app {:port port :join? false}))

(comment
  (def dev-instance (run 3000))
  (.stop dev-instance))