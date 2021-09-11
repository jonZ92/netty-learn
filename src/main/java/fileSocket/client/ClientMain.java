package fileSocket.client;

import fileSocket.client.socket.ClientSocket;

/**
 * @author jon 2021:09:11
 */


public class ClientMain {

    public static void main(String[] args) {
        ClientSocket socket = new ClientSocket("127.0.0.1", 8801);
        socket.connect();
    }
}

