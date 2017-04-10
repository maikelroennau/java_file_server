/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Server {

    private ServerSocket server;
    private static final int SERVER_PORT = 2090;
    private static final String FILE_SERVER_IP = "127.0.0.1";

    private static final String DEFAULT_PATH = "";

    // Setting up the server in the given port
    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Keep runing and accepting all requisitions, then calling the method to
    // save the files
    public void run() {
        while (true) {
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

    public void attendRequisition(Socket clientSocket) throws IOException {
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

                    break;

                case "get":

                    break;

                case "delete":

                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBinaryFile(String fileName, String encodedString) throws IOException {
        try {
            fileName += ".bin";

            File file = new File(fileName);
            FileOutputStream os = new FileOutputStream(file);

            os.write(encodedString.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create json message here
    }

    public void sendBase64File(Socket clientSocket, String fileName) throws IOException {
        try {
            File file = new File(fileName + ".bin");
            FileInputStream is = new FileInputStream(file);

            // Continue from here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSaveLocation() {
        return "";
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server fileServer = new Server(SERVER_PORT);
        fileServer.run();
    }
}
