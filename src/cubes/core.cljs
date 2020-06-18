(ns cubes.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [cubes.render :as render]
            [cubes.stages :as stages]))

(defn setup []
  (q/frame-rate 30)
  {:text (str "CONTROLS:\n"
               "THE ARROW KEYS CONTROL ACCELERATION ACCELERATION\n"
               "THE CUBES MOVE AT THE OUTPUT OF A FUNCTION OF YOUR SPEED\n"
               "\n"
               "RULES:\n"
               "HITTING RED SQUARES ENDS THE GAME\n"
               "HITTING BLUE SQUARES INCREMENTS THE SCORE COUNTER\n"
               "THE LONGER YOU PLAY THE MORE FREQUENTLY RED CUBES SPAWN\n"
               "\n"
               "GOOD LUCK!\n"
               "PRESS ANY KEY TO CONTINUE...")
   :speed 0
   :distance 0
   :player {:x -20
               :y 20}
   :enemies []
   :point-cubes []
   :time 0
   :score 0
   :max-score 0
   :stage "title"})

; this function is called in index.html
(defn ^:export run-sketch []
  (let [width (- (.-innerWidth js/window) 15)
        height (- (.-innerHeight js/window) 20)]
    (q/defsketch cubes
     :host "cubes"
     :size [width height]
    ; setup function called only once, during sketch initialization.
     :setup setup
    ; update-state is called on each iteration before draw-state.
     :update stages/update-stage-state
     :draw render/render-state
    ; This sketch uses functional-mode middleware.
    ; Check quil wiki for more info about middlewares and particularly
    ; fun-mode.
     :middleware [m/fun-mode])))

; uncomment this line to reset the sketch:
; (run-sketch)
