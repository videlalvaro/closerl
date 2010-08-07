(import '(com.ericsson.otp.erlang 
  OtpSelf 
  OtpErlangList 
  OtpErlangObject 
  OtpPeer))

(def self (new OtpSelf "client"))

(def other (new OtpPeer "a"))

(def other (new OtpPeer "a@mrhyde"))

(def connection (. self connect other))

(. connection sendRPC "erlang" "date" (new OtpErlangList))

(def received (. connection receiveRPC))