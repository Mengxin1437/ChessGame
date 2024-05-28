package com.example.myapplication.logic;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 棋类(黑棋和白棋，轮流下的棋类，棋盘是方格)，行列坐标都是从0开始
 *  * 用true表示黑棋，false表示白棋 null表示棋盘的空位置
 *  * turn为true时表示轮到黑方落子
 *  * winner为null时表示游戏还未结束，true黑方赢，false白方赢
 */
public abstract class Chess {
    protected Boolean turn; //当前轮到黑方还是白方下棋
    protected Boolean[][] board; //棋盘的状态，遵循所见即所得
    protected Boolean winner; //胜利方

    public Boolean getWinner() {
        return winner;
    }

    //用于存储每一步的若干操作
    protected LinkedList<ArrayList<Operation>> operations;
    public Point previousPoint; //上一步落子位置

    public Boolean getTurn() {
        return turn;
    }

    public Boolean[][] getBoard() { return board; }

    //初始化游戏
    public void init(int row, int column){
        previousPoint = null;
        operations = new LinkedList<>();
        winner = null; //初始化胜利者为空
        turn = true; //黑方先
        //初始化棋盘
        initBoard(row, column);
    }

    //初始化棋盘
    protected void initBoard(int row, int column){
        board = new Boolean[row][column];
        for(int i=0; i<row; i++){
            board[i] = new Boolean[column];
        }
    }

    /**
     * 需要用户在落子前进行判断是否合法 一般来说落子点必须在范围内并为空
     * 如果子类有新的要求则需要重写并调用super.isMovePositionOk来判断是否合法
     * @param x 行 [0, row)
     * @param y 列 [0, column)
     * @return true合法
     */
    public boolean isMovePositionOk(int x, int y){
        //判断范围是否合法
        if(x<0 || x>=board.length) return false;
        if(y<0 || y>=board[0].length) return false;
        //判断落子点是否为空
        return board[x][y] == null;
    }

    //撤销一步棋
    public void cancelOneStep(){
        if(operations.isEmpty()) return; //空操作链表无法再撤销
        ArrayList<Operation> opts = operations.getLast();
        for(Operation o: opts){
            int r = o.p.x;
            int c = o.p.y;
            board[r][c] = o.bl;
        }
        turn = !turn;
        operations.removeLast();
        //回滚最后一步下的棋的位置
        if(operations.isEmpty()) {
            previousPoint = null;
        }else {
            opts = operations.getLast();
            Operation ot = opts.get(0);
            previousPoint = ot.p;
        }
    }

    /**
     * 落子并交换回合
     * @param x 行[0, row)
     * @param y 列[0, column)
     */
    public void moveDown(int x, int y){
        operations.add(new ArrayList<>());
        previousPoint = new Point(x, y);
        //记录棋盘变化点之前的状态
        operations.getLast().add(new Operation(previousPoint, board[x][y]));
        board[x][y] = turn;
        turn = !turn;
    }

    /**
     * 从落子点出发判断游戏是否结束
     * @param x 行 [0, row)
     * @param y 列 [0, column)
     * @return null未结束 true黑方胜利 false白方胜利
     */
    public abstract Boolean isWin(int x, int y);
}
