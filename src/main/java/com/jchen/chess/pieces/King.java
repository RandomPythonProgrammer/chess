package com.jchen.chess.pieces;

import com.jchen.chess.AssetLoader;
import com.jchen.chess.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class King extends Piece{
    public King(Game game, Point position, Color color) {
        super(game, position, color);
    }

    @Override
    public ArrayList<Point> getMoves() {
        Point pos = getPosition();
        ArrayList<Point> moves = new ArrayList<>();
        Game game = getGame();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <=1; j++) {
                if (i != 0 || j != 0) {
                    Point move = new Point(pos.x + i, pos.y + j);
                    if (game.squareStatus(pos.x + i, pos.y + j, getColor()) > 0 && !game.isAttacked(move, getColor())) {
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public int getValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    public BufferedImage getSprite() {
        return AssetLoader.getImage(String.format("%sking.png", getColor().equals(Color.WHITE) ? "w" : "b"));
    }

    @Override
    public boolean attacks(Point position) {
        return Math.abs(position.x - getPosition().x) <= 1 && Math.abs(position.y - getPosition().y) <= 1;
    }
}
