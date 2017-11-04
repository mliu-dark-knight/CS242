package main;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 9/15/16.
 */
public class Princess extends Piece {
    public Princess(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return union of available positions of bishop and knight
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        Set<Position> positions = new HashSet<>();
        positions.addAll(exploreDiagonal(game));
        positions.addAll(exploreKnight(game));
        return positions;
    }
}
