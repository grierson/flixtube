(ns catalog.routes-test
  (:require
    [clojure.test :refer :all]
    [video.routes :refer :all]
    [ring.mock.request :refer [request]]))

(deftest get-products-test
  (let [sut (app)]
    (testing "GET"
      (is (= (sut (request :get ""))
             {:status 200
              :body "hello"})))))
