package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;



import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;

/**
 * 
 * @author hayata_maekawa
 *
 *	公休繰越データ
 */
@Getter
@Setter
@AllArgsConstructor
public class PublicHolidayCarryForwardData implements DomainAggregate{

	/*
	 * 社員ID
	 */
	private String employeeId;
	
	/*
	 * 繰越数
	 */
	private LeaveRemainingDayNumber numberCarriedForward;
	
	/*
	 * 登録種別
	 */
	private GrantRemainRegisterType grantRemainRegisterType;
	
	
	public PublicHolidayCarryForwardData clone(){
		return new PublicHolidayCarryForwardData(
				this.employeeId,
				this.numberCarriedForward,
				this.grantRemainRegisterType);
	}
	
	
	public static PublicHolidayCarryForwardData createFromJavaType(
			String employeeId,
			double numberCarriedForward,
			int  grantRemainRegisterType
			) {

		return new PublicHolidayCarryForwardData(employeeId,
				new LeaveRemainingDayNumber(Double.isNaN(numberCarriedForward) ? 0.0 :numberCarriedForward),
				EnumAdaptor.valueOf(grantRemainRegisterType, GrantRemainRegisterType.class));
	}
	
	public static PublicHolidayCarryForwardData createFromJavaType(
			String employeeId,
			BigDecimal numberCarriedForward,
			int  grantRemainRegisterType
			) {

		return new PublicHolidayCarryForwardData(employeeId,
				new LeaveRemainingDayNumber(numberCarriedForward == null ? 0.0 :numberCarriedForward.doubleValue()),
				EnumAdaptor.valueOf(grantRemainRegisterType, GrantRemainRegisterType.class));
	}
	
	
	/**
	 * 相殺後の繰越数を求める
	 * @param numberCarriedForward 残数
	 * @return 公休繰越データ
	 */
	public PublicHolidayCarryForwardData getCarryForwardDataAfterOffset(LeaveRemainingDayNumber remainingData){
		
		if(remainingData.v() < 0.0 && this.numberCarriedForward.v() > 0.0){
			return new PublicHolidayCarryForwardData(
					this.employeeId,
					new LeaveRemainingDayNumber(this.numberCarriedForward.v() - getOffsetDays(remainingData).v()),
					this.grantRemainRegisterType);
		}
		
		if(remainingData.v() > 0.0 && this.numberCarriedForward.v() < 0.0){
			return new PublicHolidayCarryForwardData(
					this.employeeId,
					new LeaveRemainingDayNumber(this.numberCarriedForward.v() + getOffsetDays(remainingData).v()),
					this.grantRemainRegisterType);
		}
		
		return this.clone();
	}
	
	
	
	/**
	 * 当月の残数を相殺する
	 * @param remainingData 当月残数
	 * @param carryForwardData 繰越残数
	 * @return 翌月繰越数
	 */
	public LeaveRemainingDayNumber offsetRemainingDataOfTheMonth(LeaveRemainingDayNumber remainingData){
		return new LeaveRemainingDayNumber(remainingData.v() + this.numberCarriedForward.v());
	}
	
	
	/**
	 * 相殺できる日数を取得する
	 * @param numberCarriedForward 残数
	 * @return 残数
	 */
	private LeaveRemainingDayNumber getOffsetDays(LeaveRemainingDayNumber numberCarriedForward){
		return new LeaveRemainingDayNumber(
				Math.min(Math.abs(numberCarriedForward.v()), Math.abs(this.numberCarriedForward.v())));
	}
}
