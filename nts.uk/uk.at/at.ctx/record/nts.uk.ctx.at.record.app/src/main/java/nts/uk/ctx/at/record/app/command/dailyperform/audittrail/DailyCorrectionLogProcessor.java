package nts.uk.ctx.at.record.app.command.dailyperform.audittrail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.classification.RecClassificationAdapter;
import nts.uk.ctx.at.record.dom.adapter.employment.SyEmploymentAdapter;
import nts.uk.ctx.at.record.dom.adapter.jobtitle.RecJobTitleAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.AutomaticCalcAfterHours;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.DoWork;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.ReasonGoOut;
import nts.uk.ctx.at.record.dom.divergence.time.reason.DivergenceReasonSelectRepository;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.security.audittrail.correction.content.CorrectionAttr;
import nts.uk.shr.com.security.audittrail.correction.content.ItemInfo;
import nts.uk.shr.com.security.audittrail.correction.processor.CorrectionLogProcessorContext;
import nts.uk.shr.com.security.audittrail.correction.processor.CorrectionProcessorId;
import nts.uk.shr.com.security.audittrail.correction.processor.DataCorrectionLogProcessor;

@Stateless
public class DailyCorrectionLogProcessor extends DataCorrectionLogProcessor {
	
	static final Integer[] DEVIATION_REASON  = {436, 438, 439, 441, 443, 444, 446, 448, 449, 451, 453, 454, 456, 458, 459, 799, 801, 802, 804, 806, 807, 809, 811, 812, 814, 816, 817, 819, 821, 822};

	final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
	@Inject
	private WorkTypeRepository workTypeRepository;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	@Inject
	private RecClassificationAdapter recClassificationAdapter;
	@Inject
	private SyEmploymentAdapter syEmploymentAdapter;
	@Inject
	private RecJobTitleAdapter recJobTitleAdapter;
	@Inject
	private SyWorkplaceAdapter syWorkplaceAdapter;
	@Inject
	private WorkLocationRepository workLocationRepository;
	@Inject
	private DivergenceReasonSelectRepository divergenceReasonSelectRepository;
	
	public static final Map<Integer, Integer> DEVIATION_REASON_MAP = IntStream.range(0, DEVIATION_REASON.length-1).boxed().collect(Collectors.toMap(x -> DEVIATION_REASON[x], x -> x/3 +1));

	@Override
	public CorrectionProcessorId getId() {
		return CorrectionProcessorId.DAILY;
	}

	@Override
	protected void buildLogContents(CorrectionLogProcessorContext context) {
		
		DailyCorrectionLogParameter parameter = context.getParameter();
		Map<Integer, DPAttendanceItemRC> lstAttendanceItem = parameter.getLstAttendanceItem();
		String companyId = AppContexts.user().companyId();
		String contractCode = AppContexts.user().contractCode();
		ItemInfo itemInfo = new ItemInfo(null, null, null, null);
		List<GeneralDate> listDate =  new ArrayList<>();
		List<String> listWorkTypeCd =  new ArrayList<>();
		List<String> listWorkTimeCd =  new ArrayList<>();
		List<String> listClassCd =  new ArrayList<>();
		List<String> listEmploymentCd =  new ArrayList<>();
		List<String> listJobId =  new ArrayList<>();
		List<String> listWorkplaceId =  new ArrayList<>();
		List<String> listWorkLocationCd =  new ArrayList<>();
		List<String> listReasonCd =  new ArrayList<>();
		
		// get list code or list id
		for (val target : parameter.getTargets()) {
			listDate.add(target.getDate());
			for (val correctedItem : target.getCorrectedItems()) {
				// neu khong co itemName thi k ghi log
				if (correctedItem.getItemName() == null) {
					continue;
				}

				DPAttendanceItemRC dPAttendanceItemRC = lstAttendanceItem.get(correctedItem.getItemNo());

				if (dPAttendanceItemRC != null && dPAttendanceItemRC.getAttendanceAtr().intValue() == 0) {
					switch (dPAttendanceItemRC.getTypeGroup().intValue()) {
					case 1:
						listWorkTypeCd.add(correctedItem.getBefore());
						listWorkTypeCd.add(correctedItem.getAfter());
						break;
					case 2:
						listWorkTimeCd.add(correctedItem.getBefore());
						listWorkTimeCd.add(correctedItem.getAfter());
						break;
					case 3:
						listWorkLocationCd.add(correctedItem.getBefore());
						listWorkLocationCd.add(correctedItem.getAfter());
						break;
					case 4:
						listReasonCd.add(correctedItem.getBefore());
						listReasonCd.add(correctedItem.getAfter());
						break;
					case 5:
						listWorkplaceId.add(correctedItem.getBefore());
						listWorkplaceId.add(correctedItem.getAfter());
						break;
					case 6:
						listClassCd.add(correctedItem.getBefore());
						listClassCd.add(correctedItem.getAfter());
						break;
					case 7:
						listJobId.add(correctedItem.getBefore());
						listJobId.add(correctedItem.getAfter());
						break;
					case 8:
						listEmploymentCd.add(correctedItem.getBefore());
						listEmploymentCd.add(correctedItem.getAfter());
						break;
					default:
						break;
					}
				}
			}
		}
		
		// remove value = null in list
		List<String> listWorkTypeCode = listWorkTypeCd.stream().filter(x -> x != null).collect(Collectors.toList());
		List<String> listWorkTimeCode = listWorkTimeCd.stream().filter(x -> x != null).collect(Collectors.toList());
		List<String> listClassCode = listClassCd.stream().filter(x-> x != null).collect(Collectors.toList());
		List<String> listEmploymCode = listEmploymentCd.stream().filter(x-> x != null).collect(Collectors.toList());
		List<String> listJobIdd = listJobId.stream().filter(x-> x != null).collect(Collectors.toList());
		List<String> listWorkplaceIdd = listWorkplaceId.stream().filter(x-> x != null).collect(Collectors.toList());
		List<String> listWorklocationCd = listWorkLocationCd.stream().filter(x-> x != null).collect(Collectors.toList());
		List<String> listReasonCode = listReasonCd.stream().filter(x-> x != null).collect(Collectors.toList());
			
		// map Code/Id-Name
		Map<String, String> listWorkTypeCodeName = listWorkTypeCode.size() > 0
				? workTypeRepository.getCodeNameWorkType(companyId, listWorkTypeCode) : new HashMap<>();
		Map<String, String> listWorkTimeCodeName = listWorkTimeCode.size() > 0
				? workTimeSettingRepository.getCodeNameByListWorkTimeCd(companyId, listWorkTimeCode) : new HashMap<>();
		Map<String, String> listClassification = listClassCode.size() > 0
				? recClassificationAdapter.getClassificationMapCodeName(companyId, listClassCode) : new HashMap<>();
		Map<String, String> listEmp = listEmploymCode.size() > 0
				? syEmploymentAdapter.getEmploymentMapCodeName(companyId, listEmploymCode) : new HashMap<>();
		Map<Pair<String, GeneralDate>, Pair<String, String>> listJobTitleIdDateName = (listJobIdd.size() > 0
				&& listDate.size() > 0)
						? recJobTitleAdapter.getJobTitleMapIdBaseDateName(companyId, listJobIdd, listDate)
						: new HashMap<>();
		Map<String, Pair<String, String>> listWorkplaceIdDateName = (listWorkplaceIdd.size() > 0
				&& listDate.size() > 0)
						? syWorkplaceAdapter.getWorkplaceMapCodeBaseDateName(companyId, listWorkplaceIdd, listDate)
						: new HashMap<>();	
		Map<String, String> listWorklocation = listWorklocationCd.size() > 0
				? workLocationRepository.getNameByCode(contractCode, listWorklocationCd) : new HashMap<>();

		Map<Pair<String,String>, String> listReason = listReasonCode.size() > 0
				? divergenceReasonSelectRepository.getNameByCodeNo(companyId, listReasonCode) : new HashMap<>();
		
		// set code + name 
		for (val target : parameter.getTargets()) {
			GeneralDate date = target.getDate();
			for (val correctedItem : target.getCorrectedItems()) {
				// neu khong co itemName thi k ghi log
				if (correctedItem.getItemName() == null) {
					continue;
				}

				DPAttendanceItemRC dPAttendanceItemRC = lstAttendanceItem.get(correctedItem.getItemNo());

				if (dPAttendanceItemRC != null) {
					switch (dPAttendanceItemRC.getAttendanceAtr().intValue()) {
					case 0:
						switch (dPAttendanceItemRC.getTypeGroup().intValue()) {
						case 1:
							String workTypeCodeNameBefore = correctedItem.getBefore() != null
									? correctedItem.getBefore() + " "
											+ listWorkTypeCodeName.get(correctedItem.getBefore())
									: null;
							String workTypeCodeNameAfter = correctedItem.getAfter() != null ? correctedItem.getAfter()
									+ " " + listWorkTypeCodeName.get(correctedItem.getAfter()) : null;
							itemInfo = correctedItem.toItemInfo(workTypeCodeNameBefore, workTypeCodeNameAfter);
							break;
						case 2:
							String workTimeCodeNameBefore = correctedItem.getBefore() != null
									? correctedItem.getBefore() + " "
											+ listWorkTimeCodeName.get(correctedItem.getBefore())
									: null;
							String workTimeCodeNameAfter = correctedItem.getAfter() != null ? correctedItem.getAfter()
									+ " " + listWorkTimeCodeName.get(correctedItem.getAfter()) : null;
							itemInfo = correctedItem.toItemInfo(workTimeCodeNameBefore, workTimeCodeNameAfter);
							break;
						case 3:
							//TODO: Test
							String worklocationCodeNameBef = correctedItem.getBefore() != null ? correctedItem.getBefore() + " " + listWorklocation.get(correctedItem.getBefore()) : null;
							String worklocationCodeNameAft = correctedItem.getAfter() != null ? correctedItem.getAfter() + " " + listWorklocation.get(correctedItem.getAfter()) : null;
							itemInfo = correctedItem.toItemInfo(worklocationCodeNameBef, worklocationCodeNameAft);
							break;
						case 4:
							//TODO: Test and review
							ItemValue atItemInfo = AttendanceItemIdContainer.getIds(Arrays.asList(dPAttendanceItemRC.getId()), AttendanceItemType.DAILY_ITEM).get(0);
							String itemNo = getNumberEndOf(atItemInfo.path());
							String reasonNameBef = correctedItem.getBefore() != null ? correctedItem.getBefore() + " " + listReason.get(Pair.of(correctedItem.getBefore(), itemNo)) : null;
							String reasonNameAft = correctedItem.getAfter() != null ? correctedItem.getAfter() + " " + listReason.get(Pair.of(correctedItem.getAfter(), itemNo)) : null;
							itemInfo = correctedItem.toItemInfo(reasonNameBef, reasonNameAft);
							break;
						case 5:
							Pair<String, String> pairWpkCodeNameBef = listWorkplaceIdDateName.get(correctedItem.getBefore());
							Pair<String, String> pairWpkCodeNameAft = listWorkplaceIdDateName.get(correctedItem.getAfter());
							String workplaceNameBef = correctedItem.getBefore() != null ? pairWpkCodeNameBef == null ? null : (pairWpkCodeNameBef.getLeft() + " " + pairWpkCodeNameBef.getRight()) : null;
							String workplaceNameAft = correctedItem.getAfter() != null ? pairWpkCodeNameAft == null ? null : (pairWpkCodeNameAft.getLeft() + " " + pairWpkCodeNameAft.getRight()) : null;
							itemInfo = correctedItem.toItemInfo(workplaceNameBef, workplaceNameAft);
							break;
						case 6:
							String classificationCodeNameBef = correctedItem.getBefore() != null ? correctedItem.getBefore() + " " + listClassification.get(correctedItem.getBefore()) : null;
							String classificationCodeNameAft = correctedItem.getAfter() != null ? correctedItem.getAfter() + " " + listClassification.get(correctedItem.getAfter()) : null;
							itemInfo = correctedItem.toItemInfo(classificationCodeNameBef, classificationCodeNameAft);
							break;
						case 7:
							Pair<String, String> pairJobCodeNameBef = listJobTitleIdDateName.get(Pair.of(correctedItem.getBefore(), date));
							Pair<String, String> pairJobCodeNameAft = listJobTitleIdDateName.get(Pair.of(correctedItem.getAfter(), date));
							String jobIdCodeNameBef = correctedItem.getBefore() != null ? pairJobCodeNameBef.getLeft() + " " + pairJobCodeNameBef.getRight() : null;
							String jobIdCodeNameAft = correctedItem.getAfter() != null ? pairJobCodeNameAft.getLeft() + " " + pairJobCodeNameAft.getRight() : null;
							itemInfo = correctedItem.toItemInfo(jobIdCodeNameBef, jobIdCodeNameAft);
							break;
						case 8:
							String employmentCodeNameBef = correctedItem.getBefore() != null ? correctedItem.getBefore() + " " + listEmp.get(correctedItem.getBefore()) : null;
							String employmentCodeNameAft = correctedItem.getAfter() != null ? correctedItem.getAfter() + " " + listEmp.get(correctedItem.getAfter()) : null;
							itemInfo = correctedItem.toItemInfo(employmentCodeNameBef, employmentCodeNameAft);
							break;
						default:
							itemInfo = correctedItem.toItemInfo(correctedItem.getBefore(), correctedItem.getAfter());
							break;
						}
						break;

					case 4:
						switch (dPAttendanceItemRC.getTypeGroup().intValue()) {
						case 9:
							String nameDoWorkAtrBef = correctedItem.getBefore() == null ? null
									: correctedItem.getBefore() + " "
											+ DoWork.valueOf(Integer.valueOf(correctedItem.getBefore())).description;
							String nameDoWorkAtrAft = correctedItem.getAfter() == null ? null
									: correctedItem.getAfter() + " "
											+ DoWork.valueOf(Integer.valueOf(correctedItem.getAfter())).description;
							itemInfo = correctedItem.toItemInfo(nameDoWorkAtrBef, nameDoWorkAtrAft);
							break;
						case 10:
							String nameAutomaticCalcAfterHoursAtrBef = correctedItem.getBefore() == null ? null
									: correctedItem.getBefore() + " " + AutomaticCalcAfterHours
											.valueOf(Integer.valueOf(correctedItem.getBefore())).description;
							String nameAutomaticCalcAfterHoursAtrAtf = correctedItem.getAfter() == null ? null
									: correctedItem.getAfter() + " " + AutomaticCalcAfterHours
											.valueOf(Integer.valueOf(correctedItem.getAfter())).description;
							itemInfo = correctedItem.toItemInfo(nameAutomaticCalcAfterHoursAtrBef,
									nameAutomaticCalcAfterHoursAtrAtf);
							break;
						case 11:
							String nameReasonGoOutBef = correctedItem.getBefore() == null ? null
									: correctedItem.getBefore() + " " + ReasonGoOut
											.valueOf(Integer.valueOf(correctedItem.getBefore())).description;
							String nameReasonGoOutAft = correctedItem.getAfter() == null ? null
									: correctedItem.getAfter() + " " + ReasonGoOut
											.valueOf(Integer.valueOf(correctedItem.getAfter())).description;
							itemInfo = correctedItem.toItemInfo(nameReasonGoOutBef, nameReasonGoOutAft);
							break;
						case 13:
							String nameTimeLimitUpperLimitSettingBef = correctedItem.getBefore() == null ? null
									: correctedItem.getBefore() + " " + TimeLimitUpperLimitSetting
											.valueOf(Integer.valueOf(correctedItem.getBefore())).description;
							String nameTimeLimitUpperLimitSettingAft = correctedItem.getAfter() == null ? null
									: correctedItem.getAfter() + " " + TimeLimitUpperLimitSetting
											.valueOf(Integer.valueOf(correctedItem.getAfter())).description;
							itemInfo = correctedItem.toItemInfo(nameTimeLimitUpperLimitSettingBef,
									nameTimeLimitUpperLimitSettingAft);
							break;
						default:
							itemInfo = correctedItem.toItemInfo(correctedItem.getBefore(), correctedItem.getAfter());
							break;
						}

						break;

					default:
						itemInfo = correctedItem.toItemInfo(correctedItem.getBefore(), correctedItem.getAfter());
						break;
					}
				} else {
					itemInfo = correctedItem.toItemInfo();
				}

				val correction = this.newCorrection(target.getEmployeeId(), target.getDate(),
						CorrectionAttr.of(correctedItem.getAttr().value), itemInfo, correctedItem.getItemNo());

				context.addCorrection(correction);
			}
		}
		
	}
	
	private String getNumberEndOf(String input){
		Matcher matcher = lastIntPattern.matcher(input);
		if (matcher.find()) {
		    return matcher.group(1);
		}
		return "";
	}

	private DailyCorrection newCorrection(String employeeId, GeneralDate targetDate, CorrectionAttr correctionAttr,
			ItemInfo correctedItem, int showOrder) {

		val targetUser = this.userInfoAdaptor.findByEmployeeId(employeeId);

		return new DailyCorrection(targetUser, targetDate, correctionAttr, correctedItem, showOrder);
	}
}
