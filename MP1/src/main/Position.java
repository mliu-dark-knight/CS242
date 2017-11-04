package main;

/**
 * Created by mengxiongliu on 9/16/16.
 */
public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(Position position) {
        this.row = position.getRow();
        this.col = position.getCol();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Position))
            return false;
        return ((Position) object).getRow() == row && ((Position) object).getCol() == col;
    }

    @Override
    public int hashCode() {
        return row * 64 + col;
    }
}
