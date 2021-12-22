package nts.uk.ctx.sys.auth.app.find.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAuthDto {
	/** ユーザID */
	private String userID;
	
	/** ユーザ名 */
	private String userName;
	
	/** ログインID */
	private String loginID;
	
	/** 社員ID */
	private String empID;
	
	/** 社員コード */
	private String empCD;
	
	/** 社員名 */
	private String empName;
	
	private String companyCD;
	
	public UserAuthDto ( String userID, String userName, String loginID, String empID, String empCD,String empName ){
		this.userID = userID;
		this.userName = userName;
		this.loginID = loginID;
		this.empID = empID;
		this.empCD = empCD;
		this.empName = empName;		
	}
}
