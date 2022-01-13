package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * ValueObject: 時間帯別勤怠の削除
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.時間帯別勤怠の削除
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class AttendanceByTimezoneDeletion implements DomainValue {

	// 枠NO
	private final SupportFrameNo supportFrameNo;

	// 状態
	private final AttendanceDeletionStatusEnum deletionStatus;

	/**
	 * [1] 勤怠情報を削除する 該当する応援勤務枠NOに該当する日別実績の応援作業別勤怠を削除する
	 * @param require
	 * @param sId
	 * @param ymd
	 * @return
	 */
	public List<AtomTask> deleteAttendanceInfo(Require require, String sId, GeneralDate ymd) {
		List<AtomTask> atomTasks = new ArrayList<>();

		// atomTask.add( require.日別実績の応援作業別勤怠時間帯を削除する(社員ID, 年月日, @枠NO) )
		atomTasks.add(AtomTask.of(() -> require.deleteBySupFrameNo(sId, ymd, this.supportFrameNo)));

		// atomTask.add( require.編集状態を削除する(社員ID, 年月日, [2]勤怠項目一覧を取得する()) )
		atomTasks.add(AtomTask.of(() -> require.deleteByListItemId(sId, ymd, require.getAttendanceItemIds(this.supportFrameNo))));
		
		return atomTasks;
	}
	
	/**
	 * [2] 勤怠項目一覧を取得する
	 * @param require
	 * @return
	 */
	public List<Integer> getItemIds(Require require) {
		return require.getAttendanceItemIds(this.supportFrameNo);
	}
	
	/**
	 * [3] 勤怠情報を削除するかどうか判断する
	 * @return
	 */
	public boolean isNeedDeletingAttInfo() {
		return this.deletionStatus.equals(AttendanceDeletionStatusEnum.COMPLETE);
	}
	
	/**
	 * [4] 編集状態のみ削除するかどうか判断する
	 * @return
	 */
	public boolean isNeedDeletingEditedStatus() {
		return this.deletionStatus.equals(AttendanceDeletionStatusEnum.OVERWRITE);
	}

	
	// ■Public
	// ■Require
	public static interface Require {
		// [R-1] 日別実績の応援作業別勤怠時間帯を削除する
		// 日別実績の応援作業別勤怠時間帯Repository.削除する(社員ID, 年月日, 応援勤務枠NO)
		void deleteBySupFrameNo(String sId, GeneralDate ymd, SupportFrameNo supportFrameNo);

		// [R-2] 編集状態を削除する
		// 日別実績の編集状態Repository.削除する(社員ID, 年月日, List<勤怠項目ID>)
		void deleteByListItemId(String sId, GeneralDate ymd, List<Integer> itemIdList);

		// [R-3] 応援作業の勤怠項目IDを取得する
		// 勤怠項目IDを取得する.応援作業の勤怠項目一覧を取得する(@枠NO)
		List<Integer> getAttendanceItemIds(SupportFrameNo supportFrameNo);
	}
}
