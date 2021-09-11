package fileSocket.client;


import fileSocket.client.socket.ClientSocket;
import fileSocket.pojo.FilePojo;

/**
 * @author jon 2021:09:11
 */


public class ClientMain {

    public static void main(String[] args) {
        FilePojo file=new FilePojo();
        file.setFilePath("C:\\Users\\Administrator\\Desktop\\");
        file.setBeging(0L);
        file.setFileName("meilisearch.exe");
        ClientSocket socket = new ClientSocket("127.0.0.1", 8801,file);
        socket.connect();
    }
}

