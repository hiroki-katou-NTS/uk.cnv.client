package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 *         UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.作業管理.作業補足情報項目設定.App.<<Query>>
 *         作業補足情報項目と選択し情報を取得する
 */
@Stateless
public class GetSupInfoChoicesDetails {

	@Inject
	private TaskSupInfoChoicesHistoryRepository repo;

	/**
	 * 取得する
	 * 工数実績項目ID itemId
	 * 年月日 refDate
	 * 
	 * @return List<作業補足情報の選択肢詳細>
	 */
	public List<TaskSupInfoChoicesDetailDto> get(int itemId, GeneralDate refDate) {

		// 1.作業補足情報の選択肢詳細を取得する
		return this.repo.get(AppContexts.user().companyId(), 
				itemId, 
				refDate)
				.stream().map(x -> new TaskSupInfoChoicesDetailDto(x)).collect(Collectors.toList());
	}

}
