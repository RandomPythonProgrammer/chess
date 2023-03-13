package com.jchen.chess;

import com.jchen.chess.pieces.King;
import com.jchen.chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Game extends JPanel implements MouseListener {

    private ArrayList<Board> boards;
    Piece selectedPiece;

    public static void main(String[] args) {
        Game game = new Game();
        JFrame frame = new JFrame();
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }

    public Game() {
        boards = new ArrayList<>();
        nextBoard(new Board(this));
        selectedPiece = null;
        addMouseListener(this);
        setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(45 * 8, 45 * 8);
    }

    public Board getBoard() {
        return boards.get(boards.size() - 1);
    }

    public Game nextBoard(Board board) {
        boards.add(board);
        return this;
    }

    public Board getLast() {
        return boards.get(boards.size() - 2);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Piece[][] pieces = getBoard().getPieces();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setColor(Color.WHITE);

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                graphics.fillRect(i * 45, j * 45, 45, 45);
                graphics.setColor(graphics.getColor().equals(Color.GREEN) ? Color.WHITE : Color.GREEN);
            }
            graphics.setColor(graphics.getColor().equals(Color.GREEN) ? Color.WHITE : Color.GREEN);
        }

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                Piece piece = pieces[i][j];
                if (piece != null) {
                    graphics.drawImage(piece.getSprite(), i * 45, getPreferredSize().height - (j + 1) * 45, null);
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
        Piece[][] pieces = getBoard().getPieces();
        Point position = e.getPoint();
        int x = Math.floorDiv(position.x, 45);
        int y = Math.floorDiv((getPreferredSize().height - position.y), 45);
        Point actual = new Point(x, y);
        if (selectedPiece != null) {
            if (!getBoard().check(selectedPiece.getColor()) || selectedPiece instanceof King) {
                if (selectedPiece.move(actual)) {
                    repaint();
                    return;
                }
            }
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
}