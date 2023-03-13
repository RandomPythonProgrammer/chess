package com.jchen.chess.pieces;

import com.jchen.chess.AssetLoader;
import com.jchen.chess.Board;
import com.jchen.chess.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Pawn extends Piece {

     private boolean moved;
    public Pawn(Board board, Point position, Color color) {
        super(board, position, color);
        moved = false;
    }

    @Override
    public int getValue() {
        return 1;
    }

    @Override
    public BufferedImage getSprite() {
        return AssetLoader.getImage(String.format("%spawn.png", getColor().equals(Color.WHITE) ? "w" : "b"));
    }

    @Override
    public boolean attacks(Point point) {
        Point pos = getPosition();
        int direction = getColor().equals(Color.WHITE) ? 1 : -1;
        if (new Point(pos.x + 1, pos.y + direction).equals(point)) {
            return true;
        }

        if (new Point(pos.x - 1, pos.y + direction).equals(point)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean move(Point position) {
        boolean result = super.move(position);
        if (result) moved = true;
        return result;
    }

    @Override
    protected ArrayList<Point> getPossibleMoves() {
        ArrayList<Point> moves = new ArrayList<>();
        Point pos = getPosition();
        Board board = getBoard();
        Color color = getColor();
        int direction = getColor().equals(Color.WHITE) ? 1 : -1;
        if (board.squareStatus(pos.x, pos.y + direction, color) == 2) {
            moves.add(new Point(pos.x, pos.y + direction));
            if (!moved && board.squareStatus(pos.x, pos.y + 2 * direction, color) == 2) {
                moves.add(new Point(pos.x, pos.y + 2 * direction));
            }
        }

        if (board.squareStatus(pos.x + 1, pos.y + direction, color) == 1) {
            moves.add(new Point(pos.x + 1, pos.y + direction));
        }

        if (board.squareStatus(pos.x - 1, pos.y + direction, color) == 1) {
            moves.add(new Point(pos.x - 1, pos.y + direction));
        }
        return moves;
    }
}
