package webSocket;



import webSocket.server.WebServerSocket;

/**
 * @author jon 2021:09:03
 */


public class ServerMain {
    public static void main(String[] args) {
        WebServerSocket socket=new WebServerSocket("192.168.247.129",8001);
        socket.init();
    }
}
