package org.nhnacademy.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Client extends Thread{
    private Socket socket;
    private BufferedReader reader;

    private static List<Socket> clientList = null;
    public Client(Socket socket) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(clientList==null)
                clientList = new ArrayList<>();
        } catch (IOException e) {
            System.out.println(e);
        }
        clientList.add(this.socket);
    }

    private void chatHandler(String line) {
        for (Socket client : clientList) {
            try {
                if(client==this.socket)
                {
                    continue;
                }
                System.out.println(client);
                BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                if(line.equals("exit"))
                {
                    socket.close();
                }
                clientWriter.write(line);
                clientWriter.newLine();
                clientWriter.flush();


            } catch (IOException e) {
                System.out.println("error handling chat : " + e.getMessage());

            }
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("클라이언트가 연결되었습니다.");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("input : " + line);
                chatHandler(line);
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("socket error : " + e.getMessage());

        }

    }
}
public class Main{
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int port = 221;

        try {
            serverSocket = new ServerSocket(port);

            System.out.println("서버 소켓이 생성되었습니다.");
            while (!Thread.interrupted()) {
                System.out.println("클라이언트 연결을 기다립니다.");
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                client.start();
            }
        } catch (IOException e) {
            System.out.println(port + "사용중");
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("소켓 에러");
                }
            }
        }
    }
}

