(ns my-todo-app.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as reagent-dom]))

(defn save-cntr [t-index]
  (let [t-index-js (js/JSON.stringify (clj->js {:t-index t-index}))]
    (js/localStorage.setItem "t-index" t-index-js)))

(defn save-todos [todos]
  (let [todos-js (js/JSON.stringify (clj->js {:todos todos}))]
    (js/localStorage.setItem "todos" todos-js)))

(defn load-cntr []
  (try (let [saved-cntr (js/localStorage.getItem "t-index")
             loaded-cntr (-> saved-cntr
                             js/JSON.parse
                             (js->clj :keywordize-keys true)
                             :t-index)]
         (if loaded-cntr loaded-cntr 0)) (catch :default e
                          (js/console.error "Error loading counter" e)
                          0)))

(defn load-todos []
  (try
    (let [saved-todos (js/localStorage.getItem "todos")
          loaded-todos (-> saved-todos
                           js/JSON.parse
                           (js->clj :keywordize-keys true)
                           :todos)] ; Extract :todos key
      loaded-todos)
    (catch :default e
      (js/console.error "Error loading todos" e)
      [])))

(defonce app-state
  (reagent/atom
   {:todos (load-todos)
    :t-index (load-cntr)})) ; Initialize t-index counter


(add-watch app-state :save-todos
  (fn [_ _ _ new-state]
    (save-todos (:todos new-state))))

(add-watch app-state :save-cntr
  (fn [_ _ _ new-state]
    (save-cntr (:t-index new-state))))

#_(defn handle-toggle [] )

(defn todo-item [todo]
  [:li
   ;; [:span "T-Index: " (:t-index todo) " - "] ; Display t-index

   [:input {:type "checkbox" :checked (:done todo)
            :on-change #(swap! app-state update :todos
                               (fn [todos]
                                 (map (fn [t]
                                        (if (= (:id t) (:id todo))
                                          (assoc t :done (not (:done todo)))
                                          t))
                                      todos)))
            }]
   
   [:span (:text todo)]
   [:button {:on-click #(swap! app-state update :todos (fn [todos] (remove (fn [x] (= (:id todo) (:id x))) todos)))} "Delete"]])

(defn cntr []
  [:p (str "The counter is at " (:t-index @app-state))])

(defn todo-list []
  (let [sorted-todos (sort-by :t-index (:todos @app-state))]
    [:ul (for [todo sorted-todos] ^{:key (:id todo)} [todo-item todo])]))

(defn add-todo [text]
  (swap! app-state
         (fn [state]
           (let [current-index (:t-index state)]
             (-> state
                 (update :todos conj
                         {:id (random-uuid)
                          :text text
                          :done false
                          :t-index current-index})
                 (assoc :t-index (inc current-index)))))))

(defn todo-input []
  (let [input-ref (reagent/atom nil)]
    (fn []
      [:div
       [:input {:type "text" :placeholder "New TODO" :ref #(reset! input-ref %)}]
       [:button {:on-click #(when-let [input-val (-> @input-ref .-value str)] ; Ensure input-val is a string
                             (add-todo input-val)
                             (set! (.-value @input-ref) ""))} "Add"]])))

(defn main-panel []
  [:div
   [:h1 "To-Do List"]
   ;; [cntr]
   [todo-input]
   [todo-list]])

(defn init []
  (reagent-dom/render [main-panel] (js/document.getElementById "app")))

