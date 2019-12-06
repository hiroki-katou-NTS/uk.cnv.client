package nts.uk.ctx.hr.develop.ws.test;

import lombok.Getter;
import lombok.Value;
import nts.arc.time.GeneralDate;
@Value
@Getter
public class ParamDate {
	public String companyId;
	public GeneralDate baseDate;
	public ParamDate(String companyId, GeneralDate baseDate) {
		super();
		this.companyId = companyId;
		this.baseDate = baseDate;
	}


}
