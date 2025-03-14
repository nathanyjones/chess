package model;

import chess.ChessGame;

public record GameData(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
