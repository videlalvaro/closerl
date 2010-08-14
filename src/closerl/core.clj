(ns closerl.core
  (:import (com.ericsson.otp.erlang
              OtpNode
              OtpSelf
              OtpPeer
              ;; Types
              OtpErlangObject
              OtpErlangBoolean
              OtpErlangAtom
              OtpErlangBinary
              OtpErlangChar
              OtpErlangByte
              OtpErlangShort
              OtpErlangUShort
              OtpErlangInt
              OtpErlangUInt
              OtpErlangLong
              OtpErlangFloat
              OtpErlangDouble
              OtpErlangString
              OtpErlangList
              OtpErlangTuple)))

;; OtpNode wrapper
(defn otp-node
  "Creates an OtpNode"
  [node-name]
  (OtpNode. node-name))
  

(defn otp-mbox
  "Creates an OtpMbox"
  ([node]
    (.createMbox node ))
  ([node name]
    (.createMbox node name)))
    
(defn otp-register-name
  "Registers the mbox name"
  [mbox name]
  (.registerName mbox name))
  
(defn otp-ping
  "Pings a remote Erlang node"
  [node remote tmo]
  (.ping node remote tmo))
              
;; OtpSelf wrapper
(defn otp-self 
  "Creates OtpSelf with name & cookie"
  ([node-name]
    (OtpSelf. node-name))
  ([node-name cookie]
    (OtpSelf. node-name cookie))
  ([node-name cookie port]
    (OtpSelf. node-name cookie port)))
    
(defn otp-accept
  "Accept an incoming connection from a remote node."
  [self]
  (.accept self))

(defn otp-connect
  "Open a connection to a remote node."
  [self peer]
  (.connect self peer))
  
(defn otp-pid
  "Get the Erlang PID that will be used as the sender id in all 'anonymous' messages sent by this node."
  [self]
  (.pid self))
  
(defn otp-publish-port
  "Make public the information needed by remote nodes that may wish to connect to this one."
  [self]
  (.publishPort self))
  
(defn otp-unpublish-port
  "Unregister the server node's name and port number from the Erlang port mapper, thus preventing any new connections from remote nodes."
  [self]
  (.unPublishPort self))
  
(defn otp-peer
  "Initializes peer"
  [name]
  (OtpPeer. name))
  
(defn otp-rpc-call
  "Performs a RPC call to remote node"
  ([connection m f]
    (.sendRPC connection m f (OtpErlangList.)))
  ([connection m f a]
    (.sendRPC connection m f a)))
  
(defn otp-receive
  "Receive result from RPC call"
  [connection]
  (.receiveRPC connection))

(defn otp-rpc-and-receive
  "Send a RPC request, receive the message and returns it
  converted as a Clojure type"
  [connection m f a]
  (do
    (.sendRPC connection m f a)
    (otp-value (.receiveRPC connection))))

;; Marshalling
;; Based on trixx
(defn parse-integer [s]
    (try (Integer/parseInt s) 
         (catch NumberFormatException nfe 0)))

(defmulti otp-value class)

(defmethod otp-value OtpErlangBoolean [o] (.booleanValue o))
(defmethod otp-value OtpErlangAtom    [o] (.atomValue o))

(defmethod otp-value OtpErlangBinary  [o] (String. (.binaryValue o)))

(defmethod otp-value OtpErlangChar    [o] (parse-integer (str o)))
(defmethod otp-value OtpErlangByte    [o] (parse-integer (str o)))
(defmethod otp-value OtpErlangShort   [o] (parse-integer (str o)))
(defmethod otp-value OtpErlangUShort  [o] (parse-integer (str o)))
(defmethod otp-value OtpErlangInt     [o] (parse-integer (str o)))
(defmethod otp-value OtpErlangUInt    [o] (parse-integer (str o)))
(defmethod otp-value OtpErlangLong    [o]
  (if (.isLong o)
      (.longValue  o)
      (.bigIntegerValue o)))
(defmethod otp-value OtpErlangFloat   [o] (float (.floatValue o)))
(defmethod otp-value OtpErlangDouble  [o] (float (.floatValue o)))
(defmethod otp-value OtpErlangString  [o] (str (.stringValue o)))
(defmethod otp-value OtpErlangList    [o] (with-meta (map otp-value (.elements o)) {:otp-type "List"}))
(defmethod otp-value OtpErlangTuple   [o] (with-meta (map otp-value (.elements o)) {:otp-type "Tuple"}))
(defmethod otp-value nil              [o] "")
(defmethod otp-value OtpErlangObject  [o] o)

(defmulti  as-seq class)
(defmethod as-seq OtpErlangList  [o] (seq (.elements o)))
(defmethod as-seq OtpErlangTuple [o] (seq (.elements o)))


;;Clojure to OtpErlang coercion

(defn otp-double [v] (OtpErlangDouble. (double v)))

(defn otp-long [v] (OtpErlangLong. (long v)))

(defn otp-string [#^String v] (OtpErlangString. v))

(defn otp-binary [#^String v] (OtpErlangBinary. (.getBytes v)))

(defn otp-atom [v] (OtpErlangAtom. v))

(defn otp-tuple [& args]
  (OtpErlangTuple. (into-array OtpErlangObject args)))

(defn otp-list [& args]
  (OtpErlangList. (into-array OtpErlangObject args)))