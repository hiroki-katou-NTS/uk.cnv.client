package nts.uk.screen.com.app.find.cmm015;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.JobTitleInfoImport;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItemWithPeriod;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffWorkplaceHistoryItemWPeriod;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.query.pub.employee.EmployeeInformationExport;

@AllArgsConstructor
@Getter
public class TransferList {
	
	// 職場履歴リスト
	private List<AffWorkplaceHistoryItemWPeriod> awhItems;
	
	// 職位履歴リスト
	private List<AffJobTitleHistoryItemWithPeriod> ajthItems;
	
	// List<社員一覧情報>
	private List<EmployeeInformationExport> empInfors;
	
	// List<職場一覧情報>
	private List<WorkplaceInforParam> wkpListInfo;
	
	// List<職位一覧情報>
	private List<JobTitleInfoImport> jtInfor;
}
