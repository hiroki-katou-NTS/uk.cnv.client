package nts.uk.ctx.at.shared.dom.scherec.application.bussinesstrip;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;

// 出張申請(反映用)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessTripInfoShare extends ApplicationShare {

	// 勤務情報
	private WorkInformation workInformation;

	// 勤務時間帯
	private List<BusinessTripWorkTime> workingHours;

	public BusinessTripInfoShare(ApplicationShare appShare, WorkInformation workInformation,
			List<BusinessTripWorkTime> workingHours) {
		super(appShare);
		this.workInformation = workInformation;
		this.workingHours = workingHours;
	}

}
