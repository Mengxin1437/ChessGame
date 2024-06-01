package server;

import com.example.myapplication.Info.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<ArrayList<Socket>> clientss;

    public Server(){
        System.out.println("服务端启动");
        clientss = new ArrayList<>();
        for(int i=0; i<3; i++){
            clientss.add(new ArrayList<>());
        }

        try {
            ServerSocket serverSocket = new ServerSocket(9990);

            while(true){
                Socket socket = serverSocket.accept();
                //读取首信息
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object object = inputStream.readObject();
                Message msg = (Message) object;
                int type = msg.chessType;
//                System.out.println(msg);

                //加入到对应的集合里边
                clientss.get(type).add(socket);
                System.out.println("玩家加入了对局,类型为："+type);

                //看对应集合里边是否符合对局要求
                ArrayList<Socket> clients = clientss.get(type);
                if (clients.size() == 2) {
                    Match match = new Match(clients.get(0), clients.get(1));
                    match.start();
                    clients.clear();
                }else {
                    //从加入到集合里边开始，超时会自动被清掉
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        if (clients.size() == 1) {
                            //向客户端发送匹配失败的信息
                            try {
                                clients.get(0).getOutputStream().write(245);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            clients.clear();
                        }
                    }).start();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
