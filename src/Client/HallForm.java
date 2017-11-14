package Client;
/*游戏大厅*/
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HallForm implements ActionListener{
	private int preDeskNo = -1,preChairNo = -1; //选中的桌子和椅子号码
	private ImageIcon noone = new ImageIcon("./res/img/noone.gif");
	private JTextArea serverMsg;
	private JTabbedPane tabbedPane = null; //by Zheng	
	
	private JButton[][] chairs= new JButton[15][2];
	private JLabel[]  desks = new JLabel[15];
	private JLabel[][] players = new JLabel[15][2];
	private JLabel[]  desklabels = new JLabel[15];
	
	private MyClient client;
	private User user = null;
	private ChessGamePanel chessGamePanel = null;
	
	public HallForm(MyClient client, User user){
		this.client = client;
		this.user = user;
	}
	
	public HallForm(User user) {
		super();
		this.user = user;
	}



	public HallForm(){
		
	}
	
	public static void main(String args[]){
		new HallForm().createUI();
	}
//这里
	
	public MyClient getClient() {
		return client;
	}

	public void setClient(MyClient client) {
		this.client = client;
	}
	
	public JFrame createUI(){
		JFrame jf = new JFrame("客户端游戏窗口");
		//设置个人信息板块（右上角板块）
		JPanel personInfo = new JPanel();
		personInfo.setBackground(Color.black);
		personInfo.setLayout(new BorderLayout());
		JPanel innerPerson = new JPanel();
		innerPerson.setLayout(new BorderLayout());
		innerPerson.add(new JLabel(new ImageIcon("./res/img/boy1.gif")),"Center");
		innerPerson.add(new JLabel(user.getName(),new ImageIcon("./res/face/"+user.getImg()),JLabel.CENTER),"South");
		JTabbedPane personTab = new JTabbedPane();
		personTab.addTab("个人信息",null,innerPerson,"个人信息");
		personInfo.add(personTab);
		personInfo.setVisible(true);
		
		//设置服务器信息版块（右下角版块）
		JPanel serverInfo = new JPanel();
		serverInfo.setLayout(new BorderLayout());
		serverInfo.setBackground(Color.black);
		
		JPanel innerServer = new JPanel();
		innerServer.setLayout(new BorderLayout());
		serverMsg = new JTextArea();
		serverMsg.setEditable(false);
		JScrollPane jta = new JScrollPane(serverMsg);
		innerServer.add(jta);
		JTabbedPane serverTab = new JTabbedPane();
		serverTab.addTab("服务器信息",null,innerServer,"服务器信息");
		serverInfo.add(serverTab);
		//设置右边板块
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplitPane.setDividerLocation(300);
		rightSplitPane.setDividerSize(5);
		rightSplitPane.setTopComponent(personInfo); //加入右上角的个人信息Panel
		rightSplitPane.setBottomComponent(serverInfo); //加入右上角的服务器信息Panel
		
		//标签页的上部，放置提示信息以及按钮"加入"和"退出"的Panel
		JPanel top = new JPanel(new BorderLayout());//只有东南西北中的布局管理器，
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,2)); //矩形的格子状布局器。这是表示一行两列的布局。
		JButton join = new JButton("加入");
		join.addActionListener(this); //添加事件监听器
		JButton exit = new JButton("退出");
		exit.addActionListener(this);
		buttonPanel.add(join);
		buttonPanel.add(exit);
		top.add(new JLabel("<<<<   象棋游戏      >>>>",JLabel.LEFT),"West");
		top.add(buttonPanel,"East");

		 //标签页的下部，包括桌子椅子等信息的Panel,个人信息，服务器信息是一个分隔面板
		//左边部分是游戏大厅，右边是个人信息和服务器信息的 rightSplitPane
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		JPanel leftSplitPane = new JPanel();
		leftSplitPane.setBackground(new Color(84,0,159));
		leftSplitPane.setBounds(0,0,660,300);
		leftSplitPane.setLayout(null);
		//在此处加入椅子，桌子等信息
		this.paintHall(leftSplitPane);
		left.add(top,"North");
		left.add(leftSplitPane,"Center");
		
		//设置整个游戏大厅和显示信息部分
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setLeftComponent(left);
		mainSplitPane.setDividerLocation(650);
		mainSplitPane.setDividerSize(5);
		
		/////////////////////
		//标签页的PANEL部分
		JPanel hall = new JPanel();
		hall.setLayout(new BorderLayout());
		hall.add(mainSplitPane,"Center");
			
		
		//左边板块的标签页
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("游戏大厅",null,hall,"游戏大厅");
		/*addTab(String title,
                Icon icon,
                Component component,
                String tip)添加由 title 和/或 icon 表示的 component 和 tip，
                其中任意一个都可以为 null。insertTab 的覆盖方法。 

                          参数：
              title - 此选项卡中要显示的标题
              icon - 此选项卡中要显示的图标
              component - 单击此选项卡时要显示的组件
              tip - 此选项卡要显示的工具提示*/
		tabbedPane.setBackground(new Color(255,255,255));

		Container con = jf.getContentPane();
		con.setBackground(Color.black);
		con.add(tabbedPane);
		jf.setSize(850,720);
		jf.setVisible(true);
		return jf;
	}
	
	public void paintHall(JPanel mainpart){
		int k = 0;
		for(int i=0;i<5;i++)
		for(int j=0;j<3;j++){
			// 生成桌子并加入
			desks[k] = new JLabel(new ImageIcon("./res/img/xqnoone.gif"));
			desks[k].setBounds(HallGUIPara.DESK_X+j*HallGUIPara.DIST_X,
					HallGUIPara.DESK_Y+i*HallGUIPara.DIST_Y, HallGUIPara.DESK_WIDTH,HallGUIPara.DESK_HEIGHT);
			mainpart.add(desks[k]); 
			//生成椅子并加入
			chairs[k][0] = new JButton(noone);
			chairs[k][1] = new JButton(noone);
			chairs[k][0].setBounds(HallGUIPara.CHAIRONE_X+j*HallGUIPara.DIST_X,
					HallGUIPara.CHAIRONE_Y+i*HallGUIPara.DIST_Y, HallGUIPara.CHAIR_WIDTH, HallGUIPara.CHAIR_HEIGHT);
			chairs[k][1].setBounds(HallGUIPara.CHAIRTWO_X+j*HallGUIPara.DIST_X,
					HallGUIPara.CHAIRTWO_Y+i*HallGUIPara.DIST_Y, HallGUIPara.CHAIR_WIDTH, HallGUIPara.CHAIR_HEIGHT);
			mainpart.add(chairs[k][0]);
			mainpart.add(chairs[k][1]);
			chairs[k][0].setActionCommand(k+":0");
			chairs[k][1].setActionCommand(k+":1");
			chairs[k][0].addActionListener(this);
			chairs[k][1].addActionListener(this);
			//生成玩家昵称和玩家登录加入的椅子标签并加入
			players[k][0] = new JLabel("",JLabel.RIGHT);
			players[k][0].setForeground(Color.yellow);
			players[k][0].setBounds(HallGUIPara.PLAYERONE_X+j*HallGUIPara.DIST_X,
					HallGUIPara.PLAYERONE_Y+i*HallGUIPara.DIST_Y,HallGUIPara.PLAYERLABEL_WIDTH,HallGUIPara.PLAYERLABEL_HEIGHT);
			players[k][1] = new JLabel("",JLabel.LEFT);
			players[k][1].setForeground(Color.yellow);
			players[k][1].setBounds(HallGUIPara.PLAYERTWO_X+j*HallGUIPara.DIST_X,
					HallGUIPara.PLAYERTWO_Y+i*HallGUIPara.DIST_Y,HallGUIPara.PLAYERLABEL_WIDTH,HallGUIPara.PLAYERLABEL_HEIGHT);
			mainpart.add(players[k][0]);
			mainpart.add(players[k][1]);
			//生成桌子标号的标签并加入
			desklabels[k] = new JLabel("- "+(k+1)+" -",JLabel.CENTER);
			desklabels[k].setForeground(Color.white);
			desklabels[k].setBounds(HallGUIPara.DESK_LABEL_X+j*HallGUIPara.DIST_X,
					HallGUIPara.DESK_LABEL_Y+i*HallGUIPara.DIST_Y,HallGUIPara.DESKLABEL_WIDTH,HallGUIPara.DESKLABEL_HEIGHT);
			mainpart.add(desklabels[k]);
			k++;
		}
	}
	
	public void actionPerformed(ActionEvent e){
		String s = e.getActionCommand();
		if(s.equals("退出")){
			int result = JOptionPane.showConfirmDialog(null,"您真的要退出程序吗?",
					"请确认",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.OK_OPTION){
				System.exit(0);
			}
		}else if(e.getActionCommand().equals("加入")){
			for(int i=0; i<15; i++)
				for(int j=0; j<2; j++)
					if(players[i][j].getText().equals("")){
						if(tabbedPane.indexOfTab("象棋游戏")!= -1){ //之前已经加入房间
							if(chessGamePanel.isStart()){ //游戏已经开始
								JOptionPane.showMessageDialog(null, "游戏已经开始，不能换位置", "错误提示",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						//发送加入桌子信息
						client.sendMessage(PacketMessage.JOIN_TABLE + ":"
								+ preDeskNo + ":" + preChairNo + ":" + i
								+ ":" + j + ":" + user.getImg() + ":"
								+ user.getName()); // 02:先前桌子号:先前椅子号:现在桌子号:现在椅子号:用户头像名:用户名
						preDeskNo = i;
						preChairNo = j;						
						return;
					}    		
		}else{
			int pos = s.indexOf(":");
			String header=s.substring(0,pos);
			String tailer = s.substring(pos+1);
			try{
				int newDeskNo = Integer.parseInt(header);
				int newChairNo = Integer.parseInt(tailer);
				
				if(preDeskNo != newDeskNo || preChairNo != newChairNo){//不是点击原来的椅子才响应
					if(tabbedPane.indexOfTab("象棋游戏")!= -1){ //之前已经加入房间
						if(chessGamePanel.isStart()){ //游戏已经开始
							JOptionPane.showMessageDialog(null, "游戏已经开始，不能换位置", "错误提示",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						//发送离开房间信息
						//client.sendMessage(PacketMessage.EXIT_GAME+":"+preDeskNo+":"+preChairNo);
					}
					//发送加入桌子信息
					client.sendMessage(PacketMessage.JOIN_TABLE + ":"
							+ preDeskNo + ":" + preChairNo + ":" + newDeskNo
							+ ":" + newChairNo + ":" + user.getImg() + ":"
							+ user.getName()); // 02:先前桌子号:先前椅子号:现在桌子号:现在椅子号:用户头像名:用户名
					preDeskNo = newDeskNo;
					preChairNo = newChairNo;					
				}else  //点击原来的椅子，仅切换焦点
					tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("象棋游戏"));
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	//更新桌子椅子
	public void setTable(int preDeskNo, int preChairNo, int deskNo, int chairNo, String imgStr, String userName){
		try{
			if(preDeskNo != -1){
				chairs[preDeskNo][preChairNo].setIcon(noone);
				players[preDeskNo][preChairNo].setText("");
			}
			chairs[deskNo][chairNo].setIcon(new ImageIcon("./res/face/" + imgStr));
			players[deskNo][chairNo].setText(userName);			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	//显示服务器发来的登录信息
	public void addServerMessage(String msg){
		serverMsg.append(msg+"\n");
	}
	
	//落座不合法
	public void canNotSeat(int preDeskNo, int preChairNo){
		this.preDeskNo = preDeskNo;  //恢复先前桌子号
		this.preChairNo = preChairNo;
		JOptionPane.showMessageDialog(null, "该桌子已经有用户落座", "错误提示",
				JOptionPane.ERROR_MESSAGE);
	}
	
	//进入房间界面
	public void enterChessRoom(String oppoImg, String oppoName, int deskNo, int chairNo){
		user.setDeskId(deskNo);
		user.setChairId(chairNo);
		if (tabbedPane.indexOfTab("象棋游戏") != -1) {// 之前已经加入房间，则删除之前的Tab
			tabbedPane.removeTabAt(tabbedPane.indexOfTab("象棋游戏"));
		}
		if (oppoImg == null) {
			chessGamePanel = new ChessGamePanel(user, client);
			client.setChessGamePanel(chessGamePanel);
			tabbedPane.addTab("象棋游戏", chessGamePanel);
		} else {
			chessGamePanel = new ChessGamePanel(user, oppoImg, oppoName, client);
			client.setChessGamePanel(chessGamePanel);
			tabbedPane.addTab("象棋游戏", chessGamePanel);
		}
		chessGamePanel.setDeskNo(deskNo);
		tabbedPane.setSelectedIndex(1);
	}
	
	//离开房间，更新桌子椅子
	public void exitChessRoom(int deskNo, int chairNo){
		System.out.println("deskNo:"+preDeskNo+":"+deskNo);
		System.out.println("chairNo:"+preChairNo+":"+chairNo);
		if(preDeskNo == deskNo && preChairNo == chairNo){ //若是自己，则删除房间界面，更新preDeskNo，preChairNo
			tabbedPane.removeTabAt(tabbedPane.indexOfTab("象棋游戏"));
			preDeskNo = -1;
			preChairNo = -1;
		}
		chairs[deskNo][chairNo].setIcon(noone);
		players[deskNo][chairNo].setText("");
	}
	

	

}