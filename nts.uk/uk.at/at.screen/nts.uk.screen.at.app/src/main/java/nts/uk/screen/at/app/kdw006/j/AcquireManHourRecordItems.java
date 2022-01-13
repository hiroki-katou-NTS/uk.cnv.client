package nts.uk.screen.at.app.kdw006.j;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItem;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItemRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * Query: 工数実績項目を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.App.工数実績項目を取得する.工数実績項目を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class AcquireManHourRecordItems {

	@Inject
	private ManHourRecordItemRepository manHourRecordItemRepo;

	public List<AcquireManHourRecordItemsDto> get() {
		List<ManHourRecordItem> domains = manHourRecordItemRepo.get(AppContexts.user().companyId());

		return domains.stream().map(m -> {
			return new AcquireManHourRecordItemsDto(m.getItemId(), m.getName(), m.getUseAtr().value);
		}).collect(Collectors.toList());
	}

}
