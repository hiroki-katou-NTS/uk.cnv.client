package nts.uk.ctx.sys.auth.app.find.grant.rolesetjob;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author HungTT
 *
 */

@Data
@AllArgsConstructor
public class RoleSetGrantedJobTitleDto {

	private List<RoleSetGrantedJobTitleDetailDto> details;

}
