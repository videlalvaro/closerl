(require '[closerl.core :as closerl])

(def n (closerl/otp-node "clj2" "riak"))

(def mbox (closerl/otp-mbox n "cljmbox"))

(println n)
(println mbox)

(println (.ping mbox "riak@mrhyde" 2000))
;
;(println (.ping mbox "dev2@mrhyde" 2000))
;
;(println (.ping mbox "dev3@mrhyde" 2000))

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

(defn red-fn [vs]
  (let [v1 (apply concat (remove-not-found vs))
        v2 (reduce (fn [m v] (assoc m (first v) (+ (get m (first v) 0) (second v)))) {} v1)]
    (as-proplist v2 closerl/otp-binary closerl/otp-long)))
    
(defn error-reply [msg]
  (closerl/otp-tuple (closerl/otp-atom "error") (closerl/otp-string msg)))
  
(defn wrap-map-reply [reply]
    (apply closerl/otp-list reply))
    
(defn wrap-red-reply [reply]
  (closerl/otp-tuple (closerl/otp-atom "struct") (apply closerl/otp-list reply)))

(defn get-reply [command data myfn]
  (do
    (println (str "got data: " data " command: " command))
  (cond 
    (= command "map") (wrap-map-reply ((eval (read-string myfn)) data))
    (= command "red") (wrap-red-reply (red-fn data))
      :else (error-reply (str "Unknown command: " command)))))
        
(defn receive-riak-request [mbox]
  (let [msg (closerl/otp-receive mbox)]
    (do
      (println (str "got message: " msg))
      (closerl/proplist-to-map (closerl/otp-value msg)))))
  
(defn reply-to-riak [mbox pid reply]
  (do
    (println (str "sending reply" reply))
    (closerl/otp-send-to-pid mbox pid
      (closerl/otp-tuple (closerl/otp-atom "clj") (closerl/otp-list reply)))))

(defn mapred-server [mbox]
  (let [{:keys [pid, command, r-value, clj-map-fn], :or {clj-map-fn "(fn [v] v)"}} (receive-riak-request mbox)]
  (do
    (println clj-map-fn)
    (reply-to-riak mbox pid (get-reply command r-value clj-map-fn)))
    (recur mbox)))

(println "running m/r server")
(mapred-server mbox)