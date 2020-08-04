package nts.uk.ctx.at.record.app.find.stamp.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto.StampDataOfEmployeesDto;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.worklocation.WorkLocation;
import nts.uk.ctx.at.record.dom.worklocation.WorkLocationRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EmployeeStampInfo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetListStampEmployeeService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampInfoDisp;
import nts.uk.shr.com.context.AppContexts;

/**
 * «ScreenQuery» 打刻結果の打刻情報を取得する
 * @author HieuLT
 *
 */
@Stateless
public class DisplayScreenStampingResultFinder {

	// 勤務場所
	@Inject
	private WorkLocationRepository workLocationRepository;
	@Inject
	private StampCardRepository stampCardRepository;

	@Inject
	private StampRecordRepository stampRecordRepository;

	@Inject
	private StampDakokuRepository stampDakokuRepository;

	public List<DisplayScreenStampingResultDto> getDisplay(DatePeriod datePerriod, String employeeId) {
		List<String> listWorkLocationCode = new ArrayList<>();
		List<EmployeeStampInfo> listStampDataOfEmployees = new ArrayList<>();
		List<DisplayScreenStampingResultDto> res = new ArrayList<>();
		// DS 社員の打刻データを取得する
		// 取得する(@Require, 社員ID, 年月日) 社員の打刻データ
		GetListStampEmployeeService.Require require = new RequireImpl(stampCardRepository, stampRecordRepository,
				stampDakokuRepository);
		for (GeneralDate date : datePerriod.datesBetween()) {
			Optional<EmployeeStampInfo> optStampDataOfEmployees = GetListStampEmployeeService.get(require, employeeId,
					date);
			if (optStampDataOfEmployees.isPresent()) {
				val stamp = optStampDataOfEmployees.get();
				listStampDataOfEmployees.add(stamp);
				//// Get distinct WorkLocationCD
				val listStamp = stamp.getListStampInfoDisp();
				for (val item : listStamp) {
					if (!item.getStamp().isEmpty()) {
						val workLocationCD = item.getStamp().get(0).getRefActualResults().getWorkLocationCD();
						if (workLocationCD.isPresent()) {
							listWorkLocationCode.add(workLocationCD.get().v());
						}
					}
				}

			}
		}
		// List<StampDataOfEmployeesDto> listDtoStamp =
		// listStampDataOfEmployees.stream().map(r -> new
		// StampDataOfEmployeesDto(r)).collect(Collectors.toList());
		// 2 get* List<社員の打刻情報>．勤務場所コード : List< 勤務場所>
		List<WorkLocation> listWorkLocation = workLocationRepository.findByCodes(AppContexts.user().companyId(),
				listWorkLocationCode);

		for (EmployeeStampInfo stampDataOfEmployees : listStampDataOfEmployees) {
			String workLocationName = ""; 
			if(!stampDataOfEmployees.getListStampInfoDisp().isEmpty()){
				StampInfoDisp info = stampDataOfEmployees.getListStampInfoDisp()
						.get(0);
				if(!info.getStamp().isEmpty()) {
					val workLocationCD = info.getStamp().get(0).getRefActualResults().getWorkLocationCD();
					if(workLocationCD.isPresent()) {
						val workLocationCode = workLocationCD.get();

						val optWorkLocation = listWorkLocation.stream()
								.filter(c -> c.getWorkLocationCD().v().equals(workLocationCode.v())).findFirst();
						workLocationName = (optWorkLocation.isPresent())
								? optWorkLocation.get().getWorkLocationName().v()
								: "";
					}
				}
			}
			
			DisplayScreenStampingResultDto data = new DisplayScreenStampingResultDto(workLocationName,
					new StampDataOfEmployeesDto(stampDataOfEmployees));
			res.add(data);		
		}
		return res;
	}

	@AllArgsConstructor
	private static class RequireImpl implements GetListStampEmployeeService.Require {
		private StampCardRepository stampCardRepository;
		private StampRecordRepository stampRecordRepository;
		private StampDakokuRepository stampDakokuRepository;

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return stampCardRepository.getListStampCard(sid);
		}

		@Override
		public List<StampRecord> getStampRecord(List<StampNumber> stampNumbers, GeneralDate date) {
			return stampRecordRepository.get(AppContexts.user().contractCode(), stampNumbers, date);
		}

		@Override
		public List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate date) {
			return stampDakokuRepository.get(AppContexts.user().contractCode(), stampNumbers, date);
		}

	}

}
