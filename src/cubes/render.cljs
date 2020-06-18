(ns cubes.render
  "The rendering function. It renders the data based upon the stage of the game,
  "
  (:require [quil.core :as q :include-macros true]))


;; The player character, a cube trying to dodge the cubes sent at it.
(defn render-player [x y]
  (q/fill 255 255 0)
  (q/rect x y 20 20)
  (q/fill 0)
  (q/rect (+ 2.5 x) (+ 2.5 y) 15 15))

;; The enemy, a cube trying to dodge the cubes sent at it.
(defn render-enemy [x y]
  (q/fill 255 0 0)
  (q/rect x y 20 20)
  (q/fill 0)
  (q/rect (+ 2.5 x) (+ 2.5 y) 15 15))

;; The player character, a cube trying to dodge the cubes sent at it.
(defn render-point-cube [x y]
  (q/fill 0 0 255)
  (q/rect x y 20 20)
  (q/fill 0)
  (q/rect (+ 2.5 x) (+ 2.5 y) 15 15))


(defn render-entities [state]
  (q/text (str (:spawned? state)) 100 100)

  (q/with-translation [(/ (q/width) 2)
                       (/ (q/height) 2)]
    ;; Draw enemies and points
    (dorun (map (fn [point-cube]
                  (render-point-cube (:x point-cube) (:y point-cube)))
                (:point-cubes state)))
    (dorun (map (fn [enemy]
                  (render-enemy (:x enemy) (:y enemy)))
                (:enemies state)))

    ;; Draw the player
    (render-player (:x (:player state))
                   (:y (:player state)))))

(defn render-state [state]
  (q/background 0)
  (render-entities state)
  (q/fill 255 255 0)
  (q/text (str (:text state)
               (if (= (int (mod (q/seconds) 2)) 0)
                 "█"
                 "")) 10 20))
