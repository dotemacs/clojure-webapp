(ns webapp.core
  (:gen-class)
  (:require [aero.core :as aero]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            muuntaja.middleware
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            ring.middleware.params
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.util.response :refer [redirect]]
            [webapp.db :as db]
            [webapp.views.pages :refer [dashboard signup thank-you login]]
            [webapp.users :refer [save-user login-user]]))


(defn form-security
  "Settings to be added to the `handler` which handles form submissions,
  so that the anti-forgery-field can be added to prevent exploitation."
  [handler]
  (wrap-defaults handler (merge site-defaults
                                {:session {:flash true
                                           :cookie-name "id"
                                           :store (cookie-store {:key "a 16-byte secret"})}
                                 :security {:anti-forgery true
                                            :frame-options :deny}})))

(defn response-html
  "Set `body` as the response with the default content type and status
  set.

  `ring.util.response/response` doesn't include the default header of
  text/html content type. And using `ring.util.response/header`, just
  to set the content type seemed like overkill. So using this function
  instead."
  [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})


(def backend (session-backend))


(defn app
  [database mailgun]
  (ring/ring-handler
   (ring/router
    [["/" {:get {:middleware [form-security]
                 :handler (fn [{:keys [request session] :as all}]
                            (if (authenticated? session)
                              (response-html (dashboard))
                              (response-html "go to /login instead")))}}]
     ["/signup" {:middleware [form-security]
                 :get {:handler (fn [data]
                                  (response-html (signup data)))}
                 :post {:handler (fn [{:keys [params session]}]
                                   (save-user {:database database
                                               :params params
                                               :session session})
                                   (redirect "/thank-you"))}}]
     ["/login" {:middleware [form-security]
                :get {:handler (fn [_] (response-html (login)))}
                :post {:handler (fn [{:keys [params session]}]
                                  (login-user {:params params
                                               :database database
                                               :session session}))}}]
     ["/logout" {:middleware [form-security]
                 :get {:handler (fn [_]
                                  (-> (redirect "/login")
                                      (assoc :session {})))}}]
     ["/thank-you" {:get {:handler (fn [_] (response-html (thank-you)))}}]
     ["/*" (ring/create-resource-handler)]]
    {:conflicts (constantly nil)
     :data {:middleware [[wrap-authentication backend]
                         ring.middleware.params/wrap-params
                         muuntaja.middleware/wrap-format
                         rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}})
   (constantly {:status 404 :body "Not the page that you want"})))


(defn run-app
  [db port mailgun]
  (-> (app db mailgun)
      (jetty/run-jetty {:join? false
                        :port port})))


(defrecord JettyWebServer [db port mailgun]
  component/Lifecycle
  (start [component]
    (if (:jetty component)
      component
      (assoc component :jetty (run-app db port mailgun))))
  (stop [component]
    (when-let [jetty (:jetty component)]
      (.stop jetty))
    (dissoc component :jetty)))


(defn new-jetty-web-server []
  (map->JettyWebServer {}))


(defn config
  "Read EDN config, with the given profile. See Aero docs at
  https://github.com/juxt/aero for details."
  [profile]
  (-> "config.edn"
      io/resource
      (aero/read-config {:profile profile})))


(defn new-system
  [profile]
  (println "new-system ====================> with profile:" profile)
  (let [config (config profile)]
    (component/system-map
     :port (:port config)
     :db (db/new-hikari (:jdbc-url config))
     :mailgun (:mailgun config)
     :webserver (component/using (new-jetty-web-server) [:db :port :mailgun]))))
