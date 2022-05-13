(ns video-storage.routes-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [video.routes :as routes]
    [ring.mock.request :refer [request]]))

(deftest get-products-test
  (let [sut (routes/app)]
    (testing "GET"
      (is (= (sut (request :get ""))
             {:status 200
              :body "hello"})))))
