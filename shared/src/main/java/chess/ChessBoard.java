package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[8-position.getRow()][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[8-position.getRow()][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor color;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i < 2) {
                    color = ChessGame.TeamColor.BLACK;
                } else {
                    color = ChessGame.TeamColor.WHITE;
                }
                if (i == 0 || i == 7) {
                    if (j == 0 || j == 7) {
                        squares[i][j] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
                    } else if (j == 1 || j == 6) {
                        squares[i][j] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
                    } else if (j == 2 || j == 5) {
                        squares[i][j] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
                    } else if (j == 3) {
                        squares[i][j] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
                    } else {
                        squares[i][j] = new ChessPiece(color, ChessPiece.PieceType.KING);
                    }
                } else if (i == 1 || i == 6) {
                    squares[i][j] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
                } else {
                    squares[i][j] = null;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder("Chess Board:\n");
        for (ChessPiece[] row : squares) {
            boardString.append("[");
            for (ChessPiece piece : row) {
                if (piece != null) {
                    boardString.append(piece);
                } else {
                    boardString.append(" ");
                }
            }
            boardString.append("]\n");
        }
        return boardString.toString();
    }

}