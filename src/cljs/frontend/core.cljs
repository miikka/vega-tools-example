(ns frontend.core
  (:require [reagent.core :as r]))

(defonce app-state (r/atom {}))

(defn main []
  [:div
   [:h1 "Hello"]])

(defn start! []
  (js/console.log "Starting the app")
  (r/render-component [main] (js/document.getElementById "app")))

;; When this namespace is (re)loaded the Reagent app is mounted to DOM
(start!)
