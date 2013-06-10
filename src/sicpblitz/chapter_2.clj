; 2.24
; Suppose we evaluate the expression (list 1 (list 2 (list 3 4))). Give the result printed by the interpreter, the corresponding box-and-pointer structure, and the interpretation of this as a tree 

(1 (2 (3 4)))

; 2.25
; Give combinations of cars and cdrs that will pick 7 from each of the following lists:

(1 3 (5 7) 9) ; cdr cdr car cdr


((7)) ; car car

(1 (2 (3 (4 (5 (6 7)))))) ; cdr cdr cdr cdr cdr cdr



; 2.29
; A binary mobile consists of two branches, a left branch and a right branch. Each branch is a rod of a certain length, from which hangs either a weight or another binary mobile. We can represent a binary mobile using compound data by constructing it from two branches (for example, using list):

(defn make-mobile [left right]
  (list left right))

(defn make-branch [length structure]
  (list length structure))

; a. Write the corresponding selectors left-branch and right-branch, which return the branches of a mobile, and branch-length and branch-structure, which return the components of a branch.

(def left-branch first)
(def right-branch (comp first rest))
(def branch-length first)
(def branch-structure (comp first rest))

(defn simple-weight? [branch]
  (number? (branch-structure branch)))

; b. Using your selectors, define a procedure total-weight that returns the total weight of a mobile.

(defn total-weight [mobile]
  (+ (branch-weight (left-branch mobile))
     (branch-weight (right-brannch mobile))))

(defn branch-weight [branch]
  (if (simple-weight? branch)
    (branch-structure branch)
    (total-weight (branch-structure branch))))

; c. A mobile is said to be balanced if the torque applied by its top-left branch is equal to that applied by its top-right branch (that is, if the length of the left rod multiplied by the weight hanging from that rod is equal to the corresponding product for the right side) and if each of the submobiles hanging off its branches is balanced. Design a predicate that tests whether a binary mobile is balanced.

(defn branch-torque [branch]
  (* (branch-length branch) (branch-weight branch)))


(defn branch-balanced? [branch]
  (if (simple-weight? branch)
    true
    (balanced-mobile? (branch-structure branch))))

(defn balanced-mobile? [mobile]
  (let [
    lbranch (left-branch mobile)
    rbranch (right-branch mobile)
    torque-balanced (= (branch-torque lbranch) (branch-torque rbranch))
    subtrees-balanced (and (branch-balanced? rbranch) (branch-balanced? lbranch))
  ] (and torque-balanced subtrees-balanced)))

; d. Suppose we change the representation of mobiles so that the constructors are
; How much do you need to change your programs to convert to the new representation?

; Just the selectors for branches and mobiles, not the procedures that use them


; 2.4 Multiple representations
;
; Lots of things can be represented in multiple ways - complex numbers as polar coords or rectangular. Via generic selectors we can write code that handles both.
;
; 2.4.2 Tagged data
; Tagging the type of data allows us to implement generic dispatch, as we can identify the representation and apply the appropriate selector.
;
; 2.4.3 Data-directed programming
;
; Looking at a datum's type and choosing a procedure is called dispatching on type. We could hand-imeplement this as in 2.4.2, looking at a type inside each operation we wish to be generic, but it's aduous. We're effectively hand implementing a table with operations on one axis and types on the other.
;
; Additivity is defined as being able to add new types without having to change every generic operation (the hand-implementation). 
;
;
; Exercise 2.74
;
; Insatiable Enterprises, Inc., is a highly decentralized conglomerate company consisting of a large number of independent divisions located all over the world. The company’s computer facilities have just been interconnected by means of a clever network-interfacing scheme that makes the entire network appear to any user to be a single computer. Insatiable’s president, in her first attempt to exploit the ability of the network to extract administrative information from division files, is dismayed to discover that, although all the division files have been implemented as data structures in Scheme, the particular data structure used varies from division to division. A meeting of division managers is hastily called to search for a strategy to integrate the files that will satisfy headquarters’ needs while preserving the existing autonomy of the divisions.

; Show how such a strategy can be implemented with data-directed programming. As an example, suppose that each division’s personnel records consist of a single file, which contains a set of records keyed on employees’ names. The structure of the set varies from division to division. Furthermore, each employee’s record is itself a set (structured differently from division to division) that contains information keyed under identifiers such as address and salary. In particular:

; a. Implement for headquarters a get-record procedure that retrieves a specified employee’s record from a specified personnel file. The procedure should be applicable to any division’s file. Explain how the individual divisions’ files should be structured. In particular, what type information must be supplied?
;
; Division files need to have a :Division tag, and all items returned from it do too (or perhaps, better, they return a generic interoperation employee data-structure?).

(defn get-record [employee file]
  (fetch-employee file (:name employee)))

(defmulti fetch-employee (fn [file name] (:Division file)))
(defmethod :Henchpeople [file name] (name file))
(defmethod :Accountants [file name] (first (filter (fn [e] (= (:name e) name)) file)))

; b. Implement for headquarters a get-salary procedure that returns the salary information from a given employee’s record from any division’s personnel file. How should the record be structured in order to make this operation work?
;
; Use the get-record above, we need to ensure either a standard employee structure or that the employee has a type-tag
;
; c. Implement for headquarters a find-employee-record procedure. This should search all the divisions’ files for the record of a given employee and return the record. Assume that this procedure takes as arguments an employee’s name and a list of all the divisions’ files.

(defn find-employee-record [name files]
  (first (filter (fn [file] (fetch-employee file name)) files)))

; d. When Insatiable takes over a new company, what changes must be made in order to incorporate the new personnel information into the central system?
;
; New multi-method definition need to be made for the division's files and employee type.
