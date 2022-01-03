(ns cart.catalog-client
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def url "https://git.io/JeHiE")
(client/get url {:async? true}
            (fn [{:keys [body]}] (json/read-str body :key-fn keyword))
            (fn [exception] (println "exception message is: " (.getMessage exception))))



;getProductPathTemplate = "?productIds=[{0}]";
;.Add(new MediaTypeWithQualityHeaderValue("application/json"));
;var productsResource = string.Format(getProductPathTemplate, string.Join(",", productCatalogIds));
;client.GetAsync(productsResource)

