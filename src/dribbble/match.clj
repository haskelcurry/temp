(ns dribbble.match
  (:require [clojure.string :as str]))

(defn map->vec [input]
  (reduce #(into %1 %2) [] input))

(defn parse-num [input-string]
  (when (and input-string (string? input-string))
    (let [is-num (re-find #"^\d+$" input-string)]
      (if is-num (Long. input-string) input-string))))

(defn parse-url [url]
  (let [[_ domain path queryparam] (re-matches #".+://([^/]+)/([^\?]*)[\?]?(.*)" url)]
    {:host domain
     :path path
     :queryparam queryparam}))

(defn parse-part [part]
  (let [[_ part-type value] (re-matches #"(.+)\((.+)\)" part)
         binds (into [] (map second (re-seq #"\?([^/]+)" value)))
         regexp (str/replace value #"\?([^/]+)" "([^&]*)")]
     [part-type value binds regexp]))

(defn get-part-value [url part]
  (let [url-map (parse-url url)
        [type _ binds regexp] (parse-part part)
        part-value-in-url ((keyword type) url-map)
        [is-found & matched] (re-find (re-pattern regexp) part-value-in-url)
        result-map (zipmap (map keyword binds) (map parse-num matched))]
    (when is-found result-map)))

(defn get-parts [pattern]
  (map str/trim (str/split pattern #";")))

(defprotocol Recognizable
  (recognize [pattern url]))

(defrecord Pattern [pattern]
  Recognizable
  (recognize [pattern url]
    (let [parts (get-parts (:pattern pattern))
          coll (map #(get-part-value url %) parts)
          is-invalid (some nil? coll)]
      (when-not is-invalid (map->vec (remove empty? coll))))))

(def twitter (Pattern. "host(twitter.com); path(?user/status/?id);"))
(def dribbble (Pattern. "host(dribbble.com); path(shots/?id); queryparam(offset=?offset);"))
(def dribbble2 (Pattern. "host(dribbble.com); path(shots/?id); queryparam(offset=?offset); queryparam(list=?type);"))
