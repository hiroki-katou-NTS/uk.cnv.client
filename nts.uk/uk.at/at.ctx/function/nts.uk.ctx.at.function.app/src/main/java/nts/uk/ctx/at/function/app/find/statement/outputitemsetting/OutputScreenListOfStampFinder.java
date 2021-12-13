package nts.uk.ctx.at.function.app.find.statement.outputitemsetting;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
import nts.uk.ctx.at.function.dom.dailyworkschedule.scrA.RoleExportRepoAdapter;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.PermissionOfEmploymentFormRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampLocationInfor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EmployeeStampInfo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetListStampEmployeeService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.RetrieveNoStampCardRegisteredService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampInfoDisp;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.shr.com.company.CompanyAdapter;
import nts.uk.shr.com.company.CompanyInfor;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 
 * @author HieuLT
 *
 */
@Stateless
public class OutputScreenListOfStampFinder {

	@Inject
	private CompanyAdapter company;

	@Inject
	private StampCardRepository stampCardRepository;

	@Inject
	private StampDakokuRepository stampDakokuRepository;

	@Inject
	private EmployeeInformationAdapter employeeInformationAdapter;

	@Inject
	private WorkLocationRepository workLocationRepository;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
	private PermissionOfEmploymentFormRepository permissionOfEmploymentFormRepository;

	@Inject
	private RoleExportRepoAdapter roleExportRepoAdapter;
	
	@Inject
	private ClosureRepository closureRepo;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;
	
	private static final Integer FUNCTION_NO = 5;

	// 起動する(khởi động)
	public OutputScreenListOfStampDto initScreen() {

		String employeeID = AppContexts.user().employeeId();
		GeneralDate ymd = GeneralDate.today();
		String roleId = AppContexts.user().roles().forAttendance();
		OutputScreenListOfStampDto result = new OutputScreenListOfStampDto();
		if (roleExportRepoAdapter.getRoleWhetherLogin().isEmployeeCharge()) {
			result.setExistAuthEmpl(true);
		} else {
			result.setExistAuthEmpl(false);
		}

		// [RQ622]会社IDから会社情報を取得する
		CompanyInfor companyInfo = company.getCurrentCompany().orElseGet(() -> {
			throw new RuntimeException("System Error: Company Info");
		});
		// 社員に対応する締め期間を取得する(Lấy closurePeriod ứng với employee)
		DatePeriod period = ClosureService.findClosurePeriod(
				ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter),
				new CacheCarrier(), employeeID, ymd);
		result.setCompanyCode(companyInfo.getCompanyCode());
		result.setCompanyName(companyInfo.getCompanyName());
		result.setStartDate(period.start());
		result.setEndDate(period.end());

		return result;
	}

	//// 社員別打刻一覧表を作成す Create a list of stamps for each employee
	public List<EmployeEngravingInfor> createListOfStampEmpQuery(DatePeriod datePerriod, List<String> listEmp) {
		List<EmployeEngravingInfor> result = new ArrayList<>();

		// 1取得する(@Require, 社員ID, 年月日) :社員の打刻情報
		List<EmployeeStampInfo> listEmployeeStampInfo = new ArrayList<>();
		List<String> listWorkLocationCode = new ArrayList<>();

		GetListStampEmployeeService.Require require = new RequireImpl(stampCardRepository, stampDakokuRepository);
		for (GeneralDate date : datePerriod.datesBetween()) {
			for (String employeeId : listEmp) {
				Optional<EmployeeStampInfo> optEmployeeStampInfo = GetListStampEmployeeService.get(require, employeeId,
						date);
				if (optEmployeeStampInfo.isPresent()) {
					val employeeStampInfo = optEmployeeStampInfo.get();
					listEmployeeStampInfo.add(employeeStampInfo);

					// Get distinct WorkLocationCD
					val listStempInfoDisp = employeeStampInfo.getListStampInfoDisp();
					for (val stampInfoDisp : listStempInfoDisp) {
						val optStamp = stampInfoDisp.getStamp();
						if (optStamp.isPresent()) {
							if (optStamp.get().getRefActualResults().getWorkInforStamp().isPresent()) {
								val workLocationCD = optStamp.get().getRefActualResults().getWorkInforStamp().get().getWorkLocationCD();
								if (workLocationCD.isPresent())
									listWorkLocationCode.add(workLocationCD.get().v());
							}
						}
					}
				}
			}
		}
		List<String> listEmployeeId = listEmployeeStampInfo.stream().map(c -> c.getEmployeeId()).distinct()
				.collect(Collectors.toList());

		// 2 <call> List＜社員の打刻情報＞.社員ID :List<社員情報>
		// <<Public>> 社員の情報を取得する
		List<EmployeeInformationImport> listEmpInfo = employeeInformationAdapter
				.getEmployeeInfo(new EmployeeInformationQueryDtoImport(listEmployeeId, datePerriod.end(), true, false,
						true, false, false, false));

		// 3 get* List<社員の打刻情報>．勤務場所コード : List< 勤務場所>
		List<WorkLocation> listWorkLocation = workLocationRepository.findByCodes(AppContexts.user().contractCode(),
				listWorkLocationCode);

		// 4 get* List<社員の打刻情報>.就業時間帯コード : List< 就業時間帯>
		List<RefectActualResult> listRefectActualResult = listEmployeeStampInfo.stream().flatMap(c -> {
			List<RefectActualResult> stampInfos = c.getListStampInfoDisp().stream().map(t -> t.getStamp())
					.filter(s -> s.isPresent()).map(r -> r.get().getRefActualResults()).collect(Collectors.toList());
			return stampInfos.stream();
		}).collect(Collectors.toList());
		List<WorkTimeCode> listWorkTime = listRefectActualResult.stream().map(c -> c.getWorkTimeCode())
				.filter(t -> t.isPresent()).distinct().map(g -> g.get()).collect(Collectors.toList());
		List<String> listWorkTimeCode = listWorkTime.stream().map(c -> c.v()).collect(Collectors.toList());
		List<WorkTimeSetting> listWorkTimeSetting = workTimeSettingRepository
				.getListWorkTimeSetByListCode(AppContexts.user().companyId(), listWorkTimeCode);

		for (val item : listEmployeeStampInfo) {
			EmployeeInformationImport empInfo = listEmpInfo.stream()
					.filter(c -> c.getEmployeeId().equals(item.getEmployeeId())).findFirst().orElse(null);

			// StampInfoDisp
			for (val stampInfoDisp : item.getListStampInfoDisp()) {
				EmployeEngravingInfor employeEngravingInfor = new EmployeEngravingInfor();
				if(!stampInfoDisp.getStamp().isPresent()){
					employeEngravingInfor
					.setWorkplaceCd((empInfo != null) ? empInfo.getWorkplace().getWorkplaceCode() : "");
			employeEngravingInfor
					.setWorkplaceName((empInfo != null) ? empInfo.getWorkplace().getWorkplaceName() : "");
			employeEngravingInfor.setEmployeeCode((empInfo != null) ? empInfo.getEmployeeCode() : "");
			employeEngravingInfor.setEmployeeName((empInfo != null) ? empInfo.getBusinessName() : "");
			employeEngravingInfor.setDateAndTime(stampInfoDisp.getStampDatetime().toString());
			employeEngravingInfor.setAttendanceAtr(stampInfoDisp.getStampAtr());
			employeEngravingInfor.setStampMeans("");
			employeEngravingInfor.setAuthcMethod("");
			employeEngravingInfor.setInstallPlace("");
			employeEngravingInfor.setLocalInfor("");
			employeEngravingInfor.setCardNo(stampInfoDisp.getStampNumber().v());
			employeEngravingInfor.setSupportCard("");
			employeEngravingInfor.setWorkTimeDisplayName("");
			result.add(employeEngravingInfor);
				}
				if (!stampInfoDisp.getStamp().isPresent())
					continue;
				val stamp = stampInfoDisp.getStamp().get();

				String local = "";
				String optSupportCard = "";
				String workLocationName = "";
				String workTimeName = "";
				Boolean isAddress = false;
				
				if (stamp.getRefActualResults().getWorkInforStamp().isPresent() && stamp.getRefActualResults().getWorkInforStamp().get().getWorkLocationCD().isPresent()) {
					val workLocationCode = stamp.getRefActualResults().getWorkInforStamp().get().getWorkLocationCD().get();
					val optWorkLocation = listWorkLocation.stream()
							.filter(c -> c.getWorkLocationCD().v().equals(workLocationCode.v())).findFirst();

					if (optWorkLocation.isPresent() && workLocationCode.v().trim() != "") {
						workLocationName = optWorkLocation.get().getWorkLocationName().v();
					}
					if (!optWorkLocation.isPresent() && !workLocationCode.v().trim().equals("")) {
						workLocationName = workLocationCode.v() + " " + TextResource.localize("KDP011_50");
					}
				}

				// Local Infor
				if (stamp.getLocationInfor().isPresent()) {
					Map<String, Boolean> gLocation = this.getLocation(new StampLocationInfor(stamp.getLocationInfor().get(), false));
					if (!gLocation.isEmpty()) {
						Map.Entry<String, Boolean> entry = gLocation.entrySet().iterator().next();
						local = entry.getKey();
						isAddress = entry.getValue();
					}
				}

				// Support Card
				if (stamp.getRefActualResults().getWorkInforStamp().isPresent() && stamp.getRefActualResults().getWorkInforStamp().get().getCardNumberSupport().isPresent()) {
					optSupportCard = stamp.getRefActualResults().getWorkInforStamp().get().getCardNumberSupport().get().toString();
				}
				// WorkTime Name

				val optWorkTimeCode = stamp.getRefActualResults().getWorkTimeCode();
				if (optWorkTimeCode.isPresent() && optWorkTimeCode.get().v().trim() != "") {
					val optWorkTimeSetting = listWorkTimeSetting.stream().filter(c -> optWorkTimeCode.isPresent()
							&& c.getWorktimeCode().v().equals(optWorkTimeCode.get().v())).findFirst();
					// val workTimeName = (optWorkTimeSetting.isPresent()) ?
					// optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v()
					// : optWorkTimeCode.get().v() +" "+
					// TextResource.localize("KDP011_50");
					if (optWorkTimeSetting.isPresent() && optWorkTimeCode.get().v().trim() != "") {
						workTimeName = optWorkTimeSetting.get().getWorkTimeDisplayName().getWorkTimeName().v();
					}
					if (!optWorkTimeSetting.isPresent() && !optWorkTimeCode.get().v().trim().equals("")) {
						workTimeName = optWorkTimeCode.get().v() + " " + TextResource.localize("KDP011_50");
					}
				}

				// Overtime Hour & Late Night Time
				val optOvertimeDeclaration = stamp.getRefActualResults().getOvertimeDeclaration();
				// val overtimeHours = (optOvertimeDeclaration.isPresent()) ?
				// optOvertimeDeclaration.get().getOverTime().v() : 0;
				// lateNightTime = (optOvertimeDeclaration.isPresent()) ?
				// optOvertimeDeclaration.get().getOverLateNightTime().v() : 0;

				// Set data
				employeEngravingInfor
						.setWorkplaceCd((empInfo != null) ? empInfo.getWorkplace().getWorkplaceCode() : "");
				employeEngravingInfor
						.setWorkplaceName((empInfo != null) ? empInfo.getWorkplace().getWorkplaceName() : "");
				employeEngravingInfor.setEmployeeCode((empInfo != null) ? empInfo.getEmployeeCode() : "");
				employeEngravingInfor.setEmployeeName((empInfo != null) ? empInfo.getBusinessName() : "");
				employeEngravingInfor.setDateAndTime(stampInfoDisp.getStampDatetime().toString());
				employeEngravingInfor.setAttendanceAtr(stampInfoDisp.getStampAtr());
				employeEngravingInfor.setStampMeans(stamp.getRelieve().getStampMeans().name);
				employeEngravingInfor.setAuthcMethod(stamp.getRelieve().getAuthcMethod().name);
				employeEngravingInfor.setInstallPlace(workLocationName);
				employeEngravingInfor.setLocalInfor(local);
				employeEngravingInfor.setAddress(isAddress);
				employeEngravingInfor.setCardNo(stampInfoDisp.getStampNumber().v());
				employeEngravingInfor.setSupportCard(optSupportCard);
				employeEngravingInfor.setWorkTimeDisplayName(workTimeName);
				if (optOvertimeDeclaration.isPresent()) {
					employeEngravingInfor
							.setOvertimeHours(getTimeString(optOvertimeDeclaration.get().getOverTime().v()));
					employeEngravingInfor
							.setLateNightTime(getTimeString(optOvertimeDeclaration.get().getOverLateNightTime().v()));
				} else {
					employeEngravingInfor.setOvertimeHours("");
					employeEngravingInfor.setLateNightTime("");
				}
				result.add(employeEngravingInfor);
			}
		}

		return result;
	}
	
	private Map<String, Boolean> getLocation(StampLocationInfor stampLocationInfor) {
		Map<String, Boolean> result = new HashMap<>();
		
		if (stampLocationInfor.getPositionInfor() == null) {
			result.put("", false);
			return result;
		} else {
			try {
				URL url = new URL("http://geoapi.heartrails.com/api/xml?method=searchByGeoLocation"
						+ "&x=" + String.format("%.6f", stampLocationInfor.getPositionInfor().getLongitude()) 
						+ "&y=" + String.format("%.6f", stampLocationInfor.getPositionInfor().getLatitude()));
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(url.openStream());
				NodeList nodelist = doc.getElementsByTagName("location");
				if(nodelist.getLength() > 0) {
					Element element = (Element) nodelist.item(0);
					result.put(element.getElementsByTagName("prefecture").item(0).getTextContent() + element.getElementsByTagName("city").item(0).getTextContent() + element.getElementsByTagName("town").item(0).getTextContent(), true);		
					return result;
				}else {
					result.put(String.format("%.6f", stampLocationInfor.getPositionInfor().getLongitude()) + " " + String.format("%.6f", stampLocationInfor.getPositionInfor().getLatitude()), false);
					return result;
				}
			} catch (Exception e) {
				result.put(String.format("%.6f", stampLocationInfor.getPositionInfor().getLongitude()) + " " + String.format("%.6f", stampLocationInfor.getPositionInfor().getLatitude()), false);
				return result;
			}
		}
	}

	public List<CardNoStampInfo> createCardNoStampQuery(DatePeriod datePerriod) {
		String contractCode = AppContexts.user().contractCode();
		// RetrieveNoStampCardRegisteredService
		// 1取得する(@Require, 期間): 打刻情報リスト
		// 打刻カード未登録の打刻データを取得する
		RetrieveNoStampCardRegisteredService.Require requireCardNo = new RequireCardNoIml(stampDakokuRepository);
		List<StampInfoDisp> listStampInfoDisp = RetrieveNoStampCardRegisteredService.get(requireCardNo, datePerriod);
		List<RefectActualResult> listRefectActual = listStampInfoDisp.stream().map(c -> c.getStamp())
				.filter(t -> t.isPresent()).distinct().map(g -> g.get().getRefActualResults())
				.collect(Collectors.toList());
		// 勤務場所コードリスト = 打刻情報リスト:map $.打刻場所distinct
		List<String> listWorkLocationCd = listRefectActual.stream().filter(i -> i.getWorkInforStamp().isPresent()).map(c -> c.getWorkInforStamp().get().getWorkLocationCD())
				.filter(t -> t.isPresent()).map(g -> g.get().v()).collect(Collectors.toList());

		// 2 打刻情報リスト: List< 勤務場所>
		List<WorkLocation> listWorkLocation = workLocationRepository.findByCodes(AppContexts.user().contractCode(),
				listWorkLocationCd);

		// 就業時間帯コードリスト＝打刻情報リスト：map $.就業時間帯コード distinct
		List<WorkTimeCode> listWorkTime = listRefectActual.stream().map(c -> c.getWorkTimeCode())
				.filter(t -> t.isPresent()).distinct().map(g -> g.get()).collect(Collectors.toList());
		List<String> listWorkTimeCode = listWorkTime.stream().map(c -> c.v()).collect(Collectors.toList());

		// 3get 打刻．就業時間帯コード List<就業時間帯>
		List<WorkTimeSetting> listWorkTimeSetting = workTimeSettingRepository
				.getListWorkTimeSetByListCode(AppContexts.user().companyId(), listWorkTimeCode);

		// 打刻情報リスト
		List<CardNoStampInfo> cardNoStampInfos = new ArrayList<>();
		for (StampInfoDisp stampInfoDisp : listStampInfoDisp) {
			CardNoStampInfo cardNoStampInfo = new CardNoStampInfo();
			cardNoStampInfo.setCardNo(stampInfoDisp.getStampNumber().toString());

			// Chuẩn bị data
			String stampMeans = "";
			String authcMethod = "";

			String localInfor = "";
			String supportCard = "";
			String workTimeDisplayName = "";
			String overtimeHours = "";
			String lateNightTime = "";
			String workLocationName = "";
			Boolean isAddress = false;
			// String latitude = "";
			// longitude = "";

			if (stampInfoDisp.getStamp().isPresent()) {

				stampMeans = stampInfoDisp.getStamp().get().getRelieve().getStampMeans().name;
				authcMethod = stampInfoDisp.getStamp().get().getRelieve().getAuthcMethod().name;

				val refActualResults = stampInfoDisp.getStamp().get().getRefActualResults();
				if (refActualResults.getWorkInforStamp().isPresent()) {
					val workLocationCD = refActualResults.getWorkInforStamp().get().getWorkLocationCD();
					if (workLocationCD.isPresent()) {
						val optWorkLocation = listWorkLocation.stream()
								.filter(c -> c.getWorkLocationCD().v().equals(workLocationCD.get().v())).findFirst();
						if (optWorkLocation.isPresent() && workLocationCD.get().v().trim() != "") {
							workLocationName = optWorkLocation.get().getWorkLocationName().v();
						}
						if (!optWorkLocation.isPresent() && !workLocationCD.get().v().trim().equals("")) {

							workLocationName = workLocationCD.get().v() + " " + TextResource.localize("KDP011_50");
						}
					}
				}
				
				val locationInfo = stampInfoDisp.getStamp().get().getLocationInfor();
				if (locationInfo.isPresent()) {
					Map<String, Boolean> gLocation = this.getLocation(new StampLocationInfor(locationInfo.get(), false));
					if (!gLocation.isEmpty()) {
						Map.Entry<String, Boolean> entry = gLocation.entrySet().iterator().next();
						localInfor = entry.getKey();
						isAddress = entry.getValue();
					}
				}
				if (refActualResults.getWorkInforStamp().isPresent() && refActualResults.getWorkInforStamp().get().getCardNumberSupport().isPresent()) {
					supportCard = refActualResults.getWorkInforStamp().get().getCardNumberSupport().get().toString();
				}
				val workTimeCode = refActualResults.getWorkTimeCode();
				if (workTimeCode.isPresent()) {
					val workTimeDisplayNameCheck = listWorkTimeSetting.stream()
							.filter(c -> c.getWorktimeCode().v().equals(workTimeCode.get().v()))
							.map(c -> c.getWorkTimeDisplayName().getWorkTimeName().v()).findFirst();
					if (workTimeDisplayNameCheck.isPresent() && workTimeCode.get().v().trim() != "") {
						workTimeDisplayName = workTimeDisplayNameCheck.get();
					}
					if (!workTimeDisplayNameCheck.isPresent() && !workTimeCode.get().v().trim().equals("")) {
						workTimeDisplayName = workTimeCode.get().v() + " " + TextResource.localize("KDP011_50");
					}

				}

				val overtimeDeclaration = refActualResults.getOvertimeDeclaration();
				if (overtimeDeclaration.isPresent()) {
					overtimeHours = getTimeString(overtimeDeclaration.get().getOverTime().v());
					lateNightTime = getTimeString(overtimeDeclaration.get().getOverLateNightTime().v());
				} else {
					overtimeHours = "";
					lateNightTime = "";
				}
			}

			// Set data
			cardNoStampInfo.setCardNo(stampInfoDisp.getStampNumber().toString());
			cardNoStampInfo.setDateAndTime(stampInfoDisp.getStampDatetime().toString());
			cardNoStampInfo.setAttendanceAtr(stampInfoDisp.getStampAtr());
			cardNoStampInfo.setStampMeans(stampMeans);
			cardNoStampInfo.setAuthcMethod(authcMethod);
			cardNoStampInfo.setInstallPlace(workLocationName);
			cardNoStampInfo.setLocalInfor(localInfor);
			cardNoStampInfo.setAddress(isAddress);
			cardNoStampInfo.setSupportCard(supportCard);
			cardNoStampInfo.setWorkTimeDisplayName(workTimeDisplayName);
			cardNoStampInfo.setOvertimeHours(overtimeHours);
			cardNoStampInfo.setLateNightTime(lateNightTime);
			cardNoStampInfos.add(cardNoStampInfo);
		}

		return cardNoStampInfos;
	}

	public String getTimeString(Integer t) {
		int hours = t / 60;
		int minutes = t % 60;
		return String.format("%02d:%02d", hours, minutes);
	}

	@AllArgsConstructor
	private static class RequireImpl implements GetListStampEmployeeService.Require {
		@Inject
		private StampCardRepository stampCardRepository;
		@Inject
		private StampDakokuRepository stampDakokuRepository;

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return stampCardRepository.getListStampCard(sid);
		}

		@Override
		public List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate stampDateTime) {
			return stampDakokuRepository.get(AppContexts.user().contractCode(),stampNumbers, stampDateTime);
		}

	}

	@AllArgsConstructor
	private static class RequireCardNoIml implements RetrieveNoStampCardRegisteredService.Require {

		@Inject
		private StampDakokuRepository stampDakokuRepo;


		@Override
		public List<Stamp> getStempRcNotResgistNumberStamp(DatePeriod period) {
			return stampDakokuRepo.getStempRcNotResgistNumberStamp(AppContexts.user().contractCode(), period);
		}	
	}

}
