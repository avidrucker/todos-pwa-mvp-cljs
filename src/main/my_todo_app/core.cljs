(ns my-todo-app.core
  (:require [reagent.core :as reagent]
            [reagent.dom :as reagent-dom]))

(defn save-todos [todos]
  (let [todos-js (js/JSON.stringify (clj->js {:todos todos}))]
    ;; (println "Saving todos:" todos-js) ; Debugging line
    (js/localStorage.setItem "todos" todos-js)))


(defn load-todos []
  (try
    (let [saved-todos (js/localStorage.getItem "todos")
      ;; (println "Saved todos JSON:" saved-todos) ; Debugging line
      loaded-todos (-> saved-todos
                             js/JSON.parse
                             (js->clj :keywordize-keys true)
                             :todos)] ; Extract :todos key
        ;; (println "Loaded todos:" loaded-todos) ; Debugging line
        loaded-todos)
    (catch :default e
      (js/console.error "Error loading todos" e)
      [])))


(defonce app-state
  (reagent/atom
   {:todos (load-todos)}))

(add-watch app-state :save-todos
  (fn [_ _ _ new-state]
    (save-todos (:todos new-state))))

(defn todo-item [todo]
  [:li
   [:input {:type "checkbox" :checked (:done todo)
            :on-change #(swap! app-state update-in [:todos (:id todo)] assoc :done (not (:done todo)))}]
   [:span (:text todo)]
   [:button {:on-click #(swap! app-state update :todos (fn [todos] (remove (fn [x] (= (:id todo) (:id x))) todos)))} "Delete"]])

(defn todo-list []
  [:ul (for [todo (:todos @app-state)] ^{:key (:id todo)} [todo-item todo])])

(defn add-todo [text]
  (swap! app-state update :todos conj {:id (random-uuid) :text text :done false}))

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
   [todo-input]
   [todo-list]])

(defn init []
  (reagent-dom/render [main-panel] (js/document.getElementById "app")))

