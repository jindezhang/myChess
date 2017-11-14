package Client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ChessGamePanel extends JPanel implements ActionListener{
	private JTabbedPane tabbedChat = new JTabbedPane();  //"����"Tab
	private JTabbedPane tabbedMe = new JTabbedPane();  //"�Լ�"Tab
	private JTabbedPane tabbedOpponent = new JTabbedPane();  //"����"Tab
	private JTabbedPane tabbedUserList = new JTabbedPane();  //"�û��б�"Tab
	private JPanel panelChat = new JPanel(); //��tabbedPane��Ӧ��panel
	private JPanel panelMe = new JPanel();
	private JPanel panelOpponent = new JPanel();
	private JPanel panelUserList = new JPanel();
	private QiPanPanel qiPanPanel;
	
	private JButton buttonExit = new JButton("�˳�");
	private JButton buttonStart = new JButton("��ʼ");
	private JButton buttonDraw = new JButton("���");
//	private JButton buttonBack = new JButton("����");
	private JButton buttonGiveUp = new JButton("����");
	private JButton buttonSend = new JButton("����");
	
	private JLabel labelMe = null; //������Ϣ
	private JLabel labelOpponent = null;  //�Է���Ϣ
	private JLabel labelMeStart = null;  //������ʼ/����
	private JLabel labelOppoStart = null;  //�Է���ʼ/����
	private JLabel labelMeThisTime = new JLabel("����ʣ��ʱ�䣺");
	private JLabel labelMeTotleTime = new JLabel("�ܹ�ʱ�䣺");
	private JLabel labelOppoThisTime = new JLabel("����ʣ��ʱ�䣺");
	private JLabel labelOppoTotleTime = new JLabel("�ܹ�ʱ�䣺");
	private JLabel labelRoomTitle = new JLabel(); //�Ϸ���<<<  ������Ϸ---�����  >>>
	
	private JTextArea txAreaChat = new JTextArea(); //��������
	private JTextField txFieldChat = new JTextField(10); //��������
	private List userList = new List(15); //�û��б�
	
	private JSplitPane splitMain = null;//JSplitPane���ڷָ�������ֻ��������Component��
	private JSplitPane splitRight = null;
	private JSplitPane splitLeft = null;
	private JSplitPane splitPlayer = null;

	private JPanel pCenter = new JPanel();
	private JPanel pCenterTop = new JPanel();
	private JPanel pQiPan = new JPanel();
	private JPanel pFourButton = new JPanel();
	private JPanel pMe = new JPanel();
	private JPanel pOpponent = new JPanel();
	private JPanel pChatMsg = new JPanel();
	private JPanel pChatSend = new JPanel();
	
	private boolean isStart = false;  //��Ϸ�Ƿ�ʼ ��˫����ʼ��
	private boolean isMeReady = false;  //�����Ƿ�׼���ã�������ʼ�����Է�δ��ʼ��
	private boolean isOppoReady = false;  //**�Է��Ƿ�׼���ã��Է���ʼ��������δ��ʼ��
	private boolean isMeGoing = false;  //�Ƿ񱾷�����
	
	private User userMe;
	private User userOpponent;//**,
	private int deskNo=0;
	private String oppoImg;
	private String oppoName = "null";
	private MyClient client;
	private Timer t;
	private FinishTimer finishTimer;	
	private DataInputStream dis = null;
	
	private final int RUNTIME = 60;
	private int finishTime = RUNTIME;
	
    public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
			
	public int getDeskNo() {
		return deskNo;
	}
	public void setDeskNo(int deskNo) {
		this.deskNo = deskNo;
		labelRoomTitle.setText("<<<  ������Ϸ---���� "+deskNo+"  >>>");
	}
	
	public ChessGamePanel(){
		labelMe = new JLabel("aaa",new ImageIcon("./res/face/1-1.gif"),SwingConstants.CENTER);
		labelOpponent = new JLabel("bbb",new ImageIcon("./res/face/2-1.gif"),SwingConstants.CENTER);
		//labelMeStart = new JLabel(new ImageIcon("./res/img/r5.gif"));
		labelMeStart = new JLabel();
		labelMeStart.setHorizontalAlignment(SwingConstants.CENTER);
		labelOppoStart = new JLabel();
		labelOppoStart.setHorizontalAlignment(SwingConstants.CENTER);
		userList.add("aaa");
		userList.add("bbb");
		isStart = false;
		buttonExit.addActionListener(this);
		buttonStart.addActionListener(this);
		buttonDraw.addActionListener(this);
//		buttonBack.addActionListener(this);
		buttonGiveUp.addActionListener(this);
		buttonSend.addActionListener(this);
		CreateUI();
	}
	
	public ChessGamePanel(User userMe, MyClient client){
		this.userMe = userMe;
		this.client = client;
		labelMe = new JLabel(userMe.getName(), new ImageIcon("./res/face/"+userMe.getImg()),SwingConstants.CENTER);
		labelMeStart = new JLabel();
		labelMeStart.setHorizontalAlignment(SwingConstants.CENTER);
		labelOpponent = new JLabel(new ImageIcon("./res/img/noone.gif"),SwingConstants.CENTER);
		labelOppoStart = new JLabel();
		labelOppoStart.setHorizontalAlignment(SwingConstants.CENTER);
		userList.add(userMe.getName());
		isStart = false;
		buttonExit.addActionListener(this);
		buttonStart.addActionListener(this);
		buttonDraw.addActionListener(this);
//		buttonBack.addActionListener(this);
		buttonGiveUp.addActionListener(this);
		buttonSend.addActionListener(this);		
		CreateUI();
	}
	
	public ChessGamePanel(User userMe, String oppoImg, String oppoName,  MyClient client){
		this.userMe = userMe;
		this.oppoImg = oppoImg;
		this.oppoName = oppoName;
		this.client = client;
		labelMe = new JLabel(userMe.getName(), new ImageIcon("./res/face/"+userMe.getImg()),SwingConstants.CENTER);
		labelOpponent = new JLabel(oppoName,new ImageIcon("./res/face/"+oppoImg),SwingConstants.CENTER);
		labelMeStart = new JLabel();
		labelMeStart.setHorizontalAlignment(SwingConstants.CENTER);
		labelOppoStart = new JLabel();
		labelOppoStart.setHorizontalAlignment(SwingConstants.CENTER);
		userList.add(userMe.getName());
		userList.add(oppoName);
		isStart = false;
		buttonExit.addActionListener(this);
		buttonStart.addActionListener(this);
		buttonDraw.addActionListener(this);
//		buttonBack.addActionListener(this);
		buttonGiveUp.addActionListener(this);
		buttonSend.addActionListener(this);		
		CreateUI();
	}
	
	public void setUserOpponent(User userOpponent){
		labelOpponent = new JLabel(userOpponent.getName(),new ImageIcon(userOpponent.getImg()),SwingConstants.CENTER);
		labelOppoStart = new JLabel(new ImageIcon("./res/img/r5.gif"));
		userList.add(userOpponent.getName());
	}
	
	public void CreateUI(){
		//�м��Ϸ�
		pCenterTop.setLayout(new BorderLayout());
		//labelRoomTitle.setText("<<< ������Ϸ---����  "+userMe.getDeskId()+" >>>");
		pCenterTop.add(labelRoomTitle, BorderLayout.CENTER);
		pCenterTop.add(buttonExit, BorderLayout.EAST);
		
		//����
		pQiPan.setLayout(new GridLayout(1,1));
		qiPanPanel = new QiPanPanel(); //�������ڱ���Ĺ��췽������
		client.setQiPanPanel(qiPanPanel);
		pQiPan.add(qiPanPanel);
		
		//�м��·��ĸ���ť
		pFourButton.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		pFourButton.setBackground(new Color(81, 113, 158));
		pFourButton.add(buttonStart);
		pFourButton.add(buttonDraw);
//		pFourButton.add(buttonBack);
		pFourButton.add(buttonGiveUp);
				
		//�м�
		pCenter.setLayout(new BorderLayout());
		pCenter.add(pCenterTop, BorderLayout.NORTH);
		pCenter.add(pQiPan, BorderLayout.CENTER);
		pCenter.add(pFourButton, BorderLayout.SOUTH);
		
		//�Լ�
		pMe.setLayout(new GridLayout(4,1));
		pMe.add(labelMe);
		pMe.add(labelMeStart);
		pMe.add(labelMeThisTime);
		//pMe.add(labelMeTotleTime);
		
		//����
		pOpponent.setLayout(new GridLayout(4,1));
		pOpponent.add(labelOpponent);
		pOpponent.add(labelOppoStart);
		pOpponent.add(labelOppoThisTime);
		//pOpponent.add(labelOppoTotleTime);
		
		//��������ͷ���
		pChatSend.setLayout(new BorderLayout());
		pChatSend.add(txFieldChat, BorderLayout.CENTER);
		pChatSend.add(buttonSend, BorderLayout.EAST);
		
		//������Ϣ
		pChatMsg.setLayout(new BorderLayout());
		pChatMsg.add(txAreaChat, BorderLayout.CENTER);
		pChatMsg.add(pChatSend, BorderLayout.SOUTH);
		
		
		tabbedMe.addTab("�Լ�", pMe);
		panelMe.setLayout(new BorderLayout());
		panelMe.add(tabbedMe, BorderLayout.CENTER);
		panelMe.setBackground(Color.black);
		
		tabbedOpponent.addTab("����", pOpponent);
		panelOpponent.setLayout(new BorderLayout());
		panelOpponent.add(tabbedOpponent, BorderLayout.CENTER);
		panelOpponent.setBackground(Color.black);
		
		tabbedChat.addTab("����", pChatMsg);
		panelChat.setLayout(new BorderLayout());
		panelChat.add(tabbedChat, BorderLayout.CENTER);
		panelChat.setBackground(Color.black);
		
		tabbedUserList.addTab("�û��б�", userList);
		panelUserList.setLayout(new BorderLayout());
		panelUserList.add(tabbedUserList, BorderLayout.CENTER);
		panelUserList.setBackground(Color.black);
		
		splitPlayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panelOpponent, panelMe);
		splitPlayer.setDividerLocation(300);
		splitPlayer.setDividerSize(5);
		
		splitLeft = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitPlayer, pCenter);
		splitLeft.setDividerLocation(180);
		splitLeft.setDividerSize(5);
		
		splitRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panelUserList, panelChat);
		splitRight.setDividerLocation(300);
		splitRight.setDividerSize(5);

		splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitLeft, splitRight);
		splitMain.setDividerLocation(637);
		splitMain.setDividerSize(5);
		
		this.setLayout(new BorderLayout());
		this.add(splitMain, BorderLayout.CENTER);
	}
	

	public static void main(String args[]){
		JFrame j = new JFrame("������Ϸ");
		j.setBounds(0,0,850,720);
		j.setLayout(new BorderLayout());
		JTabbedPane tabbedChessGame = new JTabbedPane();
		tabbedChessGame.addTab("������Ϸ",new ChessGamePanel());
		j.add(tabbedChessGame, BorderLayout.CENTER);
		//j.pack();
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("��ʼ")){
			isMeReady = true;
			labelMeStart.setIcon(new ImageIcon("./res/img/start.gif"));
			labelMeStart.setHorizontalAlignment(SwingConstants.CENTER);
			client.sendMessage(PacketMessage.GAME_READY+":");
			buttonStart.setEnabled(false);
			qiPanPanel.repaint();
		}else if(e.getActionCommand().equals("����")){
    		client.sendMessage(PacketMessage.GAME_GIVEIN+":");
    		gameOver("�ܱ�Ǹ�������ˣ�");
		}
		else if(e.getActionCommand().equals("����")){
			String say=userMe.getName()+"˵:"+txFieldChat.getText()+"\n";
			txAreaChat.append(say);
			client.sendMessage(PacketMessage.CHAT_MESSAGE+":"+say);
			txFieldChat.setText("");
		}
		else if(e.getActionCommand().equals("���")){
			if(isStart){
			client.sendMessage(PacketMessage.GAME_PEACE+":");
		}
		}
		else if(e.getActionCommand().equals("�˳�")){
    		if(isStart){
    			JOptionPane.showMessageDialog(null, "������Ϸ�У������˳�", "������ʾ",
    					JOptionPane.ERROR_MESSAGE);
    			return;
    		}
    		else{ //�˳�����
    			client.sendMessage(PacketMessage.EXIT_GAME+":");
    		}
		}
	}
	//��ʾ���졣
	public void showChat(String s){
		txAreaChat.append(s);
	}
	
	//�������
	public void gamePeace(String reveive){
		StringTokenizer st=new StringTokenizer(reveive, ":");
		String s=st.nextToken();
		if(st.hasMoreTokens()){
			s=st.nextToken();
			if(s.equals("0")){
				JOptionPane.showMessageDialog(null, "���ֲ�ͬ��ƽ�֣����������","��ܰ��ʾ",JOptionPane.ERROR_MESSAGE);
				
			}else if(s.equals("1")){
				gameOver("�˾�ƽ�֣�");
			}
		}
		else{
			int result =JOptionPane.showConfirmDialog(null, "����������ͣ���ͬ����","��ʾ",JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_OPTION){
				client.sendMessage(PacketMessage.GAME_PEACE+":"+1+":");
				gameOver("�˾�ƽ��");
			}
			else if(result==JOptionPane.NO_OPTION){
				client.sendMessage(PacketMessage.GAME_PEACE+":"+0+":");
			}
		}
	}
	//���¼������ӵĶ��ֵ���Ϣ
	public void updateOpponent(String oppoImg, String oppoName){
		labelOpponent.setIcon(new ImageIcon("./res/face/"+oppoImg));
		labelOpponent.setText(oppoName);
		userList.add(oppoName);	
	}
	
	//�����뿪���ӵĶ��ֵ�ͷ�������
	public void updateLeaveOpponent(String oppoName){
		labelOpponent.setIcon(new ImageIcon("./res/img/noone.gif"));
		labelOpponent.setText("");
		labelOppoStart.setIcon(null);
		try{
			userList.remove(oppoName);
		}catch(IllegalArgumentException e){
			//do nothing
		}
	}
	
	//��Ϸ��ʼ
	public void gameStart(){
		isStart = true;
		t = new Timer();
		finishTimer = new FinishTimer();
		t.schedule(finishTimer, 0, 1000);
		//t.scheduleAtFixedRate(finishTimer, 0, 1000);
		labelMeStart.setIcon(null);
		labelOppoStart.setIcon(null);
		if(userMe.getChairId() == 0){ //��������û�������
			isMeGoing = true;
			labelMeStart.setIcon(new ImageIcon("./res/img/r5.gif"));
		}else{
			isMeGoing = false;
			labelOppoStart.setIcon(new ImageIcon("./res/img/r5.gif"));
		}
		//qiPanPanel.repaint();
	}
	
	//�Է��Ѿ�׼����
	public void opponentReady(){
		labelOppoStart.setIcon(new ImageIcon("./res/img/start.gif"));
		labelOppoStart.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	//�յ��Է���һ������Ϣ
	public void oppoMoveChess(int oldx, int oldy, int newx, int newy){
		finishTime = RUNTIME;
		isMeGoing = true;
		labelMeStart.setIcon(new ImageIcon("./res/img/r5.gif"));
		labelOppoStart.setIcon(null);
		labelOppoThisTime.setText("����ʣ��ʱ�䣺");	
		if(oldx != -1){
			qiPanPanel.updatePos(oldx, oldy, newx, newy); //ֻ�ܷ�����������Ϸ������Ҫ������Ӧ��label
		}		
	}
	
	//��Ϸ��������ʾ���Ľ������ʼ��
	public void gameOver(String displayMsg){
		t.cancel();
		//finishTimer.cancel();
		JOptionPane.showMessageDialog(null,displayMsg, "���Ľ��",
				JOptionPane.INFORMATION_MESSAGE);
		//���³�ʼ����ر���
		isStart = false;
		isMeReady = false;
		buttonStart.setEnabled(true);
		isMeGoing = false;
		finishTime = RUNTIME;
		labelMeStart.setIcon(null);
		labelOppoStart.setIcon(null);
		labelMeThisTime.setText("����ʣ��ʱ�䣺");
		labelOppoThisTime.setText("����ʣ��ʱ�䣺");
		qiPanPanel.pos = new int[][]{
				{ 17, 18, 19, 20, 21, 22, 23, 24, 25 },// ��ʼ��λ������pos[10][9]
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 26, 0, 0, 0, 0, 0, 27, 0 },
				{ 28, 0, 29, 0, 30, 0, 31, 0, 32},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 12, 0, 13, 0, 14, 0, 15, 0, 16 },
				{ 0, 10, 0, 0, 0, 0, 0, 11, 0  },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 2, 3, 4, 5, 6, 7, 8, 9  } };
		qiPanPanel.repaint();
	}
	
	//�ڲ��࣬��ʱ����ʵ��ʱ�䵹��
	class FinishTimer extends TimerTask{
		
		public void run(){
			if(finishTime >= 0){				
				if(isMeGoing){
					labelMeThisTime.setText("����ʣ��ʱ�䣺"+finishTime+" ��");
					labelOppoThisTime.setText("����ʣ��ʱ�䣺");	
				}
				else{
					labelOppoThisTime.setText("����ʣ��ʱ�䣺"+finishTime+" ��");
					labelMeThisTime.setText("����ʣ��ʱ�䣺");
				}
				finishTime--;
			}else{
				if(isMeGoing){ //����ʱ�䵽������һ�����Ȩ�޽����Է�					
					client.sendMessage(PacketMessage.MOVE_CHESS+":-1:-1:-1:-1");
	    			finishTime = RUNTIME; //���¼�ʱ
	    			isMeGoing = false;
	    			labelOppoStart.setIcon(new ImageIcon("./res/img/r5.gif"));
	    			labelMeStart.setIcon(null);	
	    			labelMeThisTime.setText("����ʣ��ʱ�䣺");
	    			qiPanPanel.recoverFirstMouseLeftPress();
				}
				
			}
				//this.cancel();
		}
	}

	//�ڲ��࣬������弰��ش���
	 class QiPanPanel extends JPanel implements MouseListener{
			private QiZi[] qizi = new QiZi[33]; 
			
		    private Image qiPanImage = null;
		    private Image selectImage = null;   
		
			private int pos[][] ={
					{ 17, 18, 19, 20, 21, 22, 23, 24, 25 },// ��ʼ��λ������pos[10][9]
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
					{ 0, 26, 0, 0, 0, 0, 0, 27, 0 },
					{ 28, 0, 29, 0, 30, 0, 31, 0, 32},
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 12, 0, 13, 0, 14, 0, 15, 0, 16 },
					{ 0, 10, 0, 0, 0, 0, 0, 11, 0  },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 2, 3, 4, 5, 6, 7, 8, 9  } };
		
			private boolean isFirstMouseLeftPress = false; //true����һ�ΰ���������ѡ�����ӣ��� false�� �ڶ��ΰ������������壩
			private int curSelectX = 0;  //��ǰѡ�е�λ��
			private int curSelectY = 0;
			private int preSelectX = 0;  //��ǰѡ�е�λ��
			private int preSelectY = 0;
			
			public QiPanPanel(){
		    	try{
		    		//��ʼ������ͼ��
		        	int k=0;
		        	for(int i=0;i<10;i++)
		        		for(int j=0;j<9;j++){
		        			if(pos[i][j]!=0){
		        				qizi[k+1] = new QiZi();
		        				//qizi[k].setX(i);
		        				//qizi[k].setY(j);
		        				//qizi[k].setId(k);
		        				if(userMe.getChairId() == 0) //��������û��ߺ���
		        					qizi[k+1].setImage(ImageIO.read(new File("./res/qizi/"+(k+1)+".gif")));
		        				else //�ұ������û��ߺ���
		        					qizi[k+1].setImage(ImageIO.read(new File("./res/qizi/"+((k+16)%32+1)+".gif")));
		        				k++;
		        			}
		        		}
		        	//��ʼ������ͼ��
		        	qiPanImage = ImageIO.read(new File("./res/qizi/xqboard.gif"));
		        	//��ʼ��ѡ���ͼ��
		        	selectImage = ImageIO.read(new File("./res/qizi/select.gif"));
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    	
				//isStart = true;
				isFirstMouseLeftPress = false;
				this.addMouseListener(this);
				this.setBackground(new Color(81, 113, 158));
			}
			
			public void paint(Graphics g){
				super.paint(g);
				//������
			   	g.drawImage(qiPanImage, QiPanPara.QIPAN_X, QiPanPara.QIPAN_Y, this);    	
		    	if(isMeReady){
		    		//������
		        	int k = 0;
		        	for(int i=0;i<10;i++)
		        		for(int j=0;j<9;j++){ 
		        			if(pos[i][j]!=0){
		            			k = pos[i][j];
		        				g.drawImage(qizi[k].getImage(), QiPanPara.QIZI_X+j*QiPanPara.QIZI_Y_DISTANCE, 
		        						QiPanPara.QIZI_Y+i*QiPanPara.QIZI_X_DISTANCE, QiPanPara.QIZI_WIDTH, QiPanPara.QIZI_HEIGHT, this);
		        			}
		        		}
		    	}else
		    		return;
		    	if(isFirstMouseLeftPress){
		    		//��ѡ���
		        	g.drawImage(selectImage, QiPanPara.QIZI_X+curSelectY*QiPanPara.QIZI_Y_DISTANCE, 
		    				QiPanPara.QIZI_Y+curSelectX*QiPanPara.QIZI_X_DISTANCE, QiPanPara.QIZI_WIDTH, QiPanPara.QIZI_HEIGHT, this);

		    	}
			}
			
			public void mouseClicked(MouseEvent e) {

			}

		    public void mouseEntered(MouseEvent e){
		    	
		    }
		    
		    public void mouseExited(MouseEvent e) {
		    	
		    }

		    public void mousePressed(MouseEvent e) {
		    	if(!isStart) //��Ϸû��ʼ
		    		return; 
		    	if(!isMeGoing) //�����ֵ��Լ�����
		    		return;
				curSelectY = (e.getX() - QiPanPara.QIZI_X )/QiPanPara.QIZI_X_DISTANCE;
				curSelectX = (e.getY() - QiPanPara.QIZI_Y )/QiPanPara.QIZI_Y_DISTANCE;
				if(curSelectX > 9 || curSelectX < 0 ||curSelectY > 8 || curSelectY < 0)  //�������̷�Χ
					return;
				if((curSelectX == preSelectX) && (curSelectY == preSelectY) && isFirstMouseLeftPress){ //��ԭ��ѡ�е�λ�ã���ʾȡ��ѡ��
					isFirstMouseLeftPress = false;
					repaint();
					return;
				}
				
				System.out.println("֮ǰѡ�е�λ���ǣ�" + preSelectX + "��" +preSelectY);
				System.out.println("֮ǰѡ�е������ǣ�" + pos[preSelectX][preSelectY]);
				System.out.println("��ǰѡ�е�λ���ǣ�" + curSelectX + "��" +curSelectY);
				
		    	if(!isFirstMouseLeftPress){  //��һ�ΰ���������ѡ�����ӣ�    	
		    		if(pos[curSelectX][curSelectY] > 0 && pos[curSelectX][curSelectY] < 17){ //�б������Ӳ���ѡ��
			    		isFirstMouseLeftPress = true;
			    		preSelectX = curSelectX;
			    		preSelectY = curSelectY;
			    		repaint();
		    		}
		    	}else{  //�ڶ��ΰ������������壩
		    		
		    		//QiPanController control = new QiPanController();
		    		if(qiZiRule(pos[preSelectX][preSelectY],preSelectX, preSelectY, curSelectX, curSelectY)){
		    			/*if(jiangJunRule()){
		    				int result = JOptionPane.showConfirmDialog(null,"�߸ò��ᱻ������������",
		    						"��ȷ��",JOptionPane.YES_NO_OPTION);
		    				if(result == JOptionPane.NO_OPTION){
		    					return;
		    				}
		    				System.out.println("�����ⲽ�ᱻ����������");
		    			}*/
		    			//if(isMeGoing){ //�п����û�һֱ��ȷ���Ի��򣬶�����ʱ���Ѿ����ˣ�Ȩ���ѽ����Է�
			    			isFirstMouseLeftPress = false;
			    			updatePos(preSelectX, preSelectY, curSelectX, curSelectY);
			    			//repaint();
			    			//����MOVE_CHESS��Ϣ
			    			client.sendMessage(PacketMessage.MOVE_CHESS+":"+preSelectX+":"+preSelectY+":"+curSelectX+":"+curSelectY);
			    			//finishTimer.cancel();
			    			finishTime = RUNTIME; //���¼�ʱ
			    			isMeGoing = false;
			    			labelOppoStart.setIcon(new ImageIcon("./res/img/r5.gif"));
			    			labelMeStart.setIcon(null);	
			    			labelMeThisTime.setText("����ʣ��ʱ�䣺");
		    			//}else
		    			//	recoverFirstMouseLeftPress();
		    		}
		    		
		    	}  	
		    }
		    

		    public void mouseReleased(MouseEvent e){
		    	
		    }   
    
		    //��������λ��
		    public void updatePos(int oldx, int oldy, int newx, int newy){
		    	if(oldx>=0 && oldx<=9 && oldy>=0 && oldy<=8 && newx>=0 && newx<=9 && newy>=0 && newy<=8){
			    	int oldValue = pos[oldx][oldy];
			    	int newValue = pos[newx][newy];
			    	pos[oldx][oldy] = 0;
			    	pos[newx][newy] = oldValue;
			    	repaint();
			    	/*if(jiangJunRule())
			    		System.out.println("����������");*/
			    	if(newValue == 5){ //�����������ˣ��������˵���Ϣ
			    		client.sendMessage(PacketMessage.GAME_FAIL+":");
			    		gameOver("�ܱ�Ǹ�������ˣ�");
			    	}			    	
		    	}		    	
		    }
		    
		    //�ָ�������ĳ�ʼ״̬�����ڼ�ʱ���Ĵ�������ʾѡ���
		    public void recoverFirstMouseLeftPress(){
		    	isFirstMouseLeftPress = false;
		    	preSelectX = 0;
		    	preSelectY = 0;
		    	repaint();		    	
		    }
			
				//�ж���һ�����Ƿ���Ϲ���
			public boolean qiZiRule(int qiZiNo, int oldx, int oldy, int newx,
					int newy) {
				boolean canGo = false;
				if (qiZiNo == 1 || qiZiNo == 9) // ��
					canGo = cheRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 2 || qiZiNo == 8) // ��
					canGo = maRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 3 || qiZiNo == 7) // ��
					canGo = xiangRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 4 || qiZiNo == 6) // ʿ
					canGo = shiRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 5) // ��
					canGo = jiangRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 10 || qiZiNo == 11) // ��
					canGo = paoRule(oldx, oldy, newx, newy);
				else if (qiZiNo >= 12 || qiZiNo <= 16) // ��
					canGo = bingRule(oldx, oldy, newx, newy);
				return canGo;
			}
	
			// ���Ĺ���
			private boolean cheRule(int oldx, int oldy, int newx, int newy) {
				if (oldx != newx && oldy != newy) { // ���Ǻ���������߶�
					return false;
				} else if (oldx == newx) { // �����߶����ж��м��Ƿ�����
					if (newy > oldy)
						for (int y = oldy + 1; y < newy; y++) {
							if (pos[oldx][y] != 0)
								return false;
						}
					else
						for (int y = newy + 1; y < oldy; y++) {
							if (pos[oldx][y] != 0)
								return false;
						}
				} else { // �����߶����ж��м��Ƿ�����
					if (oldx < newx)
						for (int x = oldx + 1; x < newx; x++) {
							if (pos[x][oldy] != 0)
								return false;
						}
					else
						for (int x = newx + 1; x < oldx; x++) {
							if (pos[x][oldy] != 0)
								return false;
						}
				}
				if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
					return true;
				else
					return false;
			}
	
			// ��Ĺ���
			private boolean maRule(int oldx, int oldy, int newx, int newy) {
				if (((newx == oldx + 1) || (newx == oldx - 1)) && newy == oldy + 2
						&& pos[oldx][oldy + 1] == 0) { // �ҷ�����������λ�ã��Ҳ�������
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
						return true;
				}
				if (((newx == oldx + 1) || (newx == oldx - 1)) && newy == oldy - 2
						&& pos[oldx][oldy - 1] == 0) { // �󷽵���������λ�ã��Ҳ�������
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
						return true;
				}
				if (((newy == oldy + 1) || (newy == oldy - 1)) && newx == oldx + 2
						&& pos[oldx + 1][oldy] == 0) { // �Ϸ�����������λ�ã��Ҳ�������
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
						return true;
				}
				if (((newy == oldy + 1) || (newy == oldy - 1)) && newx == oldx - 2
						&& pos[oldx - 1][oldy] == 0) { // �·�����������λ�ã��Ҳ�������
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
						return true;
				}
				return false;
			}
	
			// ��Ĺ���
			private boolean xiangRule(int oldx, int oldy, int newx, int newy) {
				if (newx >= 5) { // ���ܹ���
					if ((newx == oldx + 2) && (newy == oldy + 2)
							&& (pos[oldx + 1][oldy + 1] == 0)) { // ���Ϸ�������λ�ã��Ҳ�������
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
					}
					if ((newx == oldx + 2) && (newy == oldy - 2)
							&& (pos[oldx + 1][oldy - 1] == 0)) { // ���·�������λ�ã��Ҳ�������
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
					}
					if ((newx == oldx - 2) && (newy == oldy + 2)
							&& (pos[oldx - 1][oldy + 1] == 0)) { // ���·�������λ�ã��Ҳ�������
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
					}
					if ((newx == oldx - 2) && (newy == oldy - 2)
							&& (pos[oldx - 1][oldy - 1] == 0)) { // ���·�������λ�ã��Ҳ�������
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
					}
				}
				return false;
			}
	
			// ʿ�Ĺ���
			private boolean shiRule(int oldx, int oldy, int newx, int newy) {
				if (newx > 6 && newx < 10 && newy > 2 && newy < 6) { // ֻ���ڡ��Ź��������ߡ���x=7,8,9 �� y=3,4,5)
					if ((newx == oldx + 1 || newx == oldx - 1)
							&& (newy == oldy + 1 || newy == oldy - 1)) { // ����б��ǰ��������ƶ�һ��λ�ã�x��y�������б仯��
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
					}
				}
				return false;
			}
	
			// ���Ĺ���
			private boolean jiangRule(int oldx, int oldy, int newx, int newy) {
				if (newx > 6 && newx < 10 && newy > 2 && newy < 6) { // ֻ���ڡ��Ź��������ߡ���x=7,8,9��y=3,4,5)
					if ((newx == oldx) && (newy == oldy + 1 || newy == oldy - 1)
							|| (newy == oldy)
							&& (newx == oldx + 1 || newx == oldx - 1)) { // ����ǰ��������ƶ�һ��λ�ã�x��yֻ�ܱ仯����һ����
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
					}
				}
				return false;
			}
	
			// �ڵĹ���
			private boolean paoRule(int oldx, int oldy, int newx, int newy) {
				int count = 0;
				if (oldx != newx && oldy != newy) { // ���Ǻ���������߶�
					return false;
				} else if (oldx == newx) { // �����߶�
					if (newy > oldy) {
						for (int y = oldy + 1; y < newy; y++) { // �����м�������
							if (pos[oldx][y] != 0) {
								count++;
							}
						}
					} else {
						for (int y = newy + 1; y < oldy; y++) { // �����м�������
							if (pos[oldx][y] != 0) {
								count++;
							}
						}
					}
				} else { // �����߶�
					if (oldx < newx) {
						for (int x = oldx + 1; x < newx; x++) { // �����м�������
							if (pos[x][oldy] != 0) {
								count++;
							}
						}
					} else {
						for (int x = newx + 1; x < oldx; x++) { // �����м�������
							if (pos[x][oldy] != 0) {
								count++;
							}
						}
					}
				}
				if (count == 0 && pos[newx][newy] == 0) // �м�û�����ӣ���������һ���յ�λ��
					return true;
				else if (count == 1 && pos[newx][newy] > 17) // �м��һ�����ӱ����ǳ���
					return true;
				else
					return false;
			}
	
			// ���Ĺ���
			private boolean bingRule(int oldx, int oldy, int newx, int newy) {
				if (oldx < 5) { // �����Ӻ������ǰ��������һ��
					if ((newx == oldx - 1 && newy == oldy)
							|| (newx == oldx && (newy == oldy + 1 || newy == oldy - 1)))
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
				} else { // ��û���ӣ�ֻ����ǰ��һ��
					if (newx == oldx - 1 && newy == oldy)
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // ��һ���յ�λ�û�Եз�������
							return true;
				}
				return false;
			}
	
			// ��ǰ�Ƿ񱻽�������ûд�ã�
			private boolean jiangJunRule() {
				// �ұ�������λ��
				int jiangX = 0, jiangY = 0;
				for (int x = 0; x < 10; x++)
					for (int y = 0; y < 9; y++)
						if (pos[x][y] == 5) {
							jiangX = x;
							jiangY = y;
							break;
						}
	
				// �Ƿ񱻶Է������ںͱ�����
				int count = 0;
				for (int x = jiangX - 1; x >= 0; x--) { // �����Ϸ�
					if (pos[x][jiangY] == 0)
						continue;
					else if ((pos[x][jiangY] == 17 || pos[x][jiangY] == 25)
							&& count == 0) // ���Է�������(�м䲻�ܸ���count==0��
						return true;
					else if ((pos[x][jiangY] == 26 || pos[x][jiangY] == 27)
							&& count == 1) // ���Է��ڽ������м�����һ��count == 1)
						return true;
					else{
						if((x == jiangX-1) && (pos[x][jiangY] >= 28 && pos[x][jiangY] <= 32)) //���Է�������
							return true;
						count++;
					}
				}
				count = 0;
				for (int x = jiangX + 1; x <= 9; x++) { // �����·�
					if (pos[x][jiangY] == 0)
						continue;
					else if ((pos[x][jiangY] == 17 || pos[x][jiangY] == 25)
							&& count == 0) // ���Է�������(�м䲻�ܸ���count==0��
						return true;
					else if ((pos[x][jiangY] == 26 || pos[x][jiangY] == 27)
							&& count == 1) // ���Է��ڽ������м�����һ��count == 1)
						return true;
					else{
						if((x == jiangX+1) && (pos[x][jiangY] >= 28 && pos[x][jiangY] <= 32)) //���Է�������
							return true;
						count++;
					}
				}
				count = 0;
				for (int y = jiangY - 1; y >= 0; y--) { // ������
					if (pos[jiangX][y] == 0)
						continue;
					else if ((pos[jiangX][y] == 17 || pos[jiangX][y] == 25)
							&& count == 0) // ���Է�������(�м䲻�ܸ���count==0��
						return true;
					else if ((pos[jiangX][y] == 26 || pos[jiangX][y] == 27)
							&& count == 1) // ���Է��ڽ������м�����һ��count == 1)
						return true;
					else{
						if((y == jiangY-1) && (pos[jiangX][y] >= 28 && pos[jiangX][y] <= 32)) //���Է�������
							return true;
						count++;
					}
				}
				count = 0;
				for (int y = jiangY + 1; y <= 8; y++) { // �����ҷ�
					if (pos[jiangX][y] == 0)
						continue;
					else if ((pos[jiangX][y] == 17 || pos[jiangX][y] == 25)
							&& count == 0) // ���Է�������(�м䲻�ܸ���count==0��
						return true;
					else if ((pos[jiangX][y] == 26 || pos[jiangX][y] == 27)
							&& count == 1) // ���Է��ڽ������м�����һ��count == 1)
						return true;
					else{
						if((y == jiangY+1) && (pos[jiangX][y] >= 28 && pos[jiangX][y] <= 32)) //���Է�������
							return true;
						count++;
					}
				}
	
				// �Ƿ񱻶Է�����
				if (pos[jiangX - 2][jiangY - 1] == 18
						|| pos[jiangX - 2][jiangY - 1] == 24
						|| pos[jiangX - 1][jiangY - 2] == 18
						|| pos[jiangX - 1][jiangY - 2] == 24) { // ���Ϸ�120�Ⱥ�160�����λ��
					if (pos[jiangX - 1][jiangY - 1] == 0) // û��������
						return true;
				}
				if (pos[jiangX - 2][jiangY + 1] == 18
						|| pos[jiangX - 2][jiangY + 1] == 24
						|| pos[jiangX - 1][jiangY + 2] == 18
						|| pos[jiangX - 1][jiangY + 2] == 24) { // ���Ϸ�60�Ⱥ�30���������λ��
					if (pos[jiangX - 1][jiangY + 1] == 0) // û��������
						return true;
				}
	
				// ���������ĸ�λ��Ҫ����Խ���ж�
				if (jiangX <= 8) {
					if (pos[jiangX + 1][jiangY + 2] == 18
							|| pos[jiangX + 1][jiangY + 2] == 24) // ���·�-30�����λ��
						if (pos[jiangX + 1][jiangY + 1] == 0) // û��������
							return true;
					if (pos[jiangX + 1][jiangY - 2] == 18
							|| pos[jiangX + 1][jiangY - 2] == 24) // ���·�210�����λ��
						if (pos[jiangX + 1][jiangY - 1] == 0) // û��������
							return true;
				} else if (jiangX <= 7) {
					if (pos[jiangX + 2][jiangY + 1] == 18
							|| pos[jiangX + 2][jiangY + 1] == 24) // ���·�-60�����λ��
						if (pos[jiangX + 1][jiangY + 1] == 0) // û��������
							return true;
					if (pos[jiangX + 2][jiangY - 1] == 18
							|| pos[jiangX + 2][jiangY - 1] == 24) // ���·�240�����λ��
						if (pos[jiangX + 1][jiangY - 1] == 0) // û��������
							return true;
				}
				return false; // û�б�����
	
			}
	 }
}
