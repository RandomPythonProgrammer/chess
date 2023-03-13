package com.jchen.chess;

import com.jchen.chess.pieces.Bishop;
import com.jchen.chess.pieces.King;
import com.jchen.chess.pieces.Pawn;
import com.jchen.chess.pieces.Piece;

import java.awt.*;

public class Board {
    private Piece[][] pieces;
    private Game game;

    public Board(Game game) {
        this.game = game;

        pieces = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            new Pawn(this, new Point(i, 1), Color.WHITE);
        }
        new King(this, new Point(4, 0), Color.WHITE);

        for (int i = 0; i < 8; i++) {
            new Pawn(this, new Point(i, 6), Color.BLACK);
        }
        new King(this, new Point(4, 7), Color.BLACK);

        new Bishop(this, new Point(2, 0), Color.WHITE);
        new Bishop(this, new Point(5, 0), Color.WHITE);
        game.repaint();
    }

    public Board() {

    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public int squareStatus(int x, int y, Color color) {
        if (x >= pieces.length || x < 0 || y >= pieces[x].length || y < 0) {
            return -2;
        } else if (pieces[x][y] != null) {
            if (pieces[x][y].getColor().equals(color)) {
                return -1;
            } else {
                return 1;
            }
        }
        return 2;
    }

    public boolean check(Color color) {
        for (Piece[] row: pieces) {
            for (Piece piece: row) {
                if (piece instanceof King && piece.getColor().equals(color)) {
                    if (isAttacked(piece.getPosition(), piece.getColor())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isAttacked(Point point, Color color) {
        for (Piece[] row: pieces) {
            for (Piece piece: row) {
                if (piece != null && !piece.getColor().equals(color)) {
                    if (piece.attacks(point)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Board setPieces(Piece[][] pieces) {
        this.pieces = pieces;
        return this;
    }

    @Override
    public Board clone() {
        Board clone = new Board();
        clone.pieces = pieces.clone();
        return clone;
    }

}
