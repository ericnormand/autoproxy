(ns autoproxy
  (:use clojure.set)
  (:import [java.lang.reflect Modifier]))

;; This macro was written by Tim Lopez
;; See the blog post
;; http://www.brool.com/index.php/snippet-automatic-proxy-creation-in-clojure
(defmacro auto-proxy [interfaces variables & args]
  (let [defined (set (map #(str (first %)) args))
        names (fn [i] (map #(.getName %) (.getMethods i)))
        all-names (into #{} (apply concat (map names (map resolve interfaces))))
        undefined (difference all-names defined)
        auto-gen (map (fn [x] `(~(symbol x) [& ~'args])) undefined)]
    `(proxy ~interfaces ~variables ~@args ~@auto-gen)))

(defn args-list [] (for [x (iterate inc 0)] (symbol (str "a" x))))

;; This macro was inspired by the above macro
(defmacro auto-wrapper [obj interfaces variables & args]
  (let [defined (set (map #(hash-map
			    :name (str (first %))
			    :args (vec (take (count (second %))
					     (args-list))))
			  args))
	signatures (fn [i]
		     (for [m (.getMethods i)
			   :when (not (Modifier/isFinal (.getModifiers m)))]
		       {:name (.getName m)
			:args (vec (take (count (.getParameterTypes m)) (args-list)))}))
	all-sigs (into #{} (apply concat (map signatures (map resolve interfaces))))
	undefined (difference all-sigs defined)
	wrap-sym (gensym "wrapped")
	auto-gen (map (fn [x] `(~(symbol (:name x)) ~(:args x)
				(~(symbol (str "." (:name x))) ~wrap-sym ~@(:args x)))) undefined)]
     `(let [~wrap-sym ~obj] (proxy ~interfaces ~variables ~@args ~@auto-gen))))