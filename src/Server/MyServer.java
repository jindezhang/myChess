package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import Client.PacketMessage;
import Client.User;

public class MyServer {
	private Vector<User> userList = new Vector<User>();
	private final int TotalDeskNum = 15;
	private Desk[] desks = new Desk[TotalDeskNum];
	
	public MyServer(){
		//初始化桌子
		for(int i=0; i<TotalDeskNum; i++)
			desks[i] = new Desk();
	}
	
	//等待连接
	public void waitConnect(){
		try{
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(8888);//建立服务器，8888为监听端口
			while (true) {
				Socket s = ss.accept();//等待监听
				//获取输入流。
				DataInputStream dis = new DataInputStream(s.getInputStream());//获取输入流
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				User user = new User(dis, dos);
				userList.add(user);//存储登录用户
				//创建用户线程
				UserThread userThread = new UserThread(user);
				userThread.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		MyServer server = new MyServer();
		server.waitConnect();
	}
	
	//内部类，用户线程，处理每个用户的连接
	 class UserThread extends Thread{
			private User user = null;
			private boolean run = true;
			private DataInputStream dis = null;
			
			public UserThread(User user) {
				super();
				this.user = user;
				dis = user.getDis();
			}

			public void run(){
				String msg = null;
				int pos;
				String commandMsg = null;
				try{
					while(run){
						msg = dis.readUTF();
						System.out.println("接收到的信息："+msg);
						pos = msg.indexOf(":");
						commandMsg = msg.substring(0,pos);
						if(commandMsg.equals(PacketMessage.LOGO_MESSAGE)){ //登录
							handleLogoMessage(msg.substring(pos+1));
							sendToAllUser(msg);
						}else if(commandMsg.equals(PacketMessage.JOIN_TABLE)){ //加入桌子
							if(handleJoinTableMessage(msg.substring(pos+1)))
								sendToAllUser(msg);
						}else if(commandMsg.equals(PacketMessage.GAME_READY)){ //本方准备好
							handleGameReadyMessage(msg.substring(pos+1));
						}else if(commandMsg.equals(PacketMessage.MOVE_CHESS)){ //下棋信息
							//直接转发给对手
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}else if(commandMsg.equals(PacketMessage.GAME_FAIL)){ //输了信息
							handleGameOverMessage();
							//转发给对手
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}else if(commandMsg.equals(PacketMessage.GAME_GIVEIN)){ //认输信息
							handleGameOverMessage();
							//转发给对手
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}else if(commandMsg.equals(PacketMessage.EXIT_GAME)){ //退出房间信息
							handleExitGameMessage();
						}
						else if(commandMsg.equals(PacketMessage.CHAT_MESSAGE)){
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}
						else if(commandMsg.equals(PacketMessage.GAME_PEACE)){
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
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
			
			//处理登陆信息，记录用户名和头像名
			public synchronized void handleLogoMessage(String msg){
				String name, img;
				StringTokenizer stMsg = new StringTokenizer(msg,":");				
				name = stMsg.nextToken();
				user.setName(name); //用户名
				img = stMsg.nextToken();
				user.setImg(img); //头像名
				System.out.println(name + "登录，头像是："+img);	
				//给该用户发送桌子列表
				User user;
				for(int i=0; i<TotalDeskNum; i++){
					user = desks[i].getUser(0); //左椅子用户
					if(user != null){
						sendToMe(PacketMessage.JOIN_TABLE+":-1:-1:"+i+":0:"+user.getImg()+":"+user.getName());
					}
					user = desks[i].getUser(1); //右椅子用户
					if(user != null){
						sendToMe(PacketMessage.JOIN_TABLE+":-1:-1:"+i+":1:"+user.getImg()+":"+user.getName());
					}
				}
				
			}
			
			//处理落座信息
			public synchronized boolean handleJoinTableMessage(String msg){
				int preDeskNo, preChairNo, newDeskNo, newChairNo;
				User userOpponent;
				StringTokenizer stMsg = new StringTokenizer(msg,":");	
				preDeskNo = Integer.parseInt(stMsg.nextToken());
				preChairNo = Integer.parseInt(stMsg.nextToken());
				newDeskNo = Integer.parseInt(stMsg.nextToken());
				newChairNo = Integer.parseInt(stMsg.nextToken());

				if(!desks[newDeskNo].setUser(newChairNo, user)){ 
					//不能加入桌子，发送JOIN_TABLE_FAILE信息   09:先前桌子号:先前椅子号
					sendToMe(PacketMessage.JOIN_TABLE_FAIL +":"+preDeskNo+":"+preChairNo);
					return false;
				}else{
					//能加入桌子，
					user.setDeskId(newDeskNo);  //更新用户桌子椅子信息
					user.setChairId(newChairNo);
					if(preDeskNo != -1){ //之前已经落座，则删除之前落座信息
						desks[preDeskNo].deleteUser(preChairNo);
						userOpponent = desks[preDeskNo].getUserOpponent(preChairNo);
						if(userOpponent != null){ //如果之前的桌子有对手，则向对手发送离开桌子信息
							sendToUser(PacketMessage.LEAVE_TABLE_OPPO+":"+user.getName(), userOpponent);
						}
						user.setReady(false); //离开桌子，更新是否准备好
					}					
					
					userOpponent = desks[newDeskNo].getUserOpponent(newChairNo);
					if(userOpponent != null){
						//发送JOIN_TABLE_SUCCESS信息   10:当前桌子号:当前椅子号:对方头像名:对方用户名  （作为打开房间界面的依据）
						sendToMe(PacketMessage.JOIN_TABLE_SUCCESS+":"+newDeskNo+":"+newChairNo+":"+userOpponent.getImg()+":"+userOpponent.getName());
						//向对手发送JOIN_TABLE_OPPO信息， 11:本方头像名：本方用户名 （让对手更新“对手"头像和名称
						sendToUser(PacketMessage.JOIN_TABLE_OPPO+":"+user.getImg()+":"+user.getName(), userOpponent);
						if(userOpponent.isReady())
							sendToMe(PacketMessage.GAME_READY+":");
					}
					else
						//发送JOIN_TABLE_SUCCESS信息   10:当前桌子号:当前椅子号 （对手不存在，不发送头像和名称）
						sendToMe(PacketMessage.JOIN_TABLE_SUCCESS+":"+newDeskNo+":"+newChairNo);
					return true;
				}
			}
			
			//处理离开房间信息
			public void handleExitGameMessage(String msg){
				
			}
			
			//处理本方准备好信息
			public void handleGameReadyMessage(String msg){
				User userOpponent;
				user.setReady(true);
				userOpponent = desks[user.getDeskId()].getUserOpponent(user.getChairId());
				if(userOpponent != null){ 
					if(userOpponent.isReady()){ //对方也准备好，则游戏开始
						sendToMe(PacketMessage.GAME_START+":");
						sendToUser(PacketMessage.GAME_START+":", userOpponent);
						desks[user.getDeskId()].setStart(true);
					}else //否则将本方准备好的信息发送给对方
						sendToUser(PacketMessage.GAME_READY+":", userOpponent);
				}
				
			}
			
			//处理认输和输了的信息（游戏结束）
			public void handleGameOverMessage(){
				user.setReady(false);
				desks[user.getDeskId()].getUserOpponent(user.getChairId()).setReady(false);
			}
			
			//处理退出房间信息
			 public void handleExitGameMessage(){				 
				 sendToAllUser(PacketMessage.EXIT_GAME+":"+user.getDeskId()+":"+user.getChairId());
				 
				 User userOpponent = desks[user.getDeskId()].getUserOpponent(user.getChairId());
				 if(userOpponent != null){ //如果桌子有对手，则向对手发送离开桌子信息
					sendToUser(PacketMessage.LEAVE_TABLE_OPPO+":"+user.getName(), userOpponent);
				}
				 desks[user.getDeskId()].deleteUser(user.getChairId());
				 user.setDeskId(-1);
				 user.setChairId(-1);
			 }
			
			//向所有人转发信息
			public void sendToAllUser(String msg){				
				try{
					Iterator<User> iterator = userList.iterator();
					while(iterator.hasNext()){
						iterator.next().getDos().writeUTF(msg);						
					}
					System.out.println("群发的信息："+msg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//向自己回复信息
			public void sendToMe(String msg){
				try{
					user.getDos().writeUTF(msg);
					System.out.println("向"+user.getName()+"回复的信息："+msg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//向某用户发送的信息
			public void sendToUser(String msg, User userOther){
				try{
					userOther.getDos().writeUTF(msg);
					System.out.println("向"+userOther.getName()+"发送的信息："+msg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
	 }
}

