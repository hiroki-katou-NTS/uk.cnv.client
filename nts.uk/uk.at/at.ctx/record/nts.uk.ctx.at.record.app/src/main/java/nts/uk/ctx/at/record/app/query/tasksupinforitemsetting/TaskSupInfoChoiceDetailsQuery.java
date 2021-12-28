package nts.uk.ctx.at.record.app.query.tasksupinforitemsetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLink;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLinkRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 作業補足情報の選択肢詳細を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業補足情報項目設定.App.作業補足情報の選択肢詳細を取得する
 * @author tutk
 *
 */
@Stateless
public class TaskSupInfoChoiceDetailsQuery {
	
	@Inject
	private ManHourRecordAndAttendanceItemLinkRepository manHourRecordAndAttendanceItemLinkRepo;
	
	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;
	
	public List<TaskSupInfoItemAndSelectInforQueryDto> get(GeneralDate ymd,int itemId) {
		String companyId = AppContexts.user().companyId();
		//1:
		Optional<ManHourRecordAndAttendanceItemLink> opt = manHourRecordAndAttendanceItemLinkRepo.get(companyId, itemId);
		if(opt.isPresent()) {
			//2:
			List<TaskSupInfoChoicesDetail> data = taskSupInfoChoicesHistoryRepo.get(companyId,opt.get().getItemId(), ymd);
			return data.stream().map(c-> new TaskSupInfoItemAndSelectInforQueryDto(c)).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	

}
