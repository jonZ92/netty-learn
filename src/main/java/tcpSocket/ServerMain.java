package tcpSocket;

import tcpSocket.server.TcpServerSocket;

/**
 * @author jon 2021:09:04
 */


public class ServerMain {

    public static void main(String[] args) {
        TcpServerSocket tcp=new TcpServerSocket(null,null);
        tcp.init();
    }
}
