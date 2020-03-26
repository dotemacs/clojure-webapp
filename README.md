# Sample Clojure web app

Basic web app written in Clojure, with authentication, prompted by [this discussion](https://groups.google.com/forum/#!msg/clojure/Tyt-U920_2w/SjNNngTrAgAJ).

It uses Postgresql database & Mailgun for email sending.

## Usage

Create the database:

```
createdb webapp-dev
```

Then start the REPL in a way that you like. With Emacs' CIDER you'd do
it like this:

<kbd>M-x [RET] cider-jack-in [RET]</kbd>

and then at the REPL:

```
(go)
```

Then run the migrations:

```
user> (repl/migrate (ragtime-config "jdbc:postgresql://localhost/webapp-dev"))
```

Then add a first user:

```clojure
user> (add-user {:database (:db system) :email "foo@bar.com" :password "secret"})
```

Open http://localhost:3000/ and play around the app.
