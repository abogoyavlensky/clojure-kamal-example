(ns ui.events
  (:require [re-frame.core :as re-frame]
            [reitit.frontend.controllers :as rfc]
            [reitit.frontend.easy :as reitit-easy]
            ; import http-fx to register http-xhrio fx
            [day8.re-frame.http-fx]
            [ui.db :as db]))


(re-frame/reg-event-db
  ::initialize-db

  (fn [_ _]
    db/default-db))


(re-frame/reg-event-db
  ::navigate
  (fn [{:keys [current-route] :as db} [_ new-route]]
    (let [old-controllers (:controllers current-route)
          ; apply controllers for old route
          new-route* (assoc new-route :controllers (rfc/apply-controllers
                                                     old-controllers
                                                     new-route))]
      (assoc db :current-route new-route*))))


(re-frame/reg-fx :fx/push-state
  (fn [{:keys [route]}]
    (reitit-easy/push-state route)))
