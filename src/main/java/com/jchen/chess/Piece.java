package com.jchen.chess;

import java.awt.image.BufferedImage;

public class Piece {
    private char color;
    private char type;

    public Piece(char color, char type) {
        this.color = color;
        this.type = type;
    }

    public Piece(char[] data) {
        this(data[0], data[1]);
    }

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public boolean isType(char type) {
        return this.type == type;
    }

    public boolean isColor(char color) {
        return this.color == color;
    }

    public char[] toChars() {
        return new char[] {color, type};
    }

    @Override
    public Piece clone() {
        return new Piece(toChars());
    }

    public BufferedImage getImage() {
        return AssetLoader.getImage(String.valueOf(color) + type + ".png");
    }

    public int getValue() {
        switch (type) {
            case 'p':
                return 1;
            case 'b':
            case 'n':
                return 3;
            case 'r':
                return 5;
            case 'q':
                return 9;
            case 'k':
                return 100;
            default:
                return 0;
        }
    }
}
