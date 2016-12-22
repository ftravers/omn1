(defproject omn1 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.omcljs/om "1.0.0-alpha48-SNAPSHOT"]]
  :source-paths ["src/clj"]
  :clean-targets ^{:protect false} ["target" "resources/public/js"]
  :target-path "target/%s"
  :plugins [[lein-figwheel "0.5.4-7"]
            [lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [cider/cider-nrepl "0.15.0-SNAPSHOT"]
            [com.billpiel/sayid "0.0.10"]]
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs" "src/cljc"]
                :figwheel true
                :compiler {:main omn1.webpage
                           :asset-path "js"
                           :output-to "resources/public/js/main.js"
                           :output-dir "resources/public/js"
                           :verbose true
                           :source-map-timestamp true}}]}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [figwheel-sidecar "0.5.4-7"]                                   
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["src/cljc" "src/cljs"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :uberjar {:aot :all}})
