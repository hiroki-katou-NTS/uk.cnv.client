package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.attendance.MasterShareBus.MasterShareContainer;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrame;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BPUnitUseSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.shorttimework.CalcOfShortTimeWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worklabor.defor.DeformLaborOT;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worklabor.flex.FlexSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.midnighttimezone.MidNightTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.HistAnPerCost;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.zerotime.ZeroTime;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrder;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workrule.specific.UpperLimitTotalWorkingHour;

/**
 * ?????????????????????
 * @author keisuke_hoshina
 *
 */
@Getter
public class ManagePerCompanySet {

	//??????????????????????????????
	Optional<HolidayAddtionSet> holidayAdditionPerCompany;
	
	//???????????????
	Optional<CalculateOfTotalConstraintTime> calculateOfTotalCons;
	
	//???????????????????????????
	CompensatoryLeaveComSetting compensatoryLeaveComSet;
	
	//????????????
	List<DivergenceTimeRoot> divergenceTime;
	
	//??????????????????????????????
//	List<ErrorAlarmWorkRecord> errorAlarm; 
	
	//????????????????????????
	List<BPTimeItemSetting> bpTimeItemSetting;
	
	@Setter
	MasterShareContainer<String> shareContainer;
	
	//????????????
	List<OptionalItem> optionalItems;
	
	// ?????????????????????
	List<Formula> formulaList;
	
	// ?????????????????????????????????
	List<FormulaDispOrder> formulaOrderList;
	
	//????????????????????????
	List<EmpCondition> empCondition;
	
	//0??????????????????
	Optional<ZeroTime> zeroTime;
	
	/** ????????????????????? */
	@Setter
	HistAnPerCost personnelCostSetting;

	@Setter
	Optional<UpperLimitTotalWorkingHour> upperControl;
	
	Optional<UsageUnitSetting> usageSetting;
		
	/** ??????????????? */
	MidNightTimeSheet midNightTimeSheet;
	
	/** ?????????????????????????????????????????? */
	FlexSet flexSet;
	
	/** ???????????????????????????????????? */
	DeformLaborOT deformLaborOT;
	
	/** ???????????? */
	Optional<DeclareSet> declareSet;
	
	/** ???????????????????????? */
	Optional<CalcOfShortTimeWork> calcShortTimeWork;
	
	/** ????????? */
	List<OvertimeWorkFrame> overtimeFrameList;
	
	public ManagePerCompanySet(
			Optional<HolidayAddtionSet> holidayAdditionPerCompany,
			Optional<CalculateOfTotalConstraintTime> calculateOfTotalCons,
			CompensatoryLeaveComSetting compensatoryLeaveComSet,
			List<DivergenceTimeRoot> divergenceTime,
//			List<ErrorAlarmWorkRecord> errorAlarm,
			List<BPTimeItemSetting> bpTimeItemSetting,
			List<OptionalItem> optionalItems,
			List<Formula> formulaList,
			List<FormulaDispOrder> formulaOrderList,
			List<EmpCondition> empCondition,
			Optional<ZeroTime> zeroTime,
			HistAnPerCost personCostCalculation,
			Optional<UpperLimitTotalWorkingHour> upperControl,
			Optional<UsageUnitSetting> usageSetting,
			MidNightTimeSheet midNightTimeSheet,
			FlexSet flexSet,
			DeformLaborOT deformLaborOT,
			Optional<DeclareSet> declareSet,
			Optional<CalcOfShortTimeWork> calcShortWork,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		super();
		this.holidayAdditionPerCompany = holidayAdditionPerCompany;
		this.calculateOfTotalCons = calculateOfTotalCons;
		this.compensatoryLeaveComSet = compensatoryLeaveComSet;
		this.divergenceTime = divergenceTime;
//		this.errorAlarm = errorAlarm;
		this.bpTimeItemSetting = bpTimeItemSetting;
		this.optionalItems = optionalItems;
		this.formulaList = formulaList;
		this.formulaOrderList = formulaOrderList;
		this.empCondition = empCondition;
		this.zeroTime = zeroTime;
		this.personnelCostSetting = personCostCalculation;
		this.upperControl = upperControl;
		this.usageSetting = usageSetting;
		this.midNightTimeSheet = midNightTimeSheet;
		this.flexSet = flexSet;
		this.deformLaborOT = deformLaborOT;
		this.declareSet = declareSet;
		this.calcShortTimeWork = calcShortWork;
		this.overtimeFrameList = overtimeFrameList;
	}
}