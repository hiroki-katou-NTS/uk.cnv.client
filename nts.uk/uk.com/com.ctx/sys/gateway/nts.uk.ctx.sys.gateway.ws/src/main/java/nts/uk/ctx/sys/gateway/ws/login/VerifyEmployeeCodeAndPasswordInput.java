package nts.uk.ctx.sys.gateway.ws.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmployeeCodeAndPasswordInput {

	private String cid;

	private String employeeCode;

	private String password;

}
