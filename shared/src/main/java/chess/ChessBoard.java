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

    ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[8 - position.getRow()][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[8 - position.getRow()][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece.PieceType type;
        ChessGame.TeamColor color;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                color = i <= 2 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                if (i >= 3 && i <= 6) {
                    addPiece(position, null);
                    continue;
                } else if (i == 2 || i == 7) {
                    type = ChessPiece.PieceType.PAWN;
                } else if (j == 1 || j == 8) {
                    type = ChessPiece.PieceType.ROOK;
                } else if (j == 2 || j == 7) {
                    type = ChessPiece.PieceType.KNIGHT;
                } else if (j == 3 || j == 6) {
                    type = ChessPiece.PieceType.BISHOP;
                } else if (j == 4) {
                    type = ChessPiece.PieceType.QUEEN;
                } else {
                    type = ChessPiece.PieceType.KING;
                }
                addPiece(position, new ChessPiece(color, type));
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
}
