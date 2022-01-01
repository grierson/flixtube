(ns cart.routes-test
  (:require [clojure.test :refer :all]
            [cart.routes :refer :all]
            [clojure.data.json :as json]
            [cart.domain :as domain]
            [cart.datastore :as data]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn success [body]
  {:status  200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body    (json/write-str body)})

(defn request [system req]
    (-> (system req)
        (update :body slurp)))

(defn make-app
  ([] (make-app (data/repository)))
  ([db] (app (atom db))))

(defn make-cart []
  (domain/->Cart
    (gen/generate (s/gen pos-int?))
    (gen/generate (s/gen (s/coll-of ::domain/cart-item :max-count 5)))))

(defn make-products []
  (map domain/map->CartItem (gen/generate (s/gen (s/coll-of ::domain/cart-item)))))

(deftest GET-test
  (testing "Cart does not exist"
    (let [id 1]
      (is (= (success (domain/make-cart id))
             (request
               (make-app)
               {:uri            (str "/cart/" id)
                :request-method :get})))))

  (testing "Cart does exist"
    (let [id 1
          cart (make-cart)]
      (is (= (success cart)
             (request
               (make-app {1 cart})
               {:uri            (str "/cart/" id)
                :request-method :get}))))))
