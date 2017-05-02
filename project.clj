(defproject omn1 "0.1.0-SNAPSHOT"
  :description "Om next tutorial"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.456"]
                 [org.omcljs/om "1.0.0-alpha48-SNAPSHOT"]
                 [com.datomic/datomic-free "0.9.5344" :exclusions [joda-time org.slf4j/slf4j-nop]]
                 [com.taoensso/timbre "4.8.0"]
                 [binaryage/devtools "0.9.2"]
                 [fentontravers/websocket-client "0.4.8"]]
  :source-paths ["src/clj" "src/cljc"]
  :clean-targets ^{:protect false} ["target" "resources/public/js"]
  :target-path "target/%s"
  :plugins [[lein-figwheel "0.5.9"]
            [lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [com.datomic/datomic-free "0.9.5344" :exclusions [joda-time org.slf4j/slf4j-nop]]
            [com.billpiel/sayid "0.0.10"]]
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs" "src/cljc" "dev"]
                :figwheel true
                :compiler {:main omn1.webpage
                           :asset-path "js"
                           :output-to "resources/public/js/main.js"
                           :output-dir "resources/public/js"
                           :verbose true
                           :preloads [devtools.preload]
                           :source-map-timestamp true}}]}
  :profiles {:dev {:dependencies [;; [org.clojure/tools.namespace "0.2.11"]
                                  [figwheel-sidecar "0.5.9"]                                   
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["src/cljc" "src/cljs"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :uberjar {:aot :all}})
