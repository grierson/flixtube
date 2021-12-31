(ns cart.core
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli :as mcoercion]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.exception :as exception]
            [expound.alpha :as expound]
            [clojure.java.io :as io]
            [juxt.clip.core :as clip]
            [aero.core :refer [read-config]]
            [cart.datastore :as data])
  (:import [cart.datastore MemoryRepo]))

(defn add-cart [db {:keys [userid] :as cart}]
  (assoc db userid cart))

(defn get-cart [db userid]
  (get db userid))

(defrecord Money [currency amount])
(defrecord Cart [userid items])
(defrecord CartItem [id name description price])

(defn make-cart [id]
  (->Cart id []))

(defn add-items [cart items]
  (update cart :items into items))

(defn remove-items [cart itemIds]
  (update cart :items #(remove (fn [{:keys [id]}] ((set itemIds) id)) %)))

(def catalog {1 {:id   1
                 :name "apples"}
              2 {:id   2
                 :name "oranges"}})

(defn get-catalog-items [ids]
  (vals (select-keys catalog ids)))

(defn coercion-error-handler [status]
  (let [printer (expound/custom-printer {:theme :figwheel-theme, :print-specs? false})
        handler (exception/create-coercion-handler status)]
    (fn [exception request]
      (printer (-> exception ex-data :problems))
      (handler exception request))))

(defn app [db]
  (ring/ring-handler
    (ring/router
      ["/cart"
       ["/:userid"
        ["" {:get {:parameters {:path [:map [:userid :int]]}
                   :handler    (fn [{{{:keys [userid]} :path} :parameters}]
                                 (let [_ (when (nil? (get-cart db userid)) (add-cart db (make-cart userid)))
                                       cart (get @db userid)]
                                   {:status 200
                                    :body   cart}))}}]
        ["/items" {:post   {:parameters {:path [:map [:userid :int]]
                                         :body [:vector :int]}
                            :handler    (fn [{{{:keys [userid]} :path
                                               productIds       :body} :parameters}]
                                          (let [cart (get-cart @db userid)
                                                products (get-catalog-items productIds)
                                                new-cart (add-items cart products)]
                                            {:status 200
                                             :body   new-cart}))}
                   :delete {:parameters {:path [:map [:userid :int]]
                                         :body [:vector :int]}
                            :handler    (fn [{{{:keys [userid]} :path
                                               productIds       :body} :parameters}]
                                          (let [cart (get-cart @db userid)]
                                            {:status 200
                                             :body   (remove-items cart productIds)}))}}]]]
      {:data {:coercion   mcoercion/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware ;; Query string
                           muuntaja/format-negotiate-middleware ;; Content-Type + Accept headers
                           muuntaja/format-response-middleware ;;
                           muuntaja/format-request-middleware
                           (exception/create-exception-middleware
                             (merge
                               exception/default-handlers
                               {:reitit.coercion/request-coercion  (coercion-error-handler 400)
                                :reitit.coercion/response-coercion (coercion-error-handler 500)}))
                           rrc/coerce-response-middleware   ;; coerce for request + response
                           rrc/coerce-request-middleware]}})))



(defn run [opts]
  (let [system-config (read-config (io/resource "config.edn"))]
    (clip/start system-config)
    @(promise)))

(comment
  (def system (clip/start {:components {:start (MemoryRepo.)}}))
  (data/fetch (MemoryRepo.)))

(comment
  (def dev-instance (run {:port 3000 :join? false}))
  (.stop dev-instance))