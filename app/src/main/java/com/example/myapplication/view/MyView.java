package com.example.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.logic.Chess;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MyView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {
    private Bitmap targetBitmap;
    private Bitmap blackChess;
    private Bitmap whiteChess;
    private Paint paint;
    private Float arrH;
    private Float arrV;
    private Boolean reaction; //当前是不是你的回合
    private Chess chess;
    public Boolean flag; //先后手 1执黑先手 如果为null表示这不是联机对局

    public Point confirmPoint; //待确认落子的位置，x行，y列
    public Socket socket;
    public MyView(@NonNull Context context) {
        super(context);
        init();
    }

    public MyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        setOnTouchListener(this);
        paint = new Paint(Color.BLACK);
        reaction = true;
    }

    public void setChess(Chess chess) { this.chess = chess; }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent != null){
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                if(!reaction) return false;
                //不是自己的回合不能落子
                if(flag != null){
                    if(!chess.getTurn().equals(flag)) return false;
                }
                Point pos = getPos(motionEvent);
                confirmPoint = pos;
                invalidate();
            }
        }
        return false;
    }

    /**
     * 被确认落子按钮调用
     * @return true操作成功 false操作失败
     */
    public boolean dropDown() {
        int x = confirmPoint.x;
        int y = confirmPoint.y;

        if(chess.isMovePositionOk(x, y)){
            moveDownAndJudgeWin(x, y);
            sendToServer(x, y);
            confirmPoint = null;
            return true;
        }
        return false;
    }

    /**
     * 落子并判断胜负
     */
    public void moveDownAndJudgeWin(int x, int y){
        chess.moveDown(x, y);

        Boolean result = chess.isWin(x, y);
        if(result != null){
            Log.i("result", (result?"黑方":"白方") + "胜利");
            reaction = false;
        }
        invalidate();
    }

    /**
     * 向服务器发送操作信息
     */
    private void sendToServer(int x, int y){
        if(socket != null){
            Log.i("myview", String.valueOf(x*100+y+" "+x+" "+y));
            new Thread(() -> {
                try {
                    socket.getOutputStream().write(x);
                    socket.getOutputStream().write(y);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();
        }
    }

    private Point getPos(MotionEvent e){
        //使用e.getX();e.getY()获取横纵坐标并转化到棋盘的坐标
        //示例:System.out.println(e.getX()+" "+e.getY());
        float x = e.getX();
        float y = e.getY();
        int row = chess.getBoard().length;
        int column = chess.getBoard()[0].length;
        double arrV = getWidth() / 2.0 / row;
        double arrH = getWidth() / 2.0 / column;

        int b = (int)Math.round(((x - arrH) * column) / getWidth());
        int a = (int)Math.round(((y - arrV) * row) / getWidth());

        return new Point(a,b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Boolean[][] board;
        if(chess != null)
            board = chess.getBoard();
        else return;

        if(arrH == null || arrV == null){
            arrV = (float) Math.round(getWidth() / 2.0 / board.length);
            arrH = (float) Math.round(getWidth() / 2.0 / board[0].length);
        }

        //绘制背景图片
        if(targetBitmap == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.desk);
            //计算放缩倍率
            float scaledW = (float) getWidth() / bitmap.getWidth();
            float scaledH = (float) getHeight() / bitmap.getHeight();
            Matrix matrix0 = new Matrix();
            matrix0.postScale(scaledW, scaledH);
            //获得目标大小的Bitmap
            targetBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix0, true);
        }

        canvas.drawBitmap(targetBitmap, 0, 0, null);

        if(board != null) {
            //绘制方格
            drawBoardBackground(canvas);
            //绘制棋子
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] != null) {
                        drawChessman(canvas, board[i][j], i, j);
                    }
                }
            }
            //绘制待放置棋子
            if(confirmPoint != null) {
                boolean turn = chess.getTurn();
                drawChessman(canvas, turn, confirmPoint.x, confirmPoint.y);
            }
            drawTriangleFlag(canvas);
            if(chess.getWinner() != null){
                paint.setTextSize(50);
                canvas.drawText(chess.getWinner() ?"黑方胜利":"白方胜利", getWidth()/2 - getWidth()/8, getWidth()/2, paint);
            }
        }
    }

    private void drawBoardBackground(Canvas g){
        int horizontal = 0;
        int vertical = 0;
        int row = chess.getBoard().length;
        int column = chess.getBoard()[0].length;
        //g.drawLine(arrH,arrV,(boardWidth*18/column)+arrH,(boardWidth*18/column)+arrH);
        for(int a = 0; a<row; a++){
            vertical = getWidth() * a / row; //先乘再做整除，精度更高
            g.drawLine(arrH, arrV+vertical,
                    arrH+getWidth()*(column-1)/(float)column, arrV+vertical, paint);

        }
        for(int b = 0; b<column; b++){
            horizontal = getWidth() * b / column;
            g.drawLine(arrH+horizontal, arrV,
                    arrH+horizontal, arrV+getWidth()*(row-1)/(float)row, paint);
        }

        //绘制角上4个星位
        int r = row>=9&&row<13 ? 2 : 3;
        int c = column>=9&&column<13 ? 2 : 3;
        drawBlackPoint(g, r, c);
        drawBlackPoint(g, r, column-c-1);
        drawBlackPoint(g, row-r-1, c);
        drawBlackPoint(g, row-r-1, column-c-1);

        if(row%2==1 && column%2==1){
            //绘制天元
            drawBlackPoint(g, row/2, column/2);

            //绘制星位之间的中点
            if(row>=15 && column>=15){
                drawBlackPoint(g, r, column/2);
                drawBlackPoint(g, row/2, c);
                drawBlackPoint(g, row/2, column-c-1);
                drawBlackPoint(g, row-r-1, column/2);
            }
        }
    }
    /**
     * 根据指定位置绘制一个棋子
     * @param g 可以绘图的对象
     * @param bn true黑棋 false白棋
     * @param r 棋子所在行
     * @param c 棋子所在列
     */
    private void drawChessman(Canvas g, boolean bn, int r, int c){
        Boolean[][] board = chess.getBoard();
        int row = board.length;
        int column = board[0].length;
        float width = (float)getWidth()/Math.max(column, row);
        if(blackChess == null || whiteChess == null){
            Bitmap whiteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white);
            Bitmap blackBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
            //计算放缩倍率
            float sw0 = width/whiteBitmap.getWidth();
            float sh0 =  width/whiteBitmap.getHeight();
            float sw1 = width/blackBitmap.getWidth();
            float sh1 = width/blackBitmap.getHeight();
            Matrix matrix0 = new Matrix();
            matrix0.postScale(sw0, sh0);
            Matrix matrix1 = new Matrix();
            matrix1.postScale(sw1, sh1);
            //获得目标大小的Bitmap
            whiteChess = Bitmap.createBitmap(whiteBitmap, 0, 0,
                    whiteBitmap.getWidth(), whiteBitmap.getHeight(), matrix0, true);
            blackChess = Bitmap.createBitmap(blackBitmap, 0, 0,
                    blackBitmap.getWidth(), blackBitmap.getHeight(), matrix1, true);
        }
        g.drawBitmap(bn?blackChess:whiteChess, getWidth()*c/(float)column+arrH-width/2,
                getWidth()*r/(float)row+arrV-width/2, null);
    }

    /**
     * 绘制当前落子标识
     */
    private void drawTriangleFlag(Canvas g){
        if (chess == null || chess.previousPoint==null) return;
        int r = chess.previousPoint.x;
        int c = chess.previousPoint.y;
        int row = chess.getBoard().length;
        int column = chess.getBoard()[0].length;
        float startX = (getWidth()*c/column)+arrH;
        float startY = (getWidth()*r/row)+arrV;
        int width = getWidth()/Math.max(column, row);
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(chess.getTurn() ? Color.BLACK : Color.WHITE);
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(startX+width/2, startY);
        path.lineTo(startX, startY+width/2);
        path.lineTo(startX, startY);
        path.close();
        g.drawPath(path, paint1);
    }

    /**
     * 绘制黑圆点
     */
    private void drawBlackPoint(Canvas g, int r, int c){
        Boolean[][] board = this.chess.getBoard();
        int row = board.length;
        int column = board[0].length;
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        int radius = getWidth()/150;
        g.drawCircle((getWidth()*c/column)+arrH,
                (getWidth()*r/row)+arrV, radius, paint1);
    }
}
