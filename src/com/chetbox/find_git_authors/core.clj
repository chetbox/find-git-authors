(ns com.chetbox.find-git-authors.core
  (:require
    [me.raynes.conch :refer [programs]]
    [clojure.java.io :as io]))

(programs git)

(def ^:dynamic log-format "%an <%ae>")
(def ^:dynamic work-dir (.getPath (io/file (System/getenv "HOME")
                                           ".find-git-authors")))

(defn repo-url
  [username repo-name]
  (str "https://github.com/" username "/" repo-name ".git"))

(defn pull!
  [repo-path url]
  (if (.exists repo-path)
    (git "fetch" {:dir repo-path})
    (do
      (io/make-parents repo-path)
      (git "clone" "--bare" url (.getPath repo-path)))))

(defn log-lines
  [repo-path]
  (git "log" (str "--format=format:" log-format) {:dir repo-path
                                                  :seq true
                                                  :buffer :line}))

(defn print-authors!
  [username repo-name]
  (let [repo-path (io/file work-dir username repo-name)]
    (pull! repo-path (repo-url username repo-name))
    (doseq [author (distinct (log-lines repo-path))]
      (println author))))

(defn -main
  ( []
    (println "Usage:\n  find-git-authors USERNAME REPO"))
  ( [username repo-name]
    (print-authors! username repo-name)
    (System/exit 0)))
