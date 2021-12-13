package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import nts.arc.task.tran.AtomTask;
//import nts.uk.ctx.at.record.dom.stamp.management.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * @author ThanhNX
 *
 *         打刻を登録する
 *         UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻データ反映処理
 */
public class RegisterStampData {
	
	//打刻を登録する
	public static Optional<AtomTask> registerStamp(Require require, Stamp stamp) {
		// $AtomTask = AtomTask:
		AtomTask atomTask = AtomTask.of(() -> {
			require.insert(stamp);
		});
		// return 打刻データ反映処理結果#打刻データ反映処理結果($反映対象日, $AtomTask)
		return Optional.of(atomTask);
	}

	
	public static interface Require extends ReflectDataStampDailyService.Require{

		// [R-2] 打刻を追加する JpaStampDakokuRepository
		public void insert(Stamp stamp);
	}
}
