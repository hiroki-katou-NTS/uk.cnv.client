package nts.uk.ctx.at.record.app.command.kdp.kdp002.a;

import java.util.Optional;

import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.App.打刻データを登録する(個人利用)
 * @author lamvt
 *
 */
public class RegisterStampIndividualSampleCommand {

	/**
	 * 打刻日時
	 */
	private String datetime;

	/**
	 * 打刻する方法
	 */
	private Integer authcMethod;
	private Integer stampMeans;

	/**
	 * ボタン種類
	 */
	private Integer reservationArt;
	private boolean changeHalfDay;
	private Integer goOutArt;
	private Integer setPreClockArt;
	private Integer changeClockArt;
	private Integer changeCalArt;	
	
	//---------------------------------------
	
	
	/**
	 * 打刻日時
	 * @return
	 */
	public GeneralDateTime retriveDateTime() {
		return GeneralDateTime.fromString(this.datetime, "yyyy/MM/dd HH:mm:ss");
		
	}
	
	/**
	 * 打刻する方法
	 */
	public Relieve toRelieve() {
		return new Relieve(AuthcMethod.valueOf(authcMethod), StampMeans.valueOf(stampMeans));
	}
	
	/**
	 * ボタン種類
	 * @return
	 */
	public ButtonType toButtonType() {
		StampType stampType = new StampType(changeHalfDay, GoingOutReason.valueOf(goOutArt),
				SetPreClockArt.valueOf(setPreClockArt), ChangeClockArt.valueOf(changeClockArt),
				ChangeCalArt.valueOf(changeCalArt));
		return new ButtonType(ReservationArt.valueOf(reservationArt), Optional.of(stampType));
	}

}

