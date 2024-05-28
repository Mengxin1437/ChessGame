package com.example.myapplication.logic;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.HashSet;

public class Go extends Chess{
    private Point banPos; //禁着点

    @Override
    protected void initBoard(int row, int column) {
        super.initBoard(row, column);
        banPos = null;
    }

    @Override
    public Boolean isWin(int x, int y) {
        return null;
    }

    @Override
    public boolean isMovePositionOk(int x, int y) {
        if(!super.isMovePositionOk(x, y)) return false;
        boolean result = true;
        //合法性判断

        //1.不能下在让自己方棋子没气的位置
        boolean res1, res2=false, res3, res4=false;
        /*试验性落子*/
        board[x][y] = turn;
        HashSet<Point> tribeSelf = getTribe(x, y); //获取落子后所处的部落
        HashSet<Point> gasSelf = getGas(tribeSelf);
        if(gasSelf.isEmpty()){
            res1 = false; //下了之后自己没气了
        }else{
            res1 = true;
        }

        //2.当下的位置可以让对方棋子被提子时规则1不成立
        ArrayList<Point> around = getAllAround(x, y);
        for(Point p: around){
            if(board[p.x][p.y] != null && board[p.x][p.y] != board[x][y]){
                HashSet<Point> enemyTribe = getTribe(p.x, p.y);
                HashSet<Point> enemyGas = getGas(enemyTribe);
                if(enemyGas.isEmpty()){
                    res2 = true; //下了之后可以提掉对方的棋子
                    break;
                }
            }
        }

        //3.当下的位置是禁着点时2不成立
        if(banPos==null){
            res3 = false; //不是禁着点
        }else{
            res3 = banPos.equals(new Point(x, y));
        }

        //4.当下的位置可以让对方提多个棋子时禁着点不成立
        for(Point p: around){
            if(board[p.x][p.y] != null && board[p.x][p.y] != board[x][y]){
                HashSet<Point> enemyTribe = getTribe(p.x, p.y);
                HashSet<Point> enemyGas = getGas(enemyTribe);
                if(enemyGas.isEmpty() && enemyTribe.size()>1){
                    res4 = true; //可以提掉对方的多个棋子
                    break;
                }
            }
        }

        /*还原*/
        board[x][y] = null;

//        if(res4){
//            return true;
//        }else{
//            if(res3){
//                return false;
//            }else{
//                if(res2) return true;
//                else return res1;
//            }
//        }
        //简化后的
        return res4 || (!res3 && (res2 || res1));
    }

    @Override
    public void moveDown(int x, int y) {
        super.moveDown(x, y); //会直接改变board并交换turn

        banPos = null; //落子后本轮的禁着点就无效了
        ArrayList<Point> around = getAllAround(x, y);
        for(Point p: around){
            if(board[p.x][p.y] != null && board[p.x][p.y] != board[x][y]){//敌方棋子
                HashSet<Point> tribe = getTribe(p.x, p.y);
                HashSet<Point> gas = getGas(tribe);
                if(gas.isEmpty()){ //敌方棋子没气了
                    ArrayList<Operation> opts = operations.getLast();
                    //提子
                    for(Point pt: tribe){
                        opts.add(new Operation(pt, board[pt.x][pt.y]));
                        board[pt.x][pt.y] = null;
                    }
                    // 被提的棋子只有一个时可能出现禁着点
                    if(tribe.size() == 1){
                        banPos = tribe.iterator().next();
                    }
                }
            }
        }
    }

    /**
     * 从一个确认有棋子的点出发获取它所属部落的所有点的集合
     */
    private HashSet<Point> getTribe(int r, int c){
        HashSet<Point> points = new HashSet<>();
        points.add(new Point(r, c)); //添加第一个点
        dg(points, r, c);
        return points;
    }
    //下面这个方法是给上面方法递归调用的
    private void dg(HashSet<Point> points, int r, int c){
        boolean self = board[r][c];
        ArrayList<Point> around = getAllAround(r, c);
        for(Point pnt: around){
            if(board[pnt.x][pnt.y]!=null && board[pnt.x][pnt.y]==self && !points.contains(pnt)){
                points.add(pnt);
                dg(points, pnt.x, pnt.y); //保证每个点只被递归过一次
            }
        }
    }

    /**
     * 获取一个部落所有的气
     */
    private HashSet<Point> getGas(HashSet<Point> tribe){
        HashSet<Point> gas = new HashSet<>();
        for(Point pnt: tribe){
            ArrayList<Point> around = getAllAround(pnt.x, pnt.y);
            for(Point p: around){
                if(board[p.x][p.y]==null){
                    gas.add(p);
                }
            }
        }
        return gas;
    }

    /**
     * 获取一个点四周的点，排除超界的点
     * @param r 这个点的行
     * @param c 这个点的列
     * @return 四周的点的集合
     */
    private ArrayList<Point> getAllAround(int r, int c){
        ArrayList<Point> points = new ArrayList<>();
        int height = board.length;
        int width = board[0].length;
        if(r-1>=0)
            points.add(new Point(r-1, c));
        if(r+1<height)
            points.add(new Point(r+1, c));
        if(c-1>=0)
            points.add(new Point(r, c-1));
        if(c+1<width)
            points.add(new Point(r, c+1));
        return points;
    }
}
