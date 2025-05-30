package com.microsoft.germany.csu.mcp_fiar.service.api;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.tool.annotation.Tool;

public interface FiarService {

    @Tool(description = "Creates a new game with the specified players and board dimensions. It is important to remember who is who")
    public Game createNewGame(String playerOne, String playerTwo, int x, int y);

    @Tool(description = "Retrieves the game details for the specified game ID.")
    public Game getGame(UUID gameId);

    @Tool(description = "Makes a move in the game with the given game id for the specified player and column. The player must be the current player.")
    public Game makeMove(UUID gameId, String player, int column);

    @Tool(description = "Neutral method to check for a given board if a player has won. It does not change the game state.")
    public GameStatus checkWin(int playerNumber, int[][] board);

    @Tool(description = "Returns a list of all games currently stored in the game store.")
    public List<Game> getGames();

    @Tool(description = "Resets the game store, clearing all stored games. Use with caution as this will delete all game data.")
    public void resetGameStore();

}
