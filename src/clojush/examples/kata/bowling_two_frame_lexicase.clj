(ns clojush.examples.kata.bowling-two-frame-lexicase
  (:use [clojush.pushgp.pushgp]
        [clojush.examples.kata.bowling-helper]))

;;;;;;;;;;
;; Run PushGP on KataBowling

(pushgp
  :error-function (kata-bowling-error-functioner two-frame-test-cases)
  :atom-generators kata-bowling-atom-generators
  :max-points 2000
  :max-points-in-initial-program 200
  :evalpush-limit 3000
  :population-size 2000
  :max-generations 300
  :mutation-probability 0.1
  :mutation-max-points 10
  :crossover-probability 0.8
  :simplification-probability 0.05
  :node-selection-method :size-tournament
  :node-selection-tournament-size 2
  :report-simplifications 0
  :final-report-simplifications 1000
  :use-lexicase-selection true
  )
