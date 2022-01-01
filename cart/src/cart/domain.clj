(ns cart.domain)

(defrecord Money [currency amount])
(defrecord Cart [userid items])
(defrecord CartItem [id name description price])

(defn add-items [cart items]
  (update cart :items concat items))

(defn remove-items [cart productIds]
  (update cart :items (fn [coll] (remove (fn [{:keys [id]}] (contains? (set productIds) id)) coll))))

(defn make-cart [id]
  (->Cart id []))

(def catalog {1 {:id   1
                 :name "apples"}
              2 {:id   2
                 :name "oranges"}})

(defn get-catalog-items [ids]
  (vals (select-keys catalog ids)))

