/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Base64;

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Client {

    private Socket client;
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 2099;

    public Client(String host, int port, String fileName, String encodedContent) {
        try {
            client = new Socket(host, port);
            sendFile(client, fileName, encodedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(Socket client, String fileName, String encodedContent) throws IOException {
        PrintWriter pr = new PrintWriter(client.getOutputStream(), true);

        InputStream is = client.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String workToDo = "put";
        String command = workToDo + " " + fileName + " " + encodedContent;
        
        pr.println(command);
        System.out.println(br.readLine());

        pr.println("Cliente: Continuo por aqui");
        System.out.println(br.readLine());

        pr.close();
        is.close();
        client.close();
    }

    public static String encodeFileToBase64Binary(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        
        byte[] bytes = new byte[(int) length];

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
        String encodedString = new String(encoded);

        return encodedString;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Wellcome!");

            Scanner scanner = new Scanner(System.in);
            int option = -1;

            while (option != 0) {
                System.out.println("\nSelect an option:");
                System.out.println("\n1 - Upload file");
                System.out.println("2 - Download file");
                System.out.println("3 - Delete file");
                System.out.println("0 - Exit");
                System.out.print("Option: ");
                option = scanner.nextInt();
                System.out.println("\n");

                switch (option) {
                    case 1:
                        System.out.print("Type the file path: ");
                        scanner.nextLine();
                        String path = scanner.nextLine();

                        File file = new File(path);
                        String fileName = file.getName();
                        String encodedContent = encodeFileToBase64Binary(file);

                        new Client(ADDRESS, PORT, fileName, encodedContent);
                        break;

                    case 2:

                        break;

                    case 3:

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
