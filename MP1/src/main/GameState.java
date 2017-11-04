package main;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class GameState {
    private Piece[][] pieces;
    private King[] Kings;
    // 0 / 1
    private int turn;
    private int boardRow;
    private int boardCol;
    // n'th round
    private int round;
    private int selectedRow;
    private int selectedCol;
    private Pair<Position, Position> undoBuffer;
    private Pair<Position, Position> redoBuffer;
    private Piece captive;

    public GameState() {
        boardRow = 8;
        boardCol = 8;
        pieces = new Piece[boardRow][boardCol];
        initPieces();
        // 0 goes first
        turn = 0;
        round = 0;
    }

    private void initPieces() {
        for (int y = 0; y < boardRow; y++)
            for (int x = 0; x < boardCol; x++)
                pieces[y][x] = null;
        // pawn
        for (int x = 0; x < 8; x ++) {
            pieces[1][x] = new Pawn(1, x, 1);
            pieces[6][x] = new Pawn(6, x, 0);
        }
        // rook
        pieces[0][0] = new Rook(0, 0, 1);
        pieces[0][7] = new Rook(0, 7, 1);
        pieces[7][0] = new Rook(7, 0, 0);
        pieces[7][7] = new Rook(7, 7, 0);
        // knight
        pieces[0][1] = new Knight(0, 1, 1);
        pieces[0][6] = new Knight(0, 6, 1);
        pieces[7][1] = new Knight(7, 1, 0);
        pieces[7][6] = new Knight(7, 6, 0);
        // bishop
        pieces[0][2] = new Bishop(0, 2, 1);
        pieces[0][5] = new Bishop(0, 5, 1);
        pieces[7][2] = new Bishop(7, 2, 0);
        pieces[7][5] = new Bishop(7, 5, 0);
        // queen
        pieces[0][3] = new Queen(0, 3, 1);
        pieces[7][3] = new Queen(7, 3, 0);
        // king
        Kings = new King[2];
        Kings[0] = new King(7, 4, 0);
        Kings[1] = new King(0, 4, 1);
        pieces[0][4] = Kings[1];
        pieces[7][4] = Kings[0];
        // nullify undoBuffer and redoBuffer
        undoBuffer = new Pair<>(new Position(-1, -1), new Position(-1, -1));
        redoBuffer = new Pair<>(new Position(-1, -1), new Position(-1, -1));
        // no piece selected at the beginning
        cancelSelected();
    }

    /**
     *
     * @param piece
     * @return true if color of piece equals current turn
     */
    private boolean validTurn(Piece piece) {
        return piece != null && piece.getColor() == turn;
    }

    public int getTurn() {
        return turn;
    }

    /**
     *
     * @param row
     * @param col
     * @return true if move is successful
     * Move selected piece to target position, must select piece before move. Assume selected piece is valid (not null).
     */
    public boolean movePiece(int row, int col) {
        Piece piece = getPieceAt(selectedRow, selectedCol);
        // error check
        if (!validTurn(piece) || !validMove(row, col, piece))
            return false;
        // castling
        if (isCastling(row, col)) {
            // move right rook
            if (col == selectedCol + 2) {
                movePiece(row, col - 1, getPieceAt(row, 7), true);
            }
            // move left rook
            else if (col == selectedCol - 2) {
                movePiece(row, col + 1, getPieceAt(row, 0), true);
            }
            captive = null;
        }
        // En passant
        else if (isEnPassant(row, col)) {
            captive = pieces[selectedRow][col];
            pieces[selectedRow][col] = null;
        }
        else
            captive = pieces[row][col];
        // move target piece
        movePiece(row, col, piece, true);
        // flip turn
        turn = 1 - turn;
        round ++;

        undoBuffer = new Pair<>(new Position(selectedRow, selectedCol), new Position(row, col));
        redoBuffer = new Pair<>(new Position(-1, -1), new Position(-1, -1));
        return true;
    }

    /**
     *
     * @return true if undo is successful
     */
    public boolean undoMove() {
        int srcRow = undoBuffer.getKey().getRow();
        int srcCol = undoBuffer.getKey().getCol();
        int destRow = undoBuffer.getValue().getRow();
        int destCol = undoBuffer.getValue().getCol();
        if (undoBuffer.getKey().equals(new Position(-1, -1)) || undoBuffer.getValue().equals(new Position(-1, -1)))
            return false;
        // undo castling
        if (isUndoCastling(srcRow, srcCol, destRow, destCol)) {
            if (srcCol == destCol + 2) {
                pieces[srcRow][0] = pieces[srcRow][srcCol - 1];
                pieces[srcRow][0].restoreTo(srcRow, 0);
                pieces[srcRow][srcCol - 1] = null;
            }
            else {
                pieces[srcRow][7] = pieces[srcRow][srcCol + 1];
                pieces[srcRow][7].restoreTo(srcRow, 7);
                pieces[srcRow][srcCol + 1] = null;
            }
        }
        // undo En Passant
        else if (isUndoEnPassant(srcRow, srcCol, destRow, destCol)) {
            pieces[captive.getRow()][captive.getCol()] = captive;
            captive = null;
        }
        Piece lastMovedPiece = pieces[destRow][destCol];
        round --;
        restorePiece(srcRow, srcCol, lastMovedPiece);
        pieces[destRow][destCol] = captive;
        captive = null;
        redoBuffer = new Pair<>(new Position(undoBuffer.getKey()), new Position(undoBuffer.getValue()));
        undoBuffer = new Pair<>(new Position(-1, -1), new Position(-1, -1));
        turn = 1 - turn;
        return true;
    }

    /**
     *
     * @param srcRow
     * @param srcCol
     * @param destRow
     * @param destCol
     * @return true is last move is castling
     */
    private boolean isUndoCastling(int srcRow, int srcCol, int destRow, int destCol) {
        Piece piece = pieces[destRow][destCol];
        if (piece == null || !(piece instanceof King))
            return false;
        return srcRow == destRow && Arrays.asList(-2, 2).contains(destCol - srcCol);
    }

    /**
     *
     * @param srcRow
     * @param srcCol
     * @param destRow
     * @param destCol
     * @return true if last move is En Passant
     */
    private boolean isUndoEnPassant(int srcRow, int srcCol, int destRow, int destCol) {
        Piece piece = pieces[destRow][destCol];
        if (piece == null || !(piece instanceof Pawn) || captive == null || !(captive instanceof Pawn))
            return false;
        return srcRow == captive.getRow() && destCol == captive.getCol();
    }

    /**
     *
     * @return true if undo is successful
     */
    public boolean redoMove() {
        if (redoBuffer.getKey().equals(new Position(-1, -1)) || redoBuffer.getValue().equals(new Position(-1, -1)))
            return false;
        selectPiece(redoBuffer.getKey().getRow(), redoBuffer.getKey().getCol());
        if (!movePiece(redoBuffer.getValue().getRow(), redoBuffer.getValue().getCol())) {
            cancelSelected();
            return false;
        }
        cancelSelected();
        return true;
    }

    /**
     *
     * @param row
     * @param col
     * @param piece
     * @param updateRound
     * Move piece no matter selected or not, without error checking
     */
    private void movePiece(int row, int col, Piece piece, boolean updateRound) {
        pieces[piece.getRow()][piece.getCol()] = null;
        if (updateRound)
            piece.moveTo(row, col, round);
        else
            piece.tryMoveTo(row, col);
        pieces[row][col] = piece;
    }

    private void restorePiece(int row, int col, Piece piece) {
        pieces[piece.getRow()][piece.getCol()] = null;
        piece.restoreTo(row, col);
        pieces[row][col] = piece;
    }

    /**
     *
     * @param row
     * @param col
     * @return true if moving selected king to the target position will result in castling
     */
    public boolean isCastling(int row, int col) {
        Piece piece = getPieceAt(selectedRow, selectedCol);
        if (piece instanceof King && (col == piece.getCol() + 2 || col == piece.getCol() - 2))
            return true;
        return false;
    }

    /**
     *
     * @param row
     * @param col
     * @return true if moving selected pawn to the target positions will result in En passant
     */
    public boolean isEnPassant(int row, int col) {
        Piece piece = getPieceAt(selectedRow, selectedCol);
        if (piece instanceof Pawn && col != piece.getCol() && emptyPosition(row, col))
            return true;
        return false;
    }

    /**
     *
     * @param row
     * @param col
     * @return true if piece selected is valid (within boundary and not null)
     */
    public boolean selectPiece(int row, int col) {
        if (!withinBoundary(row, col) || emptyPosition(row, col)) {
            cancelSelected();
            return false;
        }
        selectedRow = row;
        selectedCol = col;
        return true;
    }

    /**
     * cancel selected piece, set selectedRow and selectedCol to -1
     */
    public void cancelSelected() {
        selectedRow = -1;
        selectedCol = -1;
    }

    /**
     *
     * @return true if a piece is selected
     */
    public boolean pieceSelected() {
        return selectedRow != -1 && selectedCol != -1;
    }

    /**
     *
     * @return true if player of current turn is under check mate
     * Check mate means current player is being checked,
     * and no matter how the player moves a piece, its king is still being checked
     */
    public boolean isCheckMate() {
        if (!isChecked(turn))
            return false;
        return allMoveChecked(turn);
    }

    /**
     *
     * @return true if stalemate
     * King of current player is not being checked, but if the current player moves a piece, its king will be checked
     */
    public boolean isStaleMate() {
        if (isChecked(turn))
            return false;
        return allMoveChecked(turn);
    }

    /**
     *
     * @param color
     * @return true if moving any piece of the target color will result in king being checked
     */
    private boolean allMoveChecked(int color) {
        for (int y = 0; y < boardRow; y++)
            for (int x = 0; x < boardCol; x++) {
                Piece piece = pieces[y][x];
                // select pieces of only one color
                if (piece != null && piece.getColor() == color)
                    for (Position pos : piece.getAvailablePositions(this)) {
                        Piece origPiece = pieces[pos.getRow()][pos.getCol()];
                        // try move piece, without updating round
                        movePiece(pos.getRow(), pos.getCol(), piece, false);
                        if (!isChecked(color)) {
                            // try restore piece
                            movePiece(y, x, piece, false);
                            pieces[pos.getRow()][pos.getCol()] = origPiece;
                            return false;
                        }
                        // try restore piece
                        movePiece(y, x, piece, false);
                        pieces[pos.getRow()][pos.getCol()] = origPiece;
                    }
            }
        return true;
    }

    /**
     *
     * @param color
     * @return true if king of color is being checked
     */
    public boolean isChecked(int color) {
        if (Kings[color] == null)
            return false;
        return isCheckedPosition(Kings[color].getRow(), Kings[color].getCol(), color);
    }

    /**
     *
     * @param row
     * @param col
     * @param color
     * @return true if king of color will be checked if placed at target positions
     */
    public boolean isCheckedPosition(int row, int col, int color) {
        Position kingPos = new Position(row, col);
        for (int y = 0; y < boardRow; y++) {
            for (int x = 0; x < boardCol; x++) {
                if (pieces[y][x] != null && pieces[y][x].getColor() != color && pieces[y][x].getAvailablePositions(this).contains(kingPos))
                    return true;
            }
        }
        return false;
    }

    /**
     *
     * @param row
     * @param col
     * @param piece
     * @return true if moving piece to target position is valid
     * Checks if target position is in the set of available positions of piece.
     * Make sure moving the piece to target position will not result in its king being checked.
     */
    private boolean validMove(int row, int col, Piece piece) {
        if (!(emptyPosition(row, col) || validCapture(row, col, piece)) ||
                !piece.getAvailablePositions(this).contains(new Position(row, col)))
            return false;
        // check if moving piece to target will result king being checked
        Piece origPiece = getPieceAt(row, col);
        int origRow = piece.getRow();
        int origCol = piece.getCol();
        // try move piece
        movePiece(row, col, piece, false);
        if (isChecked(turn)) {
            // restore moved piece
            movePiece(origRow, origCol, piece, false);
            pieces[row][col] = origPiece;
            return false;
        }
        // restore moved piece
        movePiece(origRow, origCol, piece, false);
        pieces[row][col] = origPiece;
        return true;
    }

    /**
     *
     * @param row
     * @param col
     * @return piece at target position
     */
    public Piece getPieceAt(int row, int col) {
        if (!withinBoundary(row, col))
            throw new IllegalArgumentException("Invalid position");
        return pieces[row][col];
    }

    /**
     *
     * @param row
     * @param col
     * @param piece
     * @return true if placing piece at target position will result in a capture
     */
    public boolean validCapture(int row, int col, Piece piece) {
        if (!withinBoundary(row, col))
            return false;
        if (emptyPosition(row, col))
            return false;
        return piece.getColor() != pieces[row][col].getColor();
    }

    /**
     *
     * @param row
     * @param col
     * @return true if target position is empty
     */
    public boolean emptyPosition(int row, int col) {
        if (!withinBoundary(row, col))
            return false;
        return pieces[row][col] == null;
    }

    /**
     *
     * @param row
     * @param col0
     * @param col1
     * @return true if all positions in row between (col0, col1) exclusive are all empty
     * Used for castling
     */
    public boolean allEmpty(int row, int col0, int col1) {
        if (!withinBoundary(row, col0) || !withinBoundary(row, col1))
            return false;
        for (int x = Math.min(col0, col1) + 1; x < Math.max(col0, col1); x++)
            if (!emptyPosition(row, x))
                return false;
        return true;
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    // returns
    private boolean withinBoundary(int row, int col) {
        return row >= 0 && row < boardRow && col >=0 && col < boardCol;
    }

    // all the following functions are for test only
    public void clearBoard() {
        pieces = new Piece[boardRow][boardCol];
        Kings = new King[2];
    }

    public void setPiece(int row, int col, Piece piece) {
        pieces[row][col] = piece;
        if (piece instanceof King)
            Kings[piece.getColor()] = (King) piece;
    }
}
