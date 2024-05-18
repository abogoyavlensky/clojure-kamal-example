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
  (let [movies @(re-frame/subscribe [::subs/movies-list-items])]
    [:section.bg-gray-50
     [:div.mx-auto.max-w-screen-xl.px-4.py-32.lg:flex.lg:h-screen.lg:items-center
      [:div.mx-auto.max-w-xl.text-center
       [:h1.text-3xl.font-extrabold.sm:text-5xl "Welcome to your"
        [:strong.font-extrabold.text-emerald-700.sm:block "Clojure full-stack application!"]]
       [:p.mt-4.sm:text-xl/relaxed "Get started by editing `src/cljs/ui/views.cljs`."]
       ; TODO: keep just title and list of movies
       [:div.mt-8.flex.flex-wrap.gap-4
        [:h2.text-3xl "Movies"]
        [:div.overflow-x-auto
         [:table.min-w-full.divide-y-2.divide-gray-200.bg-white.text-sm
          [:thead.ltr:text-left.rtl:text-right
           [:tr
            [:th.whitespace-nowrap.px-4.py-2.font-medium.text-gray-900 "Title"]
            [:th.whitespace-nowrap.px-4.py-2.font-medium.text-gray-900 "Year"]
            [:th.whitespace-nowrap.px-4.py-2.font-medium.text-gray-900 "Director"]]]
          [:tbody.divide-y.divide-gray-200
           (for [item movies]
             ^{:key (:id item)}
             [:tr
              [:td.whitespace-nowrap.px-4.py-2.font-medium.text-gray-900 (:title item)]
              [:td.whitespace-nowrap.px-4.py-2.text-gray-700 (:year item)]
              [:td.whitespace-nowrap.px-4.py-2.text-gray-700 (:director item)]])]]]]]]]))
