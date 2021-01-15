package no.sonkin.ticketscore;

import java.io.*;
import java.net.Socket;
import static java.nio.charset.StandardCharsets.*;

public class SocketsClient {
    static Thread sent;
    static Thread receive;
    static Socket socket;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            // socket.setKeepAlive(true);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //String msg = in.readUTF();

            //System.out.println("Server: " + msg);

            //out.writeUTF("Ok Boss");  // Bad formatting cuz of utf-16
            out.write("Ok Boss".getBytes(UTF_8));
            out.flush();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
