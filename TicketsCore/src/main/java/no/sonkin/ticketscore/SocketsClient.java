package no.sonkin.ticketscore;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;
import no.sonkin.ticketscore.models.Comment;
import no.sonkin.ticketscore.models.Ticket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;


public class SocketsClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    // 85.166.124.249
    private final String ip = "ticketsbot.duckdns.org";
    private final int port = 1337;

    private final String token;
    private final String guild;

    private final String commentString = """
            {
                "action": "comment",
                "guild": 692406121020915743,
                "token": "nN9Br1Yw7pYcvpJztFL1BJiR_gl85yMh",
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
    private final String bufferString = """
            {
                "action": "buffer",
                "guild": 692406121020915743,
                "token": "nN9Br1Yw7pYcvpJztFL1BJiR_gl85yMh",
                "data": {}
            }
            """;

    public SocketsClient(String token, String guild) {
        this.token = token;
        this.guild = guild;
    }

    public static void main(String[] args) {
        SocketsClient client = new SocketsClient("nN9Br1Yw7pYcvpJztFL1BJiR_gl85yMh", "692406121020915743");
        try {
            client.startConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(client.fetchBuffer());
    }

    public void startConnection() throws IOException {
        try {
            clientSocket = new Socket(this.ip, this.port);
            //clientSocket.setKeepAlive(true);
            clientSocket.setSoTimeout(4000);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("Could not connect to socket server!");
            throw new IOException();
        }
    }

    /**
     * Send a new ticket to the discord bot
     *
     * @param ticket the new ticket
     */
    public String sendTicket(Ticket ticket) {
        try {
            String json = new CommonResponse(guild, token, "create", ticket).toJson();

            System.out.println(json);

            this.startConnection();
            //String response = this.sendAndReceiveMessage(json);
            String response = sendAndReceiveOnce(json);

            stopConnection();

            if (response == null) {
                System.out.println("ERROR! Did not get any response from socket");
                return null;
            }

            Any obj = JsonIterator.deserialize(response);
            if (!obj.get("error").toString().equals("") && obj.get("error") != null) {
                System.err.println("Discord bot returned error while trying to send ticket: " + obj.get("error"));
            }

            return obj.get("data").get("discordChannel").toString();
        } catch (JsonException | IOException e) {
            stopConnection();
            System.out.println(e.getMessage());
            // ex.printStackTrace();
            return null;
        }
    }

    public boolean sendComment(Comment comment) {
        try {
            String json = new CommonResponse(guild, token, "comment", comment).toJson();

            System.out.println(json);

            startConnection();

            String response = sendAndReceiveOnce(json);

            if (response == null) {
                System.err.println("ERROR! Socket did not receive confirmation when trying to send comment");
                return false;
            }

            System.out.println("RESPONSE: " + response);
            return true;

        } catch (IOException e) {
            stopConnection();
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String sendAndReceiveOnce(String msg) {
        out.println(msg);
        String line;
        String response = null;
        try {
            while ((line = in.readLine()) != null) {
                if (!line.equals("~~/START/~~")) {
                    response = line;
                    break;
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Error while sending and receiving once on socket: " + e.getMessage());
        }
        return response;
    }

    public String sendAndReceiveMessage(String msg) {
        try {
            out.println(msg);

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
            // e.printStackTrace();
            System.err.println("Error while sending and receiving on socket: " + e.getMessage());
            return null;
        }
    }

    /**
     * Method for pinging the discord bot & retrieving any stored items.
     * Once the bot has sent all its items, the plugin's stored items will be sent
     *
     * @return idk
     */
    public String fetchBuffer() {
        try {
            out.println(new CommonResponse(guild, token, "buffer", null));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = in.readLine()) != null) {
                if (line.equals("~~/START/~~"))
                    sb = new StringBuilder();
                else if (line.equals("~~/END/~~")) {
                    System.out.println(sb.toString());
                    sb.delete(0, sb.length());
                } else {
                    System.out.println("\nRECEIVED LINE");
                    System.out.println(line);
                    sb.append(line);
                    Any result = JsonIterator.deserialize(line);

                    if (result.get("action").toString().equals("noBuffer")) {
                        // TODO: send saved buffer now
                        break;
                    }

                    HashMap<String, String> stuff = new HashMap<>();

                    result.bindTo(stuff);
                    stuff.put("action", "bufferDel");
                    stuff.put("token", token);
                    String json = JsonStream.serialize(stuff);
                    System.out.println(json);
                    out.println(json);
                }
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public void stopConnection() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class CommonResponse {
        private final HashMap<String, Object> commonResponse;

        public CommonResponse(String guild, String token, String action, Object data) {
            commonResponse = new HashMap<>();
            commonResponse.put("action", action);
            commonResponse.put("guild", guild);
            commonResponse.put("token", token);
            commonResponse.put("data", data);
        }

        @Override
        public String toString() {
            return "CommonResponse{" +
                    "commonResponse=" + commonResponse +
                    '}';
        }

        public String toJson() {
            return JsonStream.serialize(commonResponse);
        }
    }
}

