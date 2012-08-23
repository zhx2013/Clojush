(ns clojush.examples.kata.hand-coded-bowling
  (:use [clojush.examples.kata.bowling]
        [clojush.evaluate]
        [clojush.individual]
        [clojush.globals]
        [clojush.interpreter]
        [clojush.pushstate]
        ;[clojush.pushgp.pushgp]
        ;[clojush.pushstate]
        ;[clojush.interpreter]
        ;[clojush.random]
        ;[clojush.instructions.tag]
        ;[clojure.math.numeric-tower]
        ))


(reset! global-atom-generators kata-bowling-atom-generators)

(def program
  '(5 10 15 integer_add)
  )


(evaluate-individual (make-individual :program program)
                     kata-bowling-error-function
                     (new java.util.Random))

(let [input "X7/9-X-88/-6XXX81"
      output 167
      program '(5 10 15 integer_add integer_sub)
      ]
  (run-push program 
            (push-item input :auxiliary 
                       (push-item input :string 
                                  (make-push-state)))))

;(push-item "53-/" :string (make-push-state))
