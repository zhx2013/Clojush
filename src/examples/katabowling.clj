(ns examples.katabowling
  (:use [clojush]
        [clojure.math.numeric-tower]))

(def test-cases
  [["XXXXXXXXXXXX" 300]
   ["9-9-9-9-9-9-9-9-9-9-" 90]
   ["5/5/5/5/5/5/5/5/5/5/5" 150]
   ["11111111111111111111" 20]
   ["2-------------------" 2]
   ["--------------------" 0]
   ["-------2------------" 2]
   ["-----------5--------" 5]
   ["-------3-----9------" 12]
   ["-------4---------2--" 6]
   ["-9-9-9-9-9-9-9-9-9-8" 89]
   ["-5-5-5-5-5-5-5-5-2-3" 45]
   ["X3-----------------" 16]
   ["X34----------------" 24]
   ["----XX3-----------" 39]
   ["6/3-----------------" 16]
   ["6/34----------------" 20]
   ["-/6-----------------" 22]
   ["-------------------/7" 24]
   ["------------------X54" 28]
   ["------------------X5/" 30]
   ["X7/729/XXX236/7/3" 168]
   ["X52X52X52X52X52" 120]
   ["X------------------" 10]
   ["--------X----------" 10]
   ["-6----2/---------7--" 23]
   ["------2/---------7--" 17]
   ["9-3561368153258-7181" 82]
   ["9-3/613/815/-/8-7/8/8" 131]
   ["X3/61XXX2/9-7/XXX" 193]])


(define-registered 
  in_string
  (fn [state] (push-item (stack-ref :auxiliary 0 state) :string state)))

;; If the top item ion the string stack is a single character that is a bowling character,
;; return the equivalent integer. Otherwise, noop.
(define-registered
  string_bowling_atoi
  (fn [state]
    (if (empty? (:string state))
      state
      (let [top-string (stack-ref :string 0 state)]
        (if (not (== (count top-string)
                     1))
          state
          (if (not (some #{(first top-string)} "123456789-X/"))
            state
            (let [int-to-push (cond
                                (= "X" top-string) 10
                                (= "/" top-string) 10
                                (= "-" top-string) 0
                                true (Integer/parseInt top-string))]
              (pop-item :string
                        (push-item int-to-push :integer state)))))))))

;;;;;;;;;;
;; Define error function and atom generators

(def kata-bowling-error-function
  (fn [program]
    (doall
      (for [test-case test-cases]
        (let [input (first test-case)
              output (second test-case)
              state (run-push program 
                              (push-item input :auxiliary 
                                         (push-item input :string 
                                                    (make-push-state))))
              top-int (top-item :integer state)]
          (if (number? top-int)
            (abs (- output top-int))
            10000))))))

(def kata-bowling-atom-generators
  (concat (list 'integer_add
                'integer_eq
                'integer_swap
                'integer_yank
                'integer_dup
                'integer_yankdup
                'integer_lt
                'integer_flush
                'integer_shove
                'integer_mult
                'integer_stackdepth
                'integer_div
                'integer_gt
                'integer_max
                'integer_fromboolean
                'integer_sub
                'integer_mod
                'integer_rot
                'integer_min
                'integer_pop)
          (list 'exec_y
                'exec_pop
                'exec_eq
                'exec_stackdepth
                'exec_rot
                'exec_when
                'exec_do*times
                'exec_do*count
                'exec_s
                'exec_do*range
                'exec_if
                'exec_k
                'exec_yank
                'exec_flush
                'exec_yankdup
                'exec_swap
                'exec_dup
                'exec_shove
                'exec_noop)
          (list 'boolean_swap
                'boolean_eq
                'boolean_yank
                'boolean_flush
                'boolean_rot
                'boolean_and
                'boolean_shove
                'boolean_not
                'boolean_or
                'boolean_frominteger
                'boolean_stackdepth
                'boolean_yankdup
                'boolean_dup
                'boolean_pop)
          (list 'string_pop
                'string_take
                'string_eq
                'string_stackdepth
                'string_rot
                'string_rand
                'string_yank
                'string_swap
                'string_yankdup
                'string_flush
                'string_length
                'string_concat
                'string_shove
                'string_dup
                'string_atoi
                'string_reverse
                'string_parse_to_chars)
          (list 'in_string
                'string_bowling_atoi
                (tag-instruction-erc [:exec :integer] 1000)
                (tagged-instruction-erc 1000)
                (fn [] (rand-int 10))
                (fn [] (rand-int 100))
                (fn [] (apply str (repeatedly (+ 1 (lrand-int 9))
                                              #(rand-nth (str "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                                              "abcdefghijklmnopqrstuvwxyz"
                                                              "0123456789+-*/=")))))
                (fn [] (str (rand-nth "123456789-X/")) ;;Bowling random character
                  ))))

;;;;;;;;;;
;; Run PushGP on KataBowling

(pushgp
  :error-function kata-bowling-error-function
  :atom-generators kata-bowling-atom-generators
  :max-points 400
  :evalpush-limit 1000
  :population-size 2000
  :max-generations 500
  :tournament-size 5)


;; Test random individual
#_(clojush/evaluate-individual (clojush/make-individual
                               :program (clojush/random-code 150 kata-bowling-atom-generators))
                             kata-bowling-error-function
                             (new java.util.Random))

;; Test string-bowling-atoi
#_(clojush/evaluate-individual
  (clojush/make-individual
    :program '("sad" string-bowling-atoi
                     "5ds+3" string-bowling-atoi
                     "832" string-bowling-atoi
                     "0" string-bowling-atoi
                     "5" string-bowling-atoi
                     "1" string-bowling-atoi
                     "9" string-bowling-atoi
                     "X" string-bowling-atoi
                     "/" string-bowling-atoi
                     "-" string-bowling-atoi)
    )
  kata-bowling-error-function
  (new java.util.Random))

#_(run-push '("sad" string-bowling-atoi
                     "5ds+3" string-bowling-atoi
                     "832" string-bowling-atoi
                     "0" string-bowling-atoi
                     "A" string-bowling-atoi
                     "5" string-bowling-atoi
                     "1" string-bowling-atoi
                     "9" string-bowling-atoi
                     "X" string-bowling-atoi
                     "/" string-bowling-atoi
                     "-" string-bowling-atoi)
          (make-push-state))
