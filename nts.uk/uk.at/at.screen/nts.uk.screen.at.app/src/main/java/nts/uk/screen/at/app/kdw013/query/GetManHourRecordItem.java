package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItemRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.App.工数実績項目を取得する
 */
@Stateless
public class GetManHourRecordItem {
	@Inject
	private ManHourRecordItemRepository repo;

	/**
	 * 取得する
	 * 
	 * @return List<工数実績項目Dto>
	 */
	public List<ManHourRecordItemDto> get() {
		// 1. get(ログイン会社ID)
		return this.repo.get(AppContexts.user().companyId()).stream()
				.map(x -> new ManHourRecordItemDto(x))
				.collect(Collectors.toList());
	}
}
