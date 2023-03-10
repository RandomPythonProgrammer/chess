package com.jchen.chess.pieces;

import com.jchen.chess.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Piece {

    private Color color;
    private Point position;
    private Game game;

    public Piece(Game game, Point position, Color color) {
        this.position = position;
        this.color = color;
        this.game = game;
        game.getPieces()[position.x][position.y] = this;
    }

    public Game getGame() {
        return game;
    }

    public Point getPosition() {
        return position;
    }

    public boolean move(Point position) {
        if (getMoves().contains(position)) {
            Piece[][] temp = getGame().getPieces();
            setPosition(position);
            if (game.check(color)) {
                game.setPieces(temp);
            } else {
                return true;
            }
        }
        return false;
    }

    public Piece setPosition(Point position) {
        game.getPieces()[this.position.x][this.position.y] = null;
        game.getPieces()[position.x][position.y] = this;
        this.position = position;
        return this;
    }

    public Color getColor() {
        return color;
    }
    public abstract ArrayList<Point> getMoves();
    public abstract int getValue();
    public abstract BufferedImage getSprite();
    public abstract boolean attacks(Point position);
}
