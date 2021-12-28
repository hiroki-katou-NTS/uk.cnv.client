package nts.uk.file.at.infra.shift.estimate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.schedule.dom.shift.estimate.EstComparisonAtr;
import nts.uk.ctx.at.schedule.dom.shift.estimate.EstimateTargetClassification;
import nts.uk.ctx.at.schedule.dom.shift.estimate.EstimatedCondition;
import nts.uk.file.at.app.export.shift.estimate.ShiftEstimateColumn;
import nts.uk.file.at.app.export.shift.estimate.ShiftEstimateRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

@Stateless
public class ShiftEstimateImpl extends JpaRepository implements ShiftEstimateRepository {

	private static final String GET_EXPORT_EXCEL = "SELECT " + " 	eas.YEAR_HD_ATR " + " 		,eas.HAVY_HD_ATR "
			+ " 		,eas.SPHD_ATR " + " 		,eas.HALF_DAY_ATR" + " 		,STUFF(( SELECT "
			+ " ','+kpi.PREMIUM_NAME " + " 				 FROM KSCMT_EST_AGGREGATE eas "
			//+ " INNER JOIN KSCMT_PER_COST_EXTRA_ITEM cei ON eas.CID = cei.CID "
			+ " INNER JOIN 	KSRMT_PREMIUM_ITEM kpi ON eas.CID = kpi.CID AND "
			//+ " eas.PREMIUM_NO = kpi.PREMIUM_NO "
			+ "  WHERE eas.CID = ?cid AND kpi.USE_ATR= 1 ORDER BY kpi.PREMIUM_NO ASC FOR XML PATH ('') ), 1, 1, '' ) AS NAME"
			+ " FROM KSCMT_EST_AGGREGATE eas "
			//" INNER JOIN KSCMT_PER_COST_EXTRA_ITEM cei ON eas.CID = cei.CID "
			+ " INNER JOIN KSRMT_PREMIUM_ITEM kpi ON eas.CID = kpi.CID AND " + "" +
			//"eas.PREMIUM_NO = kpi.PREMIUM_NO " +
			" AND kpi.USE_ATR= 1 "
			+ " WHERE eas.CID = ?cid" + " 	GROUP BY eas.YEAR_HD_ATR ,eas.HAVY_HD_ATR ,eas.SPHD_ATR ,eas.HALF_DAY_ATR";

	private static final String GET_EXPORT_EXCEL_SHEET_TWO = "SELECT" + " ecs.TIME_Y_DISP" + " ,ecs.TIME_M_DISP"
			+ " ,ecs.TIME_Y_ALARM_CHECK" + " ,ecs.TIME_M_ALARM_CHECK" + " ,ecs.PRICE_Y_DISP" + " ,ecs.PRICE_M_DISP"
			+ " ,ecs.PRICE_Y_ALARM_CHECK" + " ,ecs.PRICE_M_ALARM_CHECK" + " ,ecs.DAYS_Y_DISP" + " ,ecs.DAYS_M_DISP"
			+ " ,ecs.DAYS_Y_ALARM_CHECK" + " ,ecs.DAYS_M_ALARM_CHECK" + " ,ec.COMPARISON_ATR" + " ,eac.COLOR_CD"
			+ " FROM" + " KSCMT_EST_ALARM_COLOR eac" + " INNER JOIN KSCMT_EST_COMMON ecs ON ecs.CID = eac.CID"
			+ " INNER JOIN KSCMT_EST_COMPARISON ec ON ecs.CID = ec.CID" + " WHERE ecs.CID = ?cid"
			+ " ORDER BY eac.EST_CONDITION ASC";

	private static final String GET_EXPORT_EXCEL_SHEET_THREE = "SELECT" + " CASE	" + " 	WHEN"
			+ " 		TABLE_RESULT.ROW_NUMBER = 1 THEN" + " 			TABLE_RESULT.YEAR_TIME ELSE NULL "
			+ " 		END YEAR_TIME" + " 		,TABLE_RESULT.MONTH_TIME"
			+ " 		,TABLE_RESULT.EST_CONDITION_1ST_TIME" + " 		,TABLE_RESULT.EST_CONDITION_2ND_TIME"
			+ " 		,TABLE_RESULT.EST_CONDITION_3RD_TIME" + " 		,TABLE_RESULT.EST_CONDITION_4TH_TIME"
			+ " 		,TABLE_RESULT.EST_CONDITION_5TH_TIME" + " 		,TABLE_RESULT.EST_CONDITION_1ST_MNY"
			+ " 		,TABLE_RESULT.EST_CONDITION_2ND_MNY" + " 		,TABLE_RESULT.EST_CONDITION_3RD_MNY"
			+ " 		,TABLE_RESULT.EST_CONDITION_4TH_MNY" + " 		,TABLE_RESULT.EST_CONDITION_5TH_MNY"
			+ " 		,TABLE_RESULT.EST_CONDITION_1ST_DAYS" + " 		,TABLE_RESULT.EST_CONDITION_2ND_DAYS"
			+ " 		,TABLE_RESULT.EST_CONDITION_3RD_DAYS" + " 		,TABLE_RESULT.EST_CONDITION_4TH_DAYS"
			+ " 		,TABLE_RESULT.EST_CONDITION_5TH_DAYS" + " FROM" + " (SELECT"
			+ " 	etcs.TARGET_YEAR AS YEAR_TIME" + " 	,etcs.TARGET_CLS AS MONTH_TIME"
			+ " 	,etcs.EST_CONDITION_1ST_TIME" + " 	,etcs.EST_CONDITION_2ND_TIME"
			+ " 	,etcs.EST_CONDITION_3RD_TIME" + " 	,etcs.EST_CONDITION_4TH_TIME"
			+ " 	,etcs.EST_CONDITION_5TH_TIME" + " 	,epcs.EST_CONDITION_1ST_MNY" + " 	,epcs.EST_CONDITION_2ND_MNY"
			+ " 	,epcs.EST_CONDITION_3RD_MNY" + " 	,epcs.EST_CONDITION_4TH_MNY" + " 	,epcs.EST_CONDITION_5TH_MNY"
			+ " 	,edcs.EST_CONDITION_1ST_DAYS" + " 	,edcs.EST_CONDITION_2ND_DAYS"
			+ " 	,edcs.EST_CONDITION_3RD_DAYS" + " 	,edcs.EST_CONDITION_4TH_DAYS"
			+ " 	,edcs.EST_CONDITION_5TH_DAYS	" + " 	,epcs.TARGET_YEAR AS YEAR_PRICE"
			+ " 	,etcs.TARGET_CLS AS MONTH_PRICE" + " 	,edcs.TARGET_YEAR AS YEAR_DAYS"
			+ " 	,etcs.TARGET_CLS AS MONTH_DAYS"
			+ " 	,ROW_NUMBER () OVER ( PARTITION BY etcs.TARGET_YEAR, epcs.TARGET_YEAR, edcs.TARGET_YEAR ORDER BY etcs.TARGET_CLS, epcs.TARGET_CLS, edcs.TARGET_CLS  ASC ) AS ROW_NUMBER"
			+ " FROM" + " 	KSCMT_EST_TIME_COM etcs"
			+ " 	INNER JOIN KSCMT_EST_PRICE_COM epcs ON etcs.CID = epcs.CID AND etcs.TARGET_YEAR = epcs.TARGET_YEAR AND etcs.TARGET_CLS = epcs.TARGET_CLS"
			+ " 	INNER JOIN KSCMT_EST_DAYS_COM edcs ON etcs.CID = edcs.CID AND etcs.TARGET_YEAR = edcs.TARGET_YEAR AND epcs.TARGET_CLS = edcs.TARGET_CLS "
			+ " 	WHERE etcs.CID = ?cid AND  ?startDate <= etcs.TARGET_YEAR AND etcs.TARGET_YEAR <= ?endDate"
			+ " ) AS TABLE_RESULT  ";

	private static final String GET_EXPORT_EXCEL_SHEET_FOUR = "SELECT "
+" 					 YEAR_TIME"
+" 			 		,CASE WHEN TABLE_RESULT_FULL.ROW_NUMBERR = 1 THEN TABLE_RESULT_FULL.CODE ELSE NULL END CODE "
+" 			 		,CASE WHEN TABLE_RESULT_FULL.ROW_NUMBERR = 1 THEN TABLE_RESULT_FULL.NAME ELSE NULL END NAME	"
+" 			 		,TABLE_RESULT_FULL.MONTH_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_1ST_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_2ND_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_3RD_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_4TH_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_5TH_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_1ST_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_2ND_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_3RD_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_4TH_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_5TH_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_1ST_DAYS"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_2ND_DAYS"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_3RD_DAYS"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_4TH_DAYS"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_5TH_DAYS"
+" 					FROM"
+" (SELECT"
+" 			 		CASE WHEN TABLE_RESULT.ROW_NUMBER = 1 THEN TABLE_RESULT.YEAR_TIME ELSE NULL END YEAR_TIME"
+" 			 		,CASE WHEN TABLE_RESULT.ROW_NUMBER = 1 THEN TABLE_RESULT.CODE ELSE NULL END CODE "
+" 			 		,CASE WHEN TABLE_RESULT.ROW_NUMBER = 1 THEN TABLE_RESULT.NAME ELSE NULL END NAME			"
+" 			 		,TABLE_RESULT.MONTH_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_1ST_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_2ND_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_3RD_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_4TH_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_5TH_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_1ST_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_2ND_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_3RD_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_4TH_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_5TH_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_1ST_DAYS"
+" 			 		,TABLE_RESULT.EST_CONDITION_2ND_DAYS"
+" 			 		,TABLE_RESULT.EST_CONDITION_3RD_DAYS"
+" 			 		,TABLE_RESULT.EST_CONDITION_4TH_DAYS"
+" 			 		,TABLE_RESULT.EST_CONDITION_5TH_DAYS"
+" 					,ROW_NUMBER () OVER ( PARTITION BY CODE, NAME ORDER BY  CODE, YEAR_TIME ASC ) AS ROW_NUMBERR"
+" 			 FROM"
+" 			 (SELECT"
+" 			 	etes.TARGET_YEAR AS YEAR_TIME"
+" 			 	,etes.EMPCD AS CODE"
+" 			 	,(CASE			"
+" 			 			WHEN emp.NAME IS NULL THEN"
+" 			 			'マスタ未登録' ELSE emp.NAME "
+" 			 		END) AS NAME"
+" 			 	,etes.TARGET_CLS AS MONTH_TIME"
+" 			 	,etes.EST_CONDITION_1ST_TIME"
+" 			 	,etes.EST_CONDITION_2ND_TIME"
+" 			 	,etes.EST_CONDITION_3RD_TIME"
+" 			 	,etes.EST_CONDITION_4TH_TIME"
+" 			 	,etes.EST_CONDITION_5TH_TIME"
+" 			 	,epes.EST_CONDITION_1ST_MNY"
+" 			 	,epes.EST_CONDITION_2ND_MNY"
+" 			 	,epes.EST_CONDITION_3RD_MNY"
+" 			 	,epes.EST_CONDITION_4TH_MNY"
+" 			 	,epes.EST_CONDITION_5TH_MNY"
+" 			 	,edes.EST_CONDITION_1ST_DAYS"
+" 			 	,edes.EST_CONDITION_2ND_DAYS"
+" 			 	,edes.EST_CONDITION_3RD_DAYS"
+" 			 	,edes.EST_CONDITION_4TH_DAYS"
+" 			 	,edes.EST_CONDITION_5TH_DAYS	"
+" 			 	,ROW_NUMBER () OVER ( PARTITION BY etes.TARGET_YEAR, etes.EMPCD, emp.NAME  ORDER BY etes.TARGET_CLS, etes.EMPCD ASC ) AS ROW_NUMBER"
+" 			 FROM"
+" 			 KSCMT_EST_TIME_EMP etes"
+" 			 INNER JOIN KSCMT_EST_PRICE_EMP epes ON etes.CID = epes.CID AND etes.TARGET_YEAR = epes.TARGET_YEAR AND etes.TARGET_CLS = epes.TARGET_CLS AND etes.EMPCD = epes.EMPCD AND etes.CID = ?cid"
+" 			 LEFT JOIN KSCMT_EST_DAYS_EMP edes ON etes.CID = edes.CID AND etes.TARGET_YEAR = edes.TARGET_YEAR AND etes.TARGET_CLS = edes.TARGET_CLS AND etes.EMPCD = edes.EMPCD AND etes.CID = ?cid"
+" 			 LEFT JOIN BSYMT_EMPLOYMENT emp ON etes.CID = emp.CID AND etes.EMPCD = emp.CODE AND etes.CID = ?cid"
+" 			 "
+" 			 WHERE ?startDate <= etes.TARGET_YEAR AND etes.TARGET_YEAR <= ?endDate"
+" 			 ) AS TABLE_RESULT) AS TABLE_RESULT_FULL";

	private static final String GET_EXPORT_EXCEL_SHEET_FIVE = "SELECT "
+" 					YEAR_TIME"
+" 			 		,CASE WHEN TABLE_RESULT_FULL.ROW_NUMBERR = 1 THEN TABLE_RESULT_FULL.CODE ELSE NULL END CODE "
+" 			 		,CASE WHEN TABLE_RESULT_FULL.ROW_NUMBERR = 1 THEN TABLE_RESULT_FULL.BUSINESS_NAME ELSE NULL END BUSINESS_NAME"
+" 			 		,TABLE_RESULT_FULL.MONTH_TIME  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_1ST_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_2ND_TIME  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_3RD_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_4TH_TIME  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_5TH_TIME"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_1ST_MNY  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_2ND_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_3RD_MNY  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_4TH_MNY"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_5TH_MNY  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_1ST_DAYS"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_2ND_DAYS  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_3RD_DAYS"
+" 			 		,TABLE_RESULT_FULL.EST_CONDITION_4TH_DAYS  		"
+" 					,TABLE_RESULT_FULL.EST_CONDITION_5TH_DAYS"
+" FROM"
+" (SELECT"
+" 			 		CASE WHEN TABLE_RESULT.ROW_NUMBER = 1 THEN TABLE_RESULT.YEAR_TIME ELSE NULL END YEAR_TIME"
+" 			 		,CASE WHEN TABLE_RESULT.ROW_NUMBER = 1 THEN TABLE_RESULT.CODE ELSE NULL END CODE "
+" 			 		,CASE WHEN TABLE_RESULT.ROW_NUMBER = 1 THEN TABLE_RESULT.BUSINESS_NAME ELSE NULL END BUSINESS_NAME"
+" 			 		,TABLE_RESULT.MONTH_TIME  		"
+" 					,TABLE_RESULT.EST_CONDITION_1ST_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_2ND_TIME  		"
+" 					,TABLE_RESULT.EST_CONDITION_3RD_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_4TH_TIME  		"
+" 					,TABLE_RESULT.EST_CONDITION_5TH_TIME"
+" 			 		,TABLE_RESULT.EST_CONDITION_1ST_MNY  		"
+" 					,TABLE_RESULT.EST_CONDITION_2ND_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_3RD_MNY  		"
+" 					,TABLE_RESULT.EST_CONDITION_4TH_MNY"
+" 			 		,TABLE_RESULT.EST_CONDITION_5TH_MNY  		"
+" 					,TABLE_RESULT.EST_CONDITION_1ST_DAYS"
+" 			 		,TABLE_RESULT.EST_CONDITION_2ND_DAYS  		"
+" 					,TABLE_RESULT.EST_CONDITION_3RD_DAYS"
+" 			 		,TABLE_RESULT.EST_CONDITION_4TH_DAYS  		"
+" 					,TABLE_RESULT.EST_CONDITION_5TH_DAYS  "
+" 					,ROW_NUMBER () OVER ( PARTITION BY CODE  ORDER BY  CODE ASC ) AS ROW_NUMBERR"
+" 					FROM"
+" 			 (SELECT  	etes.TARGET_YEAR AS YEAR_TIME  	"
+" 			  ,edmi.SCD AS CODE"
+" 			 	,per.BUSINESS_NAME AS BUSINESS_NAME  	"
+" 				,etes.TARGET_CLS AS MONTH_TIME"
+" 			 	,etes.EST_CONDITION_1ST_TIME  	"
+" 				,etes.EST_CONDITION_2ND_TIME"
+" 			 	,etes.EST_CONDITION_3RD_TIME  	"
+" 				,etes.EST_CONDITION_4TH_TIME"
+" 			 	,etes.EST_CONDITION_5TH_TIME  	"
+" 				,epes.EST_CONDITION_1ST_MNY  	"
+" 				,epes.EST_CONDITION_2ND_MNY"
+" 			 	,epes.EST_CONDITION_3RD_MNY  	"
+" 				,epes.EST_CONDITION_4TH_MNY  	"
+" 				,epes.EST_CONDITION_5TH_MNY"
+" 			 	,edes.EST_CONDITION_1ST_DAYS  	"
+" 				,edes.EST_CONDITION_2ND_DAYS"
+" 			 	,edes.EST_CONDITION_3RD_DAYS  	"
+" 				,edes.EST_CONDITION_4TH_DAYS"
+" 			 	,edes.EST_CONDITION_5TH_DAYS	"
+" 			 	,ROW_NUMBER () OVER ( PARTITION BY etes.TARGET_YEAR,edmi.SCD ORDER BY etes.TARGET_CLS, edmi.SCD ASC ) AS ROW_NUMBER"
+" 			 FROM  	BSYMT_SYAIN edmi"
+" 			 	INNER JOIN BPSMT_PERSON per ON edmi.PID = per.PID  AND edmi.CID = ?cid"
+" 			 	INNER JOIN KSCMT_EST_TIME_SYA etes ON edmi.SID = etes.SID"
+" 			 	INNER JOIN KSCMT_EST_PRICE_SYA epes ON edmi.SID = epes.SID AND etes.TARGET_YEAR = epes.TARGET_YEAR AND etes.TARGET_CLS = epes.TARGET_CLS"
+" 			 	INNER JOIN KSCMT_EST_DAYS_SYA edes ON edmi.SID = edes.SID AND etes.TARGET_YEAR = edes.TARGET_YEAR AND etes.TARGET_CLS = edes.TARGET_CLS "
+" 			 	WHERE   ?startDate <= etes.TARGET_YEAR AND etes.TARGET_YEAR <= ?endDate"
+" 			 ) AS TABLE_RESULT) AS TABLE_RESULT_FULL";

	/** Sheet 1 **/
	@Override
	public List<MasterData> getDataExport() {
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		Query query = getEntityManager().createNativeQuery(GET_EXPORT_EXCEL.toString()).setParameter("cid", cid);
		Object[] data = null;
		try {
			data = (Object[]) query.getSingleResult();
			for (int i = 0; i < data.length; i++) {
				datas.add(dataContent(data[i], i));
			}
		} catch (Exception e) {
			for (int i = 0; i < 5; i++) {
				datas.add(dataContent(data,i));
			}
			return datas;
		}
		return datas;
	}

	private MasterData dataContent(Object object, int rowNumber) {
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(ShiftEstimateColumn.KSM001_101,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_101)
						.value(rowNumber == 0 ? ShiftEstimateColumn.KSM001_103
								: rowNumber == 4 ? ShiftEstimateColumn.KSM001_110 : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_101_1,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_101_1)
						.value(rowNumber == 0 ? ShiftEstimateColumn.KSM001_104
								: rowNumber == 3 ? ShiftEstimateColumn.KSM001_108
										: rowNumber == 4 ? ShiftEstimateColumn.KSM001_111 : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_101_2,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_101_1)
						.value(rowNumber == 0 ? ShiftEstimateColumn.KSM001_105
								: rowNumber == 1 ? ShiftEstimateColumn.KSM001_106
										: rowNumber == 2 ? ShiftEstimateColumn.KSM001_107
												: rowNumber == 3 ? ShiftEstimateColumn.KSM001_109 : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_102,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_102)
						.value(object!= null ?  ((rowNumber < 4 ? ((BigDecimal) object).intValue() == 1 ? "○" : "-"
								: rowNumber == 4 ? (String) object : "")) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		return MasterData.builder().rowData(data).build();
	}

	/** Sheet 2 **/
	@Override
	public List<MasterData> getDataSheetTwoExport() {
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		Query query = getEntityManager().createNativeQuery(GET_EXPORT_EXCEL_SHEET_TWO.toString()).setParameter("cid", cid);
		@SuppressWarnings("unchecked")
		List<Object[]> data = query.getResultList();
		for (int i = 0; i < 18; i++) {
			datas.add(dataContentTwo(data, i));
		}
		return datas;
	}

	/** Sheet 3 **/
	@Override
	public List<MasterData> getDataSheetThreeExport(int startDate, int endDate) {
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		Query query = getEntityManager().createNativeQuery(GET_EXPORT_EXCEL_SHEET_THREE.toString()).setParameter("cid", cid)
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		@SuppressWarnings("unchecked")
		List<Object[]> data = query.getResultList();
		for (Object[] objects : data) {
			datas.add(dataContentThree(objects));
		}
		return datas;
	}

	/** Sheet 4 **/
	@Override
	public List<MasterData> getDataSheetFourExport(int startDate, int endDate) {
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		Query query = getEntityManager().createNativeQuery(GET_EXPORT_EXCEL_SHEET_FOUR.toString()).setParameter("cid", cid)
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		@SuppressWarnings("unchecked")
		List<Object[]> data = query.getResultList();
		for (Object[] objects : data) {
			datas.add(dataContentFour(objects));
		}
		return datas;
	}

	/** Sheet 5 **/
	@Override
	public List<MasterData> getDataSheetFiveExport(int startDate, int endDate) {
		String cid = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		Query query = getEntityManager().createNativeQuery(GET_EXPORT_EXCEL_SHEET_FIVE.toString()).setParameter("cid", cid)
				.setParameter("startDate", startDate).setParameter("endDate", endDate);
		@SuppressWarnings("unchecked")
		List<Object[]> data = query.getResultList();
		for (Object[] objects : data) {
			datas.add(dataContentFive(objects));
		}
		return datas;
	}

	private MasterData dataContentTwo(List<Object[]> dataFromDB, int rowNumber) {
		Map<String, MasterCellData> data = new HashMap<>();

		data.put(ShiftEstimateColumn.KSM001_112,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_112)
						.value(rowNumber == 0 ? ShiftEstimateColumn.KSM001_114
								: rowNumber == 5 ? ShiftEstimateColumn.KSM001_125
										: rowNumber == 9 ? ShiftEstimateColumn.KSM001_130
												: rowNumber == 13 ? ShiftEstimateColumn.KSM001_135
														: rowNumber == 17 ? ShiftEstimateColumn.KSM001_140 : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());

		data.put(ShiftEstimateColumn.KSM001_112_1,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_112_1).value(getHeader(rowNumber))
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());

		data.put(ShiftEstimateColumn.KSM001_112_2,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_112_2)
						.value(rowNumber == 0 ? ShiftEstimateColumn.KSM001_116
								: rowNumber == 1 ? ShiftEstimateColumn.KSM001_118
										: rowNumber == 2 ? ShiftEstimateColumn.KSM001_120
												: rowNumber == 3 ? ShiftEstimateColumn.KSM001_122
														: rowNumber == 4 ? ShiftEstimateColumn.KSM001_124 : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		if (rowNumber < 5) {
			data.put(ShiftEstimateColumn.KSM001_113, MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_113)
					.value(!dataFromDB.isEmpty() ? dataFromDB.get(rowNumber)[13] : "")
					.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
					.style(MasterCellStyle.build().backgroundColor(!dataFromDB.isEmpty() ? dataFromDB.get(rowNumber)[13].toString() : ""))
					//.style(MasterCellStyle.build().backgroundColor(dataFromDB.get(rowNumber)[13].toString()))
					.build());
		} else {
			// EstComparisonAtr enum
			// EstimatedCondition enum
			data.put(ShiftEstimateColumn.KSM001_113,
					MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_113)
							.value(!dataFromDB.isEmpty() ? (rowNumber == 17
									? EnumAdaptor.valueOf(((BigDecimal) dataFromDB.get(0)[rowNumber - 5]).intValue(),
											EstComparisonAtr.class).description
									: EnumAdaptor.valueOf(((BigDecimal) dataFromDB.get(0)[rowNumber - 5]).intValue(),
											EstimatedCondition.class).description)
									: "")
							.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		}
		return MasterData.builder().rowData(data).build();
	}

	private MasterData dataContentThree(Object[] object) {
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(ShiftEstimateColumn.KSM001_141,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_141)
						.value(object[0] != null ? (BigDecimal) object[0] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_142,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_142)
						.value(object[1] != null ? EnumAdaptor.valueOf(((BigDecimal) object[1]).intValue(),
								EstimateTargetClassification.class).description : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_143,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_143)
						.value(object[2] != null ? formatTime(((BigDecimal) object[2]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_144,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_144)
						.value(object[3] != null ? formatTime(((BigDecimal) object[3]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_145,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_145)
						.value(object[4] != null ? formatTime(((BigDecimal) object[4]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_146,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_146)
						.value(object[5] != null ? formatTime(((BigDecimal) object[5]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_147,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_147)
						.value(object[6] != null ? formatTime(((BigDecimal) object[6]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_148,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_148)
						.value(object[7] != null ? formatPrice(((BigDecimal) object[7]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_149,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_149)
						.value(object[8] != null ? formatPrice(((BigDecimal) object[8]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_150,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_150)
						.value(object[9] != null ? formatPrice(((BigDecimal) object[9]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_151,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_151)
						.value(object[10] != null ? formatPrice(((BigDecimal) object[10]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_152,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_152)
						.value(object[11] != null ? formatPrice(((BigDecimal) object[11]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_153,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_153)
						.value(object[12] != null ? formatDays(((BigDecimal) object[12]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_154,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_154)
						.value(object[13] != null ? formatDays(((BigDecimal) object[13]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_155,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_155)
						.value(object[14] != null ? formatDays(((BigDecimal) object[14]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_156,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_156)
						.value(object[15] != null ? formatDays(((BigDecimal) object[15]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_157,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_157)
						.value(object[16] != null ? formatDays(((BigDecimal) object[16]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData dataContentFour(Object[] object) {
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(ShiftEstimateColumn.KSM001_158,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_158)
						.value(object[0] != null ? (BigDecimal) object[0] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_159,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_159)
						.value(object[1] != null ? (String) object[1] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_160,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_160)
						.value(object[2] != null ? (String) object[2] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_161,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_161)
						.value(object[3] != null ? EnumAdaptor.valueOf(((BigDecimal) object[3]).intValue(),
								EstimateTargetClassification.class).description : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_162,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_162)
						.value(object[4] != null ? formatTime(((BigDecimal) object[4]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_163,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_163)
						.value(object[5] != null ? formatTime(((BigDecimal) object[5]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_164,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_164)
						.value(object[6] != null ? formatTime(((BigDecimal) object[6]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_165,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_165)
						.value(object[7] != null ? formatTime(((BigDecimal) object[7]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_166,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_166)
						.value(object[8] != null ? formatTime(((BigDecimal) object[8]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_167,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_167)
						.value(object[9] != null ? formatPrice(((BigDecimal) object[9]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_168,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_168)
						.value(object[10] != null ? formatPrice(((BigDecimal) object[10]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_169,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_169)
						.value(object[11] != null ? formatPrice(((BigDecimal) object[11]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_170,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_170)
						.value(object[12] != null ? formatPrice(((BigDecimal) object[12]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_171,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_171)
						.value(object[13] != null ? formatPrice(((BigDecimal) object[13]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_172,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_172)
						.value(object[14] != null ? formatDays(((BigDecimal) object[14]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_173,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_173)
						.value(object[15] != null ? formatDays(((BigDecimal) object[15]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_174,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_174)
						.value(object[16] != null ? formatDays(((BigDecimal) object[16]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_175,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_175)
						.value(object[17] != null ? formatDays(((BigDecimal) object[17]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_176,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_176)
						.value(object[18] != null ? formatDays(((BigDecimal) object[18]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData dataContentFive(Object[] object) {
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(ShiftEstimateColumn.KSM001_177,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_177)
						.value(object[0] != null ? (BigDecimal) object[0] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_178,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_178)
						.value(object[1] != null ? (String) object[1] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_179,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_179)
						.value(object[2] != null ? (String) object[2] : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_180,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_180)
						.value(object[3] != null ? EnumAdaptor.valueOf(((BigDecimal) object[3]).intValue(),
								EstimateTargetClassification.class).description : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		data.put(ShiftEstimateColumn.KSM001_181,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_181)
						.value(object[4] != null ? formatTime(((BigDecimal) object[4]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_182,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_182)
						.value(object[5] != null ? formatTime(((BigDecimal) object[5]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_183,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_183)
						.value(object[6] != null ? formatTime(((BigDecimal) object[6]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_184,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_184)
						.value(object[7] != null ? formatTime(((BigDecimal) object[7]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_185,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_185)
						.value(object[8] != null ? formatTime(((BigDecimal) object[8]).intValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_186,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_186)
						.value(object[9] != null ? formatPrice(((BigDecimal) object[9]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_187,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_187)
						.value(object[10] != null ? formatPrice(((BigDecimal) object[10]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_188,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_188)
						.value(object[11] != null ? formatPrice(((BigDecimal) object[11]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_189,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_189)
						.value(object[12] != null ? formatPrice(((BigDecimal) object[12]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_190,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_190)
						.value(object[13] != null ? formatPrice(((BigDecimal) object[13]).toString()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_191,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_191)
						.value(object[14] != null ? formatDays(((BigDecimal) object[14]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_192,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_192)
						.value(object[15] != null ? formatDays(((BigDecimal) object[15]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_193,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_193)
						.value(object[16] != null ? formatDays(((BigDecimal) object[16]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_194,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_194)
						.value(object[17] != null ? formatDays(((BigDecimal) object[17]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		data.put(ShiftEstimateColumn.KSM001_195,
				MasterCellData.builder().columnId(ShiftEstimateColumn.KSM001_195)
						.value(object[18] != null ? formatDays(((BigDecimal) object[18]).doubleValue()) : "")
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT)).build());
		return MasterData.builder().rowData(data).build();
	}

	private String formatTime(int time) {
		int hours = time / 60;
		int minutes = time % 60;
		String result = String.format("%d:%02d", hours, minutes);
		return result;
	}

	private String formatDays(Double day) {
		String result = String.format("%1$.1f", day);
		return result;
	}

	private String formatPrice(String price) {
		double amountParse = Double.parseDouble(price);
		DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.JAPAN);
		dfs.setCurrencySymbol("¥");
		decimalFormat.setDecimalFormatSymbols(dfs);
		return decimalFormat.format(amountParse);
	}

	private String getHeader(int rowNumber) {
		String value = "";
		switch (rowNumber) {
		case 0:
			value = ShiftEstimateColumn.KSM001_115;
			break;
		case 1:
			value = ShiftEstimateColumn.KSM001_117;
			break;
		case 2:
			value = ShiftEstimateColumn.KSM001_119;
			break;
		case 3:
			value = ShiftEstimateColumn.KSM001_121;
			break;
		case 4:
			value = ShiftEstimateColumn.KSM001_123;
			break;
		case 5:
			value = ShiftEstimateColumn.KSM001_126;
			break;
		case 6:
			value = ShiftEstimateColumn.KSM001_127;
			break;
		case 7:
			value = ShiftEstimateColumn.KSM001_128;
			break;
		case 8:
			value = ShiftEstimateColumn.KSM001_129;
			break;
		case 9:
			value = ShiftEstimateColumn.KSM001_131;
			break;
		case 10:
			value = ShiftEstimateColumn.KSM001_132;
			break;
		case 11:
			value = ShiftEstimateColumn.KSM001_133;
			break;
		case 12:
			value = ShiftEstimateColumn.KSM001_134;
			break;
		case 13:
			value = ShiftEstimateColumn.KSM001_136;
			break;
		case 14:
			value = ShiftEstimateColumn.KSM001_137;
			break;
		case 15:
			value = ShiftEstimateColumn.KSM001_138;
			break;
		case 16:
			value = ShiftEstimateColumn.KSM001_139;
			break;

		default:
			break;
		}
		return value;
	}

}
