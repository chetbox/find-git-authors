(defproject find-github-emails "0.1.0-SNAPSHOT"
  :description "Find all authors in a git repo"
  :url "http://github.com/chetbox/find-git-authors"
  :license {:name "The MIT License (MIT)"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [me.raynes/conch "0.8.0"]]
  :main com.chetbox.find-git-authors.core)
