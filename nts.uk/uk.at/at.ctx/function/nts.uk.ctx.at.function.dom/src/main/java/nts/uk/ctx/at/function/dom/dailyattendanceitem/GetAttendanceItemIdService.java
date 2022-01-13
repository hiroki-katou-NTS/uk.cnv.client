package nts.uk.ctx.at.function.dom.dailyattendanceitem;

import java.util.ArrayList;
import java.util.List;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤怠項目.日次の勤怠項目.勤怠項目IDを取得する
 * DS: 勤怠項目IDを取得する
 * 
 * @author tutt
 *
 */
public class GetAttendanceItemIdService {

	public static List<Integer> getAttendanceItemIds(List<SupportFrameNo> supportFrameNoLst) {
		List<Integer> attendanceItemIds = new ArrayList<>();

		for (SupportFrameNo no : supportFrameNoLst) {

			switch (no.v()) {
			case 1:
				addIdtoList(921, 930, attendanceItemIds);
				addIdtoList(1248, 1249, attendanceItemIds);
				addIdtoList(1268, 1269, attendanceItemIds);
				addIdtoList(1305, 1348, attendanceItemIds);
				attendanceItemIds.add(2191);
				attendanceItemIds.add(2211);
				attendanceItemIds.add(2231);
				addIdtoList(2251, 2270, attendanceItemIds);
				break;

			case 2:
				addIdtoList(931, 940, attendanceItemIds);
				addIdtoList(1250, 1251, attendanceItemIds);
				addIdtoList(1270, 1271, attendanceItemIds);
				addIdtoList(1349, 1392, attendanceItemIds);
				attendanceItemIds.add(2192);
				attendanceItemIds.add(2212);
				attendanceItemIds.add(2232);
				addIdtoList(2271, 2290, attendanceItemIds);
				break;

			case 3:
				addIdtoList(941, 950, attendanceItemIds);
				addIdtoList(1252, 1253, attendanceItemIds);
				addIdtoList(1272, 1273, attendanceItemIds);
				addIdtoList(1393, 1436, attendanceItemIds);
				attendanceItemIds.add(2193);
				attendanceItemIds.add(2213);
				attendanceItemIds.add(2233);
				addIdtoList(2291, 2310, attendanceItemIds);
				break;

			case 4:
				addIdtoList(951, 960, attendanceItemIds);
				addIdtoList(1254, 1255, attendanceItemIds);
				addIdtoList(1274, 1275, attendanceItemIds);
				addIdtoList(1437, 1480, attendanceItemIds);
				attendanceItemIds.add(2194);
				attendanceItemIds.add(2214);
				attendanceItemIds.add(2234);
				addIdtoList(2311, 2330, attendanceItemIds);
				break;

			case 5:
				addIdtoList(961, 970, attendanceItemIds);
				addIdtoList(1256, 1257, attendanceItemIds);
				addIdtoList(1276, 1277, attendanceItemIds);
				addIdtoList(1481, 1524, attendanceItemIds);
				attendanceItemIds.add(2195);
				attendanceItemIds.add(2215);
				attendanceItemIds.add(2235);
				addIdtoList(2331, 2350, attendanceItemIds);
				break;

			case 6:
				addIdtoList(971, 980, attendanceItemIds);
				addIdtoList(1258, 1259, attendanceItemIds);
				addIdtoList(1278, 1279, attendanceItemIds);
				addIdtoList(1525, 1568, attendanceItemIds);
				attendanceItemIds.add(2196);
				attendanceItemIds.add(2216);
				attendanceItemIds.add(2236);
				addIdtoList(2351, 2370, attendanceItemIds);
				break;

			case 7:
				addIdtoList(981, 990, attendanceItemIds);
				addIdtoList(1260, 1261, attendanceItemIds);
				addIdtoList(1280, 1281, attendanceItemIds);
				addIdtoList(1569, 1612, attendanceItemIds);
				attendanceItemIds.add(2197);
				attendanceItemIds.add(2217);
				attendanceItemIds.add(2237);
				addIdtoList(2371, 2390, attendanceItemIds);
				break;

			case 8:
				addIdtoList(991, 1000, attendanceItemIds);
				addIdtoList(1262, 1263, attendanceItemIds);
				addIdtoList(1282, 1283, attendanceItemIds);
				addIdtoList(1613, 1656, attendanceItemIds);
				attendanceItemIds.add(2198);
				attendanceItemIds.add(2218);
				attendanceItemIds.add(2238);
				addIdtoList(2391, 2410, attendanceItemIds);
				break;

			case 9:
				addIdtoList(1001, 1010, attendanceItemIds);
				addIdtoList(1264, 1265, attendanceItemIds);
				addIdtoList(1284, 1285, attendanceItemIds);
				addIdtoList(1657, 1700, attendanceItemIds);
				attendanceItemIds.add(2199);
				attendanceItemIds.add(2219);
				attendanceItemIds.add(2239);
				addIdtoList(2411, 2430, attendanceItemIds);
				break;

			case 10:
				addIdtoList(1011, 1020, attendanceItemIds);
				addIdtoList(1266, 1267, attendanceItemIds);
				addIdtoList(1286, 1287, attendanceItemIds);
				addIdtoList(1701, 1744, attendanceItemIds);
				attendanceItemIds.add(2200);
				attendanceItemIds.add(2220);
				attendanceItemIds.add(2240);
				addIdtoList(2431, 2450, attendanceItemIds);
				break;

			case 11:
				addIdtoList(1021, 1030, attendanceItemIds);
				addIdtoList(2691, 2692, attendanceItemIds);
				addIdtoList(2711, 2712, attendanceItemIds);
				addIdtoList(1745, 1788, attendanceItemIds);
				attendanceItemIds.add(2201);
				attendanceItemIds.add(2221);
				attendanceItemIds.add(2241);
				addIdtoList(2451, 2470, attendanceItemIds);
				break;

			case 12:
				addIdtoList(1031, 1040, attendanceItemIds);
				addIdtoList(2693, 2694, attendanceItemIds);
				addIdtoList(2713, 2714, attendanceItemIds);
				addIdtoList(1789, 1832, attendanceItemIds);
				attendanceItemIds.add(2202);
				attendanceItemIds.add(2222);
				attendanceItemIds.add(2242);
				addIdtoList(2471, 2490, attendanceItemIds);
				break;

			case 13:
				addIdtoList(1041, 1050, attendanceItemIds);
				addIdtoList(2695, 2696, attendanceItemIds);
				addIdtoList(2715, 2716, attendanceItemIds);
				addIdtoList(1833, 1876, attendanceItemIds);
				attendanceItemIds.add(2203);
				attendanceItemIds.add(2223);
				attendanceItemIds.add(2243);
				addIdtoList(2491, 2510, attendanceItemIds);
				break;

			case 14:
				addIdtoList(1051, 1060, attendanceItemIds);
				addIdtoList(2697, 2698, attendanceItemIds);
				addIdtoList(2717, 2718, attendanceItemIds);
				addIdtoList(1877, 1920, attendanceItemIds);
				attendanceItemIds.add(2204);
				attendanceItemIds.add(2224);
				attendanceItemIds.add(2244);
				addIdtoList(2511, 2530, attendanceItemIds);
				break;

			case 15:
				addIdtoList(1061, 1070, attendanceItemIds);
				addIdtoList(2699, 2700, attendanceItemIds);
				addIdtoList(2719, 2720, attendanceItemIds);
				addIdtoList(1921, 1964, attendanceItemIds);
				attendanceItemIds.add(2205);
				attendanceItemIds.add(2225);
				attendanceItemIds.add(2245);
				addIdtoList(2531, 2550, attendanceItemIds);
				break;

			case 16:
				addIdtoList(1071, 1080, attendanceItemIds);
				addIdtoList(2701, 2702, attendanceItemIds);
				addIdtoList(2721, 2722, attendanceItemIds);
				addIdtoList(1965, 2008, attendanceItemIds);
				attendanceItemIds.add(2206);
				attendanceItemIds.add(2226);
				attendanceItemIds.add(2246);
				addIdtoList(2551, 2570, attendanceItemIds);
				break;

			case 17:
				addIdtoList(1081, 1090, attendanceItemIds);
				addIdtoList(2703, 2704, attendanceItemIds);
				addIdtoList(2723, 2724, attendanceItemIds);
				addIdtoList(2009, 2052, attendanceItemIds);
				attendanceItemIds.add(2207);
				attendanceItemIds.add(2227);
				attendanceItemIds.add(2247);
				addIdtoList(2571, 2590, attendanceItemIds);
				break;

			case 18:
				addIdtoList(1091, 1100, attendanceItemIds);
				addIdtoList(2705, 2706, attendanceItemIds);
				addIdtoList(2725, 2726, attendanceItemIds);
				addIdtoList(2053, 2096, attendanceItemIds);
				attendanceItemIds.add(2208);
				attendanceItemIds.add(2228);
				attendanceItemIds.add(2248);
				addIdtoList(2591, 2610, attendanceItemIds);
				break;

			case 19:
				addIdtoList(1101, 1110, attendanceItemIds);
				addIdtoList(2707, 2708, attendanceItemIds);
				addIdtoList(2727, 2728, attendanceItemIds);
				addIdtoList(2097, 2140, attendanceItemIds);
				attendanceItemIds.add(2209);
				attendanceItemIds.add(2229);
				attendanceItemIds.add(2249);
				addIdtoList(2611, 2630, attendanceItemIds);
				break;

			case 20:
				addIdtoList(1111, 1120, attendanceItemIds);
				addIdtoList(2709, 2710, attendanceItemIds);
				addIdtoList(2729, 2730, attendanceItemIds);
				addIdtoList(2141, 2184, attendanceItemIds);
				attendanceItemIds.add(2210);
				attendanceItemIds.add(2230);
				attendanceItemIds.add(2250);
				addIdtoList(2631, 2650, attendanceItemIds);
				break;
			}
		}

		return attendanceItemIds;
	}

	private static void addIdtoList(int start, int end, List<Integer> attendanceItemIds) {
		for (int i = start; i <= end; i++) {
			attendanceItemIds.add(i);
		}
	}

}
