/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author Admin
 */
public class SocketHandle implements Runnable {
    private BufferedWriter outputWriter;
    private Socket socketOfClient;

    public List<User> getListUser(String[] message) {
        List<User> userlist = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 4) {
            userlist.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2].equals("1"),
                    message[i + 3].equals("1")));
        }
        return userlist;
    }

    public List<User> getListRank(String[] message) {
        List<User> userlist = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 8) {
            userlist.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2],
                    message[i + 3],
                    message[i + 4],
                    Integer.parseInt(message[i + 5]),
                    Integer.parseInt(message[i + 6]),
                    Integer.parseInt(message[i + 7])));
        }
        return userlist;
    }

    public User getUserFromString(int start, String[] message) {
        return new User(Integer.parseInt(message[start]),
                message[start + 1],
                message[start + 2],
                message[start + 3],
                message[start + 4],
                Integer.parseInt(message[start + 5]),
                Integer.parseInt(message[start + 6]),
                Integer.parseInt(message[start + 7]));
    }

    @Override
    public void run() {

        try {
            socketOfClient = new Socket("127.0.0.1", 7776);
//            socketOfClient = new Socket("127.0.0.1", 7776);
            System.out.println("Kết nối thành công!");
            outputWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;
            while (true) {
                message = inputReader.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                if (messageSplit[0].equals("server-send-id")) {
                    int serverId = Integer.parseInt(messageSplit[1]);
                }
                //Đăng nhập thành công
                if (messageSplit[0].equals("login-success")) {
                    System.out.println("Đăng nhập thành công");
                    Client.closeAllViews();
                    Client.user = getUserFromString(1, messageSplit);
                    Client.openView(Client.View.HOMEPAGE);
                }
                //Thông tin tài khoản sai
                if (messageSplit[0].equals("wrong-user")) {
                    System.out.println("Thông tin sai");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản hoặc mật khẩu không chính xác");
                }
                //Tài khoản đã đăng nhập ở nơi khác
                if (messageSplit[0].equals("dupplicate-login")) {
                    System.out.println("Đã đăng nhập");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản đã đăng nhập ở nơi khác");
                }
                //Xử lý register trùng tên
                if (messageSplit[0].equals("duplicate-username")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.REGISTER);
                    JOptionPane.showMessageDialog(Client.registerFrm, "Tên tài khoản đã được người khác sử dụng");
                }
                //Xử lý kết quả tìm phòng từ server
                if (messageSplit[0].equals("room-fully")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Phòng chơi đã đủ 2 người chơi");
                }
                // Xử lý không tìm thấy phòng trong chức năng vào phòng
                if (messageSplit[0].equals("room-not-found")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Không tìm thấy phòng");
                }
                // Xử lý phòng có mật khẩu sai
                if (messageSplit[0].equals("room-wrong-password")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Mật khẩu phòng sai");
                }
                //Xử lý xem rank
                if (messageSplit[0].equals("return-get-rank-charts")) {
                    if (Client.rankFrm != null) {
                        Client.rankFrm.setDataToTable(getListRank(messageSplit));
                    }
                }
                //Xử lý lấy danh sách phòng
                if (messageSplit[0].equals("room-list")) {
                    Vector<String> rooms = new Vector<>();
                    Vector<String> passwords = new Vector<>();
                    for (int i = 1; i < messageSplit.length; i = i + 2) {
                        rooms.add("Phòng " + messageSplit[i]);
                        passwords.add(messageSplit[i + 1]);
                    }
                    Client.roomListFrm.updateRoomList(rooms, passwords);
                }
                //Xử lý xem danh sách người chơi toàn server
                if (messageSplit[0].equals("return-world-users-list")) {
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.updateWorldUsersList(getListUser(messageSplit));
                    }
                }
                //Xử lý vào phòng
                if (messageSplit[0].equals("go-to-room")) {
                    System.out.println("Vào phòng");
                    int roomID = Integer.parseInt(messageSplit[1]);
                    String competitorIP = messageSplit[2];
                    int isStart = Integer.parseInt(messageSplit[3]);

                    User competitor = getUserFromString(4, messageSplit);
                    if (Client.findRoomFrm != null) {
                        Client.findRoomFrm.showFoundRoom();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            JOptionPane.showMessageDialog(Client.findRoomFrm, "Lỗi khi sleep thread");
                        }
                    } else if (Client.waitingRoomFrm != null) {
                        Client.waitingRoomFrm.showFoundCompetitor();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            JOptionPane.showMessageDialog(Client.waitingRoomFrm, "Lỗi khi sleep thread");
                        }
                    }
                    Client.closeAllViews();
                    System.out.println("Đã vào phòng: " + roomID);
                    //Xử lý vào phòng
                    Client.openView(Client.View.GAME_CLIENT
                            , competitor
                            , roomID
                            , isStart
                            , competitorIP);
//                    Client.gameClientFrm.newgame();
                }
                //Tạo phòng và server trả về tên phòng
                if (messageSplit[0].equals("your-created-room")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.WAITING_ROOM);
                    Client.waitingRoomFrm.setRoomName(messageSplit[1]);
                    if (messageSplit.length == 3)
                        Client.waitingRoomFrm.setRoomPassword("Mật khẩu phòng: " + messageSplit[2]);
                }
                //Xử lý khi nhận được yêu cầu thách đấu
                if (messageSplit[0].equals("duel-notice")) {
                    int res = JOptionPane.showConfirmDialog(Client.getVisibleJFrame(), "Bạn nhận được lời thách đấu của " + messageSplit[2] + " (ID=" + messageSplit[1] + ")", "Xác nhận thách đấu", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        Client.socketHandle.write("agree-duel," + messageSplit[1]);
                    } else {
                        Client.socketHandle.write("disagree-duel," + messageSplit[1]);
                    }
                }
                //Xử lý không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Đối thủ không đồng ý thách đấu");
                }
              

                if (messageSplit[0].equals("game-end")) {
                    Client.gameClientFrm.stopTimer();
                    Client.closeAllViews(); // Đóng tất cả các giao diện liên quan đến trò chơi
                    Client.openView(Client.View.GAME_NOTICE, "Bạn đã thắng", "Đang trở về trang chủ...");
                    Thread.sleep(3000);
                    Client.closeAllViews();
                }
                
                if (messageSplit[0].equals("game-end1")) {
                    Client.gameClientFrm.stopTimer();
                    JOptionPane.showMessageDialog(Client.gameClientFrm, "Bạn đã thua! Trận đấu kết thúc.");
                    Client.closeAllViews(); // Đóng tất cả các giao diện liên quan đến trò chơi
                    Client.openView(Client.View.GAME_NOTICE, "Bạn đã thua", "Đang trở về trang chủ...");
                    Thread.sleep(3000);
                    Client.closeAllViews();
                }

                if (messageSplit[0].equals("left-room")) {
                    Client.gameClientFrm.stopTimer();
                    Client.closeAllViews();
                    Client.openView(Client.View.GAME_NOTICE, "Đối thủ đã thoát khỏi phòng", "Đang trở về trang chủ");
                    Thread.sleep(3000);
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                }
                if (messageSplit[0].equals("update-home")) {
                    User updatedUser = getUserFromString(1, messageSplit);
                    Client.user = updatedUser; // Cập nhật thông tin người dùng
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.updateUserInfo(updatedUser); // Hiển thị thông tin mới trên trang chủ
                    }
                    Client.openView(Client.View.HOMEPAGE);
                }
                if (messageSplit[0].equals("exit game")) {
                    Client.gameClientFrm.stopTimer();
                    //Client.closeAllViews();
                    //Client.openView(Client.View.GAME_NOTICE, "Đối thủ đã thoát khỏi phòng", "Đang trở về trang chủ");
                    Thread.sleep(3000);
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String message) throws IOException {
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }

}
