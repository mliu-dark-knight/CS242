package main;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public abstract class Piece {
    protected Position position;
    protected int color;
    // for castling and En passant
    protected int lastMovedRound;
    protected int secondLastMovedRound;
    protected boolean firstMove;

    public Piece(int row, int col, int color) {
        this.position = new Position(row, col);
        this.color = color;
        this.lastMovedRound = -1;
        this.secondLastMovedRound = -1;
        firstMove = false;
    }

    public int getRow() {
        return position.getRow();
    }

    public int getCol() {
        return position.getCol();
    }

    public void setRow(int row) {
        position.setRow(row);
    }

    public void setCol(int col) {
        position.setCol(col);
    }

    public int getLastMovedRound() {
        return lastMovedRound;
    }

    public void setLastMovedRound(int round) {
        this.lastMovedRound = round;
    }

    public boolean hasMoved() {
        return lastMovedRound != -1;
    }

    public boolean firstMove() {
        return firstMove;
    }

    public int getColor() {
        return color;
    }

    /**
     *
     * @param game
     * @return all availabel positions on game board
     */
    public abstract Set<Position> getAvailablePositions(GameState game);

    /**
     *
     * @param row
     * @param col
     * @param round
     * Update lastMovedRound to round
     */
    public void moveTo(int row, int col, int round) {
        if (lastMovedRound == -1)
            firstMove = true;
        else
            firstMove = false;
        tryMoveTo(row, col);
        secondLastMovedRound = lastMovedRound;
        setLastMovedRound(round);;
    }

    /**
     *
     * @param row
     * @param col
     * Move without updaing lastMovedRound
     */
    public void tryMoveTo(int row, int col) {
        setRow(row);
        setCol(col);
    }

    public void restoreTo(int row, int col) {
        tryMoveTo(row, col);
        if (firstMove) {
            lastMovedRound = -1;
            firstMove = false;
        }
        else
            lastMovedRound = secondLastMovedRound;
    }

    /**
     *
     * @param game
     * @return set of available positions on the vertical and horizontal line
     */
    protected Set<Position> exploreHorizonalVertical(GameState game) {
        Set<Position> positions = new HashSet<>();
        positions.addAll(explore(game, -1, 0));
        positions.addAll(explore(game, 1, 0));
        positions.addAll(explore(game, 0, -1));
        positions.addAll(explore(game, 0, 1));
        return positions;
    }

    /**
     *
     * @param game
     * @return set of available positinos on the diagonal
     */
    protected Set<Position> exploreDiagonal(GameState game) {
        Set<Position> positions = new HashSet<>();
        positions.addAll(explore(game, -1, -1));
        positions.addAll(explore(game, -1, 1));
        positions.addAll(explore(game, 1, -1));
        positions.addAll(explore(game, 1, 1));
        return positions;
    }

    /**
     *
     * @param game
     * @return set of available positions for a knight
     */
    protected Set<Position> exploreKnight(GameState game) {
        Set<Position> positions = new HashSet<>();
        for (int i : Arrays.asList(-2, -1, 1, 2)) {
            for (int j : Arrays.asList(2 / i, -2 / i)) {
                int r = position.getRow() + i;
                int c = position.getCol() + j;
                if (game.emptyPosition(r, c) || game.validCapture(r, c, this))
                    positions.add(new Position(r, c));
            }
        }
        return positions;
    }

    /**
     *
     * @param game
     * @param offsetR
     * @param offsetC
     * @return set of available positions on straight line with slope offsetR / offsetC from current position
     */
    private Set<Position> explore(GameState game, int offsetR, int offsetC) {
        Set<Position> positions = new HashSet<>();
        int r = position.getRow();
        int c = position.getCol();
        while (true) {
            r += offsetR;
            c += offsetC;
            if (game.emptyPosition(r, c))
                positions.add(new Position(r, c));
            else if (game.validCapture(r, c, this)) {
                positions.add(new Position(r, c));
                break;
            }
            else
                break;
        }
        return positions;
    }
}
