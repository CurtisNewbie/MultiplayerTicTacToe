package com.curtisnewbie.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Client-side Terminal of TicTacToe Game
 * 
 * @author Yongjie Zhuang
 * 
 */
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
        // Initiate gui
        gamePane = new GamePane();
        Scene s = new Scene(gamePane, 500, 500);
        priStage.setScene(s);
        priStage.setTitle("Host");
        priStage.show();

        // Terminate the program when primary stage being closed
        priStage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        // Connect to Client and Start the Game
        new Thread(() -> {
            gamePane.freeze();
            // connect to client
            makeConnection();
            // start the game
            gamePane.unfreeze();
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

    /**
     * Start the game.<br>
     * <br>
     * This method follows the logic that:<br>
     * 1. The Host starts first,<br>
     * 2. waits for user (Host) to move,<br>
     * 3. send the data (the step that the user moved to) to the Client,<br>
     * 4. repeat this process until the game finishes.
     * 
     */
    private void startGame() {
        System.out.println("Game Started\n");
        int[] lastStep = null;
        gamePane.freeze();
        try {
            while (true) {
                // it's user's turn to move
                gamePane.unfreeze();

                System.out.println("Wait For User to select");
                while ((lastStep = gamePane.getLastStep()) == null) {
                    // wait for user to start
                    Thread.sleep(10);
                }
                // user has moved
                gamePane.freeze();

                // tell the Opponent/client which step the user moved
                out.writeInt(lastStep[0]);
                out.writeInt(lastStep[1]);
                out.flush();

                if (gamePane.hasWon() || gamePane.isFull())
                    break;

                // Opponent/ Client has moved, update the gamePane
                int row = in.readInt();
                int col = in.readInt();
                gamePane.opponentMoveTo(row, col);

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
     * Close connection.<br>
     * <br>
     * This method closes the {@code Socket} and {@code ServerSocket} created for
     * connection. The {@code DataInputStream} and {@code DataOutputStream} are
     * closed when the {@code Socket} is closed.
     */
    private void closeConnection() {
        try {
            socket.close();
            server.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Create and show the dialog when connection lost. */
    private void showDisconnectDialog() {
        Platform.runLater(() -> {
            Alert dia = new Alert(AlertType.WARNING);
            dia.setTitle("Connection Lost");
            dia.setContentText("Connection to the client is lost.");
            dia.show();
        });
    }
}