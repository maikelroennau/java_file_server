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
    private static final int PORT = 2090;

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
            try {
                Socket clientSock = server.accept();
                attendRequisition(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String attendRequisition(Socket clientSocket) throws IOException {
        try {
            PrintWriter pr = new PrintWriter(clientSocket.getOutputStream(), true);
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
    
    public String sendFileToServer(PrintWriter pr, BufferedReader br, String fileName, String contentFile) {
        
        
        return "";
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Manager manager = new Manager(PORT);
        manager.run();
    }

}
