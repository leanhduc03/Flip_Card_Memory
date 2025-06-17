# Flip Card Memory Game Project

A client-server memory card game application where players flip cards to find matching pairs.

## Project Structure

This project is divided into two main components:

- **Client**: The front-end application that users interact with
- **Server**: The back-end that handles game logic and user data

## Features

### Client Features
- User authentication (login, registration)
- Game lobby with online player list
- Interactive card flipping interface
- Match finding mechanics
- Score tracking
- User profile management
- In-game chat system
- Rankings and statistics
- Avatar customization

### Server Features
- User authentication management
- Memory game session handling
- Real-time multiplayer functionality
- Database operations for user data and game statistics
- Player statistics tracking
- Game state synchronization
- Card matching validation
- Turn-based gameplay management
- Score calculation and ranking system

## Technologies Used

- Java
- Socket Programming
- MySQL/MSSQL Database
- Swing UI Framework

## Setup Instructions

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- MySQL/MSSQL Database
- NetBeans IDE (recommended)

### Database Setup

1. Run the SQL script to create required tables:

```sql
CREATE TABLE `user`(
    ID int AUTO_INCREMENT PRIMARY KEY,
    `username` varchar(255) UNIQUE,
    `password` varchar(255),
    nickname varchar(255),
    avatar varchar(255),
    numberOfGame int DEFAULT 0,
    numberOfWin int DEFAULT 0,
    IsOnline int DEFAULT 0,
    IsPlaying int DEFAULT 0
);
```

2. Configure your database connection in the DAO.java file:

```java
final String DATABASE_NAME = "your_database_name"; // TODO FILL YOUR DATABASE NAME
...
final String JDBC_USER = "root";  // TODO FILL YOUR DATABASE USER
final String JDBC_PASSWORD = "your_password"; // TODO FILL YOUR DATABASE PASSWORD
```

### Running the Server

1. Open the Server project in NetBeans
2. Build the project
3. Run the server application
4. Ensure the server is running before starting client applications

### Running the Client

1. Open the Client project in NetBeans
2. Build the project
3. Run the client application

## Game Details

The application focuses on the Flip Card Memory game:
- Players take turns flipping pairs of cards
- The goal is to find matching card pairs
- Players earn points for successful matches
- Multiplayer mode allows competition between users
- Ranking system based on player performance

## User Interface

The client application uses Java Swing for the user interface, providing:
- Login and registration screens
- Game lobby with online player list
- Flip Card Memory game interface with interactive cards
- Chat panels
- User profile and statistics
- Ranking display

## Assets

The application includes various assets:
- Avatar images for user profiles
- Card images for the memory game
- Card flip animations
- Icons and UI elements
- Sound effects for card flips and matches






