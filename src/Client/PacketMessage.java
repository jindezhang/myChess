package Client;

public interface PacketMessage {
	
	public String LOGO_MESSAGE = "00";  //��¼��Ϣ
	public String CHAT_MESSAGE="01";   //��������
	public String JOIN_TABLE = "02";   //����������Ϣ
	public String GAME_START = "03";   //��ʼ��Ϸ��Ϣ
	public String MOVE_CHESS = "04";   //�����߲���Ϣ
	public String GAME_GIVEIN = "05";  //������Ϣ
	public String GAME_PEACE="06";     //�����Ϣ
	public String EXIT_GAME = "07";    //�˳�������Ϸ��������Ϸ����
	public String EXIT_APP = "08";     //�˳�������Ϣ
	public String JOIN_TABLE_FAIL = "09";  //��������ʧ����Ϣ
	public String JOIN_TABLE_SUCCESS = "10";  //�������ӳɹ���Ϣ
	public String JOIN_TABLE_OPPO = "11";    //���ּ�����Ϣ
	public String LEAVE_TABLE_OPPO = "12";   //�����뿪������Ϣ
	public String GAME_READY = "13";    //����׼������Ϣ
	public String GAME_FAIL = "14";   //������Ϣ
	
}
