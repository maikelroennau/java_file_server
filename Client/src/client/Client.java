/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;
import org.json.JSONObject;

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Client {

    private Socket clientSocket = null;
    private static final String MANAGER_ADDRESS = "127.0.0.1";
    private static final int MANAGER_START_PORT = 2080;
    private static final int MANAGER_END_PORT = 2082;
    
    private boolean connected = false;

    public Socket getClientSocket() {
        return clientSocket;
    }
    
    public boolean isConnectec() {
        return connected;
    }
    
    public void setConnected(boolean status) {
        connected = status;
    }

    public Client() {
        clientSocket = discoverManager();
    }

    public Socket discoverManager() {
        for (int i = MANAGER_START_PORT; i <= MANAGER_END_PORT; i++) {
            try {
                Socket manager = new Socket(MANAGER_ADDRESS, i);
                setConnected(true);
                return manager;
            } catch (IOException e) {
                clientSocket = null;
                setConnected(false);
            }
        }
        
        return null;
    }

    public void uploadFile(Socket clientSocket, String filePath) throws IOException {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("File does not exist.");
                return;
            }

            String fileName = file.getName();
            String encodedContent = encodeFileToBase64Binary(file);

            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            InputStream is = clientSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String workToDo = "put";
            String command = workToDo + " " + fileName + " " + encodedContent;

            pw.println(command);

            String response = br.readLine();
            printResponse(new JSONObject(response));

            pw.close();
            is.close();
            br.close();
        } catch (IOException e) {
            //System.out.println("Failed to send file.");
            e.printStackTrace();
        }
    }

    public void downloadFile(Socket clientSocket, String fileName) throws IOException {
        try {
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            InputStream is = clientSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String workToDo = "get";
            String command = workToDo + " " + fileName;

            pw.println(command);

            String response = br.readLine();

            String returnCone = new JSONObject(response).get("returnCode").toString();

            if (returnCone.equals("5")) {
                byte[] data = Base64.getDecoder().decode(new JSONObject(response).get("content").toString());

                OutputStream file = new FileOutputStream(fileName);
                file.write(data);

                file.flush();
                file.close();
                System.out.println("File sucessfull downloaded.");
            } else {
                printResponse(new JSONObject(response));
            }

            pw.close();
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFlie(Socket clientSocket, String fileName) throws IOException {
        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
        InputStream is = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String workToDo = "delete";
        String command = workToDo + " " + fileName;

        pw.println(command);

        String response = br.readLine();

        printResponse(new JSONObject(response));

        pw.close();
        is.close();
        br.close();
    }

    public static String encodeFileToBase64Binary(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        if (file.length() > Integer.MAX_VALUE) {
            // File is too large
        }

        byte[] bytes = new byte[(int) file.length()];

        int offset = 0;
        int numRead = 0;

        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();

        byte[] encoded = Base64.getEncoder().encode(bytes);

        return new String(encoded);
    }

    public void printResponse(JSONObject response) {
        //System.out.println(response.get("returnCode"));
        System.out.println(response.get("returnDescription"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Client client = new Client();
        
        if (!client.isConnectec()) {
            System.out.println("No manager(s) online, standby...");
            client.discoverManager();
        } else {
            try {
                System.out.println("Wellcome!");

                Scanner scanner = new Scanner(System.in);
                int option = -1;
                String filePath;

                while (option != 0) {
                    client = new Client();
                    
                    if (!client.isConnectec()) {
                        System.out.println("No manager(s) online, standby...");
                    } else {
                    
                        System.out.println("\nSelect an option:");
                        System.out.println("\n1 - Upload file");
                        System.out.println("2 - Download file");
                        System.out.println("3 - Delete file");
                        System.out.println("0 - Exit");
                        System.out.print("Option: ");
                        option = scanner.nextInt();

                        switch (option) {
                            case 1:
                                System.out.print("\nType the file path: ");
                                scanner.nextLine();
                                filePath = scanner.nextLine();

                                client.uploadFile(client.getClientSocket(), filePath);
                                break;

                            case 2:
                                System.out.print("\nType the file name to be downloaded: ");
                                scanner.nextLine();
                                filePath = scanner.nextLine();

                                client.downloadFile(client.getClientSocket(), filePath);
                                break;

                            case 3:
                                System.out.print("\nType the file name to deleted: ");
                                scanner.nextLine();
                                filePath = scanner.nextLine();

                                client.deleteFlie(client.getClientSocket(), filePath);
                                break;

                            case 0:
                                System.out.println("Exiting...");
                                break;

                            default:
                                System.out.println("Unknown command.");
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
