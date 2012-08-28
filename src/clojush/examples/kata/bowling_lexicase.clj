(ns clojush.examples.kata.bowling-lexicase
  (:use [clojush.pushgp.pushgp]
        [clojush.examples.kata.bowling-helper]))

;;;;;;;;;;
;; Run PushGP on KataBowling

(pushgp
  :error-function kata-bowling-error-function
  :atom-generators kata-bowling-atom-generators
  :max-points 1000
  :evalpush-limit 2000
  :population-size 10000
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
