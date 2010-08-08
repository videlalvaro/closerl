# Closerl #

This library is a bridge between Clojure and Erlang using the library JInterface.

The library is a simple wrapper around JInterface. I'm writing it as a way to learn Clojure.

So far the capabilities are really simply. My goal is to add more features while I learn more Clojure. Feel free to comment on the coding style, on how I can improve it to be more idiomatic Clojure.

## Usage ##

First launch erlang with the following command:

    erl -sname a

Then at the Clojure repl:

    (use 'closerl.core)

    (def self (otp-self "b"))

    (def peer (otp-peer "a@mrhyde"))

Once you got the nodes you can connect them:

    (def conn (otp-connect self peer))

Then we do a rpc_call to obtain the date on the remote node:

    (otp-rpc-call conn "erlang" "date")

And we call receive to fetch the reply:

    (otp-receive)

For the above code to work you will need the JInterface .jar in your java path.

## Installation ##

FIXME: write

## License ##

MIT