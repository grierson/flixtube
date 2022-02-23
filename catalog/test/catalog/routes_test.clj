(ns catalog.routes-test
  (:require
    [clojure.test :refer :all]
    [catalog.routes :refer :all]
    [ring.mock.request :refer [request]]))


(deftest example-server
  (testing "GET"
    (is (= (-> (request :get "/products?productIds=1,2")
               app :body slurp)
           "[1,2]"))
    (is (= (-> {:request-method :get
                :uri            "/products"
                :query-string   "productIds=1,2"}
               app :body slurp)
           "[1,2]"))
    (is (= (-> {:request-method :get
                :uri            "/products"
                :query-params {:productIds [1 2]}}
               app :body slurp)
           "[1,2]"))))
