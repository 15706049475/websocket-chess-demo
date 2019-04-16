package com.xk.demo.server;

import com.xk.demo.chess.GamePlayer;
import com.xk.demo.util.Localtion;
import com.xk.demo.util.Result;
import com.xk.demo.util.ResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * webSocket服务端
 * @author xiake
 * date - 2019/03/28 11:23:00
 */
/**
 * @author xiake
 *
 */
@Component
@ServerEndpoint("/chess/{username}")
public class ChessServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChessServer.class);

	private static final Map<String, ChessServer> WEB_SOCKET_CHESS_GAMER = new HashMap<String, ChessServer>();
	private static final int LENGTH = 2;
	private GamePlayer gamer = null;
	private Session session = null;

	public GamePlayer getGamer() {
		return gamer;
	}

	@OnOpen
	public void onOpen(Session session,@PathParam("username")String username) {
		if(WEB_SOCKET_CHESS_GAMER.size() == LENGTH) {//第三个人加入，提示他人数够了,强制他退出
			sendMessage(this,"对不起，此房间已满");//提示第三个人，游戏玩家够了
			this.onClose();
			return ;
		}
		//成功连接上服务端，并且玩家人数还不够
		WEB_SOCKET_CHESS_GAMER.put(username,this);
		this.session = session;
		this.gamer = new GamePlayer(username,this.getColor());
		//连接成功，告诉这个客户端一些数据信息
		sendMessage(Result.resultJson(Result.system("GAME_DATA", this.gamer)));
	}

	/**
	 * 收到客户端发来的消息
	 * @param message
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(String message) throws IOException {
		LOGGER.info("message==>{}",message);
		Result rep = Result.resultObject(message);
		LOGGER.info("resultObject===>{}",rep.toString());
		int status = rep.getStatus();
		String data = rep.getData().toString();
		if(status == 1) {
			if("START".equals(data)){
				sendChatMessage(Result.resultJson(Result.system("START",null)));
			}else if("READ".equals(data)){
				this.gamer.setStart(true);//表示这个客户端进入游戏准备状态,那么就把集合中该游戏玩家变为准备状态
				sendChatMessage(Result.resultJson(Result.system(this.gamer.getUsername()+"进入准备状态......",null)));
				//判断两个人是否都进入准备状态
				if(isPrepareAll()){
					//给所有客户端发送游戏开始提示
					sendChatMessage(Result.resultJson(Result.system("START",null)));
				}
			}else if("END".equals(data)){
				//表示这个客户端赢得了游戏，那么就广播所有玩家游戏结束

				Integer color = rep.getLocaltion().getX().intValue();
				GamePlayer gamer = GamePlayer.findGamePlayerByChessColor(WEB_SOCKET_CHESS_GAMER,color);
				sendChatMessage(Result.resultJson(Result.system("END",gamer)));
			}
			//为1是是系统消息
			//data==START表示可以开始游戏
			//data==END表示游戏结束
			//data==CHESS表示提醒页面绘制棋子
		}else if(status == 2) {
			//为2是是普通消息
			sendChatMessage(Result.resultJson(Result.common(this.gamer.getUsername()+ "：" + rep.getData())));
		}else if(status == 3){
			//为3是指令消息
			if("CHESS".equals(rep.getData().toString())){
				//落棋子（动作）
				//应该得到这个棋子的落点,然后广播所有客户端
				Localtion local = rep.getLocaltion();
				String msg = Result.resultJson(Result.command("PLAY_CHESS",this.gamer,local.getX(),local.getY()));
				System.out.println(msg);
				sendChatMessage(msg);

			}
		}
	}

	/**
	 * 单发消息
	 * @param chessServer - 给哪个客户端发
	 * @param msg - 消息
	 */
	public void sendMessage(ChessServer chessServer, String msg) {
		try {
			chessServer.session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			//发送失败，就关闭session，从集合中移除该对象
			chessServer.onClose();
		}
	}

	/**
	 * 给自己发消息
	 * @param msg
	 */
	public void sendMessage(String msg){
		sendMessage(this,msg);
	}

	/**
	 * 群发消息
	 * @param msg - 消息
	 */
	public static void sendChatMessage(String msg) {
		Set<String> keySet = WEB_SOCKET_CHESS_GAMER.keySet();
		ChessServer chessServer = null;
		for (String username : keySet) {
			chessServer = WEB_SOCKET_CHESS_GAMER.get(username);
			chessServer.sendMessage(msg);
		}
	}

	@OnClose
	public void onClose(){
		WEB_SOCKET_CHESS_GAMER.remove(this.gamer.getUsername());
		if(this.session.isOpen()) {
			try {
				this.session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 为加入游戏的玩家分配棋子，1为白，-1为黑
	 * @return
	 */
	protected Integer getColor(){
		int size = WEB_SOCKET_CHESS_GAMER.size();
		return size == 1 ? 1 : -1;
	}

	/**
	 * 判断所有玩家是否都进入游戏准备状态，玩家人数必须为2人
	 * @return
	 */
	public static boolean isPrepareAll(){
		Collection<ChessServer> values = WEB_SOCKET_CHESS_GAMER.values();
		if(values.size() != 2)
			return false;
		GamePlayer player = null;
		for(ChessServer server: values){
			player = server.gamer;
			if(player.isStart()==false){
				return false;
			}
		}
		return true;
	}



}
