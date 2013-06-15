(ns sicpblitz.week-three
  (:use [clojure.set]))

; primitives are connectors - things that have a value, and some connections to other connections. when they have a value they tell their connections about it

; we also have constraints - that will update a set of connectors according to a constraint. e.g an 'adder' constraint will ensure A + B = C, so if C is set, and A + B != C, BOOM; if C and A are set, C - A = B

; msg passing - we're using fns that return fns in response to msgs
; that we then call with args
(defn has-value? [connector]
  (connector 'has-value?))
(defn get-value [connector]
  (connector 'get-value))
(defn set-value! [connector new-value informant]
  ((connector 'set-value!) new-value informant))
(defn forget-value [connector retractor]
  ((connector 'forget-value) retractor))
(defn connect [connector new-constraint]
  ((connector 'connect) new-constraint))

(defn error [msg]
  ; ugh, java interop to throw an error?!
  (throw (Exception. msg)))

(defn constant [value connector]
  (let [
    me (fn [request]
      (error "Whoops - I'm a constant, you can't do anything to me"))
  ]
  (connect connector me)
  (set-value! connector value me)
  me))

(defn inform-about-value [connector]
  (connector 'I-have-a-value))
(defn inform-about-no-value [connector]
  (connector 'I-lost-my-value))

(defn for-each-except [except f all]
  (doseq [a (filter (fn [x] (not= except x)) all)] (f a))) 

(defn make-connector []
  (let [
    value (atom nil)
    informant (atom nil)
    connections (atom #{})
  ]
    (letfn [
      (me [request]
        (case request
          has-value? (not (nil? (deref value)))
          get-value (deref value)
          set-value! set-value!
          connect connect
          forget-value forget-value))
      (set-value! [new-value informant]
        (if (has-value? me)
          (do
            (if (not= new-value) (error (format "Contradiction: have value %s and was told to set value %s" value new-value))))
          (do
            (compare-and-set! value nil new-value)
            (for-each-except informant inform-about-value (deref connections)))
        )
      )
      (connect [new-connector]
        (swap! connections clojure.set/union #{new-connector}))
      (forget-value [retractor]
        (compare-and-set! value @value nil)
        (for-each-except retractor inform-about-no-value (deref connections)))
    ]
    me)))

(defn adder [a b c]
  (letfn [
    (add [& connectors]
      (apply + (map get-value connectors)))
    (update []
      (cond (and (has-value? a) (has-value? b)) (set-value! c (add a b) me)
            (and (has-value? a) (has-value? c)) (set-value! b (add a c) me)
            (and (has-value? b) (has-value? c)) (set-value! a (add b c) me)))
    (forget []
      (doseq [x [a b c]] (forget-value x me)))
    (me [message]
      (case message
        I-have-a-value (update)
        I-lost-my-value (forget)))
  ]
  (connect a me)
  (connect b me)
  (connect c me)
  me))
