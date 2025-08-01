/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.UserDAO;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * @author Admin
 */
public class ServerThread implements Runnable {

    private User user;
    private final Socket socketOfServer;
    private final int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
    Room room;
    private final UserDAO userDAO;
    private final String clientIP;

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        userDAO = new UserDAO();
        isClosed = false;
        room = null;

        if (this.socketOfServer.getInetAddress().getHostAddress().equals("127.0.0.1")) {
            clientIP = "127.0.0.1";
        } else {
            clientIP = this.socketOfServer.getInetAddress().getHostAddress();
        }

    }

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getClientIP() {
        return clientIP;
    }

    public String getStringFromUser(User user1) {
        return user1.getID() + "," + user1.getUsername()
                + "," + user1.getPassword() + "," + user1.getNickname() + ","
                + user1.getAvatar() + "," + user1.getNumberOfGame() + ","
                + user1.getNumberOfWin() + "," + user1.getRank();
    }

    public void goToOwnRoom() throws IOException {
        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",1," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",0," + getStringFromUser(user));
    }

    public void goToPartnerRoom() throws IOException {
        write("go-to-room," + room.getId() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",0," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getId() + "," + this.clientIP + ",1," + getStringFromUser(user));
    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khời động luông mới thành công, ID là: " + clientNumber);
            write("server-send-id" + "," + this.clientNumber);
            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                //Xác minh
                if (messageSplit[0].equals("client-verify")) {
                    System.out.println(message);
                    User user1 = userDAO.verifyUser(new User(messageSplit[1], messageSplit[2]));
                    if (user1 == null) {
                        write("wrong-user," + messageSplit[1] + "," + messageSplit[2]);
                    } else if (!user1.getIsOnline()) {
                        write("login-success," + getStringFromUser(user1));
                        this.user = user1;
                        userDAO.updateToOnline(this.user.getID());
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + user1.getNickname() + " đang online");
                        Server.admin.addMessage("[" + user1.getID() + "] " + user1.getNickname() + " đang online");
                    }
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = userDAO.checkDuplicated(messageSplit[1]);
                    if (checkdup) {
                        write("duplicate-username,");
                    } else {
                        User userRegister = new User(messageSplit[1], messageSplit[2], messageSplit[3], messageSplit[4]);
                        userDAO.addUser(userRegister);
                        this.user = userDAO.verifyUser(userRegister);
                        userDAO.updateToOnline(this.user.getID());
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đang online");
                        write("login-success," + getStringFromUser(this.user));
                    }
                }
                //Xử lý xem danh sách tất cả các người chơi toàn server
                if (messageSplit[0].equals("view-world-users-list")) {
                    List<User> worldUsers = userDAO.getWorldUsers(this.user.getID());
                    StringBuilder res = new StringBuilder("return-world-users-list,");
                    for (User user : worldUsers) {
                        res.append(user.getID()).append(",").append(user.getNickname()).append(",").append(user.getIsOnline() ? 1 : 0).append(",").append(user.getIsPlaying() ? 1 : 0).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    userDAO.updateToOffline(this.user.getID());
                    Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
                    Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                    this.user = null;
                }
                if (messageSplit[0].equals("go-to-room")) {
                    int roomName = Integer.parseInt(messageSplit[1]);
                    boolean isFinded = false;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.getRoom() != null && serverThread.getRoom().getId() == roomName) {
                            isFinded = true;
                            if (serverThread.getRoom().getNumberOfUser() == 2) {
                                write("room-fully,");
                            } else {
                                if (serverThread.getRoom().getPassword() == null || serverThread.getRoom().getPassword().equals(messageSplit[2])) {
                                    this.room = serverThread.getRoom();
                                    room.setUser2(this);
                                    room.increaseNumberOfGame();
                                    this.userDAO.updateToPlaying(this.user.getID());
                                    goToPartnerRoom();
                                } else {
                                    write("room-wrong-password,");
                                }
                            }
                            break;
                        }
                    }
                    if (!isFinded) {
                        write("room-not-found,");
                    }
                }
                //Xử lý lấy danh sách bảng xếp hạng
                if (messageSplit[0].equals("get-rank-charts")) {
                    List<User> ranks = userDAO.getUserStaticRank();
                    StringBuilder res = new StringBuilder("return-get-rank-charts,");
                    for (User user : ranks) {
                        res.append(getStringFromUser(user)).append(",");
                    }
                    System.out.println(res);
                    write(res.toString());
                }
                //Xử lý tạo phòng
                if (messageSplit[0].equals("create-room")) {
                    room = new Room(this);
                    if (messageSplit.length == 2) {
                        room.setPassword(messageSplit[1]);
                        write("your-created-room," + room.getId() + "," + messageSplit[1]);
                        System.out.println("Tạo phòng mới thành công, password là " + messageSplit[1]);
                    } else {
                        write("your-created-room," + room.getId());
                        System.out.println("Tạo phòng mới thành công");
                    }
                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý xem danh sách phòng trống
                if (messageSplit[0].equals("view-room-list")) {
                    StringBuilder res = new StringBuilder("room-list,");
                    int number = 1;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (number > 8) {
                            break;
                        }
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1) {
                            res.append(serverThread.room.getId()).append(",").append(serverThread.room.getPassword()).append(",");
                        }
                        number++;
                    }
                    write(res.toString());
                    System.out.println(res);
                }

                //Xử lý tìm phòng nhanh
                if (messageSplit[0].equals("quick-room")) {
                    boolean isFinded = false;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1 && serverThread.room.getPassword().equals(" ")) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
                            room.increaseNumberOfGame();
                            System.out.println("Đã vào phòng " + room.getId());
                            goToPartnerRoom();
                            userDAO.updateToPlaying(this.user.getID());
                            isFinded = true;
                            //Xử lý phần mời cả 2 người chơi vào phòng
                            break;
                        }
                    }

                    if (!isFinded) {
                        this.room = new Room(this);
                        userDAO.updateToPlaying(this.user.getID());
                        System.out.println("Không tìm thấy phòng, tạo phòng mới");
                    }
                }
                //Xử lý không tìm được phòng
                if (messageSplit[0].equals("cancel-room")) {
                    userDAO.updateToNotPlaying(this.user.getID());
                    System.out.println("Đã hủy phòng");
                    this.room = null;
                }
                //Xử lý khi có người chơi thứ 2 vào phòng
                if (messageSplit[0].equals("join-room")) {
                    int ID_room = Integer.parseInt(messageSplit[1]);
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.room != null && serverThread.room.getId() == ID_room) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
                            System.out.println("Đã vào phòng " + room.getId());
                            room.increaseNumberOfGame();
                            goToPartnerRoom();
                            userDAO.updateToPlaying(this.user.getID());
                            break;
                        }
                    }
                }

                //Xử lý khi gửi yêu cầu thách đấu tới bạn bè
                if (messageSplit[0].equals("duel-request")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]),
                            "duel-notice," + this.user.getID() + "," + this.user.getNickname());
                }
                //Xử lý khi đối thủ đồng ý thách đấu
                if (messageSplit[0].equals("agree-duel")) {
                    this.room = new Room(this);
                    int ID_User2 = Integer.parseInt(messageSplit[1]);
                    ServerThread user2 = Server.serverThreadBus.getServerThreadByUserID(ID_User2);
                    room.setUser2(user2);
                    user2.setRoom(room);
                    room.increaseNumberOfGame();
                    goToOwnRoom();
                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý khi không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]), message);
                }
                //Xử lý khi người chơi thắng
                if (messageSplit[0].equals("win")) {
                    userDAO.addWinGame(this.user.getID());  // Cập nhật dữ liệu thắng cho người chơi
                    room.getCompetitor(clientNumber).write("game-end1");  // Gửi thông báo cho đối thủ rời khỏi phòng

                    write("game-end"); // Gửi thông báo cho người chơi thắng rời khỏi phòng
                    User updatedUser = userDAO.getUserByID(this.user.getID());
                    User updatedUser2 = userDAO.getUserByID(room.getCompetitorID(clientNumber));
                    room.getCompetitor(clientNumber).write("update-home," + getStringFromUser(updatedUser2));
                    write("update-home," + getStringFromUser(updatedUser));
                    room.setUsersToNotPlaying(); // Cập nhật trạng thái không chơi cho cả hai người
                    room.getCompetitor(clientNumber).room = null;
                    this.room = null; // Xóa phòng khỏi thread

                    // Gửi thông báo cập nhật trang chủ với dữ liệu người dùng mới
                    // Gửi dữ liệu đã cập nhật để làm mới trang chủ
                }

                if (messageSplit[0].equals("left-room")) {
                    if (room != null) {
                        room.setUsersToNotPlaying();
                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                        this.room = null;
                    }
                }
            }
        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
            if (this.user != null) {
                userDAO.updateToOffline(this.user.getID());
                userDAO.updateToNotPlaying(this.user.getID());
                Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
            }

            //remove thread khỏi bus
            Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            if (room != null) {
                try {
                    if (room.getCompetitor(clientNumber) != null) {
//                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        room.getCompetitor(clientNumber).room = null;
                    }
                    this.room = null;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }
}
