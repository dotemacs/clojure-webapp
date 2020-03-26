(ns webapp.users
  (:require [buddy.hashers :as hashers]
            [crypto.random :as crypto]
            [ring.util.response :refer [redirect]]
            [webapp.db :as db]
            [webapp.mailer :refer [signup-email]]))

(defn generate-confirmation-token
  "Generate signup confirmation token."
  []
  (crypto/url-part 50))


(defn add-user
  "Add user with the `email` and their `password` encrypted"
  [{:keys [database email password]}]
  (db/create-user (:conn database)
                  {:email              email
                   :encrypted-password (hashers/encrypt password)
                   :confirmation-token (generate-confirmation-token)}))


(defn save-user
  "Save a user based on the `params` (but only if they are not already
  on the system, if they are, just omit that step).

  The idea is not to let the unauthenticated user know if they are
  already signed up or not. This way we don't let random people on the
  net verify if somebody they are interested in, has an account on
  this system."
  [{:keys [database mailgun params]}]
  (let [email (:email params)
        conn (:conn database)]
    (when-not (db/get-user-by-email conn {:email email})
      (let [user (add-user {:database database
                            :email    email
                            :password (:password params)})
            email-details {:mailgun mailgun
                           :to email
                           :token (:confirmation-token user)}]
        (signup-email email-details)))))


(defn check-password
  "Verify that the supplied password and the one saved in the system,
  match."
  [{:keys [params session email user]}]
  (let [form-password (:password params)]
    (if (hashers/check form-password (:encrypted-password user))
      (-> (redirect "/")
          (assoc :session (assoc session :identity email)))
      {:status 401
       :headers {"Content-Type" "text/plain"}
       :body "no because the password is incorrect"})))


(defn login-user
  [{:keys [params session database]}]
  (let [email (:email params)]
    (if-let [user (db/get-user-by-email (:conn database) {:email email})]
      ;; you might want to consider doing a check on the
      ;; field `:confirmed-at` before you check the password, because
      ;; the idea is to allow the user to confirm their email, by
      ;; clicking on the link in the email that you send them when
      ;; they sign up. Otherwise treat them as if they've not signed
      ;; up at all.
      (check-password {:params params
                       :session session
                       :email email
                       :user user})
      {:status 401
       :headers {"Content-Type" "text/plain"}
       :body "no because ... here you should give some meaningful error message, hopefully not one where they'll know if the user with the email exists in the system or not"})))
