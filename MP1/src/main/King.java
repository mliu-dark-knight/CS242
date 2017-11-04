package main;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class King extends Piece {
    public King(int row, int col, int color) {
        super(row, col, color);
    }

    /**
     *
     * @param game
     * @return available surrounding positions, and possible castling positions
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        int row = position.getRow();
        int col = position.getCol();
        Set<Position> positions = new HashSet<>();
        // explore positions around current position
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j ++) {
                int r = row + i;
                int c = col + j;
                if (game.emptyPosition(r, c) || game.validCapture(r, c, this))
                    positions.add(new Position(r, c));
            }
        }
        // castling
        if (game.getTurn() == color && !hasMoved() && !game.isChecked(color)) {
            // left castling
            if (game.getPieceAt(row, 0) instanceof Rook && !game.getPieceAt(row, 0).hasMoved()
                    && game.allEmpty(row, col, 0) && !game.isCheckedPosition(row, col - 2, color))
                positions.add(new Position(row, col - 2));
            // right castling
            if (game.getPieceAt(row, 7) instanceof Rook && !game.getPieceAt(row, 7).hasMoved()
                    && game.allEmpty(row, col, 7) && !game.isCheckedPosition(row, col + 2, color))
                positions.add(new Position(row, col + 2));
        }
        return positions;
    }
}
