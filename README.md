# Four in a Row - MCP Game

A Connect Four-style game implemented as a Model Context Protocol (MCP) server using Spring Boot.

## Overview

This project implements a Four in a Row (also known as Connect Four) game as a Model Context Protocol (MCP) server. The game allows players to compete against GitHub Copilot in a classic game of vertical checkers where the goal is to connect four pieces in a row - horizontally, vertically, or diagonally.

## Prerequisites

- Java JDK 17 or later
- Maven 3.6 or later
- Visual Studio Code with the GitHub Copilot extension installed

## Setup and Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   cd mcp-fiar
   ```

2. Build the project using Maven:
   ```
   ./mvnw clean install
   ```
   This will create the JAR file at `target/mcp-fiar-0.0.1-SNAPSHOT.jar`

## Starting the MCP Server

The project includes a VS Code MCP configuration file (`mcp.json` in the `.vscode` folder) that makes it easy to start the server:

1. Open the project in Visual Studio Code
2. Make sure the GitHub Copilot Chat extension is installed and enabled
3. Open the command palette (Cmd+Shift+P or Ctrl+Shift+P)
4. Type and select: "Copilot: Open Chat"
5. The MCP server should start automatically when you interact with Copilot Chat

If the server doesn't start automatically, ensure the MCP configuration is correctly set up in the `.vscode/mcp.json` file:

```json
{
  "servers": {
    "fiar-game-mcp-weather": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "${workspaceFolder}/target/mcp-fiar-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## Playing the Game

Once the server is running, you can interact with GitHub Copilot to play the game:

1. In the GitHub Copilot Chat panel, type a prompt like:
   - "Let's play Four in a Row"
   - "Start a Four in a Row game with a 6×6 grid"
   - "Can we play Connect Four with a 5×5 board?"

2. Copilot will create a new game and act as your opponent

3. To make a move, simply respond with the column number (1-7 for standard boards) where you want to place your piece

4. Copilot will make its move and show the updated board after each turn

### Game Commands

Here are some useful prompts you can use during gameplay:

- `1` to `7` - Drop your piece in the specified column
- "Show me the board" - Display the current board state
- "Reset all games" - Clear all stored games
- "Get all games" - List all active and completed games

## Project Structure

The project is organized as follows:

- `src/main/java/com/microsoft/germany/csu/mcp_fiar/` - Core application code
  - `McpFiarApplication.java` - Spring Boot application entry point
  - `service/api/` - API interfaces and model classes
    - `FiarService.java` - Game service interface
    - `Game.java` - Game state model
    - `GameStatus.java` - Enum for game status (IN_PROGRESS, PLAYER_ONE_WON, etc.)
  - `service/impl/` - Service implementations
    - `FiarServiceImpl.java` - Implementation of the game logic

## Game Rules

1. Players take turns dropping their pieces into one of the columns
2. Pieces fall to the lowest available position in the selected column
3. The first player to connect four of their pieces in a row (horizontally, vertically, or diagonally) wins
4. If all positions are filled without a winner, the game is a draw

## Development Notes

This project uses:
- Spring Boot for the application framework
- Model Context Protocol (MCP) for communication with GitHub Copilot
- JUnit for testing

To extend or modify the game, the main classes to look at are:
- `FiarServiceImpl.java` - contains the core game logic
- `Game.java` - represents the game state
