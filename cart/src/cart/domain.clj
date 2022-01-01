(ns cart.domain
  (:require [clojure.spec.alpha :as s]))

(defrecord Money [currency amount])
(defrecord Cart [user-id items])
(defrecord CartItem [product-id name description price])

(s/def ::currency string?)
(s/def ::amount pos-int?)

(s/def ::money
  (s/keys :req-un [::currency ::amount]))

(s/def ::product-id pos-int?)
(s/def ::name string?)
(s/def ::description string?)
(s/def ::price ::money)

(s/def ::cart-item
  (s/keys :req-un [::product-id ::name ::description ::price]))

(s/def ::user-id pos-int?)
(s/def ::items (s/coll-of ::cart-item))

(s/def ::cart
  (s/keys :req-un [::userid ::items]))

;--- methods

(defn add-items [cart items]
  (update cart :items concat items))

(defn remove-items [cart productIds]
  (update cart :items (fn [coll] (remove (fn [{:keys [id]}] (contains? (set productIds) id)) coll))))

(defn make-cart
  ([id] (->Cart id []))
  ([id items] (->Cart id items)))

(def catalog {1 {:id   1
                 :name "apples"}
              2 {:id   2
                 :name "oranges"}})

(defn get-catalog-items [ids]
  (vals (select-keys catalog ids)))

