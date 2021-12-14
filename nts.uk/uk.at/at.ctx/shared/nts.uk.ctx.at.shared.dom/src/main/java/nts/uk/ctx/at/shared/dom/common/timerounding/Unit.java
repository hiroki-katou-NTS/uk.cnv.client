/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.common.timerounding;

import java.math.BigDecimal;

/**
 * The Enum Unit.
 */
// 単位
public enum Unit {

	/** The rounding time 1min. */
	// 時系列（原則）
	ROUNDING_TIME_1MIN(0, "1分", "Enum_RoundingTime_1Min"),

	/** The rounding time 5min. */
	ROUNDING_TIME_5MIN(1, "5分", "Enum_RoundingTime_5Min"),

	/** The rounding time 6min. */
	ROUNDING_TIME_6MIN(2, "6分", "Enum_RoundingTime_6Min"),

	/** The rounding time 10min. */
	ROUNDING_TIME_10MIN(3, "10分", "Enum_RoundingTime_10Min"),

	/** The rounding time 15min. */
	ROUNDING_TIME_15MIN(4, "15分", "Enum_RoundingTime_15Min"),

	/** The rounding time 20min. */
	ROUNDING_TIME_20MIN(5, "20分", "Enum_RoundingTime_20Min"),

	/** The rounding time 30min. */
	ROUNDING_TIME_30MIN(6, "30分", "Enum_RoundingTime_30Min"),

	/** The rounding time 60min. */
	ROUNDING_TIME_60MIN(7, "60分", "Enum_RoundingTime_60Min");

	
	/** The value. */
	public int value;

	/** The name id. */
	public String nameId;

	/** The description. */
	public String description;

	/** The Constant values. */
	private final static Unit[] values = Unit.values();

	/**
	 * Instantiates a new unit.
	 *
	 * @param value the value
	 * @param nameId the name id
	 * @param description the description
	 */
	private Unit(int value, String nameId, String description) {
		this.value = value;
		this.nameId = nameId;
		this.description = description;
	}

	/**
	 * Value of.
	 *
	 * @param value the value
	 * @return the unit
	 */
	public static Unit valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (Unit val : Unit.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
	
	public int asTime() {
	switch (this) {
	case ROUNDING_TIME_1MIN: return 1;
	case ROUNDING_TIME_5MIN: return 5;
	case ROUNDING_TIME_6MIN: return 6;
	case ROUNDING_TIME_10MIN: return 10;
	case ROUNDING_TIME_15MIN: return 15;
	case ROUNDING_TIME_20MIN: return 20;
	case ROUNDING_TIME_30MIN: return 30;
	case ROUNDING_TIME_60MIN: return 60;
	default: throw new RuntimeException("invalid value: " + this);
	}
}

	public static enum Direction {
		TO_FORWARD,
		TO_BACK;
	
		public int value() {
			return this == TO_FORWARD ? 1 : -1;
		}
	}

	public int round(int timeAsMinutes, Direction roundingDirection) {
		int minutesInHour = timeAsMinutes % 60;
		int hourPart = timeAsMinutes / 60;
	
		for (int minute = minutesInHour; ; minute += roundingDirection.value()) {
			if (minute % this.asTime() == 0) {
				return hourPart * 60 + minute;
			}
		}
	}
	
	
	/**
	 * 切り捨て
	 * @param timeAsMinutes
	 * @param roundingDirection
	 * @return
	 */
	public int roundDown(int timeAsMinutes) {
		/** 単位を求める */
		int unit = this.asTime();
		
		if (this == Unit.ROUNDING_TIME_1MIN) {
			return timeAsMinutes;
		}
		
		/** 【時間】÷単位の余りチェック */
		int remain = Math.abs(timeAsMinutes % unit);
		
		if (remain > 0) {
			/** 時間を切り捨てする */
			return downByUnit(timeAsMinutes, unit);
		}
		
		return timeAsMinutes;
	}
	
	public BigDecimal roundDownBigDecimal(BigDecimal timeAsMinutes) {

		return BigDecimal.valueOf(roundDown(timeAsMinutes.intValue()));
//		//マイナスの場合
//		if(timeAsMinutes.compareTo(BigDecimal.ZERO)<0) {
//			// 一旦、プラスの数値にする
//			BigDecimal result = timeAsMinutes.negate();
//			BigDecimal amari = result.remainder(BigDecimal.valueOf(this.asTime()));
//			if(amari.compareTo(BigDecimal.ONE)>0) {
//				// 切り捨て処理
//				result = result.subtract(amari).add(BigDecimal.valueOf(this.asTime()));
//			}
//			//再びマイナスの値に戻す
//			return result.negate();
//		}
//		//マイナスではない場合
//		BigDecimal amari = timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime()));
//		if(amari.compareTo(BigDecimal.ZERO)>0) {
//			//切り捨て処理
//			return timeAsMinutes.subtract(amari);
//		}
//		return timeAsMinutes;
	}
	
	
	
	/**
	 * 切り上げ
	 * @param timeAsMinutes
	 * @param roundingDirection
	 * @return
	 */
	public int roundUp(int timeAsMinutes) {
		/** 単位を求める */
		int unit = this.asTime();
		
		if (this == Unit.ROUNDING_TIME_1MIN) {
			return timeAsMinutes;
		}
		
		/** 【時間】÷単位の余りチェック */
		int remain = Math.abs(timeAsMinutes % unit);
		
		if (remain > 0) {
			/** 時間を切り上げ */
			return upByUnit(timeAsMinutes, unit);
		}
		
		return timeAsMinutes;
	}
	
	public BigDecimal roundUpBigDecimal(BigDecimal timeAsMinutes) {
		return BigDecimal.valueOf(roundUp(timeAsMinutes.intValue()));
//		//マイナスの場合
//		if(timeAsMinutes.compareTo(BigDecimal.ZERO)<0) {
//			// 一旦、プラスの数値にする
//			BigDecimal result = timeAsMinutes.negate();
//			BigDecimal amari = result.remainder(BigDecimal.valueOf(this.asTime()));
//			if(amari.compareTo(BigDecimal.ZERO)>0) {
//				// 切り上げ処理
//				result = result.subtract(amari);
//			}
//			//再びマイナスの値に戻す
//			return result.negate();
//		}
//		//マイナスではない場合
//		BigDecimal amari = timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime()));
//		if(amari.compareTo(BigDecimal.ZERO)>0) {
//			//切り上げ処理
//			return timeAsMinutes.subtract(amari).add(BigDecimal.valueOf(this.asTime()));
//		}
//		return timeAsMinutes;
	}
	
	
	
	/**
	 * 未満切捨以上切上
	 * @param timeAsMinutes
	 * @return
	 */
	public int roundDownOver(int timeAsMinutes) {
		
		/** 単位を求める */
		int unit = this.asTime();
		
		
		if (this != Unit.ROUNDING_TIME_15MIN && this != Unit.ROUNDING_TIME_30MIN) {
			return timeAsMinutes;
		}
		
		
		/** 切捨て～切り上げの範囲を求める */
		int border = unit * 2;

		/** 対象時間から範囲の倍数を求める */
		int multiple = timeAsMinutes / border;

		/** 切捨ての位置と切り上の位置を求める */
		int botBorder = multiple * border - unit;
		int topBorder = multiple * border + unit;

		/** 切り上げ、切捨てかを判断する */
		if (botBorder <= timeAsMinutes && timeAsMinutes < topBorder) {

			/** 切捨て */
			return multiple * border;
		} else {
			/**　切上げ */
			return timeAsMinutes > 0 ? (multiple + 1) * border : (multiple - 1) * border;
		}
	}

	/** 時間を切り捨てする */
	private int downByUnit(int timeAsMinutes, int unit) {
		/** 時間から単位分を求める */
		int perUnit = timeAsMinutes / unit;
		
		/** 時間を切り捨て */
		return timeAsMinutes < 0 ? unit * (perUnit - 1) : unit * perUnit;
	}

	/** 時間を切り上げする */
	private int upByUnit(int timeAsMinutes, int unit) {
		/** 時間から単位分を求める */
		int perUnit = timeAsMinutes / unit;
		
		/** 時間を切り捨て */
		return timeAsMinutes > 0 ? unit * (perUnit + 1) : unit * perUnit;
	}
	
	public BigDecimal roundDownOverBigDecimal(BigDecimal timeAsMinutes) {
		
		return BigDecimal.valueOf(roundDownOver(timeAsMinutes.intValue()));
//		BigDecimal div = timeAsMinutes.divide(BigDecimal.valueOf(this.asTime()),6,BigDecimal.ROUND_HALF_UP);
//		//マイナスの場合
//		if(timeAsMinutes.compareTo(BigDecimal.ZERO)<0) {
//			if(div.remainder(BigDecimal.valueOf(2)).signum()==0) {
//				return timeAsMinutes.subtract(timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime())));
//			}else {
//				if(timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime())).signum() == 0) {
//					return timeAsMinutes.add(BigDecimal.valueOf(this.asTime()));
//				}else {
//					return timeAsMinutes.subtract(timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime())).subtract(BigDecimal.valueOf(this.asTime())));
//				}
//			}
//		}
//		//マイナスではない場合
//		if(div.remainder(BigDecimal.valueOf(2)).signum()==0) {
//			return timeAsMinutes.subtract(timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime())));
//		}else {
//			return timeAsMinutes.subtract(timeAsMinutes.remainder(BigDecimal.valueOf(this.asTime())).add(BigDecimal.valueOf(this.asTime())));
//		}
	}
}
