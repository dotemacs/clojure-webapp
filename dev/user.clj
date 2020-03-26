(ns user
  (:require [clojure.repl :refer [source doc]]
            [clojure.pprint :refer [pprint]]
            [clojure.reflect :refer [reflect]]
            [clojure.repl :refer [apropos dir doc find-doc pst source]]
            [clojure.test :refer [run-all-tests]]
            [com.stuartsierra.component :as component]
            [ragtime.jdbc :as jdbc]
            [reloaded.repl :refer [system init start stop go reset reset-all]]
            [ragtime.repl :as repl]
            [webapp.core :refer [new-system]]
            [webapp.users :refer [add-user]]))

(defn new-dev-system
  "Create a development system"
  []
  (new-system :dev))

(reloaded.repl/set-init! new-dev-system)

(defn ragtime-config
  [jdbc-url]
  {:datastore  (jdbc/sql-database {:connection-uri jdbc-url})
   :migrations (jdbc/load-resources "migrations")})
