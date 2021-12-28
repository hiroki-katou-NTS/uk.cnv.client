package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHistRepo;
import nts.uk.shr.com.context.AppContexts;

/**
 * 初期作業選択設定済み社員を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW003_日別実績の修正.G：初期作業選択設定.メニュー別OCD.初期作業選択設定済み社員を取得する.初期作業選択設定済み社員を取得する
 * @author quytb
 *
 */

@Stateless
public class GetEmployeesWithTaskInitialSelScreenQuery {
	@Inject
	private TaskInitialSelHistRepo taskInitialSelHistRepo;
	
	public List<String> getEmployeeIds(){
		String companyId = AppContexts.user().companyId();
		List<String> emps = this.taskInitialSelHistRepo.getByCid(companyId).stream().map(e -> e.getEmpId()).collect(Collectors.toList());
		return emps;
	}
}


