(ns autoproxy
  (:use clojure.set)
  (:import [java.lang.reflect Modifier]))

(defn- parse-proxy-fns
  [fns]
  (set (for [f fns]
	 {:name (str (first f))
	  :args (count (second f))})))

(defn- parse-methods
  [methods]
  (set (for [m methods
	     :when (not (Modifier/isFinal (.getModifiers m)))]
	 {:name (.getName m)
	  :args (count (.getParameterTypes m))})))

(defn- resolve-interfaces
  [ins]
  (set (map resolve ins)))

(defn- get-all-methods
  [ins]
  (apply union (for [i (resolve-interfaces ins)] (parse-methods (.getMethods i)))))

(defn- args-list [] (for [x (iterate inc 0)] (symbol (str "a" x))))

;; This macro was originally written by Tim Lopez
;;
;; See the blog post
;;
;; http://www.brool.com/index.php/snippet-automatic-proxy-creation-in-clojure
;;
;; Modified by Eric Normand to deal with methods with the same name
;; but different numbers of arguments.
(defmacro auto-proxy [interfaces variables & args]
  (let [defined (parse-proxy-fns args)
	all-methods (get-all-methods interfaces)
	names (set (for [m all-methods] (:name m)))
	undefined (difference all-methods defined)
	auto-gen (for [name names]
		   `(~(symbol name)
		     ~@(for [m args
			     :let [n (str (first m))]
			     :when (= name n)]
			 (rest m))
		     ~@(for [m undefined
			     :when (= name (:name m))]
			 `(~(vec (take (:args m) (args-list)))))))]
    `(proxy ~interfaces ~variables ~@auto-gen)))



;; This macro was inspired by the above macro
(defmacro auto-wrapper [obj interfaces variables & args]
  (let [defined (parse-proxy-fns args)
	all-methods (get-all-methods interfaces)
	names (set (for [m all-methods] (:name m)))
	undefined (difference all-methods defined)
	wrap-sym (gensym "wrapped")
	auto-gen (for [name names]
		   `(~(symbol name)
		     ~@(for [m args
			     :let [n (str (first m))]
			     :when (= name n)]
			 (rest m))
		     ~@(for [m undefined
			     :when (= name (:name m))]
			 `(~(vec (take (:args m) (args-list)))
			   (~(symbol (str "." (:name m)))
			    ~wrap-sym
			    ~@(take (:args m) (args-list)))))))]
    `(let [~wrap-sym ~obj] (proxy ~interfaces ~variables ~@auto-gen))))