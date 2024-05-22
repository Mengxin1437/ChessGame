package com.example.myapplication.logic;

/**
 * 黑白棋
 */
public class Reversi extends Chess {
    @Override
    public Boolean isWin(int x, int y) {
        //黑白棋的判断胜负方式

        return winner;
    }

    @Override
    public void moveDown(int x, int y) {
        super.moveDown(x, y);
        //导致棋的黑白变换

    }

    @Override
    protected void initBoard(int row, int column) {
        super.initBoard(row, column);
        //初始放置四个棋子

    }

    @Override
    public boolean isMovePositionOk(int x, int y) {
        if(!super.isMovePositionOk(x, y)) return false;
        boolean result = true;
        //合法性判断

        return result;
    }
}
