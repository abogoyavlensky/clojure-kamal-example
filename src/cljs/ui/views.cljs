(ns ui.views
  (:require [re-frame.core :as re-frame]
            [ui.subs :as subs]
            [ui.router :as-alias ui-router]))

(defn router-component
  "Component for routing ui navigation."
  [{:keys [router]}]
  (let [current-route @(re-frame/subscribe [::subs/current-route])
        view (-> current-route :data :view)]
    [view {:router router
           :current-route current-route}]))

(defn home-page
  "Render home page."
  [_]
  [:h1 "Movies"])
