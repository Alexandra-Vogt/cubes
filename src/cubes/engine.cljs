(ns cubes.engine)


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
