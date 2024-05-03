(ns ui.db)


(def default-db
  "Main state for the app."
  {:current-route nil
   :movies-list {:loading? false
                 :errors nil
                 :items nil}})
