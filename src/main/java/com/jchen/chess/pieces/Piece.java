package com.jchen.chess.pieces;

import com.jchen.chess.Board;
import com.jchen.chess.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Piece {

    private Color color;
    private Point position;
    private Board board;

    public Piece(Board board, Point position, Color color) {
        this.position = position;
        this.color = color;
        this.board = board;
        board.getPieces()[position.x][position.y] = this;
    }

    public Board getBoard() {
        return board;
    }

    public Point getPosition() {
        return position;
    }

    public boolean move(Point position) {
        if (getMoves().contains(position)) {
            setPosition(position);
            return true;
        }
        return false;
    }

    public Piece setPosition(Point position) {
        board.getPieces()[this.position.x][this.position.y] = null;
        board.getPieces()[position.x][position.y] = this;
        this.position = position;
        return this;
    }

    public Color getColor() {
        return color;
    }
    protected abstract ArrayList<Point> getPossibleMoves();
    public ArrayList<Point> getMoves() {
        ArrayList<Point> nonCheck = new ArrayList<>();
        ArrayList<Point> moves = getPossibleMoves();
        Point pos = getPosition();
        for (Point move: moves) {
            Piece temp = board.getPieces()[move.x][move.y];
            position = pos;
            setPosition(move);
            if (!board.check(getColor())) {
                nonCheck.add(move);
            }
            setPosition(pos);
            board.getPieces()[move.x][move.y] = temp;
        }
        return nonCheck;
    }
    public abstract int getValue();
    public abstract BufferedImage getSprite();
    public abstract boolean attacks(Point point);
}
