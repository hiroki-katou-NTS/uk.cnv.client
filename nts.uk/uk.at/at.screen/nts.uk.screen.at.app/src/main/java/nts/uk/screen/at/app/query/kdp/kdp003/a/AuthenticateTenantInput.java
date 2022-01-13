package nts.uk.screen.at.app.query.kdp.kdp003.a;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticateTenantInput {

	public String contactCode;
	
	public String password;
	
	public HttpServletRequest reques;
	
}
