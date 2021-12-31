(ns cart.datastore)

(defn create [db {:keys [userid] :as cart}]
  (swap! db assoc userid cart))

(defn fetch [db]
  @db)

(defn fetch-by-id [db id]
  (get @db id))

(defn repository []
  (atom {}))

(comment
  (create state {:userid 2 :items []})
  (fetch-by-id state 2))
