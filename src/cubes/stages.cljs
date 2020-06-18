(ns cubes.stages
  "A series of different functions that serves to manage the state of the game,
  splitting it into a number of different \"stages\" that are synchronized with
  the rendering function. Each stage manages the state of the code and changes
  between the different stages. Future games will not use a stage system and
  instead endeavor for a more simple mono-stage ECS system."
  (:require [cubes.io :as io]
            [cubes.engine :as engine]
            [quil.core :as q :include-macros true]))

(defn title-stage
  "The initial stage of the game where the title is displayed."
  [state]
  {:speed 0
   :distance 0
   :player {:x -20
            :y 20}
   :enemies []
   :point-cubes []
   :time 0
   :score 0
   :max-score 0
   :stage (if (q/key-pressed?)
            "game"
            "title")})

(defn game-stage
  "The function that executes during the game stage."
  [state]
  (let [enemies (:enemies state)
        player-x (:x (:player state))
        player-y (:y (:player state))
        min-x (- (/ (q/width) 2) 20)
        max-x (- (/ (q/width) 2))
        min-y (- (/ (q/height) 2) 20)
        max-y (- (/ (q/height) 2))
        time (:time state)
        score (:score state)
        point-cubes (:point-cubes state)
        player-speed-x (+ (:speed-x (:player state)) (io/get-input-x))
        player-speed-y (+ (:speed-y (:player state)) (io/get-input-y))
        speed (q/sqrt (+ (q/pow player-speed-x 2) (q/pow player-speed-y 2)))
        distance (:distance state)
        player-killed (engine/player-killed? player-x player-y min-x max-x min-y max-y enemies)]
    {:speed speed
     :distance (+ distance speed)
     :player (engine/update-player player-speed-x player-speed-y player-x player-y)
     :enemies (engine/gen-enemies min-x max-x min-y max-y time speed enemies)
     :point-cubes (engine/gen-point-cubes player-x player-y min-x max-x min-y max-y point-cubes speed time)
     :time (inc time)
     :score (engine/update-score player-x player-y score point-cubes)
     :max-score (:max-score state)
     :ignore-keypress (if player-killed
                        (q/key-pressed?)
                        false)
     :screen-time (if player-killed
                    0)
     :stage (if player-killed
              "score"
              "game")}))

(defn score-stage
  [state]
  (let [restart (if-not (:ignore-keypress state)
                  (q/key-pressed?)
                  false)
        max-score (max (:max-score state) (:score state))]
    {:screen-time (inc (:screen-time state))
     :ignore-keypress (if (or (q/key-pressed?) (> 30 (:screen-time state)))
                        (:ignore-keypress state)
                        false)
     :speed 0
     :player {:x (if restart
                   -20
                   (:x (:player state)))
              :y (if restart
                   20
                   (:y (:player state)))}
     :enemies (if restart
                []
                (:enemies state))
     :point-cubes (if restart
                    []
                    (:point-cubes state))
     :time (if restart
             0
             (:time state))
     :distance (if restart
                 0
                 (:distance state))
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
