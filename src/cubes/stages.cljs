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
  (merge
   state
   (when (q/key-pressed?)
     {:stage "game"})))

(defn game-stage
  "The function that executes during the game stage."
  [state]
  (merge
   state
   (let [enemies (:enemies state)
         player-x (:x (:player state))
         player-y (:y (:player state))
         player-speed-x (+ (:speed-x (:player state)) (io/get-x-accel))
         player-speed-y (+ (:speed-y (:player state)) (io/get-y-accel))
         player (:player state)
         min-x (- (/ (q/width) 2))
         max-x (- (/ (q/width) 2) 20)
         min-y (- (/ (q/height) 2))
         max-y (- (/ (q/height) 2) 20)
         time (:time state)
         score (:score state)
         point-cubes (:point-cubes state)
         speed (q/sqrt (+ (q/pow player-speed-x 2) (q/pow player-speed-y 2)))
         distance (:distance state)]
     (if (engine/player-alive? player min-x max-x min-y max-y enemies)
       {:text (str "SCORE: " (:score state) "\n"
               "FRAME: " (:time state) "\n"
               "SPEED: " (.toFixed (:speed state) 1))
        :speed speed
        :distance (+ distance speed)
        :player (engine/update-player player-speed-x player-speed-y player-x player-y)
        :enemies (engine/gen-enemies min-x max-x min-y max-y time speed enemies)
        :point-cubes (engine/gen-point-cubes player min-x max-x min-y max-y point-cubes speed time)
        :time (inc time)
        :score (engine/update-score player score point-cubes)
        :max-score (:max-score state)}
       {:ignore-keypress true
        :screen-time 0
        :stage "score"}))))

(defn score-stage
  [state]
  (merge
   state
   (let [restart (if-not (:ignore-keypress state)
                   (q/key-pressed?)
                   false)
         max-score (max (:max-score state) (:score state))]
     (if-not restart
       {:text (str "GAME OVER\n"
               "FRAMES:    " (:time state) "\n"
               "DISTANCE:  " (int (:distance state)) "\n"
               "MAX SCORE: " (:max-score state) "\n"
               "SCORE:     " (:score state)
               (when (< 60 (:screen-time state))
                     "\n\nPRESS ANY KEY TO CONTINUE..."))
        :screen-time (inc (:screen-time state))
        :ignore-keypress (if (or (q/key-pressed?) (> 30 (:screen-time state)))
                          (:ignore-keypress state)
                          false)
        :speed 0
        :max-score max-score}
       {:player {:x -20
                 :y 20}
        :enemies []
        :point-cubes []
        :time 0
        :distance 0
        :score 0
        :stage "game"}))))

(defn update-stage-state [state]
  (condp = (:stage state)
        "title" (title-stage state)
        "game" (game-stage state)
        "score" (score-stage state)))
