package nts.uk.screen.at.app.kdw006.k;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistory;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * ScreenQuery: 作業補足情報の選択肢履歴を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.K：任意作業コード設定.メニュー別OCD.作業補足情報の選択肢履歴を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class AcquireSelectionHistoryOfWork {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;

	public List<AcquireSelectionHistoryOfWorkDto> get() {

		List<AcquireSelectionHistoryOfWorkDto> result = new ArrayList<>();
		String cid = AppContexts.user().companyId();

		List<TaskSupInfoChoicesHistory> doamins = taskSupInfoChoicesHistoryRepo.getAll(cid);

		Map<Integer, List<TaskSupInfoChoicesHistory>> taskSupInfoChoices = doamins.stream()
				  .collect(Collectors.groupingBy(g -> g.getItemId()));
		
		for (Map.Entry<Integer, List<TaskSupInfoChoicesHistory>> entry : taskSupInfoChoices.entrySet()) {
			List<DateHistoryItemDto> dateHistoryItems = new ArrayList<>();
			
			entry.getValue().stream().forEach(f -> {
				f.getDateHistoryItems().forEach(m -> {
					dateHistoryItems.add(new DateHistoryItemDto(m.identifier(), m.start(), m.end()));
				});
			});
			
			result.add(new AcquireSelectionHistoryOfWorkDto(entry.getKey(), dateHistoryItems));
		}

		return result;
	}
}
