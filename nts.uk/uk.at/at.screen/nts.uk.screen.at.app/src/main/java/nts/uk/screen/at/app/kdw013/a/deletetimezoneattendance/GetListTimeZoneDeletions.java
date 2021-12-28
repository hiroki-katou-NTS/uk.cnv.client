package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.GetAttendanceItemIdService;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceByTimezoneDeletion;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.DeleteAttendancesByTimezoneRepo;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.dailyrecord.EditStateOfDailyRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * 
 * @author sonnlb
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.App.編集状態を削除する対象勤怠項目を取得する
 */
@Stateless
public class GetListTimeZoneDeletions {

	@Inject
	private DeleteAttendancesByTimezoneRepo repo;
	
	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenWorkRepo;
	
	@Inject
	private EditStateOfDailyRepository editStateRepo;

	public Map<GeneralDate, List<Integer>> get(String empTarget, List<GeneralDate> dateLst) {

		Map<GeneralDate, List<Integer>> result = new HashMap<>();

		// 1. 取得する(社員ID,年月日リスト)
		this.repo.get(empTarget, dateLst).forEach(d -> {

			result.put(d.getYmd(),
					d.getAttendanceItems(new AttendanceByTimezoneDeletionImpl(ouenWorkRepo, editStateRepo)));
			// 2. 勤怠項目ID一覧を取得する()
		});
		return result;
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
