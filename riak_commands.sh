curl -X PUT -H "content-type: text/plain" \
 http://localhost:8098/riak/alice/p1 --data-binary @-
Alice was beginning to get very tired of sitting by her sister on the
bank, and of having nothing to do: once or twice she had peeped into the
book her sister was reading, but it had no pictures or conversations in
it, 'and what is the use of a book,' thought Alice 'without pictures or
conversation?'

curl -X PUT -H "content-type: text/plain" \
 http://localhost:8098/riak/alice/p2 --data-binary @-
So she was considering in her own mind (as well as she could, for the
hot day made her feel very sleepy and stupid), whether the pleasure
of making a daisy-chain would be worth the trouble of getting up and
picking the daisies, when suddenly a White Rabbit with pink eyes ran
close by her.

curl -X PUT -H "content-type: text/plain" \
 http://localhost:8098/riak/alice/p5 --data-binary @-
The rabbit-hole went straight on like a tunnel for some way, and then
dipped suddenly down, so suddenly that Alice had not a moment to think
about stopping herself before she found herself falling down a very deep
well.

curl -X GET -H "content-type: text/plain" http://localhost:8098/riak/alice/p1

# Clojure M/R Function:
curl -X POST -H "content-type: application/json" http://localhost:8098/mapred --data @-
{"inputs":[["alice","p1"],["alice","p2"],["alice","p5"]],"query":[{"map":{"language":"clojure","source":"(fn [data]  (let [words (re-seq #\"\\w+\" data)] (map (fn [v] (closerl/otp-tuple (closerl/otp-binary v) (closerl/otp-long 1))) words)))"}},{"reduce":{"language":"clojure","source":"(fn [vs] (let [v1 (remove-struct (remove-not-found vs)) v2 (apply concat v1) v3 (reduce (fn [m v] (assoc m (first v) (+ (get m (first v) 0) (second v)))) {} v2)] (as-proplist v3 closerl/otp-binary closerl/otp-long)))"}}]}

# Clojure M/R Function:
curl -X POST -H "content-type: application/json" http://localhost:8098/mapred --data @-
{"inputs":[["alice","p1"],["alice","p2"],["alice","p3"]],"query":[{"map":{"language":"clojure","source":"(fn [data]  (let [words (re-seq #\"\\w+\" data)] (map (fn [v] (closerl/otp-tuple (closerl/otp-binary v) (closerl/otp-long 1))) words)))"}},{"reduce":{"language":"clojure","source":"(fn [vs] (let [v1 (remove-struct (remove-not-found vs)) v2 (apply concat v1) v3 (reduce (fn [m v] (assoc m (first v) (+ (get m (first v) 0) (second v)))) {} v2)] (as-proplist v3 closerl/otp-binary closerl/otp-long)))"}}]}