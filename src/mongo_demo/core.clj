(ns mongo-demo.core
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  )


(def conn (mg/connect))
;if any other surfer where mongo is running
;(def conn (mg/connect {:host "some.other.server" :port 7878}))
(def db (mg/get-db conn "factbook"))


(mc/insert db "countries" {:name "Germany" :official-language "German" :population 82000000})
;
(mc/find db "countries" {:name "Germany"})
 (mc/find-maps db "countries" {:name "Germany"})

;(defn fingGermany()
;  (mc/find db "countries" {:name "Germany"}))
;(printf result)
;
(mc/remove db "countries")
(mc/remove db "countries" {:name "Germany"})
;
;
(def factbook-as-map (-> "factbook.json"
                         clojure.java.io/resource
                         slurp
                          parse-string
                         (get "countries")))


(printf "Success")
;
(doseq [[_ document] factbook-as-map]
  (mc/insert db "countries" (document "data")))

(mc/find-maps db
              "countries"
              {:name "Germany"}
              {:_id 0 :people.population 1 :name 1})


(defn countries-by-language [lang]
  (mc/aggregate db "countries"
                [{$match {:people.languages.language {$elemMatch {:name lang}}}}
                 {$unwind :$people.languages.language}
                 {$match {:people.languages.language.name lang}}
                 {"$addFields"
                  {:country :$name
                   :speakersPercentage :$people.languages.language.percent}}
                 {$project {:country 1 :speakersPercentage 1 :_id 0}}
                 {$sort {:speakersPercentage -1}}]))


;(defn foo
;  "I don't do a whole lot."
;  [x]
;  (println x "Hello, World!"))
