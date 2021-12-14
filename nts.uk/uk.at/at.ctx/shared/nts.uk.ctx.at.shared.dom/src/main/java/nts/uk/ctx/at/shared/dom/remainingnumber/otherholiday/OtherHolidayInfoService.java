package nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.HolidayAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemainRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.DesignatedTime;
import nts.uk.ctx.at.shared.dom.worktime.common.OneDayTime;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneOtherSubHolTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * 休出管理データの初回登録
 * 
 * @author lanlt
 *
 */
@Stateless
public class OtherHolidayInfoService {

	@Inject
	private PublicHolidayRemainRepository publicHolidayRemainRepository;

	@Inject
	private ExcessLeaveInfoRepository excessLeaveInfoRepository;

	@Inject
	private LeaveManaDataRepository leaveManaDataRepository;

	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepository;

	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepository;

	@Inject
	private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository;

	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;

	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;

	@Inject
	private WorkingConditionRepository workingConditionRepository;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;

	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;

	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;

	@Inject
	private DiffTimeWorkSettingRepository diffTimeWorkSettingRepository;

	// Constants
	private static final BigDecimal ZERO = new BigDecimal(0);
	private static final BigDecimal ONE = new BigDecimal(1);
	private static final BigDecimal AHALF = new BigDecimal(0.5);

	public void addOtherHolidayInfo(String cid, PublicHolidayRemain pubHD, ExcessLeaveInfo exLeav,
			BigDecimal remainNumber, BigDecimal remainLeft) {

		publicHolidayRemainRepository.add(pubHD);
		excessLeaveInfoRepository.add(exLeav);

		String sid = pubHD.getSID();
		
		// Item IS00366
		if (checkEnableLeaveMan(sid)) {
			if (remainNumber != null) {
				setRemainNumber(cid, sid, remainNumber);
			}
		}

		// Item IS00368
		if (checkEnablePayout(sid)) {
			if (remainLeft != null) {
				setRemainLeftItem(cid, sid, remainLeft);
			}
		}

	}
	
	public void addOtherHolidayInfo(String cid, Map<String, OtherHolidayInfoInter> otherHolidayInfos) {
		List<ExcessLeaveInfo> exLeavLst = new ArrayList<ExcessLeaveInfo>();
		List<PublicHolidayRemain> pubHDLst  = new ArrayList<PublicHolidayRemain>();
		Map<String, BigDecimal> remainLeftMap = new HashMap<>();
		Map<String, BigDecimal> remainNumberMap = new HashMap<>();
       
        otherHolidayInfos.entrySet().stream().forEach(c ->{
        	pubHDLst.add(c.getValue().getPubHD());
        	exLeavLst.add(c.getValue().getExLeav());
        	remainNumberMap.put(c.getKey(), c.getValue().getRemainNumber());
        	remainLeftMap.put(c.getKey(), c.getValue().getRemainLeft());
        });
        
        excessLeaveInfoRepository.addAll(exLeavLst);
		publicHolidayRemainRepository.addAll(pubHDLst);
		
		Map<String, Boolean> checkEnableLeaveMap = checkEnableLeaveMan(new ArrayList<>(otherHolidayInfos.keySet()));
		Map<String, Boolean> checkEnablePayoutMaP = checkEnablePayout(new ArrayList<>(otherHolidayInfos.keySet()));

		Map<String, BigDecimal> remainLeftFinalMap = new HashMap<>();
		Map<String, BigDecimal> remainNumberFinalMap = new HashMap<>();
       
		checkEnableLeaveMap.entrySet().stream().forEach(c ->{
			if(c.getValue()!= null) {
				if(c.getValue().booleanValue()) {
					BigDecimal remainNumber = remainNumberMap.get(c.getKey());
					if (remainNumber != null) {
						remainNumberFinalMap.put(c.getKey(), remainNumber);
					}
				}
			}
		});
		
		// Item IS00368
		checkEnablePayoutMaP.entrySet().stream().forEach(c ->{
			if(c.getValue()!= null) {
				if(c.getValue().booleanValue()) {
					BigDecimal remainLeft = remainLeftMap.get(c.getKey());
					if (remainLeft != null) {
						remainLeftFinalMap.put(c.getKey(), remainLeft);
					}
				}
			}
		});
		
		if(!remainNumberFinalMap.isEmpty()) {
			setRemainNumber(cid, remainNumberFinalMap);
		}
		
		if(!remainLeftFinalMap.isEmpty()) {
			setRemainLeftItem(cid, remainLeftFinalMap);
		}
	}

	// Item IS00368
	// 振出振休管理データの初回登録
	private void setRemainLeftItem(String cid, String sid, BigDecimal remainLeft) {

		// IS00368
		if (remainLeft.compareTo(ZERO) > 0) {
			remainLeftIsBiggerThanZero(cid, sid, remainLeft);
		} else if (remainLeft.compareTo(ZERO) < 0) {
			remainLeftIsSmallerThanZero(cid, sid, remainLeft);
		}
	}
	
	// Item IS00368
	// 振出振休管理データの初回登録
	private void setRemainLeftItem(String cid, Map<String, BigDecimal> remainLeftMaps) {

		// IS00368
		Map<String, BigDecimal> remainNumberBigZero = new HashMap<>();
		Map<String, BigDecimal> remainNumberSmallZero = new HashMap<>();
		remainLeftMaps.entrySet().stream().forEach(c ->{
			// IS00368
			if (c.getValue().compareTo(ZERO) > 0) {
				remainNumberBigZero.put(c.getKey(), c.getValue());
			} else if (c.getValue().compareTo(ZERO) < 0) {
				remainNumberSmallZero.put(c.getKey(), c.getValue());
			}
		});
		// IS00368
		if (!remainNumberBigZero.isEmpty()) {
			remainLeftIsBiggerThanZero(cid, remainNumberBigZero);
		} 
		if (!remainNumberSmallZero.isEmpty()) {
			remainLeftIsSmallerThanZero(cid, remainNumberSmallZero);
		}
	}

	/**
	 * In case of remain left is bigger than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainLeft
	 */
	private void remainLeftIsBiggerThanZero(String cid, String sid, BigDecimal remainLeft) {
		BigDecimal remainLeftTpm = remainLeft;
		String newID = null;
		PayoutManagementData payout = null;

		while (remainLeftTpm.compareTo(ZERO) > 0) {
			newID = IdentifierUtil.randomUniqueId();
			if (remainLeftTpm.compareTo(ONE) >= 0) {
				/**
				 * 社員ID←パラメータ「社員ID」 休出日←日付不明=true 使用期限日←9999/12/31 振休消化区分←未消化 未使用日数←1.0日
				 * 法定内外区分←法定外休日
				 */
				payout = new PayoutManagementData(newID, cid, sid, true, null, GeneralDate.max(),
						HolidayAtr.NON_STATUTORYHOLIDAY.value, 1d, 1d, DigestionAtr.UNUSED.value);

				remainLeftTpm = remainLeftTpm.subtract(ONE);
			} else if (remainLeftTpm.compareTo(AHALF) == 0) {
				payout = new PayoutManagementData(newID, cid, sid, true, null, GeneralDate.max(),
						HolidayAtr.NON_STATUTORYHOLIDAY.value, 0.5d, 0.5d, DigestionAtr.UNUSED.value);
				remainLeftTpm = remainLeftTpm.subtract(AHALF);
			}
			payoutManagementDataRepository.create(payout);
		}
	}
	
	/**
	 * In case of remain left is bigger than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainLeft
	 */
	private void remainLeftIsBiggerThanZero(String cid, Map<String, BigDecimal> remainLeftMaps) {
		List<PayoutManagementData> payoutLst = new ArrayList<>();
		remainLeftMaps.entrySet().stream().forEach(c ->{
			BigDecimal remainLeftTpm = c.getValue();
			String newID = null;
			PayoutManagementData payout = null;
			while (remainLeftTpm.compareTo(ZERO) > 0) {
				newID = IdentifierUtil.randomUniqueId();
				if (remainLeftTpm.compareTo(ONE) >= 0) {
					/**
					 * 社員ID←パラメータ「社員ID」 休出日←日付不明=true 使用期限日←9999/12/31 振休消化区分←未消化 未使用日数←1.0日
					 * 法定内外区分←法定外休日
					 */
					payout = new PayoutManagementData(newID, cid, c.getKey(), true, null, GeneralDate.max(),
							HolidayAtr.NON_STATUTORYHOLIDAY.value, 1d, 1d, DigestionAtr.UNUSED.value);

					remainLeftTpm = remainLeftTpm.subtract(ONE);
				} else if (remainLeftTpm.compareTo(AHALF) == 0) {
					payout = new PayoutManagementData(newID, cid, c.getKey(), true, null, GeneralDate.max(),
							HolidayAtr.NON_STATUTORYHOLIDAY.value, 0.5d, 0.5d, DigestionAtr.UNUSED.value);
					remainLeftTpm = remainLeftTpm.subtract(AHALF);
				}
				payoutLst.add(payout);
			}
		});
		
		if(!payoutLst.isEmpty()) {
			payoutManagementDataRepository.addAll(payoutLst);
		}
	}
	/**
	 * In case of remain left is smaller than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainLeft
	 */
	private void remainLeftIsSmallerThanZero(String cid, String sid, BigDecimal remainLeft) {
		String newID = null;
		// 振休作成数=-1*パラメータ「振休残数」
		BigDecimal remainLeftTpm = remainLeft.multiply(new BigDecimal(-1));
		SubstitutionOfHDManagementData subOfHD = null;
		while (remainLeftTpm.compareTo(ZERO) > 0) {
			newID = IdentifierUtil.randomUniqueId();
			if (remainLeftTpm.compareTo(ONE) >= 0) {
				/**
				 * 社員ID←パラメータ「社員ID」 休出日←日付不明=true 使用期限日←9999/12/31 振休消化区分←未消化
				 */
				subOfHD = new SubstitutionOfHDManagementData(newID, cid, sid, true, null, 1d, 1d);
				remainLeftTpm = remainLeftTpm.subtract(ONE);
			} else if (remainLeftTpm.compareTo(AHALF) == 0) {
				subOfHD = new SubstitutionOfHDManagementData(newID, cid, sid, true, null, 0.5d, 0.5d);
				remainLeftTpm = remainLeftTpm.subtract(AHALF);
			}
			substitutionOfHDManaDataRepository.create(subOfHD);
		}
	}
	
	/**
	 * In case of remain left is smaller than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainLeft
	 */
	private void remainLeftIsSmallerThanZero(String cid, Map<String, BigDecimal> remainLeftMaps) {
		List<SubstitutionOfHDManagementData> subOfHDLst = new ArrayList<>();
		remainLeftMaps.entrySet().stream().forEach(c ->{
			String newID = null;
			// 振休作成数=-1*パラメータ「振休残数」
			BigDecimal remainLeftTpm = c.getValue().multiply(new BigDecimal(-1));
			SubstitutionOfHDManagementData subOfHD = null;
			while (remainLeftTpm.compareTo(ZERO) > 0) {
				newID = IdentifierUtil.randomUniqueId();
				if (remainLeftTpm.compareTo(ONE) >= 0) {
					/**
					 * 社員ID←パラメータ「社員ID」 休出日←日付不明=true 使用期限日←9999/12/31 振休消化区分←未消化
					 */
					subOfHD = new SubstitutionOfHDManagementData(newID, cid, c.getKey(), true, null, 1d, 1d);
					remainLeftTpm = remainLeftTpm.subtract(ONE);
				} else if (remainLeftTpm.compareTo(AHALF) == 0) {
					subOfHD = new SubstitutionOfHDManagementData(newID, cid, c.getKey(), true, null, 0.5d, 0.5d);
					remainLeftTpm = remainLeftTpm.subtract(AHALF);
				}
				subOfHDLst.add(subOfHD);
			}
		});
		if(!subOfHDLst.isEmpty()) {
			substitutionOfHDManaDataRepository.addAll(subOfHDLst);
		}
	}

	// Item IS00366
	// 休出代休管理データの初回登録
	private void setRemainNumber(String cid, String sid, BigDecimal remainNumber) {
		// IS00368
		if (remainNumber.compareTo(ZERO) > 0) {
			remainNumberIsMoreThanZero(cid, sid, remainNumber);
		} else if (remainNumber.compareTo(ZERO) < 0) {
			remainNumberIsSmallerThanZero(cid, sid, remainNumber);
		}
	}
	
	// Item IS00366
	// 休出代休管理データの初回登録
	private void setRemainNumber(String cid, Map<String, BigDecimal> remainNumberMap) {
		Map<String, BigDecimal> remainNumberBigZero = new HashMap<>();
		Map<String, BigDecimal> remainNumberSmallZero = new HashMap<>();
		remainNumberMap.entrySet().stream().forEach(c ->{
			// IS00368
			if (c.getValue().compareTo(ZERO) > 0) {
				remainNumberBigZero.put(c.getKey(), c.getValue());
			} else if (c.getValue().compareTo(ZERO) < 0) {
				remainNumberSmallZero.put(c.getKey(), c.getValue());
			}
		});
		// IS00368
		if (!remainNumberBigZero.isEmpty()) {
			remainNumberIsMoreThanZero(cid, remainNumberBigZero);
		} 
		if (!remainNumberSmallZero.isEmpty()) {
			remainNumberIsSmallerThanZero(cid, remainNumberSmallZero);
		}
	}

	/**
	 * In case of remain number is bigger than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainNumber
	 */
	private void remainNumberIsMoreThanZero(String cid, String sid, BigDecimal remainNumber) {
		BigDecimal remainNumberTpm = remainNumber;
		LeaveManagementData leaveMana = null;
		String newID = null;
		// 1日相当時間←指定時間．1日の時間
		// 半日相当時間←指定時間．半日の時間
		// TODO QA
		DesignatedTime commonSet = getWorkTimeSetting(cid, sid);

		int aDay = commonSet.getOneDayTime().v();
		int aHalf = commonSet.getHalfDayTime().v();
		while (remainNumberTpm.compareTo(ZERO) > 0) {
			newID = IdentifierUtil.randomUniqueId();
			if (remainNumberTpm.compareTo(ONE) >= 0) {
				leaveMana = new LeaveManagementData(newID, cid, sid, true, null, GeneralDate.max(), 1d, 0, 1d, 0,
						DigestionAtr.UNUSED.value, aDay, aHalf);

				remainNumberTpm = remainNumberTpm.subtract(ONE);
			} else if (remainNumberTpm.compareTo(AHALF) == 0) {
				leaveMana = new LeaveManagementData(newID, cid, sid, true, null, GeneralDate.max(), 0.5d, 0, 0.5d, 0,
						DigestionAtr.UNUSED.value, aDay, aHalf);
				remainNumberTpm = remainNumberTpm.subtract(AHALF);
			}
			leaveManaDataRepository.create(leaveMana);
		}
	}
	
	/**
	 * In case of remain number is bigger than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainNumber
	 */
	private void remainNumberIsMoreThanZero(String cid, Map<String, BigDecimal> remainNumberMap) {
		
		Map<String, DesignatedTime> commonSetMap = getWorkTimeSetting(cid, new ArrayList<>(remainNumberMap.keySet()));
		List<LeaveManagementData> leaveManaLst = new ArrayList<>();
		remainNumberMap.entrySet().stream().forEach(c ->{
			BigDecimal remainNumberTpm = c.getValue();
			LeaveManagementData leaveMana = null;
			// 1日相当時間←指定時間．1日の時間
			// 半日相当時間←指定時間．半日の時間
			// TODO QA
			DesignatedTime commonSet = commonSetMap.get(c.getKey());
			String newID = null;
			int aDay = commonSet.getOneDayTime().v();
			int aHalf = commonSet.getHalfDayTime().v();
			while (remainNumberTpm.compareTo(ZERO) > 0) {
				newID = IdentifierUtil.randomUniqueId();
				if (remainNumberTpm.compareTo(ONE) >= 0) {
					leaveMana = new LeaveManagementData(newID, cid, c.getKey(), true, null, GeneralDate.max(), 1d, 0, 1d, 0,
							DigestionAtr.UNUSED.value, aDay, aHalf);
					remainNumberTpm = remainNumberTpm.subtract(ONE);
				} else if (remainNumberTpm.compareTo(AHALF) == 0) {
					leaveMana = new LeaveManagementData(newID, cid, c.getKey(), true, null, GeneralDate.max(), 0.5d, 0, 0.5d, 0,
							DigestionAtr.UNUSED.value, aDay, aHalf);
					remainNumberTpm = remainNumberTpm.subtract(AHALF);
				}
				leaveManaLst.add(leaveMana);
			}
		});
		
		if(!leaveManaLst.isEmpty()) {
			leaveManaDataRepository.addAll(leaveManaLst);
		}
	}

	/**
	 * In case of remain number is smaller than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainNumber
	 */
	private void remainNumberIsSmallerThanZero(String cid, String sid, BigDecimal remainNumber) {
		String newID = null;
		// 振休作成数=-1*パラメータ「振休残数」
		BigDecimal remainNumberTpm = remainNumber.multiply(new BigDecimal(-1));
		CompensatoryDayOffManaData comDayOff = null;

		while (remainNumberTpm.compareTo(ZERO) > 0) {
			newID = IdentifierUtil.randomUniqueId();
			if (remainNumberTpm.compareTo(ONE) >= 0) {
				/**
				 * 社員ID←パラメータ「社員ID」 休出日←日付不明=true 使用期限日←9999/12/31 振休消化区分←未消化
				 */
				comDayOff = new CompensatoryDayOffManaData(newID, cid, sid, true, null, 1d, 0, 1d, 0);
				remainNumberTpm = remainNumberTpm.subtract(ONE);
			} else if (remainNumberTpm.compareTo(AHALF) == 0) {
				comDayOff = new CompensatoryDayOffManaData(newID, cid, sid, true, null, 0.5d, 0, 0.5d, 0);
				remainNumberTpm = remainNumberTpm.subtract(AHALF);
			}
			comDayOffManaDataRepository.create(comDayOff);
		}
	}
	
	/**
	 * In case of remain number is smaller than zero
	 * 
	 * @param cid
	 * @param sid
	 * @param remainNumber
	 */
	private void remainNumberIsSmallerThanZero(String cid, Map<String, BigDecimal> remainNumber) {
		List<CompensatoryDayOffManaData> comDayOffLst = new ArrayList<>();
		remainNumber.entrySet().stream().forEach(c ->{
			String newID = null;
			// 振休作成数=-1*パラメータ「振休残数」
			BigDecimal remainNumberTpm = c.getValue().multiply(new BigDecimal(-1));
			CompensatoryDayOffManaData comDayOff = null;

			while (remainNumberTpm.compareTo(ZERO) > 0) {
				newID = IdentifierUtil.randomUniqueId();
				if (remainNumberTpm.compareTo(ONE) >= 0) {
					/**
					 * 社員ID←パラメータ「社員ID」 休出日←日付不明=true 使用期限日←9999/12/31 振休消化区分←未消化
					 */
					comDayOff = new CompensatoryDayOffManaData(newID, cid, c.getKey(), true, null, 1d, 0, 1d, 0);
					remainNumberTpm = remainNumberTpm.subtract(ONE);
				} else if (remainNumberTpm.compareTo(AHALF) == 0) {
					comDayOff = new CompensatoryDayOffManaData(newID, cid, c.getKey(), true, null, 0.5d, 0, 0.5d, 0);
					remainNumberTpm = remainNumberTpm.subtract(AHALF);
				}
				comDayOffLst.add(comDayOff);
			}
		});
		
		if(!comDayOffLst.isEmpty()) {
			comDayOffManaDataRepository.addAll(comDayOffLst);
		}
	}

	/**
	 * For Item IS00366 LeaveManagementData
	 * 
	 * @param sid
	 * @return 指定時間
	 */
	// TODO Pending QA
	private DesignatedTime getWorkTimeSetting(String cid, String sid) {

		DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));

		Optional<WorkingCondition> workingCond = workingConditionRepository.getBySid(cid, sid);
		if (!workingCond.isPresent() || workingCond.get().getDateHistoryItem().isEmpty()) {
			return result;
		}
		DateHistoryItem histItem = workingCond.get().getDateHistoryItem()
				.get(workingCond.get().getDateHistoryItem().size() - 1);
		Optional<WorkingConditionItem> workingCondItem = workingConditionItemRepository.getBySidAndHistId(sid,
				histItem.identifier());
		if (!workingCondItem.isPresent()) {
			return result;
		}
		// 労働条件項目．区分別勤務．休日出勤時．就業時間帯コードの就業時間帯
		Optional<WorkTimeCode> workTimeCD = workingCondItem.get().getWorkCategory().getWorkTime().getHolidayWork().getWorkTimeCode();

		if (!workTimeCD.isPresent()) {
			return getCompanySet(cid);
		}
		Optional<WorkTimeSetting> workTimeSet = workTimeSettingRepository.findByCode(cid, workTimeCD.get().v());

		if (!workTimeSet.isPresent()) {
			return getCompanySet(cid);
		}

		// Flexible time
		if (workTimeSet.get().getWorkTimeDivision().getWorkTimeDailyAtr().isFlex()) {
			getFlexTime(cid, workTimeCD.get().v());
		} else {
			switch (workTimeSet.get().getWorkTimeDivision().getWorkTimeMethodSet()) {
			case FIXED_WORK:
				return getFixedWork(cid, workTimeCD.get().v());
			case FLOW_WORK:
				return getFlowWork(cid, workTimeCD.get().v());
			case DIFFTIME_WORK:
				return getDiffTimeWork(cid, workTimeCD.get().v());
			default:
				break;
			}

		}

		return result;
	}
	

	/**
	 * @author lanlt
	 * For Item IS00366 LeaveManagementData
	 * 
	 * @param sid
	 * @return 指定時間
	 */
	// TODO Pending QA
	private Map<String, DesignatedTime> getWorkTimeSetting(String cid, List<String> sids) {
		
		List<String> histIds = new ArrayList<>();
		Map<String, String> workTimes = new HashMap<>();
		Map<String, DesignatedTime> result = new HashMap<>();
		
		List<WorkingCondition> workingCondLst = workingConditionRepository.getBySidsAndCid(cid, sids);

		sids.stream().forEach(c ->{
			Optional<WorkingCondition> workingCondOpt = workingCondLst.stream().filter(item -> item.getEmployeeId().equals(c)).findFirst();
			if (!workingCondOpt.isPresent() || workingCondOpt.get().getDateHistoryItem().isEmpty()) {
				result.put(c, new DesignatedTime(new OneDayTime(0), new OneDayTime(0)));
				return;
			}
			
			DateHistoryItem histItem = workingCondOpt.get().getDateHistoryItem()
					.get(workingCondOpt.get().getDateHistoryItem().size() - 1);
			histIds.add(histItem.identifier());
		});
		
		DesignatedTime designatedTimeCid = getCompanySet(cid);
		List<WorkingConditionItem> workingCondItemLst  = workingConditionItemRepository.getByListHistoryID(histIds);
		
		sids.stream().forEach(c ->{
			Optional<WorkingConditionItem> workingCondItemOpt = workingCondItemLst.stream().filter(item -> item.getEmployeeId().equals(c)).findFirst();
			if (!workingCondItemOpt.isPresent()) {
				result.put(c, new DesignatedTime(new OneDayTime(0), new OneDayTime(0)));
				return;
			}
			
			// 労働条件項目．区分別勤務．休日出勤時．就業時間帯コードの就業時間帯
			Optional<WorkTimeCode> workTimeCD = workingCondItemOpt.get().getWorkCategory().getWorkTime().getHolidayWork().getWorkTimeCode();
			if (!workTimeCD.isPresent()) {
				result.put(c, designatedTimeCid);
				return;
			}
			workTimes.put(c, workTimeCD.get().v());
		});
		
		List<WorkTimeSetting> workTimeSettings = workTimeSettingRepository.findByCodes(cid, workTimes.values().stream().collect(Collectors.toList()));
		List<String> flexTimes = new ArrayList<>();
		List<String> fixedWorks = new ArrayList<>();
		List<String> flowWorks = new ArrayList<>();
		List<String> diffTimeWorks = new ArrayList<>();
		workTimes.entrySet().stream().forEach(c ->{
			Optional<WorkTimeSetting>  workTimeSettingOpt = workTimeSettings.stream().filter(item -> item.getWorktimeCode().v().equals(c.getValue())).findFirst();
			if (!workTimeSettingOpt.isPresent()) {
				result.put(c.getKey(), designatedTimeCid);
				return;
			}
			
			// Flexible time
			if (workTimeSettingOpt.get().getWorkTimeDivision().getWorkTimeDailyAtr().isFlex()) {
				flexTimes.add(c.getValue());
			} else {
				switch (workTimeSettingOpt.get().getWorkTimeDivision().getWorkTimeMethodSet()) {
				case FIXED_WORK:
					fixedWorks.add(c.getValue());
					return;
				case FLOW_WORK:
					flowWorks.add(c.getValue());
					return;
				case DIFFTIME_WORK:
					diffTimeWorks.add(c.getValue());
					return;
				default:
					return;
				}

			}
		});

		if(!flexTimes.isEmpty()) {
			List<String> flexFilterTimes = flexTimes.stream().distinct().collect(Collectors.toList());
			Map<String, DesignatedTime> flexDesignatedTime = getFlexTime(cid, flexFilterTimes);
			workTimes.entrySet().stream().forEach(c ->{
				DesignatedTime flexTime = flexDesignatedTime.get(c.getValue());
				result.put(c.getKey(), flexTime);
				
			});
		}
		
		if(!fixedWorks.isEmpty()) {
			List<String> fixedFilterWorks = fixedWorks.stream().distinct().collect(Collectors.toList());
			Map<String, DesignatedTime> fixedFilterWork = getFixedWork(cid, fixedFilterWorks);
			workTimes.entrySet().stream().forEach(c ->{
				DesignatedTime fixedWork = fixedFilterWork.get(c.getValue());
				result.put(c.getKey(), fixedWork);
				
			});
		}
		
		if(!flowWorks.isEmpty()) {
			List<String> flowFilterWorks = flowWorks.stream().distinct().collect(Collectors.toList());
			Map<String, DesignatedTime> flowFilterWork = getFlowWork(cid, flowFilterWorks);
			workTimes.entrySet().stream().forEach(c ->{
				DesignatedTime flowWork = flowFilterWork.get(c.getValue());
				result.put(c.getKey(), flowWork);
				
			});
		}
		
		if(!diffTimeWorks.isEmpty()) {
			List<String> diffTimeFilterWorks = diffTimeWorks.stream().distinct().collect(Collectors.toList());
			Map<String, DesignatedTime> diffTimeWorksMap = getDiffTimeWork(cid, diffTimeFilterWorks);
			workTimes.entrySet().stream().forEach(c ->{
				DesignatedTime flowWork = diffTimeWorksMap.get(c.getValue());
				result.put(c.getKey(), flowWork);
			});
		}
		
		return result;
	}
	
	/**
	 * Get flexible time
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private DesignatedTime getFlexTime(String cid, String workTimeCD) {
		DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));
		Optional<FlexWorkSetting> flexWork = flexWorkSettingRepository.find(cid, workTimeCD);
		if (!flexWork.isPresent()) {
			return result;
		}
		Optional<WorkTimezoneOtherSubHolTimeSet> subHolTimeSet = flexWork.get().getCommonSetting().getSubHolTimeSet()
				.stream().filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
						&& (i.getSubHolTimeSet().isUseDivision() == true))
				.findFirst();
		if (subHolTimeSet.isPresent()) {
			if (subHolTimeSet.get().getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
				return subHolTimeSet.get().getSubHolTimeSet().getDesignatedTime();
			} else {
				return new DesignatedTime(subHolTimeSet.get().getSubHolTimeSet().getCertainTime(), new OneDayTime(0));
			}
		}
		return getCompanySet(cid);
	}
	
	/**
	 * Get flexible time
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private Map<String, DesignatedTime> getFlexTime(String cid, List<String> workTimeCDs) {
		//DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));
		DesignatedTime designatedTimeCid = getCompanySet(cid);
		Map<String, DesignatedTime> result = new HashMap<>();
		List<FlexWorkSetting> flexWorks = flexWorkSettingRepository.getAllByCidAndWorkCodes(cid, workTimeCDs);
		workTimeCDs.stream().forEach(c -> {
			
			Optional<FlexWorkSetting> flexWorkOpt = flexWorks.stream()
					.filter(item -> item.getWorkTimeCode().v().equals(c)).findFirst();
			
			if (!flexWorkOpt.isPresent()) {
				result.put(c, new DesignatedTime(new OneDayTime(0), new OneDayTime(0)));
				return;
			}
			
			Optional<WorkTimezoneOtherSubHolTimeSet> subHolTimeSet = flexWorkOpt.get().getCommonSetting()
					.getSubHolTimeSet().stream()
					.filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
							&& (i.getSubHolTimeSet().isUseDivision() == true))
					.findFirst();
			
			if (subHolTimeSet.isPresent()) {
				if (subHolTimeSet.get().getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
					result.put(c, subHolTimeSet.get().getSubHolTimeSet().getDesignatedTime());
					return;
				} else {
					result.put(c, new DesignatedTime(subHolTimeSet.get().getSubHolTimeSet().getCertainTime(),
							new OneDayTime(0)));
					return;
				}
			}

			result.put(c, designatedTimeCid);

		});
		
		return result;
	}

	/**
	 * 会社別代休時間設定
	 * 
	 * @param cid
	 * @return
	 */
	private DesignatedTime getCompanySet(String cid) {
		DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));

		CompensatoryLeaveComSetting comSet = compensLeaveComSetRepository.find(cid);
		if (comSet != null) {
			return comSet.getSubstituteHolidaySetting().getHolidayWorkHourRequired().getTimeSetting().getDesignatedTime();
//			Optional<CompensatoryOccurrenceSetting> comCurrentSetOptional = comSet.getCompensatoryOccurrenceSetting().stream()
//					.filter(i -> i.getOccurrenceType().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value)
//					.findFirst();
//			if (comCurrentSetOptional.isPresent()) {
//				CompensatoryOccurrenceSetting comCurrentSet = comCurrentSetOptional.get();
//				if(comCurrentSet.getTransferSetting().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
//					return comCurrentSet.getTransferSetting().getDesignatedTime();
//				}else {
//					return new  DesignatedTime(comCurrentSet.getTransferSetting().getCertainTime(), new OneDayTime(0));
//				}
//			}
		}
		return result;
	}

	/**
	 * ドメインモデル「固定勤務設定」を取得
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private DesignatedTime getFixedWork(String cid, String workTimeCD) {
		DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));
		Optional<FixedWorkSetting> fixedWork = fixedWorkSettingRepository.findByKey(cid, workTimeCD);
		if (!fixedWork.isPresent()) {
			return result;
		}
		Optional<WorkTimezoneOtherSubHolTimeSet> workTimeOptional = fixedWork.get().getCommonSetting()
				.getSubHolTimeSet().stream()
				.filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
						&& (i.getSubHolTimeSet().isUseDivision() == true))
				.findFirst();

		if (!workTimeOptional.isPresent()) {
			return getCompanySet(cid);
		}

		WorkTimezoneOtherSubHolTimeSet workTime = workTimeOptional.get();
		if (workTime.getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
			return workTime.getSubHolTimeSet().getDesignatedTime();
		} else {
			return new DesignatedTime(workTime.getSubHolTimeSet().getCertainTime(), new OneDayTime(0));
		}
	}
	
	/**
	 * ドメインモデル「固定勤務設定」を取得
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private Map<String, DesignatedTime> getFixedWork(String cid, List<String> workTimeCDs) {
		Map<String, DesignatedTime> result = new HashMap<>();
		List<FixedWorkSetting> fixedWorks = fixedWorkSettingRepository.findByCidAndWorkTimeCodes(cid, workTimeCDs);
		DesignatedTime designatedTimeCid = getCompanySet(cid);
		workTimeCDs.stream().forEach(c -> {

			Optional<FixedWorkSetting> fixedWorkOpt = fixedWorks.stream()
					.filter(item -> item.getWorkTimeCode().v().equals(c)).findFirst();
			if (!fixedWorkOpt.isPresent()) {
				result.put(c, new DesignatedTime(new OneDayTime(0), new OneDayTime(0)));
				return;
			}

			Optional<WorkTimezoneOtherSubHolTimeSet> workTimeOptional = fixedWorkOpt.get().getCommonSetting()
					.getSubHolTimeSet().stream()
					.filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
							&& (i.getSubHolTimeSet().isUseDivision() == true))
					.findFirst();

			if (!workTimeOptional.isPresent()) {
				result.put(c, designatedTimeCid);
				return;
			}

			WorkTimezoneOtherSubHolTimeSet workTime = workTimeOptional.get();
			if (workTime.getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
				result.put(c, workTime.getSubHolTimeSet().getDesignatedTime());
				return;
			} else {
				result.put(c, new DesignatedTime(workTime.getSubHolTimeSet().getCertainTime(), new OneDayTime(0)));
				return;
			}
		});
		return result;
	}

	/**
	 * ドメインモデル「流動勤務設定」を取得
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private DesignatedTime getFlowWork(String cid, String workTimeCD) {
		DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));
		Optional<FlowWorkSetting> flowWork = flowWorkSettingRepository.find(cid, workTimeCD);
		if (!flowWork.isPresent()) {
			return result;
		}
		Optional<WorkTimezoneOtherSubHolTimeSet> workTimeOptional = flowWork.get().getCommonSetting().getSubHolTimeSet()
				.stream().filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
						&& (i.getSubHolTimeSet().isUseDivision() == true))
				.findFirst();

		if (!workTimeOptional.isPresent()) {
			return getCompanySet(cid);
		}
		WorkTimezoneOtherSubHolTimeSet workTime = workTimeOptional.get();
		if (workTime.getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
			return workTime.getSubHolTimeSet().getDesignatedTime();
		} else {
			return new DesignatedTime(workTime.getSubHolTimeSet().getCertainTime(), new OneDayTime(0));
		}
	}
	
	/**
	 * ドメインモデル「流動勤務設定」を取得
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private Map<String, DesignatedTime> getFlowWork(String cid, List<String> workTimeCDs) {
		Map<String, DesignatedTime> result = new HashMap<>();
		List<FlowWorkSetting> flowWorks = flowWorkSettingRepository.findByCidAndWorkCodes(cid, workTimeCDs);
		DesignatedTime designatedTimeCid = getCompanySet(cid);
		workTimeCDs.stream().forEach(c -> {
			Optional<FlowWorkSetting> flowWorkOpt = flowWorks.stream()
					.filter(item -> item.getWorkingCode().v().equals(c)).findFirst();
			if (!flowWorkOpt.isPresent()) {
				result.put(c, new DesignatedTime(new OneDayTime(0), new OneDayTime(0)));
				return;
			}

			Optional<WorkTimezoneOtherSubHolTimeSet> workTimeOptional = flowWorkOpt.get().getCommonSetting()
					.getSubHolTimeSet().stream()
					.filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
							&& (i.getSubHolTimeSet().isUseDivision() == true))
					.findFirst();

			if (!workTimeOptional.isPresent()) {
				result.put(c, designatedTimeCid);
				return;
			}

			WorkTimezoneOtherSubHolTimeSet workTime = workTimeOptional.get();
			if (workTime.getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
				result.put(c, workTime.getSubHolTimeSet().getDesignatedTime());
				return;
			} else {
				result.put(c, new DesignatedTime(workTime.getSubHolTimeSet().getCertainTime(), new OneDayTime(0)));
				return;
			}
		});

		return result;
	}

	/**
	 * ドメインモデル「時差勤務設定」を取得
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private DesignatedTime getDiffTimeWork(String cid, String workTimeCD) {
		DesignatedTime result = new DesignatedTime(new OneDayTime(0), new OneDayTime(0));
		Optional<DiffTimeWorkSetting> diffTime = diffTimeWorkSettingRepository.find(cid, workTimeCD);
		if (!diffTime.isPresent()) {
			return result;
		}
		Optional<WorkTimezoneOtherSubHolTimeSet> workTimeOptional = diffTime.get().getCommonSet().getSubHolTimeSet()
				.stream().filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
						&& (i.getSubHolTimeSet().isUseDivision() == true))
				.findFirst();

		if (!workTimeOptional.isPresent()) {
			return getCompanySet(cid);
		}
		WorkTimezoneOtherSubHolTimeSet workTime = workTimeOptional.get();
		if (workTime.getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
			return workTime.getSubHolTimeSet().getDesignatedTime();
		} else {
			return new DesignatedTime(workTime.getSubHolTimeSet().getCertainTime(), new OneDayTime(0));
		}
	}
	
	/**
	 * ドメインモデル「時差勤務設定」を取得
	 * 
	 * @param cid
	 * @param workTimeCD
	 * @return
	 */
	private Map<String, DesignatedTime> getDiffTimeWork(String cid, List<String> workTimeCDs) {
		Map<String, DesignatedTime> result = new HashMap<>();
		List<DiffTimeWorkSetting> diffTimes = diffTimeWorkSettingRepository.findByCidAndWorkCodes(cid, workTimeCDs);
		DesignatedTime designatedTimeCid = getCompanySet(cid);
		workTimeCDs.stream().forEach(c -> {
			Optional<DiffTimeWorkSetting> diffTimeOpt = diffTimes.stream()
					.filter(item -> item.getWorkTimeCode().v().equals(c)).findFirst();

			if (!diffTimeOpt.isPresent()) {
				result.put(c, new DesignatedTime(new OneDayTime(0), new OneDayTime(0)));
				return;
			}

			Optional<WorkTimezoneOtherSubHolTimeSet> workTimeOptional = diffTimeOpt.get().getCommonSet()
					.getSubHolTimeSet().stream()
					.filter(i -> i.getOriginAtr().value == CompensatoryOccurrenceDivision.WorkDayOffTime.value
							&& (i.getSubHolTimeSet().isUseDivision() == true))
					.findFirst();

			if (!workTimeOptional.isPresent()) {
				result.put(c, designatedTimeCid);
				return;
			}

			WorkTimezoneOtherSubHolTimeSet workTime = workTimeOptional.get();
			if (workTime.getSubHolTimeSet().getSubHolTransferSetAtr().isSpecifiedTimeSubHol()) {
				result.put(c, workTime.getSubHolTimeSet().getDesignatedTime());
				return;
			} else {
				result.put(c, new DesignatedTime(workTime.getSubHolTimeSet().getCertainTime(), new OneDayTime(0)));
				return;
			}
		});

		return result;
	}

	public void updateOtherHolidayInfo(String cid, Map<String, OtherHolidayInfoInter> otherHolidayInfos) {

		List<ExcessLeaveInfo> exLeavLst = new ArrayList<ExcessLeaveInfo>();
		List<PublicHolidayRemain> pubHDLst  = new ArrayList<PublicHolidayRemain>();
		Map<String, BigDecimal> remainLeftMap = new HashMap<>();
		Map<String, BigDecimal> remainNumberMap = new HashMap<>();
       
        otherHolidayInfos.entrySet().stream().forEach(c ->{
        	pubHDLst.add(c.getValue().getPubHD());
        	exLeavLst.add(c.getValue().getExLeav());
        	remainNumberMap.put(c.getKey(), c.getValue().getRemainNumber());
        	remainLeftMap.put(c.getKey(), c.getValue().getRemainLeft());
        });
        
        excessLeaveInfoRepository.updateAll(exLeavLst);
		publicHolidayRemainRepository.updateAll(pubHDLst);
		
		Map<String, Boolean> checkEnableLeaveMap = checkEnableLeaveMan(new ArrayList<>(otherHolidayInfos.keySet()));
		Map<String, Boolean> checkEnablePayoutMaP = checkEnablePayout(new ArrayList<>(otherHolidayInfos.keySet()));

		Map<String, BigDecimal> remainLeftFinalMap = new HashMap<>();
		Map<String, BigDecimal> remainNumberFinalMap = new HashMap<>();
		// Item IS00366
		checkEnableLeaveMap.entrySet().stream().forEach(c ->{
			if(c.getValue()!= null) {
				if(c.getValue().booleanValue()) {
					BigDecimal remainNumber = remainNumberMap.get(c.getKey());
					if (remainNumber != null) {
						remainNumberFinalMap.put(c.getKey(), remainNumber);
					}
				}
			}
		});
		
		// Item IS00368
		checkEnablePayoutMaP.entrySet().stream().forEach(c ->{
			if(c.getValue()!= null) {
				if(c.getValue().booleanValue()) {
					BigDecimal remainLeft = remainLeftMap.get(c.getKey());
					if (remainLeft != null) {
						remainLeftFinalMap.put(c.getKey(), remainLeft);
					}
				}
			}
		});
		
		if(!remainNumberFinalMap.isEmpty()) {
			setRemainNumber(cid, remainNumberFinalMap);
		}
		
		if(!remainLeftFinalMap.isEmpty()) {
			setRemainLeftItem(cid, remainLeftFinalMap);
		}

	}
	
	public void updateOtherHolidayInfo(String cid, PublicHolidayRemain pubHD, ExcessLeaveInfo exLeav,
			BigDecimal remainNumber, BigDecimal remainLeft) {

		publicHolidayRemainRepository.update(pubHD);
		excessLeaveInfoRepository.update(exLeav);

		String sid = pubHD.getSID();
		// Item IS00366
		if (checkEnableLeaveMan(sid)) {
			if (remainNumber != null) {
				setRemainNumber(cid, sid, remainNumber);
			}
		}

		// Item IS00368
		if (checkEnablePayout(sid)) {
			if (remainLeft != null) {
				setRemainLeftItem(cid, sid, remainLeft);
			}
		}

	}

	/**
	 * IS00366 check Enable
	 * 
	 * @param cid
	 * @param sid
	 * @return
	 */
	public boolean checkEnableLeaveMan(String sid) {
		String cid = AppContexts.user().companyId();
		if (comDayOffManaDataRepository.getBySid(cid, sid).isEmpty()
				&& leaveManaDataRepository.getBySid(cid, sid).isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * IS00366 check Enable
	 * 
	 * @param cid
	 * @param sids
	 * @return
	 */
	public Map<String, Boolean> checkEnableLeaveMan(List<String> sids) {
		Map<String, Boolean> result = new HashMap<>();
		String cid = AppContexts.user().companyId();
		Map<String, List<CompensatoryDayOffManaData>> comDayOffMap = comDayOffManaDataRepository.getBySidsAndCid(cid,  sids).stream().collect(Collectors.groupingBy(c -> c.getSID()));
		Map<String, List<LeaveManagementData>> leaveManaMap = leaveManaDataRepository.getBySidsAndCid(cid,  sids).stream().collect(Collectors.groupingBy(c -> c.getSID()));
		sids.stream().forEach(c ->{
			List<CompensatoryDayOffManaData> comDayOff = comDayOffMap.get(c);
			List<LeaveManagementData> leaveMana = leaveManaMap.get(c);
			if((comDayOff == null || comDayOff.isEmpty()) && (leaveMana == null || leaveMana.isEmpty())) {
				result.put(c, new Boolean(true));
			}else {
				result.put(c, new Boolean(false));
			}
		});
		return result;
	}

	/**
	 * IS00368 check Enable
	 * 
	 * @param sid
	 * @return
	 */
	public boolean checkEnablePayout(String sid) {
		String cid = AppContexts.user().companyId();
		if (substitutionOfHDManaDataRepository.getBysiD(cid, sid).isEmpty()
				&& payoutManagementDataRepository.getSid(cid, sid).isEmpty()) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * IS00368 check Enable
	 * 
	 * @param sid
	 * @return
	 */
	public Map<String, Boolean> checkEnablePayout(List<String> sids) {
		String cid = AppContexts.user().companyId();
		Map<String, Boolean> result = new HashMap<>();
		Map<String, List<SubstitutionOfHDManagementData>> substitutionOfHDManaDataMap = substitutionOfHDManaDataRepository.getBySidsAndCid(cid, sids).stream().collect(Collectors.groupingBy(c -> c.getSID()));
		Map<String, List<PayoutManagementData>> payoutManagementDataMap =  payoutManagementDataRepository.getBySidsAndCid(cid, sids).stream().collect(Collectors.groupingBy(c -> c.getSID()));
		sids.stream().forEach(c ->{
			List<SubstitutionOfHDManagementData> substitutionOfHDManaData = substitutionOfHDManaDataMap.get(c);
			List<PayoutManagementData> payoutManagementData = payoutManagementDataMap.get(c);
			if((substitutionOfHDManaData == null || substitutionOfHDManaData.isEmpty()) && (payoutManagementData == null || payoutManagementData.isEmpty())) {
				result.put(c, new Boolean(true));
			}else {
				result.put(c, new Boolean(false));
			}
		});
		return result;
	}
	
	/**
	 * IS00366 getRemainDays
	 */
	public BigDecimal getRemainDays(String sid) {
		String cid = AppContexts.user().companyId();
		// 取得した「休出管理データ」の未使用日数を合計
		Double sumUnUsedDay = leaveManaDataRepository
				.getBySidWithsubHDAtr(cid, sid, DigestionAtr.UNUSED.value).stream()
				.mapToDouble(i -> i.getUnUsedDays().v()).sum();
		// 取得した「代休管理データ」の未相殺日数を合計
		Double sumRemain = comDayOffManaDataRepository.getBySidWithReDay(cid, sid).stream()
				.mapToDouble(i -> i.getRemainDays().v()).sum();
		return new BigDecimal(sumUnUsedDay - sumRemain);
	}
	
	/**
	 * IS00368 getRemainLeft
	 */
	public BigDecimal getRemainLeft(String sid) {
		String cid = AppContexts.user().companyId();
		// 取得した「振出管理データ」の未使用日数を合計
		Double sumUnUsedDay = payoutManagementDataRepository
				.getSidWithCod(cid,sid, DigestionAtr.UNUSED.value).stream()
				.mapToDouble(i -> i.getUnUsedDays().v()).sum();
		// 取得した「振休管理データ」の未相殺日数を合計
		Double sumRemain = substitutionOfHDManaDataRepository.getBysiDRemCod(cid, sid).stream()
				.mapToDouble(i -> i.getRemainDays().v()).sum();
		return new BigDecimal(sumUnUsedDay - sumRemain);
	}
}
