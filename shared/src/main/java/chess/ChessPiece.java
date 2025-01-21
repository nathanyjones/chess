package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (pieceType == PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (pieceType == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }
        return new ArrayList<>();
    }

    /**
     * Determines if a given move is valid.
     *
     * @return Boolean for given move
     */
    private boolean isPositionValid(ChessBoard board, ChessPosition position) {
        if (position.getRow() > 8 || position.getRow() < 1 ||
                position.getColumn() > 8 || position.getColumn() < 1) {
            return false;
        } else if (board.getPiece(position) != null && pieceColor == board.getPiece(position).pieceColor) {
            return false;
        }
        return true;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int k = -1; k < 2; k++) {
                if (i == 0 && k == 0) { continue; }
                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + k);
                if (isPositionValid(board, currPosition)) {
                    moves.add(new ChessMove(myPosition, currPosition, null));
                }
            }
        }
        return moves;
    }

    // Pawn currently always promotes to Queen. Will change in the future.
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int moveDirection;
        int numRowsToCheck = 1;
        int startingRow = myPosition.getRow();
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            moveDirection = 1;
            if (startingRow == 2) {
                numRowsToCheck = 2;
            }
        } else {
            moveDirection = -1;
            if (myPosition.getRow() == 7) {
                numRowsToCheck = 2;
            }
        }
        for (int i = 0; i < numRowsToCheck; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 1 && (j == -1 || j == 1)) { continue; }
                ChessPosition currPosition = new ChessPosition(myPosition.getRow() + moveDirection*(i+1), myPosition.getColumn() + j);
                if (isPositionValid(board, currPosition)) {
                    if ((j != 0 && board.getPiece(currPosition) != null) || (j == 0 && board.getPiece(currPosition) == null)) {
                        if (currPosition.getRow() == 8 && pieceColor == ChessGame.TeamColor.WHITE ||
                                currPosition.getRow() == 1 && pieceColor == ChessGame.TeamColor.BLACK) {
                            moves.add(new ChessMove(myPosition, currPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, currPosition, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, currPosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, currPosition, PieceType.ROOK));
                            continue;
                        }
                        if (i == 1  && board.getPiece(new ChessPosition(myPosition.getRow() + moveDirection, myPosition.getColumn())) != null) {
                            continue;
                        }
                        moves.add(new ChessMove(myPosition, currPosition, null));
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                '}';
    }
}
