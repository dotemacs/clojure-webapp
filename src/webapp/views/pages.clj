(ns webapp.views.pages
  (:require [hiccup.form :as form :refer [check-box form-to
                                          password-field submit-button
                                          text-field select-options drop-down
                                          hidden-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [webapp.views.layout :refer [application]]))

(defn index-view
  []
  (application
   [:main
    [:div.container--small.border
     [:h1
      [:i.icon.icon--btc-circle] "Welcome to webapp"]
     [:a {:href "/logout"} "Logout"]]]))

(defn dashboard
  []
  (application
   [:div.container
    [:div.row
     [:div.col-lg-12
      [:div.bs-component
       [:div.alert.alert-dismissible.alert-warning
        [:button.close {:type "button" :data-dismiss "alert"} "×"]
        [:h4.alert-heading "Well done!"]
        [:p.mb-0
         "Add some "
         [:a.alert-link {:href "#"} "menu sections"]
         " to use the app."]]]]]]))

(defn signup [request]
  (application
   (list
    [:div.container
     [:div.row
      [:div.col-lg-6.mx-auto.p-3.bg-dark.text-center
       (form-to [:post "/signup"]
                [:fieldset
                 [:legend "Sign up"]
                 [:div.form-group.mb-60
                  [:input#email.form-control
                   {:name "email" :type "text" :placeholder "Email"}]]
                 [:div.form-group.mb-60
                  [:input#password.form-control
                   {:name "password" :type "password" :placeholder "Password"}]]
                 (anti-forgery-field)
                 [:div.form-group
                  [:button.btn.btn-primary
                   {:type "submit" :name "Create account" :href "/login"}
                   "Create an account"]]])]]])))

(defn thank-you
  []
  (application
   [:main
    [:div.container
     [:h1.tac.mt-40 "Check your email and follow instructions there"]]]))


(defn login
  []
  (application
   [:div.container
    [:div.row
     [:div.col-lg-6.mx-auto.p-3.bg-dark.text-center
      (form-to [:post "/login"]
               [:fieldset
                [:legend "Log in"]
                [:div.form-group
                 [:label "Email address"]
                 [:input.form-control {:name "email" :type "email" :placeholder "Email"}]]
                [:div.form-group
                 [:label "Password"]
                 [:input.form-control
                  {:name "password" :type "password" :placeholder "Password"}]
                 [:a.forgot {:href ""} "Forgot Password"]]
                (anti-forgery-field)
                [:button.btn.btn-primary
                 {:type "submit" :href "/signup"}
                 "Submit"]])]]]))
