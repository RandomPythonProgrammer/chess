package com.jchen.chess;

import java.awt.*;

public class Move {
    public Point start, end;
    public Move(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Move(int sx, int sy, int ex, int ey) {
        this(new Point(sx, sy), new Point(ex, ey));
    }

    @Override
    public boolean equals(Object obj) {
        Move other = (Move) obj;
        return other.start.equals(start) && other.end.equals(end);
    }
}
