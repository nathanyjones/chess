package chess;

import java.util.ArrayList;
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
        board.resetBoard();
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (piece == null) {
            return validMoves;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            if (isMoveValid(move)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     *
     * @param move the move to check the validity of on the board
     * @return boolean indicating if the move is valid
     */
    public boolean isMoveValid(ChessMove move) {
        TeamColor moveColor = board.getPiece(move.getStartPosition()).getTeamColor();
        ChessGame simulatedGame = new ChessGame();
        simulatedGame.setBoard(board);
        if (simulatedGame.simulateMove(move)) {
            return (!simulatedGame.isInCheck(moveColor));
        }
        return false;
    }

    /**
     *
     * @param move the move to simulate on the game's board
     * @return boolean indicating whether the piece was successfully moved
     */
    public boolean simulateMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece.inRange(endPosition)) {
            if (board.getPiece(endPosition) == null ||
                    board.getPiece(endPosition).getTeamColor() != piece.getTeamColor()) {
                board.addPiece(endPosition, piece);
                board.addPiece(startPosition, null);
                return true;
            }
        }
        return false;
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
        if (validMoves.contains(move) && piece != null && piece.pieceColor == teamTurn) {
            int promotionRow = piece.getTeamColor() == TeamColor.BLACK ? 1 : 8;
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && endPosition.getRow() == promotionRow) {
                board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.addPiece(endPosition, piece);
            }
            board.addPiece(startPosition, null);
        } else if (piece != null && piece.pieceColor != teamTurn) {
            throw new InvalidMoveException("Team " + piece.pieceColor + " cannot move on " + teamTurn + "'s turn.");
        } else {
            throw new InvalidMoveException("Not a valid move.");
        }
        setTeamTurn(getTeamTurn() == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition == null) { return false; }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece == null || currPiece.getTeamColor() == teamColor) { continue; }
                Collection<ChessMove> currPieceMoves = currPiece.pieceMoves(board, currPosition);
                for (ChessMove move : currPieceMoves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param teamColor the color of the team's king to locate
     * @return ChessPosition of the team's king if it's on the board; null otherwise
     */
    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.pieceColor == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param teamColor which team to check for any valid moves
     * @return boolean indicating whether the team has any valid moves
     */
    private boolean teamHasNoValidMoves(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == teamColor &&
                        !validMoves(currPosition).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (teamHasNoValidMoves(teamColor) && isInCheck(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (teamHasNoValidMoves(teamColor) && !isInCheck(teamColor));
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board.clone();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public boolean isGameOver() {
        for (TeamColor color : TeamColor.values()) {
            if (isInStalemate(color) || isInCheckmate(color)) {
                return true;
            }
        }
        return false;
    }

    public String getWinner() {
        if (isInCheckmate(TeamColor.WHITE)) {
            return "BLACK";
        } else if (isInCheckmate(TeamColor.BLACK)) {
            return "WHITE";
        } else if (isGameOver()) {
            return "DRAW";
        } else {
            return "";
        }
    }

}
