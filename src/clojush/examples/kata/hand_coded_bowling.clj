(ns clojush.examples.kata.hand-coded-bowling
  (:use [clojush.examples.kata.bowling-helper]
        [clojush.evaluate]
        [clojush.individual]
        [clojush.globals]
        [clojush.interpreter]
        [clojush.pushstate]
        [clojush.util]
        ))


(reset! global-atom-generators kata-bowling-atom-generators)

(reset! global-evalpush-limit 2000)
(reset! global-max-points-in-program 1000)



(def bowling-program
  '(string_parse_to_chars
     (tag_exec_100 (string_bowling_atoi integer_add)) ;; The regular add up
     (tag_exec_200 (string_pop tagged_100 string_dup tagged_100)) ;; The spare (frame) code
     (tag_exec_300 (tagged_100 ; Add in 10 and remove X
                    tagged_800 ; IsSpare? If true, add 10, else, add next 2
                    exec_if (10 integer_add)
                            (string_dup tagged_100
                             string_swap string_dup tagged_100
                             string_swap)
                               )) ;; The Strike code
     (tag_exec_700 (string_dup "X" string_eq)) ;;IsStrike?
     (tag_exec_800 (1 string_yankdup "/" string_eq)) ;;IsSpare? (returns True if current frame results in spare)
     (tag_exec_900 (string_pop)) ;; Pop string (for if the next bowl is spare)
     (tag_exec_400 ;;Frame function
       (tagged_700
         exec_if (tagged_300)
                 (tagged_800
                   exec_if (tagged_200)
                           (tagged_100 tagged_100))))
     (10 exec_do*times tagged_400) ;;Do frame function 10 times
     )
  )

#_(let [input "X7/9-X-88/-6XXX81"
      output 167
      ]
  (run-push bowling-program
            (push-item input :auxiliary
                       (push-item input :string
                                  (make-push-state)))))



(evaluate-individual (make-individual :program bowling-program)
                     kata-bowling-error-function
                     (new java.util.Random))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test an evolved partial solution

(def evolved-bowling-program
  '((((boolean_stackdepth ((string_parse_to_chars (string_atoi boolean_swap)) (in_string boolean_swap exec_yank) (string_reverse 7) integer_gt string_rot) (in_string ((boolean_not) boolean_stackdepth) (((boolean_dup) (boolean_not integer_fromboolean integer_add) exec_do*range string_atoi) integer_yankdup string_yank ((integer_eq) (integer_fromboolean) (string_bowling_atoi) exec_rot))) (boolean_eq (string_reverse)) (integer_shove string_stackdepth integer_rot) integer_shove) (string_parse_to_chars integer_stackdepth) integer_eq) (string_swap) boolean_dup) 34 ((exec_stackdepth (exec_dup ((7) string_shove 4) (string_dup ((integer_gt string_length) integer_mod integer_div) integer_div (integer_lt)) (string_reverse (integer_mult string_dup string_yankdup exec_swap boolean_dup exec_when exec_when (exec_swap string_yank) exec_when string_swap exec_s boolean_frominteger) integer_add) integer_fromboolean ((boolean_stackdepth string_shove (string_yank exec_eq boolean_or) integer_add boolean_dup) string_reverse))) exec_dup (integer_mod boolean_swap integer_swap string_atoi) 47) ((integer_add) ((((string_shove (integer_mult (string_dup) (((((boolean_stackdepth string_shove integer_lt integer_max) string_dup) exec_stackdepth) (((boolean_and in_string (string_length) (integer_fromboolean (integer_rot integer_yankdup) exec_yankdup))) (integer_dup) string_atoi (boolean_not)) exec_yank (string_stackdepth)) integer_gt)) (((tagged_4 (string_parse_to_chars integer_stackdepth) integer_eq) (exec_when (integer_mult) exec_when string_dup) boolean_dup) (2) ((string_dup ((integer_div) ((7) string_shove 4) (string_dup ((integer_gt string_atoi) exec_yank exec_do*count) (integer_add) integer_shove) (string_rot ((((integer_mult)) string_shove boolean_rot) (string_dup) (boolean_swap (21 exec_rot exec_s) boolean_frominteger)) integer_mult) (((tagged_4 (string_parse_to_chars integer_stackdepth) (string_yank exec_eq boolean_or)) ((exec_stackdepth (exec_dup ((7) string_shove 4) (string_dup ((integer_gt string_atoi) integer_mod exec_do*count) integer_div (integer_lt)) (string_reverse (integer_mult string_dup string_yankdup exec_swap boolean_dup exec_when exec_when (boolean_stackdepth boolean_stackdepth (boolean_eq)) integer_max string_swap exec_s ((boolean_stackdepth (integer_div boolean_swap) ((exec_stackdepth (exec_dup ((7) string_shove 4) (string_dup ((integer_gt string_atoi) integer_mod exec_do*count) integer_div (integer_lt)) (((string_dup ((integer_gt string_atoi) integer_mod exec_do*count) integer_div (integer_lt))) integer_add) integer_fromboolean ((((integer_fromboolean ((boolean_or integer_eq) (boolean_dup) ("9")) integer_div ((string_yankdup exec_if (integer_add exec_k)) exec_eq)) string_atoi ((string_shove string_atoi) integer_mult in_string string_bowling_atoi) (exec_rot string_atoi exec_shove)) 7 integer_add boolean_dup) (integer_max string_parse_to_chars)))) exec_dup (integer_mod (integer_rot 6) integer_swap) 47) (string_swap in_string (string_length) (string_dup)) boolean_dup) (integer_eq (exec_shove) ((exec_eq) string_reverse exec_pop) string_reverse integer_swap (string_atoi 4 exec_dup exec_s)))) integer_add) integer_fromboolean (exec_rot (boolean_not 4) integer_max))) exec_dup (integer_mod boolean_swap integer_swap string_atoi) 47) boolean_dup) integer_max ((((integer_yankdup) integer_gt) (integer_stackdepth ((string_yank) exec_eq) exec_eq ("6"))) exec_dup (integer_mod boolean_swap integer_swap string_atoi) (exec_yank)) (string_bowling_atoi ((((string_shove (integer_mult (string_dup) (boolean_and in_string (string_length) (integer_fromboolean (integer_rot integer_yankdup) exec_yankdup))) (exec_swap)) string_dup) (boolean_stackdepth ((boolean_frominteger) (string_yankdup integer_max string_swap) boolean_eq (((string_shove (integer_mult (tagged_782 string_parse_to_chars) (string_swap in_string (string_length) (string_dup))) string_reverse) ((string_parse_to_chars) boolean_dup integer_max)) (exec_yank)) (string_eq boolean_stackdepth (integer_gt ((exec_do*range integer_sub) integer_mult integer_yank) (string_yank exec_do*range))) string_dup) integer_lt (string_parse_to_chars integer_stackdepth)) (string_yank) (integer_shove ((boolean_stackdepth string_shove (string_yank exec_eq exec_y) integer_add boolean_dup) string_shove (string_bowling_atoi))) exec_stackdepth (exec_yank)) (exec_swap exec_rot exec_s) (integer_sub) ((integer_div (exec_yankdup integer_min (boolean_eq))) string_yankdup (boolean_not) (in_string boolean_swap exec_yank)) exec_swap exec_eq ((boolean_stackdepth string_shove (string_parse_to_chars integer_stackdepth) (string_swap in_string (string_length) (string_dup)) boolean_dup) string_stackdepth) exec_yankdup) string_rot)) ((boolean_stackdepth string_shove (string_parse_to_chars integer_stackdepth) integer_add boolean_dup) string_stackdepth))) exec_dup (((boolean_or boolean_frominteger) (boolean_stackdepth string_shove integer_lt integer_max) (string_parse_to_chars string_parse_to_chars (integer_eq exec_y (integer_yank string_bowling_atoi integer_add (integer_div))) integer_swap exec_if string_dup 6 boolean_dup) (string_yankdup) (((exec_when (integer_shove (exec_dup integer_mod) exec_if) (string_eq)) 1 exec_shove (exec_y)) exec_yankdup ((exec_pop boolean_swap (exec_yank (((tagged_4 (string_parse_to_chars integer_stackdepth) (string_yank exec_eq boolean_or)) exec_rot (integer_mod integer_mult string_shove ((integer_gt string_atoi) integer_mod exec_do*count)) (exec_rot boolean_rot integer_eq exec_shove)) exec_do*range) boolean_eq string_length) ((integer_yankdup) integer_yankdup (((boolean_and integer_add ((exec_yankdup) integer_mult (tag_integer_98) string_bowling_atoi)) integer_yankdup integer_eq ((integer_mod boolean_swap integer_swap (integer_lt)) boolean_frominteger)) (integer_gt integer_eq) integer_sub (string_atoi) exec_if))) string_dup) in_string integer_div (integer_mod string_yank) (integer_max (integer_gt integer_eq) (string_length) integer_max exec_if) integer_yankdup) boolean_rot) boolean_frominteger (string_parse_to_chars integer_stackdepth) (exec_when) integer_eq exec_yank string_dup integer_mod) ((string_shove (integer_mult (string_dup) ((exec_when (integer_rot integer_yank) exec_when exec_eq) integer_lt (exec_yank))) string_reverse) string_dup)) (string_eq))) 7) string_reverse ((exec_rot boolean_stackdepth) ((integer_max) boolean_not (boolean_swap integer_mod)) (integer_max string_take string_stackdepth) boolean_eq (integer_eq)) exec_yank exec_stackdepth (exec_shove)) boolean_frominteger (integer_mult (string_dup) (((integer_fromboolean) exec_k (integer_mult (string_dup) (((exec_shove (tag_exec_147) boolean_dup)) exec_do*count (integer_rot integer_yankdup) integer_swap))) exec_yankdup)) (string_atoi ((exec_shove ((string_bowling_atoi integer_add (integer_div)) exec_yank (in_string))) boolean_frominteger string_dup) integer_swap exec_s) boolean_rot exec_eq ((exec_do*count string_atoi ((exec_stackdepth (exec_dup ((7) string_shove 4) (((boolean_frominteger) string_atoi (((21 exec_rot exec_s) boolean_frominteger ((string_stackdepth string_parse_to_chars) exec_pop exec_swap) (string_parse_to_chars) string_yank) boolean_not boolean_swap boolean_not) (string_dup string_reverse) integer_shove string_dup) ((integer_gt string_atoi) integer_mod exec_do*count) integer_div (integer_lt)) (exec_dup (integer_mult (string_dup) ((string_yankdup (exec_swap "-") exec_when string_swap) exec_s boolean_frominteger)) integer_yankdup) integer_fromboolean ((boolean_stackdepth string_shove (integer_mult (string_dup)) integer_add boolean_dup) string_stackdepth))) exec_dup (integer_mod boolean_swap integer_swap string_atoi) 47) (integer_min exec_do*count) boolean_swap string_dup) boolean_swap integer_swap (((string_shove (boolean_and) string_reverse) 7) ((string_shove string_take) (integer_mult (string_parse_to_chars integer_min) integer_div) (tag_exec_770) ((integer_max integer_yankdup (exec_do*count)) string_reverse) integer_lt) integer_yankdup (string_swap ((integer_gt string_swap integer_lt) integer_shove integer_rot integer_gt) (string_length) (string_dup)) ((exec_s)) string_atoi)) (boolean_frominteger (integer_sub string_dup ((integer_add (string_bowling_atoi ((integer_sub) (integer_div exec_eq))) exec_pop (string_bowling_atoi ("1" integer_mod (integer_yankdup)) integer_div (integer_lt))) string_yankdup string_rot exec_yankdup)))) string_rot))
  )

(evaluate-individual (make-individual :program evolved-bowling-program)
                     kata-bowling-error-function
                     (new java.util.Random))

(count-points evolved-bowling-program)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;Enumerate test cases based on strike, spare, both, and none
(def test-cases-type
  (map-indexed #(vector %1 %2 (if (some #{\X} (first %2))
                                (if (some #{\/} (first %2))
                                  :both
                                  :strike)
                                (if (some #{\/} (first %2))
                                  :spare
                                  :none)))
               test-cases))

(defn get-test-cases-type
  [type]
  (filter #(= (nth % 2) type) test-cases-type))

(defn get-test-cases-type-numbers
  [type]
  (map first (get-test-cases-type type)))

(defn Rify-column-names
  [type]
  (println (str "(TC"
                (apply str (interpose ", TC" (get-test-cases-type-numbers type)))
                ")")))

;(get-test-cases-type-numbers :both)

;(Rify-column-names :spare)
