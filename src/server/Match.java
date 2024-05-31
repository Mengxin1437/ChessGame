package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Match extends Thread{
    Socket player1;
    Socket player2;
    private int value;
    public Match(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;

        //猜先，然后分别向双方发送信息
        value = (int)(Math.random()*100) % 2;
        try{
            //1执黑 0执白
            player1.getOutputStream().write(value);
            player2.getOutputStream().write(1-value);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("创建了一个新的对局");
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (value == 1) {
                    readAndSendToOther(player1, player2);
                    readAndSendToOther(player2, player1);
                } else {
                    readAndSendToOther(player2, player1);
                    readAndSendToOther(player1, player2);
                }
            }
        }catch (RuntimeException e){
            System.out.println("对局结束");
        }
    }

    /**
     * 从s1接收，发送给s2，这里235代表游戏中断的信号
     */
    private void readAndSendToOther(Socket s1, Socket s2){
        try {
            InputStream input = s1.getInputStream();
            int x = input.read();
            int y = input.read();
            System.out.println(" x:"+x+" y:"+y);
            if(x!=-1){
                OutputStream ot = s2.getOutputStream();
                ot.write(x);
                ot.write(y);
            }else{
                s2.getOutputStream().write(235);;
                throw new RuntimeException();
            }
        } catch (Exception e) {
            System.out.println("有一方断开连接");
            //向双方发送游戏结束的信息（强制执行）
            try{
                s1.getOutputStream().write(235);
            }catch (Exception ex){
            }
            try {
                s2.getOutputStream().write(235);;
            } catch (Exception ex) {
            }
            throw new RuntimeException(e);
        }
    }
}
