(ns cart.core-test
  (:require [clojure.test :refer :all]
            [cart.core :refer :all]
            [clojure.data.json :as json]))

(defn success [body]
  {:status  200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body    (json/write-str body)})

(defn request [req]
  (-> (app req)
      (update :body slurp)))

(deftest cart-test
  (is (= (success {:userid 1 :items []})
         (request {:uri            "/cart/1"
                   :request-method :get})))
  (is (= (success {:userid 1 :items []})
         (request {:uri            "/cart/42"
                   :request-method :get}))))
