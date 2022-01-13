package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;

@AllArgsConstructor
@Getter
@Setter
public class WorkplaceHistoryWorkplaceHistoryItem {

	//所属職場履歴（List）、
	private List<AffWorkplaceHistory> workplaceHistory;
	
	//所属職場履歴項目（List）
	private List<AffWorkplaceHistoryItem> workplaceHistoryItem;
	
}
