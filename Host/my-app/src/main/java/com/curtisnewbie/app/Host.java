package com.curtisnewbie.app;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Host extends Application {

    // modify it and the one in Client program, if necessary
    private static final int PORT = 7000;

    /** The View of this program */
    private GamePane gamePane;

    private ServerSocket server;
    private Socket socket;
    // InputStream from client
    private DataInputStream in;
    // OutputStream to client
    private DataOutputStream out;

    @Override
    public void start(Stage priStage) {
        gamePane = new GamePane();
        gamePane.setDisable(true);
        Scene s = new Scene(gamePane, 500, 500);
        priStage.setScene(s);
        priStage.setTitle("Host");
        priStage.show();
        System.out.println("GUI Initated");

        priStage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        new Thread(() -> {
            // connect to client
            makeConnection();

            System.out.println("Starting Game");
            startGame();
        }).start();
    }

    /** Wait for client to connect */
    private void makeConnection() {
        try {
            // setup server
            server = new ServerSocket(PORT);
            System.out.println("Waiting for Connection");
            socket = server.accept();
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to Client : " + socket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** Start the game, Host always starts first */
    private void startGame() {
        gamePane.setDisable(false);
        int[] lastStep = null;

        // the game stops when it is full (all cells have been selected) or one player
        // wins. The pane itself disables all the buttons when it wins, so we only
        // need to check whether it is full.
        try {
            while (!gamePane.isFull() || !gamePane.hasWon()) {

                System.out.println("Wait For User to select");

                while ((lastStep = gamePane.getLastStep()) == null) {
                    // Host starts first, wait for user to start
                    Thread.sleep(100);
                    System.out.println("Waiting");
                }
                // user has moved
                System.out.println("Moved");
                System.out.println(Arrays.toString(lastStep));

                // tell the client which step the user moved
                out.writeInt(lastStep[0]);
                out.writeInt(lastStep[1]);
                out.flush();

                int row = in.readInt();
                int col = in.readInt();
                // update the game pane as opponent (client) moved.
                gamePane.opponentMove(row, col);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}