package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.GetAttendanceItemIdService;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceByTimezoneDeletion;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.DeleteAttendancesByTimezone;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.DeleteAttendancesByTimezoneRepo;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.dailyrecord.EditStateOfDailyRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * 
 * @author sonnlb
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.App.時間帯別勤怠を削除する.時間帯別勤怠を削除する
 */
@Stateless
public class DeleteTimeZoneAttendanceCommandHandler extends CommandHandler<DeleteTimeZoneAttendanceCommand> {

	@Inject
	private DeleteAttendancesByTimezoneRepo repo;

	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenWorkRepo;

	@Inject
	private EditStateOfDailyRepository editStateRepo;

	@Override
	protected void handle(CommandHandlerContext<DeleteTimeZoneAttendanceCommand> context) {

		DeleteTimeZoneAttendanceCommand cmd = context.getCommand();
		// 1. 取得する(社員ID,年月日リスト)
		List<DeleteAttendancesByTimezone> deletes = this.repo.get(cmd.getEmployeeId(), cmd.getChangedDates());

		List<AtomTask> deleteAtoms = new ArrayList<AtomTask>();

		// 2.削除する(require)(List<AtomTask>)
		deletes.forEach(d -> {
			deleteAtoms.addAll(d.deleteAttendance(new AttendanceByTimezoneDeletionImpl(ouenWorkRepo, editStateRepo)));
		});
		// 3. persist
		// 3.1 複数日を削除する (社員ID,年月日リスト)
		transaction.execute(() -> {
			deleteAtoms.forEach(atom -> {
				atom.run();
			});
		}); 
		
	}

	@AllArgsConstructor
	private class AttendanceByTimezoneDeletionImpl implements AttendanceByTimezoneDeletion.Require {

		private OuenWorkTimeSheetOfDailyRepo ouenWorkRepo;

		private EditStateOfDailyRepository editStateRepo;

		@Override
		public void deleteBySupFrameNo(String sId, GeneralDate ymd, SupportFrameNo supportFrameNo) {
			this.ouenWorkRepo.deleteBySupFrameNo(sId, ymd, supportFrameNo);
		}

		@Override
		public void deleteByListItemId(String sId, GeneralDate ymd, List<Integer> itemIdList) {
			this.editStateRepo.deleteByListItemId(sId, ymd, itemIdList);
		}

		@Override
		public List<Integer> getAttendanceItemIds(SupportFrameNo supportFrameNo) {
			return GetAttendanceItemIdService.getAttendanceItemIds(Arrays.asList(supportFrameNo));
		}

	}
}
