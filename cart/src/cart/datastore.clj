(ns cart.datastore)

(defn add-cart [db {:keys [userid] :as cart}]
  (swap! db assoc userid cart))

(defn add-items [db userid items]
  (swap! db update-in [userid :items] concat items))

(defn remove-items [db userid productIds]
  (swap! db update-in [userid :items] (fn [coll] (remove (fn [{:keys [id]}] (contains? (set productIds) id)) coll))))

(defn fetch-by-id [db id]
  (get @db id))

(defn repository []
  (atom {}))

(comment
  (def temp (repository))
  (add-cart temp {:userid 2 :items []})
  (fetch-by-id temp 2))