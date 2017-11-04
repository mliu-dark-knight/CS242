package main;

import javafx.util.Pair;

import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class Bishop extends Piece {
    public Bishop(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return available positions on the diagonal
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        return exploreDiagonal(game);
    }
}
