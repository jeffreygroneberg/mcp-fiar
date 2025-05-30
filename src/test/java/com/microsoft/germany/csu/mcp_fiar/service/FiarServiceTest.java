package com.microsoft.germany.csu.mcp_fiar.service;

import com.microsoft.germany.csu.mcp_fiar.service.api.FiarService;
import com.microsoft.germany.csu.mcp_fiar.service.api.Game;
import com.microsoft.germany.csu.mcp_fiar.service.api.GameStatus;
import com.microsoft.germany.csu.mcp_fiar.service.impl.FiarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FiarServiceTest {

    private FiarService fiarService;

    @BeforeEach
    void setUp() {
        fiarService = new FiarServiceImpl();
        fiarService.resetGameStore(); // Reset the game store before each test

    }

    @Test
    void testCreateNewGame_ValidInput() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);

        assertNotNull(game);
        assertEquals("Alice", game.getPlayerOne());
        assertEquals("Bob", game.getPlayerTwo());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertNotNull(game.getGameId());
        assertEquals(6, game.getBoard().length);
        assertEquals(7, game.getBoard()[0].length);
        assertTrue(game.getCurrentPlayer().equals("Alice") || game.getCurrentPlayer().equals("Bob"));
    }

    @Test
    void testCreateNewGame_InvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame("Alice", "Bob", 0, 7));
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame("Alice", "Bob", 6, -1));
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame("Alice", "Bob", -5, -5));
    }

    @Test
    void testCreateNewGame_InvalidPlayerNames() {
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame(null, "Bob", 6, 7));
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame("Alice", null, 6, 7));
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame("", "Bob", 6, 7));
        assertThrows(IllegalArgumentException.class, () -> fiarService.createNewGame("Alice", "", 6, 7));
    }

    @Test
    void testGetGame_ValidGameId() {
        Game createdGame = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = createdGame.getGameId();

        Game retrievedGame = fiarService.getGame(gameId);

        assertNotNull(retrievedGame);
        assertEquals(gameId, retrievedGame.getGameId());
        assertEquals("Alice", retrievedGame.getPlayerOne());
        assertEquals("Bob", retrievedGame.getPlayerTwo());
    }

    @Test
    void testGetGame_InvalidGameId() {
        assertThrows(IllegalArgumentException.class, () -> fiarService.getGame(null));
        assertThrows(IllegalArgumentException.class, () -> fiarService.getGame(UUID.randomUUID()));
    }

    @Test
    void testMakeMove_ValidMove() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();
        String currentPlayer = game.getCurrentPlayer();

        Game updatedGame = fiarService.makeMove(gameId, currentPlayer, 3);

        assertNotNull(updatedGame);
        assertEquals(GameStatus.IN_PROGRESS, updatedGame.getStatus());
        // Check that the piece was placed (should be in bottom row of column 3)
        int playerNumber = game.getPlayerOne().equals(currentPlayer) ? 1 : 2;
        assertEquals(playerNumber, updatedGame.getBoard()[5][3]); // Bottom row
        // Current player should have switched
        assertNotEquals(currentPlayer, updatedGame.getCurrentPlayer());
        updatedGame.toString();
    }

    @Test
    void testMakeMove_InvalidGameId() {
        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(null, "Alice", 3));
        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(UUID.randomUUID(), "Alice", 3));
    }

    @Test
    void testMakeMove_InvalidPlayer() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();

        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, null, 3));
        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, "", 3));
        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, "Charlie", 3));
    }

    @Test
    void testMakeMove_WrongPlayerTurn() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();
        String currentPlayer = game.getCurrentPlayer();
        String otherPlayer = currentPlayer.equals("Alice") ? "Bob" : "Alice";

        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, otherPlayer, 3));
    }

    @Test
    void testMakeMove_InvalidColumn() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();
        String currentPlayer = game.getCurrentPlayer();

        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, currentPlayer, -1));
        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, currentPlayer, 7));
    }

    @Test
    void testMakeMove_ColumnFull() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();

        // Fill column 0 completely
        for (int i = 0; i < 6; i++) {
            String currentPlayer = fiarService.getGame(gameId).getCurrentPlayer();
            fiarService.makeMove(gameId, currentPlayer, 0);
        }

        String currentPlayer = fiarService.getGame(gameId).getCurrentPlayer();
        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, currentPlayer, 0));
    }

    @Test
    void testMakeMove_HorizontalWin() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();

        // Simulate a horizontal win for player 1
        // Place 4 pieces in a row for Alice
        String alice = "Alice";
        String bob = "Bob";

        // Make sure Alice goes first by checking current player
        String currentPlayer = game.getCurrentPlayer();
        if (!currentPlayer.equals(alice)) {
            // Switch the names if Bob is first
            alice = "Bob";
            bob = "Alice";
        }

        fiarService.makeMove(gameId, alice, 0); // Alice
        fiarService.makeMove(gameId, bob, 0); // Bob
        fiarService.makeMove(gameId, alice, 1); // Alice
        fiarService.makeMove(gameId, bob, 0); // Bob
        fiarService.makeMove(gameId, alice, 2); // Alice
        fiarService.makeMove(gameId, bob, 1); // Bob
        fiarService.makeMove(gameId, alice, 3); // Alice wins horizontally

        game.toString();

        // The game should end with Alice winning
        assertTrue(game.getStatus() == GameStatus.PLAYER_ONE_WON ||
                game.getStatus() == GameStatus.PLAYER_TWO_WON);

        // Check that the winning piece is in the correct position
        
    }

    @Test
    void testMakeMove_VerticalWin() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();

        String alice = "Alice";
        String bob = "Bob";
        String currentPlayer = game.getCurrentPlayer();
        if (!currentPlayer.equals(alice)) {
            alice = "Bob";
            bob = "Alice";
        }

        // Alice gets 4 in a column, Bob plays in different column
        fiarService.makeMove(gameId, alice, 0); // Alice
        fiarService.makeMove(gameId, bob, 1); // Bob
        fiarService.makeMove(gameId, alice, 0); // Alice
        fiarService.makeMove(gameId, bob, 1); // Bob
        fiarService.makeMove(gameId, alice, 0); // Alice
        fiarService.makeMove(gameId, bob, 1); // Bob
        Game finalGame = fiarService.makeMove(gameId, alice, 0); // Alice wins vertically

        assertTrue(finalGame.getStatus() == GameStatus.PLAYER_ONE_WON ||
                finalGame.getStatus() == GameStatus.PLAYER_TWO_WON);
    }

    @Test
    void testMakeMove_GameAlreadyOver() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        game.setStatus(GameStatus.PLAYER_ONE_WON);
        UUID gameId = game.getGameId();

        assertThrows(IllegalArgumentException.class, () -> fiarService.makeMove(gameId, "Alice", 3));
    }

    @Test
    void testPlayerSwitching() {
        Game game = fiarService.createNewGame("Alice", "Bob", 6, 7);
        UUID gameId = game.getGameId();
        String firstPlayer = game.getCurrentPlayer();
        String secondPlayer = firstPlayer.equals("Alice") ? "Bob" : "Alice";

        fiarService.makeMove(gameId, firstPlayer, 0);
        Game updatedGame = fiarService.getGame(gameId);
        assertEquals(secondPlayer, updatedGame.getCurrentPlayer());

        fiarService.makeMove(gameId, secondPlayer, 1);
        updatedGame = fiarService.getGame(gameId);
        assertEquals(firstPlayer, updatedGame.getCurrentPlayer());
    }
}
