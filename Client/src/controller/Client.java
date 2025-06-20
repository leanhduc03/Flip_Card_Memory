/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import model.User;
import view.*;

import javax.swing.*;

/**
 * @author Admin
 */
public class Client {
    public static User user;
    public static LoginFrm loginFrm;
    public static RegisterFrm registerFrm;
    public static HomePageFrm homePageFrm;
    public static RoomListFrm roomListFrm;
    public static FindRoomFrm findRoomFrm;
    public static WaitingRoomFrm waitingRoomFrm;
    public static GameClientFrm gameClientFrm;
    public static CreateRoomPasswordFrm createRoomPasswordFrm;
    public static JoinRoomPasswordFrm joinRoomPasswordFrm;
    public static CompetitorInfoFrm competitorInfoFrm;
    public static RankFrm rankFrm;
    public static GameNoticeFrm gameNoticeFrm;
    public static RoomNameFrm roomNameFrm;
    public static SocketHandle socketHandle;
    
    public Client() {
    }

    public static JFrame getVisibleJFrame() {
        if (roomListFrm != null && roomListFrm.isVisible())
            return roomListFrm;
        if (createRoomPasswordFrm != null && createRoomPasswordFrm.isVisible()) {
            return createRoomPasswordFrm;
        }
        if (joinRoomPasswordFrm != null && joinRoomPasswordFrm.isVisible()) {
            return joinRoomPasswordFrm;
        }
        if (rankFrm != null && rankFrm.isVisible()) {
            return rankFrm;
        }
        return homePageFrm;
    }

    public static void openView(View viewName) {
        if (viewName != null) {
            switch (viewName) {
                case LOGIN:
                    loginFrm = new LoginFrm();
                    loginFrm.setVisible(true);
                    break;
                case REGISTER:
                    registerFrm = new RegisterFrm();
                    registerFrm.setVisible(true);
                    break;
                case HOMEPAGE:
                    homePageFrm = new HomePageFrm();
                    homePageFrm.setVisible(true);
                    break;
                case ROOM_LIST:
                    roomListFrm = new RoomListFrm();
                    roomListFrm.setVisible(true);
                    break;
                case FIND_ROOM:
                    findRoomFrm = new FindRoomFrm();
                    findRoomFrm.setVisible(true);
                    break;
                case WAITING_ROOM:
                    waitingRoomFrm = new WaitingRoomFrm();
                    waitingRoomFrm.setVisible(true);
                    break;

                case CREATE_ROOM_PASSWORD:
                    createRoomPasswordFrm = new CreateRoomPasswordFrm();
                    createRoomPasswordFrm.setVisible(true);
                    break;
                case RANK:
                    rankFrm = new RankFrm();
                    rankFrm.setVisible(true);
                    break;

                case ROOM_NAME_FRM:
                    roomNameFrm = new RoomNameFrm();
                    roomNameFrm.setVisible(true);
            }
        }
    }

    public static void openView(View viewName, int arg1, String arg2) {
        if (viewName != null) {
            switch (viewName) {
                case JOIN_ROOM_PASSWORD:
                    joinRoomPasswordFrm = new JoinRoomPasswordFrm(arg1, arg2);
                    joinRoomPasswordFrm.setVisible(true);
                    break;
            }
        }
    }

    public static void openView(View viewName, User competitor, int room_ID, int isStart, String competitorIP) {
        if (viewName == View.GAME_CLIENT) {
            gameClientFrm = new GameClientFrm(competitor, room_ID, isStart, competitorIP);
            gameClientFrm.setVisible(true);
        }
    }

    public static void openView(View viewName, User user) {
        if (viewName == View.COMPETITOR_INFO) {
            competitorInfoFrm = new CompetitorInfoFrm(user);
            competitorInfoFrm.setVisible(true);
        }
    }

    public static void openView(View viewName, String arg1, String arg2) {
        if (viewName != null) {
            switch (viewName) {
                case GAME_NOTICE:
                    gameNoticeFrm = new GameNoticeFrm(arg1, arg2);
                    gameNoticeFrm.setVisible(true);
                    break;
                case LOGIN:
                    loginFrm = new LoginFrm(arg1, arg2);
                    loginFrm.setVisible(true);
            }
        }
    }

    public static void closeView(View viewName) {
        if (viewName != null) {
            switch (viewName) {
                case LOGIN:
                    loginFrm.dispose();
                    break;
                case REGISTER:
                    registerFrm.dispose();
                    break;
                case HOMEPAGE:
                    homePageFrm.dispose();
                    break;
                case ROOM_LIST:
                    roomListFrm.dispose();
                    break;
                case FIND_ROOM:
                    findRoomFrm.stopAllThread();
                    findRoomFrm.dispose();
                    break;
                case WAITING_ROOM:
                    waitingRoomFrm.dispose();
                    break;
                case GAME_CLIENT:
                    gameClientFrm.stopAllThread();
                    gameClientFrm.dispose();
                    break;
                case CREATE_ROOM_PASSWORD:
                    createRoomPasswordFrm.dispose();
                    break;
                case JOIN_ROOM_PASSWORD:
                    joinRoomPasswordFrm.dispose();
                    break;
                case COMPETITOR_INFO:
                    competitorInfoFrm.dispose();
                    break;
                case RANK:
                    rankFrm.dispose();
                    break;
                case GAME_NOTICE:
                    gameNoticeFrm.dispose();
                    break;
                case ROOM_NAME_FRM:
                    roomNameFrm.dispose();
                    break;
            }

        }
    }

    public static void closeAllViews() {
        if (loginFrm != null) loginFrm.dispose();
        if (registerFrm != null) registerFrm.dispose();
        if (homePageFrm != null) homePageFrm.dispose();
        if (roomListFrm != null) roomListFrm.dispose();
        if (findRoomFrm != null) {
            findRoomFrm.stopAllThread();
            findRoomFrm.dispose();
        }
        if (waitingRoomFrm != null) waitingRoomFrm.dispose();
        if (gameClientFrm != null) {
            gameClientFrm.stopAllThread();
            gameClientFrm.dispose();
        }
        if (createRoomPasswordFrm != null) createRoomPasswordFrm.dispose();
        if (joinRoomPasswordFrm != null) joinRoomPasswordFrm.dispose();
        if (competitorInfoFrm != null) competitorInfoFrm.dispose();
        if (rankFrm != null) rankFrm.dispose();
        if (gameNoticeFrm != null) gameNoticeFrm.dispose();
        if (roomNameFrm != null) roomNameFrm.dispose();
    }

    public static void main(String[] args) {
        new Client().initView();
    }

    public void initView() {

        loginFrm = new LoginFrm();
        loginFrm.setVisible(true);
        socketHandle = new SocketHandle();
        socketHandle.run();
    }

    public enum View {
        LOGIN,
        REGISTER,
        HOMEPAGE,
        ROOM_LIST,
        FIND_ROOM,
        WAITING_ROOM,
        GAME_CLIENT,
        CREATE_ROOM_PASSWORD,
        JOIN_ROOM_PASSWORD,
        COMPETITOR_INFO,
        RANK,
        GAME_NOTICE,
        ROOM_NAME_FRM
    }
}
