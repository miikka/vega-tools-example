(set-env!
  :source-paths #{"src/cljs" "src/less"}
  :resource-paths #{"src/clj"}
  :dependencies '[[org.clojure/clojure    "1.9.0-alpha14"]
                  [org.clojure/clojurescript "1.9.293"]

                  [boot/core              "2.6.0"      :scope "test"]
                  [adzerk/boot-cljs       "1.7.228-2"  :scope "test"]
                  [adzerk/boot-cljs-repl  "0.3.3"      :scope "test"]
                  [com.cemerick/piggieback "0.2.1"     :scope "test"]
                  [weasel                 "0.7.0"      :scope "test"]
                  [org.clojure/tools.nrepl "0.2.12"    :scope "test"]
                  [adzerk/boot-reload     "0.4.13"     :scope "test"]
                  [deraen/boot-less       "0.6.0"      :scope "test"]
                  ;; For boot-less
                  [org.slf4j/slf4j-nop    "1.7.21"     :scope "test"]

                  ;; Backend
                  [http-kit "2.2.0"]
                  [org.clojure/tools.namespace "0.3.0-alpha3"]
                  [reloaded.repl "0.2.3"]
                  [com.stuartsierra/component "0.3.1"]
                  [metosin/ring-http-response "0.8.0"]
                  [ring "1.5.0"]
                  [compojure "1.5.1"]
                  [hiccup "1.0.5"]

                  ;; Frontend
                  [reagent "0.6.0"]
                  [binaryage/devtools "0.8.3"]
                  [metosin/vega-tools "0.2.0"]

                  ;; LESS
                  [org.webjars/bootstrap "3.3.7-1"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[deraen.boot-less      :refer [less]]
  '[backend.boot          :refer [start-app]]
  '[reloaded.repl         :refer [go reset start stop system]])

(task-options!
  pom {:project 'vega-tools-example
       :version "0.1.0-SNAPSHOT"
       :description "An example of using vega-tools."}
  aot {:namespace #{'backend.main}}
  jar {:main 'backend.main}
  cljs {:source-map true}
  less {:source-map true})

(deftask dev
  "Start the dev env..."
  [s speak           bool "Notify when build is done"
   p port       PORT int  "Port for web server"]
  (comp
    (watch)
    (reload :ids #{"js/main"})
    (less)
    ;; This starts a repl server with piggieback middleware
    (cljs-repl :ids #{"js/main"})
    (cljs :ids #{"js/main"})
    (start-app :port port)
    (if speak (boot.task.built-in/speak) identity)))

(deftask package
  "Build the package"
  []
  (comp
    (less :compression true)
    (cljs :optimizations :advanced
          :compiler-options {:preloads nil})
    (aot)
    (pom)
    (uber)
    (jar)
    (sift :include #{#".*\.jar"})
    (target)))
