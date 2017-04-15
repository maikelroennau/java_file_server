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
import java.util.Arrays;
import org.json.JSONObject;
import utilities.AlphanumFileComparator;

/**
 *
 * @author Maikel Maciel Rönnau
 */
public class Server {

    private ServerSocket server;
    private static final int SERVER_PORT = 2090;
    private static final String FILE_SERVER_IP = "127.0.0.1";

    private static final String DEFAULT_PATH = "";

    private static final int REQUISITON_OK = 0;
    private static final int SERVER_UNAVAILABLE = 1;
    private static final int FILE_NOT_EXISTS = 2;
    private static final int FILE_ALREADY_EXISTS = 3;
    private static final int FILE_NOT_AVALIABLE = 4;

    private static final String ROOT = "storage/";

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
                    String response = saveBinaryFile(fileName, contentFile);

                    pw.println(response);

                    pw.close();
                    br.close();
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

    public String saveBinaryFile(String fileName, String encodedString) throws IOException {
        try {
            fileName += ".bin";

            File file = new File(getSaveLocation() + "/" + fileName);
            FileOutputStream os = new FileOutputStream(file);

            os.write(encodedString.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJSONMessage(REQUISITON_OK);
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

    public String getJSONMessage(int requisitionType) {
        JSONObject responseMessage = new JSONObject();

        switch (requisitionType) {
            case 0:
                responseMessage.put("returnCode", 0);
                responseMessage.put("returnDescription", "Requisition successful executed (inclusion/busca/deletion).");
                break;

            case 1:
                break;

            case 2:
                break;

            case 3:

                break;

            case 4:

                break;
        }

        return responseMessage.toString();
    }

    public String getSaveLocation() {
        if (!new File(ROOT).exists()) {
            new File(ROOT).mkdir();

            createFileTree(ROOT);

            return recursiveWalk(sortListFiles(new File(ROOT).listFiles(File::isDirectory)));
        } else {

            return recursiveWalk(sortListFiles(new File(ROOT).listFiles(File::isDirectory)));
        }
    }

    public static String recursiveWalk(File[] fileList) {

        for (File f : fileList) {
            if (f.listFiles().length < 36 && !f.getPath().substring(f.getPath().length() - 2).equals("35")) {
                return f.getPath();
            }

            if (f.getPath().substring(f.getPath().length() - 2).equals("35")) {
                createFileTree(f.getPath() + "/");
                return recursiveWalk(f.listFiles());
            }
        }

        return "None";
    }

    public static File[] sortListFiles(File[] files) {
        Arrays.sort(files, new AlphanumFileComparator());
        return files;
    }

    public static String createFileTree(String path) {
        for (int i = 0; i < 36; i++) {
            new File(path + i).mkdir();
        }

        return path + "0";
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server fileServer = new Server(SERVER_PORT);
        fileServer.run();
    }
}
