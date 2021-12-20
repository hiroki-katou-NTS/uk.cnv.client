package nts.uk.ctx.bs.employee.app.find.workplace.affiliate;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

/**
 * AffWorlplaceHistItemDto CS00017
 * 
 * @author xuan vinh
 *
 */
@Getter
@Setter
public class AffWorlplaceHistItemDto extends PeregDomainDto {

	// 期間
	@PeregItem("IS00081")
	private String period;

	// 開始日
	@PeregItem("IS00082")
	private GeneralDate startDate;

	// 終了日
	@PeregItem("IS00083")
	private GeneralDate endDate;

	/** The workplaceCode. */
	// 職場コード
	@PeregItem("IS00084")
	private String workplaceCode;

//	/** The normalWorkplaceCode. */
//	// 通常職場コード
//	@PeregItem("IS00085")
//	private String normalWorkplaceCode;

	private AffWorlplaceHistItemDto(String recordId) {
		super(recordId);
	}

	public static AffWorlplaceHistItemDto getFirstFromDomain(AffWorkplaceHistory affWrkPlcHist,
			AffWorkplaceHistoryItem affWrkplcHistItem) {
		return getBaseOnDateHist(affWrkplcHistItem, affWrkPlcHist.getHistoryItems().get(0).start(),
				affWrkPlcHist.getHistoryItems().get(0).end());
	}

	public static List<AffWorlplaceHistItemDto> getListFromDomain(AffWorkplaceHistory affWrkPlcHist,
			AffWorkplaceHistoryItem affWrkplcHistItem) {
		return affWrkPlcHist.getHistoryItems().stream()
				.map(item -> getBaseOnDateHist(affWrkplcHistItem, item.start(), item.end()))
				.collect(Collectors.toList());
	}

	public static AffWorlplaceHistItemDto getBaseOnDateHist(AffWorkplaceHistoryItem affWrkplcHistItem,
			GeneralDate startDate, GeneralDate endDate) {
		AffWorlplaceHistItemDto dto = new AffWorlplaceHistItemDto(affWrkplcHistItem.getHistoryId());
		dto.setRecordId(affWrkplcHistItem.getHistoryId());
		dto.setWorkplaceCode(affWrkplcHistItem.getWorkplaceId());
		dto.setStartDate(startDate);
		dto.setEndDate(endDate);
		return dto;
	}
}
