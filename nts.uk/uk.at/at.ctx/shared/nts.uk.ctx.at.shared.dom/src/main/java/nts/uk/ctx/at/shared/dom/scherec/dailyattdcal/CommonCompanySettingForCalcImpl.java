package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.at.shared.dom.ot.frame.NotUseAtr;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrameRepository;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPTimeItemSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPUnitUseSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.shorttimework.CalcOfShortTimeWorkRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worklabor.defor.DeformLaborOTRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worklabor.flex.FlexSetRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareSetRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.midnighttimezone.MidNightTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculationRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.zerotime.ZeroTimeRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpConditionRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.FormulaRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrderRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.workrule.specific.SpecificWorkRuleRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * ????????????????????????????????????????????????
 * @author keisuke_hoshina
 *
 */
@Stateless
public class CommonCompanySettingForCalcImpl implements CommonCompanySettingForCalc{

	//??????????????????
	@Inject
	private HolidayAddtionRepository holidayAddtionRepository;
	//???????????????
	@Inject
	private SpecificWorkRuleRepository specificWorkRuleRepository;
	//???????????????????????????
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;
	//??????
	@Inject 
	private DivergenceTimeRepository divergenceTimeRepository;
	//????????????????????????
	@Inject
	private BPTimeItemSettingRepository bPTimeItemSettingRepository;
	//0?????????
	@Inject
	private ZeroTimeRepository zeroTimeRepository;
	//????????????
	@Inject
	private OptionalItemRepository optionalItemRepository;
	//?????????
	@Inject
	private FormulaRepository formulaRepository;
	//?????????????????????
	@Inject
	private FormulaDispOrderRepository formulaOrderRepository;
	//????????????
	@Inject
	private EmpConditionRepository empConditionRepository;
	
	@Inject
	//??????????????????????????????????????????????????????
	private UsageUnitSettingRepository usageUnitSettingRepository;
	
	@Inject
	private FlexSetRepository flexSetRepository;
	
	@Inject
	private DeformLaborOTRepository deformLaborOTRepository;

	//????????????
	@Inject
	private DeclareSetRepository declareSetRepository;

	//????????????????????????
	@Inject
	private CalcOfShortTimeWorkRepository calcShortTimeWorkRepository;
	
	//?????????
	@Inject
	private OvertimeWorkFrameRepository overtimeFrameRepository;
	
	@Inject
	/** ????????????????????? */
	private PersonCostCalculationRepository personCostCalculationRepository;
	
	
//	@Inject
//	private EmployeeWtSettingRepository employeeWtSettingRepository;
	
	@Override
	public ManagePerCompanySet getCompanySetting(CalculateOption calcOption) {

		String companyId = AppContexts.user().companyId();
		
//		List<ErrorAlarmWorkRecord> errorAlerms = Collections.emptyList();
//		if (!calcOption.isMasterTime()) {
//			errorAlerms = errorAlarmWorkRecordRepository.getAllErAlCompanyAndUseAtr(companyId, true);
//		}
		
		val optionalItems = optionalItemRepository.findAll(companyId);
		val usageSetting = usageUnitSettingRepository.findByCompany(companyId);
		return new ManagePerCompanySet(
									  holidayAddtionRepository.findByCId(companyId),
									  specificWorkRuleRepository.findCalcMethodByCid(companyId),
									  compensLeaveComSetRepository.find(companyId),
									  divergenceTimeRepository.getAllDivTime(companyId),
//									  errorAlerms,
									  bPTimeItemSettingRepository.getListAllSetting(companyId),
									  optionalItems,
									  formulaRepository.find(companyId),
									  formulaOrderRepository.findAll(companyId),
									  empConditionRepository.findAll(companyId, optionalItems.stream().map(oi -> oi.getOptionalItemNo().v()).collect(Collectors.toList())),
									  zeroTimeRepository.findByCId(companyId),
									  personCostCalculationRepository.getHistAnPerCost(companyId),
									  specificWorkRuleRepository.findUpperLimitWkHourByCid(companyId),
									  usageSetting,
									// ???????????????(2019.3.31????????????Not????????????????????????????????????)
									new MidNightTimeSheet(companyId, new TimeWithDayAttr(1320),new TimeWithDayAttr(1740)),
									flexSetRepository.findByCId(companyId).get(),
									deformLaborOTRepository.findByCId(companyId).get(),
									this.declareSetRepository.find(companyId),
									this.calcShortTimeWorkRepository.find(companyId),
									this.overtimeFrameRepository.getOvertimeWorkFrameByFrameByCom(companyId, NotUseAtr.USE.value));
	}
}
