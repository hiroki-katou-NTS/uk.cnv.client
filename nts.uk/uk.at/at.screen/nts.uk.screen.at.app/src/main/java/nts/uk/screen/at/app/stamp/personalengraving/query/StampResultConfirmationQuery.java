package nts.uk.screen.at.app.stamp.personalengraving.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordWorkFinder;
import nts.uk.ctx.at.record.app.find.stamp.management.DisplayScreenStampingResultDto;
import nts.uk.ctx.at.record.app.find.stamp.management.DisplayScreenStampingResultFinder;
import nts.uk.ctx.at.record.dom.adapter.workplace.SWkpHistRcImported;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ConfirmStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.ConfirmStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.DailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.IGetDailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.StatusActualDay;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.ConfirmStatusOfDayService;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemUtil;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author anhdt
 * 打刻結果の確認及び実績の確認画面を取得する
 */
@Stateless
public class StampResultConfirmationQuery {
	
	@Inject
	private DisplayScreenStampingResultFinder displayScreenStamping;
	
	@Inject
	private CompanyDailyItemService companyDailyItemService;
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	
	@Inject
	private WorkTimeSettingRepository workTimeRepo;
	
	@Inject
	private DailyRecordWorkFinder fullFinder;
	
	@Inject
	private ClosureService closereSv;
	
	@Inject
	private SyWorkplaceAdapter syWorkplaceAdapter;
	
	@Inject
	private ConfirmStatusActualDayChange confirmStatusActualDayChange;
	
	@Inject
	private IGetDailyLock iGetDailyLock;
	
	public StampResultConfirmDto getStampResultConfirm(StampResultConfirmRequest param) {
		String cid = AppContexts.user().companyId();
		String authorityId = AppContexts.user().roles().forAttendance();
		String sid = AppContexts.user().employeeId();
		
		// 1
		List<DisplayScreenStampingResultDto> screenDisplays = displayScreenStamping.getDisplay(param.toStampDatePeriod());
		
		// 2
		ConfirmStatusOfDayRequiredImpl required = new ConfirmStatusOfDayRequiredImpl(closereSv, syWorkplaceAdapter, confirmStatusActualDayChange, iGetDailyLock);
		ConfirmStatusActualResult confirmStatusAcResults = ConfirmStatusOfDayService.get(required, cid, sid, GeneralDateTime.now().toDate());
		
		List<AttItemName> getDailyItems = companyDailyItemService.getDailyItems(cid, Optional.ofNullable(authorityId) , param.getAttendanceItems(), Collections.emptyList());
		
		// 3
		List<String> sids = new ArrayList<>();
		sids.add(sid);
		DailyModifyResult dailyResult = AttendanceItemUtil.toItemValues(this.fullFinder.find(sids, param.toStampDatePeriod()), param.getAttendanceItems())
			.entrySet().stream().map(c -> DailyModifyResult.builder().items(c.getValue())
						.workingDate(c.getKey().workingDate()).employeeId(c.getKey().employeeId()).completed())
				.findFirst().orElse(null);
		List<ItemValue> itemValues = dailyResult != null ? dailyResult.getItems() : Collections.emptyList();
		// 4
		List<String> itemIds = new ArrayList<>();
		itemIds.add(!CollectionUtil.isEmpty(itemValues) ? itemValues.stream().filter(i -> i.getItemId() == 28).findFirst().orElse(null).getValue() : "");
		List<WorkType> workTypes = workTypeRepo.getPossibleWorkType(cid, itemIds);
		// 5
		itemIds.clear();
		itemIds.add(!CollectionUtil.isEmpty(itemValues) ? itemValues.stream().filter(i -> i.getItemId() == 29).findFirst().orElse(null).getValue() : "");
		List<WorkTimeSetting> workTimes = workTimeRepo.getListWorkTimeSetByListCode(cid, itemIds);
		
		
		return new StampResultConfirmDto(screenDisplays, getDailyItems, itemValues, workTypes, workTimes, confirmStatusAcResults);
	}
	
	@AllArgsConstructor
	private class ConfirmStatusOfDayRequiredImpl implements ConfirmStatusOfDayService.Require {
		
		@Inject
		private ClosureService closereSv;
		
		@Inject
		private SyWorkplaceAdapter syWorkplaceAdapter;
		
		@Inject
		private ConfirmStatusActualDayChange confirmStatusActualDayChange;
		
		@Inject
		private IGetDailyLock iGetDailyLock;
		
		@Override
		public Closure getClosureDataByEmployee(String employeeId, GeneralDate baseDate) {
			return closereSv.getClosureDataByEmployee(employeeId, baseDate);
		}

		@Override
		public Optional<SWkpHistRcImported> findBySid(String employeeId, GeneralDate baseDate) {
			return syWorkplaceAdapter.findBySid(employeeId, baseDate);
		}

		@Override
		public DailyLock getDailyLock(StatusActualDay satusActual) {
			return iGetDailyLock.getDailyLock(satusActual);
		}

		@Override
		public List<ConfirmStatusActualResult> processConfirmStatus(String companyId, String empTarget,
				List<String> employeeIds, Optional<DatePeriod> periodOpt, Optional<YearMonth> yearMonthOpt,
				Optional<DailyLock> dailyLockOpt) {
			return confirmStatusActualDayChange.processConfirmStatus(companyId, empTarget, employeeIds, periodOpt, yearMonthOpt, dailyLockOpt);
		}
		
	}
	
}
