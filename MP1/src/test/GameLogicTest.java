package test;

import main.*;
import org.testng.annotations.Test;

/**
 * Created by mengxiongliu on 9/8/16.
 */
public class GameLogicTest {
    @Test
    public void ValidPawnMove() throws Exception {
        GameState game = new GameState();
        game.selectPiece(6, 0);
        assert game.movePiece(4, 0);
        game.selectPiece(1, 1);
        assert game.movePiece(3, 1);
        game.selectPiece(4, 0);
        assert game.movePiece(3, 1);
        assert game.getPieceAt(3, 1).getColor() == 0;
    }

    @Test
    public void InvalidPawnMove() throws Exception {
        GameState game = new GameState();
        game.selectPiece(1, 0);
        assert !game.movePiece(2, 1);
        assert game.getPieceAt(1, 0) instanceof Pawn && game.getPieceAt(2, 1) == null;
    }

    @Test
    public void ValidQueenMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new Queen(0, 0, 0));
        game.setPiece(7, 7, new Queen(7, 7, 1));
        game.selectPiece(0, 0);
        assert game.movePiece(0, 3);
        game.selectPiece(7, 7);
        assert game.movePiece(4, 7);
        game.selectPiece(0, 3);
        assert game.movePiece(3, 0);
        game.selectPiece(4, 7);
        assert  game.movePiece(1, 4);
    }

    @Test
    public void InvalidQueenMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new Queen(0, 0, 0));
        game.selectPiece(0, 0);
        assert !game.movePiece(1, 2);
    }

    @Test
    public void ValidKnightMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new Knight(0, 0, 0));
        game.selectPiece(0, 0);
        assert game.movePiece(1, 2);
        game.setPiece(7, 7, new Knight(7, 7, 1));
        game.selectPiece(7, 7);
        assert game.movePiece(5, 6);
    }

    @Test
    public void InvalidKnightMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new Knight(0, 0, 0));
        game.selectPiece(0, 0);
        assert !game.movePiece(3, 3);
    }

    @Test
    public void ValidPrincessMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new Princess(0, 0, 0));
        game.selectPiece(0, 0);
        assert game.movePiece(1, 2);
        game.setPiece(7, 7, new Princess(7, 7, 1));
        game.selectPiece(7, 7);
        assert game.movePiece(4, 4);
    }

    @Test
    public void ValidEmpressMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new Empress(0, 0, 0));
        game.selectPiece(0, 0);
        assert game.movePiece(1, 2);
        game.setPiece(7, 7, new Empress(7, 7, 1));
        game.selectPiece(7, 7);
        assert game.movePiece(0, 7);
    }

    @Test
    public void ValidKingMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new King(0, 0, 0));
        game.setPiece(7, 7, new King(7, 7, 1));
        game.selectPiece(0, 0);
        assert game.movePiece(1, 1);
        game.selectPiece(7, 7);
        assert game.movePiece(6, 7);
        game.selectPiece(1, 1);
        assert game.movePiece(1, 2);
    }

    @Test
    public void InvalidKingMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new King(0, 0, 0));
        game.selectPiece(0, 0);
        assert !game.movePiece(2, 2);
    }

    @Test
    public void MoveOffBoard() throws Exception {
        GameState game = new GameState();
        game.selectPiece(0, 0);
        assert !game.movePiece(0, -1);
        assert !game.movePiece(-1, 0);
        game.selectPiece(7, 7);
        assert !game.movePiece(7, 8);
        assert !game.movePiece(8, 7);
    }

    @Test
    public void MoveOnOwnPiece() throws Exception {
        GameState game = new GameState();
        game.selectPiece(0, 0);
        assert !game.movePiece(1, 0);
    }

    @Test
    public void ValidCheckMate() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new King(0, 0, 0));
        game.setPiece(0, 7, new Rook(0, 7, 1));
        game.setPiece(1, 7, new Rook(1, 7, 1));
        assert game.isCheckMate();
    }

    @Test
    public void ValidStaleMate() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new King(0, 0, 0));
        game.setPiece(2, 1, new Rook(2, 1, 1));
        game.setPiece(1, 2, new Rook(1, 2, 1));
        assert game.isStaleMate();
    }

    @Test
    public void ValidUndoMove() throws Exception {
        GameState game = new GameState();
        game.clearBoard();
        game.setPiece(0, 0, new King(0, 0, 0));
        game.selectPiece(0, 0);
        game.movePiece(1, 1);
        assert game.undoMove();
        assert game.getPieceAt(0, 0) instanceof King && game.getPieceAt(1, 1) == null;
        assert game.redoMove();
        assert game.getPieceAt(1, 1) instanceof King && game.getPieceAt(0, 0) == null;
    }
}
