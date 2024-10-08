package com.jchen.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;

public class Game extends JPanel implements MouseListener {

    private Board board;
    private Point selectedPoint;
    private char currentColor;

    public static void main(String[] args) {
        Game game = new Game();
        JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }

    public Game() {
        selectedPoint = null;
        addMouseListener(this);

        getInputMap().put(KeyStroke.getKeyStroke('z'), "undo");
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (board.getPrevious() != null) {
                    board = board.getPrevious();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke('r'), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board = new Board();
            }
        });

        setVisible(true);
        board = new Board();
        new Timer(0, e -> repaint()).start();
        board = board.next(board.bestMove('w'));
        currentColor = 'b';
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(45 * 8, 45 * 8);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics = (Graphics2D) g;

        graphics.setColor(Color.WHITE);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int x = i * 45;
                int y = j * 45;
                graphics.fillRect(i * 45, j * 45, 45, 45);
                Piece piece = board.get(new Point(i, 7-j));
                if (!piece.isColor('n'))
                    graphics.drawImage(piece.getImage(), x, y, null);
                graphics.setColor(graphics.getColor().equals(Color.GREEN) ? Color.WHITE : Color.GREEN);
            }
            graphics.setColor(graphics.getColor().equals(Color.GREEN) ? Color.WHITE : Color.GREEN);
        }

        graphics.setColor(Color.RED);
        Move move = board.getMove();
        if (move != null) {
            Point start = move.start;
            Point end = move.end;
            graphics.fillOval(start.x * 45 + (45-10)/2, (7 - start.y) * 45 + (45-10)/2, 10, 10);
            graphics.drawOval(end.x * 45, 45 * (7 - end.y), 45, 45);
        }

        if (selectedPoint != null) {
            double x = selectedPoint.x * 45;
            double y = selectedPoint.y * 45;
            graphics.drawOval((int) x, (int) ((45 * 7) - y), 45, 45);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clicked = e.getPoint();
        Point scaled = new Point(Math.floorDiv(clicked.x, 45), 7 - Math.floorDiv(clicked.y, 45));
        if (selectedPoint != null) {
            if (board.get(selectedPoint).isColor(currentColor)) {
                Collection<Point> moves = board.getMoves(selectedPoint);
                boolean canMove = moves.contains(scaled);
                boolean rookCheck = board.get(scaled).isType('r') && board.get(scaled).isColor(currentColor);
                boolean leftCheck = moves.contains(new Point(-1, 0));
                boolean rightCheck = moves.contains(new Point(0, -1));
                boolean cCheck = leftCheck || rightCheck;

                if (canMove || (rookCheck && cCheck)) {
                    if (cCheck) {
                        if (scaled.x > selectedPoint.x && rightCheck) {
                            board = board.next(new Move(selectedPoint, new Point(0, -1)));
                        } else if (leftCheck) {
                            board = board.next(new Move(selectedPoint, new Point(-1, 0)));
                        }
                        selectedPoint = scaled;
                    } else {
                        board = board.next(new Move(selectedPoint, scaled));
                        selectedPoint = scaled;
                    }
                    if (board.check(currentColor)) {
                        board = board.getPrevious();
                    } else {
                        selectedPoint = scaled;
                        paint(getGraphics());
                        Move move = board.bestMove('w');
                        if (move != null) {
                            board = board.next(move);
                        } else {
                            System.out.println("out of moves");
                        }
                        if (board.checkmate(currentColor)) {
                            System.out.println("checkmate");
                        } else if (board.statemate(currentColor)) {
                            System.out.println("stalemate");
                        }
                    }
                    System.out.printf("Black evaluation: %f\n", board.evaluate('b'));
                    System.out.printf("White evaluation: %f\n", board.evaluate('w'));
                    System.out.println();
                }
            }
        }
        Piece piece = board.get(scaled);
        if (piece != null && !piece.isColor('n')) {
            selectedPoint = scaled;
        }
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