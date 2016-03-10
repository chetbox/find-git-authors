(ns com.chetbox.find-git-authors.core
  (:require
    [clj-jgit.porcelain :as git]
    [clj-jgit.querying :as gitq]
    [clojure.java.io :as io]))

(def work-dir (.getPath (io/file (System/getenv "HOME")
                                 ".find-git-authors")))

(defn repo-url
  [username repo-name]
  (str "https://github.com/" username "/" repo-name ".git"))

(defn pull-repo
  [path url]
  (if (.exists path)
    (let [repo (git/load-repo path)]
      (git/git-fetch repo))
    (git/git-clone2 url {:path path
                         :bare true}))
  (git/load-repo path))

(defn print-emails
  [repo]
  (loop [log (git/git-log repo)
         emails-seen #{}]
    (when-let [message (first log)]
      (let [commit (gitq/commit-info repo message)]
        (when-not (emails-seen (:email commit))
          (println (str (:author commit) " <" (:email commit) ">")))
        (recur (next log)
               (conj emails-seen (:email commit)))))))

(defn find-git-authors
  [username repo-name]
  (let [repo-path (io/file work-dir username repo-name)]
    (io/make-parents repo-path)
    (let [repo (pull-repo repo-path (repo-url username repo-name))]
      (print-emails repo))))

(defn -main
  ( []
    (println "Usage:\n  find-git-authors USERNAME REPO"))
  ( [username repo-name]
    (find-git-authors username repo-name)))
