package nts.uk.ctx.sys.gateway.app.find.login.password.userpassword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeregLoginPasswordDto extends PeregDomainDto {
	
	/* パスワード */
	@PeregItem("IS01108")
	private String password;
	
	public static PeregLoginPasswordDto createFromDomain(User user) {
		PeregLoginPasswordDto dto = new PeregLoginPasswordDto("");
		dto.setRecordId(user.getAssociatedPersonID().get());
		return dto;
	}
}
