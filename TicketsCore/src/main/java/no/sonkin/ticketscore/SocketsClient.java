package no.sonkin.ticketscore;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import no.sonkin.ticketscore.models.Ticket;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SocketsClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    // 85.166.124.249
    private String ip = "85.166.124.249";
    private int port = 1337;

    private String createString = """
            {
                "action": "close",
                "guild": 692406121020915743,
                "token": "124asd14voknmo135r",
                "data": {
                    "ticketId": 14,
                    "description": "OMG Jeg trenger hjelp! Newfarm admin abuser!!!1 zomg",
                    "playerName": "Sonk1n",
                    "discordUser": "Sondre",
                    "playerUUID": "b1g-s7r1ng-1D",
                    "server": "server name",
                    "world": "world name",
                    "x": 153,
                    "y": 512,
                    "z": 120,
                    "created": 14125123213,
                    "updated": 51652342342,
                    "closed": true,
                    "closedBy": "Sondre",
                    "discordChannel": 802958441885990982
                }
            }
            """;
    private String commentString = """
            {
                "action": "comment",
                "guild": 692406121020915743,
                "token": "124asd14voknmo135r",
                "data": {
                    "ticketId": 123,
                    "cid": 802929984444301333,
                    "message": "Hei så fin ticket du har her",
                    "created": "12334567",
                    "playerName": "Sonk1n",
                    "admin": true
                }
            }
            """;


    public static void main(String[] args) {
        SocketsClient client = new SocketsClient();
        client.startConnection();
        System.out.println(client.sendAndReceiveMessage(client.createString));
    }

    public void startConnection() {
        try {
            clientSocket = new Socket(this.ip, this.port);
            clientSocket.setKeepAlive(true);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a new ticket to the discord bot
     *
     * @param ticket the new ticket
     */
    public void sendTicket(Ticket ticket) {
        String json = """
                {
                    "action": "create",
                    "guild": 692406121020915743,
                    "token": "124asd14voknmo135r",
                    "data": """ + ticket.toJson() + """
                }
                """;
        this.startConnection();
        System.out.println(ticket.toJson());
        //String response = this.sendAndReceiveMessage(json);
        String response = sendAndReceiveOnce(json);

        if (response == null) {
            System.out.println("ERROR! Did not get any response from socket");
            return;
        }

        try {
            Any obj = JsonIterator.deserialize(response);
            System.out.println(obj.get("data").get("discordChannel"));
        } catch (JsonException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        stopConnection();
    }

    public String sendAndReceiveOnce(String msg) {
        out.println(msg);
        String line;
        String response = null;
        try {
            while ((line = in.readLine()) != null) {
                if (!line.equals("~~/START/~~")) {
                    System.out.println(line);
                    response = line;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String sendAndReceiveMessage(String msg) {
        try {
            out.println(msg);

            //out.println('{}');

            //String resp = in.readLine();

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = in.readLine()) != null) {
                if (line.equals("~~/START/~~"))
                    sb = new StringBuilder();
                else if (line.equals("~~/END/~~")) {
                    System.out.println(sb.toString());
                    sb.delete(0, sb.length());
                } else
                    sb.append(line);
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readBuff(ByteBuffer myByteBuffer) {
        if (myByteBuffer.hasArray()) {
            return new String(myByteBuffer.array(),
                    myByteBuffer.arrayOffset() + myByteBuffer.position(),
                    myByteBuffer.remaining(), UTF_8);
        } else {
            final byte[] b = new byte[myByteBuffer.remaining()];
            myByteBuffer.duplicate().get(b);
            return new String(b);
        }
    }


    public void old() {
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            socket.setKeepAlive(true);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //String msg = in.readUTF();

            //System.out.println("Server: " + msg);

            //out.writeUTF("Ok Boss");  // Bad formatting cuz of utf-16
            out.write("infinite".getBytes(UTF_8));
            out.flush();
            boolean go = true;
            System.out.println("waiting");
            String msg = new String(in.readAllBytes(), UTF_8);
            System.out.println("got it");
            System.out.println(msg);

            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
