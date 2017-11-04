package main;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 9/15/16.
 */
public class Empress extends Piece {
    public Empress(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return union of available positions of rook and knight
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        Set<Position> positions = new HashSet<>();
        positions.addAll(exploreHorizonalVertical(game));
        positions.addAll(exploreKnight(game));
        return positions;
    }
}
