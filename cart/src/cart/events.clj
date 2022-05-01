(ns cart.events
  (:require [java-time :as datetime]))


(defrecord Event [sequenceNumber occurrence name content])

(defn next-seq-number [db]
  (if-some [last-event (last @db)]
    (inc (:sequenceNumber last-event))
    1))

(defn raise [db event content]
  (let [seq-number (next-seq-number db)]
    (swap! db conj (->Event seq-number (datetime/local-date-time) event content))))

(defn get-events [db firstEvent lastEvent]
  (->> @db
       (drop-while #(< (:sequenceNumber %) firstEvent))
       (take-while #(<= (:sequenceNumber %) lastEvent))))

(defn repository
  ([] (atom []))
  ([state] (atom state)))

(comment
  (def temp (repository))
  (next-seq-number temp)
  (raise temp "AddItem" {:itemid 1})
  (get-events temp 2 3))
