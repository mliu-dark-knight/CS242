package main;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class Rook extends Piece {
    public Rook(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return available positions on the horizontal and vertical line
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        return exploreHorizonalVertical(game);
    }
}
