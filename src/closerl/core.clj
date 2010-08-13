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
  [s]
  (.accept s))

(defn otp-connect
  "Open a connection to a remote node."
  [s peer]
  (.connect s peer))
  
(defn otp-pid
  "Get the Erlang PID that will be used as the sender id in all 'anonymous' messages sent by this node."
  [s]
  (.pid s))
  
(defn otp-publish-port
  "Make public the information needed by remote nodes that may wish to connect to this one."
  [s]
  (.publishPort s))
  
(defn otp-unpublish-port
  "Unregister the server node's name and port number from the Erlang port mapper, thus preventing any new connections from remote nodes."
  [s]
  (.unPublishPort s))
  
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

;; Marshalling
;; Based on trixx
(defmulti otp-value class)

(defmethod otp-value OtpErlangBoolean [o] (.booleanotp-value o))
(defmethod otp-value OtpErlangAtom    [o] (.atomotp-value o))

(defmethod otp-value OtpErlangBinary  [o] (String. (.binaryotp-value o)))

(defmethod otp-value OtpErlangChar    [o] (Integer/parseInt (str o)))
(defmethod otp-value OtpErlangByte    [o] (Integer/parseInt (str o)))
(defmethod otp-value OtpErlangShort   [o] (Integer/parseInt (str o)))
(defmethod otp-value OtpErlangUShort  [o] (Integer/parseInt (str o)))
(defmethod otp-value OtpErlangInt     [o] (Integer/parseInt (str o)))
(defmethod otp-value OtpErlangUInt    [o] (Integer/parseInt (str o)))
(defmethod otp-value OtpErlangLong    [o]
  (if (.isLong o)
    (long o)
    (.bigIntegerotp-value o)))
(defmethod otp-value OtpErlangFloat   [o] (float (.floatotp-value o)))
(defmethod otp-value OtpErlangDouble  [o] (float (.floatotp-value o)))
(defmethod otp-value OtpErlangList    [o] (with-meta (map otp-value (.elements o)) {:otp-type "List"}))
(defmethod otp-value OtpErlangTuple   [o] (with-meta (map otp-value (.elements o)) {:otp-type "Tuple"}))
(defmethod otp-value nil              [o] "")
(defmethod otp-value OtpErlangObject  [o] o)

(defmulti  as-seq class)
(defmethod as-seq OtpErlangList  [o] (seq (.elements o)))
(defmethod as-seq OtpErlangTuple [o] (seq (.elements o)))