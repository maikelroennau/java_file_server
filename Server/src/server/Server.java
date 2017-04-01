/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Server {

    private ServerSocket server;

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
                Socket clientSock = server.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile(Socket clientSock) throws IOException {
        PrintWriter pr = new PrintWriter(clientSock.getOutputStream(), true);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));

        String[] splitedCommand = br.readLine().split("\\s+");
        
        String command = splitedCommand[0];
        String fileName = splitedCommand[1];
        String contentFile = splitedCommand[2];
        
        byte[] data = Base64.getDecoder().decode(contentFile);
        try {
            OutputStream file = new FileOutputStream(".\\" + fileName);
            file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }        
        
        pr.println("Olá, eu sou o servidor");

        System.out.println(br.readLine());
        pr.println("Servidor: Continuo por aqui");

        pr.close();
        clientSock.close();
        //server.close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server fileServer = new Server(2099);
        fileServer.run();
    }
}
