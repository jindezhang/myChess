package Client;
import java.io.*;
public class User {
	private int userId;
	private String name;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String img;
	private int DeskId;   //��ǰ���������Ӻ�
	private int ChairId;  //��ǰ���������Ӻ�
	private boolean isReady;  //�Ƿ�׼���ã����˿�ʼ��ť��
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataInputStream getDis() {
		return dis;
	}
	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}
	public DataOutputStream getDos() {
		return dos;
	}
	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}	
	public int getDeskId() {
		return DeskId;
	}
	public void setDeskId(int deskId) {
		DeskId = deskId;
	}
	public int getChairId() {
		return ChairId;
	}
	public void setChairId(int chairId) {
		ChairId = chairId;
	}
	
	public boolean isReady() {
		return isReady;
	}
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	public User(int userId, String name, DataInputStream dis,
			DataOutputStream dos) {
		super();
		this.userId = userId;
		this.name = name;
		this.dis = dis;
		this.dos = dos;
	}
	
	//���ڽ������
	public User(int userId, String name, String img){
		super();
		this.userId = userId;
		this.name = name;
		this.img = img;
	}
	
	public User(DataInputStream dis, DataOutputStream dos) {
		super();
		this.dis = dis;
		this.dos = dos;
	}
	public User(String name, String img){
		this.name = name;
		this.img = img;
	}
	
	public User(){
		
	}
	
	
}
