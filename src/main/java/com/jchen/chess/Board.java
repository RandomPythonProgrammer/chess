package com.jchen.chess;

import java.awt.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

public class Board {
    Piece[][] pieces;
    Board previous;

    public Board() {
        previous = null;
        pieces = new Piece[8][8];

        try (FileReader reader = new FileReader(getClass().getClassLoader().getResource("board.txt").getPath())) {
            for (int i = pieces.length - 1; i >= 0; i--) {
                for (int j = 0; j < pieces[i].length; j++) {
                    char[] buffer = new char[3];
                    reader.read(buffer);
                    pieces[j][i] = new Piece(buffer);
                    if (buffer[2] == '\r') {
                        reader.read();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Board(Board previous) {
        this.previous = previous;
        this.pieces = new Piece[8][8];
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                pieces[i][j] = previous.pieces[i][j];
            }
        }
    }

    public Board getPrevious() {
        return previous;
    }

    public Collection<Point> getMoves(Point position) {
        ArrayList<Point> moves = new ArrayList<>();
        Piece piece = get(position);
        char type = piece.getType();
        char color = piece.getColor();
        int x = position.x;
        int y = position.y;

        int dir = color == 'w' ? 1 : -1;

        //pawn
        if (type == 'p') {
            Piece front = get(new Point(x, y + dir));
            if (front.isColor('n')) {
                moves.add(new Point(x, y + dir));
                Piece frontFront = get(new Point(x, y + dir * 2));
                if ((y * dir + 7) % 7 == 1 && frontFront.isColor('n')) {
                    moves.add(new Point(x, y + 2 * dir));
                }
            }

            Piece frontRight = get(new Point(x + 1, y + dir));
            if (frontRight != null && frontRight.getColor() != 'n' && frontRight.getColor() != color) {
                moves.add(new Point(x + 1, y + dir));
            }

            Piece frontLeft = get(new Point(x - 1, y + dir));
            if (frontLeft != null && frontLeft.getColor() != 'n' && frontLeft.getColor() != color) {
                moves.add(new Point(x - 1, y + dir));
            }

            //Add en-passant later
        }

        //bishop
        if (type == 'b' || type == 'q') {
            int xp = x + 1;
            int yp = y + 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(new Point(xp, yp));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                xp++;
                yp++;
            }

            xp = x + 1;
            yp = y - 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(new Point(xp, yp));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                xp++;
                yp--;
            }

            xp = x - 1;
            yp = y + 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(new Point(xp, yp));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                xp--;
                yp++;
            }

            xp = x - 1;
            yp = y - 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(new Point(xp, yp));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                xp--;
                yp--;
            }
        }

        //knight
        if (type == 'n') {
            java.util.List<Point> crds = java.util.List.of(
                    new Point(2, 1),
                    new Point(2, -1),
                    new Point(1, 2),
                    new Point (-1, 2),
                    new Point(-2, 1),
                    new Point(-2, -1),
                    new Point(1, -2),
                    new Point(-1, -2)
            );

            for (Point crd: crds) {
                Point point = new Point(x + crd.x, y + crd.y);
                Piece square = get(point);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(point);
                }
            }
        }

        //rook
        if (type == 'r' || type == 'q') {
            int xp = x + 1;
            while (xp < 8 && xp >= 0) {
                Piece square = get(new Point(xp, y));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, y));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                xp++;
            }

            xp = x - 1;
            while (xp < 8 && xp >= 0) {
                Piece square = get(new Point(xp, y));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, y));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                xp--;
            }

            int yp = y + 1;
            while (yp < 8 && yp >= 0) {
                Piece square = get(new Point(x, yp));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(x, yp));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                yp++;
            }

            yp = y - 1;
            while (yp < 8 && yp >= 0) {
                Piece square = get(new Point(x, yp));
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(x, yp));
                    if (!square.isColor('n')) {
                        break;
                    }
                }
                yp--;
            }
        }
        //king
        if (type == 'k') {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        Piece square = get(new Point(x + i, y + j));
                        if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                            moves.add(new Point(x + i, y + j));
                        }
                    }
                }
            }
            if (get(new Point(0, y)).isType('r') && get(new Point(1, y)).isColor('n') && get(new Point(2, y)).isColor('n') && get(new Point(3, y)).isColor('n')) {
                moves.add(new Point(-1, 0));
            }
            if (get(new Point(7, y)).isType('r') && get(new Point(5, y)).isColor('n') && get(new Point(6, y)).isColor('n')) {
                moves.add(new Point(0, -1));
            }
        }

        return moves;
    }

    public Board move(Point origin, Point dest) {
        if (dest.x == -1 || dest.y == -1) {
            int y = origin.y;
            if (dest.x < 0) {
                move(origin, new Point(2, y));
                move(new Point(0, y), new Point(3, y));
            } else {
                move(origin, new Point(6, y));
                move(new Point(7, y), new Point(5, y));
            }
        } else {
            set(dest, get(origin));
            set(origin, new Piece('n', 'a'));
        }
        return this;
    }

    public boolean check(char color) {
        //check for checks
        Point point = getKing(color);
        int i = point.x;
        int j = point.y;
        for (int ii = 0; ii < pieces.length; ii++) {
            for (int jj = 0; jj < pieces[ii].length; jj++) {
                if (ii != i || jj != j) {
                    Piece attacker = get(new Point(ii, jj));
                    if (attacker.isColor(invert(color)) && getMoves(new Point(ii, jj)).contains(new Point(i, j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Point getKing(char color) {
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                Piece piece = get(new Point(i, j));
                if (piece != null && piece.isColor(color) && piece.isType('k')) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    public boolean checkmate(char color) {
        Point king = getKing(color);
        if (check(color)) {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    Point point = new Point(i, j);
                    Piece piece = get(point);
                    if (piece != null && piece.isColor(color)) {
                        for (Point move : getMoves(point)) {
                            Board next = next();
                            next.move(point, move);
                            if (!next.check(color)) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static char invert(char color) {
        return color == 'b' ? 'w' : 'b';
    }

    public Piece get(Point point) {
        if (point.y < 8 && point.y >= 0 && point.x < 8 && point.x >= 0) {
            return pieces[point.x][point.y];
        }
        return null;
    }

    public Board set(Point point, Piece piece) {
        pieces[point.x][point.y] = piece;
        return this;
    }

    public Board next() {
        return new Board(this);
    }

    public double evaluate(char color) {
        double value = 0;
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                Point point = new Point(i, j);
                Piece piece = get(point);
                value += piece.getValue();
                value += 0.1 * getMoves(point).size();
            }
        }
        return value;
    }
}
