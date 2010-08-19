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

(defn map-fn [words]
  (do
    (println words)
    (map (fn [v] (closerl/otp-tuple (closerl/otp-binary v) (closerl/otp-long 1)))
          words)
    ))
    
(defn remove-not-found [v]
  (try
    (=  (first (first (second v))) "not_found")
    (catch Exception _ false)
    ))
        
(defn reducer [vs]
  (let [the-vals (apply concat vs)]
    (do
      (println the-vals)
  (reduce (fn [m v]
    (assoc m (first v) (+ (get m (first v) 0) (second v)))) {} 
      the-vals
        ))))

(defn red-fn [vs]
  (let [tm (reducer (remove remove-not-found vs))]
    (map (fn [k v] (closerl/otp-tuple (closerl/otp-binary k) (closerl/otp-long v))) (keys tm) (vals tm))))

(defn get-reply [command data]
  (do
    (println (str "got data: " data " command: " command))
  (if (= command "map")
    (apply closerl/otp-list (map-fn (re-seq #"\w+" (first data))))
    (closerl/otp-tuple (closerl/otp-atom "struct") (apply closerl/otp-list (red-fn data)))
        )))
        
(defn receive-riak-request [mbox]
  (closerl/proplist-to-map (closerl/otp-value (closerl/otp-receive mbox))))
  
(defn reply-to-riak [mbox pid reply]
  (do
    (println (str "sending reply" reply))
    (closerl/otp-send-to-pid mbox pid
      (closerl/otp-tuple (closerl/otp-atom "clj") (closerl/otp-list reply)))))

(defn mapred-server [mbox]
  (let [{pid :pid, command :command, r-value :r-value} (receive-riak-request mbox)]
      (reply-to-riak mbox pid (get-reply command r-value))
      (recur mbox)))

(println "running m/r server")
(mapred-server mbox)