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
      (is (= 5 (get-value a)))
      )
    ))

(deftest adder-test
  (let [
      a (make-connector)
      b (make-connector)
      c (make-connector)
      f (make-connector)
      add-abc (adder a b c) 
    ]
    (do
      (set-value! a 5 f)
      (set-value! b 10 f)
      (is (= 15 (get-value c)))
      (forget-value b f)
      (is (= nil (get-value c)))
      )
    ))
