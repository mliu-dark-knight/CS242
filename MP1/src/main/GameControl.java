package main;

import javax.swing.*;

/**
 * Created by mengxiongliu on 9/5/16.
 */
public class GameControl {
    private GameState game;
    private GameGUI gameGUI;
    private int[] score;

    public GameControl() {
        game = new GameState();
        score = new int[]{0, 0};
        gameGUI = new GameGUI(this);
    }

    /**
     *
     * @param finalY
     * @param finalX
     * Event handler for each button on game board
     */
    public void pieceButtonHandler(int finalY, int finalX) {
        // if piece selected, move piece
        if (game.pieceSelected()) {
            if (game.movePiece(finalY, finalX)) {
                // refresh all buttons
                refreshBoard();
                // check mate pop up
                if (game.isCheckMate()) {
                    gameGUI.popupDialog("Game over");
                    newHandler();
                }
                // stale mate pop up
                if (game.isStaleMate()) {
                    gameGUI.popupDialog("Draw");
                    newHandler();
                }
                // check pop up
                else if (game.isChecked(game.getTurn()))
                    gameGUI.popupDialog("Check");
            }
            else
                gameGUI.popupDialog("Illegal move");
            // cancel selected piece
            game.cancelSelected();
        }
        // if piece not selected, select piece
        else
            game.selectPiece(finalY, finalX);
    }

    /**
     * Event handler for menu button "New"
     */
    public void newHandler() {
        gameGUI.closeFrame();
        newGame();
    }

    /**
     * Event handler for menu button "Undo"
     */
    public void undoHandler() {
        if (!game.undoMove())
            gameGUI.popupDialog("Illegal undo");
        else
            refreshBoard();
    }

    /**
     * Event handler for menu button "Redo"
     */
    public void redoHandler() {
        if (!game.redoMove())
            gameGUI.popupDialog("Illegal redo");
        else
            refreshBoard();
    }

    /**
     * Event handler for menu button "Resign"
     */
    public void resignHandler() {
        int winner = 1 - game.getTurn();
        score[winner] += 1;
        gameGUI.popupDialog(String.format("%s wins, White %d Black %d", winner == 1 ? "Black" : "White", score[0], score[1]));
        newHandler();
    }

    /**
     *
     * @param row
     * @param col
     * @return image icon at board position (row, col)
     */
    public ImageIcon getIcon(int row, int col) {
        Piece piece = game.getPieceAt(row, col);
        if (piece == null)
            return null;
        String prefix = "images/";
        String suffix = piece.getColor() + ".png";
        String pieceName = "";
        if (piece instanceof King)
            pieceName = "king";
        else if (piece instanceof Queen)
            pieceName = "queen";
        else if (piece instanceof Bishop)
            pieceName = "bishop";
        else if (piece instanceof Knight)
            pieceName = "knight";
        else if (piece instanceof Rook)
            pieceName = "rook";
        else
            pieceName = "pawn";
        return new ImageIcon(prefix + pieceName + suffix);
    }

    /**
     * Reset all image icon on board
     */
    private void refreshBoard() {
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                gameGUI.drawButton(y, x, getIcon(y, x));
    }

    /**
     * Start new game without changing the current score
     */
    public void newGame() {
        game = new GameState();
        gameGUI = new GameGUI(this);
    }

    public static void main(String args[]) {
        new GameControl();
    }
}
