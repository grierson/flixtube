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
  (update cart :items (fn [coll] (remove (fn [{:keys [product-id]}] (contains? (set productIds) product-id)) coll))))

(defn make-cart
  ([id] (->Cart id []))
  ([id items] (->Cart id items)))