(ns webapp.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn header
  []
  [:div.navbar.navbar-expand-lg.fixed-top.navbar-dark.bg-dark
   [:div.container
    [:a.navbar-brand {:href "/"} "webapp"]
    [:button.navbar-toggler {:type "button"
                             :data-toggle "collapse"
                             :data-target "#navbarResponsive"
                             :aria-controls "navbarResponsive"
                             :aria-expanded "false"
                             :aria-label "Toggle navigation"}
     [:span.navbar-toggler-icon]]
    [:div#navbarResponsive.collapse.navbar-collapse
     [:ul.nav.navbar-nav.ml-auto [:li.nav-item
                                  [:a.nav-link
                                   {:href "/#" :target "_blank"}
                                   "Preferences"]]
      [:li.nav-item [:a.nav-link
                     {:href "/logout"
                      :target "_blank"}
                     "Log out"]]]]]])

(defn footer
  []
  [:div.container
   [:footer#footer
    [:div.row
     [:div.col-lg-12
      [:ul.list-unstyled
       [:li.float-lg-right
        [:a {:href "#top"} "Back to top"]]
       [:li [:a {:href "http://blog.bootswatch.com"
                 :onclick "pageTracker._link(this.href); return false;"} "Blog"]]
       [:li [:a {:href "https://feeds.feedburner.com/bootswatch"} "RSS"]]
       [:li [:a {:href "https://twitter.com/bootswatch"} "Twitter"]]
       [:li [:a {:href "https://github.com/thomaspark/bootswatch/"} "GitHub"]]
       [:li [:a {:href "../help/#api"} "API"]]
       [:li [:a {:href "../help/#donate"} "Donate"]]]
      [:p "Made by " [:a {:href "http://thomaspark.co"} "Thomas Park"] "."]
      [:p "Code released under the "
       [:a {:href "https://github.com/thomaspark/bootswatch/blob/master/LICENSE"} "MIT License"] "."]
      [:p "Based on " [:a {:href "https://getbootstrap.com" :rel "nofollow"} "Bootstrap"] ". Icons from "
       [:a {:href "http://fontawesome.io/" :rel "nofollow"} "Font Awesome"] ". Web fonts from "
       [:a {:href "https://fonts.google.com/" :rel "nofollow"} "Google"] "."]]]]])

(defn application [& content]
  (html5 {:lang "en"}
         [:head
          [:meta {:charset "UTF-8"}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
          [:meta
           {:content "width=device-width, initial-scale=1", :name "viewport"}]
          [:meta {:content "IE=edge", :http-equiv "X-UA-Compatible"}]
          [:link {:media "screen", :href "/css/bootstrap.css" :rel "stylesheet"}]
          [:title "webapp"]
          (include-css "/css/bootstrap.css")
          (include-css "/css/custom.min.css")]
         (list (header)
               [:body
                content
                (footer)])))
