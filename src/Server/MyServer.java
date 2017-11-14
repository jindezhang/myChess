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
		//��ʼ������
		for(int i=0; i<TotalDeskNum; i++)
			desks[i] = new Desk();
	}
	
	//�ȴ�����
	public void waitConnect(){
		try{
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(8888);//������������8888Ϊ�����˿�
			while (true) {
				Socket s = ss.accept();//�ȴ�����
				//��ȡ��������
				DataInputStream dis = new DataInputStream(s.getInputStream());//��ȡ������
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				User user = new User(dis, dos);
				userList.add(user);//�洢��¼�û�
				//�����û��߳�
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
	
	//�ڲ��࣬�û��̣߳�����ÿ���û�������
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
						System.out.println("���յ�����Ϣ��"+msg);
						pos = msg.indexOf(":");
						commandMsg = msg.substring(0,pos);
						if(commandMsg.equals(PacketMessage.LOGO_MESSAGE)){ //��¼
							handleLogoMessage(msg.substring(pos+1));
							sendToAllUser(msg);
						}else if(commandMsg.equals(PacketMessage.JOIN_TABLE)){ //��������
							if(handleJoinTableMessage(msg.substring(pos+1)))
								sendToAllUser(msg);
						}else if(commandMsg.equals(PacketMessage.GAME_READY)){ //����׼����
							handleGameReadyMessage(msg.substring(pos+1));
						}else if(commandMsg.equals(PacketMessage.MOVE_CHESS)){ //������Ϣ
							//ֱ��ת��������
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}else if(commandMsg.equals(PacketMessage.GAME_FAIL)){ //������Ϣ
							handleGameOverMessage();
							//ת��������
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}else if(commandMsg.equals(PacketMessage.GAME_GIVEIN)){ //������Ϣ
							handleGameOverMessage();
							//ת��������
							sendToUser(msg, desks[user.getDeskId()].getUserOpponent(user.getChairId()));
						}else if(commandMsg.equals(PacketMessage.EXIT_GAME)){ //�˳�������Ϣ
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
			
			//�����½��Ϣ����¼�û�����ͷ����
			public synchronized void handleLogoMessage(String msg){
				String name, img;
				StringTokenizer stMsg = new StringTokenizer(msg,":");				
				name = stMsg.nextToken();
				user.setName(name); //�û���
				img = stMsg.nextToken();
				user.setImg(img); //ͷ����
				System.out.println(name + "��¼��ͷ���ǣ�"+img);	
				//�����û����������б�
				User user;
				for(int i=0; i<TotalDeskNum; i++){
					user = desks[i].getUser(0); //�������û�
					if(user != null){
						sendToMe(PacketMessage.JOIN_TABLE+":-1:-1:"+i+":0:"+user.getImg()+":"+user.getName());
					}
					user = desks[i].getUser(1); //�������û�
					if(user != null){
						sendToMe(PacketMessage.JOIN_TABLE+":-1:-1:"+i+":1:"+user.getImg()+":"+user.getName());
					}
				}
				
			}
			
			//����������Ϣ
			public synchronized boolean handleJoinTableMessage(String msg){
				int preDeskNo, preChairNo, newDeskNo, newChairNo;
				User userOpponent;
				StringTokenizer stMsg = new StringTokenizer(msg,":");	
				preDeskNo = Integer.parseInt(stMsg.nextToken());
				preChairNo = Integer.parseInt(stMsg.nextToken());
				newDeskNo = Integer.parseInt(stMsg.nextToken());
				newChairNo = Integer.parseInt(stMsg.nextToken());

				if(!desks[newDeskNo].setUser(newChairNo, user)){ 
					//���ܼ������ӣ�����JOIN_TABLE_FAILE��Ϣ   09:��ǰ���Ӻ�:��ǰ���Ӻ�
					sendToMe(PacketMessage.JOIN_TABLE_FAIL +":"+preDeskNo+":"+preChairNo);
					return false;
				}else{
					//�ܼ������ӣ�
					user.setDeskId(newDeskNo);  //�����û�����������Ϣ
					user.setChairId(newChairNo);
					if(preDeskNo != -1){ //֮ǰ�Ѿ���������ɾ��֮ǰ������Ϣ
						desks[preDeskNo].deleteUser(preChairNo);
						userOpponent = desks[preDeskNo].getUserOpponent(preChairNo);
						if(userOpponent != null){ //���֮ǰ�������ж��֣�������ַ����뿪������Ϣ
							sendToUser(PacketMessage.LEAVE_TABLE_OPPO+":"+user.getName(), userOpponent);
						}
						user.setReady(false); //�뿪���ӣ������Ƿ�׼����
					}					
					
					userOpponent = desks[newDeskNo].getUserOpponent(newChairNo);
					if(userOpponent != null){
						//����JOIN_TABLE_SUCCESS��Ϣ   10:��ǰ���Ӻ�:��ǰ���Ӻ�:�Է�ͷ����:�Է��û���  ����Ϊ�򿪷����������ݣ�
						sendToMe(PacketMessage.JOIN_TABLE_SUCCESS+":"+newDeskNo+":"+newChairNo+":"+userOpponent.getImg()+":"+userOpponent.getName());
						//����ַ���JOIN_TABLE_OPPO��Ϣ�� 11:����ͷ�����������û��� ���ö��ָ��¡�����"ͷ�������
						sendToUser(PacketMessage.JOIN_TABLE_OPPO+":"+user.getImg()+":"+user.getName(), userOpponent);
						if(userOpponent.isReady())
							sendToMe(PacketMessage.GAME_READY+":");
					}
					else
						//����JOIN_TABLE_SUCCESS��Ϣ   10:��ǰ���Ӻ�:��ǰ���Ӻ� �����ֲ����ڣ�������ͷ������ƣ�
						sendToMe(PacketMessage.JOIN_TABLE_SUCCESS+":"+newDeskNo+":"+newChairNo);
					return true;
				}
			}
			
			//�����뿪������Ϣ
			public void handleExitGameMessage(String msg){
				
			}
			
			//������׼������Ϣ
			public void handleGameReadyMessage(String msg){
				User userOpponent;
				user.setReady(true);
				userOpponent = desks[user.getDeskId()].getUserOpponent(user.getChairId());
				if(userOpponent != null){ 
					if(userOpponent.isReady()){ //�Է�Ҳ׼���ã�����Ϸ��ʼ
						sendToMe(PacketMessage.GAME_START+":");
						sendToUser(PacketMessage.GAME_START+":", userOpponent);
						desks[user.getDeskId()].setStart(true);
					}else //���򽫱���׼���õ���Ϣ���͸��Է�
						sendToUser(PacketMessage.GAME_READY+":", userOpponent);
				}
				
			}
			
			//������������˵���Ϣ����Ϸ������
			public void handleGameOverMessage(){
				user.setReady(false);
				desks[user.getDeskId()].getUserOpponent(user.getChairId()).setReady(false);
			}
			
			//�����˳�������Ϣ
			 public void handleExitGameMessage(){				 
				 sendToAllUser(PacketMessage.EXIT_GAME+":"+user.getDeskId()+":"+user.getChairId());
				 
				 User userOpponent = desks[user.getDeskId()].getUserOpponent(user.getChairId());
				 if(userOpponent != null){ //��������ж��֣�������ַ����뿪������Ϣ
					sendToUser(PacketMessage.LEAVE_TABLE_OPPO+":"+user.getName(), userOpponent);
				}
				 desks[user.getDeskId()].deleteUser(user.getChairId());
				 user.setDeskId(-1);
				 user.setChairId(-1);
			 }
			
			//��������ת����Ϣ
			public void sendToAllUser(String msg){				
				try{
					Iterator<User> iterator = userList.iterator();
					while(iterator.hasNext()){
						iterator.next().getDos().writeUTF(msg);						
					}
					System.out.println("Ⱥ������Ϣ��"+msg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//���Լ��ظ���Ϣ
			public void sendToMe(String msg){
				try{
					user.getDos().writeUTF(msg);
					System.out.println("��"+user.getName()+"�ظ�����Ϣ��"+msg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//��ĳ�û����͵���Ϣ
			public void sendToUser(String msg, User userOther){
				try{
					userOther.getDos().writeUTF(msg);
					System.out.println("��"+userOther.getName()+"���͵���Ϣ��"+msg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
	 }
}

