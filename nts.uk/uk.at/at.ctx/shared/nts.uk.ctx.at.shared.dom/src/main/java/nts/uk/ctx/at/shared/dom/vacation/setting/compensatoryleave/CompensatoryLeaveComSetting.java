/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.worktime.common.OneDayTime;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSetAtr;

/**
 * 代休管理設定
 * The Class CompensatoryLeaveComSetting.
 */
// 
@Getter
public class CompensatoryLeaveComSetting extends AggregateRoot {

	// 会社ID
	/** The company id. */
	private String companyId;

	// 管理区分
	/** The is managed. */
	private ManageDistinct isManaged;

	// 取得と使用方法
	/** The normal vacation setting. */
	private CompensatoryAcquisitionUse compensatoryAcquisitionUse;

	// 発生設定
	private SubstituteHolidaySetting substituteHolidaySetting;
		
	// 時間代休の消化単位
	/** The compensatory digestive time unit. */
	private CompensatoryDigestiveTimeUnit compensatoryDigestiveTimeUnit;
   
	// 紐付け管理区分
	private ManageDistinct linkingManagementATR;
	
	
	public List<CompensatoryOccurrenceSetting> getCompensatoryOccurrenceSetting(){
		List<CompensatoryOccurrenceSetting> result = new ArrayList<>();
		CompensatoryOccurrenceSetting holidayWorkTime = new CompensatoryOccurrenceSetting(
				CompensatoryOccurrenceDivision.WorkDayOffTime,
				new SubHolTransferSet(
						new OneDayTime(this.substituteHolidaySetting.getHolidayWorkHourRequired().getTimeSetting().getCertainPeriodofTime().getCertainPeriodofTime().v()), 
						this.substituteHolidaySetting.getHolidayWorkHourRequired().isUseAtr(), 
						this.substituteHolidaySetting.getHolidayWorkHourRequired().getTimeSetting().getDesignatedTime(),
						SubHolTransferSetAtr.valueOf(this.substituteHolidaySetting.getHolidayWorkHourRequired().getTimeSetting().getEnumTimeDivision().value)
						)
				);
		result.add(holidayWorkTime);
		CompensatoryOccurrenceSetting overTime = new CompensatoryOccurrenceSetting(
				CompensatoryOccurrenceDivision.FromOverTime,
				new SubHolTransferSet(
						new OneDayTime(this.substituteHolidaySetting.getOvertimeHourRequired().getTimeSetting().getCertainPeriodofTime().getCertainPeriodofTime().v()), 
						this.substituteHolidaySetting.getOvertimeHourRequired().isUseAtr(), 
						this.substituteHolidaySetting.getOvertimeHourRequired().getTimeSetting().getDesignatedTime(),
						SubHolTransferSetAtr.valueOf(this.substituteHolidaySetting.getOvertimeHourRequired().getTimeSetting().getEnumTimeDivision().value)
						)
				);
		result.add(overTime);
		return result;
	}
	
	/**
	 * Checks if is managed.
	 *
	 * @return true, if is managed
	 */
	//代休を管理するかどうか判断する
	public boolean isManaged() {
		return this.isManaged.equals(ManageDistinct.YES);
	}
	
	//時間代休を管理するか判断する
	public boolean isManagedTime() {
		return isManaged() && this.compensatoryDigestiveTimeUnit.getIsManageByTime().equals(ManageDistinct.YES);
	}

	/**
	 * Instantiates a new compensatory leave com setting.
	 *
	 * @param memento
	 *            the memento
	 */
	public CompensatoryLeaveComSetting(CompensatoryLeaveComGetMemento memento) {
		this.companyId = memento.getCompanyId();
		this.isManaged = memento.getIsManaged();
		this.compensatoryAcquisitionUse = memento.getCompensatoryAcquisitionUse();
		this.compensatoryDigestiveTimeUnit = memento.getCompensatoryDigestiveTimeUnit();
//		this.compensatoryOccurrenceSetting = memento.getCompensatoryOccurrenceSetting();
		this.substituteHolidaySetting = memento.getSubstituteHolidaySetting();
		this.linkingManagementATR = memento.getLinkingManagementATR();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(CompensatoryLeaveComSetMemento memento) {
		memento.setCompanyId(this.companyId);
		memento.setIsManaged(this.isManaged);
		memento.setCompensatoryAcquisitionUse(this.compensatoryAcquisitionUse);
		memento.setCompensatoryDigestiveTimeUnit(this.compensatoryDigestiveTimeUnit);
//		memento.setCompensatoryOccurrenceSetting(this.compensatoryOccurrenceSetting);
		memento.setSubstituteHolidaySetting(this.substituteHolidaySetting);
		memento.setLinkingManagementATR(this.linkingManagementATR);
	}

	public CompensatoryLeaveComSetting(String companyId, ManageDistinct isManaged,
			CompensatoryAcquisitionUse compensatoryAcquisitionUse, SubstituteHolidaySetting substituteHolidaySetting,
			CompensatoryDigestiveTimeUnit compensatoryDigestiveTimeUnit, ManageDistinct linkingManagementATR) {
		super();
		this.companyId = companyId;
		this.isManaged = isManaged;
		this.compensatoryAcquisitionUse = compensatoryAcquisitionUse;
		this.substituteHolidaySetting = substituteHolidaySetting;
		this.compensatoryDigestiveTimeUnit = compensatoryDigestiveTimeUnit;
		this.linkingManagementATR = linkingManagementATR;
	}
	
}