(ns frontend.core
  (:require [cljs.reader :as reader]
            [clojure.pprint :as pprint]
            [promesa.core :as p]
            [reagent.core :as r]
            [vega-tools.core :as vega-tools]))

;; Copied from <https://github.com/vega/vega/blob/76ab79f711b80840e34484974c9b717f584e8f7c/examples/bar.json>
(def initial-spec
  {:width  400
   :height 200
   :padding {:top 10, :left 30, :bottom 30, :right 10}

   :data
   [{:name "table"
     :values [{:x 1, :y 28} {:x 2, :y 55}
              {:x 3, :y 43} {:x 4, :y 91}
              {:x 5, :y 81} {:x 6, :y 53}
              {:x 7, :y 19} {:x 8, :y 87}
              {:x 9, :y 52} {:x 10, :y 48}
              {:x 11, :y 24} {:x 12, :y 49}
              {:x 13, :y 87} {:x 14, :y 66}
              {:x 15, :y 17} {:x 16, :y 27}
              {:x 17, :y 68} {:x 18, :y 16}
              {:x 19, :y 49} {:x 20, :y 15}]}]

   :scales
   [{:name "x"
     :type "ordinal"
     :range "width"
     :domain {:data "table", :field "x"}}
    {:name "y"
     :type "linear"
     :range "height"
     :domain {:data "table", :field "y"}, :nice true}]

   :axes
   [{:type "x", :scale "x"}
    {:type "y", :scale "y"}]

   :marks
   [{:type "rect", :from {:data "table"},
     :properties {:enter {:x {:scale "x", :field "x"}
                          :width {:scale "x", :band true, :offset -1}
                          :y {:scale "y", :field "y"}
                          :y2 {:scale "y", :value 0}}
                  :update {:fill {:value "steelblue"}}
                  :hover {:fill {:value "red"}}}}]})

(defonce app-state (r/atom {:input (with-out-str (pprint/pprint initial-spec))}))

(defn vega-chart [{:keys [chart]}]
  (r/create-class
   {:display-name "vega-chart"
    :reagent-render (fn [] [:div])
    :component-did-mount
    (fn [this]
      (.update (chart {:el (r/dom-node this)})))}))

(defn parse-input []
  (let [{:keys [input]} @app-state]
    (swap! app-state assoc :chart nil)
    (-> (reader/read-string input)
        (vega-tools/validate-and-parse)
        (p/catch #(js/alert (str "Unable to parse spec:\n\n" %)))
        (p/then #(swap! app-state assoc :chart %)))))

(defn main []
  (let [{:keys [input chart]} @app-state]
    [:div
     [:h1 "vega-tools example"]
     [:div.container-fluid
      [:div.editor.col-md-6
       [:button {:on-click #(parse-input)} "Parse"] [:br]
       [:textarea.spec-input
        {:default-value input
         :on-change #(swap! app-state assoc :input (-> % .-target .-value))}]]
      [:div.col-md-6
       (if chart
         [vega-chart {:chart chart}]
         "Processing...")]]]))

(defn start! []
  (js/console.log "Starting the app")
  (parse-input)
  (r/render-component [main] (js/document.getElementById "app")))

;; When this namespace is (re)loaded the Reagent app is mounted to DOM
(start!)
