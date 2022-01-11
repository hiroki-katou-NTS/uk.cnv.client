package nts.uk.ctx.at.shared.dom.specialholiday;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;


/**
 * 次回特別休暇付与パラメータ
 * @author hayata_maekawa
 *
 */

@AllArgsConstructor
@Getter
@Setter
public class NextSpecialHolidayGrantParameter {
	/** 会社ID */
	private String companyId;
	
	/** 社員ID */
	private Optional<String> employeeId;
	
	/** 特別休暇コード */
	private SpecialHolidayCode specialHolidayCode;
	
	/** 期間 */
	private DatePeriod period;

	/** 特別休暇基本情報 */
	private SpecialLeaveBasicInfo specialLeaveBasicInfo;
	
	/** 付与基準日 */
	private Optional<NextSpecialHolidayGrantParameterGrantDate> grantDate;
	
	
	/**
	 * 入社日を取得する
	 * @param require
	 * @param cacheCarrier
	 * @return
	 */
	public Optional<GeneralDate> getEntryDate(Require require, CacheCarrier cacheCarrier){ 
		if(!this.getEmployeeId().isPresent() && !this.getGrantDate().isPresent()){
			return Optional.empty();
		}
		
		if(this.getGrantDate().isPresent()){
			return Optional.of(this.getGrantDate().get().getEntryDate());
		}
		

		Optional<EmpEnrollPeriodImport> empEnrollPeriod = require.getLatestEnrollmentPeriod(employeeId.get(), period);

		if (empEnrollPeriod.isPresent()){
			return Optional.of(empEnrollPeriod.get().getDatePeriod().start());
		}else{
			return Optional.empty();
		}
	}
	
	/**
	 * 年休付与基準日を取得する
	 * @param require
	 * @param cacheCarrier
	 * @return
	 */
	public Optional<GeneralDate> getAnnualLeaveGrantDate(Require require, CacheCarrier cacheCarrier){ 
		if(!this.getEmployeeId().isPresent() && !this.getGrantDate().isPresent()){
			return Optional.empty();
		}
		
		if(this.getGrantDate().isPresent()){
			return Optional.of(this.getGrantDate().get().getAnnualLeaveGrantDate());
		}
		
		Optional<AnnualLeaveEmpBasicInfo> empBasicInfo = require.employeeAnnualLeaveBasicInfo(getEmployeeId().get());
		
		if(!empBasicInfo.isPresent()){
			return Optional.empty();
		}
		
		return Optional.of(empBasicInfo.get().getGrantRule().getGrantStandardDate());
	}
	
	
	/**
	 * 特別休暇付与基準日を取得する
	 * @return
	 */
	public Optional<GeneralDate> getSpecialHolidayGrantDate(){
		if(this.getGrantDate().isPresent()){
			return Optional.of(this.getGrantDate().get().getSpecialHolidayGrantDate());
		}
		
		return Optional.of(this.getSpecialLeaveBasicInfo().getGrantSetting().getGrantDate());
		
	}
	
	
	public static interface Require{
		Optional<EmpEnrollPeriodImport> getLatestEnrollmentPeriod(String lstEmpId , DatePeriod datePeriod);
		
		Optional<AnnualLeaveEmpBasicInfo> employeeAnnualLeaveBasicInfo(String employeeId);
	}
}
