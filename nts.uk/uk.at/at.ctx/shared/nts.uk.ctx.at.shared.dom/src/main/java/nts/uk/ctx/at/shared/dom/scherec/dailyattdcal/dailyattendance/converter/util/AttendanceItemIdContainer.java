package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.enu.DailyDomainGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.enu.MonthlyDomainGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

public class AttendanceItemIdContainer implements ItemConst {

	private final static Map<Integer, String> DAY_ITEM_ID_CONTAINER;
	private final static Map<Integer, String> MONTHLY_ITEM_ID_CONTAINER;
	private final static Map<String, Integer> ENUM_CONTAINER;
	
	static {
		ENUM_CONTAINER = new HashMap<>();
		ENUM_CONTAINER.put(E_WORK_REF, 0);
		ENUM_CONTAINER.put(E_SCHEDULE_REF, 1);
		ENUM_CONTAINER.put(E_CHILD_CARE, 0);
		ENUM_CONTAINER.put(E_CARE, 1);
		ENUM_CONTAINER.put(E_DAY_WORK, 0);
		ENUM_CONTAINER.put(E_NIGHT_WORK, 1);
		ENUM_CONTAINER.put(joinNS(LEGAL, HOLIDAY_WORK), 0);
		ENUM_CONTAINER.put(joinNS(ILLEGAL, HOLIDAY_WORK), 1);
		ENUM_CONTAINER.put(joinNS(PUBLIC_HOLIDAY, HOLIDAY_WORK), 0);
		ENUM_CONTAINER.put(E_SUPPORT, 0);
		ENUM_CONTAINER.put(E_UNION, 1);
		ENUM_CONTAINER.put(E_CHARGE, 2);
		ENUM_CONTAINER.put(E_OFFICAL, 3);
		ENUM_CONTAINER.put(E_OFF_BEFORE_BIRTH, 0);
		ENUM_CONTAINER.put(E_OFF_AFTER_BIRTH, 1);
		ENUM_CONTAINER.put(E_OFF_CHILD_CARE, 2);
		ENUM_CONTAINER.put(E_OFF_CARE, 3);
		ENUM_CONTAINER.put(E_OFF_INJURY, 4);

		DAY_ITEM_ID_CONTAINER = new HashMap<>();
		MONTHLY_ITEM_ID_CONTAINER = new HashMap<>();
		
		getDailyKey(DAY_ITEM_ID_CONTAINER);
	
		getMonthlyKey(MONTHLY_ITEM_ID_CONTAINER);
		getMonthlyKey2(MONTHLY_ITEM_ID_CONTAINER);
		getMonthlyKeySpecialHoliday(MONTHLY_ITEM_ID_CONTAINER);
		getMonthlyKeyAnnual(MONTHLY_ITEM_ID_CONTAINER);
	}
	
	private static void getDailyKey(Map<Integer, String> temp){
		
		// 921, 922, 923, 924, 925, 926, 927, 928, 929, 930, 931, 932, 933, 934, 935, 936, 937, 938, 939, 940, 941, 942, 943, 944, 945, 946, 947, 948, 949, 950, 
		// 951, 952, 953, 954, 955, 956, 957, 958, 959, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975, 976, 977, 978, 979, 980, 
		// 981, 982, 983, 984, 985, 986, 987, 988, 989, 990, 991, 992, 993, 994, 995, 996, 997, 998, 999, 1000, 
		// 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1017, 1018, 1019, 1020, 1021, 1022, 1023, 1024, 1025, 
		// 1026, 1027, 1028, 1029, 1030, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038, 1039, 1040, 1041, 1042, 1043, 1044, 1045, 1046, 1047, 1048, 1049, 1050, 1051, 
		// 1052, 1053, 1054, 1055, 1056, 1057, 1058, 1059, 1060, 1061, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073, 1074, 1075, 1076, 1077, 
		// 1078, 1079, 1080, 1081, 1082, 1083, 1084, 1085, 1086, 1087, 1088, 1089, 1090, 1091, 1092, 1093, 1094, 1095, 1096, 1097, 1098, 1099, 1100, 1101, 1102, 1103, 
		// 1104, 1105, 1106, 1107, 1108, 1109, 1110, 1111, 1112, 1113, 1114, 1115, 1116, 1117, 1118, 1119, 1120
		for (int i = 0; i < 20; i++) {
			String NUMBER = i+1 + "";
			temp.put(921 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKPLACE_BYSUPPORT, WORKLOCATIONCD + NUMBER ));
			temp.put(922 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKPLACE_BYSUPPORT, WORKPLACEID + NUMBER ));
			temp.put(923 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKREMARKS + NUMBER ));
			temp.put(924 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKGROUP, WORKCODE + LAYOUT_A + NUMBER));
			temp.put(925 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKGROUP, WORKCODE + LAYOUT_B + NUMBER));
			temp.put(926 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKGROUP, WORKCODE + LAYOUT_C + NUMBER));
			temp.put(927 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKGROUP, WORKCODE + LAYOUT_D + NUMBER));
			temp.put(928 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, WORK_CONTENT, WORKGROUP, WORKCODE + LAYOUT_E + NUMBER));
			temp.put(929 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, TIME_ZONE, START, CLOCK + NUMBER));
			temp.put(930 + 10*i , join(DAILY_SUPPORT_TIMESHEET_NAME, TIME_ZONE, END,   CLOCK + NUMBER));
		}
		
		
		temp.put(833, join(DAILY_REMARKS_NAME, FAKED, joinNS(REMARK, NUMBER_1)));
		temp.put(834, join(DAILY_REMARKS_NAME, FAKED, joinNS(REMARK, NUMBER_2)));
		temp.put(835, join(DAILY_REMARKS_NAME, FAKED, joinNS(REMARK, NUMBER_3)));
		temp.put(836, join(DAILY_REMARKS_NAME, FAKED, joinNS(REMARK, NUMBER_4)));
		temp.put(837, join(DAILY_REMARKS_NAME, FAKED, joinNS(REMARK, NUMBER_5)));

		temp.put(794, join(DAILY_PC_LOG_INFO_NAME, INFO, joinNS(LOGON, NUMBER_1)));
		temp.put(795, join(DAILY_PC_LOG_INFO_NAME, INFO, joinNS(LOGOFF, NUMBER_1)));
		temp.put(796, join(DAILY_PC_LOG_INFO_NAME, INFO, joinNS(LOGON, NUMBER_2)));
		temp.put(797, join(DAILY_PC_LOG_INFO_NAME, INFO, joinNS(LOGOFF, NUMBER_2)));

		temp.put(623, join(DAILY_AFFILIATION_INFO_NAME, WORKPLACE));
		temp.put(624, join(DAILY_AFFILIATION_INFO_NAME, CLASSIFICATION));
		temp.put(625, join(DAILY_AFFILIATION_INFO_NAME, JOB_TITLE));
		temp.put(626, join(DAILY_AFFILIATION_INFO_NAME, EMPLOYEMENT));
		temp.put(1294, join(DAILY_AFFILIATION_INFO_NAME, RAISING_SALARY));
		temp.put(858, join(DAILY_AFFILIATION_INFO_NAME, BUSINESS_TYPE));
		
		temp.put(1, join(DAILY_SNAPSHOT_NAME, WORK_TYPE));
		temp.put(2, join(DAILY_SNAPSHOT_NAME, WORK_TIME));
		temp.put(530, join(DAILY_SNAPSHOT_NAME, TIME));

		temp.put(28, join(DAILY_WORK_INFO_NAME, ACTUAL, WORK_TYPE));
		temp.put(29, join(DAILY_WORK_INFO_NAME, ACTUAL, WORK_TIME));
		temp.put(3, join(DAILY_WORK_INFO_NAME, joinNS(PLAN, TIME_ZONE), joinNS(ATTENDANCE, NUMBER_1)));
		temp.put(5, join(DAILY_WORK_INFO_NAME, joinNS(PLAN, TIME_ZONE), joinNS(ATTENDANCE, NUMBER_2)));
		temp.put(4, join(DAILY_WORK_INFO_NAME, joinNS(PLAN, TIME_ZONE), joinNS(LEAVE, NUMBER_1)));
		temp.put(6, join(DAILY_WORK_INFO_NAME, joinNS(PLAN, TIME_ZONE), joinNS(LEAVE, NUMBER_2)));
		
		temp.put(759, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(START, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE, NUMBER_1)));
		temp.put(760, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(END, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE, NUMBER_1)));
		temp.put(761, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(START, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE, NUMBER_2)));
		temp.put(762, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(END, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE, NUMBER_2)));
		temp.put(763, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(START, DEFAULT_ENUM_SEPERATOR, E_CARE, NUMBER_1)));
		temp.put(764, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(END, DEFAULT_ENUM_SEPERATOR, E_CARE, NUMBER_1)));
		temp.put(765, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(START, DEFAULT_ENUM_SEPERATOR, E_CARE, NUMBER_2)));
		temp.put(766, join(DAILY_SHORT_TIME_NAME, TIME_ZONE, joinNS(END, DEFAULT_ENUM_SEPERATOR, E_CARE, NUMBER_2)));

		temp.put(416, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_1)));
		temp.put(417, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_2)));
		temp.put(418, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_3)));
		temp.put(419, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_4)));
		temp.put(420, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_5)));
		temp.put(421, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_6)));
		temp.put(422, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_7)));
		temp.put(423, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_8)));
		temp.put(424, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_9)));
		temp.put(425, join(DAILY_SPECIFIC_DATE_ATTR_NAME, ATTRIBUTE, joinNS(ATTRIBUTE, NUMBER_1, NUMBER_0)));

		temp.put(824, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, EARLY_SHIFT, UPPER_LIMIT));
		temp.put(825, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, joinNS(EARLY_SHIFT, LATE_NIGHT), UPPER_LIMIT));
		temp.put(826, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, NORMAL, UPPER_LIMIT));
		temp.put(827, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, joinNS(NORMAL, LATE_NIGHT), UPPER_LIMIT));
		temp.put(828, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, LEGAL, UPPER_LIMIT));
		temp.put(829, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, joinNS(LEGAL, LATE_NIGHT), UPPER_LIMIT));
		temp.put(830, join(DAILY_CALCULATION_ATTR_NAME, HOLIDAY_WORK, HOLIDAY_WORK, UPPER_LIMIT));
		temp.put(831, join(DAILY_CALCULATION_ATTR_NAME, HOLIDAY_WORK, LATE_NIGHT, UPPER_LIMIT));
		temp.put(832, join(DAILY_CALCULATION_ATTR_NAME, FLEX, UPPER_LIMIT));
		temp.put(627, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, EARLY_SHIFT, CALC_ATTR));
		temp.put(628, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, joinNS(EARLY_SHIFT, LATE_NIGHT), CALC_ATTR));
		temp.put(629, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, NORMAL, CALC_ATTR));
		temp.put(630, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, joinNS(NORMAL, LATE_NIGHT), CALC_ATTR));
		temp.put(631, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, LEGAL, CALC_ATTR));
		temp.put(632, join(DAILY_CALCULATION_ATTR_NAME, OVERTIME, joinNS(LEGAL, LATE_NIGHT), CALC_ATTR));
		temp.put(633, join(DAILY_CALCULATION_ATTR_NAME, HOLIDAY_WORK, HOLIDAY_WORK, CALC_ATTR));
		temp.put(634, join(DAILY_CALCULATION_ATTR_NAME, HOLIDAY_WORK, LATE_NIGHT, CALC_ATTR));
		temp.put(635, join(DAILY_CALCULATION_ATTR_NAME, FLEX, CALC_ATTR));
		temp.put(636, join(DAILY_CALCULATION_ATTR_NAME, RAISING_SALARY, RAISING_SALARY));
		temp.put(637, join(DAILY_CALCULATION_ATTR_NAME, RAISING_SALARY, SPECIFIC));
		temp.put(638, join(DAILY_CALCULATION_ATTR_NAME, joinNS(LATE, LEAVE_EARLY), LATE));
		temp.put(639, join(DAILY_CALCULATION_ATTR_NAME, joinNS(LATE, LEAVE_EARLY), LEAVE_EARLY));
		temp.put(640, join(DAILY_CALCULATION_ATTR_NAME, DIVERGENCE));

		temp.put(74, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, START, joinNS(PLACE, NUMBER_1)));
		temp.put(75, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_1)));
		temp.put(76, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, END, joinNS(PLACE, NUMBER_1)));
		temp.put(77, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_1)));
		temp.put(78, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, START, joinNS(PLACE, NUMBER_2)));
		temp.put(79, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_2)));
		temp.put(80, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, END, joinNS(PLACE, NUMBER_2)));
		temp.put(81, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_2)));
		temp.put(82, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, START, joinNS(PLACE, NUMBER_3)));
		temp.put(83, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_3)));
		temp.put(84, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, END, joinNS(PLACE, NUMBER_3)));
		temp.put(85, join(DAILY_ATTENDANCE_LEAVE_GATE_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_3)));

//		temp.put(461, getText(DAILY_ATTENDACE_LEAVE_NAME, COUNT));
		temp.put(30, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(PLACE, NUMBER_1)));
		temp.put(31, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(CLOCK, NUMBER_1)));
		temp.put(33, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(PLACE, NUMBER_1)));
		temp.put(34, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(CLOCK, NUMBER_1)));
		temp.put(36, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(PLACE, NUMBER_1)));
		temp.put(37, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(CLOCK, NUMBER_1)));
		temp.put(38, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(PLACE, NUMBER_1)));
		temp.put(39, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(CLOCK, NUMBER_1)));
		temp.put(40, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(PLACE, NUMBER_2)));
		temp.put(41, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(CLOCK, NUMBER_2)));
		temp.put(43, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(PLACE, NUMBER_2)));
		temp.put(44, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(CLOCK, NUMBER_2)));
		temp.put(46, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(PLACE, NUMBER_2)));
		temp.put(47, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(CLOCK, NUMBER_2)));
		temp.put(48, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(PLACE, NUMBER_2)));
		temp.put(49, join(DAILY_ATTENDACE_LEAVE_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(CLOCK, NUMBER_2)));

		temp.put(616, join(DAILY_TEMPORARY_TIME_NAME, COUNT));
		temp.put(50, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(PLACE, NUMBER_1)));
		temp.put(51, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(CLOCK, NUMBER_1)));
		temp.put(52, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(PLACE, NUMBER_1)));
		temp.put(53, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(CLOCK, NUMBER_1)));
		temp.put(54, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(PLACE, NUMBER_1)));
		temp.put(55, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(CLOCK, NUMBER_1)));
		temp.put(56, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(PLACE, NUMBER_1)));
		temp.put(57, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(CLOCK, NUMBER_1)));
		temp.put(58, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(PLACE, NUMBER_2)));
		temp.put(59, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(CLOCK, NUMBER_2)));
		temp.put(60, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(PLACE, NUMBER_2)));
		temp.put(61, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(CLOCK, NUMBER_2)));
		temp.put(62, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(PLACE, NUMBER_2)));
		temp.put(63, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(CLOCK, NUMBER_2)));
		temp.put(64, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(PLACE, NUMBER_2)));
		temp.put(65, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(CLOCK, NUMBER_2)));
		temp.put(66, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(PLACE, NUMBER_3)));
		temp.put(67, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, STAMP, joinNS(CLOCK, NUMBER_3)));
		temp.put(68, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(PLACE, NUMBER_3)));
		temp.put(69, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, STAMP, joinNS(CLOCK, NUMBER_3)));
		temp.put(70, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(PLACE, NUMBER_3)));
		temp.put(71, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, ATTENDANCE, ACTUAL, joinNS(CLOCK, NUMBER_3)));
		temp.put(72, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(PLACE, NUMBER_3)));
		temp.put(73, join(DAILY_TEMPORARY_TIME_NAME, TIME_ZONE, LEAVE, ACTUAL, joinNS(CLOCK, NUMBER_3)));

		temp.put(86, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_1)));
		temp.put(93, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_2)));
		temp.put(100, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_3)));
		temp.put(107, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_4)));
		temp.put(114, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_5)));
		temp.put(121, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_6)));
		temp.put(128, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_7)));
		temp.put(135, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_8)));
		temp.put(142, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_9)));
		temp.put(149, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, joinNS(REASON, NUMBER_1, NUMBER_0)));
		temp.put(87, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_1)));
		temp.put(88, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_1)));
		temp.put(90, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_1)));
		temp.put(91, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_1)));
		temp.put(94, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_2)));
		temp.put(95, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_2)));
		temp.put(97, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_2)));
		temp.put(98, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_2)));
		temp.put(101, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_3)));
		temp.put(102, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_3)));
		temp.put(104, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_3)));
		temp.put(105, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_3)));
		temp.put(108, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_4)));
		temp.put(109, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_4)));
		temp.put(111, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_4)));
		temp.put(112, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_4)));
		temp.put(115, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_5)));
		temp.put(116, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_5)));
		temp.put(118, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_5)));
		temp.put(119, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_5)));
		temp.put(122, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_6)));
		temp.put(123, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_6)));
		temp.put(125, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_6)));
		temp.put(126, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_6)));
		temp.put(129, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_7)));
		temp.put(130, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_7)));
		temp.put(132, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_7)));
		temp.put(133, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_7)));
		temp.put(136, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_8)));
		temp.put(137, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_8)));
		temp.put(139, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_8)));
		temp.put(140, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_8)));
		temp.put(143, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_9)));
		temp.put(144, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_9)));
		temp.put(146, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_9)));
		temp.put(147, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_9)));
		temp.put(150, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(PLACE, NUMBER_1, NUMBER_0)));
		temp.put(151, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, GO_OUT, joinNS(CLOCK, NUMBER_1, NUMBER_0)));
		temp.put(153, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(PLACE, NUMBER_1, NUMBER_0)));
		temp.put(154, join(DAILY_OUTING_TIME_NAME, TIME_ZONE, BACK, joinNS(CLOCK, NUMBER_1, NUMBER_0)));
		
//		temp.put(156, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_1)));
//		temp.put(162, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_2)));
//		temp.put(168, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_3)));
//		temp.put(174, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_4)));
//		temp.put(180, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_5)));
//		temp.put(186, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_6)));
//		temp.put(192, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_7)));
//		temp.put(198, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_8)));
//		temp.put(204, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_9)));
//		temp.put(210, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_1, NUMBER_0)));
//		temp.put(158, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_1)));
//		temp.put(164, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_2)));
//		temp.put(170, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_3)));
//		temp.put(176, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_4)));
//		temp.put(182, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_5)));
//		temp.put(188, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_6)));
//		temp.put(194, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_7)));
//		temp.put(200, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_8)));
//		temp.put(206, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_9)));
//		temp.put(212, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(PLACE, DEFAULT_ENUM_SEPERATOR, E_WORK_REF, NUMBER_1, NUMBER_0)));
		temp.put(157, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_1)));
		temp.put(163, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_2)));
		temp.put(169, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_3)));
		temp.put(175, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_4)));
		temp.put(181, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_5)));
		temp.put(187, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_6)));
		temp.put(193, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_7)));
		temp.put(199, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_8)));
		temp.put(205, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_9)));
		temp.put(211, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, NUMBER_1, NUMBER_0)));
		temp.put(159, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_1)));
		temp.put(165, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_2)));
		temp.put(171, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_3)));
		temp.put(177, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_4)));
		temp.put(183, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_5)));
		temp.put(189, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_6)));
		temp.put(195, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_7)));
		temp.put(201, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_8)));
		temp.put(207, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_9)));
		temp.put(213, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, NUMBER_1, NUMBER_0)));
//		temp.put(7, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_1)));
//		temp.put(9, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_2)));
//		temp.put(11, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_3)));
//		temp.put(13, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_4)));
//		temp.put(15, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_5)));
//		temp.put(17, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_6)));
//		temp.put(19, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_7)));
//		temp.put(21, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_8)));
//		temp.put(23, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_9)));
//		temp.put(25, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, START, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_1, NUMBER_0)));
//		temp.put(8, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_1)));
//		temp.put(10, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_2)));
//		temp.put(12, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_3)));
//		temp.put(14, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_4)));
//		temp.put(16, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_5)));
//		temp.put(18, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_6)));
//		temp.put(20, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_7)));
//		temp.put(22, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_8)));
//		temp.put(24, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_9)));
//		temp.put(26, join(DAILY_BREAK_TIME_NAME, TIME_ZONE, END, joinNS(CLOCK, DEFAULT_ENUM_SEPERATOR, E_SCHEDULE_REF, NUMBER_1, NUMBER_0)));

		String optionalFixedPath = join(DAILY_OPTIONAL_ITEM_NAME, OPTIONAL_ITEM_VALUE, VALUE);
		temp.put(641, joinNS(optionalFixedPath, NUMBER_1));
		temp.put(642, joinNS(optionalFixedPath, NUMBER_2));
		temp.put(643, joinNS(optionalFixedPath, NUMBER_3));
		temp.put(644, joinNS(optionalFixedPath, NUMBER_4));
		temp.put(645, joinNS(optionalFixedPath, NUMBER_5));
		temp.put(646, joinNS(optionalFixedPath, NUMBER_6));
		temp.put(647, joinNS(optionalFixedPath, NUMBER_7));
		temp.put(648, joinNS(optionalFixedPath, NUMBER_8));
		temp.put(649, joinNS(optionalFixedPath, NUMBER_9));
		temp.put(650, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0));
		temp.put(651, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1));
		temp.put(652, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2));
		temp.put(653, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3));
		temp.put(654, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4));
		temp.put(655, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5));
		temp.put(656, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6));
		temp.put(657, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7));
		temp.put(658, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8));
		temp.put(659, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9));
		temp.put(660, joinNS(optionalFixedPath, NUMBER_2, NUMBER_0));
		temp.put(661, joinNS(optionalFixedPath, NUMBER_2, NUMBER_1));
		temp.put(662, joinNS(optionalFixedPath, NUMBER_2, NUMBER_2));
		temp.put(663, joinNS(optionalFixedPath, NUMBER_2, NUMBER_3));
		temp.put(664, joinNS(optionalFixedPath, NUMBER_2, NUMBER_4));
		temp.put(665, joinNS(optionalFixedPath, NUMBER_2, NUMBER_5));
		temp.put(666, joinNS(optionalFixedPath, NUMBER_2, NUMBER_6));
		temp.put(667, joinNS(optionalFixedPath, NUMBER_2, NUMBER_7));
		temp.put(668, joinNS(optionalFixedPath, NUMBER_2, NUMBER_8));
		temp.put(669, joinNS(optionalFixedPath, NUMBER_2, NUMBER_9));
		temp.put(670, joinNS(optionalFixedPath, NUMBER_3, NUMBER_0));
		temp.put(671, joinNS(optionalFixedPath, NUMBER_3, NUMBER_1));
		temp.put(672, joinNS(optionalFixedPath, NUMBER_3, NUMBER_2));
		temp.put(673, joinNS(optionalFixedPath, NUMBER_3, NUMBER_3));
		temp.put(674, joinNS(optionalFixedPath, NUMBER_3, NUMBER_4));
		temp.put(675, joinNS(optionalFixedPath, NUMBER_3, NUMBER_5));
		temp.put(676, joinNS(optionalFixedPath, NUMBER_3, NUMBER_6));
		temp.put(677, joinNS(optionalFixedPath, NUMBER_3, NUMBER_7));
		temp.put(678, joinNS(optionalFixedPath, NUMBER_3, NUMBER_8));
		temp.put(679, joinNS(optionalFixedPath, NUMBER_3, NUMBER_9));
		temp.put(680, joinNS(optionalFixedPath, NUMBER_4, NUMBER_0));
		temp.put(681, joinNS(optionalFixedPath, NUMBER_4, NUMBER_1));
		temp.put(682, joinNS(optionalFixedPath, NUMBER_4, NUMBER_2));
		temp.put(683, joinNS(optionalFixedPath, NUMBER_4, NUMBER_3));
		temp.put(684, joinNS(optionalFixedPath, NUMBER_4, NUMBER_4));
		temp.put(685, joinNS(optionalFixedPath, NUMBER_4, NUMBER_5));
		temp.put(686, joinNS(optionalFixedPath, NUMBER_4, NUMBER_6));
		temp.put(687, joinNS(optionalFixedPath, NUMBER_4, NUMBER_7));
		temp.put(688, joinNS(optionalFixedPath, NUMBER_4, NUMBER_8));
		temp.put(689, joinNS(optionalFixedPath, NUMBER_4, NUMBER_9));
		temp.put(690, joinNS(optionalFixedPath, NUMBER_5, NUMBER_0));
		temp.put(691, joinNS(optionalFixedPath, NUMBER_5, NUMBER_1));
		temp.put(692, joinNS(optionalFixedPath, NUMBER_5, NUMBER_2));
		temp.put(693, joinNS(optionalFixedPath, NUMBER_5, NUMBER_3));
		temp.put(694, joinNS(optionalFixedPath, NUMBER_5, NUMBER_4));
		temp.put(695, joinNS(optionalFixedPath, NUMBER_5, NUMBER_5));
		temp.put(696, joinNS(optionalFixedPath, NUMBER_5, NUMBER_6));
		temp.put(697, joinNS(optionalFixedPath, NUMBER_5, NUMBER_7));
		temp.put(698, joinNS(optionalFixedPath, NUMBER_5, NUMBER_8));
		temp.put(699, joinNS(optionalFixedPath, NUMBER_5, NUMBER_9));
		temp.put(700, joinNS(optionalFixedPath, NUMBER_6, NUMBER_0));
		temp.put(701, joinNS(optionalFixedPath, NUMBER_6, NUMBER_1));
		temp.put(702, joinNS(optionalFixedPath, NUMBER_6, NUMBER_2));
		temp.put(703, joinNS(optionalFixedPath, NUMBER_6, NUMBER_3));
		temp.put(704, joinNS(optionalFixedPath, NUMBER_6, NUMBER_4));
		temp.put(705, joinNS(optionalFixedPath, NUMBER_6, NUMBER_5));
		temp.put(706, joinNS(optionalFixedPath, NUMBER_6, NUMBER_6));
		temp.put(707, joinNS(optionalFixedPath, NUMBER_6, NUMBER_7));
		temp.put(708, joinNS(optionalFixedPath, NUMBER_6, NUMBER_8));
		temp.put(709, joinNS(optionalFixedPath, NUMBER_6, NUMBER_9));
		temp.put(710, joinNS(optionalFixedPath, NUMBER_7, NUMBER_0));
		temp.put(711, joinNS(optionalFixedPath, NUMBER_7, NUMBER_1));
		temp.put(712, joinNS(optionalFixedPath, NUMBER_7, NUMBER_2));
		temp.put(713, joinNS(optionalFixedPath, NUMBER_7, NUMBER_3));
		temp.put(714, joinNS(optionalFixedPath, NUMBER_7, NUMBER_4));
		temp.put(715, joinNS(optionalFixedPath, NUMBER_7, NUMBER_5));
		temp.put(716, joinNS(optionalFixedPath, NUMBER_7, NUMBER_6));
		temp.put(717, joinNS(optionalFixedPath, NUMBER_7, NUMBER_7));
		temp.put(718, joinNS(optionalFixedPath, NUMBER_7, NUMBER_8));
		temp.put(719, joinNS(optionalFixedPath, NUMBER_7, NUMBER_9));
		temp.put(720, joinNS(optionalFixedPath, NUMBER_8, NUMBER_0));
		temp.put(721, joinNS(optionalFixedPath, NUMBER_8, NUMBER_1));
		temp.put(722, joinNS(optionalFixedPath, NUMBER_8, NUMBER_2));
		temp.put(723, joinNS(optionalFixedPath, NUMBER_8, NUMBER_3));
		temp.put(724, joinNS(optionalFixedPath, NUMBER_8, NUMBER_4));
		temp.put(725, joinNS(optionalFixedPath, NUMBER_8, NUMBER_5));
		temp.put(726, joinNS(optionalFixedPath, NUMBER_8, NUMBER_6));
		temp.put(727, joinNS(optionalFixedPath, NUMBER_8, NUMBER_7));
		temp.put(728, joinNS(optionalFixedPath, NUMBER_8, NUMBER_8));
		temp.put(729, joinNS(optionalFixedPath, NUMBER_8, NUMBER_9));
		temp.put(730, joinNS(optionalFixedPath, NUMBER_9, NUMBER_0));
		temp.put(731, joinNS(optionalFixedPath, NUMBER_9, NUMBER_1));
		temp.put(732, joinNS(optionalFixedPath, NUMBER_9, NUMBER_2));
		temp.put(733, joinNS(optionalFixedPath, NUMBER_9, NUMBER_3));
		temp.put(734, joinNS(optionalFixedPath, NUMBER_9, NUMBER_4));
		temp.put(735, joinNS(optionalFixedPath, NUMBER_9, NUMBER_5));
		temp.put(736, joinNS(optionalFixedPath, NUMBER_9, NUMBER_6));
		temp.put(737, joinNS(optionalFixedPath, NUMBER_9, NUMBER_7));
		temp.put(738, joinNS(optionalFixedPath, NUMBER_9, NUMBER_8));
		temp.put(739, joinNS(optionalFixedPath, NUMBER_9, NUMBER_9));
		temp.put(740, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_0));


		temp.put(756, join(DAILY_ATTENDANCE_TIME_NAME, ACTUAL, TOTAL_LABOR, INTERVAL + TIME));
//		temp.put(757, "日別実績の遅刻時間．インターバル免除時間.免除時間");
		temp.put(758, join(DAILY_ATTENDANCE_TIME_NAME, ACTUAL, TOTAL_LABOR, INTERVAL + ATTENDANCE));
		
		temp.put(552, join(DAILY_ATTENDANCE_TIME_NAME, PLAN_ACTUAL_DIFF));
		temp.put(554, join(DAILY_ATTENDANCE_TIME_NAME, UNEMPLOYED));
		
		temp.put(531, join(DAILY_ATTENDANCE_TIME_NAME, PLAN, joinNS(ACTUAL, FIXED_WORK)));
		temp.put(27, join(DAILY_ATTENDANCE_TIME_NAME, PLAN, PLAN, PLAN));
		temp.put(553, join(DAILY_ATTENDANCE_TIME_NAME, STAYING, STAYING));
		temp.put(741, join(DAILY_ATTENDANCE_TIME_NAME, STAYING, ATTENDANCE));
		temp.put(742, join(DAILY_ATTENDANCE_TIME_NAME, STAYING, LEAVE));
		temp.put(743, join(DAILY_ATTENDANCE_TIME_NAME, STAYING, LOGON));
		temp.put(744, join(DAILY_ATTENDANCE_TIME_NAME, STAYING, LOGOFF));
		temp.put(750, join(DAILY_ATTENDANCE_TIME_NAME, MEDICAL, joinNS(TAKE_OVER, DEFAULT_ENUM_SEPERATOR, E_DAY_WORK)));
		temp.put(751, join(DAILY_ATTENDANCE_TIME_NAME, MEDICAL, joinNS(TAKE_OVER, DEFAULT_ENUM_SEPERATOR, E_NIGHT_WORK)));
		temp.put(752, join(DAILY_ATTENDANCE_TIME_NAME, MEDICAL, joinNS(DEDUCTION, DEFAULT_ENUM_SEPERATOR, E_DAY_WORK)));
		temp.put(753, join(DAILY_ATTENDANCE_TIME_NAME, MEDICAL, joinNS(DEDUCTION, DEFAULT_ENUM_SEPERATOR, E_NIGHT_WORK)));
		temp.put(754, join(DAILY_ATTENDANCE_TIME_NAME, MEDICAL, joinNS(WORKING_TIME, DEFAULT_ENUM_SEPERATOR, E_DAY_WORK)));
		temp.put(755, join(DAILY_ATTENDANCE_TIME_NAME, MEDICAL, joinNS(WORKING_TIME, DEFAULT_ENUM_SEPERATOR, E_NIGHT_WORK)));
		String attendActual = join(DAILY_ATTENDANCE_TIME_NAME, ACTUAL);
		temp.put(747, join(attendActual, RESTRAINT + DIFF));
		temp.put(575, join(attendActual, TIME_DIFF + WORKING_TIME));
		temp.put(426, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_1)));
		temp.put(427, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_2)));
		temp.put(428, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_3)));
		temp.put(429, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_4)));
		temp.put(430, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_5)));
		temp.put(431, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_6)));
		temp.put(432, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_7)));
		temp.put(433, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_8)));
		temp.put(434, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_9)));
		temp.put(435, join(attendActual, PREMIUM, joinNS(PREMIUM, NUMBER_1, NUMBER_0)));
		temp.put(748, join(attendActual, RESTRAINT, LATE_NIGHT));
		temp.put(749, join(attendActual, RESTRAINT, TOTAL));
		String divergence = join(attendActual, DIVERGENCE);
		String divReason = joinNS(DIVERGENCE, REASON);
		temp.put(436, join(divergence, joinNS(DIVERGENCE, NUMBER_1)));
		temp.put(441, join(divergence, joinNS(DIVERGENCE, NUMBER_2)));
		temp.put(446, join(divergence, joinNS(DIVERGENCE, NUMBER_3)));
		temp.put(451, join(divergence, joinNS(DIVERGENCE, NUMBER_4)));
		temp.put(456, join(divergence, joinNS(DIVERGENCE, NUMBER_5)));
		temp.put(799, join(divergence, joinNS(DIVERGENCE, NUMBER_6)));
		temp.put(804, join(divergence, joinNS(DIVERGENCE, NUMBER_7)));
		temp.put(809, join(divergence, joinNS(DIVERGENCE, NUMBER_8)));
		temp.put(814, join(divergence, joinNS(DIVERGENCE, NUMBER_9)));
		temp.put(819, join(divergence, joinNS(DIVERGENCE, NUMBER_1, NUMBER_0)));
		temp.put(437, join(divergence, joinNS(DEDUCTION, NUMBER_1)));
		temp.put(442, join(divergence, joinNS(DEDUCTION, NUMBER_2)));
		temp.put(447, join(divergence, joinNS(DEDUCTION, NUMBER_3)));
		temp.put(452, join(divergence, joinNS(DEDUCTION, NUMBER_4)));
		temp.put(457, join(divergence, joinNS(DEDUCTION, NUMBER_5)));
		temp.put(800, join(divergence, joinNS(DEDUCTION, NUMBER_6)));
		temp.put(805, join(divergence, joinNS(DEDUCTION, NUMBER_7)));
		temp.put(810, join(divergence, joinNS(DEDUCTION, NUMBER_8)));
		temp.put(815, join(divergence, joinNS(DEDUCTION, NUMBER_9)));
		temp.put(820, join(divergence, joinNS(DEDUCTION, NUMBER_1, NUMBER_0)));
		temp.put(438, join(divergence, joinNS(divReason, CODE, NUMBER_1)));
		temp.put(443, join(divergence, joinNS(divReason, CODE, NUMBER_2)));
		temp.put(448, join(divergence, joinNS(divReason, CODE, NUMBER_3)));
		temp.put(453, join(divergence, joinNS(divReason, CODE, NUMBER_4)));
		temp.put(458, join(divergence, joinNS(divReason, CODE, NUMBER_5)));
		temp.put(801, join(divergence, joinNS(divReason, CODE, NUMBER_6)));
		temp.put(806, join(divergence, joinNS(divReason, CODE, NUMBER_7)));
		temp.put(811, join(divergence, joinNS(divReason, CODE, NUMBER_8)));
		temp.put(816, join(divergence, joinNS(divReason, CODE, NUMBER_9)));
		temp.put(821, join(divergence, joinNS(divReason, CODE, NUMBER_1, NUMBER_0)));
		temp.put(439, join(divergence, joinNS(divReason, NUMBER_1)));
		temp.put(444, join(divergence, joinNS(divReason, NUMBER_2)));
		temp.put(449, join(divergence, joinNS(divReason, NUMBER_3)));
		temp.put(454, join(divergence, joinNS(divReason, NUMBER_4)));
		temp.put(459, join(divergence, joinNS(divReason, NUMBER_5)));
		temp.put(802, join(divergence, joinNS(divReason, NUMBER_6)));
		temp.put(807, join(divergence, joinNS(divReason, NUMBER_7)));
		temp.put(812, join(divergence, joinNS(divReason, NUMBER_8)));
		temp.put(817, join(divergence, joinNS(divReason, NUMBER_9)));
		temp.put(822, join(divergence, joinNS(divReason, NUMBER_1, NUMBER_0)));
		temp.put(440, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_1)));
		temp.put(445, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_2)));
		temp.put(450, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_3)));
		temp.put(455, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_4)));
		temp.put(460, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_5)));
		temp.put(803, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_6)));
		temp.put(808, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_7)));
		temp.put(813, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_8)));
		temp.put(818, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_9)));
		temp.put(823, join(divergence, joinNS(DEDUCTION, AFTER, NUMBER_1, NUMBER_0)));
		String totalLabol = join(attendActual, TOTAL_LABOR);
		
		temp.put(547, join(totalLabol, HOLIDAY, RETENTION));
		temp.put(548, join(totalLabol, HOLIDAY, ABSENCE));
		temp.put(539, join(totalLabol, HOLIDAY, ANNUNAL_LEAVE, USAGE));
		temp.put(540, join(totalLabol, HOLIDAY, ANNUNAL_LEAVE, TIME_DIGESTION));
		temp.put(543, join(totalLabol, HOLIDAY, SPECIAL, USAGE));
		temp.put(544, join(totalLabol, HOLIDAY, SPECIAL, TIME_DIGESTION));
		temp.put(545, join(totalLabol, HOLIDAY, EXCESS, USAGE));
		temp.put(546, join(totalLabol, HOLIDAY, EXCESS, TIME_DIGESTION));
		temp.put(541, join(totalLabol, HOLIDAY, COMPENSATORY, USAGE));
		temp.put(542, join(totalLabol, HOLIDAY, COMPENSATORY, TIME_DIGESTION));
		temp.put(549, join(totalLabol, HOLIDAY, TIME_DIGESTION, SHORTAGE));
		temp.put(550, join(totalLabol, HOLIDAY, TIME_DIGESTION, USAGE));
		temp.put(559, join(totalLabol, TOTAL_LABOR));
		temp.put(560, join(totalLabol, TOTAL_CALC));
		temp.put(579, join(totalLabol, ACTUAL));
		temp.put(461, join(totalLabol, COUNT));
		temp.put(561, join(totalLabol, WITHIN_STATUTORY, LATE_NIGHT, TIME));
		temp.put(562, join(totalLabol, WITHIN_STATUTORY, LATE_NIGHT, CALC));
		temp.put(532, join(totalLabol, WITHIN_STATUTORY, WORK_TIME));
		temp.put(533, join(totalLabol, WITHIN_STATUTORY, ACTUAL + WORK_TIME));
		temp.put(558, join(totalLabol, WITHIN_STATUTORY, PREMIUM));
		temp.put(576, join(totalLabol, HOLIDAY + ADD));
		// ITEM_ID_CONTAINER.put(576,"日別実績の勤怠時間.実績時間.総労働時間.所定内時間.休暇加算時間.年休加算時間");
		// ITEM_ID_CONTAINER.put(577,"日別実績の勤怠時間.実績時間.総労働時間.所定内時間.休暇加算時間.特別休暇加算時間");
		// ITEM_ID_CONTAINER.put(578,"日別実績の勤怠時間.実績時間.総労働時間.所定内時間.休暇加算時間.積立年休加算時間");
		String breakTime = join(totalLabol, BREAK);
		temp.put(534, join(breakTime, COUNT));
		temp.put(535, join(breakTime, CALC, TOTAL, TIME));
		temp.put(536, join(breakTime, DEDUCTION, TOTAL, TIME));
		temp.put(537, join(breakTime, CALC, WITHIN_STATUTORY, TIME));
		temp.put(538, join(breakTime, DEDUCTION, WITHIN_STATUTORY, TIME));
		temp.put(798, join(breakTime, CALC, EXCESS_STATUTORY, TIME));
		temp.put(574, join(breakTime, WORKING_TIME));
		temp.put(160, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_1)));
		temp.put(161, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_1)));
		temp.put(166, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_2)));
		temp.put(167, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_2)));
		temp.put(172, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_3)));
		temp.put(173, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_3)));
		temp.put(178, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_4)));
		temp.put(179, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_4)));
		temp.put(184, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_5)));
		temp.put(185, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_5)));
		temp.put(190, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_6)));
		temp.put(191, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_6)));
		temp.put(196, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_7)));
		temp.put(197, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_7)));
		temp.put(202, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_8)));
		temp.put(203, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_8)));
		temp.put(208, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_9)));
		temp.put(209, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_9)));
		temp.put(214, join(breakTime, AFTER_CORRECTED, joinNS(START, NUMBER_1, NUMBER_0)));
		temp.put(215, join(breakTime, AFTER_CORRECTED, joinNS(END, NUMBER_1, NUMBER_0)));
		
		temp.put(580, join(totalLabol, SHORT_WORK, TOTAL, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(581, join(totalLabol, SHORT_WORK, TOTAL, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(582, join(totalLabol, SHORT_WORK, TOTAL, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(583, join(totalLabol, SHORT_WORK, DEDUCTION, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(584, join(totalLabol, SHORT_WORK, DEDUCTION, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(585, join(totalLabol, SHORT_WORK, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(586, join(totalLabol, SHORT_WORK, TOTAL, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(587, join(totalLabol, SHORT_WORK, TOTAL, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(588, join(totalLabol, SHORT_WORK, TOTAL, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(589, join(totalLabol, SHORT_WORK, DEDUCTION, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(590, join(totalLabol, SHORT_WORK, DEDUCTION, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(591, join(totalLabol, SHORT_WORK, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(617, join(totalLabol, TEMPORARY, joinNS(LATE_NIGHT, NUMBER_1)));
		temp.put(619, join(totalLabol, TEMPORARY, joinNS(LATE_NIGHT, NUMBER_2)));
		temp.put(621, join(totalLabol, TEMPORARY, joinNS(LATE_NIGHT, NUMBER_3)));
		temp.put(618, join(totalLabol, TEMPORARY, joinNS(TIME, NUMBER_1)));
		temp.put(620, join(totalLabol, TEMPORARY, joinNS(TIME, NUMBER_2)));
		temp.put(622, join(totalLabol, TEMPORARY, joinNS(TIME, NUMBER_3)));
		String excess = join(totalLabol, EXCESS_STATUTORY);
		// ITEM_ID_CONTAINER.put(565,"日別実績の勤怠時間.実績時間.総労働時間.所定外時間.残業時間.所定外深夜時間.事前申請時間");
		temp.put(565, join(excess, LATE_NIGHT, BEFOR_APPLICATION));
		temp.put(563, join(excess, LATE_NIGHT, TIME, TIME));
		temp.put(564, join(excess, LATE_NIGHT, TIME, CALC));
		String excessHoliday = join(excess, HOLIDAY_WORK);
		temp.put(746, join(excessHoliday, RESTRAINT));
		temp.put(568, join(excessHoliday, LATE_NIGHT, LEGAL, TIME));
		temp.put(569, join(excessHoliday, LATE_NIGHT, LEGAL, CALC));
		temp.put(570, join(excessHoliday, LATE_NIGHT, ILLEGAL, TIME));
		temp.put(571, join(excessHoliday, LATE_NIGHT, ILLEGAL, CALC));
		temp.put(572, join(excessHoliday, LATE_NIGHT, PUBLIC_HOLIDAY, TIME));
		temp.put(573, join(excessHoliday, LATE_NIGHT, PUBLIC_HOLIDAY, CALC));
		temp.put(270, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_1)));
		temp.put(275, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_2)));
		temp.put(280, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_3)));
		temp.put(285, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_4)));
		temp.put(290, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_5)));
		temp.put(295, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_6)));
		temp.put(300, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_7)));
		temp.put(305, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_8)));
		temp.put(310, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_9)));
		temp.put(315, join(excessHoliday, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_1, NUMBER_0)));
		temp.put(266, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_1)));
		temp.put(268, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_1)));
		temp.put(267, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_1)));
		temp.put(269, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_1)));
		temp.put(271, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_2)));
		temp.put(273, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_2)));
		temp.put(272, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_2)));
		temp.put(274, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_2)));
		temp.put(276, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_3)));
		temp.put(278, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_3)));
		temp.put(277, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_3)));
		temp.put(279, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_3)));
		temp.put(281, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_4)));
		temp.put(283, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_4)));
		temp.put(282, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_4)));
		temp.put(284, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_4)));
		temp.put(286, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_5)));
		temp.put(288, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_5)));
		temp.put(287, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_5)));
		temp.put(289, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_5)));
		temp.put(291, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_6)));
		temp.put(293, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_6)));
		temp.put(292, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_6)));
		temp.put(294, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_6)));
		temp.put(296, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_7)));
		temp.put(298, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_7)));
		temp.put(297, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_7)));
		temp.put(299, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_7)));
		temp.put(301, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_8)));
		temp.put(303, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_8)));
		temp.put(302, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_8)));
		temp.put(304, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_8)));
		temp.put(306, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_9)));
		temp.put(308, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_9)));
		temp.put(307, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_9)));
		temp.put(309, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_9)));
		temp.put(311, join(excessHoliday, FRAMES, TIME, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(313, join(excessHoliday, FRAMES, TIME, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(312, join(excessHoliday, FRAMES, TRANSFER, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(314, join(excessHoliday, FRAMES, TRANSFER, joinNS(CALC, NUMBER_1, NUMBER_0)));
		
		temp.put(777, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_1)));
		temp.put(778, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_2)));
		temp.put(779, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_3)));
		temp.put(780, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_4)));
		temp.put(781, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_5)));
		temp.put(782, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_6)));
		temp.put(783, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_7)));
		temp.put(784, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_8)));
		temp.put(785, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_9)));
		temp.put(786, join(excessHoliday, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_1, NUMBER_0)));
		temp.put(790, join(excessHoliday, LATE_NIGHT, LEGAL, DIVERGENCE));
		temp.put(791, join(excessHoliday, LATE_NIGHT, ILLEGAL, DIVERGENCE));
		temp.put(792, join(excessHoliday, LATE_NIGHT, PUBLIC_HOLIDAY, DIVERGENCE));
		
		String excessOvertime = join(excess, OVERTIME);
		temp.put(566, join(excessOvertime, LATE_NIGHT, TIME, TIME));
		temp.put(567, join(excessOvertime, LATE_NIGHT, TIME, CALC));
		temp.put(555, join(excessOvertime, FLEX, BEFOR_APPLICATION));
		temp.put(556, join(excessOvertime, FLEX, TIME, TIME));
		temp.put(557, join(excessOvertime, FLEX, TIME, CALC));
		temp.put(745, join(excessOvertime, RESTRAINT));
		temp.put(551, join(excessOvertime, IRREGULAR + LEGAL));
		temp.put(220, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_1)));
		temp.put(225, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_2)));
		temp.put(230, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_3)));
		temp.put(235, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_4)));
		temp.put(240, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_5)));
		temp.put(245, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_6)));
		temp.put(250, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_7)));
		temp.put(255, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_8)));
		temp.put(260, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_9)));
		temp.put(265, join(excessOvertime, FRAMES, joinNS(BEFOR_APPLICATION, NUMBER_1, NUMBER_0)));
		temp.put(217, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_1)));
		temp.put(219, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_1)));
		temp.put(222, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_2)));
		temp.put(224, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_2)));
		temp.put(227, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_3)));
		temp.put(229, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_3)));
		temp.put(232, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_4)));
		temp.put(234, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_4)));
		temp.put(237, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_5)));
		temp.put(239, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_5)));
		temp.put(242, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_6)));
		temp.put(244, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_6)));
		temp.put(247, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_7)));
		temp.put(249, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_7)));
		temp.put(252, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_8)));
		temp.put(254, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_8)));
		temp.put(257, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_9)));
		temp.put(259, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_9)));
		temp.put(262, join(excessOvertime, FRAMES, TRANSFER, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(264, join(excessOvertime, FRAMES, TRANSFER, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(216, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_1)));
		temp.put(218, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_1)));
		temp.put(221, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_2)));
		temp.put(223, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_2)));
		temp.put(226, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_3)));
		temp.put(228, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_3)));
		temp.put(231, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_4)));
		temp.put(233, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_4)));
		temp.put(236, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_5)));
		temp.put(238, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_5)));
		temp.put(241, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_6)));
		temp.put(243, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_6)));
		temp.put(246, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_7)));
		temp.put(248, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_7)));
		temp.put(251, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_8)));
		temp.put(253, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_8)));
		temp.put(256, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_9)));
		temp.put(258, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_9)));
		temp.put(261, join(excessOvertime, FRAMES, TIME, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(263, join(excessOvertime, FRAMES, TIME, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(767, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_1)));
		temp.put(768, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_2)));
		temp.put(769, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_3)));
		temp.put(770, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_4)));
		temp.put(771, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_5)));
		temp.put(772, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_6)));
		temp.put(773, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_7)));
		temp.put(774, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_8)));
		temp.put(775, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_9)));
		temp.put(776, join(excessOvertime, FRAMES, TIME, joinNS(DIVERGENCE, NUMBER_1, NUMBER_0)));
		temp.put(788, join(excessOvertime, FLEX, TIME, DIVERGENCE));
		temp.put(789, join(excessOvertime, LATE_NIGHT, TIME, DIVERGENCE));
		temp.put(604, join(totalLabol, LEAVE_EARLY, TIME, joinNS(TIME, NUMBER_1)));
		temp.put(605, join(totalLabol, LEAVE_EARLY, TIME, joinNS(CALC, NUMBER_1)));
		temp.put(610, join(totalLabol, LEAVE_EARLY, TIME, joinNS(TIME, NUMBER_2)));
		temp.put(611, join(totalLabol, LEAVE_EARLY, TIME, joinNS(CALC, NUMBER_2)));
		temp.put(606, join(totalLabol, LEAVE_EARLY, DEDUCTION, joinNS(CALC, NUMBER_1)));
		temp.put(612, join(totalLabol, LEAVE_EARLY, DEDUCTION, joinNS(CALC, NUMBER_2)));
		temp.put(607, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, NUMBER_1)));
		temp.put(613, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, NUMBER_2)));
		
		temp.put(1131, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(SPECIAL, NUMBER_1)));
		temp.put(1132, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(SPECIAL+FRAME, NUMBER_1)));
		temp.put(1133, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(CHILD_CARE, NUMBER_1)));
		temp.put(1134, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(CARE, NUMBER_1)));
		temp.put(1135, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(SPECIAL, NUMBER_2)));
		temp.put(1136, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(SPECIAL+FRAME, NUMBER_2)));
		temp.put(1137, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(CHILD_CARE, NUMBER_2)));
		temp.put(1138, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(CARE, NUMBER_2)));
		
		temp.put(609, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(COMPENSATORY, NUMBER_1)));
		temp.put(615, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(COMPENSATORY, NUMBER_2)));
		temp.put(608, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(EXCESS, NUMBER_1)));
		temp.put(614, join(totalLabol, LEAVE_EARLY, HOLIDAY + USAGE, joinNS(EXCESS, NUMBER_2)));
		temp.put(867, join(totalLabol, LEAVE_EARLY,  joinNS(VALUE, NUMBER_1)));
		temp.put(868, join(totalLabol, LEAVE_EARLY,  joinNS(VALUE, NUMBER_2)));
		temp.put(594, join(totalLabol, LATE, DEDUCTION, joinNS(CALC, NUMBER_1)));
		temp.put(600, join(totalLabol, LATE, DEDUCTION, joinNS(CALC, NUMBER_2)));
		temp.put(592, join(totalLabol, LATE, TIME, joinNS(TIME, NUMBER_1)));
		temp.put(593, join(totalLabol, LATE, TIME, joinNS(CALC, NUMBER_1)));
		temp.put(598, join(totalLabol, LATE, TIME, joinNS(TIME, NUMBER_2)));
		temp.put(599, join(totalLabol, LATE, TIME, joinNS(CALC, NUMBER_2)));
		temp.put(595, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, NUMBER_1)));
		temp.put(601, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, NUMBER_2)));
		
		temp.put(1123, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(SPECIAL, NUMBER_1)));
		temp.put(1124, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(SPECIAL+FRAME, NUMBER_1)));
		temp.put(1125, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(CHILD_CARE, NUMBER_1)));
		temp.put(1126, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(CARE, NUMBER_1)));
		temp.put(1127, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(SPECIAL, NUMBER_2)));
		temp.put(1128, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(SPECIAL+FRAME, NUMBER_2)));
		temp.put(1129, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(CHILD_CARE, NUMBER_2)));
		temp.put(1130, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(CARE, NUMBER_2)));
		
		
		temp.put(597, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(COMPENSATORY, NUMBER_1)));
		temp.put(603, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(COMPENSATORY, NUMBER_2)));
		temp.put(596, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(EXCESS, NUMBER_1)));
		temp.put(602, join(totalLabol, LATE, HOLIDAY + USAGE, joinNS(EXCESS, NUMBER_2)));
		temp.put(865, join(totalLabol, LATE,  joinNS(VALUE, NUMBER_1)));
		temp.put(866, join(totalLabol, LATE,  joinNS(VALUE, NUMBER_1)));
		temp.put(462, join(totalLabol, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(475, join(totalLabol, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(482, join(totalLabol, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(489, join(totalLabol, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(463, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(467, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(476, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(479, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(483, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(486, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(490, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(494, join(totalLabol, GO_OUT, CALC, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(465, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(469, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(478, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(481, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(485, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(488, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(492, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(496, join(totalLabol, GO_OUT, CALC, EXCESS_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(464, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(468, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(477, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(480, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(484, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(487, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(491, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(495, join(totalLabol, GO_OUT, CALC, WITHIN_STATUTORY, WITHIN_STATUTORY, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(502, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(503, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(EXCESS, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(504, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(SPECIAL, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(1145, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(SPECIAL+FRAME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(1140, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(CHILD_CARE, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(1141, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(CARE, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		
		temp.put(505, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(COMPENSATORY, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(506, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(507, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(EXCESS, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(508, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(SPECIAL, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(509, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(COMPENSATORY, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(510, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(511, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(EXCESS, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(512, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(SPECIAL, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(513, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(COMPENSATORY, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(514, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(ANNUNAL_LEAVE, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(515, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(EXCESS, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(516, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(SPECIAL, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(1146, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(SPECIAL+FRAME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(1142, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(CHILD_CARE, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(1143, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(CARE, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		
		temp.put(517, join(totalLabol, GO_OUT, HOLIDAY + USAGE, joinNS(COMPENSATORY, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(466, join(totalLabol, GO_OUT, CALC + OUT_CORE, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(470, join(totalLabol, GO_OUT, CALC + OUT_CORE, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(493, join(totalLabol, GO_OUT, CALC + OUT_CORE, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(497, join(totalLabol, GO_OUT, CALC + OUT_CORE, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(474, join(totalLabol, GO_OUT, DEDUCTION + OUT_CORE, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(501, join(totalLabol, GO_OUT, DEDUCTION + OUT_CORE, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(471, join(totalLabol, GO_OUT, DEDUCTION, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(498, join(totalLabol, GO_OUT, DEDUCTION, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(473, join(totalLabol, GO_OUT, DEDUCTION, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(500, join(totalLabol, GO_OUT, DEDUCTION, EXCESS_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(472, join(totalLabol, GO_OUT, DEDUCTION, WITHIN_STATUTORY,  WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(499, join(totalLabol, GO_OUT, DEDUCTION, WITHIN_STATUTORY,  WITHIN_STATUTORY, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(89, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_1)));
		temp.put(96, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_2)));
		temp.put(103, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_3)));
		temp.put(110, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_4)));
		temp.put(117, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_5)));
		temp.put(124, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_6)));
		temp.put(131, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_7)));
		temp.put(138, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_8)));
		temp.put(145, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_9)));
		temp.put(152, join(totalLabol, GO_OUT, AFTER_CORRECTED, GO_OUT, STAMP, joinNS(CLOCK, NUMBER_1, NUMBER_1)));
		temp.put(92, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_1)));
		temp.put(99, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_2)));
		temp.put(106, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_3)));
		temp.put(113, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_4)));
		temp.put(120, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_5)));
		temp.put(127, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_6)));
		temp.put(134, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_7)));
		temp.put(141, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_8)));
		temp.put(148, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_9)));
		temp.put(155, join(totalLabol, GO_OUT, AFTER_CORRECTED, BACK, STAMP, joinNS(CLOCK, NUMBER_1, NUMBER_0)));
		
		temp.put(316, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_1)));
		temp.put(317, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_2)));
		temp.put(318, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_3)));
		temp.put(319, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_4)));
		temp.put(320, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_5)));
		temp.put(321, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_6)));
		temp.put(322, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_7)));
		temp.put(323, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_8)));
		temp.put(324, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_9)));
		temp.put(325, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(326, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_1)));
		temp.put(327, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_2)));
		temp.put(328, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_3)));
		temp.put(329, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_4)));
		temp.put(330, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_5)));
		temp.put(331, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_6)));
		temp.put(332, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_7)));
		temp.put(333, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_8)));
		temp.put(334, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_9)));
		temp.put(335, join(totalLabol, RAISING_SALARY, RAISING_SALARY, RAISING_SALARY, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(346, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_1)));
		temp.put(347, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_2)));
		temp.put(348, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_3)));
		temp.put(349, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_4)));
		temp.put(350, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_5)));
		temp.put(351, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_6)));
		temp.put(352, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_7)));
		temp.put(353, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_8)));
		temp.put(354, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_9)));
		temp.put(355, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(356, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_1)));
		temp.put(357, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_2)));
		temp.put(358, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_3)));
		temp.put(359, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_4)));
		temp.put(360, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_5)));
		temp.put(361, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_6)));
		temp.put(362, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_7)));
		temp.put(363, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_8)));
		temp.put(364, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_9)));
		temp.put(365, join(totalLabol, RAISING_SALARY, RAISING_SALARY, ILLEGAL, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(336, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_1)));
		temp.put(337, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_2)));
		temp.put(338, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_3)));
		temp.put(339, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_4)));
		temp.put(340, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_5)));
		temp.put(341, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_6)));
		temp.put(342, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_7)));
		temp.put(343, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_8)));
		temp.put(344, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_9)));
		temp.put(345, join(totalLabol, RAISING_SALARY, RAISING_SALARY, LEGAL, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(366, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_1)));
		temp.put(367, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_2)));
		temp.put(368, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_3)));
		temp.put(369, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_4)));
		temp.put(370, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_5)));
		temp.put(371, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_6)));
		temp.put(372, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_7)));
		temp.put(373, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_8)));
		temp.put(374, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_9)));
		temp.put(375, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(376, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_1)));
		temp.put(377, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_2)));
		temp.put(378, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_3)));
		temp.put(379, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_4)));
		temp.put(380, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_5)));
		temp.put(381, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_6)));
		temp.put(382, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_7)));
		temp.put(383, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_8)));
		temp.put(384, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_9)));
		temp.put(385, join(totalLabol, RAISING_SALARY, SPECIFIC, RAISING_SALARY, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(396, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_1)));
		temp.put(397, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_2)));
		temp.put(398, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_3)));
		temp.put(399, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_4)));
		temp.put(400, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_5)));
		temp.put(401, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_6)));
		temp.put(402, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_7)));
		temp.put(403, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_8)));
		temp.put(404, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_9)));
		temp.put(405, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(406, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_1)));
		temp.put(407, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_2)));
		temp.put(408, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_3)));
		temp.put(409, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_4)));
		temp.put(410, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_5)));
		temp.put(411, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_6)));
		temp.put(412, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_7)));
		temp.put(413, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_8)));
		temp.put(414, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_9)));
		temp.put(415, join(totalLabol, RAISING_SALARY, SPECIFIC, ILLEGAL, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(386, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_1)));
		temp.put(387, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_2)));
		temp.put(388, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_3)));
		temp.put(389, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_4)));
		temp.put(390, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_5)));
		temp.put(391, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_6)));
		temp.put(392, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_7)));
		temp.put(393, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_8)));
		temp.put(394, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_9)));
		temp.put(395, join(totalLabol, RAISING_SALARY, SPECIFIC, LEGAL, joinNS(TIME, NUMBER_1, NUMBER_0)));
	}
	
	private static void getMonthlyKey(Map<Integer, String> temp) {
		temp.put(192, join(MONTHLY_AFFILIATION_INFO_NAME, START_MONTH, EMPLOYEMENT));
		temp.put(193, join(MONTHLY_AFFILIATION_INFO_NAME, START_MONTH, JOB_TITLE));
		temp.put(194, join(MONTHLY_AFFILIATION_INFO_NAME, START_MONTH, WORKPLACE));
		temp.put(195, join(MONTHLY_AFFILIATION_INFO_NAME, START_MONTH, CLASSIFICATION));
		temp.put(196, join(MONTHLY_AFFILIATION_INFO_NAME, START_MONTH, BUSINESS_TYPE));
		temp.put(197, join(MONTHLY_AFFILIATION_INFO_NAME, END_MONTH, EMPLOYEMENT));
		temp.put(198, join(MONTHLY_AFFILIATION_INFO_NAME, END_MONTH, JOB_TITLE));
		temp.put(199, join(MONTHLY_AFFILIATION_INFO_NAME, END_MONTH, WORKPLACE));
		temp.put(200, join(MONTHLY_AFFILIATION_INFO_NAME, END_MONTH, CLASSIFICATION));
		temp.put(201, join(MONTHLY_AFFILIATION_INFO_NAME, END_MONTH, BUSINESS_TYPE));
		
		temp.put(1,    join(MONTHLY_ATTENDANCE_TIME_NAME, PERIOD, START));
		temp.put(2,    join(MONTHLY_ATTENDANCE_TIME_NAME, PERIOD, END));
		temp.put(3,    join(MONTHLY_ATTENDANCE_TIME_NAME, AGGREGATE + DAYS));
		temp.put(4,    join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, WITHIN_STATUTORY));
		temp.put(5,    join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, WEEKLY_PREMIUM + TOTAL));
		temp.put(6,    join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, MONTHLY_PREMIUM + TOTAL));
		temp.put(7,    join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, IRREGULAR + LABOR, SHORTAGE));
		temp.put(8,    join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, IRREGULAR + LABOR, CARRY_FORWARD));
		temp.put(9,    join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, IRREGULAR + LABOR, MULTI_MONTH + MIDDLE));
		temp.put(10,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, IRREGULAR + LABOR, LEGAL + OVERTIME, TIME));
		temp.put(11,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, ACTUAL, IRREGULAR + LABOR, LEGAL + OVERTIME, CALC));
		temp.put(12,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, TIME, TIME));
		temp.put(13,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, TIME, CALC));
		temp.put(14,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, BEFORE));
		temp.put(15,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, LEGAL));
		temp.put(16,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, ILLEGAL));
		temp.put(2199,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, CUR_MONTH, FLEX));
		temp.put(2202,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, TIME, CUR_MONTH, EXCESS + AVERAGE));
		temp.put(17,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, EXCESS + TIME));
		temp.put(18,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, SHORTAGE));
		temp.put(19,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, CARRY_FORWARD, TIME));
		temp.put(20,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, CARRY_FORWARD, WORKING_TIME));
		temp.put(21,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, CARRY_FORWARD, SHORTAGE));
		temp.put(2201,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, CARRY_FORWARD, NO + SHORTAGE));
		temp.put(22,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, EXCESS, EXCESS + ATTRIBUTE));
		temp.put(23,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, EXCESS, PRINCIPLE));
		temp.put(24,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, EXCESS, CONVENIENCE));
		temp.put(189,  join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, SHORTAGE + DEDUCTION, ANNUNAL_LEAVE));
		temp.put(190,  join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, SHORTAGE + DEDUCTION, ABSENCE));
		temp.put(191,  join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, SHORTAGE + DEDUCTION, BEFORE));
		temp.put(2200,  join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, FLEX, CUR_MONTH + FLEX));
		
		temp.put(25,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, RESTRAINT, OVERTIME));
		temp.put(26,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, RESTRAINT, HOLIDAY_WORK));
		temp.put(27,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, RESTRAINT, LATE_NIGHT));
		temp.put(28,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, RESTRAINT, TOTAL));
		temp.put(29,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, RESTRAINT, DIFF));
		temp.put(30,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, TOTAL_LABOR));
		temp.put(31,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, WORK_TIME, WORK_TIME));
		temp.put(32,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, WORK_TIME, PREMIUM));
		temp.put(33,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, FIXED_WORK, PLAN));
		temp.put(34,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, FIXED_WORK, ACTUAL));
		temp.put(45,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, TOTAL, TIME));
		temp.put(56,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, TOTAL, CALC));
		temp.put(67,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, BEFORE));
		temp.put(78,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, TRANSFER + TOTAL, TIME));
		temp.put(89,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, TRANSFER + TOTAL, CALC));
		temp.put(35,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_1));
		temp.put(36,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_2));
		temp.put(37,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_3));
		temp.put(38,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_4));
		temp.put(39,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_5));
		temp.put(40,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_6));
		temp.put(41,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_7));
		temp.put(42,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_8));
		temp.put(43,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, TIME + NUMBER_9));
		temp.put(44,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(46,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_1));
		temp.put(47,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_2));
		temp.put(48,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_3));
		temp.put(49,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_4));
		temp.put(50,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_5));
		temp.put(51,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_6));
		temp.put(52,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_7));
		temp.put(53,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_8));
		temp.put(54,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, CALC + NUMBER_9));
		temp.put(55,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, OVERTIME, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(57,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_1));
		temp.put(58,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_2));
		temp.put(59,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_3));
		temp.put(60,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_4));
		temp.put(61,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_5));
		temp.put(62,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_6));
		temp.put(63,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_7));
		temp.put(64,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_8));
		temp.put(65,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, BEFORE + NUMBER_9));
		temp.put(66,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(BEFORE, NUMBER_1, NUMBER_0)));
		temp.put(68,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_1));
		temp.put(69,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_2));
		temp.put(70,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_3));
		temp.put(71,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_4));
		temp.put(72,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_5));
		temp.put(73,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_6));
		temp.put(74,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_7));
		temp.put(75,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_8));
		temp.put(76,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, TIME + NUMBER_9));
		temp.put(77,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(79,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_1));
		temp.put(80,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_2));
		temp.put(81,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_3));
		temp.put(82,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_4));
		temp.put(83,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_5));
		temp.put(84,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_6));
		temp.put(85,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_7));
		temp.put(86,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_8));
		temp.put(87,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, CALC + NUMBER_9));
		temp.put(88,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, TRANSFER, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(90,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_1));
		temp.put(91,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_2));
		temp.put(92,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_3));
		temp.put(93,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_4));
		temp.put(94,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_5));
		temp.put(95,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_6));
		temp.put(96,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_7));
		temp.put(97,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_8));
		temp.put(98,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, LEGAL + NUMBER_9));
		temp.put(99,   join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, NUMBER_1, NUMBER_0)));
		temp.put(100, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_1)));
		temp.put(101, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_2)));
		temp.put(102, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_3)));
		temp.put(103, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_4)));
		temp.put(104, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_5)));
		temp.put(105, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_6)));
		temp.put(106, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_7)));
		temp.put(107, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_8)));
		temp.put(108, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_9)));
		temp.put(109, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, OVERTIME, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_1, NUMBER_0)));
		temp.put(120, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, TOTAL, TIME));
		temp.put(131, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, TOTAL, CALC));
		temp.put(142, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, BEFORE));
		temp.put(153, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, TRANSFER + TOTAL, TIME));
		temp.put(164, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, TRANSFER + TOTAL, CALC));
		temp.put(110, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_1));
		temp.put(111, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_2));
		temp.put(112, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_3));
		temp.put(113, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_4));
		temp.put(114, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_5));
		temp.put(115, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_6));
		temp.put(116, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_7));
		temp.put(117, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_8));
		temp.put(118, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, TIME + NUMBER_9));
		temp.put(119, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(121, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_1));
		temp.put(122, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_2));
		temp.put(123, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_3));
		temp.put(124, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_4));
		temp.put(125, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_5));
		temp.put(126, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_6));
		temp.put(127, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_7));
		temp.put(128, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_8));
		temp.put(129, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, CALC + NUMBER_9));
		temp.put(130, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, HOLIDAY_WORK, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(132, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_1));
		temp.put(133, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_2));
		temp.put(134, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_3));
		temp.put(135, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_4));
		temp.put(136, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_5));
		temp.put(137, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_6));
		temp.put(138, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_7));
		temp.put(139, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_8));
		temp.put(140, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, BEFORE + NUMBER_9));
		temp.put(141, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(BEFORE, NUMBER_1, NUMBER_0)));
		temp.put(143, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_1));
		temp.put(144, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_2));
		temp.put(145, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_3));
		temp.put(146, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_4));
		temp.put(147, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_5));
		temp.put(148, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_6));
		temp.put(149, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_7));
		temp.put(150, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_8));
		temp.put(151, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, TIME + NUMBER_9));
		temp.put(152, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(154, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_1));
		temp.put(155, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_2));
		temp.put(156, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_3));
		temp.put(157, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_4));
		temp.put(158, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_5));
		temp.put(159, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_6));
		temp.put(160, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_7));
		temp.put(161, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_8));
		temp.put(162, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, CALC + NUMBER_9));
		temp.put(163, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, TRANSFER, joinNS(CALC, NUMBER_1, NUMBER_0)));
		temp.put(165, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_1)));
		temp.put(166, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_2)));
		temp.put(167, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_3)));
		temp.put(168, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_4)));
		temp.put(169, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_5)));
		temp.put(170, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_6)));
		temp.put(171, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_7)));
		temp.put(172, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_8)));
		temp.put(173, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_9)));
		temp.put(174, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, HOLIDAY_WORK, NUMBER_1, NUMBER_0)));
		temp.put(175, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_1)));
		temp.put(176, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_2)));
		temp.put(177, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_3)));
		temp.put(178, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_4)));
		temp.put(179, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_5)));
		temp.put(180, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_6)));
		temp.put(181, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_7)));
		temp.put(182, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_8)));
		temp.put(183, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_9)));
		temp.put(184, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY_WORK, AGGREGATE, joinNS(LEGAL, TRANSFER, NUMBER_1, NUMBER_0)));
		temp.put(185, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY + USAGE, ANNUNAL_LEAVE));
		temp.put(186, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY + USAGE, SPECIAL));
		temp.put(187, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY + USAGE, RETENTION));
		temp.put(188, join(MONTHLY_ATTENDANCE_TIME_NAME, CALC, AGGREGATE, HOLIDAY + USAGE, COMPENSATORY));
		
		temp.put(208, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ATTENDANCE));
		temp.put(209, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, DAYS));
		temp.put(210, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, COUNT));
		temp.put(211, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, TWO_TIMES + COUNT));
		temp.put(212, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, TEMPORARY + COUNT));
		temp.put(2079, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, TEMPORARY + TIME));
		temp.put(213, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, HOLIDAY_WORK));
		temp.put(214, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, TRANSFER));
		temp.put(215, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, HOLIDAY));
		temp.put(216, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, WITHIN_STATUTORY, DAYS));
		temp.put(217, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, WITHIN_STATUTORY, BEFORE));
		temp.put(218, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, WITHIN_STATUTORY, AFTER));
		
		temp.put(219, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_1));
		temp.put(220, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_2));
		temp.put(221, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_3));
		temp.put(222, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_4));
		temp.put(223, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_5));
		temp.put(224, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_6));
		temp.put(225, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_7));
		temp.put(226, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_8));
		temp.put(227, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, DAYS + NUMBER_9));
		temp.put(228, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, joinNS(DAYS, NUMBER_1, NUMBER_0)));
		temp.put(229, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_1));
		temp.put(230, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_2));
		temp.put(231, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_3));
		temp.put(232, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_4));
		temp.put(233, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_5));
		temp.put(234, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_6));
		temp.put(235, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_7));
		temp.put(236, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_8));
		temp.put(237, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, HOLIDAY_WORK + NUMBER_9));
		temp.put(238, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIFIC, joinNS(HOLIDAY_WORK, NUMBER_1, NUMBER_0)));
		temp.put(239, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_1));
		temp.put(240, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_2));
		temp.put(241, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_3));
		temp.put(242, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_4));
		temp.put(243, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_5));
		temp.put(244, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_6));
		temp.put(245, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_7));
		temp.put(246, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_8));
		temp.put(247, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, DAYS + NUMBER_9));
		temp.put(248, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_0)));
		temp.put(249, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_1)));
		temp.put(250, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_2)));
		temp.put(251, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_3)));
		temp.put(252, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_4)));
		temp.put(253, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_5)));
		temp.put(254, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_6)));
		temp.put(255, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_7)));
		temp.put(256, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_8)));
		temp.put(257, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_9)));
		temp.put(258, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_0)));
		temp.put(259, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_1)));
		temp.put(260, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_2)));
		temp.put(261, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_3)));
		temp.put(262, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_4)));
		temp.put(263, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_5)));
		temp.put(264, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_6)));
		temp.put(265, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_7)));
		temp.put(266, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_8)));
		temp.put(267, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_9)));
		temp.put(268, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(DAYS, NUMBER_3, NUMBER_0)));
		temp.put(269, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, TOTAL + DAYS));
		temp.put(270, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_1));
		temp.put(271, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_2));
		temp.put(272, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_3));
		temp.put(273, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_4));
		temp.put(274, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_5));
		temp.put(275, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_6));
		temp.put(276, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_7));
		temp.put(277, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_8));
		temp.put(278, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, DAYS + NUMBER_9));
		temp.put(279, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_0)));
		temp.put(280, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_1)));
		temp.put(281, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_2)));
		temp.put(282, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_3)));
		temp.put(283, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_4)));
		temp.put(284, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_5)));
		temp.put(285, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_6)));
		temp.put(286, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_7)));
		temp.put(287, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_8)));
		temp.put(288, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_1, NUMBER_9)));
		temp.put(289, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_0)));
		temp.put(290, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_1)));
		temp.put(291, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_2)));
		temp.put(292, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_3)));
		temp.put(293, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_4)));
		temp.put(294, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_5)));
		temp.put(295, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_6)));
		temp.put(296, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_7)));
		temp.put(297, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_8)));
		temp.put(298, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_2, NUMBER_9)));
		temp.put(299, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(DAYS, NUMBER_3, NUMBER_0)));
		temp.put(300, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, TOTAL + DAYS));
		temp.put(303, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, FIXED, joinNS(DAYS, DEFAULT_ENUM_SEPERATOR, E_OFF_BEFORE_BIRTH)));
		temp.put(304, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, FIXED, joinNS(DAYS, DEFAULT_ENUM_SEPERATOR, E_OFF_AFTER_BIRTH)));
		temp.put(305, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, FIXED, joinNS(DAYS, DEFAULT_ENUM_SEPERATOR, E_OFF_CHILD_CARE)));
		temp.put(306, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, FIXED, joinNS(DAYS, DEFAULT_ENUM_SEPERATOR, E_OFF_CARE)));
		temp.put(307, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, FIXED, joinNS(DAYS, DEFAULT_ENUM_SEPERATOR, E_OFF_INJURY)));
		temp.put(308, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, OPTIONAL, DAYS + NUMBER_1));
		temp.put(309, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, OPTIONAL, DAYS + NUMBER_2));
		temp.put(310, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, OPTIONAL, DAYS + NUMBER_3));
		temp.put(311, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SUSPENS_WORK, OPTIONAL, DAYS + NUMBER_4));
		
		
		temp.put(312, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE + LEAVE_EARLY, LATE, COUNT));
		temp.put(313, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE + LEAVE_EARLY, LATE, TIME, TIME));
		temp.put(314, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE + LEAVE_EARLY, LATE, TIME, CALC));
		temp.put(315, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE + LEAVE_EARLY, LEAVE_EARLY, COUNT));
		temp.put(316, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE + LEAVE_EARLY, LEAVE_EARLY, TIME, TIME));
		temp.put(317, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE + LEAVE_EARLY, LEAVE_EARLY, TIME, CALC));
		temp.put(318, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, LEGAL, TIME));
		temp.put(320, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, LEGAL, CALC));
		temp.put(319, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, ILLEGAL, TIME, TIME));
		temp.put(321, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, ILLEGAL, TIME, CALC));
		temp.put(322, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, ILLEGAL, BEFOR_APPLICATION));
		temp.put(323, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, LEGAL + HOLIDAY_WORK, TIME));
		temp.put(324, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, LEGAL + HOLIDAY_WORK, CALC));
		temp.put(325, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, ILLEGAL + HOLIDAY_WORK, TIME));
		temp.put(326, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, ILLEGAL + HOLIDAY_WORK, CALC));
		temp.put(327, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, PUBLIC_HOLIDAY, TIME));
		temp.put(328, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, PUBLIC_HOLIDAY, CALC));
		temp.put(329, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, OVERTIME, TIME));
		temp.put(330, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LATE_NIGHT, OVERTIME, CALC));
		
		temp.put(331, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, BREAK, TIME));
		temp.put(332, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, BREAK, WITHIN_STATUTORY));
		temp.put(333, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, BREAK, EXCESS_STATUTORY));
		temp.put(2080, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, BREAK, COUNT));
		temp.put(2081, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, BREAK, WITHIN_STATUTORY + DEDUCTION));
		temp.put(2082, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, BREAK, EXCESS_STATUTORY + DEDUCTION));
		
		temp.put(334, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_1));
		temp.put(335, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_2));
		temp.put(336, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_3));
		temp.put(337, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_4));
		temp.put(338, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_5));
		temp.put(339, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_6));
		temp.put(340, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_7));
		temp.put(341, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_8));
		temp.put(342, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, TIME + NUMBER_9));
		temp.put(343, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(344, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_1));
		temp.put(345, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_2));
		temp.put(346, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_3));
		temp.put(347, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_4));
		temp.put(348, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_5));
		temp.put(349, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_6));
		temp.put(350, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_7));
		temp.put(351, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_8));
		temp.put(352, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, HOLIDAY_WORK + NUMBER_9));
		temp.put(353, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, NUMBER_1, NUMBER_0)));
		temp.put(354, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_1));
		temp.put(355, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_2));
		temp.put(356, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_3));
		temp.put(357, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_4));
		temp.put(358, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_5));
		temp.put(359, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_6));
		temp.put(360, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_7));
		temp.put(361, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_8));
		temp.put(362, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, SPECIFIC + NUMBER_9));
		temp.put(363, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(SPECIFIC, NUMBER_1, NUMBER_0)));
		temp.put(364, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_1)));
		temp.put(365, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_2)));
		temp.put(366, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_3)));
		temp.put(367, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_4)));
		temp.put(368, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_5)));
		temp.put(369, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_6)));
		temp.put(370, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_7)));
		temp.put(371, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_8)));
		temp.put(372, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_9)));
		temp.put(373, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(HOLIDAY_WORK, SPECIFIC, NUMBER_1, NUMBER_0)));

		temp.put(2097, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_1));
		temp.put(2098, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_2));
		temp.put(2099, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_3));
		temp.put(2100, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_4));
		temp.put(2101, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_5));
		temp.put(2102, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_6));
		temp.put(2103, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_7));
		temp.put(2104, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_8));
		temp.put(2105, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + NUMBER_9));
		temp.put(2106, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(WITHIN_STATUTORY, NUMBER_1, NUMBER_0)));
		temp.put(2107, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_1));
		temp.put(2108, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_2));
		temp.put(2109, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_3));
		temp.put(2110, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_4));
		temp.put(2111, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_5));
		temp.put(2112, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_6));
		temp.put(2113, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_7));
		temp.put(2114, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_8));
		temp.put(2115, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, WITHIN_STATUTORY + SPECIFIC + NUMBER_9));
		temp.put(2116, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(WITHIN_STATUTORY + SPECIFIC, NUMBER_1, NUMBER_0)));
		temp.put(2117, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_1));
		temp.put(2118, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_2));
		temp.put(2119, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_3));
		temp.put(2120, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_4));
		temp.put(2121, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_5));
		temp.put(2122, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_6));
		temp.put(2123, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_7));
		temp.put(2124, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_8));
		temp.put(2125, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, EXCESS_STATUTORY + NUMBER_9));
		temp.put(2126, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, NUMBER_1, NUMBER_0)));
		temp.put(2127, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_1)));
		temp.put(2128, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_2)));
		temp.put(2129, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_3)));
		temp.put(2130, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_4)));
		temp.put(2131, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_5)));
		temp.put(2132, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_6)));
		temp.put(2133, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_7)));
		temp.put(2134, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_8)));
		temp.put(2135, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_9)));
		temp.put(2136, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RAISING_SALARY, joinNS(EXCESS_STATUTORY, SPECIFIC, NUMBER_1, NUMBER_0)));
		
		temp.put(374, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, ATTENDANCE_LEAVE_GATE, STAYING));
		temp.put(375, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, ATTENDANCE_LEAVE_GATE, ATTENDANCE));
		temp.put(376, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, ATTENDANCE_LEAVE_GATE, LEAVE));
		temp.put(377, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, ATTENDANCE_LEAVE_GATE, UNEMPLOYED));
		temp.put(378, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_1));
		temp.put(379, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_2));
		temp.put(380, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_3));
		temp.put(381, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_4));
		temp.put(382, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_5));
		temp.put(383, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_6));
		temp.put(384, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_7));
		temp.put(385, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_8));
		temp.put(386, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, TIME + NUMBER_9));
		temp.put(387, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(2083, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_1));
		temp.put(2084, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_2));
		temp.put(2085, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_3));
		temp.put(2086, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_4));
		temp.put(2087, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_5));
		temp.put(2088, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_6));
		temp.put(2089, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_7));
		temp.put(2090, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_8));
		temp.put(2091, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, AMOUNT + NUMBER_9));
		temp.put(2092, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PREMIUM, PREMIUM, joinNS(AMOUNT, NUMBER_1, NUMBER_0)));
		temp.put(388, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, PLAN_ACTUAL_DIFF));
		temp.put(389, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_1));
		temp.put(390, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_2));
		temp.put(391, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_3));
		temp.put(392, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_4));
		temp.put(393, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_5));
		temp.put(394, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_6));
		temp.put(395, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_7));
		temp.put(396, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_8));
		temp.put(397, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DIVERGENCE + NUMBER_9));
		temp.put(398, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DIVERGENCE, NUMBER_1, NUMBER_0)));
		temp.put(399, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_1));
		temp.put(400, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_2));
		temp.put(401, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_3));
		temp.put(402, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_4));
		temp.put(403, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_5));
		temp.put(404, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_6));
		temp.put(405, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_7));
		temp.put(406, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_8));
		temp.put(407, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, DEDUCTION + NUMBER_9));
		temp.put(408, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, NUMBER_1, NUMBER_0)));
		temp.put(409, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_1)));
		temp.put(410, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_2)));
		temp.put(411, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_3)));
		temp.put(412, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_4)));
		temp.put(413, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_5)));
		temp.put(414, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_6)));
		temp.put(415, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_7)));
		temp.put(416, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_8)));
		temp.put(417, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_9)));
		temp.put(418, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(DEDUCTION, AFTER, NUMBER_1, NUMBER_0)));
		temp.put(419, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_1));
		temp.put(420, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_2));
		temp.put(421, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_3));
		temp.put(422, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_4));
		temp.put(423, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_5));
		temp.put(424, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_6));
		temp.put(425, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_7));
		temp.put(426, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_8));
		temp.put(427, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, ATTRIBUTE + NUMBER_9));
		temp.put(428, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, DIVERGENCE, joinNS(ATTRIBUTE, NUMBER_1, NUMBER_0)));
		
		temp.put(429, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(430, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(431, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(432, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(433, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(434, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(435, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(2137, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(2138, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_SUPPORT)));
		temp.put(436, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(437, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(438, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(439, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(440, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(441, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(442, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(2139, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(2140, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_UNION)));
		temp.put(443, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(444, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(445, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(446, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(447, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(448, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(449, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(2141, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(2142, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_CHARGE)));
		temp.put(450, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(451, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(452, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, LEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(453, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(454, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, ILLEGAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(455, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(456, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, TOTAL, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(2143, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(2144, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, GO_OUT, CORE_OUT, joinNS(CALC, DEFAULT_ENUM_SEPERATOR, E_OFFICAL)));
		temp.put(457, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(458, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(2093, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(WITHIN_STATUTORY, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(2094, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(EXCESS_STATUTORY, DEFAULT_ENUM_SEPERATOR, E_CHILD_CARE)));
		temp.put(459, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(COUNT, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(460, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(TIME, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(2095, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(WITHIN_STATUTORY, DEFAULT_ENUM_SEPERATOR, E_CARE)));
		temp.put(2096, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, GO_OUT, E_CHILD_CARE, joinNS(EXCESS_STATUTORY, DEFAULT_ENUM_SEPERATOR, E_CARE)));

		temp.put(2145, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, STRAIGHT_GO_BACK, STRAIGHT_GO));
		temp.put(2146, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, STRAIGHT_GO_BACK, STRAIGHT_BACK));
		temp.put(2147, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, STRAIGHT_GO_BACK, STRAIGHT_GO_BACK));
		temp.put(2148, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, TIME_DIGESTION + TIME));
		temp.put(2149, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, TIME_DIGESTION + DAYS));
		temp.put(2150, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, INTERVAL));
		temp.put(2151, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, INTERVAL + DEDUCTION));
		temp.put(2194, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, HOLIDAY + USAGE, TRANSFER_HOLIDAY));
		temp.put(2195, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, HOLIDAY + USAGE, ABSENCE));
		temp.put(2196, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LABOR, ACTUAL));
		temp.put(2197, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LABOR, TOTAL_CALC));
		temp.put(2198, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, LABOR, CALC + DIFF));
		
		temp.put(2152, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, AMOUNT + LAYOUT_A));
		temp.put(2153, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, AMOUNT + LAYOUT_B));
		temp.put(2154, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1));
		temp.put(2155, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2));
		temp.put(2156, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3));
		temp.put(2157, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_4));
		temp.put(2158, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_5));
		temp.put(2159, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_6));
		temp.put(2160, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_7));
		temp.put(2161, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_8));
		temp.put(2162, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_9));
		temp.put(2163, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_0));
		temp.put(2163, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_0));
		temp.put(2164, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_1));
		temp.put(2165, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_2));
		temp.put(2166, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_3));
		temp.put(2167, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_4));
		temp.put(2168, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_5));
		temp.put(2169, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_6));
		temp.put(2170, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_7));
		temp.put(2171, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_8));
		temp.put(2172, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_1 + NUMBER_9));
		temp.put(2173, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_0));
		temp.put(2174, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_1));
		temp.put(2175, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_2));
		temp.put(2176, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_3));
		temp.put(2177, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_4));
		temp.put(2178, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_5));
		temp.put(2179, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_6));
		temp.put(2180, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_7));
		temp.put(2181, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_8));
		temp.put(2182, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_2 + NUMBER_9));
		temp.put(2183, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_0));
		temp.put(2184, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_1));
		temp.put(2185, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_2));
		temp.put(2186, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_3));
		temp.put(2187, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_4));
		temp.put(2188, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_5));
		temp.put(2189, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_6));
		temp.put(2190, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_7));
		temp.put(2191, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_8));
		temp.put(2192, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_3 + NUMBER_9));
		temp.put(2193, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, RESERVATION, RESERVATION, NUMBER + NUMBER_4 + NUMBER_0));
		
		temp.put(461, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, CLOCK, LOGON, DAYS));
		temp.put(462, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, CLOCK, LOGON, TOTAL));
		temp.put(463, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, CLOCK, LOGON, AVERAGE));
		temp.put(464, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, CLOCK, LOGOFF, DAYS));
		temp.put(465, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, CLOCK, LOGOFF, TOTAL));
		temp.put(466, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, CLOCK, LOGOFF, AVERAGE));
		temp.put(467, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, DIVERGENCE, LOGON, TOTAL));
		temp.put(468, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, DIVERGENCE, LOGON, DAYS));
		temp.put(469, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, DIVERGENCE, LOGON, AVERAGE));
		temp.put(470, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, DIVERGENCE, LOGOFF, TOTAL));
		temp.put(471, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, DIVERGENCE, LOGOFF, DAYS));
		temp.put(472, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, PC, DIVERGENCE, LOGOFF, AVERAGE));
		temp.put(473, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, END_WORK, COUNT));
		temp.put(474, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, END_WORK, AVERAGE));
		temp.put(475, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, CLOCK, END_WORK, TOTAL));
		
		temp.put(476, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_1));
		temp.put(477, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_2));
		temp.put(478, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_3));
		temp.put(479, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_4));
		temp.put(480, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_5));
		temp.put(481, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_6));
		temp.put(482, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_7));
		temp.put(483, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_8));
		temp.put(484, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, COUNT + NUMBER_9));
		temp.put(485, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_0)));
		temp.put(486, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_1)));
		temp.put(487, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_2)));
		temp.put(488, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_3)));
		temp.put(489, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_4)));
		temp.put(490, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_5)));
		temp.put(491, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_6)));
		temp.put(492, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_7)));
		temp.put(493, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_8)));
		temp.put(494, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_1, NUMBER_9)));
		temp.put(495, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_0)));
		temp.put(496, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_1)));
		temp.put(497, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_2)));
		temp.put(498, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_3)));
		temp.put(499, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_4)));
		temp.put(500, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_5)));
		temp.put(501, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_6)));
		temp.put(502, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_7)));
		temp.put(503, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_8)));
		temp.put(504, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_2, NUMBER_9)));
		temp.put(505, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(COUNT, NUMBER_3, NUMBER_0)));
		temp.put(506, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_1));
		temp.put(507, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_2));
		temp.put(508, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_3));
		temp.put(509, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_4));
		temp.put(510, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_5));
		temp.put(511, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_6));
		temp.put(512, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_7));
		temp.put(513, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_8));
		temp.put(514, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, TIME + NUMBER_9));
		temp.put(515, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(516, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_1)));
		temp.put(517, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_2)));
		temp.put(518, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_3)));
		temp.put(519, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_4)));
		temp.put(520, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_5)));
		temp.put(521, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_6)));
		temp.put(522, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_7)));
		temp.put(523, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_8)));
		temp.put(524, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_1, NUMBER_9)));
		temp.put(525, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_0)));
		temp.put(526, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_1)));
		temp.put(527, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_2)));
		temp.put(528, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_3)));
		temp.put(529, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_4)));
		temp.put(530, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_5)));
		temp.put(531, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_6)));
		temp.put(532, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_7)));
		temp.put(533, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_8)));
		temp.put(534, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_2, NUMBER_9)));
		temp.put(535, join(MONTHLY_ATTENDANCE_TIME_NAME, COUNT + AGGREGATE, AGGREGATE, joinNS(TIME, NUMBER_3, NUMBER_0)));
		temp.put(536, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_1)));
		temp.put(537, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_2)));
		temp.put(538, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_3)));
		temp.put(539, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_4)));
		temp.put(540, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_5)));
		temp.put(541, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_6)));
		temp.put(542, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_7)));
		temp.put(543, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_8)));
		temp.put(544, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_9)));
		temp.put(545, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_1, NUMBER_1, NUMBER_0)));
		temp.put(546, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_1)));
		temp.put(547, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_2)));
		temp.put(548, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_3)));
		temp.put(549, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_4)));
		temp.put(550, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_5)));
		temp.put(551, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_6)));
		temp.put(552, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_7)));
		temp.put(553, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_8)));
		temp.put(554, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_9)));
		temp.put(555, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_2, NUMBER_1, NUMBER_0)));
		temp.put(556, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_1)));
		temp.put(557, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_2)));
		temp.put(558, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_3)));
		temp.put(559, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_4)));
		temp.put(560, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_5)));
		temp.put(561, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_6)));
		temp.put(562, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_7)));
		temp.put(563, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_8)));
		temp.put(564, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_9)));
		temp.put(565, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_3, NUMBER_1, NUMBER_0)));
		temp.put(566, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_1)));
		temp.put(567, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_2)));
		temp.put(568, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_3)));
		temp.put(569, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_4)));
		temp.put(570, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_5)));
		temp.put(571, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_6)));
		temp.put(572, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_7)));
		temp.put(573, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_8)));
		temp.put(574, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_9)));
		temp.put(575, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_4, NUMBER_1, NUMBER_0)));
		temp.put(576, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_1)));
		temp.put(577, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_2)));
		temp.put(578, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_3)));
		temp.put(579, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_4)));
		temp.put(580, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_5)));
		temp.put(581, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_6)));
		temp.put(582, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_7)));
		temp.put(583, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_8)));
		temp.put(584, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_9)));
		temp.put(585, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, TIME, joinNS(TIME, NUMBER_5, NUMBER_1, NUMBER_0)));
		temp.put(586, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, WEEKLY_PREMIUM));
		temp.put(587, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, MONTHLY_PREMIUM));
		temp.put(588, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, IRREGULAR + CARRY_FORWARD));
		
		String optionalFixedPath = join(MONTHLY_OPTIONAL_ITEM_NAME, OPTIONAL_ITEM_VALUE, VALUE);
		temp.put(589, joinNS(optionalFixedPath, NUMBER_1));
		temp.put(590, joinNS(optionalFixedPath, NUMBER_2));
		temp.put(591, joinNS(optionalFixedPath, NUMBER_3));
		temp.put(592, joinNS(optionalFixedPath, NUMBER_4));
		temp.put(593, joinNS(optionalFixedPath, NUMBER_5));
		temp.put(594, joinNS(optionalFixedPath, NUMBER_6));
		temp.put(595, joinNS(optionalFixedPath, NUMBER_7));
		temp.put(596, joinNS(optionalFixedPath, NUMBER_8));
		temp.put(597, joinNS(optionalFixedPath, NUMBER_9));
		temp.put(598, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0));
		temp.put(599, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1));
		temp.put(600, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2));
		temp.put(601, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3));
		temp.put(602, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4));
		temp.put(603, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5));
		temp.put(604, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6));
		temp.put(605, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7));
		temp.put(606, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8));
		temp.put(607, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9));
		temp.put(608, joinNS(optionalFixedPath, NUMBER_2, NUMBER_0));
		temp.put(609, joinNS(optionalFixedPath, NUMBER_2, NUMBER_1));
		temp.put(610, joinNS(optionalFixedPath, NUMBER_2, NUMBER_2));
		temp.put(611, joinNS(optionalFixedPath, NUMBER_2, NUMBER_3));
		temp.put(612, joinNS(optionalFixedPath, NUMBER_2, NUMBER_4));
		temp.put(613, joinNS(optionalFixedPath, NUMBER_2, NUMBER_5));
		temp.put(614, joinNS(optionalFixedPath, NUMBER_2, NUMBER_6));
		temp.put(615, joinNS(optionalFixedPath, NUMBER_2, NUMBER_7));
		temp.put(616, joinNS(optionalFixedPath, NUMBER_2, NUMBER_8));
		temp.put(617, joinNS(optionalFixedPath, NUMBER_2, NUMBER_9));
		temp.put(618, joinNS(optionalFixedPath, NUMBER_3, NUMBER_0));
		temp.put(619, joinNS(optionalFixedPath, NUMBER_3, NUMBER_1));
		temp.put(620, joinNS(optionalFixedPath, NUMBER_3, NUMBER_2));
		temp.put(621, joinNS(optionalFixedPath, NUMBER_3, NUMBER_3));
		temp.put(622, joinNS(optionalFixedPath, NUMBER_3, NUMBER_4));
		temp.put(623, joinNS(optionalFixedPath, NUMBER_3, NUMBER_5));
		temp.put(624, joinNS(optionalFixedPath, NUMBER_3, NUMBER_6));
		temp.put(625, joinNS(optionalFixedPath, NUMBER_3, NUMBER_7));
		temp.put(626, joinNS(optionalFixedPath, NUMBER_3, NUMBER_8));
		temp.put(627, joinNS(optionalFixedPath, NUMBER_3, NUMBER_9));
		temp.put(628, joinNS(optionalFixedPath, NUMBER_4, NUMBER_0));
		temp.put(629, joinNS(optionalFixedPath, NUMBER_4, NUMBER_1));
		temp.put(630, joinNS(optionalFixedPath, NUMBER_4, NUMBER_2));
		temp.put(631, joinNS(optionalFixedPath, NUMBER_4, NUMBER_3));
		temp.put(632, joinNS(optionalFixedPath, NUMBER_4, NUMBER_4));
		temp.put(633, joinNS(optionalFixedPath, NUMBER_4, NUMBER_5));
		temp.put(634, joinNS(optionalFixedPath, NUMBER_4, NUMBER_6));
		temp.put(635, joinNS(optionalFixedPath, NUMBER_4, NUMBER_7));
		temp.put(636, joinNS(optionalFixedPath, NUMBER_4, NUMBER_8));
		temp.put(637, joinNS(optionalFixedPath, NUMBER_4, NUMBER_9));
		temp.put(638, joinNS(optionalFixedPath, NUMBER_5, NUMBER_0));
		temp.put(639, joinNS(optionalFixedPath, NUMBER_5, NUMBER_1));
		temp.put(640, joinNS(optionalFixedPath, NUMBER_5, NUMBER_2));
		temp.put(641, joinNS(optionalFixedPath, NUMBER_5, NUMBER_3));
		temp.put(642, joinNS(optionalFixedPath, NUMBER_5, NUMBER_4));
		temp.put(643, joinNS(optionalFixedPath, NUMBER_5, NUMBER_5));
		temp.put(644, joinNS(optionalFixedPath, NUMBER_5, NUMBER_6));
		temp.put(645, joinNS(optionalFixedPath, NUMBER_5, NUMBER_7));
		temp.put(646, joinNS(optionalFixedPath, NUMBER_5, NUMBER_8));
		temp.put(647, joinNS(optionalFixedPath, NUMBER_5, NUMBER_9));
		temp.put(648, joinNS(optionalFixedPath, NUMBER_6, NUMBER_0));
		temp.put(649, joinNS(optionalFixedPath, NUMBER_6, NUMBER_1));
		temp.put(650, joinNS(optionalFixedPath, NUMBER_6, NUMBER_2));
		temp.put(651, joinNS(optionalFixedPath, NUMBER_6, NUMBER_3));
		temp.put(652, joinNS(optionalFixedPath, NUMBER_6, NUMBER_4));
		temp.put(653, joinNS(optionalFixedPath, NUMBER_6, NUMBER_5));
		temp.put(654, joinNS(optionalFixedPath, NUMBER_6, NUMBER_6));
		temp.put(655, joinNS(optionalFixedPath, NUMBER_6, NUMBER_7));
		temp.put(656, joinNS(optionalFixedPath, NUMBER_6, NUMBER_8));
		temp.put(657, joinNS(optionalFixedPath, NUMBER_6, NUMBER_9));
		temp.put(658, joinNS(optionalFixedPath, NUMBER_7, NUMBER_0));
		temp.put(659, joinNS(optionalFixedPath, NUMBER_7, NUMBER_1));
		temp.put(660, joinNS(optionalFixedPath, NUMBER_7, NUMBER_2));
		temp.put(661, joinNS(optionalFixedPath, NUMBER_7, NUMBER_3));
		temp.put(662, joinNS(optionalFixedPath, NUMBER_7, NUMBER_4));
		temp.put(663, joinNS(optionalFixedPath, NUMBER_7, NUMBER_5));
		temp.put(664, joinNS(optionalFixedPath, NUMBER_7, NUMBER_6));
		temp.put(665, joinNS(optionalFixedPath, NUMBER_7, NUMBER_7));
		temp.put(666, joinNS(optionalFixedPath, NUMBER_7, NUMBER_8));
		temp.put(667, joinNS(optionalFixedPath, NUMBER_7, NUMBER_9));
		temp.put(668, joinNS(optionalFixedPath, NUMBER_8, NUMBER_0));
		temp.put(669, joinNS(optionalFixedPath, NUMBER_8, NUMBER_1));
		temp.put(670, joinNS(optionalFixedPath, NUMBER_8, NUMBER_2));
		temp.put(671, joinNS(optionalFixedPath, NUMBER_8, NUMBER_3));
		temp.put(672, joinNS(optionalFixedPath, NUMBER_8, NUMBER_4));
		temp.put(673, joinNS(optionalFixedPath, NUMBER_8, NUMBER_5));
		temp.put(674, joinNS(optionalFixedPath, NUMBER_8, NUMBER_6));
		temp.put(675, joinNS(optionalFixedPath, NUMBER_8, NUMBER_7));
		temp.put(676, joinNS(optionalFixedPath, NUMBER_8, NUMBER_8));
		temp.put(677, joinNS(optionalFixedPath, NUMBER_8, NUMBER_9));
		temp.put(678, joinNS(optionalFixedPath, NUMBER_9, NUMBER_0));
		temp.put(679, joinNS(optionalFixedPath, NUMBER_9, NUMBER_1));
		temp.put(680, joinNS(optionalFixedPath, NUMBER_9, NUMBER_2));
		temp.put(681, joinNS(optionalFixedPath, NUMBER_9, NUMBER_3));
		temp.put(682, joinNS(optionalFixedPath, NUMBER_9, NUMBER_4));
		temp.put(683, joinNS(optionalFixedPath, NUMBER_9, NUMBER_5));
		temp.put(684, joinNS(optionalFixedPath, NUMBER_9, NUMBER_6));
		temp.put(685, joinNS(optionalFixedPath, NUMBER_9, NUMBER_7));
		temp.put(686, joinNS(optionalFixedPath, NUMBER_9, NUMBER_8));
		temp.put(687, joinNS(optionalFixedPath, NUMBER_9, NUMBER_9));
		temp.put(688, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_0));
		temp.put(689, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_1));
		temp.put(690, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_2));
		temp.put(691, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_3));
		temp.put(692, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_4));
		temp.put(693, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_5));
		temp.put(694, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_6));
		temp.put(695, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_7));
		temp.put(696, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_8));
		temp.put(697, joinNS(optionalFixedPath, NUMBER_1, NUMBER_0, NUMBER_9));
		temp.put(698, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_0));
		temp.put(699, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_1));
		temp.put(700, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_2));
		temp.put(701, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_3));
		temp.put(702, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_4));
		temp.put(703, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_5));
		temp.put(704, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_6));
		temp.put(705, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_7));
		temp.put(706, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_8));
		temp.put(707, joinNS(optionalFixedPath, NUMBER_1, NUMBER_1, NUMBER_9));
		temp.put(708, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_0));
		temp.put(709, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_1));
		temp.put(710, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_2));
		temp.put(711, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_3));
		temp.put(712, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_4));
		temp.put(713, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_5));
		temp.put(714, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_6));
		temp.put(715, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_7));
		temp.put(716, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_8));
		temp.put(717, joinNS(optionalFixedPath, NUMBER_1, NUMBER_2, NUMBER_9));
		temp.put(718, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_0));
		temp.put(719, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_1));
		temp.put(720, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_2));
		temp.put(721, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_3));
		temp.put(722, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_4));
		temp.put(723, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_5));
		temp.put(724, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_6));
		temp.put(725, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_7));
		temp.put(726, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_8));
		temp.put(727, joinNS(optionalFixedPath, NUMBER_1, NUMBER_3, NUMBER_9));
		temp.put(728, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_0));
		temp.put(729, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_1));
		temp.put(730, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_2));
		temp.put(731, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_3));
		temp.put(732, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_4));
		temp.put(733, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_5));
		temp.put(734, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_6));
		temp.put(735, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_7));
		temp.put(736, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_8));
		temp.put(737, joinNS(optionalFixedPath, NUMBER_1, NUMBER_4, NUMBER_9));
		temp.put(738, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_0));
		temp.put(739, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_1));
		temp.put(740, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_2));
		temp.put(741, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_3));
		temp.put(742, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_4));
		temp.put(743, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_5));
		temp.put(744, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_6));
		temp.put(745, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_7));
		temp.put(746, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_8));
		temp.put(747, joinNS(optionalFixedPath, NUMBER_1, NUMBER_5, NUMBER_9));
		temp.put(748, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_0));
		temp.put(749, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_1));
		temp.put(750, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_2));
		temp.put(751, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_3));
		temp.put(752, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_4));
		temp.put(753, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_5));
		temp.put(754, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_6));
		temp.put(755, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_7));
		temp.put(756, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_8));
		temp.put(757, joinNS(optionalFixedPath, NUMBER_1, NUMBER_6, NUMBER_9));
		temp.put(758, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_0));
		temp.put(759, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_1));
		temp.put(760, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_2));
		temp.put(761, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_3));
		temp.put(762, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_4));
		temp.put(763, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_5));
		temp.put(764, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_6));
		temp.put(765, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_7));
		temp.put(766, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_8));
		temp.put(767, joinNS(optionalFixedPath, NUMBER_1, NUMBER_7, NUMBER_9));
		temp.put(768, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_0));
		temp.put(769, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_1));
		temp.put(770, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_2));
		temp.put(771, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_3));
		temp.put(772, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_4));
		temp.put(773, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_5));
		temp.put(774, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_6));
		temp.put(775, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_7));
		temp.put(776, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_8));
		temp.put(777, joinNS(optionalFixedPath, NUMBER_1, NUMBER_8, NUMBER_9));
		temp.put(778, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_0));
		temp.put(779, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_1));
		temp.put(780, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_2));
		temp.put(781, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_3));
		temp.put(782, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_4));
		temp.put(783, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_5));
		temp.put(784, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_6));
		temp.put(785, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_7));
		temp.put(786, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_8));
		temp.put(787, joinNS(optionalFixedPath, NUMBER_1, NUMBER_9, NUMBER_9));
		temp.put(788, joinNS(optionalFixedPath, NUMBER_2, NUMBER_0, NUMBER_0));
		
				
		temp.put(830, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, RETENTION, USAGE, DAYS));
		temp.put(831, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, RETENTION, USAGE, GRANT + AFTER));
		temp.put(832, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, RETENTION, REMAIN, DAYS, DAYS));
//		temp.put(833, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, RETENTION, GRANT + AFTER, DAYS));
		temp.put(834, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, NOT_DIGESTION));
		temp.put(835, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, GRANT + INFO));
		temp.put(836, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, REAL + RETENTION, USAGE, DAYS));
		temp.put(837, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, REAL + RETENTION, USAGE, GRANT + AFTER));
		temp.put(838, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, REAL + RETENTION, REMAIN, DAYS, DAYS));
//		temp.put(839, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, REAL + RETENTION, GRANT + AFTER, DAYS));
		
		temp.put(1790, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, RETENTION, REMAIN, AFTER, DAYS));
		temp.put(1791, join(MONTHLY_RESERVE_LEAVING_REMAIN_NAME, REAL + RETENTION, REMAIN, AFTER, DAYS));
		
	}

	private static void getMonthlyKey2(Map<Integer, String> temp) {
		temp.put(1260, join(MONTHLY_OFF_REMAIN_NAME, OCCURRENCE, DAYS));
		temp.put(1261, join(MONTHLY_OFF_REMAIN_NAME, OCCURRENCE, TIME));
		temp.put(1262, join(MONTHLY_OFF_REMAIN_NAME, USAGE, DAYS));
		temp.put(1263, join(MONTHLY_OFF_REMAIN_NAME, USAGE, TIME));
		temp.put(1264, join(MONTHLY_OFF_REMAIN_NAME, REMAIN, DAYS));
		temp.put(1265, join(MONTHLY_OFF_REMAIN_NAME, REMAIN, TIME));
		temp.put(1266, join(MONTHLY_OFF_REMAIN_NAME, CARRY_FORWARD, DAYS));
		temp.put(1267, join(MONTHLY_OFF_REMAIN_NAME, CARRY_FORWARD, TIME));
		temp.put(1268, join(MONTHLY_OFF_REMAIN_NAME, NOT_DIGESTION, DAYS));
		temp.put(1269, join(MONTHLY_OFF_REMAIN_NAME, NOT_DIGESTION, TIME));
		
		temp.put(1270, join(MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, OCCURRENCE));
		temp.put(1271, join(MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, USAGE));
		temp.put(1272, join(MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, REMAIN));
		temp.put(1273, join(MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, NOT_DIGESTION));
		temp.put(1274, join(MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, CARRY_FORWARD));
		
		temp.put(1275, join(MONTHLY_CHILD_CARE_HD_REMAIN_NAME, USAGE + DAYS));
		temp.put(1276, join(MONTHLY_CHILD_CARE_HD_REMAIN_NAME, USAGE + DAYS + AFTER));
		temp.put(1277, join(MONTHLY_CHILD_CARE_HD_REMAIN_NAME, USAGE + TIME));
		temp.put(1278, join(MONTHLY_CHILD_CARE_HD_REMAIN_NAME, USAGE + TIME + AFTER));
		
		temp.put(1279, join(MONTHLY_CARE_HD_REMAIN_NAME, USAGE + DAYS));
		temp.put(1280, join(MONTHLY_CARE_HD_REMAIN_NAME, USAGE + DAYS + AFTER));
		temp.put(1281, join(MONTHLY_CARE_HD_REMAIN_NAME, USAGE + TIME));
		temp.put(1282, join(MONTHLY_CARE_HD_REMAIN_NAME, USAGE + TIME + AFTER));
		
		temp.put(1283, join(MONTHLY_REMARKS_NAME, FAKED, REMARK + NUMBER_1));
		temp.put(1284, join(MONTHLY_REMARKS_NAME, FAKED, REMARK + NUMBER_2));
		temp.put(1285, join(MONTHLY_REMARKS_NAME, FAKED, REMARK + NUMBER_3));
		temp.put(1286, join(MONTHLY_REMARKS_NAME, FAKED, REMARK + NUMBER_4));
		temp.put(1287, join(MONTHLY_REMARKS_NAME, FAKED, REMARK + NUMBER_5));
		
		temp.put(1288, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_1));
		temp.put(1289, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_2));
		temp.put(1290, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_3));
		temp.put(1291, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_4));
		temp.put(1292, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_5));
		temp.put(1293, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_6));
		temp.put(1294, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_7));
		temp.put(1295, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_8));
		temp.put(1296, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, TIME + NUMBER_9));
		temp.put(1297, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(1298, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_1)));
		temp.put(1299, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_2)));
		temp.put(1300, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_3)));
		temp.put(1301, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_4)));
		temp.put(1302, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_5)));
		temp.put(1303, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_6)));
		temp.put(1304, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_7)));
		temp.put(1305, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_8)));
		temp.put(1306, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_1, NUMBER_9)));
		temp.put(1307, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_0)));
		temp.put(1308, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_1)));
		temp.put(1309, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_2)));
		temp.put(1310, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_3)));
		temp.put(1311, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_4)));
		temp.put(1312, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_5)));
		temp.put(1313, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_6)));
		temp.put(1314, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_7)));
		temp.put(1315, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_8)));
		temp.put(1316, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_2, NUMBER_9)));
		temp.put(1317, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, DAYS, joinNS(TIME, NUMBER_3, NUMBER_0)));
		temp.put(1318, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, ABSENCE, TOTAL + TIME));
		
		temp.put(1319, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_1));
		temp.put(1320, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_2));
		temp.put(1321, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_3));
		temp.put(1322, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_4));
		temp.put(1323, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_5));
		temp.put(1324, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_6));
		temp.put(1325, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_7));
		temp.put(1326, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_8));
		temp.put(1327, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, TIME + NUMBER_9));
		temp.put(1328, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(1329, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_1)));
		temp.put(1330, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_2)));
		temp.put(1331, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_3)));
		temp.put(1332, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_4)));
		temp.put(1333, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_5)));
		temp.put(1334, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_6)));
		temp.put(1335, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_7)));
		temp.put(1336, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_8)));
		temp.put(1337, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_1, NUMBER_9)));
		temp.put(1338, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_0)));
		temp.put(1339, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_1)));
		temp.put(1340, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_2)));
		temp.put(1341, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_3)));
		temp.put(1342, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_4)));
		temp.put(1343, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_5)));
		temp.put(1344, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_6)));
		temp.put(1345, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_7)));
		temp.put(1346, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_8)));
		temp.put(1347, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_2, NUMBER_9)));
		temp.put(1348, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, DAYS, joinNS(TIME, NUMBER_3, NUMBER_0)));
		temp.put(1349, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, DAYS, SPECIAL + HOLIDAY, TOTAL + TIME));
		
		temp.put(1863, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, OTHER, TIME));
		temp.put(1864, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, OTHER, AMOUNT));
		temp.put(1865, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1)));
		temp.put(1866, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1)));
		temp.put(1867, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2)));
		temp.put(1868, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2)));
		temp.put(1869, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3)));
		temp.put(1870, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3)));
		temp.put(1871, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4)));
		temp.put(1872, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4)));
		temp.put(1873, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5)));
		temp.put(1874, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5)));
		temp.put(1875, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6)));
		temp.put(1876, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6)));
		temp.put(1877, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7)));
		temp.put(1878, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7)));
		temp.put(1879, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8)));
		temp.put(1880, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8)));
		temp.put(1881, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9)));
		temp.put(1882, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9)));
		temp.put(1883, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_0)));
		temp.put(1884, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_0)));
		temp.put(1885, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_1)));
		temp.put(1886, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_1)));
		temp.put(1887, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_2)));
		temp.put(1888, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_2)));
		temp.put(1889, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_3)));
		temp.put(1890, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_3)));
		temp.put(1891, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_4)));
		temp.put(1892, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_4)));
		temp.put(1893, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_5)));
		temp.put(1894, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_5)));
		temp.put(1895, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_6)));
		temp.put(1896, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_6)));
		temp.put(1897, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_7)));
		temp.put(1898, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_7)));
		temp.put(1899, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_8)));
		temp.put(1900, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_8)));
		temp.put(1901, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_1, NUMBER_9)));
		temp.put(1902, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_1, NUMBER_9)));
		temp.put(1903, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_0)));
		temp.put(1904, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_0)));
		temp.put(1905, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_1)));
		temp.put(1906, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_1)));
		temp.put(1907, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_2)));
		temp.put(1908, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_2)));
		temp.put(1909, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_3)));
		temp.put(1910, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_3)));
		temp.put(1911, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_4)));
		temp.put(1912, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_4)));
		temp.put(1913, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_5)));
		temp.put(1914, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_5)));
		temp.put(1915, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_6)));
		temp.put(1916, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_6)));
		temp.put(1917, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_7)));
		temp.put(1918, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_7)));
		temp.put(1919, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_8)));
		temp.put(1920, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_8)));
		temp.put(1921, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_2, NUMBER_9)));
		temp.put(1922, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_2, NUMBER_9)));
		temp.put(1923, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_0)));
		temp.put(1924, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_0)));
		temp.put(1925, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_1)));
		temp.put(1926, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_1)));
		temp.put(1927, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_2)));
		temp.put(1928, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_2)));
		temp.put(1929, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_3)));
		temp.put(1930, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_3)));
		temp.put(1931, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_4)));
		temp.put(1932, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_4)));
		temp.put(1933, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_5)));
		temp.put(1934, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_5)));
		temp.put(1935, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_6)));
		temp.put(1936, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_6)));
		temp.put(1937, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_7)));
		temp.put(1938, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_7)));
		temp.put(1939, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_8)));
		temp.put(1940, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_8)));
		temp.put(1941, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_3, NUMBER_9)));
		temp.put(1942, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_3, NUMBER_9)));
		temp.put(1943, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_0)));
		temp.put(1944, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_0)));
		temp.put(1945, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_1)));
		temp.put(1946, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_1)));
		temp.put(1947, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_2)));
		temp.put(1948, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_2)));
		temp.put(1949, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_3)));
		temp.put(1950, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_3)));
		temp.put(1951, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_4)));
		temp.put(1952, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_4)));
		temp.put(1953, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_5)));
		temp.put(1954, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_5)));
		temp.put(1955, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_6)));
		temp.put(1956, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_6)));
		temp.put(1957, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_7)));
		temp.put(1958, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_7)));
		temp.put(1959, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_8)));
		temp.put(1960, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_8)));
		temp.put(1961, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_4, NUMBER_9)));
		temp.put(1962, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_4, NUMBER_9)));
		temp.put(1963, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_0)));
		temp.put(1964, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_0)));
		temp.put(1965, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_1)));
		temp.put(1966, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_1)));
		temp.put(1967, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_2)));
		temp.put(1968, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_2)));
		temp.put(1969, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_3)));
		temp.put(1970, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_3)));
		temp.put(1971, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_4)));
		temp.put(1972, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_4)));
		temp.put(1973, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_5)));
		temp.put(1974, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_5)));
		temp.put(1975, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_6)));
		temp.put(1976, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_6)));
		temp.put(1977, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_7)));
		temp.put(1978, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_7)));
		temp.put(1979, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_8)));
		temp.put(1980, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_8)));
		temp.put(1981, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_5, NUMBER_9)));
		temp.put(1982, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_5, NUMBER_9)));
		temp.put(1983, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_0)));
		temp.put(1984, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_0)));
		temp.put(1985, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_1)));
		temp.put(1986, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_1)));
		temp.put(1987, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_2)));
		temp.put(1988, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_2)));
		temp.put(1989, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_3)));
		temp.put(1990, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_3)));
		temp.put(1991, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_4)));
		temp.put(1992, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_4)));
		temp.put(1993, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_5)));
		temp.put(1994, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_5)));
		temp.put(1995, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_6)));
		temp.put(1996, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_6)));
		temp.put(1997, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_7)));
		temp.put(1998, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_7)));
		temp.put(1999, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_8)));
		temp.put(2000, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_8)));
		temp.put(2001, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_6, NUMBER_9)));
		temp.put(2002, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_6, NUMBER_9)));
		temp.put(2003, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_0)));
		temp.put(2004, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_0)));
		temp.put(2005, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_1)));
		temp.put(2006, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_1)));
		temp.put(2007, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_2)));
		temp.put(2008, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_2)));
		temp.put(2009, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_3)));
		temp.put(2010, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_3)));
		temp.put(2011, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_4)));
		temp.put(2012, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_4)));
		temp.put(2013, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_5)));
		temp.put(2014, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_5)));
		temp.put(2015, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_6)));
		temp.put(2016, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_6)));
		temp.put(2017, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_7)));
		temp.put(2018, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_7)));
		temp.put(2019, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_8)));
		temp.put(2020, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_8)));
		temp.put(2021, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_7, NUMBER_9)));
		temp.put(2022, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_7, NUMBER_9)));
		temp.put(2023, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_0)));
		temp.put(2024, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_0)));
		temp.put(2025, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_1)));
		temp.put(2026, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_1)));
		temp.put(2027, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_2)));
		temp.put(2028, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_2)));
		temp.put(2029, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_3)));
		temp.put(2030, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_3)));
		temp.put(2031, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_4)));
		temp.put(2032, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_4)));
		temp.put(2033, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_5)));
		temp.put(2034, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_5)));
		temp.put(2035, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_6)));
		temp.put(2036, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_6)));
		temp.put(2037, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_7)));
		temp.put(2038, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_7)));
		temp.put(2039, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_8)));
		temp.put(2040, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_8)));
		temp.put(2041, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_8, NUMBER_9)));
		temp.put(2042, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_8, NUMBER_9)));
		temp.put(2043, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_0)));
		temp.put(2044, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_0)));
		temp.put(2045, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_1)));
		temp.put(2046, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_1)));
		temp.put(2047, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_2)));
		temp.put(2048, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_2)));
		temp.put(2049, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_3)));
		temp.put(2050, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_3)));
		temp.put(2051, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_4)));
		temp.put(2052, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_4)));
		temp.put(2053, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_5)));
		temp.put(2054, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_5)));
		temp.put(2055, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_6)));
		temp.put(2056, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_6)));
		temp.put(2057, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_7)));
		temp.put(2058, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_7)));
		temp.put(2059, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_8)));
		temp.put(2060, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_8)));
		temp.put(2061, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(TIME, NUMBER_9, NUMBER_9)));
		temp.put(2062, join(MONTHLY_ATTENDANCE_TIME_NAME, OUEN, FRAME, DETAIL, joinNS(AMOUNT, NUMBER_9, NUMBER_9)));
		
		temp.put(2063, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, TOPPAGE, OVERTIME));
		temp.put(2064, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, TOPPAGE, HOLIDAY_WORK));
		temp.put(2065, join(MONTHLY_ATTENDANCE_TIME_NAME, VERTICAL_TOTAL, TIME, TOPPAGE, FLEX));
		
		temp.put(2066, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, SUPER_60 + TRANSFER));
		temp.put(2067, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, SUPER_60 + GRANT));
		temp.put(2068, join(MONTHLY_ATTENDANCE_TIME_NAME, EXCESS, SUPER_60 + CALC));

	}

	private static void getMonthlyKeySpecialHoliday(Map<Integer, String> temp) {
		
		for(int i = 0; i< 20; i++) {
			temp.put(840+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, USAGE, TOTAL, DAYS , DAYS + (i+1)));
			temp.put(841+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, USAGE, GRANT+AFTER, DAYS , DAYS + (i+1)));
			temp.put(845+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, REMAIN, TOTAL, DAYS + (i+1)));
			temp.put(846+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, REMAIN, GRANT+AFTER, DAYS + (i+1)));
			temp.put(849+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME,  FAKED, NOT_DIGESTION, DAYS + (i+1)));
			temp.put(851+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME,  FAKED, GRANT + DAYS + (i+1)));
			temp.put(852+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, USAGE, TOTAL, DAYS , DAYS + (i+1)));
			temp.put(853+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, USAGE, GRANT+AFTER, DAYS , DAYS + (i+1)));
			temp.put(857+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, REMAIN, TOTAL, DAYS + (i+1)));
			temp.put(858+ i*21, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, REMAIN, GRANT+AFTER, DAYS + (i+1)));
		}
	
		for(int i = 0; i< 20; i++) {
			temp.put(1446+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, USAGE, TOTAL, TIME , TIME + (i+1)));
			temp.put(1447+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, USAGE, GRANT+AFTER, TIME , TIME + (i+1)));
			temp.put(1448+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, USAGE, COUNT + (i+1)));
			temp.put(1449+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, REMAIN, TOTAL, TIME , TIME + (i+1)));
			temp.put(1450+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, SPECIAL_HOLIDAY, REMAIN, GRANT+AFTER, TIME , TIME + (i+1)));
			temp.put(1451+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME,  FAKED, NOT_DIGESTION, TIME + (i+1)));
			temp.put(1452+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, USAGE, TOTAL, TIME , TIME + (i+1)));
			temp.put(1453+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, USAGE, GRANT+AFTER, TIME , TIME + (i+1)));
			temp.put(1454+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, USAGE, COUNT + (i+1)));
			temp.put(1455+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, REMAIN, TOTAL, TIME , TIME + (i+1)));
			temp.put(1456+ i*11, join(MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, FAKED, REAL + SPECIAL_HOLIDAY, REMAIN, GRANT+AFTER, TIME , TIME + (i+1)));
			
		}
		
	}
	
	private static void getMonthlyKeyAnnual(Map<Integer, String> temp) {
		temp.put(789, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, USAGE + NUMBER, TOTAL, DAYS, DAYS));
		temp.put(790, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, USAGE + NUMBER, GRANT + AFTER, DAYS, DAYS));
		temp.put(794, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, REMAIN, TOTAL, DAYS));
		temp.put(798, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, NOT_DIGESTION, DAYS));
		temp.put(799, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, NOT_DIGESTION, TIME));
		temp.put(800, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, USAGE + NUMBER, TOTAL, DAYS, DAYS));
		temp.put(801, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, USAGE + NUMBER, GRANT + AFTER, DAYS, DAYS));
		temp.put(805, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, REMAIN, TOTAL, DAYS));
		temp.put(809, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE + GRANT, DAYS));

		temp.put(1424, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, USAGE + NUMBER, TOTAL, TIME, TIME));
		temp.put(1425, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, USAGE + NUMBER, GRANT + AFTER, TIME, TIME));
		temp.put(1426, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, USAGE + NUMBER, USAGE + COUNT));
		temp.put(1427, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, REMAIN, TOTAL, TIME));
		temp.put(1428, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, REMAIN, AFTER, TIME));
		
		temp.put(1429, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, USAGE + NUMBER, TOTAL, TIME, TIME));
		temp.put(1430, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, USAGE + NUMBER, GRANT + AFTER, TIME, TIME));
		temp.put(1431, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, USAGE + NUMBER, USAGE + COUNT));
		temp.put(1432, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, REMAIN, TOTAL, TIME));
		temp.put(1433, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + ANNUNAL_LEAVE, REMAIN, AFTER, TIME));
		
		temp.put(1434, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, HALF_DAY + ANNUNAL_LEAVE, USAGE, COUNT));
		temp.put(1435, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, HALF_DAY + ANNUNAL_LEAVE, USAGE, GRANT + AFTER));
		temp.put(1436, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, HALF_DAY + ANNUNAL_LEAVE, REMAIN, COUNT));
		temp.put(1437, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, HALF_DAY + ANNUNAL_LEAVE, REMAIN, GRANT + AFTER));
		
		temp.put(1438, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + HALF_DAY + ANNUNAL_LEAVE, USAGE, COUNT));
		temp.put(1439, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + HALF_DAY + ANNUNAL_LEAVE, USAGE, GRANT + AFTER));
		temp.put(1440, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + HALF_DAY + ANNUNAL_LEAVE, REMAIN, COUNT));
		temp.put(1441, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL + HALF_DAY + ANNUNAL_LEAVE, REMAIN, GRANT + AFTER));
		
		temp.put(1442, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, UPPER_LIMIT + REMAIN, TIME));
		temp.put(1443, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, UPPER_LIMIT + REMAIN, GRANT + AFTER));
		temp.put(1444, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  REAL + UPPER_LIMIT + REMAIN, TIME));
		temp.put(1445, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  REAL + UPPER_LIMIT + REMAIN, GRANT + AFTER));
		
		temp.put(1780, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ANNUNAL_LEAVE, REMAIN, AFTER, DAYS));
		temp.put(1781, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  REAL +ANNUNAL_LEAVE, REMAIN, AFTER, DAYS));
		
		temp.put(1782, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ANNUNAL_LEAVE + GRANT, WITHIN_STATUTORY));
		temp.put(1783, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ANNUNAL_LEAVE + GRANT, LABOR));
		temp.put(1784, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ANNUNAL_LEAVE + GRANT, DEDUCTION));
		temp.put(1785, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ANNUNAL_LEAVE + GRANT, DEDUCTION + AFTER));
		temp.put(1786, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ANNUNAL_LEAVE + GRANT, ATTENDANCE + RATE));
		
		temp.put(1787, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ATTENDANCE + RATE, WITHIN_STATUTORY));
		temp.put(1788, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ATTENDANCE + RATE, LABOR));
		temp.put(1789, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME,  ATTENDANCE + RATE, DEDUCTION));
		
		temp.put(1861, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, ANNUNAL_LEAVE, USAGE + NUMBER, USAGE + DAYS));
		temp.put(1862, join(MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, REAL +ANNUNAL_LEAVE, USAGE + NUMBER, USAGE + DAYS));
		
	}
	
	public static <V> Map<String, List<V>> groupItemByDomain(Collection<V> itemIds, Function<V, 
			Integer> getId, boolean monthly) {
		Map<String, List<V>> groups = new HashMap<>();
		
		Map<Integer, String> container = monthly ? MONTHLY_ITEM_ID_CONTAINER : DAY_ITEM_ID_CONTAINER;
		
		itemIds.stream().forEach(i -> {
			String itemPath = container.get(getId.apply(i));
			if (itemPath != null) {
				String[] group = itemPath.split(Pattern.quote(DEFAULT_SEPERATOR));
				if (groups.get(group[0]) == null) {
					List<V> ids = new ArrayList<>();
					ids.add(i);
					groups.put(group[0], ids);
				} else {
					groups.get(group[0]).add(i);
				}
			}
		});
		
		return groups;
	}
	
	public static List<Integer> getItemIdByDailyDomains(DailyDomainGroup... domains){
		return Arrays.stream(domains).map(e -> {
			return DAY_ITEM_ID_CONTAINER.entrySet().stream()
										.filter(en -> en.getValue().indexOf(e.name) == 0)
										.map(en -> en.getKey()).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static List<Integer> getItemIdByDailyDomains(Collection<DailyDomainGroup> domains){
		return domains.stream().map(e -> {
			return DAY_ITEM_ID_CONTAINER.entrySet().stream()
										.filter(en -> en.getValue().indexOf(e.name) == 0)
										.map(en -> en.getKey()).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static List<Integer> getItemIdByDailyDomains(Collection<DailyDomainGroup> domains, 
			BiFunction<DailyDomainGroup, String, Boolean> customCondition){
		return domains.stream().map(e -> {
			return DAY_ITEM_ID_CONTAINER.entrySet().stream()
										.filter(en -> en.getValue().indexOf(e.name) == 0)
										.filter(en -> customCondition.apply(e, en.getValue()))
										.map(en -> en.getKey()).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static List<Integer> getItemIdByDailyDomains(DailyDomainGroup domain, 
			BiFunction<DailyDomainGroup, String, Boolean> customCondition){
		
		return DAY_ITEM_ID_CONTAINER.entrySet().stream()
									.filter(en -> en.getValue().indexOf(domain.name) == 0)
									.filter(en -> customCondition.apply(domain, en.getValue()))
									.map(en -> en.getKey()).collect(Collectors.toList());
	}
	
	public static List<Integer> getItemIdByMonthlyDomains(MonthlyDomainGroup... domains){
		return Arrays.stream(domains).map(e -> {
			return MONTHLY_ITEM_ID_CONTAINER.entrySet().stream()
											.filter(en -> en.getValue().indexOf(e.name) == 0)
											.map(en -> en.getKey()).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static List<Integer> getItemIdByMonthlyDomains(Collection<MonthlyDomainGroup> domains){
		return domains.stream().map(e -> {
			return MONTHLY_ITEM_ID_CONTAINER.entrySet().stream()
											.filter(en -> en.getValue().indexOf(e.name) == 0)
											.map(en -> en.getKey()).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static boolean isHaveOptionalItems(Collection<ItemValue> items) {
		return toFilterStream(items).findFirst().isPresent();
	}
	
	public static List<ItemValue> filterOptionalItems(Collection<ItemValue> items) {
		return toFilterStream(items).collect(Collectors.toList());
	}
	
	public static Map<ItemValue, Integer> mapOptionalItemsToNos(Collection<ItemValue> items) {
		return toFilterStream(items).collect(Collectors.toMap(i -> i, i -> {
			return Integer.parseInt(i.path().replace(i.path().replaceAll(DEFAULT_NUMBER_REGEX, EMPTY_STRING), EMPTY_STRING));
		}));
	}
	
	public static Map<Integer, List<ItemValue>> mapWorkNoItemsValue(Collection<ItemValue> items) {
		Map<Integer, List<ItemValue>> rs = items.stream().collect(Collectors.groupingBy(i -> Integer.parseInt(i.path().replace(i.path().replaceAll(DEFAULT_NUMBER_REGEX, EMPTY_STRING), EMPTY_STRING))));
		return rs;
	}
	
	public static Map<Integer, Integer> mapOptionalItemIdsToNos(Collection<ItemValue> items) {
		return toFilterStream(items).collect(Collectors.toMap(i -> i.getItemId(), i -> {
			return Integer.parseInt(i.path().replace(i.path().replaceAll(DEFAULT_NUMBER_REGEX, EMPTY_STRING), EMPTY_STRING));
		}));
	}
	
	public static Map<Integer, Integer> mapOptionalItemIdsToNos() {
		return mapDailyOptionalItemIdsToNos();
	}
	
	public static Map<Integer, Integer> mapOptionalItemIdsToNos(AttendanceItemType type) {
		if(type == AttendanceItemType.MONTHLY_ITEM){
			return mapMonthlyOptionalItemIdsToNos();
		}
		return mapDailyOptionalItemIdsToNos();
	}
	
	private static Map<Integer, Integer> mapDailyOptionalItemIdsToNos() {
		return DAY_ITEM_ID_CONTAINER.entrySet().stream()
				.filter(en -> en.getValue().indexOf(DailyDomainGroup.OPTIONAL_ITEM.name) == 0)
				.collect(Collectors.toMap(i -> i.getKey(), i -> {
			return Integer.parseInt(i.getValue().replace(i.getValue().replaceAll(DEFAULT_NUMBER_REGEX, EMPTY_STRING), EMPTY_STRING));
		}));
	}
	
	private static Map<Integer, Integer> mapMonthlyOptionalItemIdsToNos() {
		return MONTHLY_ITEM_ID_CONTAINER.entrySet().stream()
				.filter(en -> en.getValue().indexOf(MonthlyDomainGroup.OPTIONAL_ITEM.name) == 0)
				.collect(Collectors.toMap(i -> i.getKey(), i -> {
			return Integer.parseInt(i.getValue().replace(i.getValue().replaceAll(DEFAULT_NUMBER_REGEX, EMPTY_STRING), EMPTY_STRING));
		}));
	}
	
	public static Map<ItemValue, Integer> mapOptionalItemsFromIdToNos(Collection<Integer> items, AttendanceItemType type) {
		return mapOptionalItemsToNos(getIds(items, type));
	}
	
	public static Map<Integer, Integer> optionalItemIdsToNos(Collection<Integer> items, AttendanceItemType type) {
		return mapOptionalItemIdsToNos(getIds(items, type));
	}
	
	public static String getPath(int key, AttendanceItemType type) {
		if (type == AttendanceItemType.MONTHLY_ITEM) {
			return MONTHLY_ITEM_ID_CONTAINER.get(key);
		}
		return DAY_ITEM_ID_CONTAINER.get(key);
	}

	public static Integer getEnumValue(String key) {
		return ENUM_CONTAINER.get(key);
	}

	public static List<ItemValue> getIds(Collection<Integer> values, AttendanceItemType type) {
		return getIdMapStream(values, type).collect(Collectors.toList());
	}

	public static List<ItemValue> getIds(AttendanceItemType type) {
		return getFullPair(type).collect(Collectors.toList());
	}

	public static Stream<ItemValue> getIdMapStream(Collection<Integer> values, AttendanceItemType type) {
		if (values == null || values.isEmpty()) {
			return getFullPair(type);
		}
		Map<Integer, String> source = type == AttendanceItemType.MONTHLY_ITEM ? MONTHLY_ITEM_ID_CONTAINER
				: DAY_ITEM_ID_CONTAINER;

		return values.stream().filter(c -> source.get(c) != null).map(c -> ItemValue.build(source.get(c), c));
	}

	public static Stream<ItemValue> getFullPair(AttendanceItemType type) {
		if (type == AttendanceItemType.MONTHLY_ITEM) {
			return MONTHLY_ITEM_ID_CONTAINER.entrySet().stream().map(c -> ItemValue.build(c.getValue(), c.getKey()));
		}
		return DAY_ITEM_ID_CONTAINER.entrySet().stream().map(c -> ItemValue.build(c.getValue(), c.getKey()));
	}

	private static Stream<ItemValue> toFilterStream(Collection<ItemValue> items) {
		return items.stream().filter(i -> isOptionalItem(i));
	}

	public static boolean isOptionalItem(ItemValue i) {
		return DAY_ITEM_ID_CONTAINER.get(i.getItemId()).contains(OPTIONAL_ITEM_VALUE);
	}

	private static String join(String... arrays) {
		return StringUtils.join(arrays, DEFAULT_SEPERATOR);
	}
	
	private static String joinNS(String... arrays) {
		return StringUtils.join(arrays);
	}
}
