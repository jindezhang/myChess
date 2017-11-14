package Client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Client.HallForm;
import Client.PacketMessage;
import Client.User;

import java.awt.event.ActionListener;
import java.io.*;
import java.awt.event.ActionEvent;

@SuppressWarnings({ "serial", "unused" })
public class LoginFrame extends JFrame implements ActionListener {

	private JPanel contentPane;
	private String iconButton[]=new String[48];
	private JTextField name;
	private JTextField ip;
	private JLabel myIcon; 
	private JButton in;
	private User user = null;
	private MyClient client = new MyClient();
	private HallForm hallForm = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public String getName() {
		String ss=name.getText();
		return ss;
	}
	public void setName(JTextField name) {
		this.name = name;
	}
	public JTextField getIP() {
		return ip;
	}
	public void setIP(JTextField ip) {
		this.ip = ip;
	}
	public JLabel getMyIcon() {
		return myIcon;
	}
	public void setMyIcon(JLabel myIcon) {
		this.myIcon = myIcon;
	}
	/**
	 * Create the frame.
	 */
	@SuppressWarnings("resource")
	public LoginFrame() {
		user = new User();
		user.setImg("1-1.gif");
		setTitle("用户连接界面");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 352, 472);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(5, 5, 321, 140);
		contentPane.add(panel);
		panel.setLayout(null);
		
		
		JLabel label = new JLabel("请输入个人信息：");
		label.setBounds(0, 0, 140, 15);
		panel.add(label);
		
		JLabel lab1 = new JLabel("用户名：");
		lab1.setBounds(37, 25, 62, 15);
		panel.add(lab1);
		
		JLabel lab2 = new JLabel("服务器：");
		lab2.setBounds(37, 60, 62, 18);
		panel.add(lab2);
		
		JLabel lab3 = new JLabel("头像：");
		lab3.setBounds(47, 99, 48, 15);
		panel.add(lab3);
		
		name = new JTextField();
		name.setColumns(10);
		name.setBounds(86, 25, 212, 21);
		panel.add(name);
		
		ip = new JTextField();
		ip.setText("127.0.0.1");
		ip.setColumns(10);
		ip.setBounds(86, 57, 212, 21);
		panel.add(ip);
		
		myIcon = new JLabel("");
		myIcon.setIcon(new ImageIcon("res/face/1-1.gif"));
		myIcon.setBounds(85, 89, 35, 35);
		panel.add(myIcon);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 390, 326, 29);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JButton out = new JButton("退出");
		out.addActionListener(this);
		out.setActionCommand("退出");
		out.setBounds(41, 0, 71, 29);
		panel_2.add(out);
		
		JButton reset = new JButton("\u91CD\u7F6E");
		reset.setBounds(122, 0, 71, 29);
		reset.addActionListener(this);
		reset.setActionCommand("重置");
		panel_2.add(reset);
		
		in = new JButton("\u767B\u5F55");
		in.addActionListener(this);
		in.setActionCommand("连接");
		in.setBounds(203, 0, 71, 29);
		panel_2.add(in);
		
		JPanel panel_3 =  new JPanel();
		panel_3.setLayout(null);  //清空布局管理器。
		panel_3.setBounds(25, 154, 290, 230);
		contentPane.add(panel_3);
		
		try{
			int n,i,j,cc;
			String s;
			i=cc=j=n=0;
			
			BufferedReader file=new  BufferedReader(new FileReader("./res/face/face.ini"));
			s=file.readLine();
			while(n<48){
				iconButton[n]=s;
				JButton bb=new JButton();
				Icon myicon=new ImageIcon("./res/face/"+s);
				bb.setName(s);
				bb.setIcon(myicon);
				bb.setBounds(j*35,i*35,35,35);
				bb.addActionListener(this);
				bb.setActionCommand(s);
				panel_3.add(bb);
				cc++;
	    		if(cc==8) {
	    			i++;j=0;
	    			cc=0;
	    		}else {
	    			j++;
	      		}
	    		s=file.readLine();
				n++;
			}
		}catch(Exception e){
	    	JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		String s = e.getActionCommand();
		if(s.equals("重置")){
			name.setText("");
			ip.setText("127.0.0.1");
			myIcon.setIcon(new ImageIcon("./res/face/1-1.gif"));
		}else if(s.equals("退出")){
			int result = JOptionPane.showConfirmDialog(null,"您真的要退出程序吗?",
					"请确认",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.OK_OPTION){
				System.exit(0); //退出程序
			}
		}else if(s.equals("连接")){
			if(name.getText().equals("")){
				JOptionPane.showMessageDialog(null, "没有填写用户名", "错误提示",
						JOptionPane.ERROR_MESSAGE);
			}else if(ip.getText().equals("")){
				JOptionPane.showMessageDialog(null, "没有填写服务器IP", "错误提示",
						JOptionPane.ERROR_MESSAGE);
			}else{
				user.setName(name.getText());
				//MyClient client = new MyClient();
				if (client.connect(ip.getText())) {
					System.out.println("连接服务器成功");
					String msg = PacketMessage.LOGO_MESSAGE + ":" + name.getText()+":"+user.getImg(); //01:name:1-1.gif

					hallForm = new HallForm(user);
					hallForm.createUI();
					hallForm.setClient(client);
					client.setHallForm(hallForm);
					client.sendMessage(msg);	//要放在set语句之后，因为网络传送比这里快				
					//this.loginFrm.dispose();
					this.setVisible(false);
				} else
					JOptionPane.showMessageDialog(null, "连接服务器不成功", "错误提示",
							JOptionPane.ERROR_MESSAGE);			
			}
			
		}else{
			myIcon.setIcon(new ImageIcon("./res/face/"+s));
			user.setImg(s);
		}
	}
	}

