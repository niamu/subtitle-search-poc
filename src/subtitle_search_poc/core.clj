(ns subtitle-search-poc.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [hiccup.page :as page]))

(defn abs [n] (if (< n 0) (- n) n))

(defn time->msecs
  [time]
  (let [[hours minutes seconds msecs]
        (->> (re-find #"(\d{2}):(\d{2}):(\d{2}),(\d{3})" time)
             rest
             (map #(Integer/parseInt %)))]
    (+ (* hours 60 60 1000)
       (* minutes 60 1000)
       (* seconds 1000)
       msecs)))

(def srt (slurp "resources/subtitle.srt"))
(defn thumbnails
  [runtime]
  (let [files (->> (file-seq (io/file "resources/thumbnails/"))
                   (filter #(.isFile %))
                   (map #(second (string/split (.getPath %) #"resources/"))))
        interval (/ (time->msecs runtime)
                    (count files))]
    (->> files
         sort
         (map-indexed (fn [idx f] [(* (inc idx) interval) f]))
         (reduce (fn [accl [idx f]] (assoc accl idx f)) {}))))

(defn -main
  [runtime]
  (spit "resources/index.html"
        (page/html5
         [:body
          [:style (str "ul { margin: 0; padding: 0; }"
                       "li { list-style: none; }"
                       "img { display: inline-block; width: 100px; }"
                       "ul ul { display: inline-block; margin: 0 0 0 2em; vertical-align: top; }"
                       "ul li li { list-style: disc; }")]
          [:ul
           (map (fn [[thumb contents]]
                  [:li
                   [:img {:src thumb}]
                   [:ul
                    (map (fn [content]
                           (map (fn [line]
                                  [:li line])
                                (:text content)))
                         contents)]])
                (->> (reduce (fn [accl s]
                               (let [[_ range & text] (string/split-lines s)
                                     time (time->msecs (first (string/split range #" ")))]
                                 (conj accl
                                       {:time time
                                        :thumbnail (->> (thumbnails runtime)
                                                        (map (fn [[thumb-time thumbnail]]
                                                               [(abs (- thumb-time time))
                                                                thumbnail]))
                                                        (sort-by first)
                                                        first second)
                                        :text text})))
                             []
                             (string/split srt #"\r?\n\r?\n"))
                     (group-by :thumbnail)
                     (sort-by key)))]])))
