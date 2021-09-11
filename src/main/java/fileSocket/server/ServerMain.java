package fileSocket.server;

import fileSocket.server.socket.ServerSocket;

/**
 * @author jon 2021:09:11
 */


public class ServerMain {
    public static void main(String[] args) {
        ServerSocket socket = new ServerSocket("127.0.0.1", 8801);
        socket.bind();
    }
}
