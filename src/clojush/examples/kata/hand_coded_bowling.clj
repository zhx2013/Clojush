(ns clojush.examples.kata.hand-coded-bowling
  (:use [clojush.examples.kata.bowling-helper]
        [clojush.evaluate]
        [clojush.individual]
        [clojush.globals]
        [clojush.interpreter]
        [clojush.pushstate]
        ))


(reset! global-atom-generators kata-bowling-atom-generators)

(reset! global-evalpush-limit 2000)


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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(evaluate-individual (make-individual :program bowling-program)
                     kata-bowling-error-function
                     (new java.util.Random))
