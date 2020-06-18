(ns cubes.io
  "This obtains user input. It only contains one function though may contain
  more in the future."
  (:require [quil.core :as q :include-macros true]))


(defn get-input-x []
  (cond (and (= (q/key-as-keyword) :ArrowLeft) (q/key-pressed?)) -0.5
        (and (= (q/key-as-keyword) :ArrowRight) (q/key-pressed?)) 0.5
        :else 0))


(defn get-input-y []
  (cond (and (= (q/key-as-keyword) :ArrowUp) (q/key-pressed?)) -0.5
        (and (= (q/key-as-keyword) :ArrowDown) (q/key-pressed?)) 0.5
        :else 0))
