(ns ui.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
  ::current-route
  (fn [db]
    (:current-route db)))


(re-frame/reg-sub
  ::movies-list-items
  (fn [db]
    (get-in db [:movies-list :items])))


(re-frame/reg-sub
  ::movies-list-loading?
  (fn [db]
    (get-in db [:movies-list :loading?])))
