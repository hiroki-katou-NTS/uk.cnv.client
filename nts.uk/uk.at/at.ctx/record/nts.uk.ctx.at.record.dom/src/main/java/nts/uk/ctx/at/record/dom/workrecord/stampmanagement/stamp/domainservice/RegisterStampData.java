package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
//import nts.uk.ctx.at.record.dom.stamp.management.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;

/**
 * @author ThanhNX
 *
 *         打刻を登録する
 *         UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻データ反映処理
 */
public class RegisterStampData {
	
	//打刻を登録する
	public static Optional<AtomTask> registerStamp(Require require, StampRecord stampRecord,
			Optional<Stamp> stamp) {
		if (stamp.isPresent() && require.existsStamp(stampRecord.getContractCode(), stampRecord.getStampNumber(),
				stampRecord.getStampDateTime(), stamp.get().getType().getChangeClockArt())) {
			return Optional.empty();
		}
		//if (employeeId.isPresent()) {
			// $AtomTask = AtomTask:
			AtomTask atomTask = AtomTask.of(() -> {
				// require.打刻記録を追加する(打刻記録)
				require.insert(stampRecord);
				// prv-1] 弁当を自動予約する(打刻)
				automaticallyBook(stampRecord, stamp);
				// if not 打刻.isEmpty
				if (stamp.isPresent()) {
					require.insert(stamp.get());
				}

			});
			// return 打刻データ反映処理結果#打刻データ反映処理結果($反映対象日, $AtomTask)
			return Optional.of(atomTask);
	}
	
	/**
	 * [prv-1] 弁当を自動予約する
	 * 
	 * @param stampRecord
	 * @param stamp
	 * @return
	 */
	private static Optional<AtomTask> automaticallyBook(StampRecord stampRecord, Optional<Stamp> stamp) {
		/*
		 * if(stampRecord.getRevervationAtr() == ReservationArt.NONE ) { //TODO chờ hàm
		 * gì đó được viết bởi đội khác, để tạm là option return Optional.empty(); }
		 * 
		 * if(!stamp.isPresent()) { return Optional.empty(); }
		 * 
		 * if(stamp.get().getType().checkBookAuto()) { //TODO chờ hàm gì đó được viết
		 * bởi đội khác, để tạm là option return Optional.empty(); }
		 */
		
		return Optional.empty();
	}

	
	public static interface Require extends ReflectDataStampDailyService.Require{

		// [R-1] 打刻記録を追加する JpaStampDakokuRepository
		public void insert(StampRecord stampRecord);

		// [R-2] 打刻を追加する JpaStampDakokuRepository
		public void insert(Stamp stamp);

		//[R-10] 既に同じ打刻あるか
		public boolean existsStamp(ContractCode contractCode, StampNumber stampNumber,
				GeneralDateTime dateTime, ChangeClockArt changeClockArt);
	}
}
