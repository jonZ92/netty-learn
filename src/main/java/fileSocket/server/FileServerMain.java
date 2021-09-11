package fileSocket.server;

import fileSocket.server.socket.ServerSocket;

/**
 * @author jon 2021:09:11
 */


public class FileServerMain {
    public static void main(String[] args) {
        ServerSocket socket = new ServerSocket("192.168.247.129", 7777);
        socket.bind();
    }
}
