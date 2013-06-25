(ns sicpblitz.pattern-matcher)

(declare match)
(declare extend-if-consistent)
(declare isvariable?)
(declare binding-in-frame)
(declare extend-frame)

; query (job ?person ?title)
; assertion (job bob (trie surgeon))
(defn match [pattern data frame]
  (cond (= frame :failed) :failed
        (= pattern data) frame
        (isvariable? pattern) (extend-if-consistent pattern data frame)

        (and (seq? pattern) (seq? data))
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

(defn extend-frame [variable value frame]
  (assoc frame variable value))

(defn isvariable? [x]
  (and (symbol? x) 
       (= \? (first (name x)))
       (> (count (name x)) 1)
    ))
