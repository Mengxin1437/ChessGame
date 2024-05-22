package com.example.myapplication.logic;

/**
 * 实现五子棋的逻辑的抽象类
 */
public class FiveInLine extends Chess {
    //如果五子棋要求有禁手需要重写isMovePositionOk方法

    /**
     * 从落子点出发判断游戏是否结束
     * @param x 行 [0, row)
     * @param y 列 [0, column)
     * @return null未结束 true黑方胜利 false白方胜利
     */
    @Override
    public Boolean isWin(int x, int y){
        //刚落下的是黑子还是白子
        boolean center = board[x][y];
        int count = 0;
        int row = board.length;
        int column = board[0].length;
        //横向
        for(int i=0; i<5; i++){
            if(y+i>=column || board[x][y+i]==null ||
                    !board[x][y+i].equals(center)){
                break;
            }else{
                count++;
            }
        }
        for(int i=1; i<5; i++){
            if(y-i<0 || board[x][y-i]==null ||
                    !board[x][y-i].equals(center)){
                break;
            }else{
                count++;
            }
        }
        if(count >= 5){
            winner = center;
            return winner;
        }
        //纵向
        count = 0;
        for(int i=0; i<5; i++){
            if(x+i>=row || board[x+i][y]==null ||
                    !board[x+i][y].equals(center)){
                break;
            }else{
                count++;
            }
        }
        for(int i=1; i<5; i++){
            if(x-i<0 || board[x-i][y]==null ||
                    !board[x-i][y].equals(center)){
                break;
            }else{
                count++;
            }
        }
        if(count >= 5){
            winner = center;
            return winner;
        }
        //主对角线
        count = 0;
        for(int i=0; i<5; i++){
            if(x+i>=row || y+i>=column || board[x+i][y+i]==null ||
                    !board[x+i][y+i].equals(center)){
                break;
            }else{
                count++;
            }
        }
        for(int i=1; i<5; i++){
            if(x-i<0 || y-i<0 || board[x-i][y-i]==null ||
                    !board[x-i][y-i].equals(center)){
                break;
            }else{
                count++;
            }
        }
        if(count >= 5){
            winner = center;
            return winner;
        }
        //副对角线
        count = 0;
        for(int i=0; i<5; i++){
            if(x+i>=row || y-i<0 || board[x+i][y-i]==null ||
                    !board[x+i][y-i].equals(center)){
                break;
            }else{
                count++;
            }
        }
        for(int i=1; i<5; i++){
            if(x-i<0 || y+i>=column || board[x-i][y+i]==null ||
                    !board[x-i][y+i].equals(center)){
                break;
            }else{
                count++;
            }
        }
        if(count >= 5){
            winner = center;
        }
        return winner;
    }
}
