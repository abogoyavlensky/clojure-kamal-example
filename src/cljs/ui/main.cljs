(ns ui.main
  #_{:clj-kondo/ignore [:unused-namespace]}
  (:require [reagent.dom.client :as reagent]
            [re-frame.core :as re-frame]
            [ui.views :as views]
            [ui.router :as router]
            [ui.events :as events]
            ; import for compiler
            [ui.subs]))


(defonce ^:private ROOT
  (reagent/create-root (.getElementById js/document "app")))


(defn render!
  "Render the page with initializing routes."
  []
  (re-frame/clear-subscription-cache!)
  (router/init-routes!)
  (reagent/render
    ROOT
    [views/router-component {:router router/router}]))


(defn init!
  "Render the whole app with default db value."
  []
  (re-frame/dispatch-sync [::events/initialize-db])
  (render!))
