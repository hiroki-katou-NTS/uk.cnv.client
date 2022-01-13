package nts.uk.ctx.at.record.app.query.tasksupinforitemsetting;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 作業補足情報項目と選択し情報を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業補足情報項目設定.App.作業補足情報項目と選択し情報を取得する
 * @author tutk
 *
 */
@Stateless
public class TaskSupInfoItemAndSelectInforQuery {
	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;
	
	public List<TaskSupInfoItemAndSelectInforQueryDto> get(int itemId,GeneralDate ymd) {
		String companyId = AppContexts.user().companyId();
		List<TaskSupInfoChoicesDetail> data = taskSupInfoChoicesHistoryRepo.get(companyId,itemId, ymd);
		return data.stream().map(c-> new TaskSupInfoItemAndSelectInforQueryDto(c)).collect(Collectors.toList());
	}
}
