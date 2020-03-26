-- :name get-all-users :? :*
-- :doc get all users
select id, email, encrypted_password from users;

-- :name get-user-by-email :? :1
-- :doc find a single user based on their email
select * from users
where email = :email

-- :name get-user-token :? :1
-- :doc find a single user based on their confirmation_token
select * from users
where confirmation_token = :confirmation-token

-- :name create-user :<! :1
-- :doc create a user with an encrypted password
INSERT INTO users(email, encrypted_password, confirmation_token)
VALUES (:email, :encrypted-password, :confirmation-token)
RETURNING *;
