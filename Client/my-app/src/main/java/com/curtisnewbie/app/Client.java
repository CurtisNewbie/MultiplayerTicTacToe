package com.curtisnewbie.app;

import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

    // modify it and the one in Host program, if necessary
    private static final int PORT = 7000;

    // modify it if necessary
    private static final String IP = "localhost";

    /** The View of thi proram */
    private GamePane gamePane;

    private Socket socket;

    // InputStream from Host
    private DataInputStream in;
    // OutputStream to Host
    private DataOutputStream out;

    public void start(Stage priStage) {
        gamePane = new GamePane();
        gamePane.setDisable(true);
        Scene s = new Scene(gamePane, 500, 500);
        priStage.setScene(s);
        priStage.setTitle("Client");
        priStage.show();
        System.out.println("GUI Initated");

        priStage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        new Thread(() -> {
            // connect to Host
            makeConnection();

            System.out.println("Starting Game");
            startGame();
        }).start();
    }

    /** Connect to Host */
    private void makeConnection() {
        try {
            // setup Client
            socket = new Socket(IP, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to Host : " + socket.getInetAddress().getHostAddress());
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

                // Host starts first, so we wait for signal
                int row = in.readInt();
                int col = in.readInt();
                System.out.println("Host went for [" + row + "," + col + "]");
                // update the game pane as opponent (Host) moved.
                gamePane.opponentMove(row, col);

                while ((lastStep = gamePane.getLastStep()) == null) {
                    // Host starts first, wait for user to start

                }
                // user has moved
                System.out.println("Moved");
                System.out.println(Arrays.toString(lastStep));

                // tell the Host which step the user moved
                out.writeInt(lastStep[0]);
                out.writeInt(lastStep[1]);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}