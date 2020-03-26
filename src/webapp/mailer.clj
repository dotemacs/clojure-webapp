(ns webapp.mailer
  (:require [mailgun.mail :as mailgun]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.util :refer [url]]))


(defn send-it
  "Takes `content`, which contains Mailgun credentials and the email as a map.
  Extracts them and sends it off."
  [content]
  (mailgun/send-mail (-> (select-keys content [:api-key :domain])
                         (clojure.set/rename-keys {:api-key :key}))
                     (select-keys content [:from :to :subject :html])))


(defn make-url
  "Construct the full URL that should be used in the email templates."
  [mailgun path id text]
  (html (link-to (url (:url mailgun) "/" path "/" id) text)))


(defn signup-email-template
  [& {:keys [mailgun to token]}]
  {:to to
   :subject "Welcome to webapp"
   :html (str "To confirm your account click on "
              (make-url mailgun "confirm-email" token "This link"))})


(defn signup-email
  "send signup email template, requires 'to' and 'token' which is a
  confirmation token auto-generated on the user creation"
  [& {:keys [mailgun to token]}]
  (when (:send-email mailgun)
    (send-it (merge mailgun
                    (signup-email-template :mailgun mailgun :to to :token token)))))


(defn password-reset-template
  "Takes the email address of the recipient as `to`"
  [& {:keys [mailgun to token]}]
  {:to to
   :subject "Password reset for webapp"
   :html (str "Hello,</br>"
              "somebody (hopefully you) requested a new password for <b>This cool service</b>. "
              "No changes have been made to your account yet.</br></br>"
              "You can reset your password by clicking on the link below:</br>"
              (make-url mailgun "password-reset" token "Password reset"))})


(defn password-reset-email
  "Send an email to the person who needs to have their password reset.
  Argument is their email address as `to` and the password reset token
  as `token`."
  [& {:keys [mailgun to token]}]
  (when (:send-email mailgun)
    (send-it (merge mailgun (password-reset-template :mailgun mailgun
                                                     :to to
                                                     :token token)))))
