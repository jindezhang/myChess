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
	private JTabbedPane tabbedChat = new JTabbedPane();  //"聊天"Tab
	private JTabbedPane tabbedMe = new JTabbedPane();  //"自己"Tab
	private JTabbedPane tabbedOpponent = new JTabbedPane();  //"对手"Tab
	private JTabbedPane tabbedUserList = new JTabbedPane();  //"用户列表"Tab
	private JPanel panelChat = new JPanel(); //与tabbedPane对应的panel
	private JPanel panelMe = new JPanel();
	private JPanel panelOpponent = new JPanel();
	private JPanel panelUserList = new JPanel();
	private QiPanPanel qiPanPanel;
	
	private JButton buttonExit = new JButton("退出");
	private JButton buttonStart = new JButton("开始");
	private JButton buttonDraw = new JButton("求和");
//	private JButton buttonBack = new JButton("悔棋");
	private JButton buttonGiveUp = new JButton("认输");
	private JButton buttonSend = new JButton("发送");
	
	private JLabel labelMe = null; //本方信息
	private JLabel labelOpponent = null;  //对方信息
	private JLabel labelMeStart = null;  //本方开始/走棋
	private JLabel labelOppoStart = null;  //对方开始/走棋
	private JLabel labelMeThisTime = new JLabel("本步剩余时间：");
	private JLabel labelMeTotleTime = new JLabel("总共时间：");
	private JLabel labelOppoThisTime = new JLabel("本步剩余时间：");
	private JLabel labelOppoTotleTime = new JLabel("总共时间：");
	private JLabel labelRoomTitle = new JLabel(); //上方的<<<  象棋游戏---房间号  >>>
	
	private JTextArea txAreaChat = new JTextArea(); //聊天区域
	private JTextField txFieldChat = new JTextField(10); //聊天输入
	private List userList = new List(15); //用户列表
	
	private JSplitPane splitMain = null;//JSplitPane用于分隔两个（只能两个）Component。
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
	
	private boolean isStart = false;  //游戏是否开始 （双方开始）
	private boolean isMeReady = false;  //本方是否准备好（本方开始，但对方未开始）
	private boolean isOppoReady = false;  //**对方是否准备好（对方开始，但本方未开始）
	private boolean isMeGoing = false;  //是否本方走棋
	
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
		labelRoomTitle.setText("<<<  象棋游戏---房间 "+deskNo+"  >>>");
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
		//中间上方
		pCenterTop.setLayout(new BorderLayout());
		//labelRoomTitle.setText("<<< 象棋游戏---房间  "+userMe.getDeskId()+" >>>");
		pCenterTop.add(labelRoomTitle, BorderLayout.CENTER);
		pCenterTop.add(buttonExit, BorderLayout.EAST);
		
		//棋盘
		pQiPan.setLayout(new GridLayout(1,1));
		qiPanPanel = new QiPanPanel(); //不能先于本类的构造方法构造
		client.setQiPanPanel(qiPanPanel);
		pQiPan.add(qiPanPanel);
		
		//中间下方四个按钮
		pFourButton.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		pFourButton.setBackground(new Color(81, 113, 158));
		pFourButton.add(buttonStart);
		pFourButton.add(buttonDraw);
//		pFourButton.add(buttonBack);
		pFourButton.add(buttonGiveUp);
				
		//中间
		pCenter.setLayout(new BorderLayout());
		pCenter.add(pCenterTop, BorderLayout.NORTH);
		pCenter.add(pQiPan, BorderLayout.CENTER);
		pCenter.add(pFourButton, BorderLayout.SOUTH);
		
		//自己
		pMe.setLayout(new GridLayout(4,1));
		pMe.add(labelMe);
		pMe.add(labelMeStart);
		pMe.add(labelMeThisTime);
		//pMe.add(labelMeTotleTime);
		
		//对手
		pOpponent.setLayout(new GridLayout(4,1));
		pOpponent.add(labelOpponent);
		pOpponent.add(labelOppoStart);
		pOpponent.add(labelOppoThisTime);
		//pOpponent.add(labelOppoTotleTime);
		
		//聊天输入和发送
		pChatSend.setLayout(new BorderLayout());
		pChatSend.add(txFieldChat, BorderLayout.CENTER);
		pChatSend.add(buttonSend, BorderLayout.EAST);
		
		//聊天信息
		pChatMsg.setLayout(new BorderLayout());
		pChatMsg.add(txAreaChat, BorderLayout.CENTER);
		pChatMsg.add(pChatSend, BorderLayout.SOUTH);
		
		
		tabbedMe.addTab("自己", pMe);
		panelMe.setLayout(new BorderLayout());
		panelMe.add(tabbedMe, BorderLayout.CENTER);
		panelMe.setBackground(Color.black);
		
		tabbedOpponent.addTab("对手", pOpponent);
		panelOpponent.setLayout(new BorderLayout());
		panelOpponent.add(tabbedOpponent, BorderLayout.CENTER);
		panelOpponent.setBackground(Color.black);
		
		tabbedChat.addTab("聊天", pChatMsg);
		panelChat.setLayout(new BorderLayout());
		panelChat.add(tabbedChat, BorderLayout.CENTER);
		panelChat.setBackground(Color.black);
		
		tabbedUserList.addTab("用户列表", userList);
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
		JFrame j = new JFrame("象棋游戏");
		j.setBounds(0,0,850,720);
		j.setLayout(new BorderLayout());
		JTabbedPane tabbedChessGame = new JTabbedPane();
		tabbedChessGame.addTab("象棋游戏",new ChessGamePanel());
		j.add(tabbedChessGame, BorderLayout.CENTER);
		//j.pack();
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("开始")){
			isMeReady = true;
			labelMeStart.setIcon(new ImageIcon("./res/img/start.gif"));
			labelMeStart.setHorizontalAlignment(SwingConstants.CENTER);
			client.sendMessage(PacketMessage.GAME_READY+":");
			buttonStart.setEnabled(false);
			qiPanPanel.repaint();
		}else if(e.getActionCommand().equals("认输")){
    		client.sendMessage(PacketMessage.GAME_GIVEIN+":");
    		gameOver("很抱歉，你输了！");
		}
		else if(e.getActionCommand().equals("发送")){
			String say=userMe.getName()+"说:"+txFieldChat.getText()+"\n";
			txAreaChat.append(say);
			client.sendMessage(PacketMessage.CHAT_MESSAGE+":"+say);
			txFieldChat.setText("");
		}
		else if(e.getActionCommand().equals("求和")){
			if(isStart){
			client.sendMessage(PacketMessage.GAME_PEACE+":");
		}
		}
		else if(e.getActionCommand().equals("退出")){
    		if(isStart){
    			JOptionPane.showMessageDialog(null, "正在游戏中，不能退出", "错误提示",
    					JOptionPane.ERROR_MESSAGE);
    			return;
    		}
    		else{ //退出房间
    			client.sendMessage(PacketMessage.EXIT_GAME+":");
    		}
		}
	}
	//显示聊天。
	public void showChat(String s){
		txAreaChat.append(s);
	}
	
	//求和请求。
	public void gamePeace(String reveive){
		StringTokenizer st=new StringTokenizer(reveive, ":");
		String s=st.nextToken();
		if(st.hasMoreTokens()){
			s=st.nextToken();
			if(s.equals("0")){
				JOptionPane.showMessageDialog(null, "对手不同意平局，请继续下棋","温馨提示",JOptionPane.ERROR_MESSAGE);
				
			}else if(s.equals("1")){
				gameOver("此局平局！");
			}
		}
		else{
			int result =JOptionPane.showConfirmDialog(null, "对手请求求和，您同意吗？","提示",JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_OPTION){
				client.sendMessage(PacketMessage.GAME_PEACE+":"+1+":");
				gameOver("此局平局");
			}
			else if(result==JOptionPane.NO_OPTION){
				client.sendMessage(PacketMessage.GAME_PEACE+":"+0+":");
			}
		}
	}
	//更新加入桌子的对手的信息
	public void updateOpponent(String oppoImg, String oppoName){
		labelOpponent.setIcon(new ImageIcon("./res/face/"+oppoImg));
		labelOpponent.setText(oppoName);
		userList.add(oppoName);	
	}
	
	//更新离开桌子的对手的头像和名称
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
	
	//游戏开始
	public void gameStart(){
		isStart = true;
		t = new Timer();
		finishTimer = new FinishTimer();
		t.schedule(finishTimer, 0, 1000);
		//t.scheduleAtFixedRate(finishTimer, 0, 1000);
		labelMeStart.setIcon(null);
		labelOppoStart.setIcon(null);
		if(userMe.getChairId() == 0){ //左边椅子用户先下棋
			isMeGoing = true;
			labelMeStart.setIcon(new ImageIcon("./res/img/r5.gif"));
		}else{
			isMeGoing = false;
			labelOppoStart.setIcon(new ImageIcon("./res/img/r5.gif"));
		}
		//qiPanPanel.repaint();
	}
	
	//对方已经准备好
	public void opponentReady(){
		labelOppoStart.setIcon(new ImageIcon("./res/img/start.gif"));
		labelOppoStart.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	//收到对方走一步棋信息
	public void oppoMoveChess(int oldx, int oldy, int newx, int newy){
		finishTime = RUNTIME;
		isMeGoing = true;
		labelMeStart.setIcon(new ImageIcon("./res/img/r5.gif"));
		labelOppoStart.setIcon(null);
		labelOppoThisTime.setText("本步剩余时间：");	
		if(oldx != -1){
			qiPanPanel.updatePos(oldx, oldy, newx, newy); //只能放在最后，如果游戏结束，要更新响应的label
		}		
	}
	
	//游戏结束，显示对弈结果，初始化
	public void gameOver(String displayMsg){
		t.cancel();
		//finishTimer.cancel();
		JOptionPane.showMessageDialog(null,displayMsg, "对弈结果",
				JOptionPane.INFORMATION_MESSAGE);
		//重新初始化相关变量
		isStart = false;
		isMeReady = false;
		buttonStart.setEnabled(true);
		isMeGoing = false;
		finishTime = RUNTIME;
		labelMeStart.setIcon(null);
		labelOppoStart.setIcon(null);
		labelMeThisTime.setText("本步剩余时间：");
		labelOppoThisTime.setText("本步剩余时间：");
		qiPanPanel.pos = new int[][]{
				{ 17, 18, 19, 20, 21, 22, 23, 24, 25 },// 初始化位置数组pos[10][9]
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
	
	//内部类，计时器，实现时间倒数
	class FinishTimer extends TimerTask{
		
		public void run(){
			if(finishTime >= 0){				
				if(isMeGoing){
					labelMeThisTime.setText("本步剩余时间："+finishTime+" 秒");
					labelOppoThisTime.setText("本步剩余时间：");	
				}
				else{
					labelOppoThisTime.setText("本步剩余时间："+finishTime+" 秒");
					labelMeThisTime.setText("本步剩余时间：");
				}
				finishTime--;
			}else{
				if(isMeGoing){ //本方时间到，将走一步棋的权限交给对方					
					client.sendMessage(PacketMessage.MOVE_CHESS+":-1:-1:-1:-1");
	    			finishTime = RUNTIME; //重新计时
	    			isMeGoing = false;
	    			labelOppoStart.setIcon(new ImageIcon("./res/img/r5.gif"));
	    			labelMeStart.setIcon(null);	
	    			labelMeThisTime.setText("本步剩余时间：");
	    			qiPanPanel.recoverFirstMouseLeftPress();
				}
				
			}
				//this.cancel();
		}
	}

	//内部类，棋盘面板及相关处理
	 class QiPanPanel extends JPanel implements MouseListener{
			private QiZi[] qizi = new QiZi[33]; 
			
		    private Image qiPanImage = null;
		    private Image selectImage = null;   
		
			private int pos[][] ={
					{ 17, 18, 19, 20, 21, 22, 23, 24, 25 },// 初始化位置数组pos[10][9]
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
					{ 0, 26, 0, 0, 0, 0, 0, 27, 0 },
					{ 28, 0, 29, 0, 30, 0, 31, 0, 32},
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 12, 0, 13, 0, 14, 0, 15, 0, 16 },
					{ 0, 10, 0, 0, 0, 0, 0, 11, 0  },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 2, 3, 4, 5, 6, 7, 8, 9  } };
		
			private boolean isFirstMouseLeftPress = false; //true：第一次按鼠标左键（选中棋子）， false： 第二次按鼠标左键（下棋）
			private int curSelectX = 0;  //当前选中的位置
			private int curSelectY = 0;
			private int preSelectX = 0;  //先前选中的位置
			private int preSelectY = 0;
			
			public QiPanPanel(){
		    	try{
		    		//初始化棋子图像
		        	int k=0;
		        	for(int i=0;i<10;i++)
		        		for(int j=0;j<9;j++){
		        			if(pos[i][j]!=0){
		        				qizi[k+1] = new QiZi();
		        				//qizi[k].setX(i);
		        				//qizi[k].setY(j);
		        				//qizi[k].setId(k);
		        				if(userMe.getChairId() == 0) //左边椅子用户走红棋
		        					qizi[k+1].setImage(ImageIO.read(new File("./res/qizi/"+(k+1)+".gif")));
		        				else //右边椅子用户走黑棋
		        					qizi[k+1].setImage(ImageIO.read(new File("./res/qizi/"+((k+16)%32+1)+".gif")));
		        				k++;
		        			}
		        		}
		        	//初始化棋盘图像
		        	qiPanImage = ImageIO.read(new File("./res/qizi/xqboard.gif"));
		        	//初始化选择框图像
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
				//画棋盘
			   	g.drawImage(qiPanImage, QiPanPara.QIPAN_X, QiPanPara.QIPAN_Y, this);    	
		    	if(isMeReady){
		    		//画棋子
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
		    		//画选择框
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
		    	if(!isStart) //游戏没开始
		    		return; 
		    	if(!isMeGoing) //不是轮到自己下棋
		    		return;
				curSelectY = (e.getX() - QiPanPara.QIZI_X )/QiPanPara.QIZI_X_DISTANCE;
				curSelectX = (e.getY() - QiPanPara.QIZI_Y )/QiPanPara.QIZI_Y_DISTANCE;
				if(curSelectX > 9 || curSelectX < 0 ||curSelectY > 8 || curSelectY < 0)  //超出棋盘范围
					return;
				if((curSelectX == preSelectX) && (curSelectY == preSelectY) && isFirstMouseLeftPress){ //点原来选中的位置，表示取消选择
					isFirstMouseLeftPress = false;
					repaint();
					return;
				}
				
				System.out.println("之前选中的位置是：" + preSelectX + "和" +preSelectY);
				System.out.println("之前选中的棋子是：" + pos[preSelectX][preSelectY]);
				System.out.println("当前选中的位置是：" + curSelectX + "和" +curSelectY);
				
		    	if(!isFirstMouseLeftPress){  //第一次按鼠标左键（选中棋子）    	
		    		if(pos[curSelectX][curSelectY] > 0 && pos[curSelectX][curSelectY] < 17){ //有本方棋子才能选中
			    		isFirstMouseLeftPress = true;
			    		preSelectX = curSelectX;
			    		preSelectY = curSelectY;
			    		repaint();
		    		}
		    	}else{  //第二次按鼠标左键（下棋）
		    		
		    		//QiPanController control = new QiPanController();
		    		if(qiZiRule(pos[preSelectX][preSelectY],preSelectX, preSelectY, curSelectX, curSelectY)){
		    			/*if(jiangJunRule()){
		    				int result = JOptionPane.showConfirmDialog(null,"走该步会被将军，继续吗？",
		    						"请确认",JOptionPane.YES_NO_OPTION);
		    				if(result == JOptionPane.NO_OPTION){
		    					return;
		    				}
		    				System.out.println("本方这步会被将军！！！");
		    			}*/
		    			//if(isMeGoing){ //有可能用户一直不确定对话框，而本方时间已经到了，权限已交给对方
			    			isFirstMouseLeftPress = false;
			    			updatePos(preSelectX, preSelectY, curSelectX, curSelectY);
			    			//repaint();
			    			//发送MOVE_CHESS信息
			    			client.sendMessage(PacketMessage.MOVE_CHESS+":"+preSelectX+":"+preSelectY+":"+curSelectX+":"+curSelectY);
			    			//finishTimer.cancel();
			    			finishTime = RUNTIME; //重新计时
			    			isMeGoing = false;
			    			labelOppoStart.setIcon(new ImageIcon("./res/img/r5.gif"));
			    			labelMeStart.setIcon(null);	
			    			labelMeThisTime.setText("本步剩余时间：");
		    			//}else
		    			//	recoverFirstMouseLeftPress();
		    		}
		    		
		    	}  	
		    }
		    

		    public void mouseReleased(MouseEvent e){
		    	
		    }   
    
		    //更新棋子位置
		    public void updatePos(int oldx, int oldy, int newx, int newy){
		    	if(oldx>=0 && oldx<=9 && oldy>=0 && oldy<=8 && newx>=0 && newx<=9 && newy>=0 && newy<=8){
			    	int oldValue = pos[oldx][oldy];
			    	int newValue = pos[newx][newy];
			    	pos[oldx][oldy] = 0;
			    	pos[newx][newy] = oldValue;
			    	repaint();
			    	/*if(jiangJunRule())
			    		System.out.println("将军！！！");*/
			    	if(newValue == 5){ //本方将被吃了，则发送输了的消息
			    		client.sendMessage(PacketMessage.GAME_FAIL+":");
			    		gameOver("很抱歉，你输了！");
			    	}			    	
		    	}		    	
		    }
		    
		    //恢复鼠标点击的初始状态，用于计时到的处理（不显示选择框）
		    public void recoverFirstMouseLeftPress(){
		    	isFirstMouseLeftPress = false;
		    	preSelectX = 0;
		    	preSelectY = 0;
		    	repaint();		    	
		    }
			
				//判定这一步棋是否符合规则
			public boolean qiZiRule(int qiZiNo, int oldx, int oldy, int newx,
					int newy) {
				boolean canGo = false;
				if (qiZiNo == 1 || qiZiNo == 9) // 车
					canGo = cheRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 2 || qiZiNo == 8) // 马
					canGo = maRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 3 || qiZiNo == 7) // 象
					canGo = xiangRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 4 || qiZiNo == 6) // 士
					canGo = shiRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 5) // 将
					canGo = jiangRule(oldx, oldy, newx, newy);
				else if (qiZiNo == 10 || qiZiNo == 11) // 炮
					canGo = paoRule(oldx, oldy, newx, newy);
				else if (qiZiNo >= 12 || qiZiNo <= 16) // 兵
					canGo = bingRule(oldx, oldy, newx, newy);
				return canGo;
			}
	
			// 车的规则
			private boolean cheRule(int oldx, int oldy, int newx, int newy) {
				if (oldx != newx && oldy != newy) { // 不是横向或纵向走动
					return false;
				} else if (oldx == newx) { // 纵向走动，判定中间是否有子
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
				} else { // 横向走动，判定中间是否有子
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
				if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
					return true;
				else
					return false;
			}
	
			// 马的规则
			private boolean maRule(int oldx, int oldy, int newx, int newy) {
				if (((newx == oldx + 1) || (newx == oldx - 1)) && newy == oldy + 2
						&& pos[oldx][oldy + 1] == 0) { // 右方的两个日字位置，且不绊马腿
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
						return true;
				}
				if (((newx == oldx + 1) || (newx == oldx - 1)) && newy == oldy - 2
						&& pos[oldx][oldy - 1] == 0) { // 左方的两个日字位置，且不绊马腿
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
						return true;
				}
				if (((newy == oldy + 1) || (newy == oldy - 1)) && newx == oldx + 2
						&& pos[oldx + 1][oldy] == 0) { // 上方的两个日字位置，且不绊马腿
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
						return true;
				}
				if (((newy == oldy + 1) || (newy == oldy - 1)) && newx == oldx - 2
						&& pos[oldx - 1][oldy] == 0) { // 下方的两个日字位置，且不绊马腿
					if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
						return true;
				}
				return false;
			}
	
			// 象的规则
			private boolean xiangRule(int oldx, int oldy, int newx, int newy) {
				if (newx >= 5) { // 象不能过河
					if ((newx == oldx + 2) && (newy == oldy + 2)
							&& (pos[oldx + 1][oldy + 1] == 0)) { // 左上方的田字位置，且不绊象腿
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
					}
					if ((newx == oldx + 2) && (newy == oldy - 2)
							&& (pos[oldx + 1][oldy - 1] == 0)) { // 左下方的田字位置，且不绊象腿
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
					}
					if ((newx == oldx - 2) && (newy == oldy + 2)
							&& (pos[oldx - 1][oldy + 1] == 0)) { // 左下方的田字位置，且不绊象腿
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
					}
					if ((newx == oldx - 2) && (newy == oldy - 2)
							&& (pos[oldx - 1][oldy - 1] == 0)) { // 左下方的田字位置，且不绊象腿
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
					}
				}
				return false;
			}
	
			// 士的规则
			private boolean shiRule(int oldx, int oldy, int newx, int newy) {
				if (newx > 6 && newx < 10 && newy > 2 && newy < 6) { // 只能在”九宫“内行走。（x=7,8,9 且 y=3,4,5)
					if ((newx == oldx + 1 || newx == oldx - 1)
							&& (newy == oldy + 1 || newy == oldy - 1)) { // 可以斜线前后或左右移动一个位置（x，y都必须有变化）
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
					}
				}
				return false;
			}
	
			// 将的规则
			private boolean jiangRule(int oldx, int oldy, int newx, int newy) {
				if (newx > 6 && newx < 10 && newy > 2 && newy < 6) { // 只能在”九宫“内行走。（x=7,8,9且y=3,4,5)
					if ((newx == oldx) && (newy == oldy + 1 || newy == oldy - 1)
							|| (newy == oldy)
							&& (newx == oldx + 1 || newx == oldx - 1)) { // 可以前后或左右移动一个位置（x，y只能变化其中一个）
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
					}
				}
				return false;
			}
	
			// 炮的规则
			private boolean paoRule(int oldx, int oldy, int newx, int newy) {
				int count = 0;
				if (oldx != newx && oldy != newy) { // 不是横向或纵向走动
					return false;
				} else if (oldx == newx) { // 纵向走动
					if (newy > oldy) {
						for (int y = oldy + 1; y < newy; y++) { // 计算中间棋子数
							if (pos[oldx][y] != 0) {
								count++;
							}
						}
					} else {
						for (int y = newy + 1; y < oldy; y++) { // 计算中间棋子数
							if (pos[oldx][y] != 0) {
								count++;
							}
						}
					}
				} else { // 横向走动
					if (oldx < newx) {
						for (int x = oldx + 1; x < newx; x++) { // 计算中间棋子数
							if (pos[x][oldy] != 0) {
								count++;
							}
						}
					} else {
						for (int x = newx + 1; x < oldx; x++) { // 计算中间棋子数
							if (pos[x][oldy] != 0) {
								count++;
							}
						}
					}
				}
				if (count == 0 && pos[newx][newy] == 0) // 中间没有棋子，必须是走一个空的位置
					return true;
				else if (count == 1 && pos[newx][newy] > 17) // 中间隔一个棋子必须是吃子
					return true;
				else
					return false;
			}
	
			// 兵的规则
			private boolean bingRule(int oldx, int oldy, int newx, int newy) {
				if (oldx < 5) { // 兵过河后可以向前、左、右走一格
					if ((newx == oldx - 1 && newy == oldy)
							|| (newx == oldx && (newy == oldy + 1 || newy == oldy - 1)))
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
				} else { // 兵没过河，只能向前走一格
					if (newx == oldx - 1 && newy == oldy)
						if (pos[newx][newy] == 0 || pos[newx][newy] > 17) // 走一个空的位置或吃敌方的棋子
							return true;
				}
				return false;
			}
	
			// 当前是否被将军（还没写好）
			private boolean jiangJunRule() {
				// 找本方将的位置
				int jiangX = 0, jiangY = 0;
				for (int x = 0; x < 10; x++)
					for (int y = 0; y < 9; y++)
						if (pos[x][y] == 5) {
							jiangX = x;
							jiangY = y;
							break;
						}
	
				// 是否被对方车和炮和兵将军
				int count = 0;
				for (int x = jiangX - 1; x >= 0; x--) { // 将的上方
					if (pos[x][jiangY] == 0)
						continue;
					else if ((pos[x][jiangY] == 17 || pos[x][jiangY] == 25)
							&& count == 0) // 被对方车将军(中间不能隔子count==0）
						return true;
					else if ((pos[x][jiangY] == 26 || pos[x][jiangY] == 27)
							&& count == 1) // 被对方炮将军（中间必须隔一子count == 1)
						return true;
					else{
						if((x == jiangX-1) && (pos[x][jiangY] >= 28 && pos[x][jiangY] <= 32)) //被对方兵将军
							return true;
						count++;
					}
				}
				count = 0;
				for (int x = jiangX + 1; x <= 9; x++) { // 将的下方
					if (pos[x][jiangY] == 0)
						continue;
					else if ((pos[x][jiangY] == 17 || pos[x][jiangY] == 25)
							&& count == 0) // 被对方车将军(中间不能隔子count==0）
						return true;
					else if ((pos[x][jiangY] == 26 || pos[x][jiangY] == 27)
							&& count == 1) // 被对方炮将军（中间必须隔一子count == 1)
						return true;
					else{
						if((x == jiangX+1) && (pos[x][jiangY] >= 28 && pos[x][jiangY] <= 32)) //被对方兵将军
							return true;
						count++;
					}
				}
				count = 0;
				for (int y = jiangY - 1; y >= 0; y--) { // 将的左方
					if (pos[jiangX][y] == 0)
						continue;
					else if ((pos[jiangX][y] == 17 || pos[jiangX][y] == 25)
							&& count == 0) // 被对方车将军(中间不能隔子count==0）
						return true;
					else if ((pos[jiangX][y] == 26 || pos[jiangX][y] == 27)
							&& count == 1) // 被对方炮将军（中间必须隔一子count == 1)
						return true;
					else{
						if((y == jiangY-1) && (pos[jiangX][y] >= 28 && pos[jiangX][y] <= 32)) //被对方兵将军
							return true;
						count++;
					}
				}
				count = 0;
				for (int y = jiangY + 1; y <= 8; y++) { // 将的右方
					if (pos[jiangX][y] == 0)
						continue;
					else if ((pos[jiangX][y] == 17 || pos[jiangX][y] == 25)
							&& count == 0) // 被对方车将军(中间不能隔子count==0）
						return true;
					else if ((pos[jiangX][y] == 26 || pos[jiangX][y] == 27)
							&& count == 1) // 被对方炮将军（中间必须隔一子count == 1)
						return true;
					else{
						if((y == jiangY+1) && (pos[jiangX][y] >= 28 && pos[jiangX][y] <= 32)) //被对方兵将军
							return true;
						count++;
					}
				}
	
				// 是否被对方马将军
				if (pos[jiangX - 2][jiangY - 1] == 18
						|| pos[jiangX - 2][jiangY - 1] == 24
						|| pos[jiangX - 1][jiangY - 2] == 18
						|| pos[jiangX - 1][jiangY - 2] == 24) { // 左上方120度和160度马的位置
					if (pos[jiangX - 1][jiangY - 1] == 0) // 没有蹩马腿
						return true;
				}
				if (pos[jiangX - 2][jiangY + 1] == 18
						|| pos[jiangX - 2][jiangY + 1] == 24
						|| pos[jiangX - 1][jiangY + 2] == 18
						|| pos[jiangX - 1][jiangY + 2] == 24) { // 右上方60度和30度两个马的位置
					if (pos[jiangX - 1][jiangY + 1] == 0) // 没有蹩马腿
						return true;
				}
	
				// 接下来的四个位置要进行越界判定
				if (jiangX <= 8) {
					if (pos[jiangX + 1][jiangY + 2] == 18
							|| pos[jiangX + 1][jiangY + 2] == 24) // 右下方-30度马的位置
						if (pos[jiangX + 1][jiangY + 1] == 0) // 没有蹩马腿
							return true;
					if (pos[jiangX + 1][jiangY - 2] == 18
							|| pos[jiangX + 1][jiangY - 2] == 24) // 左下方210度马的位置
						if (pos[jiangX + 1][jiangY - 1] == 0) // 没有蹩马腿
							return true;
				} else if (jiangX <= 7) {
					if (pos[jiangX + 2][jiangY + 1] == 18
							|| pos[jiangX + 2][jiangY + 1] == 24) // 右下方-60度马的位置
						if (pos[jiangX + 1][jiangY + 1] == 0) // 没有蹩马腿
							return true;
					if (pos[jiangX + 2][jiangY - 1] == 18
							|| pos[jiangX + 2][jiangY - 1] == 24) // 左下方240度马的位置
						if (pos[jiangX + 1][jiangY - 1] == 0) // 没有蹩马腿
							return true;
				}
				return false; // 没有被将军
	
			}
	 }
}
