package nts.uk.ctx.at.record.dom.workrecord.actualsituation.approvalsituationmanagement.export.clearapprovalconfirm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.ApprovalStatusAdapter;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.ConfirmDeleteParamImport;
import nts.uk.ctx.at.record.dom.approvalmanagement.ApprovalProcessingUseSetting;
import nts.uk.ctx.at.record.dom.approvalmanagement.repository.ApprovalProcessingUseSettingRepository;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.IdentityProcessUseSet;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.ConfirmationMonthRepository;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentificationRepository;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentityProcessUseSetRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author thanhnx
 *
 */
@Stateless
public class ClearConfirmApprovalService {

	@Inject
	private IdentityProcessUseSetRepository identityProcessUseSetRepository;

	@Inject
	private IdentificationRepository identificationRepo;

	@Inject
	private ApprovalProcessingUseSettingRepository approvalUseSetRepo;

//	@Inject
//	private GetClosurePeriod getClosurePeriod;

	@Inject
	private ConfirmationMonthRepository confirmationMonthRepo;

	@Inject
	private ApprovalStatusAdapter approvalStatusAdapter;
	
	@Inject 
	private RecordDomRequireService requireService;

	/**
	 * ???????????????????????????
	 */
	public void clearConfirmApproval(String employeeId, List<GeneralDate> lstDate) {
		clearConfirmApproval(employeeId, lstDate, Optional.empty());
	}
	
	/**
	 * ???????????????????????????
	 */
	public void clearConfirmApproval(String employeeId, List<GeneralDate> lstDate, Optional<ApprovalProcessingUseSetting> approvalSetOpt) {
		clearConfirmApproval(employeeId, lstDate, Optional.empty(), Optional.empty());
	}
	
	/**
	 * ???????????????????????????
	 */
	@SuppressWarnings("unchecked")
	public void clearConfirmApproval(String employeeId, List<GeneralDate> lstDate, Optional<ApprovalProcessingUseSetting> approvalSetOpt,
			Optional<IdentityProcessUseSet> iPUS) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();
		String companyId = AppContexts.user().companyId();
		// ???????????????????????????????????????????????????????????????????????????
		Optional<IdentityProcessUseSet> indenUseSetOpt = iPUS.isPresent() ? iPUS : identityProcessUseSetRepository.findByKey(companyId);
		if (indenUseSetOpt.isPresent() && indenUseSetOpt.get().isUseConfirmByYourself()) {
			// ????????????????????????????????????????????????????????????
			identificationRepo.removeByEmpListDate(employeeId, lstDate);
		}

		// ???????????????????????????????????????????????????????????????????????????
		Optional<ApprovalProcessingUseSetting> approvalSettingOpt = approvalSetOpt.isPresent() ? approvalSetOpt : approvalUseSetRepo.findByCompanyId(companyId);
		if (approvalSettingOpt.isPresent() && approvalSettingOpt.get().getUseDayApproverConfirm()) {
			// [No.601]?????????????????????????????????
			lstDate.forEach(date -> {
				approvalStatusAdapter.deleteRootConfirmDay(employeeId, date);
			});
		}

		List<ClosurePeriod> lstClosureAll = new ArrayList<>();
		for (GeneralDate dateRefer : lstDate) {
			// ???????????? slow response 
			Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, employeeId, dateRefer);
			//?????????????????????????????????????????????????????????
			// Check exist and active
			if (closure == null || closure.getUseClassification()
					.equals(UseClassification.UseClass_NotUse)) {
				continue;
			}

			Optional<ClosurePeriod> cPeriod = closure.getClosurePeriodByYmd(dateRefer);
			if(!cPeriod.isPresent()) continue;
//			List<ClosurePeriod> closurePeriods = getClosurePeriod.get(companyId, employeeId, dateRefer,
//					Optional.empty(), Optional.empty(), Optional.empty());
			lstClosureAll.add(cPeriod.get());
		};

		lstClosureAll = lstClosureAll.stream().filter(
				distinctByKeys(ClosurePeriod::getClosureId, ClosurePeriod::getClosureDate, ClosurePeriod::getYearMonth))
				.collect(Collectors.toList());
		// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
		if (indenUseSetOpt.isPresent() && indenUseSetOpt.get().isUseIdentityOfMonth()) {
			// ????????????????????????????????????????????????????????????(Delete domain ????????????????????????)
			lstClosureAll.forEach(cls -> {
				confirmationMonthRepo.delete(companyId, employeeId, cls.getClosureId().value,
						cls.getClosureDate().getClosureDay().v(), cls.getClosureDate().getLastDayOfMonth(),
						cls.getYearMonth().v());
			});
		}

		if (approvalSettingOpt.isPresent() && approvalSettingOpt.get().getUseMonthApproverConfirm()) {
			// [No.602]?????????????????????????????????
			approvalStatusAdapter
					.deleteRootConfirmMonth(employeeId,
							lstClosureAll.stream()
									.map(cls -> new ConfirmDeleteParamImport(cls.getYearMonth(),
											cls.getClosureId().value, cls.getClosureDate()))
									.collect(Collectors.toList()));
		}

	}

	@SuppressWarnings("unchecked")
	private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
		final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

		return t -> {
			final List<?> keys = Arrays.stream(keyExtractors).map(ke -> ke.apply(t)).collect(Collectors.toList());

			return seen.putIfAbsent(keys, Boolean.TRUE) == null;
		};
	}
}
