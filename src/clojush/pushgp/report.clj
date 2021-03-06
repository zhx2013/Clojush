(ns clojush.pushgp.report
  (:use [clojush util globals pushstate simplification individual]
        [clojure.data.json :only (json-str)])
  (:require [clojure.string :as string]
            [config :as config]
            [clj-random.core :as random]
            [local-file]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helper functions

(defn default-problem-specific-report
  "Customize this for your own problem. It will be called at the end of the generational report."
  [best population generation error-function report-simplifications]
  :no-problem-specific-report-function-defined)

(defn git-last-commit-hash
  "Returns the last Git commit hash"
  []
  (let [dir (local-file/project-dir)]
    (string/trim
      (slurp
        (str dir
             "/.git/"
             (subs
               (string/trim
                 (slurp
                   (str dir "/.git/HEAD")))
               5))))))

(defn print-params [push-argmap]
  (doseq [[param val] push-argmap]
    (if (= param :random-seed)
      (println (name param) "=" (random/seed-to-string val))
      (println (name param) "=" val))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; log printing (csv and json)

(defn csv-print
  "Prints a csv of the population, with each individual's fitness and size.
   If log-fitnesses-for-all-cases is true, it also prints the value
   of each fitness case."
  [population generation csv-log-filename log-fitnesses-for-all-cases]
  (if (not log-fitnesses-for-all-cases)
    (do
      (when (zero? generation)
        (spit csv-log-filename "generation,individual,total-error,size\n" :append false))
      (doseq [[ind p] (map-indexed vector population)]
        (spit csv-log-filename
              (format "%s,%s,%s,%s\n"
                      generation
                      ind
                      (:total-error p)
                      (count-points (:program p)))
              :append true)))
    (do
      (when (zero? generation)
        (spit csv-log-filename "generation,individual,total-error,size," :append false)
        (spit csv-log-filename
              (format "%s\n"
                      (apply str
                             "TC"
                             (interpose ",TC"
                                        (range (count (:errors (first population)))))))
              :append true))
      (doseq [[ind p] (map-indexed vector population)]
        (spit csv-log-filename
              (format "%s,%s,%s,%s,%s\n"
                      generation
                      ind
                      (:total-error p)
                      (count-points (:program p))
                      (apply str (interpose "," (:errors p))))
              :append true)))))

(defn jsonize-individual
  "Takes an individual and returns it with only the items of interest
   for the json logs."
  [log-fitnesses-for-all-cases json-log-program-strings generation individual]
  (let [part1-ind (-> (if log-fitnesses-for-all-cases
                        {:errors (:errors individual)}
                        {})
                      (assoc :total-error (:total-error individual))
                      (assoc :generation generation)
                      (assoc :size (count-points (:program individual))))
        part2-ind (if json-log-program-strings
                    (assoc part1-ind :program (str (not-lazy (:program individual))))
                    part1-ind)
        part3-ind (if (:hah-error individual)
                    (assoc part2-ind :hah-error (:hah-error individual))
                    part2-ind)]
    (if (:rms-error individual)
      (assoc part3-ind :rms-error (:rms-error individual))
      part3-ind)))

(defn json-print
  "Prints a json file of the population, with each individual's fitness and size.
   If log-fitnesses-for-all-cases is true, it also prints the value
   of each fitness case."
  [population generation json-log-filename log-fitnesses-for-all-cases
   json-log-program-strings]
  (let [pop-json-string (json-str (map #(jsonize-individual
                                          log-fitnesses-for-all-cases
                                          json-log-program-strings
                                          generation
                                          %)
                                       population))]
  (if (zero? generation)
    (spit json-log-filename (str pop-json-string "\n") :append false)
    (spit json-log-filename (str "," pop-json-string "\n") :append true))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; report printing functions

(defn lexicase-report
  "This extra report is printed whenever lexicase selection is used."
  [population {:keys [error-function report-simplifications print-errors
                      print-history use-rmse
                      ]}]
  (let [min-error-by-case (apply map
                                 (fn [& args] (apply min args))
                                 (map :errors population))
        lex-best (apply max-key
                        (fn [ind]
                          (apply + (map #(if (== %1 %2) 1 0)
                                        (:errors ind)
                                        min-error-by-case)))
                        population)
        pop-elite-by-case (map (fn [ind]
                                 (map #(if (== %1 %2) 1 0)
                                      (:errors ind)
                                      min-error-by-case))
                               population)
        count-elites-by-case (map #(apply + %) (apply mapv vector pop-elite-by-case))
        most-zero-cases-best (apply max-key
                                    (fn [ind]
                                      (apply + (map #(if (zero? %) 1 0)
                                                    (:errors ind))))
                                    population)
        pop-zero-by-case (map (fn [ind]
                                (map #(if (zero? %) 1 0)
                                     (:errors ind)))
                              population)
        count-zero-by-case (map #(apply + %) (apply mapv vector pop-zero-by-case))
        ]
    (println "--- Lexicse Program with Most Elite Cases Statistics ---")
    (println "Lexicase best program:" (pr-str (not-lazy (:program lex-best))))
    (when (> report-simplifications 0)
      (println "Lexicase best partial simplification:"
               (pr-str (not-lazy (:program (auto-simplify lex-best error-function report-simplifications false 1000))))))
    (when print-errors (println "Lexicase best errors:" (not-lazy (:errors lex-best))))
    (println "Lexicase best number of elite cases:" (apply + (map #(if (== %1 %2) 1 0)
                                                                  (:errors lex-best)
                                                                  min-error-by-case)))
    (println "Lexicase best total error:" (:total-error lex-best))
    (println "Lexicase best mean error:" (float (/ (:total-error lex-best)
                                                   (count (:errors lex-best)))))
    (when use-rmse (println "Lexicase best RMS-error:" (:rms-error lex-best)))
    (when print-history (println "Lexicase best history:" (not-lazy (:history lex-best))))
    (println "Lexicase best size:" (count-points (:program lex-best)))
    (printf "Percent parens: %.3f\n" (double (/ (count-parens (:program lex-best)) (count-points (:program lex-best))))) ;Number of (open) parens / points
    (println "--- Lexicse Program with Most Zero Cases Statistics ---")
    (println "Zero cases best program:" (pr-str (not-lazy (:program most-zero-cases-best))))
    (when (> report-simplifications 0)
      (println "Zero cases best partial simplification:"
               (pr-str (not-lazy (:program (auto-simplify most-zero-cases-best error-function report-simplifications false 1000))))))
    (when print-errors (println "Zero cases best errors:" (not-lazy (:errors most-zero-cases-best))))
    (println "Zero cases best number of elite cases:" (apply + (map #(if (== %1 %2) 1 0)
                                                                  (:errors most-zero-cases-best)
                                                                  min-error-by-case)))
    (println "Zero cases best number of zero cases:" (apply + (map #(if (< %1 min-number-magnitude) 1 0)
                                                                   (:errors most-zero-cases-best))))
    (println "Zero cases best total error:" (:total-error most-zero-cases-best))
    (println "Zero cases best mean error:" (float (/ (:total-error most-zero-cases-best)
                                                   (count (:errors most-zero-cases-best)))))
    (when use-rmse (println "Zero cases best RMS-error:" (:rms-error most-zero-cases-best)))
    (when print-history (println "Zero cases best history:" (not-lazy (:history most-zero-cases-best))))
    (println "Zero cases best size:" (count-points (:program most-zero-cases-best)))
    (printf "Percent parens: %.3f\n" (double (/ (count-parens (:program most-zero-cases-best)) (count-points (:program most-zero-cases-best))))) ;Number of (open) parens / points
    (println "--- Lexicase Population Statistics ---")
    (println "Count of elite individuals by case:" count-elites-by-case)
    (println (format "Population mean number of elite cases: %.2f" (float (/ (apply + count-elites-by-case) (count population)))))
    (println "Count of perfect (error zero) individuals by case:" count-zero-by-case)
    (println (format "Population mean number of perfect (error zero) cases: %.2f" (float (/ (apply + count-zero-by-case) (count population)))))
    ))

(defn report-and-check-for-success
  "Reports on the specified generation of a pushgp run. Returns the best
   individual of the generation."
  [population generation
   {:keys [error-function report-simplifications
           error-threshold max-generations
           print-errors print-history print-cosmos-data print-timings
           problem-specific-report use-rmse use-historically-assessed-hardness
           use-lexicase-selection
           print-error-frequencies-by-case
           ;; The following are for CSV or JSON logs
           print-csv-logs print-json-logs csv-log-filename json-log-filename
           log-fitnesses-for-all-cases json-log-program-strings
           ]
    :as argmap}]
  (println)
  (println ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;")
  (println ";; -*- Report at generation" generation)
  (let [err-fn (cond
                 use-rmse :rms-error
                 true :total-error)
        sorted (sort-by err-fn < population)
        err-fn-best (first sorted)
        psr-best (problem-specific-report err-fn-best population generation error-function report-simplifications)
        best (if (= (type psr-best) clojush.individual.individual)
               psr-best
               err-fn-best)]
    (when print-error-frequencies-by-case
      (println "Error frequencies by case:" (doall (map frequencies (apply map vector (map :errors population))))))
    (when use-lexicase-selection (lexicase-report population argmap))
    (println (format "--- Best Program (%s) Statistics ---" (str "based on " (name err-fn))))
    (println "Best program:" (pr-str (not-lazy (:program best))))
    (when (> report-simplifications 0)
      (println "Partial simplification:"
               (pr-str (not-lazy (:program (auto-simplify best error-function report-simplifications false 1000))))))
    (when print-errors (println "Errors:" (not-lazy (:errors best))))
    (println "Total:" (:total-error best))
    (println "Mean:" (float (/ (:total-error best)
                               (count (:errors best)))))
    (when use-historically-assessed-hardness
      (println "HAH-error:" (:hah-error best)))
    (when use-rmse (println "RMS-error:" (:rms-error best)))
    (when print-history (println "History:" (not-lazy (:history best))))
    (println "Size:" (count-points (:program best)))
    (printf "Percent parens: %.3f\n" (double (/ (count-parens (:program best)) (count-points (:program best))))) ;Number of (open) parens / points
    (println "--- Population Statistics ---")
    (when print-cosmos-data
      (println "Cosmos Data:" (let [quants (config/quantiles (count population))]
                                (zipmap quants (map #(:total-error (nth (sort-by :total-error population) %)) quants)))))
    (println "Average total errors in population:"
             (*' 1.0 (/ (reduce +' (map :total-error sorted)) (count population))))
    (println "Median total errors in population:"
             (:total-error (nth sorted (truncate (/ (count sorted) 2)))))
    (when print-errors (println "Error averages by case:"
                                (apply map (fn [& args] (*' 1.0 (/ (reduce +' args) (count args))))
                                       (map :errors population))))
    (when print-errors (println "Error minima by case:"
                                (apply map (fn [& args] (apply min args))
                                       (map :errors population))))
    (println "Average program size in population (points):"
             (*' 1.0 (/ (reduce +' (map count-points (map :program sorted)))
                        (count population))))
    (printf "Average percent parens in population: %.3f\n" (/ (apply + (map #(double (/ (count-parens (:program %)) (count-points (:program %)))) sorted))
                                                              (count population)))
    (let [frequency-map (frequencies (map :program population))]
      (println "Number of unique programs in population:" (count frequency-map))
      (println "Max copy number of one program:" (apply max (vals frequency-map)))
      (println "Min copy number of one program:" (apply min (vals frequency-map)))
      (println "Median copy number:" (nth (sort (vals frequency-map)) (Math/floor (/ (count frequency-map) 2)))))
    (println "--- Timings ---")
    (println "Current time:" (System/currentTimeMillis) "milliseconds")
    (when print-timings
      (let [total-time (apply + (vals @timing-map))
            init (get @timing-map :initialization)
            reproduction (get @timing-map :reproduction)
            fitness (get @timing-map :fitness)
            report-time (get @timing-map :report)
            other (get @timing-map :other)]
        (printf "Total Time:      %8.1f seconds\n" (/ total-time 1000.0))
        (printf "Initialization:  %8.1f seconds, %4.1f%%\n" (/ init 1000.0) (* 100.0 (/ init total-time)))
        (printf "Reproduction:    %8.1f seconds, %4.1f%%\n" (/ reproduction 1000.0) (* 100.0 (/ reproduction total-time)))
        (printf "Fitness Testing: %8.1f seconds, %4.1f%%\n" (/ fitness 1000.0) (* 100.0 (/ fitness total-time)))
        (printf "Report:          %8.1f seconds, %4.1f%%\n" (/ report-time 1000.0) (* 100.0 (/ report-time total-time)))
        (printf "Other:           %8.1f seconds, %4.1f%%\n" (/ other 1000.0) (* 100.0 (/ other total-time)))))
    (println ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;")
    (flush)
    (when print-csv-logs (csv-print population generation csv-log-filename
                                    log-fitnesses-for-all-cases))
    (when print-json-logs (json-print population generation json-log-filename
                                      log-fitnesses-for-all-cases json-log-program-strings))
    (cond (or (<= (:total-error best) error-threshold)
              (:success best)) [:success best]
          (>= generation max-generations) [:failure best]
          :else [:continue best])))

(defn initial-report
  "Prints the initial report of a PushGP run."
  []
  (println "Registered instructions:" @registered-instructions)
  (println "Starting PushGP run.")
  (printf "Clojush version = ")
  (try
    (let [version-str (apply str (butlast (re-find #"\".*\""
                                                   (first (string/split-lines
                                                            (local-file/slurp* "project.clj"))))))
          version-number (.substring version-str 1 (count version-str))]
      (if (empty? version-number)
        (throw Exception)
        (printf (str version-number "\n"))))
    (flush)
    (catch Exception e
           (printf "version number unavailable\n")
           (flush)))
  (try
    (let [git-hash (git-last-commit-hash)]
      (if (empty? git-hash)
        (throw Exception)
        (do
          ;; NOTES: - Last commit hash will only be correct if this code has
          ;;          been committed already.
          ;;        - GitHub link will only work if commit has been pushed
          ;;          to GitHub.
          (printf (str "Hash of last Git commit = " git-hash "\n"))
          (printf (str "GitHub link = https://github.com/lspector/Clojush/commit/"
                       git-hash
                       "\n"))
          (flush))))
    (catch Exception e
           (printf "Hash of last Git commit = unavailable\n")
           (printf "GitHub link = unavailable\n")
           (flush))))

(defn final-report
  "Prints the final report of a PushGP run if the run is successful."
  [generation best
   {:keys [error-function final-report-simplifications print-ancestors-of-solution]}]
  (printf "\n\nSUCCESS at generation %s\nSuccessful program: %s\nErrors: %s\nTotal error: %s\nHistory: %s\nSize: %s\n\n"
          generation (pr-str (not-lazy (:program best))) (not-lazy (:errors best)) (:total-error best) 
          (not-lazy (:history best)) (count-points (:program best)))
  (when print-ancestors-of-solution
    (printf "\nAncestors of solution:\n")
    (prn (:ancestors best)))
  (auto-simplify best error-function final-report-simplifications true 500))
