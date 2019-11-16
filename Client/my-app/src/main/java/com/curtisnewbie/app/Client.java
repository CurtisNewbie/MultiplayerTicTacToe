package com.curtisnewbie.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

    // modify it and the one in Host program, if necessary
    private static final int PORT = 7000;

    // modify it if necessary
    private static final String IP = "localhost";

    /** The View of this proram */
    private GamePane gamePane;

    private Socket socket;

    // InputStream from Host
    private DataInputStream in;
    // OutputStream to Host
    private DataOutputStream out;

    public void start(Stage priStage) {
        // Initiate gui
        gamePane = new GamePane();
        Scene s = new Scene(gamePane, 500, 500);
        priStage.setScene(s);
        priStage.setTitle("Client");
        priStage.show();

        // Terminate the program when primary stage being closes
        priStage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        // Connect to Host and Start the Game
        new Thread(() -> {
            gamePane.setDisable(true);
            // connect to Host
            makeConnection();
            // start the game
            gamePane.setDisable(false);
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

    /**
     * Start the game.<br>
     * <br>
     * This method follows the logic that:<br>
     * 1. Host always starts first, so we wait for Host to move. <br>
     * 2. Then it's Client's turn to move, waiting for user to move using a While
     * loop.<br>
     * 3. Send the data (the step that user moved to) to the Host. <br>
     * 4. Repeat this process until the game finishes.
     */
    private void startGame() {
        System.out.println("Starting Game");
        int[] lastStep = null;

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