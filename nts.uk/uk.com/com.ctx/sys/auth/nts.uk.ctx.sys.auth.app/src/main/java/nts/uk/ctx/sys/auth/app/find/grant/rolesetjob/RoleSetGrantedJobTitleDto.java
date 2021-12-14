package nts.uk.ctx.sys.auth.app.find.grant.rolesetjob;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;

/**
 * 
 * @author HungTT
 *
 */

@Data
public class RoleSetGrantedJobTitleDto {

	// private String companyId;

	private boolean applyToConcurrentPerson;

	private List<RoleSetGrantedJobTitleDetailDto> details;

	public RoleSetGrantedJobTitleDto(boolean applyToConcurrentPerson, List<RoleSetGrantedJobTitleDetailDto> details) {
		super();
		// this.companyId = companyId;
		this.applyToConcurrentPerson = applyToConcurrentPerson;
		this.details = details;
	}

	public static RoleSetGrantedJobTitleDto fromDomain(RoleSetGrantedJobTitle domain) {
		return new RoleSetGrantedJobTitleDto(domain.isApplyToConcurrentPerson(), domain.getDetails().stream()
				.map(detail -> RoleSetGrantedJobTitleDetailDto.fromDomain(detail)).collect(Collectors.toList()));
	}

}
