package com.curtisnewbie.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class Client extends Application {

    // modify it and the one in Host program, if necessary
    private static final int PORT = 7000;

    /** Default ip */
    private static final String DEF_IP = "localhost";

    /** Ip address provided by user */
    private String ip = null;

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
            gamePane.freeze();
            // connect to Host
            makeConnection();
            // start the game
            gamePane.unfreeze();
            startGame();
        }).start();
    }

    /** Connect to Host */
    private void makeConnection() {
        try {
            // ask for ip through dialog
            getIPThruAlert();

            // setup Client, use default IP if not provided
            socket = new Socket(ip == null ? DEF_IP : ip, PORT);
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
        System.out.println("Game Started\n");
        int[] lastStep = null;
        gamePane.freeze();
        try {

            // Host starts first, so we wait for signal
            gamePane.opponentMoveTo(in.readInt(), in.readInt());

            while (true) {
                // it's user's turn to move
                gamePane.unfreeze();

                System.out.println("Wait For User to select");
                while ((lastStep = gamePane.getLastStep()) == null) {
                    // wait for user to start
                    Thread.sleep(10);
                }
                // User has moved
                gamePane.freeze();

                // tell the Opponent/Host which step the user moved
                out.writeInt(lastStep[0]);
                out.writeInt(lastStep[1]);
                out.flush();

                if (gamePane.hasWon() || gamePane.isFull())
                    break;

                // Opponent/ Host has moved, update the gamePane
                int row = in.readInt();
                int col = in.readInt();
                gamePane.opponentMoveTo(row, col);
                System.out.println(row + " " + col);

                if (gamePane.hasWon() || gamePane.isFull())
                    break;
            }
            System.out.print("End");
            gamePane.freeze();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get IP address through Creating a {@code TextInputDialog}.
     *
     * @return IP address or {@code NULL} if not provided through dialog.
     */
    private void getIPThruAlert() {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("IP Input Dialog");
            dialog.setContentText("Enter Host's IP:");
            var response = dialog.showAndWait();
            if (response.isEmpty())
                ip = response.get();
            else
                ip = null;
        });
    }

}