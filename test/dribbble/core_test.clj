(ns dribbble.core-test
  (:require [clojure.test :refer :all]
            [dribbble.match :refer :all]))

(deftest test-recognize-twitter
  (is (= (recognize twitter "http://twitter.com/bradfitz/status/562360748727611392")
         [[:user "bradfitz"] [:id 562360748727611392]])))

(deftest test-recognize-dribble
  (is (= (recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users&offset=1")
         [[:id "1905065-Travel-Icons-pack"] [:offset 1]])))

(deftest test-recognize-dribble-fail1
  (is (= (recognize dribbble "https://twitter.com/shots/1905065-Travel-Icons-pack?list=users&offset=1")
         nil)))

(deftest test-recognize-dribble-fail2
  (is (= (recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users")
         nil)))

(deftest test-fail-recognize-dribble2
  (is (= (recognize dribbble2 "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users&offset=1")
         [[:id "1905065-Travel-Icons-pack"] [:offset 1] [:type "users"]])))
