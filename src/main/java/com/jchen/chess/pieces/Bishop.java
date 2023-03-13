package com.jchen.chess.pieces;

import com.jchen.chess.AssetLoader;
import com.jchen.chess.Board;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Bishop extends Piece{
    public Bishop(Board board, Point position, Color color) {
        super(board, position, color);
    }

    @Override
    public ArrayList<Point> getPossibleMoves() {
        Board board = getBoard();
        Point pos = getPosition();
        ArrayList<Point> moves = new ArrayList<>();

        int x = pos.x;
        int y = pos.y;
        while (x < 8 && x >= 0 && y >= 0 && y < 8) {
            int status = board.squareStatus(x, y, getColor());
            if (status > 0) {
                moves.add(new Point(x, y));
                if (status != 2) {
                    break;
                }
            }
            x++; y++;
        }

        x = pos.x;
        y = pos.y;
        while (x < 8 && x >= 0 && y >= 0 && y < 8) {
            int status = board.squareStatus(x, y, getColor());
            if (status > 0) {
                moves.add(new Point(x, y));
                if (status != 2) {
                    break;
                }
            }
            x++; y--;
        }

        x = pos.x;
        y = pos.y;
        while (x < 8 && x >= 0 && y >= 0 && y < 8) {
            int status = board.squareStatus(x, y, getColor());
            if (status > 0) {
                moves.add(new Point(x, y));
                if (status != 2) {
                    break;
                }
            }
            x--; y++;
        }

        x = pos.x;
        y = pos.y;
        while (x < 8 && x >= 0 && y >= 0 && y < 8) {
            int status = board.squareStatus(x, y, getColor());
            if (status > 0) {
                moves.add(new Point(x, y));
                if (status != 2) {
                    break;
                }
            }
            x--; y--;
        }

        return moves;
    }

    @Override
    public int getValue() {
        return 3;
    }

    @Override
    public BufferedImage getSprite() {
        return AssetLoader.getImage(String.format("%sbishop.png", getColor().equals(Color.WHITE) ? "w" : "b"));
    }

    @Override
    public boolean attacks(Point point) {
        return getMoves().contains(point);
    }
}
