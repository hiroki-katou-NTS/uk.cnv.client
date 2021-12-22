package nts.uk.ctx.at.shared.dom.remainingnumber.paymana;

import java.util.ArrayList;
import java.util.Arrays;
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
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.SubstVacationSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class FurikyuMngDataExtractionService {
	@Inject
	private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository;
	
	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepository;
	
	@Inject
	private PayoutSubofHDManaRepository payoutSubofHDManaRepository;
	
	@Inject
	private EmpSubstVacationRepository empSubstVacationRepository;
	
	@Inject
	private ComSubstVacationRepository comSubstVacationRepository;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;
	
	@Inject
	private InterimRecAbasMngRepository interimRecAbasMngRepository;
	
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;
	
	@Inject
	private ClosureRepository closureRepo;

	@Inject
	private SysWorkplaceAdapter syWorkplaceAdapter;

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;

	public DisplayRemainingNumberDataInformation getFurikyuMngDataExtractionUpdate(String empId, boolean isPeriod) {		
		String cid = AppContexts.user().companyId();
		List<PayoutManagementData> payoutManagementData;
		List<SubstitutionOfHDManagementData> substitutionOfHDManagementData;
		List<PayoutSubofHDManagement> payoutSubofHDManagementLinkToPayout = new ArrayList<PayoutSubofHDManagement>();
		Integer closureId;
		// Step 振休管理データを管理するかチェック
		EmploymentManageDistinctDto emplManage = getEmploymentManageDistinct(cid, empId);
		// Step 取得した管理区分をチェック
		if (emplManage.getIsManage() == ManageDistinct.NO) {
			throw new BusinessException("Msg_1731", "Com_SubstituteHoliday");
		} else {
			// Step Input．設定期間区分をチェック
			// select 全ての状況
			if (isPeriod) {
				// Step ドメイン「振出管理データ」を取得する
				payoutManagementData = payoutManagementDataRepository.getSid(cid, empId);
				// Step ドメイン「振休管理データ」を取得する
				substitutionOfHDManagementData = substitutionOfHDManaDataRepository.getBysiD(cid, empId);
			} else {
				// Step ドメイン「振出管理データ」を取得する
				payoutManagementData = payoutManagementDataRepository.getBySidAndStateAtr(cid, empId);
				// Step ドメイン「振休管理データ」を取得する
				substitutionOfHDManagementData = substitutionOfHDManaDataRepository.getBysiDAndAtr(cid, empId);
			}
			// Step ドメイン「振出振休紐付け管理」を取得する
			if (!payoutManagementData.isEmpty()) {
				List<GeneralDate> listPayoutDate = payoutManagementData.stream().map(x -> x.getPayoutDate().getDayoffDate().orElse(null))
						.collect(Collectors.toList());
				payoutSubofHDManagementLinkToPayout.addAll(payoutSubofHDManaRepository.getByListOccDate(empId,
						listPayoutDate));
			}
			if (!substitutionOfHDManagementData.isEmpty()) {
				List<GeneralDate> listSubDate = substitutionOfHDManagementData.stream().map(x -> {
					return x.getHolidayDate().getDayoffDate().orElse(null);
				}).collect(Collectors.toList());
				payoutSubofHDManagementLinkToPayout.addAll(payoutSubofHDManaRepository.getByListDate(empId, listSubDate));
			}

			Optional<SWkpHistImport> sWkpHistImport = syWorkplaceAdapter.findBySid(empId, GeneralDate.today());
			List<String> employeeIds = new ArrayList<>();
			employeeIds.add(empId);
			List<PersonEmpBasicInfoImport> employeeBasicInfo = empEmployeeAdapter.getPerEmpBasicInfo(employeeIds);
			PersonEmpBasicInfoImport personEmpBasicInfoImport = null;
			if (!employeeBasicInfo.isEmpty()) {
				personEmpBasicInfoImport = employeeBasicInfo.get(0);
			}
			// Step 振休管理設定を取得する
			ManagementClassificationSetting manageSetting = this.getClassifiedManagementSetup(cid, emplManage.getEmploymentCode());
			// Step 締めIDを取得する
			closureId = getClosureId(empId, emplManage.getEmploymentCode());
						
			Optional<PresentClosingPeriodExport> closing = this.find(cid, closureId);
			if(!closing.isPresent()) return null;
			PresentClosingPeriodExport close =  closing.get();
			// Step 振休残数データ情報を作成
			List<RemainInfoData> lstRemainData = this.getRemainInfoData(payoutManagementData,
					substitutionOfHDManagementData,
					payoutSubofHDManagementLinkToPayout,
					empId,
					close.getClosureStartDate());
			List<RemainInfoDto> lstDataRemainDto =  lstRemainData.stream().map(item -> {
				RemainInfoDto itemData =RemainInfoDto.builder()
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
						.legalDistinction(item.getLegalDistinction().orElse(null))
						.build();
				return itemData;
			}).collect(Collectors.toList());
			
			DisplayRemainingNumberDataInformation result = DisplayRemainingNumberDataInformation.builder()
					.employeeId(empId)
					.totalRemainingNumber(0d)
					.expirationDate(manageSetting.getSubstVacationSetting() == null
						? null
						: manageSetting.getSubstVacationSetting().getExpirationDate().value)
					.remainingData(lstDataRemainDto)
					.startDate(closing.get().getClosureStartDate())
					.endDate(closing.get().getClosureEndDate())
					.closureId(closureId)
					.wkHistory(sWkpHistImport.orElse(null))
					.employeeCode(personEmpBasicInfoImport == null ? "" : personEmpBasicInfoImport.getEmployeeCode())
					.employeeName(personEmpBasicInfoImport == null ? "" : personEmpBasicInfoImport.getBusinessName())
					.build();
			return result;
		}
	}

	// Step 振休管理データを管理するかチェック
	public EmploymentManageDistinctDto getEmploymentManageDistinct(String compId, String empId) {
		// Step 管理区分 ＝ 管理しない
		GeneralDate now = GeneralDate.today();
		// Step 社員IDから全ての雇用履歴を取得
		List<EmploymentHistShareImport> empHistShrImp = this.shareEmploymentAdapter.findByEmployeeIdOrderByStartDate(empId);
		// Step 取得した社員の雇用履歴をチェック
		if (empHistShrImp.isEmpty()) {
			// Step エラーメッセージ(Msg_1306)を表示する
			throw new BusinessException("Msg_1306");
		}
		// 取得した社員の雇用履歴をチェック
		// 雇用コードを取得(雇用コード　＝ 取得した社員の雇用履歴．期間．開始日　＜＝　システム日付 AND　取得した社員の雇用履歴．期間．終了日　＞＝システム日付)
		Optional<EmploymentHistShareImport> employmentHist = empHistShrImp.stream()
				.filter(item -> item.getPeriod().start().before(now) && item.getPeriod().end().after(now))
				.findFirst();
		ManagementClassificationSetting manageSetting = null;
		if (employmentHist.isPresent()) {
			// 管理区分設定を取得する (Get the setup of classified management)
			manageSetting = this.getClassifiedManagementSetup(compId, employmentHist.get().getEmploymentCode());
		}

		return EmploymentManageDistinctDto.builder()
				.employmentCode(employmentHist.map(EmploymentHistShareImport::getEmploymentCode).orElse(null))
				.isManage(manageSetting == null ? ManageDistinct.NO : manageSetting.getManageDistinct())
				.build();
	}

	// Step 管理区分設定を取得する
	public ManagementClassificationSetting getClassifiedManagementSetup(String compId, String empCode) {
		// Step ドメインモデル「雇用振休管理設定」を取得
		Optional<EmpSubstVacation> optEmpSubData = empSubstVacationRepository.findById(compId, empCode);
		// Step ドメインモデル「振休管理設定」を取得
		Optional<ComSubstVacation> optComSubData = comSubstVacationRepository.findById(compId);
		// ・振休取得・使用方法　＝　取得した「振休管理設定」．振休取得・使用方法
		SubstVacationSetting substVacationSetting = optComSubData.map(ComSubstVacation::getSetting).orElse(null);
		ManageDistinct manageDistinct = ManageDistinct.NO;
		if (optEmpSubData.isPresent()) {
			manageDistinct = optEmpSubData.get().getManageDistinct();
		} else {
			manageDistinct = optComSubData.map(ComSubstVacation::getManageDistinct).orElse(ManageDistinct.NO);
		}
		return ManagementClassificationSetting.builder()
				.substVacationSetting(substVacationSetting)
				.manageDistinct(manageDistinct)
				.build();
	}
	
	// Step 月初の振休残数を取得
	public double getNumberOfRemainingHolidays(String empId) {
		// Step ドメインモデル「振出管理データ」を取得
		List<PayoutManagementData> payoutManagementData = payoutManagementDataRepository.getByStateAtr(empId, DigestionAtr.UNUSED);
		// Step 取得した「振出管理データ」の未使用日数を合計
		double totalUnUseDays = 0;
		for (PayoutManagementData py : payoutManagementData) {
			totalUnUseDays += py.getUnUsedDays().v();
		}
		// Step ドメインモデル「振休管理データ」を取得
		List<SubstitutionOfHDManagementData> substitutionOfHDManagementData = substitutionOfHDManaDataRepository
				.getByRemainDays(empId, 0);
		// Step 取得した「振休管理データ」の未相殺日数を合計
		double remainDays = 0;
		for (SubstitutionOfHDManagementData absComfirmDay : substitutionOfHDManagementData) {
			remainDays += absComfirmDay.getRemainDays().v();
		}
		// Step 振休発生数合計－振休使用数合計を返す
		return totalUnUseDays - remainDays;
	}	
	
	public Double getNumberOfDayLeft(String sID) {
		String cid = AppContexts.user().companyId();
		Double totalUnUseDay = 0.0;
		Double  totalUndeliveredDay = 0.0;
		
		List<PayoutManagementData> payoutManagementData = payoutManagementDataRepository.getSidWithCod(cid, sID, 0);
		List<SubstitutionOfHDManagementData> substitutionOfHDManagementData = substitutionOfHDManaDataRepository.getBysiDRemCod(cid, sID);
		
		for (PayoutManagementData item : payoutManagementData) {
			totalUnUseDay += item.getUnUsedDays().v();
		}
		
		for (SubstitutionOfHDManagementData item : substitutionOfHDManagementData) {
			totalUndeliveredDay += item.getRemainDays().v();
		}
		
		return totalUnUseDay - totalUndeliveredDay;
	}
	
	public int getExpirationDate(String sid, String empCD) {
		String cid = AppContexts.user().companyId();
		EmpSubstVacation empSubstVacation;
		ComSubstVacation comSubstVacation;
		int expirationDate = 0;
		
		// get scd
		if (empCD != null) {
			if(empSubstVacationRepository.findById(cid, empCD).isPresent()) {
				empSubstVacation = empSubstVacationRepository.findById(cid, empCD).get();
			//	expirationDate = empSubstVacation.getSetting().getExpirationDate().value;
			} else if (comSubstVacationRepository.findById(cid).isPresent()){
				comSubstVacation = comSubstVacationRepository.findById(cid).get();
				expirationDate = comSubstVacation.getSetting().getExpirationDate().value;
			}
		} else {
			if (comSubstVacationRepository.findById(cid).isPresent()){
				comSubstVacation = comSubstVacationRepository.findById(cid).get();
				expirationDate = comSubstVacation.getSetting().getExpirationDate().value;
			}
		}
		
		return expirationDate;
	}
	
	// Step 締めIDを取得する
	public Integer getClosureId(String sid, String empCD) {
		String cid = AppContexts.user().companyId();
		Integer closureId = null;
		
		if (empCD != null) {
			if(closureEmploymentRepository.findByEmploymentCD(cid, empCD).isPresent()) {
				closureId = closureEmploymentRepository.findByEmploymentCD(cid, empCD).get().getClosureId();
			}
		}
		 
		return closureId;
	}
	
	// UKDesign.UniversalK.就業.KDM_残数管理 (Quản lý số dư).KDM001_残数管理データの登録 (Đăng ký dữ liệu quản lý số dư).Ａ：振休管理.アルゴリズム.Ａ：振休管理データ抽出処理.振休残数データ情報を作成.振休残数データ情報を作成
	public List<RemainInfoData> getRemainInfoData(List<PayoutManagementData> payoutManagementData,
			List<SubstitutionOfHDManagementData> substitutionOfHDManagementData, 
			List<PayoutSubofHDManagement> payoutSubofHDManagementLinkToPayout,
			String empId,
			GeneralDate startDate) {
		String cid = AppContexts.user().companyId();
		// List＜残数データ情報＞を作成
		List<RemainInfoData> lstRemainInfoData = new ArrayList<RemainInfoData>();
		Integer mergeCell = 0;
		// Input．List<振出管理データ＞をループする
		for (PayoutManagementData itemPayout : payoutManagementData) {
			// Input.List＜振出振休紐付け管理＞を絞り込みする
			List<PayoutSubofHDManagement> listPayoutSub = payoutSubofHDManagementLinkToPayout.stream()
					.filter(item -> item.getAssocialInfo().getOutbreakDay().equals(itemPayout.getPayoutDate().getDayoffDate().orElse(null)))
					.collect(Collectors.toList());
			// 	絞り込みした「振出振休紐付け管理」をチェック
			if (!listPayoutSub.isEmpty()) {
				GeneralDate useDate = listPayoutSub.stream()
						.map(x -> x.getAssocialInfo().getDateOfUse())
						.filter(Objects::nonNull).findFirst().orElse(null);
				// Input．List＜振休管理データ＞を絞り込み
				Optional<SubstitutionOfHDManagementData> optSubstitution = substitutionOfHDManagementData.stream()
						.filter(item -> item.getHolidayDate().getDayoffDate()
								.map(date -> date.equals(useDate)).orElse(false))
						.findFirst();
				Optional<InterimAbsMng> optInterimDayOff = Optional.empty();
				if (!optSubstitution.isPresent()) {
					// Step ドメインモデル「振休管理データ」を取得
					optSubstitution = this.substitutionOfHDManaDataRepository
							.find(cid, empId, useDate);
					// 取得した振休管理データをチェック
					if (!optSubstitution.isPresent()) {
						// ドメイン「暫定振休管理データ」を取得
						optInterimDayOff = this.interimRecAbasMngRepository.getAbsBySidDateList(empId, Arrays.asList(useDate))
								.stream().findFirst();
					}
				}
				if (optSubstitution.isPresent() || optInterimDayOff.isPresent()) {
					Optional<PayoutSubofHDManagement> optPayoutSub = listPayoutSub.stream()
							.filter(data -> data.getAssocialInfo().getDateOfUse().equals(useDate))
							.findFirst();
					Optional<GeneralDate> digestionDay = optPayoutSub.map(data -> data.getAssocialInfo().getDateOfUse());
					Optional<Double> digestionDays = optPayoutSub.map(data -> data.getAssocialInfo().getDayNumberUsed().v());
					Optional<String> digestionId = Optional.of(optSubstitution.map(data -> data.getSubOfHDID())
							.orElse(optInterimDayOff.map(data -> data.getRemainManaID()).orElse(null)));
					// 残数データ情報を作成
					RemainInfoData itemRemainInfo = RemainInfoData.builder()
							.accrualDate(itemPayout.getPayoutDate().getDayoffDate())
							.deadLine(Optional.of(itemPayout.getExpiredDate()))
							.occurrenceDay(Optional.of(itemPayout.getOccurredDays().v()))
							.digestionDay(digestionDay)
							.digestionDays(digestionDays)
							.legalDistinction(Optional.of(itemPayout.getLawAtr().value))
							.occurrenceId(Optional.of(itemPayout.getPayoutId()))
							.digestionId(digestionId)
							.dayLetf(itemPayout.getExpiredDate().afterOrEquals(startDate)
									? itemPayout.getUnUsedDays().v()
									: 0.0)
							.usedDay(itemPayout.getExpiredDate().afterOrEquals(startDate) ? 0.0
									: itemPayout.getUnUsedDays().v())
							.usedTime(0).occurrenceHour(Optional.empty()).digestionTimes(Optional.empty())
							.remainingHours(Optional.empty()).mergeCell(mergeCell)
							.build();
					// List＜残数データ情報＞に作成した「残数データ情報＞を追加
					lstRemainInfoData.add(itemRemainInfo);
				}
				mergeCell++;
				// Input．List<振出振休紐付け管理＞に絞り込みした「振出振休紐付け管理」を除く
				payoutSubofHDManagementLinkToPayout.removeIf(x -> listPayoutSub.contains(x));
				// Input．List＜振休管理データ＞に絞り込みした「振休管理データ」を除く
				SubstitutionOfHDManagementData subs = optSubstitution.orElse(null);
				substitutionOfHDManagementData.removeIf(y -> y.equals(subs));
			} else {
				// 残数データ情報を作成
				RemainInfoData itemRemainInfo = RemainInfoData.builder()
						.accrualDate(itemPayout.getPayoutDate().getDayoffDate())
						.deadLine(Optional.of(itemPayout.getExpiredDate()))
						.occurrenceDay(Optional.of(itemPayout.getOccurredDays().v())).digestionDay(Optional.empty())
						.digestionDays(Optional.empty()).legalDistinction(Optional.of(itemPayout.getLawAtr().value))
						.occurrenceId(Optional.of(itemPayout.getPayoutId())).digestionId(Optional.empty())
						.dayLetf(itemPayout.getExpiredDate().afterOrEquals(startDate)
								? itemPayout.getUnUsedDays().v()
								: 0.0)
						.usedDay(itemPayout.getExpiredDate().afterOrEquals(startDate) ? 0.0
								: itemPayout.getUnUsedDays().v())
						.usedTime(0).occurrenceHour(Optional.empty()).digestionTimes(Optional.empty())
						.remainingHours(Optional.empty()).mergeCell(mergeCell).build();
				// List＜残数データ情報＞に作成した「残数データ情報＞を追加
				lstRemainInfoData.add(itemRemainInfo);
				mergeCell++;
			}
		}
		// List＜振休管理データ＞をループする
		for (SubstitutionOfHDManagementData itemSubstitution : substitutionOfHDManagementData) {
			// List＜振出振休紐付け管理＞を絞り込みする
			List<PayoutSubofHDManagement> lstLinkToPayout = payoutSubofHDManagementLinkToPayout.stream()
					.filter(item -> itemSubstitution.getHolidayDate().getDayoffDate().isPresent()
							? itemSubstitution.getHolidayDate().getDayoffDate().get().equals(item.getAssocialInfo().getDateOfUse())
							: false)
					.collect(Collectors.toList());
			// 絞り込みした「振出振休紐付け管理」をチェック: あるの場合
			if (!lstLinkToPayout.isEmpty()) {
				List<GeneralDate> lstDayOff = lstLinkToPayout.stream().map(x -> {
					return x.getAssocialInfo().getOutbreakDay();
				}).collect(Collectors.toList());
				// ドメインモデル「振出管理データ」を取得する
				List<PayoutManagementData> listItemPayout = payoutManagementDataRepository
						.getByListPayoutDate(AppContexts.user().companyId(), empId, lstDayOff);
				// 残数データ情報を作成
				for (PayoutManagementData x : listItemPayout) {
					RemainInfoData itemRemainInfo = RemainInfoData.builder()
							.accrualDate(x.getPayoutDate().getDayoffDate()).deadLine(Optional.of(x.getExpiredDate()))
							.occurrenceDay(Optional.of(x.getOccurredDays().v()))
							.digestionDay(itemSubstitution.getHolidayDate().getDayoffDate())
							.digestionDays(Optional.of(itemSubstitution.getRequiredDays().v()))
							.legalDistinction(Optional.of(x.getLawAtr().value))
							.occurrenceId(Optional.of(x.getPayoutId()))
							.digestionId(Optional.of(itemSubstitution.getSubOfHDID()))
							.dayLetf(x.getExpiredDate().afterOrEquals(startDate) ? x.getUnUsedDays().v()
									: 0.0)
							.usedDay(x.getExpiredDate().afterOrEquals(startDate) ? 0.0
									: x.getUnUsedDays().v())
							.usedTime(0).occurrenceHour(Optional.empty()).digestionTimes(Optional.empty())
							.remainingHours(Optional.empty()).mergeCell(mergeCell).build();
					// List＜残数データ情報＞に作成した残数データ情報を追加
					lstRemainInfoData.add(itemRemainInfo);
				}
				mergeCell++;
			} else {
				// 絞り込みした「振出振休紐付け管理」をチェック: ないの場合
				// 残数データ情報を作成
				RemainInfoData itemRemainInfo = RemainInfoData.builder().accrualDate(Optional.empty())
						.deadLine(Optional.empty()).occurrenceDay(Optional.empty())
						.digestionDay(itemSubstitution.getHolidayDate().getDayoffDate())
						.digestionDays(Optional.of(itemSubstitution.getRequiredDays().v()))
						.legalDistinction(Optional.empty()).occurrenceId(Optional.empty())
						.digestionId(Optional.of(itemSubstitution.getSubOfHDID())).dayLetf(0.0).usedDay(0.0).usedTime(0)
						.occurrenceHour(Optional.empty()).digestionTimes(Optional.empty())
						.remainingHours(Optional.empty()).mergeCell(mergeCell).build();
				// List＜残数データ情報＞に作成した残数データ情報を追加
				lstRemainInfoData.add(itemRemainInfo);
				mergeCell++;
			}
		}
		// 作成したList＜残数データ情報＞を返す
		if (!lstRemainInfoData.isEmpty()) {
			lstRemainInfoData.sort((a, b) -> {
				return a.getAccrualDate().isPresent() && b.getAccrualDate().isPresent()
						? a.getAccrualDate().get().compareTo(b.getAccrualDate().get())
						: 0;
			});
		}
		return lstRemainInfoData;
	}
	
	public Optional<PresentClosingPeriodExport> find(String cId, int closureId) {
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
}

