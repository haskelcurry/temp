(ns dribbble.core
  (:require [clojure.string :as str]
            [clojure.data.json :as json]))

(def token "cdf99597fa3d5f33c8148cfc517d42dd0da1c9f0aafa6ca448a3f233e81e1ee2")

(defn api-url [action] (str "https://api.dribbble.com/v1/" action "?access_token=" token))
(defn followers-url [username] (api-url (str "users/" username "/followers")))
(defn shots-url [username] (api-url (str "users/" username "/shots")))
(defn shot-likes-url [shot] (api-url (str "shots/" shot "/likes")))

(defn get-shots [username] (when username (json/read-str (-> username shots-url slurp))))
(defn get-shots-likes [shot]
  (when shot
    (json/read-str (-> shot shot-likes-url slurp))))

(defn count-liker [likers liker-name]
  (if (contains? likers liker-name)
    (update likers liker-name inc)
    (assoc likers liker-name 1)))

(defn get-follower-shots-likes [shots coll]
  "Get likes count for follower shots"
  (let [shot (first shots)
        shot-id (get shot "id")
        likes (get-shots-likes shot-id)
        likers (map #(get-in % ["user" "username"]) likes)]
      (if (empty? shots)
        coll
        (recur (rest shots) (reduce count-liker coll likers)))))

(defn get-followers [username]
  (let [data (json/read-str (-> username followers-url slurp))]
    (map #(get-in % ["follower" "username"]) data)))

(defn get-followers-likes [username]
  "Get a map of followers with likes count"
  (loop [followers (get-followers username)
         acc {}]
    (let [follower (first followers)]
      (if (empty? followers)
        acc
        (recur (rest followers) (get-follower-shots-likes (get-shots follower) acc))))))

(defn top-10 [username]
  (let [likes-counted (get-followers-likes username)]
    (take 10 (reverse (sort-by val likes-counted)))))

(defn -main [username]
  (println (top-10 username)))
