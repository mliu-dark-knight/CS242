package main;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class Knight extends Piece {
    public Knight(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return available positions for a knight
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        return exploreKnight(game);
    }
}
