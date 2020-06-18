(ns cubes.engine
  "The engine behind the game. It provides utility functions that are used in the
  state update function. I will be rewriting these as I reactor the code-base of
  the game."
  (:require [quil.core :as q :include-macros true]))


(defn player-killed?
  "Checks if the player has collided with the walls or an enemy.
  Returns true if yes, false if no."
  [state]
  (let [player-x (:x (:player state))
        player-y (:y (:player state))
        enemies (:enemies state)]
    (or (>= player-x 330)
        (<= player-x -350)
        (->> enemies
             (map (fn [enemy]
                    (let [enemy-x (:x enemy)
                          enemy-y (:y enemy)]
                      (and  (>= player-y (- enemy-y 20))
                            (<= (- player-y 20) enemy-y)
                            (>= player-x (- enemy-x 20))
                            (<= (- player-x 20) enemy-x)))))
             (apply max)))))

(defn update-point-cube-pos
  "Checks if the player has collided with a point cube. Returns
  true if yes, false if no."
  [state]
  (let [player-x (:x (:player state))
        player-y (:y (:player state))
        point-cubes (:point-cubes state)
        speed (:speed state)]
    (->> point-cubes
         (filter (fn [point]
                   (let [point-x (:x point)
                         point-y (:y point)]
                     (not (and  (>= player-y (- point-y 20))
                                (<= (- player-y 20) point-y)
                                (>= player-x (- point-x 20))
                                (<= (- player-x 20) point-x))))))
         (map (fn [point]
                {:x (:x point)
                 :y (+ (:y point) (* (:speed-mul point) speed))
                 :speed-mul (:speed-mul point)})))))

(defn update-enemy-pos [state]
  (let [speed (:speed state)]
    (->> (:enemies state)
         (map (fn [enemy]
                {:x (:x enemy)
                 :y (+ (:y enemy) (* (:speed-mul enemy) speed))
                 :speed-mul (:speed-mul enemy)})))))

(defn gen-enemies [state]
  (let [spawn-freq (max 1 (int (/ 60 (inc (/ (+ 10 (:time state)) 500)))))
        speed (:speed state)]
    (if (and (= 0 (mod (:time state) spawn-freq))
             (< 0.4 (max speed (- speed)))
             (< 100 (:time state)))
      (conj (update-enemy-pos state)
            {:x (q/random -350 330)
             :y -400
             :speed-mul (q/random 1.5 0.5)})
      (update-enemy-pos state))))

(defn gen-point-cubes
  "This calculates when the enemies should be spawned in the game.
  the spawn frequency calculation computes the frequency with which
  enemies will spawn."
  [state]
  (let [speed (:speed state)]
    (if (and (= 0 (mod (:time state) 128))
             (< 0.4 (max speed (- speed))))
      (conj (update-point-cube-pos state)
            {:x (q/random -350 330)
             :y -400
             :speed-mul (q/random 1.5 0.5)})
      (update-point-cube-pos state))))
