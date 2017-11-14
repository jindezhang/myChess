package Client;
/*��Ϸ����*/
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HallForm implements ActionListener{
	private int preDeskNo = -1,preChairNo = -1; //ѡ�е����Ӻ����Ӻ���
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
//����
	
	public MyClient getClient() {
		return client;
	}

	public void setClient(MyClient client) {
		this.client = client;
	}
	
	public JFrame createUI(){
		JFrame jf = new JFrame("�ͻ�����Ϸ����");
		//���ø�����Ϣ��飨���Ͻǰ�飩
		JPanel personInfo = new JPanel();
		personInfo.setBackground(Color.black);
		personInfo.setLayout(new BorderLayout());
		JPanel innerPerson = new JPanel();
		innerPerson.setLayout(new BorderLayout());
		innerPerson.add(new JLabel(new ImageIcon("./res/img/boy1.gif")),"Center");
		innerPerson.add(new JLabel(user.getName(),new ImageIcon("./res/face/"+user.getImg()),JLabel.CENTER),"South");
		JTabbedPane personTab = new JTabbedPane();
		personTab.addTab("������Ϣ",null,innerPerson,"������Ϣ");
		personInfo.add(personTab);
		personInfo.setVisible(true);
		
		//���÷�������Ϣ��飨���½ǰ�飩
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
		serverTab.addTab("��������Ϣ",null,innerServer,"��������Ϣ");
		serverInfo.add(serverTab);
		//�����ұ߰��
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplitPane.setDividerLocation(300);
		rightSplitPane.setDividerSize(5);
		rightSplitPane.setTopComponent(personInfo); //�������Ͻǵĸ�����ϢPanel
		rightSplitPane.setBottomComponent(serverInfo); //�������Ͻǵķ�������ϢPanel
		
		//��ǩҳ���ϲ���������ʾ��Ϣ�Լ���ť"����"��"�˳�"��Panel
		JPanel top = new JPanel(new BorderLayout());//ֻ�ж��������еĲ��ֹ�������
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,2)); //���εĸ���״�����������Ǳ�ʾһ�����еĲ��֡�
		JButton join = new JButton("����");
		join.addActionListener(this); //����¼�������
		JButton exit = new JButton("�˳�");
		exit.addActionListener(this);
		buttonPanel.add(join);
		buttonPanel.add(exit);
		top.add(new JLabel("<<<<   ������Ϸ      >>>>",JLabel.LEFT),"West");
		top.add(buttonPanel,"East");

		 //��ǩҳ���²��������������ӵ���Ϣ��Panel,������Ϣ����������Ϣ��һ���ָ����
		//��߲�������Ϸ�������ұ��Ǹ�����Ϣ�ͷ�������Ϣ�� rightSplitPane
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		JPanel leftSplitPane = new JPanel();
		leftSplitPane.setBackground(new Color(84,0,159));
		leftSplitPane.setBounds(0,0,660,300);
		leftSplitPane.setLayout(null);
		//�ڴ˴��������ӣ����ӵ���Ϣ
		this.paintHall(leftSplitPane);
		left.add(top,"North");
		left.add(leftSplitPane,"Center");
		
		//����������Ϸ��������ʾ��Ϣ����
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setLeftComponent(left);
		mainSplitPane.setDividerLocation(650);
		mainSplitPane.setDividerSize(5);
		
		/////////////////////
		//��ǩҳ��PANEL����
		JPanel hall = new JPanel();
		hall.setLayout(new BorderLayout());
		hall.add(mainSplitPane,"Center");
			
		
		//��߰��ı�ǩҳ
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("��Ϸ����",null,hall,"��Ϸ����");
		/*addTab(String title,
                Icon icon,
                Component component,
                String tip)����� title ��/�� icon ��ʾ�� component �� tip��
                ��������һ��������Ϊ null��insertTab �ĸ��Ƿ����� 

                          ������
              title - ��ѡ���Ҫ��ʾ�ı���
              icon - ��ѡ���Ҫ��ʾ��ͼ��
              component - ������ѡ�ʱҪ��ʾ�����
              tip - ��ѡ�Ҫ��ʾ�Ĺ�����ʾ*/
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
			// �������Ӳ�����
			desks[k] = new JLabel(new ImageIcon("./res/img/xqnoone.gif"));
			desks[k].setBounds(HallGUIPara.DESK_X+j*HallGUIPara.DIST_X,
					HallGUIPara.DESK_Y+i*HallGUIPara.DIST_Y, HallGUIPara.DESK_WIDTH,HallGUIPara.DESK_HEIGHT);
			mainpart.add(desks[k]); 
			//�������Ӳ�����
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
			//��������ǳƺ���ҵ�¼��������ӱ�ǩ������
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
			//�������ӱ�ŵı�ǩ������
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
		if(s.equals("�˳�")){
			int result = JOptionPane.showConfirmDialog(null,"�����Ҫ�˳�������?",
					"��ȷ��",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.OK_OPTION){
				System.exit(0);
			}
		}else if(e.getActionCommand().equals("����")){
			for(int i=0; i<15; i++)
				for(int j=0; j<2; j++)
					if(players[i][j].getText().equals("")){
						if(tabbedPane.indexOfTab("������Ϸ")!= -1){ //֮ǰ�Ѿ����뷿��
							if(chessGamePanel.isStart()){ //��Ϸ�Ѿ���ʼ
								JOptionPane.showMessageDialog(null, "��Ϸ�Ѿ���ʼ�����ܻ�λ��", "������ʾ",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						//���ͼ���������Ϣ
						client.sendMessage(PacketMessage.JOIN_TABLE + ":"
								+ preDeskNo + ":" + preChairNo + ":" + i
								+ ":" + j + ":" + user.getImg() + ":"
								+ user.getName()); // 02:��ǰ���Ӻ�:��ǰ���Ӻ�:�������Ӻ�:�������Ӻ�:�û�ͷ����:�û���
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
				
				if(preDeskNo != newDeskNo || preChairNo != newChairNo){//���ǵ��ԭ�������Ӳ���Ӧ
					if(tabbedPane.indexOfTab("������Ϸ")!= -1){ //֮ǰ�Ѿ����뷿��
						if(chessGamePanel.isStart()){ //��Ϸ�Ѿ���ʼ
							JOptionPane.showMessageDialog(null, "��Ϸ�Ѿ���ʼ�����ܻ�λ��", "������ʾ",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						//�����뿪������Ϣ
						//client.sendMessage(PacketMessage.EXIT_GAME+":"+preDeskNo+":"+preChairNo);
					}
					//���ͼ���������Ϣ
					client.sendMessage(PacketMessage.JOIN_TABLE + ":"
							+ preDeskNo + ":" + preChairNo + ":" + newDeskNo
							+ ":" + newChairNo + ":" + user.getImg() + ":"
							+ user.getName()); // 02:��ǰ���Ӻ�:��ǰ���Ӻ�:�������Ӻ�:�������Ӻ�:�û�ͷ����:�û���
					preDeskNo = newDeskNo;
					preChairNo = newChairNo;					
				}else  //���ԭ�������ӣ����л�����
					tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("������Ϸ"));
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	//������������
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
	
	//��ʾ�����������ĵ�¼��Ϣ
	public void addServerMessage(String msg){
		serverMsg.append(msg+"\n");
	}
	
	//�������Ϸ�
	public void canNotSeat(int preDeskNo, int preChairNo){
		this.preDeskNo = preDeskNo;  //�ָ���ǰ���Ӻ�
		this.preChairNo = preChairNo;
		JOptionPane.showMessageDialog(null, "�������Ѿ����û�����", "������ʾ",
				JOptionPane.ERROR_MESSAGE);
	}
	
	//���뷿�����
	public void enterChessRoom(String oppoImg, String oppoName, int deskNo, int chairNo){
		user.setDeskId(deskNo);
		user.setChairId(chairNo);
		if (tabbedPane.indexOfTab("������Ϸ") != -1) {// ֮ǰ�Ѿ����뷿�䣬��ɾ��֮ǰ��Tab
			tabbedPane.removeTabAt(tabbedPane.indexOfTab("������Ϸ"));
		}
		if (oppoImg == null) {
			chessGamePanel = new ChessGamePanel(user, client);
			client.setChessGamePanel(chessGamePanel);
			tabbedPane.addTab("������Ϸ", chessGamePanel);
		} else {
			chessGamePanel = new ChessGamePanel(user, oppoImg, oppoName, client);
			client.setChessGamePanel(chessGamePanel);
			tabbedPane.addTab("������Ϸ", chessGamePanel);
		}
		chessGamePanel.setDeskNo(deskNo);
		tabbedPane.setSelectedIndex(1);
	}
	
	//�뿪���䣬������������
	public void exitChessRoom(int deskNo, int chairNo){
		System.out.println("deskNo:"+preDeskNo+":"+deskNo);
		System.out.println("chairNo:"+preChairNo+":"+chairNo);
		if(preDeskNo == deskNo && preChairNo == chairNo){ //�����Լ�����ɾ��������棬����preDeskNo��preChairNo
			tabbedPane.removeTabAt(tabbedPane.indexOfTab("������Ϸ"));
			preDeskNo = -1;
			preChairNo = -1;
		}
		chairs[deskNo][chairNo].setIcon(noone);
		players[deskNo][chairNo].setText("");
	}
	

	

}