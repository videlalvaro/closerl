(require '[closerl.core :as closerl])
(use '[closerl.riak-map-red])

(def n (closerl/otp-node "clj2" "riak"))

(def mbox (closerl/otp-mbox n "cljmbox"))

(println n)
(println mbox)

(println (.ping mbox "riak@mrhyde" 2000))

(println "running m/r server")
(mapred-server mbox)