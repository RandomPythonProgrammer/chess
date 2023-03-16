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
    private final static Board REFERENCE_BOARD = new Board();
    private Board previous;
    private Point wKing;
    private Point bKing;
    private boolean brc, blc;
    private boolean wrc, wlc;
    private boolean wc, bc;
    private Move move;
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
        blc = true;
        brc = true;
        wlc = true;
        wrc = true;
        bc = false;
        wc = false;
    }

    public Board(Board previous, Move move) {
        this.previous = previous;
        this.pieces = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = previous.pieces[i][j].clone();
            }
        }
        bKing = previous.bKing;
        wKing = previous.wKing;
        blc = previous.blc;
        brc = previous.brc;
        wlc = previous.wlc;
        wrc = previous.wrc;
        wc = previous.wc;
        bc = previous.bc;
        move(move.start, move.end);
        this.move = move;
    }

    public Move getMove() {
        return move;
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

            if ((color == 'b' && y == 3) || (color == 'w' && y == 4)) {
                Piece right = get(x + 1, y);
                if (right != null && right.isType('p') && !right.isColor(color)) {
                    Piece og = previous.get(x + 1, y);
                    Piece upOne = previous.get(x + 1, y+dir);
                    if (og == null || og.isColor('n'))
                        if (upOne == null || upOne.isColor('n'))
                            moves.add(new Point(x+1, y+dir));
                }

                Piece left = get(x - 1, y);
                if (left != null && left.isType('p') && !left.isColor(color)) {
                    Piece og = previous.get(x - 1, y);
                    Piece upOne = previous.get(x - 1, y+dir);
                    if (og == null || og.isColor('n'))
                        if (upOne == null || upOne.isColor('n'))
                            moves.add(new Point(x - 1, y+dir));
                }
            }
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
            boolean check = check(color);
            boolean canLeft = (color == 'b' ? blc : wlc);
            boolean canRight = (color == 'b' ? brc : wrc);

            if (!check && canLeft && pieces[0][y].isType('r') && pieces[1][y].isColor('n') && pieces[2][y].isColor('n') && pieces[3][y].isColor('n')) {
                if (!next(new Move(x, y,x - 1, y)).check(color))
                    if (!next(new Move(x-1, y, x-2, y)).check(color))
                            moves.add(new Point(-1, 0));
            }
            if (!check && canRight && pieces[7][y].isType('r') && pieces[5][y].isColor('n') && pieces[6][y].isColor('n')) {
                if (!next(new Move(x, y,x+1, y)).check(color))
                    if (!next(new Move(x+1, y, x+2, y)).check(color))
                            moves.add(new Point(0, -1));
            }
        }

        return moves;
    }

    public Board move(Point origin, Point dest) {
        Piece piece = pieces[origin.x][origin.y];
        if (dest.x == -1 || dest.y == -1) {
            int y = origin.y;
            if (dest.x < 0) {
                move(origin, new Point(2, y));
                move(new Point(0, y), new Point(3, y));
            } else {
                move(origin, new Point(6, y));
                move(new Point(7, y), new Point(5, y));
            }
            if (piece.isColor('b')) {
                bc = true;
            } else {
                wc = true;
            }
        } else {
            if (piece.isType('k')) {
                if (piece.isColor('w')) {
                    wKing = dest;
                    wrc = false;
                    wlc = false;
                } else {
                    bKing = dest;
                    brc = false;
                    blc = false;
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
            if (piece.isType('r')) {
                if (origin.x == 0) {
                    if (piece.isColor('w')) {
                        wlc = false;
                    } else {
                        blc = false;
                    }
                } else {
                    if (piece.isColor('w')) {
                        wrc = false;
                    } else {
                        brc = false;
                    }
                }
            }
            if (piece.isType('p')) {
                if (dest.y == 0 || dest.y == 7){
                    pieces[origin.x][origin.y] = new Piece(piece.getColor(), 'q');
                }
                if (destP.isColor('n') && dest.x != origin.x) {
                    pieces[dest.x][origin.y] = new Piece('n', 'a');
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
                    if (!attacker.isType('k')) {
                        if (attacker.isColor(invert(color)) && getMoves(ii, jj).contains(new Point(i, j))) {
                            return true;
                        }
                    } else if (Math.abs(ii - i) <= 1 && Math.abs(jj - j) <= 1) {
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
                            Board next = next(new Move(point, move));
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

    public boolean statemate(char color) {
        if (!check(color)) {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    Point point = new Point(i, j);
                    Piece piece = get(point);
                    if (piece != null && piece.isColor(color)) {
                        for (Point move : getMoves(point)) {
                            Board next = next(new Move(point, move));
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

    public Board next(Move move) {
        return new Board(this, move);
    }

    public double evaluate(char color) {
        double activePieces = 0;
        Point king = getKing(color);
        if (king == null || checkmate(color)) {
            return 0;
        } else if (statemate(color)) {
            return 0.1;
        }
        double value = 0;
        double vision = 0;
        int side = color == 'w' ? 0: 7;
        boolean castled = color == 'b' ? bc : wc;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = pieces[i][j];
                if (piece.isColor(color)) {
                    value += piece.getValue();
                    vision += getMoves(i, j).size();
                    if (!piece.isType('p')) {
                        Piece ref = REFERENCE_BOARD.pieces[i][j];
                        if (ref.equals(get(move.start)) && !ref.equals(get(move.end)) ) {
                            if (getMoves(i, j).size() > REFERENCE_BOARD.getMoves(i, j).size())
                                activePieces += 2d/(1 + Math.abs(3.5 - piece.getValue()));
                        }
                    } else {
                        activePieces += Math.abs(j - side - 4)/(4 + Math.abs(i - 3.5));
                    }
                }
            }
        }
        return value + vision * 0.05 + activePieces + (castled ? 1 : 0);
    }

    public Move bestMove(char color) {
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
                            double current = evaluate(color)/evaluate(invert(color));
                            Board next = next(new Move(point, move));
                            if (!next.check(color)) {
                                double value = evaluateTree(next, current, color, invert(color), 1, 4);
                                moves.put(new Move(point, move), value);
                            }
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
        if (highest != null) {
            return highest.getKey();
        }
        return null;
    }

    public static double evaluateTree(Board board, double last, char oColor, char color, int depth, int maxDepth) {
        boolean isColor = color == oColor;
        double val = board.evaluate(oColor)/board.evaluate(invert(oColor));
        double value = isColor ? Double.MIN_VALUE : Double.MAX_VALUE;
        if (depth >= maxDepth || val/last < 0.85) {
            return val;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point point = new Point(i, j);
                Piece piece = board.pieces[i][j];
                if (piece.isColor(color)) {
                    for (Point move: board.getMoves(i, j)) {
                        double eval = evaluateTree(board.next(new Move(point, move)), val, oColor, invert(color), depth + 1, maxDepth);
                        value = (isColor ? Math.max(eval, value) : Math.min(eval, value));
                    }
                }
            }
        }
        return value;
    }
}