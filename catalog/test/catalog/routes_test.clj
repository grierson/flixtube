(ns catalog.routes-test
  (:require
    [clojure.test :refer :all]
    [catalog.routes :refer :all]
    [jsonista.core :as j]
    [ring.mock.request :refer [request]]))

(defn get-body [path]
  (let [sut (app)]
    (-> (request :get path) sut :body j/read-value)))

(deftest get-products-test
  (testing "GET"
    (is (= (get-body "/products?productIds=1")
           [1]))
    (is (= (get-body "/products?productIds=1,2")
           [1, 2]))))
