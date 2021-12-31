(ns cart.datastore)

(defprotocol CartRepository
  (create [this cart] "Add cart")
  (fetch [this] "Get all carts"))

(def carts (atom {}))

(defrecord MemoryRepo []
  CartRepository
  (create [_ cart]
    (swap! carts conj cart))
  (fetch [_]
    @carts))