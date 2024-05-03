(ns ui.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [reitit.frontend.controllers :as rfc]
            [reitit.frontend.easy :as reitit-easy]
            ; import http-fx to register http-xhrio fx
            [day8.re-frame.http-fx]
            [common.api-routes :as api-routes]
            [ui.util.api-routes :as api-routes-util]
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


(re-frame/reg-fx
  :fx/push-state
  (fn [{:keys [route]}]
    (reitit-easy/push-state route)))


(re-frame/reg-event-fx
  ::get-movies-list
  (fn [{:keys [db]} [_ _]]
    {:db (assoc-in db [:movies-list :loading?] true)
     :http-xhrio {:method :get
                  :uri (api-routes-util/api-route-path ::api-routes/movies)
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::get-movies-list-success]
                  :on-failure [::get-movies-list-error]}}))


(re-frame/reg-event-db
  ::get-movies-list-error
  (fn [db [_ {:keys [_response]}]]
    (-> db
        (assoc :error-message "Server error")
        (assoc-in [:movies-list :loading?] false))))


(re-frame/reg-event-db
  ::get-movies-list-success
  (fn [db [_ movies-list]]
    (-> db
        (assoc-in [:movies-list :items] movies-list)
        (assoc-in [:movies-list :loading?] false))))
