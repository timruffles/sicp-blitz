(ns sicpblitz.test.week-three
  (:use [sicpblitz.week-three])
  (:use [clojure.test]))

(deftest basic-connector
  (let [
      a (make-connector)
      b (make-connector)
    ]
    (do
      (set-value! a 5 b)
      (is 5 (get-value a))
      )
    ))
