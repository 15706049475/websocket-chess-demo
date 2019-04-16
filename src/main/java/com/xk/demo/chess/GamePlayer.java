package com.xk.demo.chess;

import com.xk.demo.server.ChessServer;

import java.util.Collection;
import java.util.Map;

/**
 * 封装玩家属性
 * @author xiake
 *
 */
public class GamePlayer {

    private String username;            //玩家名
    private Integer color = 0;			//玩家执红旗还是黑棋，1为白，-1为黑
    private boolean	isStart = false;	//此玩家是否已经准备游戏
    private Boolean isChess;			//是否由该玩家下棋

    public GamePlayer(String username, Integer color) {
        this.username = username;
        this.color = color;
        this.isChess = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public Boolean getChess() {
        return isChess;
    }

    public void setChess(Boolean chess) {
        isChess = chess;
    }

    /**
     * 根据棋子颜色查找赢家
     * @return
     */
    public static GamePlayer findGamePlayerByChessColor(Map<String, ChessServer> map, Integer color){
        Collection<ChessServer> values = map.values();
        GamePlayer gamer = null;
        for(ChessServer server: values){
            gamer = server.getGamer();
            if(gamer.getColor()==color){
                return gamer;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "username='" + username + '\'' +
                ", color=" + color +
                ", isStart=" + isStart +
                ", isChess=" + isChess +
                '}';
    }
}
