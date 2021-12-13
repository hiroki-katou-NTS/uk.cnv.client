package nts.uk.ctx.sys.shared.dom.user;

import lombok.Value;

@Value

public class SearchUser {
	
	String userID;
	String loginID;
	String userName;
	String personId;
	
	public SearchUser(String userID, String loginID, String userName, String personId) {
		super();
		this.userID = userID;
		this.loginID = loginID;
		this.userName = userName;
		this.personId = personId;
	}
	

}