package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.valueobject.AnnLeaRemNumValueObject;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnLeaGrantRemDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AnnLeaEmpBasicInfoDomService{

	@Inject
	private AnnualPaidLeaveSettingRepository annPaidLeaSettingRepo;

	@Inject
	private AnnLeaGrantRemDataRepository annLeaDataRepo;

	@Inject
	private AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepository;

	private static final String not_grant = "未付与";

	public AnnLeaRemNumValueObject getAnnLeaveNumber(String companyId, String employeeId) {
		List<AnnualLeaveGrantRemainingData> annualLeaveDataList = annLeaDataRepo.findNotExp(employeeId);
		return getAnnualLeaveNumber(companyId, annualLeaveDataList);
	}

	public AnnLeaRemNumValueObject getAnnualLeaveNumber(String companyId,
			List<AnnualLeaveGrantRemainingData> listData) {
		Double remainingDays = 0d;
		for (AnnualLeaveGrantRemainingData data : listData) {
			remainingDays += data.getDetails().getRemainingNumber().getDays().v();
		}

		AnnualPaidLeaveSetting setting = annPaidLeaSettingRepo.findByCompanyId(companyId);
		boolean useTimeAnnualLeave = setting.getYearManageType() == ManageDistinct.YES
				&& setting.getTimeSetting().getTimeManageType() == ManageDistinct.YES;

		int remainingMinutes = 0;
		if (useTimeAnnualLeave) {
			for (AnnualLeaveGrantRemainingData data : listData) {
				Optional<LeaveRemainingTime> minutes = data.getDetails().getRemainingNumber().getMinutes();
				remainingMinutes += minutes.isPresent() ? minutes.get().v() : 0;
			}
		}
		return new AnnLeaRemNumValueObject(remainingDays, remainingMinutes);
	}

	public Map<String, AnnLeaRemNumValueObject> getAnnualLeaveNumber(String companyId,
			Map<String,List<AnnualLeaveGrantRemainingData>> annualLeaveGrantDataLst) {
		Map<String, AnnLeaRemNumValueObject> result = new HashMap<>();
		AnnualPaidLeaveSetting setting = annPaidLeaSettingRepo.findByCompanyId(companyId);
		boolean useTimeAnnualLeave = setting.getYearManageType() == ManageDistinct.YES
				&& setting.getTimeSetting().getTimeManageType() == ManageDistinct.YES;

		for(Map.Entry<String, List<AnnualLeaveGrantRemainingData>> entry : annualLeaveGrantDataLst.entrySet()) {
		    String key = entry.getKey();
		    Double remainingDays = 0d;
		    int remainingMinutes = 0;

		    List<AnnualLeaveGrantRemainingData> listData = entry.getValue();

		    for (AnnualLeaveGrantRemainingData data : listData) {
				remainingDays += data.getDetails().getRemainingNumber().getDays().v();
			}

			if (useTimeAnnualLeave) {
				for (AnnualLeaveGrantRemainingData data : listData) {
					Optional<LeaveRemainingTime> minutes = data.getDetails().getRemainingNumber().getMinutes();
					remainingMinutes += minutes.isPresent() ? minutes.get().v() : 0;
				}
			}
			result.put(key, new AnnLeaRemNumValueObject(remainingDays, remainingMinutes));
		}

		return result;
	}

	public String calculateAnnLeaNumWithFormat(String companyId, List<AnnualLeaveGrantRemainingData> listData) {

		AnnLeaRemNumValueObject annLeaveRemainNumber = getAnnualLeaveNumber(companyId, listData);

		// Total time
		// No268特別休暇の利用制御
		AnnualPaidLeaveSetting annualPaidLeaveSet = annualPaidLeaveSettingRepository.findByCompanyId(AppContexts.user().companyId());

		if (annualPaidLeaveSet.getTimeSetting().getTimeManageType() == ManageDistinct.NO) {
			return annLeaveRemainNumber.getDays() + "日";
		}	

		int remainingMinutes = annLeaveRemainNumber.getMinutes();

		int remainingHours = remainingMinutes / 60;
		remainingMinutes -= remainingHours * 60;

		return annLeaveRemainNumber.getDays() + "日と" + remainingHours + ":" + convertWithMinutes(remainingMinutes);
	}

	public Map<String, String> calculateAnnLeaNumWithFormat(String cid, Map<String, List<AnnualLeaveGrantRemainingData>> annualLeaveGrantDataLst) {
		Map<String, String> result = new HashMap<>();
		// Total time
		// No268特別休暇の利用制御
		AnnualPaidLeaveSetting annualPaidLeaveSet = annualPaidLeaveSettingRepository.findByCompanyId(AppContexts.user().companyId());

		Map<String, AnnLeaRemNumValueObject> annLeaveRemainNumber = getAnnualLeaveNumber(cid,  annualLeaveGrantDataLst);
		boolean isNo = annualPaidLeaveSet.getTimeSetting().getTimeManageType() == ManageDistinct.NO
				|| annualPaidLeaveSet.getTimeSetting().getMaxYearDayLeave().manageType == ManageDistinct.NO;
		for (Map.Entry<String, AnnLeaRemNumValueObject> entry : annLeaveRemainNumber.entrySet()) {
			String key = entry.getKey();
			if (isNo) {
				result.put(key, entry.getValue().getDays() + "日");
			} else {
				int remainingMinutes = entry.getValue().getMinutes();
				int remainingHours = remainingMinutes / 60;
				remainingMinutes -= remainingHours * 60;
				result.put(key, entry.getValue().getDays() + "日と　" + remainingHours + ":"
						+ convertWithMinutes(remainingMinutes));
			}
		}
		return result;
	}

	public String calculateLastGrantDate(String employeeId) {
		Optional<AnnualLeaveGrantRemainingData> lastDataOpt = annLeaDataRepo.getLast(employeeId);
		if (!lastDataOpt.isPresent()) {
			return not_grant;
		} else {
			return lastDataOpt.get().getGrantDate().toString();
		}
	}

	// truyền list nhân viên đã được trả về trong list 年休残数
	public Map<String, String> calculateLastGrantDate(String cid, List<String> sids) {
		Map<String, String> result = new HashMap<>();
		Map<String, List<AnnualLeaveGrantRemainingData>> annLeaveRemainDataMap = annLeaDataRepo.findByCidAndSids(cid, sids)
				.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		if(annLeaveRemainDataMap.size() < sids.size()) {
			for(String sid: sids) {
				List<AnnualLeaveGrantRemainingData> annLeaveRemainData = annLeaveRemainDataMap.get(sid);
				if(CollectionUtil.isEmpty(annLeaveRemainData)) {
					annLeaveRemainDataMap.put(sid, new ArrayList<AnnualLeaveGrantRemainingData>());
				}
			}
		}
		for (Map.Entry<String, List<AnnualLeaveGrantRemainingData>> entry : annLeaveRemainDataMap.entrySet()) {
			String key = entry.getKey();
			entry.getValue().stream().sorted(Comparator.comparing(AnnualLeaveGrantRemainingData::getGrantDate).reversed()).collect(Collectors.toList());
			if(entry.getValue().size() == 0) {
				result.put(key, not_grant);
			}else {
				result.put(key, entry.getValue().get(0).getGrantDate().toString());
			}
		}

		return result;
	}

	public String calculateRervLeaveNumber(List<ReserveLeaveGrantRemainingData> listData) {
		Double remainingDays = 0d;
		for (ReserveLeaveGrantRemainingData data : listData) {
			remainingDays += data.getDetails().getRemainingNumber().getDays().v();
		}
		return remainingDays + "日";
	}


	private String convertWithMinutes(int minutes) {
		if ( Math.abs(minutes) < 10) {
			return "0" + Math.abs(minutes);
		}
		return "" + Math.abs(minutes);
	}

}
