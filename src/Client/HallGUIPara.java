package Client;
/*游戏大厅接口
 * 默认为public static final*/
public interface HallGUIPara{
	
	int CHAIR_WIDTH = 40;
	int CHAIR_HEIGHT = 45;
	int DESK_WIDTH = 52;
	int DESK_HEIGHT =52;
	int PLAYERLABEL_WIDTH = 80;
	int PLAYERLABEL_HEIGHT = 30;
	int DESKLABEL_WIDTH = DESK_WIDTH;
	int DESKLABEL_HEIGHT = 40;
	
	int CHAIRONE_X = 40;
	int CHAIRONE_Y = 40;
	
	int DESK_X = CHAIRONE_X+CHAIR_WIDTH;
	int DESK_Y = 35;

	int CHAIRTWO_X = CHAIRONE_X+CHAIR_WIDTH+DESK_WIDTH;
	int CHAIRTWO_Y = 40;
	
	int PLAYERONE_X = 0;
	int PLAYERONE_Y = CHAIRONE_Y+CHAIR_HEIGHT;
	
	int PLAYERTWO_X = CHAIRTWO_X;
	int PLAYERTWO_Y = PLAYERONE_Y;
	
	int DESK_LABEL_X = DESK_X;
	int DESK_LABEL_Y = DESK_Y+DESK_HEIGHT+20;
	
	int DIST_X = 200;
	int DIST_Y = 120;
	
	
}

