package testing;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoadTestingClient {

    public static void main(String[] args) throws InterruptedException, IOException {
        List<Socket> sockets = new ArrayList<Socket>();

        System.out.println("Opening sockets");
//        Socket socket = new Socket("localhost", 8080);

        for (int i = 0; i < 3; i++) {
            try {
                sockets.add(new Socket("localhost", 8080));
                PrintWriter writer = new PrintWriter(sockets.get(i).getOutputStream());

                //PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.println("1" + " " + "1");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("*** Ошибка подключения к серверу!");
                return;
            }
        }

        System.out.print("Print any string to exit");
        new Scanner(System.in).next();

        // Closing connections
        System.out.print("Closing connections");
//        socket.close();

        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("error closing socket " + e.getMessage());
            }
        }
    }
}