package com.jchen.chess.pieces;

import com.jchen.chess.AssetLoader;
import com.jchen.chess.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Pawn extends Piece {

     private boolean moved;
    public Pawn(Game game, Point position, Color color) {
        super(game, position, color);
        moved = false;
    }

    @Override
    public ArrayList<Point> getMoves() {
        ArrayList<Point> moves = new ArrayList<>();
        Point pos = getPosition();
        Game game = getGame();
        Color color = getColor();
        int direction = getColor().equals(Color.WHITE) ? 1 : -1;
        if (game.squareStatus(pos.x, pos.y + direction, color) == 2) {
            moves.add(new Point(pos.x, pos.y + direction));
            if (!moved && game.squareStatus(pos.x, pos.y + 2 * direction, color) == 2) {
                moves.add(new Point(pos.x, pos.y + 2 * direction));
            }
        }

        if (game.squareStatus(pos.x + 1, pos.y + direction, color) == 1) {
            moves.add(new Point(pos.x + 1, pos.y + direction));
        }

        if (game.squareStatus(pos.x - 1, pos.y + direction, color) == 1) {
            moves.add(new Point(pos.x - 1, pos.y + direction));
        }
        return moves;
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
    public boolean attacks(Point position) {
        int direction = getColor().equals(Color.WHITE) ? 1 : -1;
        Point pos = getPosition();
        return position.equals(new Point(pos.x + 1, pos.y + direction)) || position.equals(new Point(pos.x -1, pos.y + direction));
    }

    @Override
    public boolean move(Point position) {
        boolean result = super.move(position);
        if (result) moved = true;
        return result;
    }
}
