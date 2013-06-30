(ns sicpblitz.pattern-matcher)

(declare match)
(declare extend-if-consistent)
(declare isvariable?)
(declare binding-in-frame)
(declare extend-frame)
(declare is-bound?)
(def compound? sequential?)

; query (job ?person ?title)
; assertion (job bob (trie surgeon))
(defn match [pattern data frame]
  (cond (= frame :failed) :failed
        (= pattern data) frame
        (isvariable? pattern) (extend-if-consistent pattern data frame)

        (and (compound? pattern) (compound? data))
           (match (rest pattern)
                  (rest data)
                  (match (first pattern)
                         (first data)
                         frame))

        :else :failed))

(defn extend-if-consistent [variable data frame]
  (let [bound-as (binding-in-frame variable frame)]
    (if bound-as
      ; make sure we have the same value
      (match bound-as data frame)
      ; extend our frame
      (extend-frame variable data frame))))

(defn binding-in-frame [variable frame]
  (variable frame))

(defn is-bound? [variable frame]
  (not= nil (binding-in-frame variable frame)))

(defn extend-frame [variable value frame]
  (assoc frame variable value))

(defn isvariable? [x]
  (and (symbol? x) 
       (= \? (first (name x)))
       (> (count (name x)) 1)
    ))

(declare unify-match)
(declare extend-if-possible)
(declare depends-on?)

; now we're dealing with matches with 2 variables, we can assign a variable to a variable
; e.g we have ?x = ?y. now if we unify ?x = 5, we first get (unify-match ?x 5), which then resolves via (extend-if-possible ?x 5) to (unify-match ?y 5), so y is now bound to 5. Next time we get (unify-match ?x 5), then (unify-match ?y 5), then (unify-match 5 5)
(defn unify-match [p1 p2 frame]
  (cond (= frame :failed) :failed
        (= p1 p2) frame
        (isvariable? p1) (extend-if-possible p1 p2 frame)
        (isvariable? p2) (extend-if-possible p2 p1 frame) ; extension from pattern-match, handling RHS being a variable
        (and (compound? p1) (compound? p2))
           (match (rest p1)
                  (rest p2)
                  (match (first p1)
                         (first p2)
                         frame))
        :else :failed))

(defn extend-if-possible [variable data frame]
  (let [
      variable-value (binding-in-frame variable)
      data-value (binding-in-frame data)
    ]

  (cond (is-bound? variable frame) 
          (unify-match variable-value data frame)

        (isvariable? data)
          (if (is-bound? data)
            (unify-match variable-value data-value frame)
            ; this could be var to var, or val to var
            (extend-frame variable data frame))

        ; next 2 cases: neither is bound
        (depends-on? data variable frame) :failed

        ; has to be var to val, as we checked above for data being a var
        :else (extend-frame variable data frame)
    )
    ))

; we don't need to worry about ?x = ?x, because unify match checks for (= p1 p2) early
; (depends-on? (?x ?x) ?x) - true
; (depends-on? (?x ?y) ?x) - true
; (depends-on? (?y ?z) ?x) - false
(defn depends-on? [from to frame]
  (letfn [
    (walk [exp]
      (cond (isvariable? exp)

              (if (= exp to) 
                true
                (if (is-bound? exp frame)
                  (walk (binding-in-frame exp frame))
                  false))

            (compound? exp)
              (if (or (nil? exp) (empty? exp))
                  false
                  (or (walk (first exp))
                      (walk (rest exp))))

            :else false))
    ]
    (walk from)
    ))







