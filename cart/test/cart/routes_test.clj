(ns cart.routes-test
  (:require [clojure.test :refer :all]
            [cart.routes :refer :all]
            [muuntaja.core :as m]
            [cart.domain :as domain]
            [cart.datastore :as data]
            [cart.events :as events]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn- request
  [app req]
  (when-some [res (app req)]
    (update res :body #(m/decode "application/json" %))))

(defn make-app
  ([] (make-app {}))
  ([{:keys [cartStore eventStore]
     :or   {cartStore  (data/repository)
            eventStore (events/repository)}}]
   (app cartStore eventStore)))

(defn make-cart
  ([] (make-cart 2))
  ([amount]
   (domain/->Cart
     (gen/generate (s/gen pos-int?))
     (gen/generate (s/gen (s/coll-of ::domain/cart-item :count amount))))))

(deftest GET-cart-test
  (testing "Cart does not exist so new cart is returned"
    (let [id 1
          http-request {:uri            (str "/cart/" id)
                        :request-method :get}
          response (request (make-app) http-request)]
      (is (= 200 (:status response)))
      (is (= (domain/make-cart id) (-> response :body domain/map->Cart)))))

  (testing "Cart already exist"
    (let [{:keys [user-id] :as cart} (make-cart)
          sut (make-app {:cartStore (data/repository {user-id cart})})
          http-request {:uri            (str "/cart/" user-id)
                        :request-method :get}
          response (request sut http-request)]
      (is (= 200 (:status response)))
      (is (= cart (-> response :body domain/map->Cart))))))

(deftest POST-items-test
  (let [id 1
        http-request {:uri            (str "/cart/" id "/items")
                      :request-method :post
                      :path-params    {:user-id id}
                      :body-params    [1 2]}
        response (request (make-app) http-request)]
    (is (= 200 (:status response)))
    (is (= {:items   [{:description "a quiet t-shirt"
                       :name        "Basic t-shirt"
                       :price       {:amount   40
                                     :currency "eur"}
                       :product-id  1}
                      {:description "a loud t-shirt"
                       :name        "Fancy shirt"
                       :price       {:amount   50
                                     :currency "eur"}
                       :product-id  2}]
            :user-id 1}
           (:body response)))))

(deftest DELETE-items-test
  (let [{:keys [user-id] :as stub-cart} (make-cart 2)
        productId (-> stub-cart :items first :product-id)
        http-request {:uri            (str "/cart/" user-id "/items")
                      :request-method :delete
                      :path-params    {:user-id user-id}
                      :body-params    [productId]}
        sut (make-app {:cartStore (data/repository {user-id stub-cart})})
        response (request sut http-request)]
    (is (= 200 (:status response)))
    (is (= 1
           (count (-> response :body :items))))))



(deftest events-test
  (let [sut (make-app {:eventStore (events/repository)})
        http-request {:uri            "/events"
                      :request-method :get
                      :query-params   {:start 1}}
        response (request
                   sut
                   http-request)]
    (is (= 200 (:status response)))
    (is (= [] (:body response)))))