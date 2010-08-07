(ns erlang.core
  (:import (com.ericsson.otp.erlang
              OtpNode
              OtpSelf 
              OtpErlangList 
              OtpErlangObject 
              OtpPeer)))

(defn otp-node
  "Creates an OtpNode"
  [node-name]
  (OtpNode. node-node))
  

(defn otp-mbox
  "Creates an OtpMbox"
  ([node]
    (. node createMbox))
  ([node name]
    (. node createMbox name)))
    
(defn otp-register-name
  "Registers the mbox name"
  [mbox name]
  (. mbox registerName name))
  
(defn otp-ping
  "Pings a remote Erlang node"
  [node remote tmo]
  (. node ping remote tmo))
              
(defn otp-self 
  "Creates OtpSelf with name & cookie"
  ([node-name]
    (OtpSelf. node-name))
  ([node-name cookie]
    (OtpSelf. node-name cookie)))

(defn otp-peer
  "Initializes peer"
  [name]
  (OtpPeer. name))

(defn connect
  "Connects to Erlang node"
  [s peer]
  (. s connect peer))
  
(defn otp-rpc-call
  "Performs a RPC call to remote node"
  ([connection m f]
    (. connection sendRPC m f (OtpErlangList.)))
  ([connection m f a]
    (. connection sendRPC m f a)))
  
(defn otp-receive
  "Receive result from RPC call"
  [connection]
  (. connection receiveRPC))