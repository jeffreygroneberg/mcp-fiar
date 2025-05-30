package com.microsoft.germany.csu.mcp_fiar.service.api;

import java.util.UUID;

public class Game {

    private GameStatus status;
    private String playerTwo;
    private UUID gameId;
    private int[][] board;
    private String playerOne;

    private String currentPlayer = null;

    public Game(String playerOne, String playerTwo, int x, int y) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameId = UUID.randomUUID();
        this.board = new int[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                this.board[i][j] = 0; // Initialize the board with zeros
            }
        }

        this.status = GameStatus.IN_PROGRESS;

        // randomize the starting player
        if (Math.random() < 0.5) {
            this.currentPlayer = playerOne;
        } else {
            this.currentPlayer = playerTwo;
        }

    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    // board to string representation
    public static String boardToString(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Game{" +
                "status=" + status +
                ", playerTwo='" + playerTwo + '\'' +
                ", gameId=" + gameId +
                ", board=\n" + Game.boardToString(board) +
                ", playerOne='" + playerOne + '\'' +
                '}';
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

}
