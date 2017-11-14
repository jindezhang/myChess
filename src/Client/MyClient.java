package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class MyClient extends Thread{
	private Socket socket = null;//此类实现客户端套接字（也可以就叫“套接字”）。套接字是两台机器间通信的端点
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private boolean run = true;
	
	private HallForm hallForm;
	private ChessGamePanel chessGamePanel;
	private ChessGamePanel.QiPanPanel qiPanPanel;
	
	public MyClient(){
	}

	public HallForm getHallForm() {
		return hallForm;
	}

	public void setHallForm(HallForm hallForm) {
		this.hallForm = hallForm;
	}

	public ChessGamePanel getChessGamePanel() {
		return chessGamePanel;
	}

	public void setChessGamePanel(ChessGamePanel chessGamePanel) {
		this.chessGamePanel = chessGamePanel;
	}

	public ChessGamePanel.QiPanPanel getQiPanPanel() {
		return qiPanPanel;
	}

	public void setQiPanPanel(ChessGamePanel.QiPanPanel qiPanPanel) {
		this.qiPanPanel = qiPanPanel;
	}

	public boolean connect(String serverIP){
		try{
			socket = new Socket(serverIP,8888);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			this.start();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	//发送消息
	public void sendMessage(String msg){
		System.out.println("发送的消息："+msg);
		try{
			dos.writeUTF(msg);		//DataOutputStream类的方法：以与机器无关方式使用 UTF-8 修改版编码将一个字符串写入基础输出流。	
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	//接收消息
	public void run(){
		String recvMsg = null;
		String commandMsg = null;
		int pos;
		try{
			while(run){
				//监听获取服务器的信息。
				recvMsg = dis.readUTF();
				System.out.println("接收到的信息："+recvMsg);
				pos = recvMsg.indexOf(":");//返回指定字符串在此字符串中第一次出现处的索引。
				commandMsg = recvMsg.substring(0,pos);//返回一个新的字符串，它是此字符串的一个子字符串
				if(commandMsg.equals(PacketMessage.LOGO_MESSAGE)){
					handleLogoMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.JOIN_TABLE)){
					handleJoinTableMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.JOIN_TABLE_FAIL)){
					handleJoinTableFailMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.JOIN_TABLE_SUCCESS)){
					handleJoinTableSuccessMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.JOIN_TABLE_OPPO)){
					handleJoinTableOppoMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.LEAVE_TABLE_OPPO)){
					handleLeaveTableOppoMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.GAME_START)){
					chessGamePanel.gameStart();
				}else if(commandMsg.equals(PacketMessage.GAME_READY)){ //对方准备好的信息
					chessGamePanel.opponentReady();
				}else if(commandMsg.equals(PacketMessage.MOVE_CHESS)){ //对方准备好的信息
					handleMoveChessMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.GAME_FAIL)){ //对方输了的信息
					chessGamePanel.gameOver("恭喜你，你赢了！");
				}else if(commandMsg.equals(PacketMessage.GAME_GIVEIN)){ //对方准备好的信息
					chessGamePanel.gameOver("对手认输，你赢了！");
				}else if(commandMsg.equals(PacketMessage.EXIT_GAME)){ //对方准备好的信息
					handleExitGameMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.CHAT_MESSAGE)){
					chessGamePanel.showChat(recvMsg.substring(pos+1));
				}
				else if(commandMsg.equals(PacketMessage.GAME_PEACE)){
					chessGamePanel.gamePeace(recvMsg);
			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
	
	//处理登录信息，在服务器信息框中显示信息
	public void handleLogoMessage(String msg){
		String name;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		//为指定的字符串构造一个	StringTokenizer，用“：”分割信息。用nextToken方法去截取			
		name = stMsg.nextToken();
		System.out.println(name+"登录");
		hallForm.addServerMessage(name+"登录");
	}
	
	//处理加入桌子信息，更新桌子椅子
	public void handleJoinTableMessage(String msg){
		int preDeskNo, preChairNo, deskNo, chairNo;
		String imgStr, userName;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		preDeskNo = Integer.parseInt(stMsg.nextToken());
		preChairNo = Integer.parseInt(stMsg.nextToken());
		deskNo = Integer.parseInt(stMsg.nextToken());
		chairNo = Integer.parseInt(stMsg.nextToken());
		imgStr = stMsg.nextToken();
		userName = stMsg.nextToken();
		hallForm.setTable(preDeskNo, preChairNo, deskNo, chairNo, imgStr, userName);	
	}
	
	//处理不能加入桌子信息，恢复preDeskNo和preChairNo
	public void handleJoinTableFailMessage(String msg){
		int preDeskNo, preChairNo;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		preDeskNo = Integer.parseInt(stMsg.nextToken());
		preChairNo = Integer.parseInt(stMsg.nextToken());
		hallForm.canNotSeat(preDeskNo, preChairNo);
	}
	
	//处理成功加入桌子消息，（关注对手是否已经落座，记录桌子椅子号）
	public void handleJoinTableSuccessMessage(String msg){
		String oppoImg, oppoName;
		int deskNo, chairNo;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		deskNo = Integer.parseInt(stMsg.nextToken());		
		chairNo = Integer.parseInt(stMsg.nextToken());
		if(stMsg.hasMoreTokens()){
			oppoImg = stMsg.nextToken();
			oppoName = stMsg.nextToken();
			hallForm.enterChessRoom(oppoImg, oppoName, deskNo, chairNo);
		}else
			hallForm.enterChessRoom(null, null, deskNo, chairNo);
	}
	
	//处理加入桌子的对手信息
	public void handleJoinTableOppoMessage(String msg){
		String oppoImg, oppoName;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		oppoImg = stMsg.nextToken();
		oppoName = stMsg.nextToken();
		chessGamePanel.updateOpponent(oppoImg, oppoName);
	}
	
	//处理对手离开桌子信息
	public void handleLeaveTableOppoMessage(String msg){
		String oppoName;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		oppoName = stMsg.nextToken();
		chessGamePanel.updateLeaveOpponent(oppoName); 
	}
	
	//处理走一步棋信息
	public void handleMoveChessMessage(String msg){
		int oldx, oldy, newx, newy;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		//读取并更新位置（对手和自己的位置正好对称）
		oldx = 9-Integer.parseInt(stMsg.nextToken());
		oldy = 8-Integer.parseInt(stMsg.nextToken());
		newx = 9-Integer.parseInt(stMsg.nextToken());
		newy = 8-Integer.parseInt(stMsg.nextToken());
		chessGamePanel.oppoMoveChess(oldx, oldy, newx, newy);
	}
	
	//处理离开房间信息
	public void handleExitGameMessage(String msg){
		int deskNo, chairNo;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		deskNo = Integer.parseInt(stMsg.nextToken());
		chairNo = Integer.parseInt(stMsg.nextToken());
		hallForm.exitChessRoom(deskNo, chairNo);
	}

}
