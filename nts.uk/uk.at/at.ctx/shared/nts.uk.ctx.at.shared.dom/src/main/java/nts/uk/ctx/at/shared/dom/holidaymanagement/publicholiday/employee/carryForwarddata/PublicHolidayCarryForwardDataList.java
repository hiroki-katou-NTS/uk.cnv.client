package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;

@AllArgsConstructor
@Setter
public class PublicHolidayCarryForwardDataList {

	/*
	 * 公休繰越データリスト
	 */
	public List<PublicHolidayCarryForwardData> publicHolidayCarryForwardData;
	
	
	public PublicHolidayCarryForwardDataList(){
		this.publicHolidayCarryForwardData = new ArrayList<>();
	}
	
	/**
	 * 基準日以降に繰り越す公休繰越データ一覧を取得
	 * @param criteriaDate 基準日
	 * @return　公休繰越データ一覧
	 */
	public PublicHolidayCarryForwardDataList getCarryForwardPublicHolidayCarryForwardDataList(
			GeneralDate criteriaDate){
		
		return getNotExpiredList(criteriaDate).getOffsetList();
	}
	
	
	/**
	 * 期限が切れる一覧を取得
	 * @param criteriaDate 基準日
	 * @return 公休繰越データ一覧
	 */
	public PublicHolidayCarryForwardDataList getExpiredList(GeneralDate criteriaDate){
		
		return new PublicHolidayCarryForwardDataList(publicHolidayCarryForwardData.stream()
				.filter(x -> x.getYmd().beforeOrEquals(criteriaDate))
				.collect(Collectors.toList()));
	}
	
	/**
	 * 繰越数の合計を取得
	 * @return 合計繰越数
	 */
	public LeaveRemainingDayNumber getCarryForwardData(){
		return new LeaveRemainingDayNumber(publicHolidayCarryForwardData.stream()
				.mapToDouble(x->x.getNumberCarriedForward().v())
				.sum());
	}
	
	
	/**
	 * 期限が切れていない一覧を取得
	 * @param criteriaDate 基準日
	 * @return 公休繰越データ一覧
	 */
	private PublicHolidayCarryForwardDataList getNotExpiredList(GeneralDate criteriaDate){
		
		return new PublicHolidayCarryForwardDataList(publicHolidayCarryForwardData.stream()
				.filter(x -> x.getYmd().after(criteriaDate))
				.collect(Collectors.toList()));
	}
	
	/**
	 * 相殺しきれていない一覧を取得
	 * @return 公休繰越データ一覧
	 */
	private PublicHolidayCarryForwardDataList getOffsetList(){
			
		return new PublicHolidayCarryForwardDataList(publicHolidayCarryForwardData.stream()
				.filter(x -> x.getNumberCarriedForward().v() != 0.0)
				.collect(Collectors.toList()));
	}
	
}
