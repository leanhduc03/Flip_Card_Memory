/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDAO;

import java.io.IOException;

/**
 * @author Admin
 */
public class Room {
    private final int id;
    private final ServerThread user1;
    private ServerThread user2;
    private String password;
    private final UserDAO userDAO;

    public Room(ServerThread user1) {
        System.out.println("Tạo phòng thành công, ID là: " + Server.ROOM_ID);
        this.password = " ";
        this.id = Server.ROOM_ID++;
        userDAO = new UserDAO();
        this.user1 = user1;
        this.user2 = null;
    }

    public int getId() {
        return id;
    }

    public ServerThread getUser2() {
        return user2;
    }

    public void setUser2(ServerThread user2) {
        this.user2 = user2;
    }

    public ServerThread getUser1() {
        return user1;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumberOfUser() {
        return user2 == null ? 1 : 2;
    }

    public void boardCast(String message) {
        try {
            user1.write(message);
            user2.write(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getCompetitorID(int ID_ClientNumber) {
        if (user1.getClientNumber() == ID_ClientNumber)
            return user2.getUser().getID();
        return user1.getUser().getID();
    }

    public ServerThread getCompetitor(int ID_ClientNumber) {
        if (user1.getClientNumber() == ID_ClientNumber)
            return user2;
        return user1;
    }

    public void setUsersToPlaying() {
        userDAO.updateToPlaying(user1.getUser().getID());
        if (user2 != null) {
            userDAO.updateToPlaying(user2.getUser().getID());
        }
    }

    public void setUsersToNotPlaying() {
        userDAO.updateToNotPlaying(user1.getUser().getID());
        if (user2 != null) {
            userDAO.updateToNotPlaying(user2.getUser().getID());
        }
    }


    public void increaseNumberOfGame() {
        userDAO.addGame(user1.getUser().getID());
        userDAO.addGame(user2.getUser().getID());
    }


    public void decreaseNumberOfGame() {
        userDAO.decreaseGame(user1.getUser().getID());
        userDAO.decreaseGame(user2.getUser().getID());
    }
}
