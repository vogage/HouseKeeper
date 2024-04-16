//
//  NativeSocketProvider.swift
//  WebRTC-Demo
//
//  Created by stasel on 15/07/2019.
//  Copyright © 2019 stasel. All rights reserved.
//

import Foundation
import UIKit

@available(iOS 13.0, *)
class NativeWebSocket: NSObject, WebSocketProvider {
    
    var delegate: WebSocketProviderDelegate?
    private let url: URL
    private var socket: URLSessionWebSocketTask?
    private lazy var urlSession: URLSession = URLSession(configuration: .default, delegate: self, delegateQueue: nil)

    init(url: URL) {
        self.url = url
        super.init()
    }

    private var webSocketTask: URLSessionWebSocketTask?
    
    func connect() {
        //let socket = urlSession.webSocketTask(with: url)
       // let socket = urlSession.webSocketTask(with: url, protocols: ["json"])
        let socket = urlSession.webSocketTask(with: url,protocols: ["json"])
       // In this case, -1011 specifically means "The server requires authentication to fulfill the request. Either the authentication credentials were missing or incorrect."
        
        socket.resume()
        self.socket = socket
        self.readMessage()
    }

    
    func send(data: Data) {
        self.socket?.send(.data(data)) { _ in }
    }
    
    func my_send(str:String){
        self.socket?.send(.string(str)){ _ in }
    }
    
    private func readMessage() {
        self.socket?.receive { [weak self] message in
            guard let self = self else { return }
            
            switch message {
            case .success(.data(let data)):
                self.delegate?.webSocket(self, didReceiveData: data)
               
                self.readMessage()
//                综上所述，self.delegate?.webSocket(self, didReceiveData: data) 这行代码的意思是：如果 self 有一个非空的委托对象（即 delegate 不是 nil），则调用该委托对象的 webSocket(_:didReceiveData:) 方法，通知它 WebSocket 对象（self）已经接收到了数据（data）。如果 delegate 是 nil，则不会执行任何操作。
                
                
            case .success(.string(let str)):
                debugPrint("Received string message: \(str)")
                self.delegate?.webSocket(self, didReceiveStr: str)
                //self.my_send(str:"hhhhhh")
                
                self.readMessage()
                
            case .success:
                debugPrint("Warning: Expected to receive data format but received a string. Check the websocket server config.")
                self.readMessage()
                

            case .failure:
                debugPrint("receive: \(message)")
                self.disconnect()
            }
        }
    }
    
    private func disconnect() {
        self.socket?.cancel()
        self.socket = nil
        self.delegate?.webSocketDidDisconnect(self)
    }
}

@available(iOS 13.0, *)
extension NativeWebSocket: URLSessionWebSocketDelegate, URLSessionDelegate  {
    func urlSession(_ session: URLSession, webSocketTask: URLSessionWebSocketTask, didOpenWithProtocol protocol: String?) {
        self.delegate?.webSocketDidConnect(self)
    }
    
    func urlSession(_ session: URLSession, webSocketTask: URLSessionWebSocketTask, didCloseWith closeCode: URLSessionWebSocketTask.CloseCode, reason: Data?) {
        self.disconnect()
    }
}
