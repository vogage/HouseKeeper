

//#!/usr/bin/env node
//
// WebSocket chat server
// Implemented using Node.js
//
// Requires the websocket module.
//
// WebSocket and WebRTC based multi-user chat sample with two-way video
// calling, including use of TURN if applicable or necessary.
//
// This file contains the JavaScript code that implements the server-side
// functionality of the chat system, including user ID management, message
// reflection, and routing of private messages, including support for
// sending through unknown JSON objects to support custom apps and signaling
// for WebRTC.
//
// Requires Node.js and the websocket module (WebSocket-Node):
//
//  - http://nodejs.org/
//  - https://github.com/theturtle32/WebSocket-Node
//
// To read about how this sample works:  http://bit.ly/webrtc-from-chat
//
// Any copyright is dedicated to the Public Domain.
// http://creativecommons.org/publicdomain/zero/1.0/

"use strict";

// var http = require('http');
// var https = require('https');
// var fs = require('fs');
// var WebSocketServer = require('websocket').server;

import { createServer } from 'http';
import { createServer as _createServer } from 'https';
import { readFileSync } from 'fs';
import { server as WebSocketServer } from 'websocket';


// Pathnames of the SSL key and certificate files to use for
// HTTPS connections.

//const keyFilePath = "/etc/pki/tls/private/mdn-samples.mozilla.org.key";
//const certFilePath = "/etc/pki/tls/certs/mdn-samples.mozilla.org.crt";

// Used for managing the text chat user list.

var connectionArray = [];
var nextID = Date.now();
var appendToMakeUnique = 1;

// Output logging information to console

function log(text) {
  var time = new Date();

  console.log("[" + time.toLocaleTimeString() + "] " + text);
}

// If you want to implement support for blocking specific origins, this is
// where you do it. Just return false to refuse WebSocket connections given
// the specified origin.
function originIsAllowed(origin) {
  return true;    // We will accept all connections
}

// Scans the list of users and see if the specified name is unique. If it is,
// return true. Otherwise, returns false. We want all users to have unique
// names.
function isUsernameUnique(name) {
  var isUnique = true;
  var i;

  for (i=0; i<connectionArray.length; i++) {
    if (connectionArray[i].username === name) {
      isUnique = false;
      break;
    }
  }
  return isUnique;
}

// Sends a message (which is already stringified JSON) to a single
// user, given their username. We use this for the WebRTC signaling,
// and we could use it for private text messaging.
function sendToOneUser(target, msgString) {
  var isUnique = true;
  var i;

  for (i=0; i<connectionArray.length; i++) {
    if (connectionArray[i].username === target) {
      connectionArray[i].sendUTF(msgString);
      break;
    }
  }
}

// Scan the list of connections and return the one for the specified
// clientID. Each login gets an ID that doesn't change during the session,
// so it can be tracked across username changes.
function getConnectionForID(id) {
  var connect = null;
  var i;

  for (i=0; i<connectionArray.length; i++) {
    if (connectionArray[i].clientID === id) {
      connect = connectionArray[i];
      break;
    }
  }

  return connect;
}

// Builds a message object of type "userlist" which contains the names of
// all connected users. Used to ramp up newly logged-in users and,
// inefficiently, to handle name change notifications.
function makeUserListMessage() {
  var userListMsg = {
    type: "userlist",
    users: []
  };
  var i;

  // Add the users to the list

  for (i=0; i<connectionArray.length; i++) {
    userListMsg.users.push(connectionArray[i].username);
  }

  return userListMsg;
}

// Sends a "userlist" message to all chat members. This is a cheesy way
// to ensure that every join/drop is reflected everywhere. It would be more
// efficient to send simple join/drop messages to each user, but this is
// good enough for this simple example.
function sendUserListToAll() {
  var userListMsg = makeUserListMessage();
  var userListMsgStr = JSON.stringify(userListMsg);
  var i;

  for (i=0; i<connectionArray.length; i++) {
    connectionArray[i].sendUTF(userListMsgStr);
  }
}


// Try to load the key and certificate files for SSL so we can
// do HTTPS (required for non-local WebRTC).

var httpsOptions = {
  key: null,
  cert: null
};

try {
  httpsOptions.key = readFileSync(keyFilePath);
  try {
    httpsOptions.cert = readFileSync(certFilePath);
  } catch(err) {
    httpsOptions.key = null;
    httpsOptions.cert = null;
  }
} catch(err) {
  httpsOptions.key = null;
  httpsOptions.cert = null;
}

// If we were able to get the key and certificate files, try to
// start up an HTTPS server.

var webServer = null;

try {
  if (httpsOptions.key && httpsOptions.cert) {
    webServer = _createServer(httpsOptions, handleWebRequest);
    log("https server created");
  }
} catch(err) {
  webServer = null;
}

if (!webServer) {
  try {
    webServer = createServer({}, handleWebRequest);
    //this line is initializing an HTTP server with no specific options and 
    //providing a callback function handleWebRequest to handle incoming requests.
    // If you need further clarification or assistance, feel free to ask!
    log("http server created");
  } catch(err) {
    webServer = null;
    log(`Error attempting to create HTTP(s) server: ${err.toString()}`);
  }
}


// Our HTTPS server does nothing but service WebSocket
// connections, so every request just returns 404. Real Web
// requests are handled by the main server on the box. If you
// want to, you can return real HTML here and serve Web content.

function handleWebRequest(request, response) {
  log ("Received request for " + request.url);
  response.writeHead(404);
  response.end();
}

// Spin up the HTTPS server on the port assigned to this sample.
// This will be turned into a WebSocket port very shortly.

// webServer.listen(6503,function() {
//   log("Server is listening on port 6503");
// });

webServer.listen(6503,"0.0.0.0",function() {
  log("Server is listening on port 6503");
});

// Create the WebSocket server by converting the HTTPS server into one.

var wsServer = new WebSocketServer({
  httpServer: webServer,
  autoAcceptConnections: false
});

if (!wsServer) {
  log("ERROR: Unable to create WbeSocket server!");
}else{
    log("Successfully Created the WebSocket server!");
}

// Set up a "connect" message handler on our WebSocket server. This is
// called whenever a user connects to the server's port using the
// WebSocket protocol.


  // enum class SignalingCommand {
  //   STATE, // Command for WebRTCSessionState
  //   OFFER, // to send or receive offer
  //   ANSWER, // to send or receive answer
  //   ICE // to send and receive ice candidates
  // }

const AndroidMsgType = Object.freeze({
  STATE: "STATE",
  OFFER: "OFFER",
  ANSWER: "ANSWER",
  ICE:"ICE"
});

// enum class WebRTCSessionState {
//   Active, // Offer and Answer messages has been sent
//   Creating, // Creating session, offer has been sent
//   Ready, // Both clients available and ready to initiate session
//   Impossible // We have less than two clients
// }
const AndroidSessionState=Object.freeze({
  Active: "Active",
  Creating: "Creating",
  Ready: "Ready",
  Impossible: "Impossible"
});






wsServer.on('request', function(request) {
  if (!originIsAllowed(request.origin)) {
    request.reject();
    log("Connection from " + request.origin + " rejected.");
    return;
  }

  // Accept the request and get a connection.
  
  //var connection = request.accept("json", request.origin);
  var connection = request.accept(request.origin);

  // Add the new connection to our list of connections.

  log("Connection accepted from " + connection.remoteAddress + ".");

  connectionArray.push(connection);

  connection.clientID = nextID;
  nextID++;

  var andriod_session_state=AndroidSessionState.Impossible;
  
  connection.send(AndroidMsgType.STATE+" "+AndroidSessionState.Impossible);
  // judge the client num
  // only support two clients
  if(connectionArray.length==2){
    //the session state is ready 
    andriod_session_state=AndroidSessionState.Ready;
    connectionArray.forEach(clientConnection => {
     
      clientConnection.send(AndroidMsgType.STATE+" "+AndroidSessionState.Ready);
      log("connection Ready ClientID:  "+clientConnection.clientID)
    });
  }

  // private fun handleOffer(sessionId: UUID, message: String) {
  //   if (sessionState != WebRTCSessionState.Ready) {
  //       error("Session should be in Ready state to handle offer")
  //   }
  //   sessionState = WebRTCSessionState.Creating
  //   println("handling offer from $sessionId")
  //   notifyAboutStateUpdate()
  //   val clientToSendOffer = clients.filterKeys { it != sessionId }.values.first()
  //   clientToSendOffer.send(message)
  // }



  // Set up a handler for the "message" event received over WebSocket. This
  // is a message sent by a client, and may be text to share with other
  // users, a private message (text or signaling) for one user, or a command
  // to the server.

  connection.on('message', function(message) {
    if (message.type === 'utf8') {
     // log("Received Message: " + message.utf8Data);

      var msg=message.utf8Data;

      if(msg.startsWith("OFFER")){
      // Process incoming data.
        log("the message is OFFER");
        if(andriod_session_state==AndroidSessionState.Ready){
         
          andriod_session_state=AndroidSessionState.Creating;
          log( "send state message Creating");
          //renew the client state to creating
          connectionArray.forEach(clientConnection => {
            
            clientConnection.send(AndroidMsgType.STATE+" "+AndroidSessionState.Creating);
            log("connection Creting ClientId: "+ clientConnection.clientID);
            
          });

          connectionArray.forEach(ClientToSendOffer =>{
            if(ClientToSendOffer.clientID!=connection.clientID){
              log("ClientToSendOffer.clientID: "+ClientToSendOffer.clientID);
              log("connection.clientId: "+connection.clientID);
              ClientToSendOffer.send(msg);
            }
          });
        }
      }else if(msg.startsWith("ICE")){
      //   private fun handleIce(sessionId: UUID, message: String) {
      //     println("handling ice from $sessionId")
      //     val clientToSendIce = clients.filterKeys { it != sessionId }.values.first()
      //     clientToSendIce.send(message)
      // }
        log("the message is ICE");
        connectionArray.forEach(ClientToSendICE => {
          if(ClientToSendICE.clientID!=connection.clientID){
            ClientToSendICE.send(msg);
          }
        })


      }else if(msg.startsWith("ANSWER")){
        log("the message is ANSWER");
        connectionArray.forEach(ClientToSendAnswer =>{
          if(ClientToSendAnswer.clientID!=connection.clientID){
            ClientToSendAnswer.send(msg);
          }
        })
        andriod_session_state=AndroidSessionState.Active;
          //renew the client state to Active
        connectionArray.forEach(clientConnection => {
          
          clientConnection.send(AndroidMsgType.STATE+" "+AndroidSessionState.Active);
         
          
        });
      }
    }
  });

  // Handle the WebSocket "close" event; this means a user has logged off
  // or has been disconnected.
  connection.on('close', function(reason, description) {
    // First, remove the connection from the list of connections.
    connectionArray = connectionArray.filter(function(el, idx, ar) {
      return el.connected;
    });

    // Now send the updated user list. Again, please don't do this in a
    // real application. Your users won't like you very much.
    sendUserListToAll();

    // Build and output log output for close information.

    var logMessage = "Connection closed: " + connection.remoteAddress + " (" +
                     reason;
    if (description !== null && description.length !== 0) {
      logMessage += ": " + description;
    }
    logMessage += ")";
    log(logMessage);
  });
});