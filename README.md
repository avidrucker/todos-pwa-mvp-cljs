# Todos-PWA-MVP-cljs

## How to Run This App

1. Clone this repo: `git clone git@github.com:avidrucker/todos-pwa-mvp-cljs.git`
2. Build the project: `npm install`
3. Run the app: `npx shadow-cljs watch app`
4. Navigate in your browser to http://localhost:8080/

## How This App Was Built

This app was bootstrapped by running `npx create-cljs-project my-todo-app`, then `npm install react react-dom`, by adding `reagent` to the `shadow-cljs.edn` dependencies, by configuring the build configuration (also in `shadow-cljs.edn`), and by requiring `reagent.core`, `reagent.dom`, and the `clojure.string` libraries in `core.cljs`.

This app was "transformed" into a PWA by adding `service-worker.js`, by adding a service worker install script into `index.html`, by adding a `manifest.json`, by referencing the `manifest.json` file in the head of `index.html`, and by adding some PNG icons.
