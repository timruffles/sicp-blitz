(ns sicpblitz.test.week-three
  (:use [sicpblitz.week-three])
  (:use [clojure.test]))

(deftest hang-of-it
  (is 2 (sicpblitz.week-three/add 1)))
