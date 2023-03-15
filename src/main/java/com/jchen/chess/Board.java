package com.jchen.chess;

import java.awt.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class Board {
    private Piece[][] pieces;
    private Board previous;
    private Point wKing;
    private Point bKing;
    private static final java.util.List<Point> knight = java.util.List.of(
            new Point(2, 1),
            new Point(2, -1),
            new Point(1, 2),
            new Point (-1, 2),
            new Point(-2, 1),
            new Point(-2, -1),
            new Point(1, -2),
            new Point(-1, -2)
    );

    public Board() {
        previous = null;
        pieces = new Piece[8][8];

        try (FileReader reader = new FileReader(getClass().getClassLoader().getResource("board.txt").getPath())) {
            for (int i = pieces.length - 1; i >= 0; i--) {
                for (int j = 0; j < pieces[i].length; j++) {
                    char[] buffer = new char[3];
                    reader.read(buffer);
                    Piece piece = new Piece(buffer);
                    if (piece.isType('k')) {
                        Point pos = new Point(j, i);
                        if (piece.isColor('w')) {
                            wKing = pos;
                        } else {
                            bKing = pos;
                        }
                    }
                    pieces[j][i] = piece;
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = previous.pieces[i][j].clone();
            }
        }
        bKing = previous.bKing;
        wKing = previous.wKing;
    }

    public Board getPrevious() {
        return previous;
    }

    public Collection<Point> getMoves(Point point) {
        return getMoves(point.x, point.y);
    }

    public Collection<Point> getMoves(int x, int y) {
        ArrayList<Point> moves = new ArrayList<>();
        Piece piece = pieces[x][y];
        char type = piece.getType();
        char color = piece.getColor();
        int dir = color == 'w' ? 1 : -1;

        //pawn
        if (type == 'p') {
            Piece front = get(x, y + dir);
            if (front != null && front.isColor('n')) {
                moves.add(new Point(x, y + dir));
                Piece frontFront = get(new Point(x, y + dir * 2));
                if ((y * dir + 7) % 7 == 1 && frontFront.isColor('n')) {
                    moves.add(new Point(x, y + 2 * dir));
                }
            }

            Piece frontRight = get(x + 1, y + dir);
            if (frontRight != null && frontRight.getColor() != 'n' && frontRight.getColor() != color) {
                moves.add(new Point(x + 1, y + dir));
            }

            Piece frontLeft = get(x - 1, y + dir);
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
                Piece square = get(xp, yp);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                xp++;
                yp++;
            }

            xp = x + 1;
            yp = y - 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(xp, yp);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                xp++;
                yp--;
            }

            xp = x - 1;
            yp = y + 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(xp, yp);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                xp--;
                yp++;
            }

            xp = x - 1;
            yp = y - 1;
            while (xp < 8 && xp >= 0 && yp < 8 && yp >= 0) {
                Piece square = get(xp, yp);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, yp));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                xp--;
                yp--;
            }
        }

        //knight
        if (type == 'n') {
            for (Point crd: knight) {
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
                Piece square = get(xp, y);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, y));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                xp++;
            }

            xp = x - 1;
            while (xp < 8 && xp >= 0) {
                Piece square = get(xp, y);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(xp, y));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                xp--;
            }

            int yp = y + 1;
            while (yp < 8 && yp >= 0) {
                Piece square = get(x, yp);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(x, yp));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                yp++;
            }

            yp = y - 1;
            while (yp < 8 && yp >= 0) {
                Piece square = get(x, yp);
                if (square != null && (square.isColor('n') || square.isColor(invert(color)))) {
                    moves.add(new Point(x, yp));
                }
                if (square == null || !square.isColor('n')) {
                    break;
                }
                yp--;
            }
        }
        //king
        if (type == 'k') {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        Piece square = get(x + i, y + j);
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
            Piece piece = pieces[origin.x][origin.y];
            if (piece.isType('k')) {
                if (piece.isColor('w')) {
                    wKing = dest;
                } else {
                    bKing = dest;
                }
            }
            Piece destP = pieces[dest.x][dest.y];
            if (destP.isType('k')) {
                if (destP.isColor('w')) {
                    wKing = null;
                } else {
                    bKing = null;
                }
            }
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
                    Piece attacker = pieces[ii][jj];
                    if (attacker.isColor(invert(color)) && getMoves(ii, jj).contains(new Point(i, j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Point getKing(char color) {
        return color == 'b' ? bKing : wKing;
    }

    public boolean checkmate(char color) {
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

    public Piece get(int x, int y) {
        if (y < 8 && y >= 0 && x < 8 && x >= 0) {
            return pieces[x][y];
        }
        return null;
    }

    public Piece get(Point point) {
        return get(point.x, point.y);
    }

    public Board set(Point point, Piece piece) {
        pieces[point.x][point.y] = piece;
        return this;
    }

    public Board next() {
        return new Board(this);
    }

    public double evaluate(char color) {
        if (getKing(color) == null) {
            return -100;
        }
        double value = 0;
        double vision = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = pieces[i][j];
                if (piece.isColor(color)) {
                    value += piece.getValue();
                    vision += getMoves(i, j).size();
                }
            }
        }
        return value + vision * 0.01;
    }

    public void bestMove(char color) {
        int waiting = 0;
        AtomicInteger done = new AtomicInteger();
        HashMap<Move, Double> moves = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point point = new Point(i, j);
                Piece piece = get(point);
                if (piece.isColor(color)) {
                    for (Point move: getMoves(point)) {
                        waiting++;
                        ForkJoinPool.commonPool().execute(() -> {
                            double value = evaluateTree(next().move(point, move), color, invert(color), 1, 5);
                            moves.put(new Move(point, move), value);
                            done.incrementAndGet();
                        });
                    }
                }
            }
        }

        while (done.get() < waiting) {

        }

        Map.Entry<Move, Double> highest = null;
        for (Map.Entry<Move, Double> entry: moves.entrySet()) {
            if (highest == null || entry.getValue() > highest.getValue()) {
                highest = entry;
            }
        }
        move(highest.getKey().start, highest.getKey().end);
    }

    public static double evaluateTree(Board board, char oColor, char color, int depth, int maxDepth) {
        double mean = 0;
        double samples = 0;
        if (depth >= maxDepth) {
            return board.evaluate(oColor);
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point point = new Point(i, j);
                Piece piece = board.pieces[i][j];
                if (piece.isColor(color)) {
                    for (Point move: board.getMoves(i, j)) {
                        mean += evaluateTree(board.next().move(point, move), oColor, invert(color), depth + 1, maxDepth);
                        samples++;
                    }
                }
            }
        }
        return samples == 0 ? 0 : mean/samples;
    }
}
class Move {
    public Point start, end;
    public Move(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        Move other = (Move) obj;
        return other.start.equals(start) && other.end.equals(end);
    }
}