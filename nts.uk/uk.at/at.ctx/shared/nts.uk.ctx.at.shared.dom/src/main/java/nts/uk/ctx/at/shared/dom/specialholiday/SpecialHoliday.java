package nts.uk.ctx.at.shared.dom.specialholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.PerServiceLengthTableCD;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.GrantNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveAppSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveGrantSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialHolidayInfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.SpecialLeaveRestriction;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.ElapseYear;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantRegular;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.PeriodGrantDate;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.primitive.Memo;

/**
 * 特別休暇
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class SpecialHoliday extends AggregateRoot {

	/** 会社ID */
	private String companyId;

	/** 特別休暇コード */
	private SpecialHolidayCode specialHolidayCode;

	/** 特別休暇名称 */
	private SpecialHolidayName specialHolidayName;

	/** 付与・期限情報 */
	private GrantRegular grantRegular;

	/** 特別休暇利用条件 */
	private SpecialLeaveRestriction specialLeaveRestriction;

	/** 対象項目 */
	private TargetItem targetItem;

	/**自動付与区分 */
	public NotUseAtr autoGrant;

	/** 連続で取得する */
	public NotUseAtr continuousAcquisition;

	/** メモ */
	private Memo memo;

	@Override
	public void validate() {
		super.validate();
	}

	public SpecialHoliday(String companyId, SpecialHolidayCode specialHolidayCode,
			SpecialHolidayName specialHolidayName, NotUseAtr autoGrant, Memo memo) {
		super();
		this.companyId = companyId;
		this.specialHolidayCode = specialHolidayCode;
		this.specialHolidayName = specialHolidayName;
		this.autoGrant = autoGrant;
		this.memo = memo;
		this.grantRegular = new GrantRegular();
		this.specialLeaveRestriction = new SpecialLeaveRestriction();
		this.targetItem = new TargetItem();
		this.continuousAcquisition = NotUseAtr.NOT_USE;
	}

	public SpecialHoliday(String companyId, SpecialHolidayCode specialHolidayCode,
			SpecialHolidayName specialHolidayName, GrantRegular grantRegular,
			SpecialLeaveRestriction specialLeaveRestriction, Memo memo) {
		super();
		this.companyId = companyId;
		this.specialHolidayCode = specialHolidayCode;
		this.specialHolidayName = specialHolidayName;
		this.grantRegular = grantRegular;
		this.specialLeaveRestriction = specialLeaveRestriction;
		this.targetItem = new TargetItem();
		this.memo = memo;
		this.continuousAcquisition = NotUseAtr.NOT_USE;
		this.autoGrant = NotUseAtr.NOT_USE;
	}

	public static SpecialHoliday createFromJavaType(
			String companyId, int specialHolidayCode, String specialHolidayName,
			int autoGrant, String memo) {
		return new SpecialHoliday(companyId,
				new SpecialHolidayCode(specialHolidayCode),
				new SpecialHolidayName(specialHolidayName),
				EnumAdaptor.valueOf(autoGrant, NotUseAtr.class),
				new Memo(memo));
	}

	public static SpecialHoliday of(
			String companyId,
			int specialHolidayCode,
			String specialHolidayName,
			GrantRegular grantRegular,
			SpecialLeaveRestriction specialLeaveRestriction,
			TargetItem targetItem,
			int autoGrant,
			String memo,
			NotUseAtr continuousAcquisition) {
		return new SpecialHoliday(companyId,
				new SpecialHolidayCode(specialHolidayCode),
				new SpecialHolidayName(specialHolidayName),
				grantRegular,
				specialLeaveRestriction,
				targetItem,
				EnumAdaptor.valueOf(autoGrant, NotUseAtr.class),
				continuousAcquisition, new Memo(memo));
	}
	
	/**
	 * 次回特別休暇付与を計算
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * int spLeaveCD 特別休暇コード
	 * @param period 期間
	 * @return 次回特休付与リスト
	 */
	public List<NextSpecialLeaveGrant> calcSpecialLeaveGrantInfo(
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter) {

		// 自動付与区分を確認
		if (this.getAutoGrant().equals(NotUseAtr.NOT_USE)) {
			return new ArrayList<>();
		}
				
		return this.getGrantRegular().getNextSpecialLeaveGrant(require, cacheCarrier, parameter,
				this.getSpecialLeaveRestriction());
		
	}	
	
	
	/**
	 * 社員に依存しない特別休暇情報一覧を作成する
	 * @param cid
	 * @param period
	 * @param specialLeaveCode
	 * @param speGrantDate
	 * @param annGrantDate
	 * @param entryDate
	 * @param specialSetting
	 * @param grantDays
	 * @param grantTableCd
	 * @param require
	 * @param cacheCarrier
	 * @return
	 */
	public List<SpecialHolidayInfor> createNotDepentInfoGrantInfo(String cid,
			DatePeriod period,
			SpecialHolidayCode specialLeaveCode,
			GeneralDate speGrantDate,
			GeneralDate annGrantDate,
			GeneralDate entryDate,
			SpecialLeaveAppSetting specialSetting,
			Optional<GrantNumber> grantDays,
			Optional<PerServiceLengthTableCD> grantTableCd,
			Require require,
			CacheCarrier cacheCarrier){
		
		NextSpecialHolidayGrantParameterGrantDate grantDate = new NextSpecialHolidayGrantParameterGrantDate(entryDate,
				annGrantDate, speGrantDate);
		
		SpecialLeaveBasicInfo basicInfo = new SpecialLeaveBasicInfo(cid, "", specialLeaveCode, UseAtr.USE,
				specialSetting, new SpecialLeaveGrantSetting(speGrantDate, grantDays, grantTableCd));
		
		
		NextSpecialHolidayGrantParameter parameter = new NextSpecialHolidayGrantParameter(cid, Optional.empty(),
				specialLeaveCode, period, basicInfo, Optional.of(grantDate));
		
		List<NextSpecialLeaveGrant> nextSpecialLeaveGrant = calcSpecialLeaveGrantInfo(require, cacheCarrier, parameter);
		
		
		return nextSpecialLeaveGrant
				.stream().map(x -> new SpecialHolidayInfor(x)).collect(Collectors.toList());
	}

	
	public static interface Require
			extends SpecialLeaveRestriction.Require, SpecialLeaveBasicInfo.Require, GrantDate.Require, PeriodGrantDate.Require {

		Optional<ElapseYear> elapseYear(String companyId, int specialHolidayCode);

		Optional<EmpEnrollPeriodImport> getLatestEnrollmentPeriod(String lstEmpId, DatePeriod datePeriod);
	}
	
}
