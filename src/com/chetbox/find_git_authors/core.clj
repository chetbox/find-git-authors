(ns com.chetbox.find-git-authors.core
  (:require
    [me.raynes.conch :refer [programs]]
    [clojure.java.io :as io]))

(programs git)

(def ^:dynamic log-format "%an <%ae>")
(def ^:dynamic work-dir (.getPath (io/file (System/getenv "HOME")
                                           ".find-git-authors")))

(defn url->local-path
  [repo-url]
  (let [url-data (new java.net.URI repo-url)]
    (io/file work-dir (.getHost url-data) (subs (.getPath url-data) 1))))

(defn pull!
  [repo-path url]
  (if (.exists repo-path)
    (git "fetch" {:dir repo-path})
    (do
      (io/make-parents repo-path)
      (git "clone" "--bare" url (.getPath repo-path)))))

(defn log-lines
  [repo-path revisions]
  (git "log" (str "--format=format:" log-format) revisions {:dir repo-path
                                                            :seq true
                                                            :buffer :line}))

(defn print-authors!
  [repo-url revisions]
  (let [repo-path (url->local-path repo-url)]
    (pull! repo-path repo-url)
    (doseq [author (distinct (log-lines repo-path revisions))]
      (println author))))

(defn -main
  ( []
    (println "Usage:\n  find-git-authors GIT_REPO_URL [REVISION_RANGE]"))
  ( [repo-url]
    (-main repo-url "master"))
  ( [repo-url revisions]
    (print-authors! repo-url revisions)
    (System/exit 0)))
