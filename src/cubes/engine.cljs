(ns cubes.engine
  "The engine behind the game. It provides utility functions that are used in the
  state update function. I will be rewriting these as I reactor the code-base of
  the game."
  (:require [quil.core :as q :include-macros true]))


(defn player-killed?
  "Checks if the player has collided with the walls or an enemy.
  Returns true if yes, false if no."
  [player-x player-y min-x max-x min-y max-y enemies]
  (or (>= player-x min-x)
      (<= player-x max-x)
      (>= player-y min-y)
      (<= player-y max-y)
      (->> enemies
           (map (fn [enemy]
                  (let [enemy-x (:x enemy)
                        enemy-y (:y enemy)]
                    (and  (>= player-y (- enemy-y 20))
                          (<= (- player-y 20) enemy-y)
                          (>= player-x (- enemy-x 20))
                          (<= (- player-x 20) enemy-x)))))
           (apply max))))

(defn gc-entities
  "Filters out out of bounds entities."
  [min-y entities]
  (filter (fn [ent]
            (let [ent-y (:y ent)]
              (>= (+ min-y 30) ent-y)))
          entities))

(defn update-point-cube-pos
  "Updates the position of the point cubes."
  [player-x player-y min-y point-cubes speed]
  (->> point-cubes
       (filter (fn [point]
                 (let [point-x (:x point)
                       point-y (:y point)]
                   (not (and  (>= player-y (- point-y 20))
                              (<= (- player-y 20) point-y)
                              (>= player-x (- point-x 20))
                              (<= (- player-x 20) point-x))))))
       (gc-entities min-y)
       (map (fn [point]
              {:x (:x point)
               :y (+ (:y point) (* (:speed-mul point) speed))
               :speed-mul (:speed-mul point)}))))

(defn update-enemy-pos
  "Updates the positions of each enemy."
  [min-y enemies speed]
  (->> enemies
       (gc-entities min-y)
       (map (fn [enemy]
              {:x (:x enemy)
               :y (+ (:y enemy) (* (:speed-mul enemy) speed))
               :speed-mul (:speed-mul enemy)}))))

(defn gen-enemies
  "Generates enemies and updates their positions."
  [min-x max-x min-y max-y time speed enemies]
  (let [spawn-freq (/ 60 (* (inc (/ (+ 10 time) 500))
                            (/ (q/width) 700)))]
    (if (and (= 0 (mod time (max 1 (int spawn-freq))))
             (< 0.4 (max speed (- speed)))
             (< 100 time))
      (concat (update-enemy-pos min-y enemies speed)
              (map (fn []
                     {:x (q/random min-x max-x)
                      :y (- max-y 20)
                      :speed-mul (q/random 1.5 0.5)})
                   (repeat (max 1 (int (/ 1 spawn-freq))) 1)))
      (update-enemy-pos min-y enemies speed))))

(defn update-score
  "Updates the score of the player."
  [player-x player-y score point-cubes]
  (reduce (fn [acc point]
            (let [point-x (:x point)
                  point-y (:y point)]
              (if (and  (>= player-y (- point-y 20))
                        (<= (- player-y 20) point-y)
                        (>= player-x (- point-x 20))
                        (<= (- player-x 20) point-x))
                (inc acc)
                (+ acc 0)))) score point-cubes))

(defn gen-point-cubes
  "This calculates when the enemies should be spawned in the game.
  the spawn frequency calculation computes the frequency with which
  enemies will spawn."
  [player-x player-y min-x max-x min-y max-y point-cubes speed time]
  (if (and (= 0 (mod time (int (/ 128 (/ (q/width) 700)))))
           (< 0.4 (max speed (- speed))))
    (conj (update-point-cube-pos player-x player-y min-y point-cubes speed)
          {:x (q/random min-x max-x)
           :y (- max-y 20)
           :speed-mul (q/random 1.5 0.5)})
    (update-point-cube-pos player-x player-y min-y point-cubes speed)))

(defn update-player
  "This updates the player state."
  [player-speed-x player-speed-y player-x player-y]
   {:x (+ player-x player-speed-x)
    :y (+ player-y player-speed-y)
    :speed-x player-speed-x
    :speed-y player-speed-y})
