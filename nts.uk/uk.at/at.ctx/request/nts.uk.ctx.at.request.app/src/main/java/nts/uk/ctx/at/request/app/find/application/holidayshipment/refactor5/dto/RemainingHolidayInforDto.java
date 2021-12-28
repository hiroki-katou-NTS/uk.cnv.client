package nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.dto.AbsRecDetailParaDto;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsRecRemainMngOfInPeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;

@NoArgsConstructor
@Getter
@Setter
public class RemainingHolidayInforDto {

	/**
	 * 振出振休明細
	 */
	private List<AbsRecDetailParaDto> lstAbsRecMng;
	/**
	 * 残日数
	 */
	private double remainDays;
	/**
	 * 未消化日数
	 */
	private double unDigestedDays;
	/**
	 * 発生日数
	 */
	private double occurrenceDays;
	/**
	 * 使用日数
	 */
	private double useDays;
	/**
	 * 繰越日数
	 */
	private double carryForwardDays;
	/**
	 * 一番近い期限日
	 */
	@Setter
	private String closestDueDate;

	public RemainingHolidayInforDto(CompenLeaveAggrResult domain) {
		this.lstAbsRecMng = domain.getVacationDetails().getLstAcctAbsenDetail().stream().map(c->new AbsRecDetailParaDto(c)).collect(Collectors.toList());
		this.remainDays = domain.getRemainDay().v();
		this.unDigestedDays = domain.getUnusedDay().v();
		this.occurrenceDays = domain.getOccurrenceDay().v();
		this.useDays = domain.getDayUse().v();
		this.carryForwardDays = domain.getCarryoverDay().v();
	}

	public AbsRecRemainMngOfInPeriod toDomain() {
	    return new AbsRecRemainMngOfInPeriod(
	            CollectionUtil.isEmpty(lstAbsRecMng) ? Collections.emptyList() : lstAbsRecMng.stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
	            remainDays, 
	            unDigestedDays, 
	            occurrenceDays, 
	            useDays, 
	            carryForwardDays, 
	            Collections.emptyList(), 
	            null);
	}
}
