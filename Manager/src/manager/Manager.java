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

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Manager {

    private ServerSocket server;
    private static final int MANAGER_PORT = 2099;

    private static final int FILE_SERVER_PORT = 2090;
    private static final String FILE_SERVER_IP = "127.0.0.1";

    // Setting up the server in the given port
    public Manager(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Manager ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Keep runing and accepting all requisitions, then calling the method to
    // save the files
    public void run() {
        while (true) {
            /*try {
                Socket clientSocket = server.accept();
                attendRequisition(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            try {
                Socket clientSocket = server.accept();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
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

            switch (command) {
                case "put":
                    String response = sendFileToServer(command, fileName, contentFile);
                    pw.println(response);
                    
                    updateRegistryTable();
                    break;

                case "get":

                    break;

                case "delete":

                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String sendFileToServer(String command, String fileName, String contentFile) {
        try {
            Socket fileServerSocket = new Socket(FILE_SERVER_IP, FILE_SERVER_PORT);
            
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
