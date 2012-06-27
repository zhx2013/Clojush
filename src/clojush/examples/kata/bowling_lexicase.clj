(ns clojush.examples.kata.bowling_lexicase
  (:use [clojush.pushgp.pushgp]
        [clojush.pushstate]
        [clojush.interpreter]
        [clojush.random]
        [clojush.instructions.tag]
        [clojure.math.numeric-tower]))

(def test-cases
  [["--------------------" 0]
   ["1-------------------" 1]
   ["--------------1-----" 1]
   ["2-------------------" 2]
   ["-------2------------" 2]
   ["----1------------1--" 2]
   ["11------------------" 2]
   ["------------------11" 2]
   ["---3----------------" 3]
   ["3-------------------" 3]
   ["--------4-----------" 4]
   ["------3------1------" 4]
   ["--1------1---11-----" 4]
   ["-----2-----11-------" 4]
   ["-----------5--------" 5]
   ["-------4---------2--" 6]
   ["----1--2---1---11---" 6]
   ["--------9-----------" 9]
   ["-9------------------" 9]
   ["X------------------" 10]
   ["--------X----------" 10]
   ["-------3-----9------" 12]
   ["X3-----------------" 16]
   ["6/3-----------------" 16]
   ["--3---2----1-8-11---" 16]
   ["------2/---------7--" 17]
   ["111111--111111111111" 18]
   ["11111111111111111111" 20]
   ["6/34----------------" 20]
   ["-/6-----------------" 22]
   ["-6----2/---------7--" 23]
   ["--8-----9-----7-----" 24]
   ["X34----------------" 24]
   ["-------------------/7" 24]
   ["------------------X54" 28]
   ["------------------X5/" 30]
   ["111111X111111111111" 30]
   ["12121212121212121222" 31]
   ["22222222221111511111" 34]
   ["----XX3-----------" 39]
   ["22222222222222222222" 40]
   ["-9--4---9---8----6-5" 41]
   ["232323--45-2-4-3317-" 44]
   ["-5-5-5-5-5-5-5-5-2-3" 45]
   ["22226222252222322212" 47]
   ["X--X--X--X--X--" 50]
   ["333333--333333333333" 54]
   ["9--89--54---7--9--3-" 54]
   ["12345123451234512345" 60]
   ["35333333533333433333" 65]
   ["623563267/21-3452-13" 66]
   ["333333X333333333333" 70]
   ["44442444424442442414" 71]
   ["444444444-4444444444" 76]
   ["9-9-9-9-9-4471-9-3-5" 78]
   ["9-3561368153258-7181" 82]
   ["-9-9-9-9-9-9-9-9-9-8" 89]
   ["9-9-9-9-9-9-9-9-9-9-" 90]
   ["X52X52X52X52X52" 120]
   ["XXXXX----------" 120]
   ["XX--XX--XX--XX-" 120]
   ["----------XXXXX52" 127]
   ["----------XXXXX5/" 130]
   ["----------XXXXX2/" 130]
   ["9-3/613/815/-/8-7/8/8" 131]
   ["45-/XX81356/442-X71" 135]
   ["XX-4XX-7XX--XX-" 142]
   ["5/5/5/5/5/5/5/5/5/5/5" 150]
   ["X7/9-X-88/-6XXX81" 167]
   ["X7/729/XXX236/7/3" 168]
   ["X3/61XXX2/9-7/XXX" 193]
   ["9/XXX9/36XXX8/9" 218]
   ["XXXXX6/XX7/XX5" 248]
   ["XXXX9/XXX2/XXX" 251]
   ["XXXX71XXXXXXX" 263]
   ["1/XXXXXXXXXXX" 290]
   ["XXXXXXXXXXX3" 293]
   ["XXXXXXXXXXX9" 299]
   ["XXXXXXXXXXXX" 300]])

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
  :mutation-probability 0.1
  :crossover-probability 0.8
  :simplification-probability 0.05
  :tournament-size 6
  :trivial-geography-radius 10
  :report-simplifications 0
  :final-report-simplifications 1000
  :use-lexicase-selection true
  )

;; Use the following if running in lein
(do (flush)
    (System/exit 0))
