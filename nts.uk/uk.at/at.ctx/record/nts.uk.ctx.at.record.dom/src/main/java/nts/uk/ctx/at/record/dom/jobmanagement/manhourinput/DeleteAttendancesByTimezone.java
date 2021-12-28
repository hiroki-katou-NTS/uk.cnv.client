package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.時間帯別勤怠の削除一覧
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class DeleteAttendancesByTimezone implements DomainAggregate {

	// 社員ID
	private final String sId;

	// 年月日
	private final GeneralDate ymd;

	// 一覧: List<時間帯別勤怠の削除>
	private final List<AttendanceByTimezoneDeletion> attendanceDeletionLst;

	/**
	 * [1] 応援作業別勤怠を削除する
	 * 
	 * @param require
	 * @return atomTask一覧
	 */
	public List<AtomTask> deleteAttendance(AttendanceByTimezoneDeletion.Require require) {
		List<AtomTask> atomTasks = new ArrayList<>();

		if (this.attendanceDeletionLst.isEmpty()) {
			return atomTasks;
		}

		// $削除対象一覧 = @一覧.filter($.勤怠情報を削除するかどうか判断する() == true)
		List<AttendanceByTimezoneDeletion> attDeletionLst = this.attendanceDeletionLst.stream()
				.filter(a -> a.isNeedDeletingAttInfo() == true).collect(Collectors.toList());

		// atomTask.add( $削除対象一覧.map($削除する(require, @社員ID, @年月日)) )
		attDeletionLst.forEach(m -> atomTasks.addAll(m.deleteAttendanceInfo(require, sId, ymd)));

		// atomTask.add ( require.編集状態を削除する(@社員ID, @年月日, [2]勤怠項目一覧を取得する) )
		//atomTasks.add(AtomTask.of(() -> require.deleteByListItemId(sId, ymd, getAttendanceItems(require))));

		return atomTasks;
	}

	/**
	 * [2] 勤怠項目一覧を取得する
	 * 
	 * @param require
	 * @return
	 */
	public List<Integer> getAttendanceItems(AttendanceByTimezoneDeletion.Require require) {
		List<Integer> attItems = new ArrayList<>();

		if (this.attendanceDeletionLst.isEmpty()) {
			return attItems;
		}

		// $削除対象一覧 = @一覧.filter($.編集状態のみ削除するかどうか判断する() == true)
		List<AttendanceByTimezoneDeletion> attDeletionLst = this.attendanceDeletionLst.stream()
				.filter(a -> a.isNeedDeletingEditedStatus() == true).collect(Collectors.toList());

		// $削除対象一覧.map($.勤怠項目一覧を取得する(require))
		attDeletionLst.forEach(m -> attItems.addAll(m.getItemIds(require)));

		return attItems;
	}

}
