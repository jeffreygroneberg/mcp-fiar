package com.microsoft.germany.csu.mcp_fiar.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.microsoft.germany.csu.mcp_fiar.service.api.FiarService;
import com.microsoft.germany.csu.mcp_fiar.service.api.Game;
import com.microsoft.germany.csu.mcp_fiar.service.api.GameStatus;

@Service
public class FiarServiceImpl implements FiarService {

    static Map<UUID, Game> games = new HashMap<>();

    /**
     * Creates a new game with the specified players and board dimensions
     * 
     * @param playerOne The name of the first player
     * @param playerTwo The name of the second player
     * @param x         The width of the game board
     * @param y         The height of the game board
     * @return A new Game object initialized with the given parameters
     * @throws IllegalArgumentException if player names are null/empty or board
     *                                  dimensions are not positive
     */
    @Tool(description = "Creates a new game with the specified players and board dimensions. It is important to remember who is who")
    @Override
    public Game createNewGame(String playerOne, String playerTwo, int x, int y) {

        if (x <= 0 || y <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive integers.");
        }
        if (playerOne == null || playerTwo == null || playerOne.isEmpty() || playerTwo.isEmpty()) {
            throw new IllegalArgumentException("Player names cannot be null or empty.");
        }

        Game game = new Game(playerOne, playerTwo, x, y);
        games.put(game.getGameId(), game);
        return game;

    }

    /**
     * Retrieves the game details for the specified game ID
     * 
     * @param gameId The unique identifier of the game to retrieve
     * @return The Game object corresponding to the given ID
     * @throws IllegalArgumentException if gameId is null or no game exists with
     *                                  that ID
     */
    @Tool(description = "Retrieves the game details for the specified game ID.")
    @Override
    public Game getGame(UUID gameId) {

        if (gameId == null) {
            throw new IllegalArgumentException("Game ID cannot be null.");
        }

        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found for the provided ID.");
        }

        return game;

    }

    /**
     * Makes a move in the game for the specified player and column
     * 
     * @param gameId The unique identifier of the game
     * @param player The name of the player making the move
     * @param column The column where the player wants to place their token (0-based
     *               indexing)
     * @return The updated Game object after the move has been made
     * @throws IllegalArgumentException if the move is invalid for any reason (wrong
     *                                  player, full column, etc.)
     */
    @Tool(description = "Makes a move in the game with the given game id for the specified player and column. The player must be the current player.")
    @Override
    public Game makeMove(UUID gameId, String player, int column) {

        if (gameId == null || player == null || player.isEmpty()) {
            throw new IllegalArgumentException("Game ID and player name cannot be null or empty.");
        }

        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found for the provided ID.");
        }

        // check if the game is not over
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Game is already over.");
        }

        // check if the player is valid
        if (!game.getPlayerOne().equals(player) && !game.getPlayerTwo().equals(player)) {
            throw new IllegalArgumentException("Invalid player for the game.");
        }

        // check if it is the current player's turn so the player does not make a move
        // out of turn
        if (!game.getCurrentPlayer().equals(player)) {
            throw new IllegalArgumentException("It's not your turn.");
        }

        // check if the column is valid and the columns is not full
        if (column < 0 || column >= game.getBoard()[0].length) {
            throw new IllegalArgumentException("Invalid column index.");
        }
        if (game.getBoard()[0][column] != 0) {
            throw new IllegalArgumentException("Column is full.");
        }

        // Logic to make a move in the game

        int[][] board = game.getBoard();
        int rows = board.length;
        int playerNumber = game.getPlayerOne().equals(player) ? 1 : 2;

        // Find the lowest empty row in the selected column
        int rowToPlace = -1;
        for (int row = rows - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                rowToPlace = row;
                break;
            }
        }
        if (rowToPlace == -1) {
            throw new IllegalArgumentException("Column is full.");
        }

        // Place the player's token
        board[rowToPlace][column] = playerNumber;

        // Check for win condition
        GameStatus winStatus = checkWin(playerNumber, board);
        if (winStatus == GameStatus.PLAYER_ONE_WON || winStatus == GameStatus.PLAYER_TWO_WON) {
            game.setStatus(winStatus);
        } else if (isBoardFull(board)) {
            game.setStatus(GameStatus.DRAW);
        } else {
            // Switch to the other player as game is still in progress
            game.setCurrentPlayer(game.getPlayerOne().equals(player) ? game.getPlayerTwo() : game.getPlayerOne());
        }

        return game;

    }

    /**
     * Checks if a player has won based on the current state of the board
     * 
     * @param playerNumber The number representing the player (1 for player one, 2
     *                     for player two)
     * @param board        The 2D array representing the current game board
     * @return The GameStatus indicating if the player has won or the game is still
     *         in progress
     */
    @Tool(description = "Neutral method to check for a given board if a player has won. It does not change the game state.")
    @Override
    public GameStatus checkWin(int playerNumber, int[][] board) {

        // check horizontal, vertical, and diagonal lines for a win
        int rows = board.length;
        int cols = board[0].length;

        // Check horizontal
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols - 4; c++) {
                if (board[r][c] == playerNumber && board[r][c + 1] == playerNumber &&
                        board[r][c + 2] == playerNumber && board[r][c + 3] == playerNumber) {

                    return playerNumber == 1 ? GameStatus.PLAYER_ONE_WON : GameStatus.PLAYER_TWO_WON;

                }
            }
        }

        // check vertical
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r <= rows - 4; r++) {
                if (board[r][c] == playerNumber && board[r + 1][c] == playerNumber &&
                        board[r + 2][c] == playerNumber && board[r + 3][c] == playerNumber) {
                    return playerNumber == 1 ? GameStatus.PLAYER_ONE_WON : GameStatus.PLAYER_TWO_WON;
                }
            }
        }

        // check diagonal (bottom-left to top-right)
        for (int r = 3; r < rows; r++) {
            for (int c = 0; c <= cols - 4; c++) {
                if (board[r][c] == playerNumber && board[r - 1][c + 1] == playerNumber &&
                        board[r - 2][c + 2] == playerNumber && board[r - 3][c + 3] == playerNumber) {
                    return playerNumber == 1 ? GameStatus.PLAYER_ONE_WON : GameStatus.PLAYER_TWO_WON;
                }
            }
        }
        // check diagonal (top-left to bottom-right)
        for (int r = 0; r <= rows - 4; r++) {
            for (int c = 0; c <= cols - 4; c++) {
                if (board[r][c] == playerNumber && board[r + 1][c + 1] == playerNumber &&
                        board[r + 2][c + 2] == playerNumber && board[r + 3][c + 3] == playerNumber) {
                    return playerNumber == 1 ? GameStatus.PLAYER_ONE_WON : GameStatus.PLAYER_TWO_WON;
                }
            }
        }

        return GameStatus.IN_PROGRESS; // No winning condition met
    }

    private boolean isBoardFull(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) {
                    return false; // Found an empty cell, so the board is not full
                }
            }
        }
        return true; // No empty cells found, the board is full

    }

    /**
     * Returns a list of all games currently stored in the game store
     * 
     * @return A list containing all Game objects in the store
     */
    @Tool(description = "Returns a list of all games currently stored in the game store.")
    @Override
    public List<Game> getGames() {
        return games.values().stream().toList();
    }

    /**
     * Resets the game store, clearing all stored games
     * 
     * @throws RuntimeException potentially if the operation cannot be completed
     */
    @Tool(description = "Resets the game store, clearing all stored games. Use with caution as this will delete all game data.")
    @Override
    public void resetGameStore() {
        games.clear();
    }

}
