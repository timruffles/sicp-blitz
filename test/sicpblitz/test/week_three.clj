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
      (forget-value b f)
      (set-value! a 5 f)
      (set-value! c 15 f)
      (is (= 10 (get-value b)))
      (forget-value a f)
      (forget-value b f)
      (set-value! b 9 f)
      (set-value! c 15 f)
      (is (= 6 (get-value a)))
      )
    ))

(deftest multiplier-test
  (let [
      a (make-connector)
      b (make-connector)
      c (make-connector)
      f (make-connector)
      add-abc (multiplier a b c) 
    ]
    (do
      (set-value! a 5 f)
      (set-value! b 10 f)
      (is (= 50 (get-value c)))
      (forget-value b f)
      (is (= nil (get-value c)))
      (forget-value b f)
      (set-value! a 5 f)
      (set-value! c 15 f)
      (is (= 3 (get-value b)))
      (forget-value b f)
      (set-value! b 3 f)
      (set-value! c 15 f)
      (is (= 5 (get-value a)))
      )
    ))

(deftest constant-test
  (let [
      a (make-connector)
      f (make-connector)
      constantly-five (constant a 5)
    ]
    (do
      (is (= 5 (get-value a)))
      (forget-value a f)
      (is (= 5 (get-value a)))
    )))

(deftest celsius
  (let [
      celsius (make-connector)
      fahrenheit (make-connector)
      u (make-connector)
      v (make-connector)
      w (make-connector)
      x (make-connector)
      y (make-connector)
      f (make-connector)
    ]
    ; 9C = 5(F - 32)
    (do
      (constant w 9)
      (constant x 5)
      (constant y 32)
      (multiplier celsius w u)
      (multiplier x v u)
      ; F is 32 less than 9C/5, so when 32, and 9C/5 are set, we have 9C/5 - 32 = F
      (adder y v fahrenheit)
      (is (= nil (get-value fahrenheit)))
      (set-value! celsius 25 f)
      (is (= 77 (get-value fahrenheit)))
      (forget-value celsius f)
      (set-value! fahrenheit 77 f)
      (is (= 25 (get-value celsius)))
    )))
