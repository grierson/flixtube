(ns cart.datastore
  (:require [cart.domain :as domain]))

(defn save [db {:keys [userid] :as cart}]
  (swap! db assoc userid cart))

(defn fetch-by-id [db id]
  (get @db id (domain/make-cart id)))

(defn repository []
  (atom {}))

(comment
  (def temp (repository))
  (save temp {:userid 2 :items []})
  (fetch-by-id temp 2))