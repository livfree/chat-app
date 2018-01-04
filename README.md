# chat-app
Social media communication app consisting of multiple clients using multi threading, broadcast, and multicast communication, and application layer protocols. There are three different files that build upon the app, starting with the simple client-server communication, and ending with multicast capabilities of the finished Chat App product.

The chatApp_client_server file contains a simple application with a client and a server using socket interface. The server simply prints out the message that it receives from the client.

The chatApp_broadcast_status_update file builds on the client_server file, using multi-threading to implment the chat app, with one server and multiple clients. The clients and server will communicate over the network using TCP. It assumes that all clients are interested in getting notified of any status update posted by any one of them.

The chatApp_multicast file builds on the broadcast_status_update file, adding multicast(friendship) capability. The client is able to post status updates only to their friends, thus clients are able to add each other as friends.

Each file contains its own README with compiling instructions and more implementation details.
