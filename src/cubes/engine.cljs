(ns cubes.engine
  "The engine behind the game. It provides utility functions that are used in the
  state update function. I will be rewriting these as I reactor the code-base of
  the game."
  (:require [quil.core :as q :include-macros true]))

(defn entity-bounds
  "Returns the rectangle representing the bounds of a size 20 square entity."
  [{:keys [:x :y]}]
  [(- x 20) x (- y 20) y])

(defn intersect?
  "Whether the two given rectangles intersect."
  [[axmin axmax aymin aymax] [bxmin bxmax bymin bymax]]
  (and (>= aymax bymin) (<= aymin bymax) (>= axmax bxmin) (<= axmin bxmax)))

(defn player-alive?
  "Returns whether the player has stayed clear of the walls and enemies."
  [player min-x max-x min-y max-y enemies]
  (and (intersect? [(:x player) (:x player) (:y player) (:y player)] [min-x max-x min-y max-y])
       (not-any? (fn [enemy] (intersect? (entity-bounds player) (entity-bounds enemy))) enemies)))

(defn gc-entities
  "Filters out out of bounds entities."
  [max-y entities]
  (filter (fn [ent]
            (let [ent-y (:y ent)]
              (<= ent-y (+ max-y 30))))
          entities))

(defn update-point-cube-pos
  "Updates the position of the point cubes."
  [player max-y point-cubes speed]
  (->> point-cubes
       (filter (fn [point] (not (intersect? (entity-bounds player) (entity-bounds point)))))
       (gc-entities max-y)
       (map (fn [point]
              {:x (:x point)
               :y (+ (:y point) (* (:speed-mul point) speed))
               :speed-mul (:speed-mul point)}))))

(defn update-enemy-pos
  "Updates the positions of each enemy."
  [max-y enemies speed]
  (->> enemies
       (gc-entities max-y)
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
      (concat (update-enemy-pos max-y enemies speed)
              (map (fn []
                     {:x (q/random min-x max-x)
                      :y (- min-y 20)
                      :speed-mul (q/random 1.5 0.5)})
                   (repeat (max 1 (int (/ 1 spawn-freq))) 1)))
      (update-enemy-pos max-y enemies speed))))

(defn update-score
  "Updates the score of the player."
  [player score point-cubes]
  (reduce (fn [acc point]
            (if (intersect? (entity-bounds player) (entity-bounds point))
              (inc acc)
              (+ acc 0))) score point-cubes))

(defn gen-point-cubes
  "This calculates when the enemies should be spawned in the game.
  the spawn frequency calculation computes the frequency with which
  enemies will spawn."
  [player min-x max-x min-y max-y point-cubes speed time]
  (if (and (= 0 (mod time (int (/ 128 (/ (q/width) 700)))))
           (< 0.4 (max speed (- speed))))
    (conj (update-point-cube-pos player max-y point-cubes speed)
          {:x (q/random min-x max-x)
           :y (- min-y 20)
           :speed-mul (q/random 1.5 0.5)})
    (update-point-cube-pos player max-y point-cubes speed)))

(defn update-player
  "This updates the player state."
  [player-speed-x player-speed-y player-x player-y]
   {:x (+ player-x player-speed-x)
    :y (+ player-y player-speed-y)
    :speed-x player-speed-x
    :speed-y player-speed-y})
