(ns clojush.examples.benchmark-bowling
  (:use [clojush.pushgp.pushgp]
        [clojush.examples.kata.bowling-helper]))

;;;;;;;;;;
;; Run PushGP on KataBowling

(def benchmark-test-cases
  [["--------------------" 0]
   ["1-------------------" 1]
   ["11------------------" 2]
   ["------------------11" 2]
   ["--------9-----------" 9]
   ["-9------------------" 9]
   ["X3-----------------" 16]
   ["6/3-----------------" 16]
   ["--3---2----1-8-11---" 16]
   ["------2/---------7--" 17]
   ["------------------X54" 19]
   ["------------------X5/" 20]
   ["11111111111111111111" 20]
   ["6/34----------------" 20]
   ["------------------4/X" 20]
   ["-/6-----------------" 22]
   ["-6----2/---------7--" 23]
   ["----XX3-----------" 39]
   ["22222222222222222222" 40]
   ["----------------X2/X" 40]
   ["232323--45-2-4-3317-" 44]
   ["-5-5-5-5-5-5-5-5-2-3" 45]
   ["22226222252222322212" 47]
   ["X--X--X--X--X--" 50]
   ["333333--333333333333" 54]
   ["9--89--54---7--9--3-" 54]
   ["623563267/21-3452-13" 66]
   ["-4-/-2-/-7-6-/-3-/-4" 66]
   ["44442444424442442414" 69]
   ["9-9-9-9-9-4471-9-3-5" 78]
   ["819-3/448--76-6-36-9" 85]
   ["-9-9-9-9-9-9-9-9-9-8" 89]
   ["9-9-9-9-9-9-9-9-9-9-" 90]
   ["726/-8-69/6/33456252" 92]
   ["-/-/-/-/-/-/-/-/-/-/-" 100]
   ["4/7/3342188/-6X21X8/" 103]
   ["3-9/1/638-2645X71X-6" 106]
   ["XX--XX--XX--XX-" 110]
   ["4/8/62-/452/348-8/23" 110]
   ["X52X52X52X52X52" 120]
   ["XXXXX----------" 120]
   ["4/6/-71/2/714/8/253/3" 120]
   ["7/X6-8/XX174-721-" 123]
   ["812/53XX9-5-17517/X" 128]
   ["4/9/3/4/8/-/6/6/3/5/9" 153]
   ["X7/9-X-88/-6XXX81" 167]
   ["9/XXX9/36XXX8/9" 218]
   ["XX7/XX8/35XXXX6" 222]
   ["XXXXXXXXXXX9" 299]
   ["XXXXXXXXXXXX" 300]])

(pushgp
  :error-function (kata-bowling-error-functioner benchmark-test-cases)
  :atom-generators kata-bowling-atom-generators
  :max-points 1500
  :max-points-in-initial-program 800
  :evalpush-limit 2000
  :population-size 500
  :max-generations 8
  :mutation-probability 0.1
  :mutation-max-points 10
  :crossover-probability 0.8
  :simplification-probability 0.05
  :tournament-size 6
  :trivial-geography-radius 10
  :node-selection-method :size-tournament
  :node-selection-tournament-size 2
  :report-simplifications 0
  :final-report-simplifications 0
  )
