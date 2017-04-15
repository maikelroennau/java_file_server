/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
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

    private static final int FILE_RETURN = 5;

    private static final String ROOT = "storage/";
    private static final int MAX_ITEMS_PER_LOCATION = 36;

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

            String response;

            switch (command) {
                case "put":
                    response = saveBinaryFile(fileName, contentFile);

                    pw.println(response);

                    pw.close();
                    br.close();
                    break;

                case "get":
                    response = sendBase64File(fileName);

                    pw.println(response);

                    pw.close();
                    br.close();
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
            
            if (isFileExists(fileName)) {
                return getJSONMessage(FILE_ALREADY_EXISTS);
            }
            
            FileOutputStream os = new FileOutputStream(file);

            os.write(encodedString.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJSONMessage(REQUISITON_OK);
    }

    public boolean isFileExists(String fileName) throws IOException {
        File root = new File(ROOT);

        Collection files = FileUtils.listFiles(root, null, true);

        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (file.getName().equalsIgnoreCase(fileName)) {
                return true;
            }
        }

        return false;
    }

    public String sendBase64File(String fileName) throws IOException {
        try {
            File root = new File(ROOT);

            Collection files = FileUtils.listFiles(root, null, true);

            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.getName().equalsIgnoreCase(fileName + ".bin")) {
                    byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));

                    return getFileReturnJSONMessage(new String(encoded));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getJSONMessage(FILE_NOT_EXISTS);
    }

    public String getFileReturnJSONMessage(String content) {
        JSONObject responseMessage = new JSONObject();

        responseMessage.put("returnCode", 5);
        responseMessage.put("returnDescription", "File return");
        responseMessage.put("content", content);

        return responseMessage.toString();
    }

    public String getJSONMessage(int requisitionType) {
        JSONObject responseMessage = new JSONObject();

        switch (requisitionType) {
            case 0:
                responseMessage.put("returnCode", 0);
                responseMessage.put("returnDescription", "Requisition successful executed (inclusion/busca/deletion).");
                break;

            //case 1:
            //break;
            case 2:
                responseMessage.put("returnCode", 2);
                responseMessage.put("returnDescription", "File does not exists.");
                break;

            case 3:
                responseMessage.put("returnCode", 3);
                responseMessage.put("returnDescription", "File already exists.");
                break;

            //case 4:
            //break;
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
            if (f.listFiles().length < MAX_ITEMS_PER_LOCATION && !f.getPath().substring(f.getPath().length() - 2).equals(String.valueOf(MAX_ITEMS_PER_LOCATION - 1))) {
                return f.getPath();
            }

            if (f.getPath().substring(f.getPath().length() - 2).equals(String.valueOf(MAX_ITEMS_PER_LOCATION - 1))) {
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
        for (int i = 0; i < MAX_ITEMS_PER_LOCATION; i++) {
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
