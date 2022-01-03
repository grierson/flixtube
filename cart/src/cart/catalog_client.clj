(ns cart.catalog-client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [cart.domain :as domain]))

(def url "https://git.io/JeHiE")

(defn- fetch-products [url]
  (let [p (promise)]
    (client/get url {:async? true}
                (fn [request] (deliver p (json/read-json (:body request) true)))
                (fn [exception] (deliver p (str "exception message is: " (.getMessage exception)))))
    @p))

(defn- data->domain [{:keys [productId productName productDescription price]}]
  (domain/->CartItem productId productName productDescription (domain/map->Money price)))

(defn get-products [productsIds]
  (map data->domain (fetch-products url)))