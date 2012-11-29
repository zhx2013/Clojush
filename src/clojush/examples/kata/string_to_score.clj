(ns clojush.examples.kata.string-to-score
  (:use [clojush.examples.kata.bowling-helper]
        [clojush.examples.kata.stats-bowling]))

(def two-frame-strings
  (list "----"
        "-11-"
        "-135"
        "1212"
        "2251"
        "--2-"
        "532-"
        "9-9-"
        "-8-9"
        "1314"
        "63-2"
        "449-"
        "-654"
        "4-33"
        "4444"
        "3--3"
        "3318"
        "7-53"
        "-925"
        "549-"
        "7/23"
        "534/6"
        "53-/3"
        "---/-"
        "8/--"
        "-/-7"
        "7/8-"
        "4/62"
        "6/7/4"
        "-/-/-"
        "X42"
        "X7-"
        "52X36"
        "17XX9"
        "-4XXX"
        "XXXX"
        "XX7/"
        "XXX4"
        "X12"
        "72X51"
        "X--"
        "2/X3/"
        "3/X71"
        "-3X9/"
        "X4/2"
        "X-/-"
        "7/X35"
        "4/XXX"
        "6/XX2"
        "X4/2"
        ))

(defn char-to-score
  [ch]
  (cond
    (= ch \X) 10
    (= ch \/) 10
    (= ch \-) 0
    true (Integer/parseInt (str ch))))

(defn string-to-score
  [string frames]
  (if (zero? frames)
    0
    (let [frame-type (cond
                       (= (first string) \X) :strike
                       (= (second string) \/) :spare
                       true :neither)
          frame-total (case frame-type
                        :neither (+ (char-to-score (first string))
                                    (char-to-score (second string)))
                        :spare (+ 10 (char-to-score (get string 2)))
                        :strike (if (= (get string 2) \/)
                                  20
                                  (+ 10
                                     (char-to-score (second string))
                                     (char-to-score (get string 2)))))
          chars (if (= frame-type :strike)
                  1
                  2)]
      (+ frame-total
         (string-to-score (apply str (drop chars string))
                          (dec frames))))))

(defn strings-to-test-cases
  "Takes a list (strings) of input strings containing (frames)
   frames each. Outputs test case vector."
  [strings frames]
  (vec (sort-by second (map #(vector % (string-to-score % frames))
                            strings))))


(count two-frame-strings)

(strings-to-test-cases two-frame-strings 2)

(count (get-test-cases-type-numbers :none (strings-to-test-cases two-frame-strings 2)))

    