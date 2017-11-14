/*
 * 保存桌子信息
 */

package Server;

import Client.User;


public class Desk {
	private int id;
	private User users[] = new User[2];
	private boolean isStart = false;
	
	public Desk(){
		
	}

	public boolean setUser(int chairNo, User user){
		if(users[chairNo] == null){ //椅子上还没坐人
			users[chairNo] = user;
			return true;
		}else
			return false;
	}
	
	public void deleteUser(int chairNo){
		users[chairNo] = null;
	}
	
	public User getUser(int chairNo){
		return users[chairNo];
	}
	
	public User getUserOpponent(int chairNo){
		return users[(chairNo+1)%2];
	}

	public boolean isStart() {
		if(users[0].isReady() && users[1].isReady())
			isStart = true;
		else 
			isStart = false;
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
	
}
