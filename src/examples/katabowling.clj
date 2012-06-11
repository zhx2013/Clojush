(ns examples.katabowling
  (:use [clojush]
        [clojure.math.numeric-tower]))

(def test-cases
  {"XXXXXXXXXXXX" 300
   "9-9-9-9-9-9-9-9-9-9-" 90
   "5/5/5/5/5/5/5/5/5/5/5" 150
   "11111111111111111111" 20
   "2-------------------" 2
   "--------------------" 0
   "-------2------------" 2
   "-----------5--------" 5
   "-------3-----9------" 12
   "-------4---------2--" 6
   "-9-9-9-9-9-9-9-9-9-8" 89
   "-5-5-5-5-5-5-5-5-2-3" 45
   "X3-----------------" 16
   "X34----------------" 24
   "----XX3-----------" 39
   "6/3-----------------" 16
   "6/34----------------" 20
   "-/6-----------------" 22
   "-------------------/7" 24
   "------------------X54" 28
   "------------------X5/" 30
   "X7/729/XXX236/7/3" 168
   "X52X52X52X52X52" 120
   "X------------------" 10
   "--------X----------" 10
   "-6----2/---------7--" 23
   "------2/---------7--" 17
   "9-3561368153258-7181" 82
   "9-3/613/815/-/8-7/8/8" 131
   "X3/61XXX2/9-7/XXX" 193
   })

(for [pair test-cases]
  (vector (second pair) (first pair)))

(define-registered 
  in_string
  (fn [state] (push-item (stack-ref :auxiliary 0 state) :string state)))

;; Run PushGP on KataBowling
(pushgp
  :error-function (fn [program]
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
                            1000)))))
  :atom-generators (concat (registered-for-type :integer)
                           (registered-for-type :exec)
                           (registered-for-type :boolean)
                           (registered-for-type :string)
                           (list 'in_string
                                 (tag-instruction-erc [:exec :integer] 1000)
                                 (tagged-instruction-erc 1000)
                                 (fn [] (rand-int 10))
                                 (fn [] (rand-int 100))
                                 (fn [] (apply str (repeatedly (+ 1 (lrand-int 9))
                                                               #(rand-nth (str "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                                                               "abcdefghijklmnopqrstuvwxyz"
                                                                               "0123456789+-*/=")))))))
  :population-size 1000
  :max-generations 300
  :tournament-size 5)
