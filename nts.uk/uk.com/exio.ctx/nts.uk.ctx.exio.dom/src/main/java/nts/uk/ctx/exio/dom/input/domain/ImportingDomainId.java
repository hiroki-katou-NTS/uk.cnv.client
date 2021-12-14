package nts.uk.ctx.exio.dom.input.domain;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.AffClassHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.CardNumberCanonicalaization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.AffCompanyHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.AffJobTitleHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.AffWorkplaceHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.EmployeeBasicCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.EmploymentHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.ShortWorkTimeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.TempAbsenceHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.WorkConditionCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.annualleave.AnnualLeaveRemainingCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.annualleave.EmployeeAnnualLeaveSettingCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.annualleave.MaxAnnualLeaveCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.compensatory.CompensatoryHolidayCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.compensatory.HolidayWorkCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.substitute.SubstituteHolidayCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.substitute.SubstituteWorkCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.special.SpecialHolidayGrantRemainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.special.SpecialHolidayGrantSettingCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.stock.StockHolidayRemainingCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace.WorkplaceCanonicalization;

/**
 * 受入グループID
 */
@RequiredArgsConstructor
public enum ImportingDomainId {
	
	/** 職場マスタ */
	WORKPLACE(13, WorkplaceCanonicalization::new),
	
	/** 個人基本情報 */
	EMPLOYEE_BASIC(100, EmployeeBasicCanonicalization::new),

	/** 入社退職履歴 */
	AFF_COMPANY_HISTORY(101, AffCompanyHistoryCanonicalization::new),
	
	/** 所属雇用履歴 */
	EMPLOYMENT_HISTORY(102, EmploymentHistoryCanonicalization::new),
	
	/** 所属職場履歴 **/
	AFF_WORKPLACE_HISTORY(103, AffWorkplaceHistoryCanonicalization::new),
	
	/** 所属職位履歴 */
	JOBTITLE_HISTORY(104, AffJobTitleHistoryCanonicalization::new),
	
	/** 所属分類履歴 */
	CLASSIFICATION_HISTORY(105, AffClassHistoryCanonicalization::new),

	/** 休職休業履歴 */
	TEMP_ABSENCE_HISTORY(107, TempAbsenceHistoryCanonicalization::new),
	
	/** 短時間勤務 */
	SHORT_WORK_TIME(108, ShortWorkTimeCanonicalization::new),
	
	/** 労働条件*/
	WORKING_CONDITION(109, WorkConditionCanonicalization::new),	

	/** カードNO */
	CARD_NO(110, CardNumberCanonicalaization::new),
	
	/** 社員の年休付与設定*/
	EMPLOYEE_ANNUAL_LEAVE_SETTING(111, EmployeeAnnualLeaveSettingCanonicalization::new),	
	
	/** 年休上限データ*/
	MAX_ANNUAL_LEAVE(112, MaxAnnualLeaveCanonicalization::new),
	
	/** 年休付与残数データ*/
	ANNUAL_LEAVE_REMAINING(114, AnnualLeaveRemainingCanonicalization::new),
	
	/** 積立休暇付与残数データ*/
	STOCK_HOLIDAY_REMAINING(115, StockHolidayRemainingCanonicalization::new),
	
	/** 社員の特別休暇付与設定 */
	EMPLOYEE_SPECIAL_HOLIDAY_GRANT_SETTING(116, SpecialHolidayGrantSettingCanonicalization::new),
	
	/** 特別休暇付与残数データ */
	SPECIAL_HOLIDAY_GRANT_REMAIN(117, SpecialHolidayGrantRemainCanonicalization::new),
	
	/** 振休管理データ */
	SUBSTITUTE_HOLIDAY(118, SubstituteHolidayCanonicalization::new),
	
	/** 振出管理データ */
	SUBSTITUTE_WORK(119, SubstituteWorkCanonicalization::new),
	
	/** 代休管理データ */
	COMPENSATORY_HOLIDAY(120, CompensatoryHolidayCanonicalization::new),
	
	/** 休出管理データ */
	HOLIDAY_WORK(121, HolidayWorkCanonicalization::new),
	
	;
	
	public final int value;
	
	private final Supplier<DomainCanonicalization> createCanonicalization;
	
	public static ImportingDomainId valueOf(int value) {
		return EnumAdaptor.valueOf(value, ImportingDomainId.class);
	}
	
	public DomainCanonicalization createCanonicalization() {
		return createCanonicalization.get();
	}
}
