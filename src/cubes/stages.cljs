;;; Commentary:
;;
;;
;;
;;
;;; Code:
(ns cubes.stages
  (:require [cubes.io :as io]
            [cubes.engine :as engine]
            [quil.core :as q :include-macros true]))

(defn title-stage
  "The initial stage of the game where the title is displayed."
  [state]
  {:speed 0
   :distance 0
   :player {:x -20
            :y (:y (:player state))}
   :enemies []
   :point-cubes []
   :time 0
   :score 0
   :max-score 0
   :stage (if (q/key-pressed?)
            "game"
            "title")})

(defn game-stage
  "The function that executes during the game state."
  [state]
  (let [player-speed-x (+ (:speed-x (:player state)) (io/get-input-horizontal))
        player-speed-y (max 0 (min 16
                                   (+ (:speed-y (:player state)) (io/get-input-vertical))))
        player-x (+ (:x (:player state)) player-speed-x)
        player-y (:y (:player state))
        speed (+ (max player-speed-x (- player-speed-x)) player-speed-y)
        distance (:distance state)
        player-killed (engine/player-killed? state)
        point-cubes (if (and (= 0 (mod (:time state) 128))
                             (< 0.4 (max speed (- speed))))
                      (conj (engine/update-point-cube-pos state)
                            {:x (q/random -350 330)
                             :y -400
                             :speed-mul (q/random 1.5 0.5)})
                      (engine/update-point-cube-pos state))
        enemies (if (and (= 0 (mod (:time state)
                                   (max 1
                                        (int (/ 60 (inc (/ (+ 10 (:time state)) 500)))))))
                         (< 0.4 (max speed (- speed)))
                         (< 100 (:time state)))
                  (conj (engine/update-enemy-pos state)
                        {:x (q/random -350 330)
                         :y -400
                         :speed-mul (q/random 1.5 0.5)})
                  (engine/update-enemy-pos state))]
    {:speed speed
     :distance (+ distance (max speed (- speed)))
     :player {:x player-x
              :y player-y
              :speed-x player-speed-x
              :speed-y player-speed-y}
     :enemies enemies
     :point-cubes point-cubes
     :time (inc (:time state))
     :score (if (< (count point-cubes) (count (:point-cubes state)))
              (inc (:score state))
              (:score state))
     :max-score (:max-score state)
     :ignore-keypress (if player-killed
                        (q/key-pressed?)
                        false)
     :stage (if player-killed
              "score"
              "game")}))

(defn score-stage
  [state]
  (let [restart (if-not (:ignore-keypress state)
                  (q/key-pressed?)
                  false)
        max-score (max (:max-score state) (:score state))]
    {:ignore-keypress (if (q/key-pressed?)
                        (:ignore-keypress state)
                        false)
     :speed 0
     :player {:x (if restart
                   -20
                   (:x (:player state)))
              :y (:y (:player state))}
     :enemies (if restart
                []
                (:enemies state))
     :point-cubes (if restart
                    []
                    (:point-cubes state))
     :time (if restart
             0
             (:time state))
     :score (if restart
              0
              (:score state))
     :max-score max-score
     :stage (if restart
              "game"
              "score")}))

(defn update-stage-state [state]
  (condp = (:stage state)
        "title" (title-stage state)
        "game" (game-stage state)
        "score" (score-stage state)))
