package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly;

/**
 * 月別実績の勤怠項目
 * @author shuichi_ishida
 */
public enum AttendanceItemOfMonthly {
	
	/** 週割増合計時間 */
	WEEKLY_TOTAL_PREMIUM_TIME(5),
	/** 月割増合計時間 */
	MONTHLY_TOTAL_PREMIUM_TIME(6),

	/** フレックス時間 */
	FLEX_TIME(12),
	/** フレックス法定内時間 */
	FLEX_LEGAL_TIME(15),
	/** フレックス法定外時間 */
	FLEX_ILLEGAL_TIME(16),
	/** フレックス超過時間 */
	FLEX_EXCESS_TIME(17),
	/** フレックス不足時間 */
	FLEX_SHORTAGE_TIME(18),
	
	/** 就業時間 */
	WORK_TIME(31),
	/** 所定内割増時間 */
	WITHIN_PRESCRIBED_PREMIUM_TIME(32),
	
	/** 残業時間1 */
	OVER_TIME_01(35),
	/** 残業時間2 */
	OVER_TIME_02(36),
	/** 残業時間3 */
	OVER_TIME_03(37),
	/** 残業時間4 */
	OVER_TIME_04(38),
	/** 残業時間5 */
	OVER_TIME_05(39),
	/** 残業時間6 */
	OVER_TIME_06(40),
	/** 残業時間7 */
	OVER_TIME_07(41),
	/** 残業時間8 */
	OVER_TIME_08(42),
	/** 残業時間9 */
	OVER_TIME_09(43),
	/** 残業時間10 */
	OVER_TIME_10(44),
	/** 残業時間合計 */
	TOTAL_OVER_TIME(45),
	/** 計算残業時間1 */
	CALC_OVER_TIME_01(46),
	/** 計算残業時間2 */
	CALC_OVER_TIME_02(47),
	/** 計算残業時間3 */
	CALC_OVER_TIME_03(48),
	/** 計算残業時間4 */
	CALC_OVER_TIME_04(49),
	/** 計算残業時間5 */
	CALC_OVER_TIME_05(50),
	/** 計算残業時間6 */
	CALC_OVER_TIME_06(51),
	/** 計算残業時間7 */
	CALC_OVER_TIME_07(52),
	/** 計算残業時間8 */
	CALC_OVER_TIME_08(53),
	/** 計算残業時間9 */
	CALC_OVER_TIME_09(54),
	/** 計算残業時間10 */
	CALC_OVER_TIME_10(55),
	/** 計算残業時間合計 */
	CALC_TOTAL_OVER_TIME(56),
	
	/** 振替残業時間1 */
	TRANSFER_OVER_TIME_01(1364),
	/** 振替残業時間2 */
	TRANSFER_OVER_TIME_02(1365),
	/** 振替残業時間3 */
	TRANSFER_OVER_TIME_03(1366),
	/** 振替残業時間4 */
	TRANSFER_OVER_TIME_04(1367),
	/** 振替残業時間5 */
	TRANSFER_OVER_TIME_05(1368),
	/** 振替残業時間6 */
	TRANSFER_OVER_TIME_06(1369),
	/** 振替残業時間7 */
	TRANSFER_OVER_TIME_07(1370),
	/** 振替残業時間8 */
	TRANSFER_OVER_TIME_08(1371),
	/** 振替残業時間9 */
	TRANSFER_OVER_TIME_09(1372),
	/** 振替残業時間10 */
	TRANSFER_OVER_TIME_10(1373),
	/** 振替残業合計時間 */
	TOTAL_TRANSFER_OVER_TIME(1374),
	/** 計算振替残業時間1 */
	CALC_TRANSFER_OVER_TIME_01(1375),
	/** 計算振替残業時間2 */
	CALC_TRANSFER_OVER_TIME_02(1376),
	/** 計算振替残業時間3 */
	CALC_TRANSFER_OVER_TIME_03(1377),
	/** 計算振替残業時間4 */
	CALC_TRANSFER_OVER_TIME_04(1378),
	/** 計算振替残業時間5 */
	CALC_TRANSFER_OVER_TIME_05(1379),
	/** 計算振替残業時間6 */
	CALC_TRANSFER_OVER_TIME_06(1380),
	/** 計算振替残業時間7 */
	CALC_TRANSFER_OVER_TIME_07(1381),
	/** 計算振替残業時間8 */
	CALC_TRANSFER_OVER_TIME_08(1382),
	/** 計算振替残業時間9 */
	CALC_TRANSFER_OVER_TIME_09(1383),
	/** 計算振替残業時間10 */
	CALC_TRANSFER_OVER_TIME_10(1384),
	/** 計算振替残業合計時間 */
	CALC_TOTAL_TRANSFER_OVER_TIME(1385),

	/** 休出時間1 */
	HOLIDAY_WORK_TIME_01(110),
	/** 休出時間2 */
	HOLIDAY_WORK_TIME_02(111),
	/** 休出時間3 */
	HOLIDAY_WORK_TIME_03(112),
	/** 休出時間4 */
	HOLIDAY_WORK_TIME_04(113),
	/** 休出時間5 */
	HOLIDAY_WORK_TIME_05(114),
	/** 休出時間6 */
	HOLIDAY_WORK_TIME_06(115),
	/** 休出時間7 */
	HOLIDAY_WORK_TIME_07(116),
	/** 休出時間8 */
	HOLIDAY_WORK_TIME_08(117),
	/** 休出時間9 */
	HOLIDAY_WORK_TIME_09(118),
	/** 休出時間10 */
	HOLIDAY_WORK_TIME_10(119),
	/** 休出時間合計 */
	TOTAL_HOLIDAY_WORK_TIME(120),
	/** 計算休出時間1 */
	CALC_HOLIDAY_WORK_TIME_01(121),
	/** 計算休出時間2 */
	CALC_HOLIDAY_WORK_TIME_02(122),
	/** 計算休出時間3 */
	CALC_HOLIDAY_WORK_TIME_03(123),
	/** 計算休出時間4 */
	CALC_HOLIDAY_WORK_TIME_04(124),
	/** 計算休出時間5 */
	CALC_HOLIDAY_WORK_TIME_05(125),
	/** 計算休出時間6 */
	CALC_HOLIDAY_WORK_TIME_06(126),
	/** 計算休出時間7 */
	CALC_HOLIDAY_WORK_TIME_07(127),
	/** 計算休出時間8 */
	CALC_HOLIDAY_WORK_TIME_08(128),
	/** 計算休出時間9 */
	CALC_HOLIDAY_WORK_TIME_09(129),
	/** 計算休出時間10 */
	CALC_HOLIDAY_WORK_TIME_10(130),
	/** 計算休出時間合計 */
	CALC_TOTAL_HOLIDAY_WORK_TIME(131),
	
	/** 振替時間1 */
	TRANSFER_TIME_01(143),
	/** 振替時間2 */
	TRANSFER_TIME_02(144),
	/** 振替時間3 */
	TRANSFER_TIME_03(145),
	/** 振替時間4 */
	TRANSFER_TIME_04(146),
	/** 振替時間5 */
	TRANSFER_TIME_05(147),
	/** 振替時間6 */
	TRANSFER_TIME_06(148),
	/** 振替時間7 */
	TRANSFER_TIME_07(149),
	/** 振替時間8 */
	TRANSFER_TIME_08(150),
	/** 振替時間9 */
	TRANSFER_TIME_09(151),
	/** 振替時間10 */
	TRANSFER_TIME_10(152),
	/** 振替時間合計 */
	TOTAL_TRANSFER_TIME(153),
	/** 計算振替時間1 */
	CALC_TRANSFER_TIME_01(154),
	/** 計算振替時間2 */
	CALC_TRANSFER_TIME_02(155),
	/** 計算振替時間3 */
	CALC_TRANSFER_TIME_03(156),
	/** 計算振替時間4 */
	CALC_TRANSFER_TIME_04(157),
	/** 計算振替時間5 */
	CALC_TRANSFER_TIME_05(158),
	/** 計算振替時間6 */
	CALC_TRANSFER_TIME_06(159),
	/** 計算振替時間7 */
	CALC_TRANSFER_TIME_07(160),
	/** 計算振替時間8 */
	CALC_TRANSFER_TIME_08(161),
	/** 計算振替時間9 */
	CALC_TRANSFER_TIME_09(162),
	/** 計算振替時間10 */
	CALC_TRANSFER_TIME_10(163),
	/** 計算振替時間合計 */
	CALC_TOTAL_TRANSFER_TIME(164),
	/** 法定内休出時間1 */
	LEGAL_HOL_WORK_TIME_01(165),
	/** 法定内休出時間2 */
	LEGAL_HOL_WORK_TIME_02(166),
	/** 法定内休出時間3 */
	LEGAL_HOL_WORK_TIME_03(167),
	/** 法定内休出時間4 */
	LEGAL_HOL_WORK_TIME_04(168),
	/** 法定内休出時間5 */
	LEGAL_HOL_WORK_TIME_05(169),
	/** 法定内休出時間6 */
	LEGAL_HOL_WORK_TIME_06(170),
	/** 法定内休出時間7 */
	LEGAL_HOL_WORK_TIME_07(171),
	/** 法定内休出時間8 */
	LEGAL_HOL_WORK_TIME_08(172),
	/** 法定内休出時間9 */
	LEGAL_HOL_WORK_TIME_09(173),
	/** 法定内休出時間10 */
	LEGAL_HOL_WORK_TIME_10(174),
	/** 法定内振替休出時間1 */
	LEGAL_HOL_TRANSFER_WORK_TIME_01(175),
	/** 法定内振替休出時間2 */
	LEGAL_HOL_TRANSFER_WORK_TIME_02(176),
	/** 法定内振替休出時間3 */
	LEGAL_HOL_TRANSFER_WORK_TIME_03(177),
	/** 法定内振替休出時間4 */
	LEGAL_HOL_TRANSFER_WORK_TIME_04(178),
	/** 法定内振替休出時間5 */
	LEGAL_HOL_TRANSFER_WORK_TIME_05(179),
	/** 法定内振替休出時間6 */
	LEGAL_HOL_TRANSFER_WORK_TIME_06(180),
	/** 法定内振替休出時間7 */
	LEGAL_HOL_TRANSFER_WORK_TIME_07(181),
	/** 法定内振替休出時間8 */
	LEGAL_HOL_TRANSFER_WORK_TIME_08(182),
	/** 法定内振替休出時間9 */
	LEGAL_HOL_TRANSFER_WORK_TIME_09(183),
	/** 法定内振替休出時間10 */
	LEGAL_HOL_TRANSFER_WORK_TIME_10(184),
	/** 臨時勤務時間  */
	TEMPORARY_TIME(2079),
	
	MAX_ID(9999);
	
	public int value;
	private AttendanceItemOfMonthly(int value){
		this.value = value;
	}
}
