(ns sicpblitz.test.pattern-matcher-test
  (:use [sicpblitz.pattern-matcher])
  (:use [clojure.test]))

(deftest pattern-matching
  (let [
      pattern '(job ?x (computer programmer))
      matches '(job tim (computer programmer))
      not-match '(job bob (tree surgeon))
    ]
    (do
      (is (not= :failed (match pattern matches {})))
      (is (= :failed (match pattern not-match {})))
      )))

#_(deftest extend-if-consistent-test
  (let [
      empty-frame {}
      frame-with-tim {'?person 'tim}
      frame-with-bob {'?person 'bob}
    ]
    (is (= (extend-if-consistent '?person 'tim empty-frame) frame-with-tim))
    (is (= (extend-if-consistent '?person 'tim frame-with-tim) frame-with-tim))
    (is (= (extend-if-consistent '?person 'tim frame-with-bob) :failed))
    ))

