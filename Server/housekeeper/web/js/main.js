/*
 *  Copyright (c) 2021 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree.
 */

'use strict';

const startButton = document.getElementById('startButton');
const hangupButton = document.getElementById('hangupButton');
hangupButton.disabled = true;

const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');

let pc;
let localStream;
// var is function scoped while let is block scoped
const signaling = new BroadcastChannel('webrtc');

signaling.onmessage = e => {//e: event
  if (!localStream) {
    console.log('not ready yet');
    return;
  }
  switch (e.data.type) {
    case 'offer':
      handleOffer(e.data);
      break;
    case 'answer':
      handleAnswer(e.data);
      break;
    case 'candidate':
      handleCandidate(e.data);
      break;
    case 'ready':
      // A second tab joined. This tab will initiate a call unless in a call already.
      if (pc) {
        console.log('already in call, ignoring');
        return;
      }
      makeCall();
      break;
    case 'bye':
      if (pc) {
        hangup();
      }
      break;
    default:
      console.log('unhandled', e);
      break;
  }
};

// async function makeCall() {
//   const configuration = {'iceServers': [{'urls': 'stun:stun.l.google.com:19302'}]}
//   const peerConnection = new RTCPeerConnection(configuration);
//   signalingChannel.addEventListener('message', async message => {
//       if (message.answer) {
//           const remoteDesc = new RTCSessionDescription(message.answer);
//           await peerConnection.setRemoteDescription(remoteDesc);
//       }
//   });
//   const offer = await peerConnection.createOffer();
//   await peerConnection.setLocalDescription(offer);
//   signalingChannel.send({'offer': offer});
// }


startButton.onclick = async () => {
  localStream = await navigator.mediaDevices.getUserMedia({audio: true, video: true});
  localVideo.srcObject = localStream;


  startButton.disabled = true;
  hangupButton.disabled = false;

  signaling.postMessage({type: 'ready'});
};

hangupButton.onclick = async () => {
  hangup();
  signaling.postMessage({type: 'bye'});
};

async function hangup() {
  if (pc) {
    pc.close();
    pc = null;
  }
  localStream.getTracks().forEach(track => track.stop());
  localStream = null;
  startButton.disabled = false;
  hangupButton.disabled = true;
};

function createPeerConnection() {
  const iceConfiguration = {
    iceServers: [
        {
            urls: 'stun:101.34.22.225:3478',
            username: 'housekeeper',
            credentials: 'housekeeper123'
        }
    ]
  
  }
  /*const iceConfiguration = {
    iceServers: [
        {
            urls: 'turn:my-turn-server.mycompany.com:19403',
            username: 'optional-username',
            credentials: 'auth-token'
        }
    ]
  
  }*/
  pc = new RTCPeerConnection(iceConfiguration);
  //pc = new RTCPeerConnection();
  pc.onicecandidate = e => {
    const message = {
      type: 'candidate',
      candidate: null,
    };
    if (e.candidate) {
      message.candidate = e.candidate.candidate;
      message.sdpMid = e.candidate.sdpMid;
      message.sdpMLineIndex = e.candidate.sdpMLineIndex;
    }
    signaling.postMessage(message);
  };
  pc.ontrack = e => remoteVideo.srcObject = e.streams[0];
  localStream.getTracks().forEach(track => pc.addTrack(track, localStream));
}

async function makeCall() {
  await createPeerConnection();

  const offer = await pc.createOffer();
  signaling.postMessage({type: 'offer', sdp: offer.sdp});
  await pc.setLocalDescription(offer);
}

async function handleOffer(offer) {
  if (pc) {
    console.error('existing peerconnection');
    return;
  }
  await createPeerConnection();
  await pc.setRemoteDescription(offer);

  const answer = await pc.createAnswer();
  signaling.postMessage({type: 'answer', sdp: answer.sdp});
  await pc.setLocalDescription(answer);
}

async function handleAnswer(answer) {
  if (!pc) {
    console.error('no peerconnection');
    return;
  }
  await pc.setRemoteDescription(answer);
}

async function handleCandidate(candidate) {
  if (!pc) {
    console.error('no peerconnection');
    return;
  }
  if (!candidate.candidate) {
    await pc.addIceCandidate(null);
  } else {
    await pc.addIceCandidate(candidate);
  }
}

// // Listen for local ICE candidates on the local RTCPeerConnection
// peerConnection.addEventListener('icecandidate', event => {
//   if (event.candidate) {
//       signalingChannel.send({'new-ice-candidate': event.candidate});
//   }
// });

// // Listen for remote ICE candidates and add them to the local RTCPeerConnection
// signalingChannel.addEventListener('message', async message => {
//   if (message.iceCandidate) {
//       try {
//           await peerConnection.addIceCandidate(message.iceCandidate);
//       } catch (e) {
//           console.error('Error adding received ice candidate', e);
//       }
//   }
// });

// // Listen for connectionstatechange on the local RTCPeerConnection
// peerConnection.addEventListener('connectionstatechange', event => {
//   if (peerConnection.connectionState === 'connected') {
//       // Peers connected!
//   }
// });
