(ns webapp.db
  (:require [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]
            [hugsql.core :as hugsql]
            [hikari-cp.core :as hikari]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [clj-time.jdbc])
  (:import [org.postgresql.util PGobject]))


(defn result-one-snake->kebab
  "Convert the results of HugSQL query, which returns one row, from
  snake case to kebab case"
  [this result options]
  (->> (hugsql.adapter/result-one this result options)
       (transform-keys ->kebab-case-keyword)))


(defn result-many-snake->kebab
  "Convert the results of HugSQL query, which returns multiple rows,
  from snake case to kebab case"
  [this result options]
  (->> (hugsql.adapter/result-many this result options)
       (map #(transform-keys ->kebab-case-keyword %))))


(defmethod hugsql.core/hugsql-result-fn :1 [sym]
  'webapp.db/result-one-snake->kebab)


(defmethod hugsql.core/hugsql-result-fn :* [sym]
  'webapp.db/result-many-snake->kebab)


(extend-protocol jdbc/IResultSetReadColumn
  java.util.UUID
  (result-set-read-column [pgobj _ _]
    (str pgobj))

  PGobject
  (result-set-read-column [pgobj metadata idx]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "citext" (.toString value)
        :else value))))


(hugsql/def-db-fns "sql/queries.sql")


(defrecord Hikari [db]
  component/Lifecycle
  (start [component]
    (if (:conn component)
      component
      (assoc component :conn {:datasource (hikari/make-datasource db)})))
  (stop [component]
    (when-let [conn (:conn component)]
      (hikari/close-datasource (:datasource conn)))
    (assoc component :conn nil)))


(defn new-hikari
  [jdbc-url]
  (map->Hikari {:db {:jdbc-url jdbc-url
                     :stringtype "unspecified"}}))
