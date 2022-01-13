package nts.uk.ctx.at.auth.app.command.employmentrole;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.shr.com.enumcommon.NotUseAtr;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmploymentRoleCmd {
	/**	ロールID	*/
	private String roleId;

	/**	会社ID	*/
	private String companyId;

	/**	未来日参照許可	*/
	private NotUseAtr futureDateRefPermit;
	
	public EmploymentRole toDomain() {
	 return new EmploymentRole (roleId,companyId,futureDateRefPermit);
	}
}
