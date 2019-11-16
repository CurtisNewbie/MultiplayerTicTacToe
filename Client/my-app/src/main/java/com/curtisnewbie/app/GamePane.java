package com.curtisnewbie.app;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.*;
import javafx.application.Platform;

/**
 * 
 * A {@code GridPane} that draws a TicTacToe game board, it has a number of
 * methods for getting information from the gui as well as methods to
 * control/update the gui. <br>
 * <br>
 * It should be noted that the current user (not opponent) is always represented
 * using "X", and the opponent is represented using "O". This may be fixed in
 * the future. <br>
 * 
 * 
 * @author Yongjie Zhuang
 * 
 */
public class GamePane extends GridPane {

    /** Current player is always CROSS */
    private final int CROSS = 1;

    /** Other player is always CIRCLE */
    private final int CIRCLE = 2;

    /** Has never been selected */
    private final int EMPTY = 0;

    /**
     * Indicate whether user has moved. This is for current user only not for the
     * opponent
     */
    private boolean moved;

    /**
     * List of buttons represent the nine squares on the tictaktoe game board, and
     * which are used to detect where the user clicks on.
     */
    private Button[][] buttons;

    /** Two dimensional array represents this game board */
    private int[][] gameBoard;

    /**
     * Record what the last step is. First element indicates row and second element
     * indicates column. This method is for current user rather than the opponant.
     */
    private int[] lastStep;

    public GamePane() {
        this.moved = false;
        this.gameBoard = new int[3][3];
        this.lastStep = new int[2];
        this.buttons = new Button[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new Button();
                buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        }

        // add the nine buttons to this pane
        addButtonsToPane();

        // setup the event handlers for the buttons, which update the two-dimentional
        // gameBoard when necessary
        setupEventHandlers();

        // make the child nodes grow vertically and horizontally
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100 / 3);
        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100 / 3);
        this.getColumnConstraints().addAll(col, col, col);
        this.getRowConstraints().addAll(row, row, row);
    }

    /**
     * Put the nine buttons (which represent the areas to be clicked by the users)
     * to the proper positions on this pane.
     * 
     */
    private void addButtonsToPane() {
        this.add(buttons[0][0], 0, 0);
        this.add(buttons[0][1], 1, 0);
        this.add(buttons[0][2], 2, 0);
        this.add(buttons[1][0], 0, 1);
        this.add(buttons[1][1], 1, 1);
        this.add(buttons[1][2], 2, 1);
        this.add(buttons[2][0], 0, 2);
        this.add(buttons[2][1], 1, 2);
        this.add(buttons[2][2], 2, 2);
    }

    private void setupEventHandlers() {
        buttons[0][0].setOnAction(new ClickHandler(0, 0));
        buttons[0][1].setOnAction(new ClickHandler(0, 1));
        buttons[0][2].setOnAction(new ClickHandler(0, 2));
        buttons[1][0].setOnAction(new ClickHandler(1, 0));
        buttons[1][1].setOnAction(new ClickHandler(1, 1));
        buttons[1][2].setOnAction(new ClickHandler(1, 2));
        buttons[2][0].setOnAction(new ClickHandler(2, 0));
        buttons[2][1].setOnAction(new ClickHandler(2, 1));
        buttons[2][2].setOnAction(new ClickHandler(2, 2));
    }

    /**
     * Check whether this step wins.
     * 
     * @param r row
     * @param c column
     * @return whether this step wins
     */
    public boolean hasWon() {

        // check each column
        for (int i = 0; i < 3; i++) {
            if (gameBoard[0][i] != EMPTY && gameBoard[0][i] == gameBoard[1][i] && gameBoard[1][i] == gameBoard[2][i]) {
                return true;
            }
        }

        // check each row
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][0] != EMPTY && gameBoard[i][0] == gameBoard[i][1] && gameBoard[i][1] == gameBoard[i][2]) {
                return true;
            }
        }

        // check diagonal
        if (gameBoard[0][0] != EMPTY && gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2])
            return true;
        if (gameBoard[0][2] != EMPTY && gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0])
            return true;

        return false;
    }

    /**
     * Whether the gameboard still has empty cell to select.
     * 
     * @return {@code true} if there are empty cells not being selected before,
     *         {@code false} if there is no empty cell.
     */
    public boolean isFull() {
        for (int[] row : gameBoard)
            for (int b : row)
                if (b == EMPTY)
                    return false;

        return true;
    }

    /**
     * This method updates the gameboard as the user (not opponenet) clicks on a
     * cell or button to draw "X". This method also updates {@code moved} variable,
     * as it indicates whether it's user to move now.
     */
    public void moveTo(int row, int col) {
        Platform.runLater(() -> {
            // current user has moved
            moved = true;
            // update gameboard
            gameBoard[row][col] = CROSS;
            buttons[row][col].setDisable(true);
            buttons[row][col].setText("X");
            lastStep[0] = row;
            lastStep[1] = col;

            // check whether current user wins
            if (hasWon()) {
                showWinningNotification();
            } else {
                if (isFull()) {
                    showFullNotification();
                }
            }
        });
    }

    /**
     * This method is used to update the gameboard as opponent click on a cell or
     * button to draw a "O". This method draws "O" on the gameboard instead of the
     * "X". This method also updates {@code moved} variable, as it indicates whether
     * it's user to move now.
     * 
     * @param row row
     * @param col col
     */
    public void opponentMoveTo(int row, int col) {
        Platform.runLater(() -> {
            // it's current user's turn to move
            moved = false;
            // update gameboard
            gameBoard[row][col] = CIRCLE;
            buttons[row][col].setDisable(true);
            buttons[row][col].setText("0");

            // check whether opponent wins
            if (hasWon()) {
                showLosingNotification();
            } else {
                if (isFull()) {
                    showFullNotification();
                }
            }
        });
    }

    /** Disable/ make all buttons unavailable */
    public void freeze() {
        this.setDisable(true);
    }

    /** Unfreeze/ make all buttons available */
    public void unfreeze() {
        this.setDisable(false);
    }

    /**
     * Get the last step that user went to
     * 
     * @return {@code Null} if user hasn't moved yet, else {@code int[]} where [0]
     *         is row and [1] is column.
     */
    public int[] getLastStep() {
        if (!hasMoved())
            return null;
        else
            return new int[] { lastStep[0], lastStep[1] };
    }

    /**
     * Check whether user has moved.
     * 
     * @return {@code True} when user has moved, {@code False} when user hasn't
     *         moved yet.
     */
    public boolean hasMoved() {
        return moved;
    }

    /** Show the winning notification by creating an Alert */
    private void showWinningNotification() {
        Platform.runLater(() -> {
            var dial = new Alert(AlertType.INFORMATION);
            dial.setContentText("You Win!");
            dial.showAndWait();
        });
    }

    /** Show the Losing notification by creating an Alert */
    private void showLosingNotification() {
        Platform.runLater(() -> {
            var dial = new Alert(AlertType.INFORMATION);
            dial.setContentText("You Lost!");
            dial.showAndWait();
        });
    }

    /**
     * Show the full notification (where all buttons have been selected) by creating
     * an Alert
     */
    private void showFullNotification() {
        Platform.runLater(() -> {
            var dial = new Alert(AlertType.INFORMATION);
            dial.setContentText("Ends, Nobody Wins!");
            dial.showAndWait();
        });
    }

    /** Handler for the nine buttons ActionEvent */
    private class ClickHandler implements EventHandler<ActionEvent> {

        private int row;
        private int col;

        public ClickHandler(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void handle(ActionEvent event) {
            // update gameBoard
            moveTo(row, col);
        }
    }
}