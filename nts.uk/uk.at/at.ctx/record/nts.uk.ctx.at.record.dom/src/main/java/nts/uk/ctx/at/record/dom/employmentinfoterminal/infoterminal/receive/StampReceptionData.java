package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;

/**
 * @author ThanhNX
 *
 *         打刻受信データ
 */
@Value
public class StampReceptionData implements ReceptionData {

	/**
	 * ID番号
	 */
	private final String idNumber;

	/**
	 * カード区分
	 */
	private final String cardCategory;

	/**
	 * シフト
	 */
	private final String shift;

	/**
	 * 出退区分
	 */
	private final String leavingCategory;

	/**
	 * 年月日
	 */
	private final String ymd;

	/**
	 * 応援コード
	 */
	private final String supportCode;

	/**
	 * 時分
	 */
	private final String time;

	/**
	 * 残業時間
	 */
	private final String overTimeHours;

	/**
	 * 深夜時間
	 */
	private final String midnightTime;

	public StampReceptionData(StampDataBuilder builder) {
		super();
		this.idNumber = builder.idNumber;
		this.cardCategory = builder.cardCategory;
		this.shift = builder.shift;
		this.leavingCategory = builder.leavingCategory;
		this.ymd = builder.ymd;
		this.supportCode = builder.supportCode;
		this.time = builder.time;
		this.overTimeHours = builder.overTimeHours;
		this.midnightTime = builder.midnightTime;
	}

	@Override
	public GeneralDateTime getDateTime() {
		int yy = GeneralDate.today().year() / 100;
		int ymdTemp = Integer.parseInt(String.valueOf(yy) + ymd);
		return GeneralDateTime.ymdhms(ymdTemp / 10000, (ymdTemp - (ymdTemp / 10000) * 10000) / 100, ymdTemp % 100,
				Integer.parseInt(time) / 100, Integer.parseInt(time) % 100, 0);
	}

	public String getOverTimeHours() {
		if (overTimeHours.trim().isEmpty())
			return "";
		return String.valueOf(
				(Integer.parseInt(overTimeHours.trim()) / 100) * 60 + (Integer.parseInt(overTimeHours.trim()) % 100));
	}

	public String getMidnightTime() {
		if (midnightTime.trim().isEmpty())
			return "";
		return String.valueOf(
				(Integer.parseInt(midnightTime.trim()) / 100) * 60 + (Integer.parseInt(midnightTime.trim()) % 100));
	}

	public String getSupportCode() {
		return supportCode.trim();
	}

	public String getShift() {
		return shift.trim();
	}
	
	public String getIdNumber() {
		return idNumber.trim();
	}
	
	public String getLeavingCategory() {
		return this.leavingCategory.trim();
	}

	// 認証方法
	public AuthcMethod convertAuthcMethod() {

		switch (CardCategory.valueStringOf(cardCategory.trim())) {

		case ID_INPUT:
			return AuthcMethod.ID_AUTHC;

		case MAGNETIC_CARD:
			return AuthcMethod.EXTERNAL_AUTHC;

		case IC_CARD:
			return AuthcMethod.IC_CARD_AUTHC;

		default:
			return AuthcMethod.VEIN_AUTHC;
		}
	}

	// 計算区分変更対象
	public ChangeCalArt convertChangeCalArt() {
		switch (LeaveCategory.valueStringOf(leavingCategory.trim())) {

		case WORK_FLEX:
		case LEAVE_FLEX:
		case WORK_FLEX_ENTRANCE:
			return ChangeCalArt.FIX;

		case LEAVE_OVERTIME:
			return ChangeCalArt.OVER_TIME;

		case EARLY:
		case EARLY_ENTRANCE:
			return ChangeCalArt.EARLY_APPEARANCE;

		case VACATION:
		case VACATION_ENTRANCE:
			return ChangeCalArt.BRARK;

		default:
			return ChangeCalArt.NONE;
		}
	}

	public static class StampDataBuilder {

		/**
		 * ID番号
		 */
		private String idNumber;

		/**
		 * カード区分
		 */
		private String cardCategory;

		/**
		 * シフト
		 */
		private String shift;

		/**
		 * 出退区分
		 */
		private String leavingCategory;

		/**
		 * 年月日
		 */
		private String ymd;

		/**
		 * 応援コード
		 */
		private String supportCode;

		/**
		 * 時分
		 */
		private String time;

		/**
		 * 残業時間
		 */
		private String overTimeHours;

		/**
		 * 深夜時間
		 */
		private String midnightTime;

		public StampDataBuilder(String idNumber, String cardCategory, String shift, String leavingCategory, String ymd,
				String supportCode) {
			this.idNumber = idNumber;
			this.cardCategory = cardCategory;
			this.shift = shift;
			this.leavingCategory = leavingCategory;
			this.ymd = ymd;
			this.supportCode = supportCode;
		}

		public StampDataBuilder time(String time) {
			this.time = time;
			return this;
		}

		public StampDataBuilder overTimeHours(String overTimeHours) {
			this.overTimeHours = overTimeHours;
			return this;
		}

		public StampDataBuilder midnightTime(String midnightTime) {
			this.midnightTime = midnightTime;
			return this;
		}

		public StampReceptionData build() {
			return new StampReceptionData(this);
		}
	}

}
