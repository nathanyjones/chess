package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.KING) {
            return getKingMoves(board, myPosition);
        } else if (type == PieceType.PAWN) {
            return getPawnMoves(board, myPosition);
        } else if (type == PieceType.ROOK) {
            return getRookMoves(board, myPosition);
        } else if (type == PieceType.BISHOP) {
            return getBishopMoves(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            return getQueenMoves(board, myPosition);
        } else if (type == PieceType.KNIGHT) {
            return getKnightMoves(board, myPosition);
        }
        return new ArrayList<>();
    }

    public boolean inRange(ChessPosition position) {
        return (position.getRow() <= 8 && position.getRow() >= 1 &&
                position.getColumn() <= 8 && position.getColumn() >= 1);
    }

    public ArrayList<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                ChessPosition currPosition = new ChessPosition(myPosition.getRow()+i,
                        myPosition.getColumn()+j);
                if (inRange(currPosition)) {
                    if (board.getPiece(currPosition) == null || board.getPiece(currPosition).pieceColor != pieceColor) {
                        moves.add(new ChessMove(myPosition, currPosition, null));
                    }
                }
            }
        }
        return moves;
    }

    public ArrayList<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int direction = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int promotionRow = pieceColor == ChessGame.TeamColor.WHITE ? 8 : 1;
        int[][] relativeCoords;
        if (pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2 ||
                pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) {
            relativeCoords = new int[][] {{1,direction}, {-1,direction}, {0, direction}, {0, direction * 2}};
        } else {
            relativeCoords = new int[][] {{1,direction}, {-1,direction}, {0, direction}};
        }
        for (int[] coord : relativeCoords) {
            ChessPosition currPosition = new ChessPosition(myPosition.getRow() + coord[1],
                    myPosition.getColumn() + coord[0]) ;
            if (inRange(currPosition)) {
                if (coord[0] == 0) {
                    if (board.getPiece(currPosition) != null) {
                        break;
                    }
                } else if (board.getPiece(currPosition) == null || board.getPiece(currPosition).pieceColor == pieceColor) {
                    continue;
                }
                if (currPosition.getRow() == promotionRow) {
                    moves.add(new ChessMove(myPosition, currPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, currPosition, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, currPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, currPosition, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, currPosition, null));
                }
            }
        }
        return moves;
    }

    public ArrayList<ChessMove> getLineMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int rowVelocity, colVelocity, rowPosition, colPosition;
        for (int[] direction : directions) {
            colVelocity = direction[0];
            rowVelocity = direction[1];
            colPosition = myPosition.getColumn();
            rowPosition = myPosition.getRow();
            while (true) {
                colPosition += colVelocity;
                rowPosition += rowVelocity;
                ChessPosition currPosition = new ChessPosition(rowPosition, colPosition);
                if (inRange(currPosition)) {
                    if (board.getPiece(currPosition) == null) {
                        moves.add(new ChessMove(myPosition, currPosition, null));
                    } else if (board.getPiece(currPosition).pieceColor != pieceColor) {
                        moves.add(new ChessMove(myPosition, currPosition, null));
                        break;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    public ArrayList<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        return getLineMoves(board, myPosition, directions);
    }

    public ArrayList<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
        return getLineMoves(board, myPosition, directions);
    }

    public ArrayList<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
        return getLineMoves(board, myPosition, directions);
    }

    public ArrayList<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int[][] relativeCoordinates = {{1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}, {-1,2}};
        for (int[] coord : relativeCoordinates) {
            ChessPosition currPosition = new ChessPosition(myPosition.getRow() + coord[1],
                    myPosition.getColumn() + coord[0]);
            if (inRange(currPosition)) {
                if (board.getPiece(currPosition) == null || board.getPiece(currPosition).pieceColor != pieceColor) {
                    moves.add(new ChessMove(myPosition, currPosition, null));
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
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
