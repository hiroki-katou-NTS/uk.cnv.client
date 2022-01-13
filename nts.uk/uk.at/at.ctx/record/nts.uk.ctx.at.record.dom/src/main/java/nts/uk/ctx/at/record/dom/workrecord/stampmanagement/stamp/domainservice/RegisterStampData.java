package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
//import nts.uk.ctx.at.record.dom.stamp.management.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;

/**
 * @author ThanhNX
 *
 *         打刻を登録する
 *         UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻データ反映処理
 */
public class RegisterStampData {
	
	//打刻を登録する
	public static Optional<AtomTask> registerStamp(Require require, Stamp stamp) {
		
		if (require.existsStamp(stamp.getContractCode(), stamp.getCardNumber(), stamp.getStampDateTime(),
				stamp.getType().getChangeClockArt())) {
			return Optional.empty();
		}
		
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
		
		//exists(契約コード,打刻カード番号, 打刻日時,時刻変更区分)
		public boolean existsStamp(ContractCode contractCode, StampNumber stampNumber, GeneralDateTime dateTime,
				ChangeClockAtr changeClockArt) ;
	}
}
