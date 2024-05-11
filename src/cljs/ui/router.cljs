(ns ui.router
  (:require [re-frame.core :as re-frame]
            [reitit.coercion.malli :as reitit-malli]
            [reitit.frontend :as reitit-front]
            [reitit.frontend.easy :as reitit-easy]
            [ui.events :as events]
            [ui.views :as views]))


(def ^:private routes
  ["/"
   [""
    {:name ::home
     :view views/home-page
     :controllers [{:start (fn [& _]
                             (re-frame/dispatch [::events/get-movies-list]))}]}]])


(def router
  "Router for frontend pages."
  (reitit-front/router
    routes
    {:data {:coercion reitit-malli/coercion}}))


(defn- on-navigate
  [new-match]
  (when new-match
    (re-frame/dispatch [::events/navigate new-match])))


(defn init-routes!
  "Initial setup router."
  []
  (reitit-easy/start! router on-navigate {:use-fragment false}))
