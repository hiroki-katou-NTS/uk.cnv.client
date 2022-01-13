package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.WorkDetailsParam;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class WorkRecordDetail {
	
	// 年月日
	private GeneralDate date;
	
	// 社員ID
	private String sID;
	
	// 作業詳細リスト
	private Optional<List<WorkDetailsParam>> lstworkDetailsParam;
	
	// 実績内容
	private Optional<ActualContent> actualContent;
	
}
