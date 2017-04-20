/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Manager {

    private ServerSocket server;
    private static final int MANAGER_PORT = 2080;

    private static final int FILE_SERVER_START_PORT = 2090;
    private static final int FILE_SERVER_END_PORT = 2094;
    private static final String FILE_SERVER_IP = "127.0.0.1";
    private static ArrayList<Socket> serverList = new ArrayList<>();
    public static int nextServer = 0;

    // Setting up the server in the given port
    public Manager(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Manager ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void discoverOnlineSevers(boolean continuosMode) {
        ArrayList<Socket> serversFound;

        if (continuosMode) {
            while (true) {
                if (!serverList.isEmpty()) {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                serversFound = new ArrayList<>();

                for (int i = FILE_SERVER_START_PORT; i <= FILE_SERVER_END_PORT; i++) {
                    try {
                        //Socket fileServerSocket = new Socket(FILE_SERVER_IP, i);
                        serversFound.add(new Socket(FILE_SERVER_IP, i));
                    } catch (IOException ex) {
                        //Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                serverList = serversFound;

                if (onlineServers() == 0) {
                    System.out.println("No server online, standby...");
                    discoverOnlineSevers(continuosMode);
                } else {
                    System.out.println(onlineServers() + " server(s) online.");
                }

            }
        } else {
            Socket server;
            serversFound = new ArrayList<>();

            for (int i = FILE_SERVER_START_PORT; i <= FILE_SERVER_END_PORT; i++) {
                try {
                    //Socket fileServerSocket = new Socket(FILE_SERVER_IP, i);
                    server = new Socket(FILE_SERVER_IP, i);
                    //server.setSoTimeout(200);
                    serversFound.add(server);
                } catch (IOException ex) {
                    //Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            serverList = serversFound;
        }
    }

    public void checkOnlineServers() {
        for (int i = 0; i < serverList.size(); i++) {
            try {
                PrintWriter pw = new PrintWriter(serverList.get(i).getOutputStream(), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(serverList.get(i).getInputStream()));
                
                pw.println("connectionCheck");
                String response = br.readLine();
                
                if (!response.equals("connected")) {
                    serverList.remove(i);
                }
            } catch (IOException e) {
                // Do nothing
            }
        }

    }

    public Socket getNextServer() {
        if (serverList.size() == 1) {
            return serverList.get(0);
        }

        if (nextServer + 1 > serverList.size()) {
            nextServer = 0;
        } else {
            nextServer++;
        }

        if (nextServer == 0) {
            return serverList.get(nextServer);
        } else {
            return serverList.get(nextServer - 1);
        }

    }

    public Socket getServer(int index) {
        if (!serverList.isEmpty()) {
            return serverList.get(index);
        }

        return null;
    }

    public boolean isServerOnline() {
        return !serverList.isEmpty();
    }

    public int onlineServers() {
        return serverList.size();
    }

    // Keep runing and accepting all requisitions, then calling the method to
    // save the files
    public void run() {
        while (true) {
            try {
                if (serverList.isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            discoverOnlineSevers(true);
                        }
                    }).start();
                }

                Socket clientSocket = server.accept();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!isServerOnline()) {
                                discoverOnlineSevers(false);
                            }
                            attendRequisition(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String attendRequisition(Socket clientSocket) throws IOException {
        try {
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String[] splitedCommand = br.readLine().split("\\s+");
            String command = splitedCommand[0];
            String fileName = splitedCommand[1];

            String contentFile = "";

            if (splitedCommand.length == 3) {
                contentFile = splitedCommand[2];
            }

            String response;

            switch (command) {
                case "put":
                    response = sendFileToServer(getNextServer(), command, fileName, contentFile);
                    pw.println(response);
                    break;

                case "get":
                    for (int i = 0; i < onlineServers(); i++) {
                        response = downloadFile(getServer(i), command, fileName);

                        String returnCode = new JSONObject(response).get("returnCode").toString();
                        if (returnCode.equals("5")) {
                            pw.println(response);
                            break;
                        }

                        if (i == onlineServers() - 1) {
                            pw.println(response);
                        }
                    }
                    break;

                case "delete":
                    for (int i = 0; i < onlineServers(); i++) {
                        response = deleteFile(getServer(i), command, fileName);

                        String returnCode = new JSONObject(response).get("returnCode").toString();
                        if (returnCode.equals("0")) {
                            pw.println(response);
                            break;
                        }

                        if (i == onlineServers() - 1) {
                            pw.println(response);
                        }
                    }
                    break;
            }

            pw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String sendFileToServer(Socket fileServerSocket, String command, String fileName, String contentFile) {
        try {
            //Socket fileServerSocket = new Socket(FILE_SERVER_IP, FILE_SERVER_PORT);

            PrintWriter pw = new PrintWriter(fileServerSocket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileServerSocket.getInputStream()));

            pw.println(command + " " + fileName + " " + contentFile);
            String response = br.readLine();

            pw.close();
            br.close();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Failed";
    }

    public String downloadFile(Socket fileServerSocket, String command, String fileName) {
        try {
            //Socket fileServerSocket = new Socket(FILE_SERVER_IP, FILE_SERVER_PORT);

            PrintWriter pw = new PrintWriter(fileServerSocket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileServerSocket.getInputStream()));

            pw.println(command + " " + fileName);
            String response = br.readLine();

            pw.close();
            br.close();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String deleteFile(Socket fileServerSocket, String command, String fileName) {
        try {
            PrintWriter pw = new PrintWriter(fileServerSocket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileServerSocket.getInputStream()));

            pw.println(command + " " + fileName);
            String response = br.readLine();

            pw.close();
            br.close();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void updateRegistryTable() {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Manager manager = new Manager(MANAGER_PORT);
        manager.run();
    }

}
