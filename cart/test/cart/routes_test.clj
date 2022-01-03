(ns cart.routes-test
  (:require [clojure.test :refer :all]
            [cart.routes :refer :all]
            [cheshire.core :refer :all]
            [cart.domain :as domain]
            [cart.datastore :as data]
            [cart.events :as events]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn success [body]
  {:status  200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body    body})

(defn request [system req]
  (-> (system req)
      (update :body slurp)
      (update :body #(parse-string % true))))

(defn make-app
  ([] (make-app {}))
  ([{:keys [cartStore eventStore]
     :or   {cartStore  (data/repository)
            eventStore (events/repository)}}]
   (app cartStore eventStore)))

(defn make-cart []
  (domain/->Cart
    (gen/generate (s/gen pos-int?))
    (gen/generate (s/gen (s/coll-of ::domain/cart-item :max-count 5)))))

(def id 1)

(deftest GET-test
  (testing "Cart does not exist"
    (let [id 1]
      (is (= (success (domain/make-cart id))
             (request
               (make-app)
               {:uri            (str "/cart/" id)
                :request-method :get}))))))

(deftest GET2-test
  (testing "Cart does exist"
    (let [{:keys [user-id] :as cart} (make-cart)]
      (is (= (success cart)
             (request
               (make-app {:cartStore (data/repository {user-id cart})})
               {:uri            (str "/cart/" user-id)
                :request-method :get}))))))

(deftest events-test
  (let [{:keys [status body]} (request
                                (make-app)
                                {:uri            "/events?start=1"
                                 :request-method :get})]
    (is (= 200 status))
    (is (= 2 (count body)))))