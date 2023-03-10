package com.jchen.chess;

import com.jchen.chess.pieces.King;
import com.jchen.chess.pieces.Pawn;
import com.jchen.chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Game extends JPanel implements MouseListener {

    private Piece[][] pieces;
    Piece selectedPiece;

    public static void main(String[] args) {
        Game game = new Game();
        JFrame frame = new JFrame();
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }

    public Game() {
        pieces = new Piece[8][8];
        selectedPiece = null;
        for (int i = 0; i < 8; i++) {
            new Pawn(this, new Point(i, 1), Color.WHITE);
        }
        new King(this, new Point(4, 0), Color.WHITE);

        for (int i = 0; i < 8; i++) {
            new Pawn(this, new Point(i, 6), Color.BLACK);
        }
        new King(this, new Point(4, 7), Color.BLACK);
        addMouseListener(this);
        setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(45 * 8, 45 * 8);
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public Game setPieces(Piece[][] pieces) {
        this.pieces = pieces;
        return this;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setColor(Color.WHITE);

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                graphics.fillRect(i * 45, j * 45, 45, 45);
                graphics.setColor(graphics.getColor().equals(Color.GREEN) ? Color.WHITE: Color.GREEN);
            }
            graphics.setColor(graphics.getColor().equals(Color.GREEN) ? Color.WHITE: Color.GREEN);
        }

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                Piece piece = pieces[i][j];
                if (piece != null) {
                    graphics.drawImage(piece.getSprite(), i * 45, getPreferredSize().height - (j+1) * 45, null);
                }
            }
        }
        if (selectedPiece != null) {
            Point position = selectedPiece.getPosition();
            graphics.setColor(Color.RED);
            graphics.drawOval(position.x * 45, getPreferredSize().height - (position.y + 1) * 45, 45, 45);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point position = e.getPoint();
        int x = Math.floorDiv(position.x, 45);
        int y = Math.floorDiv((getPreferredSize().height - position.y), 45);
        Point actual = new Point(x, y);
        if (selectedPiece != null && selectedPiece.move(actual)) {
            repaint();
            return;
        }
        selectedPiece = pieces[x][y];
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public int squareStatus(int x, int y, Color color) {
        if (x >= pieces.length || x < 0 || y >= pieces[x].length || y < 0) {
            return -2;
        } else if (pieces[x][y] != null) {
            if (pieces[x][y].getColor().equals(color)) {
                return -1;
            } else {
                return 1;
            }
        }
        return 2;
    }

    public boolean check(Color color) {
        for (Piece[] row: pieces) {
            for (Piece piece: row) {
                if (piece != null && !piece.getColor().equals(color)) {
                    for (Point point : piece.getMoves()) {
                        Piece attacked = pieces[point.x][point.y];
                        if (attacked != null && attacked.getColor().equals(color) && attacked instanceof King){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isAttacked(Point point, Color color) {
        for (Piece[] row: pieces) {
            for (Piece piece: row) {
                if (piece != null && !piece.getColor().equals(color)) {
                    if (piece.attacks(point)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}