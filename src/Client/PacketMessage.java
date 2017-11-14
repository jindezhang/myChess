package Client;

public interface PacketMessage {
	
	public String LOGO_MESSAGE = "00";  //登录信息
	public String CHAT_MESSAGE="01";   //聊天内容
	public String JOIN_TABLE = "02";   //加入桌子消息
	public String GAME_START = "03";   //开始游戏消息
	public String MOVE_CHESS = "04";   //棋子走步消息
	public String GAME_GIVEIN = "05";  //认输消息
	public String GAME_PEACE="06";     //求和消息
	public String EXIT_GAME = "07";    //退出本场游戏，返回游戏大厅
	public String EXIT_APP = "08";     //退出程序消息
	public String JOIN_TABLE_FAIL = "09";  //加入桌子失败消息
	public String JOIN_TABLE_SUCCESS = "10";  //加入桌子成功消息
	public String JOIN_TABLE_OPPO = "11";    //对手加入信息
	public String LEAVE_TABLE_OPPO = "12";   //对手离开桌子信息
	public String GAME_READY = "13";    //本方准备好信息
	public String GAME_FAIL = "14";   //输了消息
	
}
