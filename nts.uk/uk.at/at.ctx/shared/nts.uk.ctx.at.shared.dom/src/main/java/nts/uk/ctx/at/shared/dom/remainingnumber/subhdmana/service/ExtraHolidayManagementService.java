package nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.DisplayRemainingNumberDataInformation;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.EmploymentManageDistinctDto;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PresentClosingPeriodExport;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.RemainInfoData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.RemainInfoDto;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SEmpHistoryImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SWkpHistImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysEmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementDataDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ExtraHolidayManagementService {

	@Inject
	private LeaveManaDataRepository leaveManaDataRepository;

	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepository;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;

	@Inject
	private SysEmploymentHisAdapter sysEmploymentHisAdapter;

	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepository;

	@Inject
	private CompensLeaveEmSetRepository compensLeaveEmSetRepository;

	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;

	@Inject
	private SysWorkplaceAdapter syWorkplaceAdapter;

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;

	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@Inject
	private ClosureRepository closureRepo;
	
	@Inject
	private InterimBreakDayOffMngRepository interimBreakDayOffMngRepository;

	public ExtraHolidayManagementOutput dataExtractionProcessing(int searchMode, String employeeId) {
		String cid = AppContexts.user().companyId();
		List<LeaveManagementData> listLeaveData = null;
		List<CompensatoryDayOffManaData> listCompensatoryData = null;
		List<LeaveComDayOffManagement> listLeaveComDayOffManagement = new ArrayList<>();
		SEmpHistoryImport empHistoryImport = null;
		ClosureEmployment closureEmploy = null;
		CompensatoryLeaveEmSetting compenLeaveEmpSetting = null;
		CompensatoryLeaveComSetting compensatoryLeaveComSetting = null;
		GeneralDate baseDate = GeneralDate.today();
		
		// 全ての状況
		if (searchMode == 0) {
			
			listLeaveData = leaveManaDataRepository.getBySidNotUnUsed(cid, employeeId);
			listCompensatoryData = comDayOffManaDataRepository.getBySidWithReDay(cid, employeeId);
		} else if (searchMode == 1) {
			
			listLeaveData = leaveManaDataRepository.getAllData();
			listCompensatoryData = comDayOffManaDataRepository.getAllData();
		}
		if (!listLeaveData.isEmpty()) {
			List<GeneralDate> lstDate = listLeaveData.stream().map(x -> {
				return x.getComDayOffDate().getDayoffDate().orElse(null);
			}).collect(Collectors.toList());
			listLeaveComDayOffManagement.addAll(leaveComDayOffManaRepository.getByListDate(employeeId,lstDate));
		}
		if (!listCompensatoryData.isEmpty()) {
			List<GeneralDate> listDate = listCompensatoryData.stream().map(x -> {
				return x.getDayOffDate().getDayoffDate().orElse(null);
			}).collect(Collectors.toList());
			listLeaveComDayOffManagement.addAll(leaveComDayOffManaRepository.getByListDate(employeeId, listDate));
		}
		Optional<SEmpHistoryImport> sEmpHistoryImport = sysEmploymentHisAdapter.findSEmpHistBySid(cid, employeeId,
				baseDate);
		if (sEmpHistoryImport.isPresent()) {
			empHistoryImport = sEmpHistoryImport.get();
			String sCd = empHistoryImport.getEmploymentCode();
			Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(cid, sCd);
			if (closureEmployment.isPresent()) {
				closureEmploy = closureEmployment.get();
			}
		}
		Optional<SWkpHistImport> sWkpHistImport = syWorkplaceAdapter.findBySid(employeeId, baseDate);
		if (!Objects.isNull(empHistoryImport)) {
			compenLeaveEmpSetting = compensLeaveEmSetRepository.find(cid, empHistoryImport.getEmploymentCode());
		}
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add(employeeId);
		List<PersonEmpBasicInfoImport> employeeBasicInfo = empEmployeeAdapter.getPerEmpBasicInfo(employeeIds);
		PersonEmpBasicInfoImport personEmpBasicInfoImport = null;
		if (!employeeBasicInfo.isEmpty()) {
			personEmpBasicInfoImport = employeeBasicInfo.get(0);
		}
		compensatoryLeaveComSetting = compensLeaveComSetRepository.find(cid);
		return new ExtraHolidayManagementOutput(listLeaveData, listCompensatoryData, listLeaveComDayOffManagement,
				empHistoryImport, closureEmploy, compenLeaveEmpSetting, compensatoryLeaveComSetting,
				sWkpHistImport.orElse(null), personEmpBasicInfoImport);
	}

	/**
	 * UKDesign.UniversalK.就業.KDM_残数管理 (Quản lý số dư).KDM001_残数管理データの登録 (Đăng ký dữ liệu quản lý số dư).Ｂ：代休管理(Quản lý ngày nghỉ bù)
	 * .アルゴリズム.Ｂ：代休管理データ抽出処理(Quản lý trích xuất dữ liệu quản lý nghỉ bù).Ｂ：代休管理データ抽出処理
	 * @param searchMode
	 * @param employeeId
	 * @param messageDisplay
	 * @return
	 */
	public DisplayRemainingNumberDataInformation dataExtractionProcessingUpdate(String cid, String employeeId, int searchMode, int messageDisplay) {
		List<LeaveManagementData> listLeaveData = null;
		List<CompensatoryDayOffManaData> listCompensatoryData = null;
		List<LeaveComDayOffManagement> listLeaveComDayOffManagement = new ArrayList<>();
		SEmpHistoryImport empHistoryImport = null;

		if (employeeId == null || employeeId.isEmpty()) {
			employeeId = AppContexts.user().employeeId();
		}
		// 代休管理データを管理するかチェック Check xem có quản lý dữ liệu quản lý nghỉ bù không
		EmploymentManageDistinctDto manageDistinct = this.checkManageSubstituteHolidayManagementData(employeeId);

		// 取得した管理区分をチェック Check phân loại quản lý đã nhận
		if (manageDistinct.getIsManage().equals(ManageDistinct.NO)) { // 「管理しない」の場合 Trường hợp không quản lý
			// エラーメッセージ「Msg_1731」を表示 hiển thị error msg
			throw new BusinessException("Msg_1731", "Com_CompensationHoliday");
		}

		// Input．設定期間区分をチェック Check Input.Phân loại thời gian setting
		if (searchMode == 0) { // 「現在の残数状況」の場合
			// ドメイン「休出管理データ」を取得する (lấy data chỉ định dựa vào domain 「休出管理データ」) 'data quản lý đi làm ngày nghỉ')
			listLeaveData = leaveManaDataRepository.getBySidAndStateAtr(cid, employeeId, DigestionAtr.UNUSED);

			// ドメイン「代休管理データ」を取得する (lấy data chị định dựa vào domain 「代休管理データ」'data quản lý ngày nghỉ thay thế')
			listCompensatoryData = comDayOffManaDataRepository.getByReDay(cid, employeeId);
		}

		if (searchMode == 1) { // 「全ての状況」の場合
			listLeaveData = leaveManaDataRepository.getBySid(cid, employeeId);
			listCompensatoryData = comDayOffManaDataRepository.getBySid(cid, employeeId);
		}

		// ドメイン「休出代休紐付け管理」を取得する (Lấy data chỉ định theo domain 「休出代休紐付け管理」 'quản lý liên quan đến đi làm ngày nghỉ và ngày nghỉ thay thế')
		List<GeneralDate> lstOccDate = new ArrayList<GeneralDate>();
		List<GeneralDate> lstDigestDate = new ArrayList<GeneralDate>();
		if (!listLeaveData.isEmpty()) {
			lstOccDate = listLeaveData.stream()
					.map(x -> x.getComDayOffDate().getDayoffDate().orElse(null))
					.collect(Collectors.toList());
		}
		
		if (!listCompensatoryData.isEmpty()) {
			lstDigestDate = listCompensatoryData.stream()
					.map(x -> x.getDayOffDate().getDayoffDate().orElse(null))
					.collect(Collectors.toList());
		}
		
		Optional<SEmpHistoryImport> sEmpHistoryImport = sysEmploymentHisAdapter.findSEmpHistBySid(cid, employeeId,
				GeneralDate.today());
		if (sEmpHistoryImport.isPresent()) {
			empHistoryImport = sEmpHistoryImport.get();
		}

		listLeaveComDayOffManagement.addAll(leaveComDayOffManaRepository.getByListOccDigestDate(employeeId, lstOccDate, lstDigestDate));
		// 締めIDを取得する Nhân shime ID (ID chốt)
		Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(cid, manageDistinct.getEmploymentCode());
		Integer closureId = closureEmployment.map(ClosureEmployment::getClosureId).orElse(null);
				
		// 処理年月と締め期間を取得する Nhận khoảng thời gian chốt và tháng năm xử lý
		Optional<PresentClosingPeriodExport> closing = this.find(cid, closureId);
		if(!closing.isPresent()) return null;
		PresentClosingPeriodExport close =  closing.get();
		// 代休残数データ情報を作成する Tạo thông tin dữ liệu số ngày nghỉ bù còn lại
		List<RemainInfoData> lstRemainData = this.getRemainInfoData(employeeId,
				listLeaveData,
				listCompensatoryData,
				listLeaveComDayOffManagement,
				close.getClosureStartDate());

		// 管理区分設定を取得 Nhận Setting phân loại quản lý
		// ドメインモデル「雇用代休管理設定」を取得する Get domain model 「雇用代休管理設定」
		CompensatoryLeaveEmSetting empSetting = compensLeaveEmSetRepository.find(cid, manageDistinct.getEmploymentCode());
		CompensatoryLeaveComSetting compLeavCom = null;
		if (empSetting == null) {
			// ドメインモデル「代休管理設定」を取得する Get modain model 「代休管理設定」
			compLeavCom = compensLeaveComSetRepository.find(cid);
		}

		
		
		GeneralDate baseDate = GeneralDate.today();
		Optional<SWkpHistImport> sWkpHistImport = syWorkplaceAdapter.findBySid(employeeId, baseDate);
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add(employeeId);
		List<PersonEmpBasicInfoImport> employeeBasicInfo = empEmployeeAdapter.getPerEmpBasicInfo(employeeIds);
		PersonEmpBasicInfoImport personEmpBasicInfoImport = null;
		if (!employeeBasicInfo.isEmpty()) {
			personEmpBasicInfoImport = employeeBasicInfo.get(0);
		}
		
		List<RemainInfoDto> lstDataRemainDto =  lstRemainData.stream()
				.map(item -> RemainInfoDto.builder()
					.occurrenceId(item.getOccurrenceId().orElse(""))
					.occurrenceHour(item.getOccurrenceHour().orElse(0))
					.occurrenceDay(item.getOccurrenceDay().orElse(0d))
					.accrualDate(item.getAccrualDate().map(t -> t.toString()).orElse(""))
					.digestionId(item.getDigestionId().orElse(""))
					.digestionTimes(item.getDigestionTimes().orElse(0))
					.digestionDays(item.getDigestionDays().orElse(0d))
					.digestionDay(item.getDigestionDay().map(t -> t.toString()).orElse(""))
					.remainingHours(item.getRemainingHours().orElse(0))
					.dayLetf(item.getDayLetf())
					.deadLine(item.getDeadLine().map(t -> t.toString()).orElse(""))
					.usedTime(item.getUsedTime())
					.usedDay(item.getUsedDay())
					.mergeCell(item.getMergeCell())
					.build())
				.collect(Collectors.toList());
		// 「表示残数データ情報」を作成 Tạo "Thông tin dữ liệu còn lại hiển thị"
		DisplayRemainingNumberDataInformation result = DisplayRemainingNumberDataInformation.builder()
				.employeeId(employeeId)
				.totalRemainingNumber(0d)
				.dispExpiredDate(compLeavCom != null
					? compLeavCom.getCompensatoryAcquisitionUse().getExpirationTime().description
					: null)
					//	empSetting.getCompensatoryAcquisitionUse().getExpirationTime().description)
				.remainingData(lstDataRemainDto)
				.startDate(closing.get().getClosureStartDate())
				.endDate(closing.get().getClosureEndDate())
				.closureEmploy(closureEmployment.orElse(null))
				.wkHistory(sWkpHistImport.orElse(null))
				.sempHistoryImport(empHistoryImport)
				.employeeCode(personEmpBasicInfoImport == null ? "" : personEmpBasicInfoImport.getEmployeeCode())
				.employeeName(personEmpBasicInfoImport == null ? "" : personEmpBasicInfoImport.getBusinessName())
				.build();

		// 作成した「表示残数データ情報」を返す Trả về "Thông tin dữ liệu còn lại hiển thị" đã tạo
		return result;
	}
	
	/**
	 * UKDesign.UniversalK.就業.KDM_残数管理 (Quản lý số dư).KDM001_残数管理データの登録 (Đăng ký dữ liệu quản lý số dư).Ｂ：代休管理(Quản lý ngày nghỉ bù)
	 * .アルゴリズム.Ｂ：代休管理データ抽出処理(Quản lý trích xuất dữ liệu quản lý nghỉ bù).代休管理データを管理するかチェック.代休管理データを管理するかチェック
	 * @param empId
	 */
	public EmploymentManageDistinctDto checkManageSubstituteHolidayManagementData(String empId) {
		EmploymentManageDistinctDto emplData = EmploymentManageDistinctDto.builder().build();
		String cid = AppContexts.user().companyId();
		GeneralDate baseDate = GeneralDate.today();
		// 管理区分　＝　管理しない Phân loại quản lý là 管理しない
		emplData.setIsManage(ManageDistinct.NO);
		
		// 社員IDから全ての雇用履歴を取得 Nhận toàn bộ lịch sử tuyển dụng từ Employee ID
		List<EmploymentHistShareImport> empHistShrImpList = this.shareEmploymentAdapter.findByEmployeeIdOrderByStartDate(empId);
		
		// 取得した社員の雇用履歴をチェック Check lịch sử tuyển dụng của nhân viên đã lấy
		if (empHistShrImpList.isEmpty()) {
			// エラーメッセージ(Msg_1306)を表示(Set error message (Msg_1306) vào error list)
			throw new BusinessException("Msg_1306");
		}
		
		// 取得した社員の雇用履歴をループする Lặp lại lịch sử tuyển dụng của nhân viên đã lấy
		for (EmploymentHistShareImport empHistShrImp : empHistShrImpList) {
			CompensatoryLeaveComSetting compLeavCom = null;
			// 代休管理設定の取得
			// ドメインモデル「雇用代休管理設定」を取得する Get domain model 「雇用代休管理設定」
			CompensatoryLeaveEmSetting empSetting = compensLeaveEmSetRepository.find(cid, empHistShrImp.getEmploymentCode());
			
			if (empSetting == null) { // 取得結果＝0件
				// ドメインモデル「代休管理設定」を取得する Get modain model 「代休管理設定」
				compLeavCom = compensLeaveComSetRepository.find(cid);
			} else {
				emplData.setIsManage(empSetting.getIsManaged());
				emplData.setEmploymentCode(empSetting.getEmploymentCode().v());
			}
			// 管理するかないかチェック Check Setting quản lý nghỉ bù hay ko
			if (compLeavCom != null && (compLeavCom.isManaged() || compLeavCom.getCompensatoryDigestiveTimeUnit().getIsManageByTime().equals(ManageDistinct.YES))) {
				emplData.setIsManage(ManageDistinct.YES);

				if (empHistShrImp.getPeriod().start().beforeOrEquals(baseDate) && empHistShrImp.getPeriod().end().afterOrEquals(baseDate)) {
					emplData.setEmploymentCode(empHistShrImp.getEmploymentCode());
					return emplData;
				}
				
				emplData.setIsManage(ManageDistinct.NO);
			}
		}
		
		return emplData;
	}
	
	
	/**
	 * UKDesign.UniversalK.就業.KDM_残数管理 (Quản lý số dư).KDM001_残数管理データの登録 (Đăng ký dữ liệu quản lý số dư).Ｂ：代休管理(Quản lý ngày nghỉ bù)
	 * .アルゴリズム.Ｂ：代休管理データ抽出処理(Quản lý trích xuất dữ liệu quản lý nghỉ bù).代休残数データ情報を作成する.代休残数データ情報を作成する
	 * @param sid 社員ID
	 * @param listLeaveData List＜休出管理データ＞
	 * @param listCompensatoryData List＜代休管理データ＞
	 * @param listLeaveComDayOffManagement List＜休出代休紐付け管理＞
	 * @return List<残数データ情報＞
	 */
	public List<RemainInfoData> getRemainInfoData(String sid
												, List<LeaveManagementData> listLeaveData
												, List<CompensatoryDayOffManaData> listCompensatoryData
												, List<LeaveComDayOffManagement> listLeaveComDayOffManagement
												, GeneralDate startDate) {
		List<RemainInfoData> lstRemainInfoData = new ArrayList<RemainInfoData>();
		Integer mergeCell = 0;
		// Input．List＜休出管理データ＞をループする
		for (LeaveManagementData leaveData : listLeaveData) {
			// 「休出代休紐付け管理」を絞り込み Filter "Quản lý liên kết đi làm ngày nghỉ và nghỉ bù"
			List<LeaveComDayOffManagement> lCOMSub = listLeaveComDayOffManagement.stream()
				.filter(item -> item.getAssocialInfo().getOutbreakDay().equals(leaveData.getComDayOffDate().getDayoffDate().orElse(null)))
				.collect(Collectors.toList());
			
			// 絞り込みした「休出代休紐付け管理」をチェック Check nội dung đã filter
			if (lCOMSub.isEmpty()) {
				// 残数データ情報を作成 Tạo thông tin data số dư
				RemainInfoData remainInfo = RemainInfoData.builder()
						.accrualDate(leaveData.getComDayOffDate().getDayoffDate())
						.deadLine(Optional.of(leaveData.getExpiredDate()))
						.occurrenceDay(Optional.of(leaveData.getOccurredDays().v()))
						.occurrenceHour(Optional.of(leaveData.getOccurredTimes().v()))
						.digestionDay(Optional.empty())
						.digestionDays(Optional.empty())
						.digestionTimes(Optional.empty())
						.occurrenceId(Optional.of(leaveData.getID()))
						.digestionId(Optional.empty())
						.dayLetf(leaveData.getExpiredDate().afterOrEquals(startDate) ? leaveData.getUnUsedDays().v() : 0d)
						.remainingHours(Optional.of(leaveData.getExpiredDate().beforeOrEquals(startDate) ? leaveData.getUnUsedTimes().v() : 0))
						.usedDay(leaveData.getExpiredDate().before(startDate) ? leaveData.getUnUsedDays().v() : 0d)
						.usedTime(leaveData.getExpiredDate().before(startDate) ? leaveData.getUnUsedTimes().v() : 0)
						.mergeCell(mergeCell)
						.build();
				mergeCell++;
				// List＜残数データ情報＞に作成した残数データ情報を追加 Bổ sung thông tin data số dư đã tạo vào List＜残数データ情報＞
				lstRemainInfoData.add(remainInfo);
				continue;
			}
			
			// 「代休管理データ」を絞り込み Filter "Dữ liệu quản lý nghỉ bù"
			GeneralDate useDate = lCOMSub.stream()
					.map(x -> x.getAssocialInfo().getDateOfUse())
					.filter(Objects::nonNull).findFirst().orElse(null);
			Optional<CompensatoryDayOffManaData> optCompenDayOffData = listCompensatoryData.stream()
					.filter(item -> item.getDayOffDate().getDayoffDate()
							.map(date -> date.equals(useDate)).orElse(false))
					.findFirst();
			Optional<InterimDayOffMng> optInterimDayOff = Optional.empty();
			
			// 絞り込みした「代休管理データ」をチェック Check dữ liệu đã filter
			if (!optCompenDayOffData.isPresent()) { // ないの場合 Không có
				// ドメインモデル「代休管理データ」を取得 get domain model 「代休管理データ」
				optCompenDayOffData = comDayOffManaDataRepository.findBySidAndDate(sid, useDate);
				// 取得したドメインモデル「代休管理データ」をチェック
				if (!optCompenDayOffData.isPresent()) {
					// ドメインモデル「暫定代休管理データ」を取得する
					optInterimDayOff = this.interimBreakDayOffMngRepository
							.getDayOffByDate(sid, useDate);
				}
			}

			if (optCompenDayOffData.isPresent() || optInterimDayOff.isPresent()) {
				Optional<LeaveComDayOffManagement> optComDayOff = lCOMSub.stream()
						.filter(data -> data.getAssocialInfo().getDateOfUse().equals(useDate))
						.findFirst();
				Optional<GeneralDate> digestionDay = optComDayOff.map(data -> data.getAssocialInfo().getDateOfUse());
				Optional<Double> digestionDays = optComDayOff.map(data -> data.getAssocialInfo().getDayNumberUsed().v());
				Optional<Integer> digestionTimes = Optional.ofNullable(optCompenDayOffData
						.map(data -> data.getRemainTimes().v())
						.orElse(optInterimDayOff.map(data -> data.getRequiredTime().v()).orElse(null)));
				Optional<String> digestionId = Optional.of(optCompenDayOffData
						.map(data -> data.getComDayOffID())
						.orElse(optInterimDayOff.map(data -> data.getRemainManaID()).orElse(null)));
				// 残数データ情報を作成 Tạo thông tin dữ liệu số dư
				RemainInfoData remainInfo = RemainInfoData.builder()
						.accrualDate(leaveData.getComDayOffDate().getDayoffDate())
						.deadLine(Optional.of(leaveData.getExpiredDate()))
						.occurrenceDay(Optional.of(leaveData.getOccurredDays().v()))
						.occurrenceHour(Optional.of(leaveData.getOccurredTimes().v()))
						.digestionDay(digestionDay)
						.digestionDays(digestionDays)
						.digestionTimes(digestionTimes)
						.occurrenceId(Optional.of(leaveData.getID()))
						.digestionId(digestionId)
						.dayLetf(leaveData.getExpiredDate().afterOrEquals(startDate) ? leaveData.getUnUsedDays().v() : 0d)
						.remainingHours(Optional.of(leaveData.getExpiredDate().beforeOrEquals(startDate) ? leaveData.getUnUsedTimes().v() : 0))
						.usedDay(leaveData.getExpiredDate().before(startDate) ? leaveData.getUnUsedDays().v() : 0d)
						.usedTime(leaveData.getExpiredDate().before(startDate) ? leaveData.getUnUsedTimes().v() : 0)
						.mergeCell(mergeCell)
						.build();
				
				// List＜残数データ情報＞に作成した残数データ情報を追加 Bổ sung thông tin data số dư đã tạo vào trong List＜残数データ情報＞
				lstRemainInfoData.add(remainInfo);
			}
			mergeCell++;
			
			// Input．List<休出代休紐付け管理＞に絞り込みした「休出代休紐付け管理」を除く
			listLeaveComDayOffManagement.removeIf(x -> lCOMSub.contains(x));
			//Input．List＜代休管理データ＞に絞り込みした「代休管理データ」を除く
			CompensatoryDayOffManaData manaData = optCompenDayOffData.orElse(null);
			listCompensatoryData.removeIf(x -> x.equals(manaData));
		}
		
		// List＜代休管理データ＞をループする Loop List＜代休管理データ＞
		for (CompensatoryDayOffManaData cdomdData : listCompensatoryData) {
			// List＜休出代休紐付け管理＞を絞り込みする  Filter List＜代休管理データ＞
			List<LeaveComDayOffManagement> lcdomList = listLeaveComDayOffManagement.stream()
					.filter(item -> cdomdData.getDayOffDate().getDayoffDate().isPresent()
								? cdomdData.getDayOffDate().getDayoffDate().get().equals(item.getAssocialInfo().getDateOfUse())
								: false)
					.collect(Collectors.toList());
			
			// 絞り込みした「休出代休紐付け管理」をチェック Check "Quản lý liên kết đi làm ngày nghỉ/nghỉ bù" đã filter
			if (lcdomList.isEmpty()) {
				// 残数データ情報を作成Tạo thông tin dữ liệu số dư
				RemainInfoData remainInfo = RemainInfoData.builder()
						.accrualDate(Optional.empty())
						.deadLine(Optional.empty())
						.occurrenceDay(Optional.empty())
						.occurrenceHour(Optional.empty())
						.digestionDay(cdomdData.getDayOffDate().getDayoffDate())
						.digestionDays(Optional.of(cdomdData.getRequireDays().v()))
						.digestionTimes(Optional.of(cdomdData.getRequiredTimes().v()))
						.occurrenceId(Optional.empty())
						.digestionId(Optional.of(cdomdData.getComDayOffID()))
						.dayLetf(0d)
						.remainingHours(Optional.of(0))
						.usedDay(0d)
						.usedTime(0)
						.mergeCell(mergeCell)
						.build();
				mergeCell++;
				// List＜残数データ情報＞に作成した残数データ情報を追加 Bổ sung thông tin dữ liệu số dư đã tạo vào List＜残数データ情報＞
				lstRemainInfoData.add(remainInfo);
				continue;
			}
			List<GeneralDate> dayOffs = lcdomList.stream()
					.map(x -> x.getAssocialInfo().getOutbreakDay())
					.collect(Collectors.toList());
			// ドメインモデル「休出管理データ」を取得する Get domain model 「休出管理データ」
			List<LeaveManagementData> manaDataList = leaveManaDataRepository.getBySidAndDatOff(sid, dayOffs);
			for (LeaveManagementData item : manaDataList) {
				// 残数データ情報を作成 Tạo thông tin dữ liệu số dư
				RemainInfoData remainInfo = RemainInfoData.builder()
						.accrualDate(item.getComDayOffDate().getDayoffDate())
						.deadLine(Optional.of(item.getExpiredDate()))
						.occurrenceDay(Optional.of(item.getOccurredDays().v()))
						.occurrenceHour(Optional.of(item.getOccurredTimes().v()))
						.digestionDay(cdomdData.getDayOffDate().getDayoffDate())
						.digestionDays(Optional.of(cdomdData.getRequireDays().v()))
						.digestionTimes(Optional.of(cdomdData.getRequiredTimes().v()))
						.occurrenceId(Optional.of(item.getID()))
						.digestionId(Optional.of(cdomdData.getComDayOffID()))
						.dayLetf(item.getExpiredDate().afterOrEquals(startDate) ? item.getUnUsedDays().v() : 0d)
						.remainingHours(Optional.of(item.getExpiredDate().beforeOrEquals(startDate) ? item.getUnUsedTimes().v() : 0))
						.usedDay(item.getExpiredDate().before(startDate) ? item.getUnUsedDays().v() : 0d)
						.usedTime(item.getExpiredDate().before(startDate) ? item.getUnUsedTimes().v() : 0)
						.mergeCell(mergeCell)
						.build();
				
				// List＜残数データ情報＞に作成した残数データ情報を追加 Bổ sung thông tin dữ liệu số dư đã tạo vào List＜残数データ情報＞
				lstRemainInfoData.add(remainInfo);
			}
			mergeCell++;
		}
		
		return lstRemainInfoData.stream()
				.sorted((x, y) -> {
					return x.getAccrualDate().isPresent() && y.getAccrualDate().isPresent() ? x.getAccrualDate().get().compareTo(y.getAccrualDate().get()) : 0;
				})
				.collect(Collectors.toList());
	}
	
	public Optional<PresentClosingPeriodExport> find(String cId, int closureId) {
		// ドメインモデル「締め」を取得 (Lấy domain 「締め」)
		Optional<Closure> optClosure = closureRepo.findById(cId, closureId);

		// Check exist and active
		if (!optClosure.isPresent() || optClosure.get().getUseClassification()
				.equals(UseClassification.UseClass_NotUse)) {
			return Optional.empty();
		}

		Closure closure = optClosure.get();

		// Get Processing Ym 処理年月
		YearMonth processingYm = closure.getClosureMonth().getProcessingYm();

		DatePeriod closurePeriod = ClosureService.getClosurePeriod(closureId, processingYm, optClosure);

		// Return
		return Optional.of(PresentClosingPeriodExport.builder().processingYm(processingYm)
				.closureStartDate(closurePeriod.start()).closureEndDate(closurePeriod.end())
				.build());
	}
	
	/**
	 * 	月初の代休残数を取得 Nhận số nghỉ bù còn lại lúc đầu tháng
	 * @param cid
	 * @param sid
	 * @return
	 */
	public double getDayOffRemainOfBeginMonth(String cid, String sid) {
		//ドメインモデル「休出管理データ」を取得
		List<LeaveManagementData> lstLeaveData = leaveManaDataRepository.getBySidWithsubHDAtr(cid, sid, DigestionAtr.UNUSED.value);
		Double unUseDays = (double) 0;
		//取得した「休出管理データ」の未使用日数を合計
		for (LeaveManagementData leaveData : lstLeaveData) {
			unUseDays += leaveData.getUnUsedDays().v();
		}
		//ドメインモデル「代休管理データ」を取得
		List<CompensatoryDayOffManaData> lstLeaveDayOffData = comDayOffManaDataRepository.getByReDay(cid, sid);
		Double unOffSet = (double) 0;
		//取得した「代休管理データ」の未相殺日数を合計
		for (CompensatoryDayOffManaData leaveDayOffData : lstLeaveDayOffData) {			
			unOffSet += leaveDayOffData.getRemainDays().v();
		}
		//代休発生数合計－代休使用数合計を返す
		return unUseDays - unOffSet;
	}
	
	/**
	 * Get LeaveManaData By Id And UnUseDay
	 * @param sid
	 * @return List<LeavesManaData>
	 */
	public List<LeaveManagementDataDto> getLeaveManaDataByIdAndUnUse(String cid, String sid, GeneralDate expiredDate, double unUse) {
		List<LeaveManagementData> listData = leaveManaDataRepository.getListByIdAndUnUse(cid, sid, expiredDate, unUse);
		return listData.stream().map(
				domain -> LeaveManagementDataDto.builder()
					.id(domain.getID())
					.dayoffDate(domain.getComDayOffDate().getDayoffDate().orElse(null))
					.unUsedDays(domain.getUnUsedDays().v())
					.build())
				.collect(Collectors.toList());
	}
}
