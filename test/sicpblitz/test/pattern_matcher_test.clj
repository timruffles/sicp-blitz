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

(deftest extend-if-consistent-test
  (let [
      empty-frame {}
      frame-with-tim {'?person 'tim}
      frame-with-bob {'?person 'bob}
    ]
    (is (= (extend-if-consistent '?person 'tim empty-frame) frame-with-tim))
    (is (= (extend-if-consistent '?person 'tim frame-with-tim) frame-with-tim))
    (is (= (extend-if-consistent '?person 'tim frame-with-bob) :failed))
    ))

(deftest extend-if-possible-test
  (let [
      frame {}
    ]
    (is (= :failed (extend-if-possible '?x '(?x ?x) frame)))
    (is (not= :failed (extend-if-possible '?x '(?z ?y) frame)))
    ))

(deftest unify-match-test
  (let [
      query '(is-hacker? ?x ?job)
      rule '(is-hacker? ?person ?role)
    ]
    (is (= (unify-match query rule {}) '{?x ?person ?job ?role}))
      ))

(deftest depends-on-test
  (let [
    cases [
      '[[?x ?x {}] true]
      '[[?x [?x ?y] {}] true]
      '[[?x [?y ?z] {}] false]
      '[[?x ?y {?y [?x ?z]}] true]
      '[[?x ?y {?y [?u ?z]}] false]
      '[[?x 5 {}] false]
      '[[?x [5 5 5] {}] false]
    ]
  ]
  (doseq [
    setup cases
  ]
    (let [
        [[to from frame] expected] setup
      ]
        (is (= expected (depends-on? from to frame)))
      )

    )
  ))
