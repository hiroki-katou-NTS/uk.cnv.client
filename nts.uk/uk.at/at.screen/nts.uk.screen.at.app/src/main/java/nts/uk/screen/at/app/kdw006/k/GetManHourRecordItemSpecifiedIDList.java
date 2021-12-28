package nts.uk.screen.at.app.kdw006.k;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItem;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItemRepository;

/**
 * Query: 指定するIDリストの工数実績項目を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.App.指定するIDリストの工数実績項目を取得する
 * @author chungnt
 *
 */

@Stateless
public class GetManHourRecordItemSpecifiedIDList {

	@Inject
	private ManHourRecordItemRepository manHourRecordItemRepository;
	
	public List<GetManHourRecordItemSpecifiedIDListDto> get(GetManHourRecordItemSpecifiedIDListParam param) {
		
		List<GetManHourRecordItemSpecifiedIDListDto> result = new ArrayList<>();
		
		List<ManHourRecordItem> domains = manHourRecordItemRepository.get(param.cId, param.items);
		
		result = domains.stream().map(m -> {
			return new GetManHourRecordItemSpecifiedIDListDto(
					m.getItemId(),
					m.getName(),
					m.getUseAtr().value == 1 ? true : false
					);
		}).collect(Collectors.toList());
		
		return result;
	}
}
