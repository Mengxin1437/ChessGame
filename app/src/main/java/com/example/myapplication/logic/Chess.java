package com.example.myapplication.logic;

/**
 * 棋类(黑棋和白棋，轮流下的棋类，棋盘是方格)，行列坐标都是从0开始
 *  * 用true表示黑棋，false表示白棋 null表示棋盘的空位置
 *  * turn为true时表示轮到黑方落子
 *  * winner为null时表示游戏还未结束，true黑方赢，false白方赢
 */
public abstract class Chess {
    protected Boolean turn; //当前轮到黑方还是白方下棋
    protected Boolean[][] board; //棋盘的状态，遵循所见即所得
    public Boolean winner; //胜利方

    public Boolean getTurn() {
        return turn;
    }

    public Boolean[][] getBoard() { return board; }

    //初始化游戏
    public void init(int row, int column){
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

    /**
     * 落子并交换回合
     * @param x 行[0, row)
     * @param y 列[0, column)
     */
    public void moveDown(int x, int y){
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
