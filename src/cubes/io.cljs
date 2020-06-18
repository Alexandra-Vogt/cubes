(ns cubes.io
  "This obtains user input. It only contains one function though may contain
  more in the future."
  (:require [quil.core :as q :include-macros true]))


(defn get-input-horizontal []
  (cond (and (= (q/key-as-keyword) :ArrowLeft) (q/key-pressed?)) -0.5
        (and (= (q/key-as-keyword) :ArrowRight) (q/key-pressed?)) 0.5
        :else 0))
