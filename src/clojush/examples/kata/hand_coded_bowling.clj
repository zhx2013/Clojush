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
  '(string_parse_to_chars
     (tag_exec_100 (string_bowling_atoi integer_add)) ;; The regular add up
     (tag_exec_200 (tagged_100 string_dup tagged_100)) ;; The spare code
     (tag_exec_300 (tagged_100 string_dup tagged_100
                    string_swap string_dup tagged_100
                    string_swap)) ;; The Strike code
     (tag_exec_800 (1 string_yankdup "/" string_eq)) ;;IsSpare? (returns True if current frame results in spare)
     (tag_exec_900 (string_pop)) ;; Pop code (for if the next bowl is spare)
     tagged_300 tagged_800 ;tagged_900 ;tagged_200 tagged_100 tagged_100 tagged_300
     ;tagged_100 ;tagged_100 tagged_100 tagged_200 tagged_100 tagged_100
     ;tagged_300 tagged_300 tagged_300  ;; Main code block
     )
  )

(let [input "X7/9-X-88/-6XXX81"
      output 167
      ;program '(5 10 15 integer_add integer_sub)
      ]
  (run-push program
            (push-item input :auxiliary
                       (push-item input :string
                                  (make-push-state)))))

"X528" -> "528", integer stack + 10 + 5 + 2
          "258", integer stack + 10 + 5 + 2

#_(evaluate-individual (make-individual :program program)
                     kata-bowling-error-function
                     (new java.util.Random))

;X = 20
;7 = -- TURNS INTO SPARE
;/ = 39
;9 = 48
;- = 48
;X = 66
;- = 66
;8 = 74
;8 = -- TURNS INTO SPARE
;/ = 84
;- = 84
;6 = 90
;X = 120
;X = 148
;X = 167
;8 = -- LAST STRIKE
;1 = -- LAST STRIKE


