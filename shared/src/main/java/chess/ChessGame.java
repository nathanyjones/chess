package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            ChessPosition endPosition = move.getEndPosition();
//            if
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        Collection<ChessMove> validMoves = validMoves(startPosition);
        ChessPiece piece = board.getPiece(startPosition);
        if (validMoves.contains(move) && piece.pieceColor == teamTurn) {
            board.addPiece(startPosition, null);
            board.addPiece(endPosition, piece);
        } else if (piece.pieceColor != teamTurn) {
            throw new InvalidMoveException("Team " + piece.pieceColor + " cannot move on " + teamTurn + "'s turn.");
        } else {
            throw new InvalidMoveException("Not a valid move.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }

        // Checks if the king is in check by an opponent's pawn
        int movementDirection = teamColor == TeamColor.WHITE ? 1 : -1;
        for (int i = -1; i < 2; i+=2) {
            ChessPosition currPosition = new ChessPosition(kingPosition.getRow()+movementDirection,
                    kingPosition.getColumn()+i);
            ChessPiece pieceAtCurrPos = board.getPiece(currPosition);
            if (pieceAtCurrPos.getPieceType() == ChessPiece.PieceType.PAWN &&
                    pieceAtCurrPos.pieceColor != teamColor) {
                return true;
            }
        }

        // Checks if the king is in check by a knight
        int[][] knightCoordinates = {{1,2}, {2,1}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}, {2,-1}};
        for (int[] coordinates : knightCoordinates) {
            ChessPosition currPosition = new ChessPosition(kingPosition.getRow() + coordinates[0],
                    kingPosition.getColumn() + coordinates[1]);
            if (ChessPosition.inRange(currPosition)) {
                ChessPiece pieceAtCurrPos = board.getPiece(currPosition);
                if (pieceAtCurrPos != null && pieceAtCurrPos.getPieceType() == ChessPiece.PieceType.KNIGHT &&
                        pieceAtCurrPos.pieceColor != teamColor) {
                    return true;
                }
            }
        }

        // Checks if the king is in check by a queen, rook, or bishop.
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
        for (int[] direction : directions) {
            int currRow = kingPosition.getRow();
            int currCol = kingPosition.getColumn();
            int rowVelocity = direction[0];
            int colVelocity = direction[1];
            while (true) {
                currRow += rowVelocity;
                currCol += colVelocity;
                boolean diagonalDirection = Math.abs(currRow) + Math.abs(currCol) == 1;
                ChessPosition currPosition = new ChessPosition(currRow, currCol);
                if (ChessPosition.inRange(currPosition)) {
                    if (board.getPiece(currPosition) != null) {
                        ChessPiece pieceAtPosition = board.getPiece(currPosition);
                        if (pieceAtPosition.pieceColor != teamColor) {
                            ChessPiece.PieceType pieceType = pieceAtPosition.getPieceType();
                            if (pieceType == ChessPiece.PieceType.QUEEN) {
                                return true;
                            } else if (diagonalDirection && pieceType == ChessPiece.PieceType.BISHOP) {
                                return true;
                            } else if (!diagonalDirection && pieceType == ChessPiece.PieceType.ROOK) {
                                return true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        // Checks if king is in check by opponents king.
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                ChessPosition currPosition = new ChessPosition(kingPosition.getRow()+i,
                        kingPosition.getColumn()+j);
                if (ChessPosition.inRange(currPosition)) {
                    if (board.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.KING &&
                            board.getPiece(currPosition).pieceColor != teamColor) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    private ChessPosition getKingPosition(TeamColor kingColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.pieceColor == kingColor) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

//    private boolean isValidMove() {
//
//    }
}
