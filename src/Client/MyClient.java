package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class MyClient extends Thread{
	private Socket socket = null;//����ʵ�ֿͻ����׽��֣�Ҳ���ԾͽС��׽��֡������׽�������̨������ͨ�ŵĶ˵�
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
	
	//������Ϣ
	public void sendMessage(String msg){
		System.out.println("���͵���Ϣ��"+msg);
		try{
			dos.writeUTF(msg);		//DataOutputStream��ķ�������������޹ط�ʽʹ�� UTF-8 �޸İ���뽫һ���ַ���д������������	
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	//������Ϣ
	public void run(){
		String recvMsg = null;
		String commandMsg = null;
		int pos;
		try{
			while(run){
				//������ȡ����������Ϣ��
				recvMsg = dis.readUTF();
				System.out.println("���յ�����Ϣ��"+recvMsg);
				pos = recvMsg.indexOf(":");//����ָ���ַ����ڴ��ַ����е�һ�γ��ִ���������
				commandMsg = recvMsg.substring(0,pos);//����һ���µ��ַ��������Ǵ��ַ�����һ�����ַ���
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
				}else if(commandMsg.equals(PacketMessage.GAME_READY)){ //�Է�׼���õ���Ϣ
					chessGamePanel.opponentReady();
				}else if(commandMsg.equals(PacketMessage.MOVE_CHESS)){ //�Է�׼���õ���Ϣ
					handleMoveChessMessage(recvMsg.substring(pos+1));
				}else if(commandMsg.equals(PacketMessage.GAME_FAIL)){ //�Է����˵���Ϣ
					chessGamePanel.gameOver("��ϲ�㣬��Ӯ�ˣ�");
				}else if(commandMsg.equals(PacketMessage.GAME_GIVEIN)){ //�Է�׼���õ���Ϣ
					chessGamePanel.gameOver("�������䣬��Ӯ�ˣ�");
				}else if(commandMsg.equals(PacketMessage.EXIT_GAME)){ //�Է�׼���õ���Ϣ
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
	
	//�����¼��Ϣ���ڷ�������Ϣ������ʾ��Ϣ
	public void handleLogoMessage(String msg){
		String name;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		//Ϊָ�����ַ�������һ��	StringTokenizer���á������ָ���Ϣ����nextToken����ȥ��ȡ			
		name = stMsg.nextToken();
		System.out.println(name+"��¼");
		hallForm.addServerMessage(name+"��¼");
	}
	
	//�������������Ϣ��������������
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
	
	//�����ܼ���������Ϣ���ָ�preDeskNo��preChairNo
	public void handleJoinTableFailMessage(String msg){
		int preDeskNo, preChairNo;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		preDeskNo = Integer.parseInt(stMsg.nextToken());
		preChairNo = Integer.parseInt(stMsg.nextToken());
		hallForm.canNotSeat(preDeskNo, preChairNo);
	}
	
	//����ɹ�����������Ϣ������ע�����Ƿ��Ѿ���������¼�������Ӻţ�
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
	
	//����������ӵĶ�����Ϣ
	public void handleJoinTableOppoMessage(String msg){
		String oppoImg, oppoName;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		oppoImg = stMsg.nextToken();
		oppoName = stMsg.nextToken();
		chessGamePanel.updateOpponent(oppoImg, oppoName);
	}
	
	//��������뿪������Ϣ
	public void handleLeaveTableOppoMessage(String msg){
		String oppoName;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		oppoName = stMsg.nextToken();
		chessGamePanel.updateLeaveOpponent(oppoName); 
	}
	
	//������һ������Ϣ
	public void handleMoveChessMessage(String msg){
		int oldx, oldy, newx, newy;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		//��ȡ������λ�ã����ֺ��Լ���λ�����öԳƣ�
		oldx = 9-Integer.parseInt(stMsg.nextToken());
		oldy = 8-Integer.parseInt(stMsg.nextToken());
		newx = 9-Integer.parseInt(stMsg.nextToken());
		newy = 8-Integer.parseInt(stMsg.nextToken());
		chessGamePanel.oppoMoveChess(oldx, oldy, newx, newy);
	}
	
	//�����뿪������Ϣ
	public void handleExitGameMessage(String msg){
		int deskNo, chairNo;
		StringTokenizer stMsg = new StringTokenizer(msg,":");
		deskNo = Integer.parseInt(stMsg.nextToken());
		chairNo = Integer.parseInt(stMsg.nextToken());
		hallForm.exitChessRoom(deskNo, chairNo);
	}

}
