package main;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class Queen extends Piece {
    public Queen(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return available positions on the horizontal, vertical line and diagonal
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        Set<Position> positions = new HashSet<>();
        positions.addAll(exploreHorizonalVertical(game));
        positions.addAll(exploreDiagonal(game));
        return positions;
    }
}
