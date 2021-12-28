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
 * 計算に必要なパラメータを取得する
 * @author keisuke_hoshina
 *
 */
@Stateless
public class CommonCompanySettingForCalcImpl implements CommonCompanySettingForCalc{

	//休暇加算設定
	@Inject
	private HolidayAddtionRepository holidayAddtionRepository;
	//総拘束時間
	@Inject
	private SpecificWorkRuleRepository specificWorkRuleRepository;
	//会社ごとの代休設定
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;
	//乖離
	@Inject 
	private DivergenceTimeRepository divergenceTimeRepository;
	//加給自動計算設定
	@Inject
	private BPTimeItemSettingRepository bPTimeItemSettingRepository;
	//0時跨ぎ
	@Inject
	private ZeroTimeRepository zeroTimeRepository;
	//任意項目
	@Inject
	private OptionalItemRepository optionalItemRepository;
	//計算式
	@Inject
	private FormulaRepository formulaRepository;
	//計算式の並び順
	@Inject
	private FormulaDispOrderRepository formulaOrderRepository;
	//労働条件
	@Inject
	private EmpConditionRepository empConditionRepository;
	
	@Inject
	//労働時間と日数の設定の利用単位の設定
	private UsageUnitSettingRepository usageUnitSettingRepository;
	
	@Inject
	private FlexSetRepository flexSetRepository;
	
	@Inject
	private DeformLaborOTRepository deformLaborOTRepository;

	//申告設定
	@Inject
	private DeclareSetRepository declareSetRepository;

	//短時間勤務の計算
	@Inject
	private CalcOfShortTimeWorkRepository calcShortTimeWorkRepository;
	
	//残業枠
	@Inject
	private OvertimeWorkFrameRepository overtimeFrameRepository;
	
	@Inject
	/** 人件費計算設定 */
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
									// 深夜時間帯(2019.3.31時点ではNotマスタ参照で動作している)
									new MidNightTimeSheet(companyId, new TimeWithDayAttr(1320),new TimeWithDayAttr(1740)),
									flexSetRepository.findByCId(companyId).get(),
									deformLaborOTRepository.findByCId(companyId).get(),
									this.declareSetRepository.find(companyId),
									this.calcShortTimeWorkRepository.find(companyId),
									this.overtimeFrameRepository.getOvertimeWorkFrameByFrameByCom(companyId, NotUseAtr.USE.value));
	}
}
