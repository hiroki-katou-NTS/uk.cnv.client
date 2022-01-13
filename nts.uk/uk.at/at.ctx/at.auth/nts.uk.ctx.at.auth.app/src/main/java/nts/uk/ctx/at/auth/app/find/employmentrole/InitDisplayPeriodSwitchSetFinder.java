package nts.uk.ctx.at.auth.app.find.employmentrole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.DateProcessed;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDataDto;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDto;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSet;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSetRepo;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureInfo;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class InitDisplayPeriodSwitchSetFinder {
	@Inject
	private InitDisplayPeriodSwitchSetRepo repo;
	@Inject
	private ClosureRepository closureRepo;
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public InitDisplayPeriodSwitchSetDto targetDateFromLogin() {
		//RQ609 Đang viết ở App chứ không phải là ở Pub
		String companyID = AppContexts.user().companyId();
		String employeeID = AppContexts.user().employeeId();
		String attendanceID = AppContexts.user().roles().forAttendance();
		GeneralDate systemDate = GeneralDate.today();
		InitDisplayPeriodSwitchSetDto data = new InitDisplayPeriodSwitchSetDto(1, new ArrayList<>());
		// 全締めの当月と期間を取得する
		// InitDisplayPeriodSwitchSetDto data = new
		// InitDisplayPeriodSwitchSetDto();
		List<ClosureInfo> listClosureInfo = ClosureService.getAllClosureInfo(
				ClosureService.createRequireM2(closureRepo));
		List<DateProcessed> listDate = new ArrayList<>();
		List<DateProcessed> listDateProcessed = listClosureInfo.stream().map(i -> {
			return new DateProcessed(i.getClosureId().value, i.getCurrentMonth(), i.getPeriod());
		}).collect(Collectors.toList());
		// ドメインモデル「初期表示期間切替設定」を取得する
		/**
		 * 条件： 会社ID←ログイン会社ID ロールID←ログインユーザコンテキスト．就業ロールID
		 **/
		Optional<InitDisplayPeriodSwitchSet> optDisSwitchSet = repo.findByKey(companyID, attendanceID);

		if (!optDisSwitchSet.isPresent()) {
			listDateProcessed = listClosureInfo.stream().map(i -> {
				return new DateProcessed(i.getClosureId().value, i.getCurrentMonth(), i.getPeriod());
			}).collect(Collectors.toList());
			/**
			 * Enum 当月 = 1 翌月 = 2
			 **/
			data = new InitDisplayPeriodSwitchSetDto(1, listDateProcessed);
			return data;
		} else {
			val require = ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter);
			// 社員に対応する処理締めを取得する
			Closure closure = ClosureService.getClosureDataByEmployee(
					require, new CacheCarrier(), employeeID, systemDate);
			// 当月・翌月を判断する
			GeneralDate endDate = listClosureInfo.stream().filter(x -> x.getClosureId().value == closure.getClosureId().value)
					.findFirst().get().getPeriod().end();
			int switchDate = optDisSwitchSet.get().getDay();
			if (endDate.addDays(switchDate).beforeOrEquals(systemDate)) {
				for (ClosureInfo item : listClosureInfo) {
					DatePeriod datePeriod = ClosureService.getClosurePeriod(require, item.getClosureId().value,
							item.getCurrentMonth().addMonths(1));
					DateProcessed endDateNextMonth = new DateProcessed(item.getClosureId().value,
							item.getCurrentMonth().addMonths(1), datePeriod);
					listDate.add(endDateNextMonth);
				}
				data = new InitDisplayPeriodSwitchSetDto(2, listDate);
			} else {
				data = new InitDisplayPeriodSwitchSetDto(1, listDateProcessed);
			}
			return data;
		}
	}
	
	public InitDisplayPeriodSwitchSetDataDto getInitDisplayPeriodSwitchSetData(String companyID, String roleID) {
		Optional<InitDisplayPeriodSwitchSet> initDisplayPeriodSwitchSet = repo.findByKey(companyID, roleID);
		if (!initDisplayPeriodSwitchSet.isPresent()) {
			return null;
		}
		
		return new InitDisplayPeriodSwitchSetDataDto(
				initDisplayPeriodSwitchSet.get().getCompanyID(),
				initDisplayPeriodSwitchSet.get().getRoleID(),
				initDisplayPeriodSwitchSet.get().getDay());
	}
	
	public List<InitDisplayPeriodSwitchSetDataDto> getInitDisplayPeriodSwitchSetByCid(String cid) {
		return this.repo.findByCid(cid).stream()
				.map(x -> new InitDisplayPeriodSwitchSetDataDto(
						x.getCompanyID(),
						x.getRoleID(),
						x.getDay()))
				.collect(Collectors.toList());
	}
}
