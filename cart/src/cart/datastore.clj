(ns cart.datastore
  (:require [cart.domain :as domain]))

(defn save [db {:keys [user-id] :as cart}]
  (swap! db assoc user-id cart))

(defn fetch-by-id [db cart-id]
  (get @db cart-id (domain/make-cart cart-id)))

(defn repository
  ([] (atom {}))
  ([state] (atom state)))

(comment
  (def temp (repository))
  (save temp {:user-id 2 :items []})
  (fetch-by-id temp 2))