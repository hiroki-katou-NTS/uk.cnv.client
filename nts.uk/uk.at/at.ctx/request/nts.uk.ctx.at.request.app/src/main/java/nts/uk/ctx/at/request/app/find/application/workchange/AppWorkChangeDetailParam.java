package nts.uk.ctx.at.request.app.find.application.workchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppWorkChangeDetailParam {
	
	private String companyId;
	
	private String appId;
	
	private AppDispInfoStartupDto appDispInfoStartupDto;
}
