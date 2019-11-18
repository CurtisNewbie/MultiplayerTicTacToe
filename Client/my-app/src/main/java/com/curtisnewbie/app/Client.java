package com.curtisnewbie.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Client-side Terminal of TicTacToe Game
 * 
 * @author Yongjie Zhuang
 * 
 */
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

    /** The Dialog used to ask for IP address of Host */
    private TextInputDialog dialog;

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

        // ask for ip through dialog, wait for response until closed
        dialog = createIPDialog();
        var response = dialog.showAndWait();
        if (response.isPresent())
            ip = response.get();
        else
            ip = null;

        // Connect to Host and Start the Game
        new Thread(() -> {
            gamePane.freeze();
            try {
                // connect to Host
                makeConnection();

                // start the game
                gamePane.unfreeze();
                startGame();
            } catch (IOException e) {
                e.printStackTrace();
                showConnectionFailedDialog();
            }
        }).start();
    }

    /**
     * Connect to Host
     * 
     * @throws IOException if the connection failed (e.g., cannot connect to the
     *                     Host or connection declined)
     */
    private void makeConnection() throws IOException {
        // setup Client, use default IP if not provided
        socket = new Socket(ip == null ? DEF_IP : ip, PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Connected to Host : " + socket.getInetAddress().getHostAddress());
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
            System.out.println("End");
            gamePane.freeze();
        } catch (IOException e) {
            e.printStackTrace();
            showDisconnectDialog();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Create a {@code TextInputDialog} to get IP address
     *
     * @return {@code TextInputDialog} to get IP address
     */
    private TextInputDialog createIPDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("IP Input Dialog");
        dialog.setContentText("Enter Host's IP:");
        return dialog;
    }

    /** Create and show the dialog when connection lost. */
    private void showDisconnectDialog() {
        Platform.runLater(() -> {
            Alert dia = new Alert(AlertType.WARNING);
            dia.setTitle("Connection Lost");
            dia.setContentText("Connection to the host is lost.");
            dia.show();
        });
    }

    /** Create and show the dialog when connection failed. */
    private void showConnectionFailedDialog() {
        Platform.runLater(() -> {
            Alert dia = new Alert(AlertType.WARNING);
            dia.setTitle("Connection Failed");
            dia.setContentText("Cannot Connect to the Host");
            dia.show();
        });
    }

    /**
     * Close connection.<br>
     * <br>
     * This method closes the {@code Socket}. The {@code DataInputStream} and
     * {@code DataOutputStream} are closed when the {@code Socket} is closed.
     */
    private void closeConnection() {
        try {
            socket.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}