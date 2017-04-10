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

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Client {

    private Socket clientSocket;
    private static final String MANAGER_ADDRESS = "127.0.0.1";
    private static final int MANAGER_PORT = 2099;

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Client(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(Socket clientSocket, String filePath) throws IOException {
        try {
            File file = new File(filePath);
            String fileName = file.getName();
            String encodedContent = encodeFileToBase64Binary(file);

            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            InputStream is = clientSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String workToDo = "put";
            String command = workToDo + " " + fileName + " " + encodedContent;

            pw.println(command);
            String response = br.readLine();

            pw.close();
            is.close();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println("Failed to send file.");
        }
    }

    public void downloadFile(Socket clientSocket, String fileName) throws IOException {
        try {
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);

            String workToDo = "get";
            String command = workToDo + " " + fileName;

            InputStream is = clientSocket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String[] receivedInformation = br.readLine().split("\\s+");

            fileName = receivedInformation[1];
            String contentFile = receivedInformation[2];

            byte[] data = Base64.getDecoder().decode(contentFile);

            OutputStream file = new FileOutputStream(fileName);
            file.write(data);

            System.out.println(br.readLine());

            pw.close();
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Maybe reate json message here
    }

    public void deleteFlie(Socket clientSocket, String fileName) throws IOException {
        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
        InputStream is = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String workToDo = "delete";
        String command = workToDo + " " + fileName;

        pw.println(command);
        System.out.println(br.readLine());

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Client client = new Client(MANAGER_ADDRESS, MANAGER_PORT);

        try {
            System.out.println("Wellcome!");

            Scanner scanner = new Scanner(System.in);
            int option = -1;
            String filePath;

            while (option != 0) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
