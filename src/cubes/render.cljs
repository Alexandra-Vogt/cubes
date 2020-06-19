(ns cubes.render
  "The rendering function. It renders the data based upon the stage of the game."
  (:require [quil.core :as q :include-macros true]))

(defn render-square [[r g b] x y thickness size]
  (let [inner (- size (* thickness 2))]
    (q/fill r g b)
    (q/rect x y size size)
    (q/fill 0)
    (q/rect (+ thickness x) (+ thickness y) inner inner)))

;; The player character, a cube trying to dodge the cubes sent at it.
(defn render-player [{:keys [x y]}] (render-square [255 255 0] x y 2.5 20))

;; The enemy, a cube hoping to collide with another cube.
(defn render-enemy [{:keys [x y]}] (render-square [255 0 0] x y 2.5 20))

;; The objective, a cube waiting to see whether a cube will collect it.
(defn render-point-cube [{:keys [x y]}] (render-square [0 0 255] x y 2.5 20))

(defn render-entities [state]
  (q/text (str (:spawned? state)) 100 100)

  (q/with-translation [(/ (q/width) 2)
                       (/ (q/height) 2)]
    ;; Draw enemies and points
    (dorun (map render-point-cube (:point-cubes state)))
    (dorun (map render-enemy (:enemies state)))

    ;; Draw the player
    (render-player (:player state))))

(defn render-state [state]
  (q/background 0)
  (render-entities state)
  (q/fill 255 255 0)
  (q/text (str (:text state)
               (if (= (int (mod (q/seconds) 2)) 0)
                 "█"
                 "")) 10 20))
