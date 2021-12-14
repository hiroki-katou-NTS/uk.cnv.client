package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
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
@NoArgsConstructor
public class PublicHolidayCarryForwardData implements DomainAggregate{

	/*
	 * 社員ID
	 */
	private String employeeId;
	
	/*
	 * 対象月
	 */
	private YearMonth yearMonth;
	
	/*
	 * 期限日
	 */
	private GeneralDate ymd;
	
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
				this.yearMonth,
				this.ymd,
				this.numberCarriedForward,
				this.grantRemainRegisterType);
	}
	
	
	public static PublicHolidayCarryForwardData createFromJavaType(
			String employeeId,
			int yearMonth,
			GeneralDate ymd,
			double numberCarriedForward,
			int  grantRemainRegisterType
			) {

		PublicHolidayCarryForwardData domain = new PublicHolidayCarryForwardData();

		domain.employeeId = employeeId;
		domain.yearMonth = new YearMonth(yearMonth);
		domain.ymd = ymd;
		domain.numberCarriedForward = new LeaveRemainingDayNumber(numberCarriedForward);
		domain.grantRemainRegisterType = EnumAdaptor.valueOf(grantRemainRegisterType, GrantRemainRegisterType.class);

		return domain;
	}
	
	/**
	 * //繰り越されてきた取れてない日数と相殺
	 * @param carryForwardNotAcquired 繰越されてきた取れていない日数
	 * @return
	 */
	public double offsetUnacquiredDays(double carryForwardNotAcquired){
		return Math.min(this.numberCarriedForward.v(), carryForwardNotAcquired);
	}
	
	/**
	 * 相殺後の残数を取得する
	 * @param numberCarriedForward 残数
	 * @return 残数
	 */
	public LeaveRemainingDayNumber getAfterOffset(LeaveRemainingDayNumber remainingData){
		
		if(remainingData.v() < 0.0 && this.numberCarriedForward.v() > 0.0){
			return new LeaveRemainingDayNumber(
					remainingData.v() + getOffsetDays(remainingData).v());
		}
		
		if(remainingData.v() > 0.0 && this.numberCarriedForward.v() < 0.0){
			return new LeaveRemainingDayNumber(
					remainingData.v() - getOffsetDays(remainingData).v());
		}
		
		return remainingData;
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
					this.yearMonth,
					this.ymd,
					new LeaveRemainingDayNumber(this.numberCarriedForward.v() - getOffsetDays(remainingData).v()),
					this.grantRemainRegisterType);
		}
		
		if(remainingData.v() > 0.0 && this.numberCarriedForward.v() < 0.0){
			return new PublicHolidayCarryForwardData(
					this.employeeId,
					this.yearMonth,
					this.ymd,
					new LeaveRemainingDayNumber(this.numberCarriedForward.v() + getOffsetDays(remainingData).v()),
					this.grantRemainRegisterType);
		}
		
		return this.clone();
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
