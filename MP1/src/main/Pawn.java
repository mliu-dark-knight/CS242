package main;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mengxiongliu on 8/28/16.
 */
public class Pawn extends Piece {
    public Pawn(int row, int col, int color) {
        super(row, col, color);
    }


    /**
     *
     * @param game
     * @return available positions pawn
     * One or two steps at first move, only one step afterwards. Diagonal capture and En passant
     */
    @Override
    public Set<Position> getAvailablePositions(GameState game) {
        int row = position.getRow();
        int col = position.getCol();
        Set<Position> positions = new HashSet<>();
        // first move
        if (lastMovedRound == -1) {
            for (int i : (color == 1 ? Arrays.asList(1, 2) : Arrays.asList(-1, -2))) {
                int r = row + i;
                int c = col;
                if (game.emptyPosition(r, c))
                    positions.add(new Position(r, c));
                else
                    break;
            }
        }
        // not first move
        else {
            int r = row + (color == 1 ? 1 : -1);
            int c = col;
            if (game.emptyPosition(r, c))
                positions.add(new Position(r, c));
        }
        // capture
        for (int j : Arrays.asList(-1, 1)) {
            int r = row + (color == 1 ? 1 : -1);
            int c = col + j;
            if (game.validCapture(r, c, this))
                positions.add(new Position(r, c));
            // En passant
            if (game.emptyPosition(r, c) && game.getPieceAt(row, c) instanceof Pawn
                    && (game.getPieceAt(row, c)).firstMove() && game.getPieceAt(row, c).getLastMovedRound() > lastMovedRound)
                positions.add(new Position(r, c));
        }
        return positions;
    }

}
