(ns cubes.io
  (:require [quil.core :as q :include-macros true]))


(defn get-input-horizontal []
  (cond (and (= (q/key-as-keyword) :ArrowLeft) (q/key-pressed?)) -0.5
        (and (= (q/key-as-keyword) :ArrowRight) (q/key-pressed?)) 0.5
        :else 0))

(defn get-input-vertical []
  (cond (and (= (q/key-as-keyword) :ArrowUp) (q/key-pressed?)) 0.5
        (and (= (q/key-as-keyword) :ArrowDown) (q/key-pressed?)) -0.5
        :else 0))
