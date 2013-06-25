(ns sicpblitz.logic-programming)

; assert data - (job tim programmer)
; retrieve - (job ?person programmer)
; queries attempt to assign some of the data asserted intp the pattern. so (job ?person programmer) could match (job tim programmer), or bob etc
;  can match structures - (address ?person (specific-place ?any-place))

; 4.55
; Simple queries that retireve the following:
; a. people Ben Bitdiddle supervises
(supervises ?person (Bitdiddle Ben))
; b. names & jobs of people in accounting division
(job ?person (accounting ?role))
; c. names & addresses of Slumerville residents
(address ?person (Slumerville . ?address))

; ## Compound queries
; (and, (not and (or
; (and - pattern variables that can fulfil all, so where ?person is bound to same throughout
; (not - everything that doesn't match the pattern
; (or - pattern variables that match one of two patterns

; (lisp-value predicate arg1 ... argN - will match assignements for which predicate is true given args

; e.g
(and (salary ?person ?amount)
     (lisp-value > ?amount 30000))

; 4.56
; Queries for:
; a. names of those suervised by Bitdiddle, along with addresses
 (and (supervised ?person (Bitdiddle))
      (address ?person ?address))
; b. those paid less than Bitdiddle, with salary and bitdiddle's salary
(and (salary (Bitdiddle) ?bitdiddle-paid)
     (and (salary ?person ?amount)
          (lisp-value > ?bitdiddle-paid ?amount)))


; ## Rules
; Give us way of abstracting queries - like procedures.
;
(rule (same ?x ?x))
(rule (wheel ?person)
      (and (supervisor ?middle-manager ?person)
           (supervisor ?x ?middle-manager)))
; can then be used in queries:
(not (same ?person-1 ?person-2))
; form is
(rule (conclusion ?a ... ?z) (body ... ))
; where conslusion has variables and body defines patterns for them

; 4.57
; Define a rule to say person 1 is a replacement for person 2 if 1 can do 2's job, or if someone who does person 1's job can do person 2\s job, and person 1 and 2 aren't the same.
(rule (replacement-for ?person-2 ?person-1)
    (and
      (job ?person-2 ?job-2)
      (job ?person-1 ?job-1)
      (not (same ?person-1 ?person-2))
      (or (can-do-job ?job-1 ?job-2)
          (and
            (job ?another-person-with-job-1 ?job-1)
            (not (same ?person-1 ?another-person-with-job-1))
            (can-do-job ?another-person-with-job-1 ?job-2)
             ))))
; a. all people who can replace Cy D. Fect
(replacement-for (Fect Cy D) ?replacement)
; b. all people who can replace a higher paid person, & their salaries
(and (salary ?person-a ?salary-a)
     (salary ?person-b ?salary-b)
     (lisp-value > ?person-b ?person-a)
     (replacement-for ?person-b ?person-a))

; 4.58
; Define a rule for 'big shots' - person who works in a division but doesn't have a supervisor within it
(rule (big-shot ?person ?division)
      (and (job ?person (?division . ?stuff))
           (not (and (supervisor ?person ?the-bigger-shot)
                     (job ?the-bigger-shot (?division . ?other-stuff))))))

