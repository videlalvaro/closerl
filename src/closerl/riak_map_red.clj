(ns closerl.riak-map-red
  (:require [closerl.core :as closerl]))
  
(defn dummy-reply []
  (closerl/otp-list 
    (closerl/otp-tuple (closerl/otp-binary "a") (closerl/otp-long 1))))

(defn remove-not-found [items]
  (remove (fn [v]
    (try
      (=  (first (first (second v))) "not_found")
      (catch Exception _ false)
      )) items))

(defn as-proplist [items key-fn val-fn]
  (map (fn [k v] (closerl/otp-tuple (key-fn k) (val-fn v))) (keys items) (vals items)))

(defn get-fn [s]
  (eval (read-string s)))

(defn error-reply [msg]
  (closerl/otp-tuple (closerl/otp-atom "error") (closerl/otp-string msg)))

(defn wrap-map-reply [reply]
  (apply closerl/otp-list reply))

(defn wrap-red-reply [reply]
  (closerl/otp-tuple (closerl/otp-atom "struct") (apply closerl/otp-list reply)))
  
(defn remove-struct [data]
  (map (fn [v] 
    (try (if (= (first v) "struct") (second v) v)
    (catch Exception _ v))) data))

(defn get-reply [command data f]
  (cond 
    (= command "map") (wrap-map-reply ((get-fn f) data))
    (= command "red") (wrap-red-reply ((get-fn f) data))
      :else (error-reply (str "Unknown command: " command))))

(defn receive-riak-request [mbox]
  (let [msg (closerl/otp-receive mbox)]
    (do
      (println (str "got message: " msg))
      (closerl/proplist-to-map (closerl/otp-value msg)))))

(defn reply-to-riak [mbox pid reply]
    (closerl/otp-send-to-pid mbox pid
      (closerl/otp-tuple (closerl/otp-atom "clj") (closerl/otp-list reply))))

(defn mapred-server [mbox]
  (let [{:keys [pid, command, r-value, clj-map-fn], :or {clj-map-fn "(fn [v] v)"}} (receive-riak-request mbox)]
    (reply-to-riak mbox pid (get-reply command r-value clj-map-fn))
    (recur mbox)))