package nts.uk.ctx.sys.auth.pub.grant;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleSetGrantedJobTitleDto {

	private List<RoleSetGrantedJobTitleDetailDto> details;
}
