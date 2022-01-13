package nts.uk.screen.at.app.kdw006.j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormat;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormatRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * Query: 表示フォーマットを取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力表示フォーマット.App.表示フォーマットを取得する.表示フォーマットを取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class GetDisplayFormat {

	@Inject
	private ManHrInputDisplayFormatRepository manHrInputDisplayFormatRepo;

	public GetDisplayFormatDto get() {
		GetDisplayFormatDto result = new GetDisplayFormatDto();

		Optional<ManHrInputDisplayFormat> domainOpt = manHrInputDisplayFormatRepo.get(AppContexts.user().companyId());

		if (domainOpt.isPresent()) {
			ManHrInputDisplayFormat domain = domainOpt.get();
			
			List<RecordColumnDisplayItemDto> listRecordColumnDisplayItem = domain.getRecordColumnDisplayItems().stream().map(c -> {
				return new RecordColumnDisplayItemDto(c.getOrder(), c.getAttendanceItemId(), c.getDisplayName().v());
			}).collect(Collectors.toList());
			
			List<DisplayAttItemDto> listDisplayAttItem = domain.getDisplayAttItems().stream().map(a -> {
				return new DisplayAttItemDto(a.getAttendanceItemId(), a.getOrder());
			}).collect(Collectors.toList());
			
			List<DisplayManHrRecordItemDto> listDisplayManHrRecordItem = domain.getDisplayManHrRecordItems().stream().map(r -> {
				return new DisplayManHrRecordItemDto(r.getItemId(), r.getOrder());
			}).collect(Collectors.toList());
			
			result.setDisplayAttItems(listDisplayAttItem);
			result.setDisplayManHrRecordItems(listDisplayManHrRecordItem);
			result.setRecordColumnDisplayItems(listRecordColumnDisplayItem);
		}
		
		return result;
	}
}
