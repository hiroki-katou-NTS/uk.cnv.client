package nts.uk.file.at.infra.registtime;

import java.math.BigDecimal;
import java.util.*;

import javax.ejb.Stateless;
import javax.persistence.Query;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.DoWork;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.*;
import nts.uk.file.at.app.export.regisagreetime.RegistTimeColumn;
import nts.uk.file.at.app.export.regisagreetime.RegistTimeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

@Stateless
public class JpaRegisterTimeImpl extends JpaRepository implements RegistTimeRepository {
	
	private static final String END_MONTH = "12";

	private static final String SQL_EXPORT_SHEET_1 = "SELECT "
			+ "START_MONTH, "
			+ "MCLOSE_DATE, "
			+ "APP_USE_ATR, "
			+ "ANNUAL_UNIT_ATR, "
			+ "IS_LAST_DAY "
			+ "FROM "
			+ "KSRMT_36AGR_OPERATION "
			+ "WHERE CID = ?1 ";

	private static final String SQL_EXPORT_SHEET_2 = "SELECT aa.EMPLOYMENT_USE_ATR,aa.WORKPLACE_USE_ATR,aa.CLASSIFICATION_USE_ATR "
			+ "FROM  "
			+ "KRCMT_36AGR_UNIT aa "
			+ "WHERE aa.CID = ?1 ";

	private static final String SQL_EXPORT_SHEET_3 = "SELECT aa.WKP_USE_ATR "
			+ "FROM  "
			+ "KRCMT_36AGR_APV_UNIT aa "
			+ "WHERE aa.CID = ?1 ";

	private static final String SQL_EXPORT_SHEET_4_8 = "SELECT "
			+ "aa.UPPER_LIMIT_CNT, "
			+ "aa.BASIC_M_LIMIT_TIME, "
			+ "aa.BASIC_M_ER_TIME, "
			+ "aa.BASIC_M_AL_TIME, "
			+ "aa.SP_M_LIMIT_TIME, "
			+ "aa.SP_M_ER_TIME, "
			+ "aa.SP_M_AL_TIME, "
			+ "aa.BASIC_Y_ER_TIME, "
			+ "aa.BASIC_Y_AL_TIME, "
			+ "aa.SP_Y_LIMIT_TIME, "
			+ "aa.SP_Y_ER_TIME, "
			+ "aa.SP_Y_AL_TIME, "
			+ "aa.MULTI_M_AVG_ER_TIME, "
			+ "aa.MULTI_M_AVG_AL_TIME "
			+ "FROM  "
			+ "KSRMT_36AGR_MGT_CMP aa "
			+ "WHERE aa.CID = ?cid AND aa.LABOR_SYSTEM_ATR = ?laborSystemAtr ";

	private static final String SQL_EXPORT_SHEET_5_9 = "SELECT "
			+ "aa.EMP_CD, "
			+ " Case When kk.NAME is NULL THEN 'マスタ未登録' ELSE kk.NAME END NAME,"
			+ "aa.UPPER_LIMIT_CNT, "
			+ "aa.BASIC_M_LIMIT_TIME, "
			+ "aa.BASIC_M_ER_TIME, "
			+ "aa.BASIC_M_AL_TIME, "
			+ "aa.SP_M_LIMIT_TIME, "
			+ "aa.SP_M_ER_TIME, "
			+ "aa.SP_M_AL_TIME, "
			+ "aa.BASIC_Y_ER_TIME, "
			+ "aa.BASIC_Y_AL_TIME, "
			+ "aa.SP_Y_LIMIT_TIME, "
			+ "aa.SP_Y_ER_TIME, "
			+ "aa.SP_Y_AL_TIME, "
			+ "aa.MULTI_M_AVG_ER_TIME, "
			+ "aa.MULTI_M_AVG_AL_TIME "
			+ "FROM  "
			+ "KSRMT_36AGR_MGT_EMP aa "
			+ "LEFT JOIN BSYMT_EMPLOYMENT kk "
			+ "ON aa.EMP_CD = kk.CODE AND kk.CID = ?cid "
			+ "WHERE aa.CID = ?cid AND aa.LABOR_SYSTEM_ATR = ?laborSystemAtr "
			+ "ORDER BY aa.EMP_CD ";

	private static final String SQL_EXPORT_SHEET_6_10 = "SELECT "
			+ "kk.WKP_CD, "
			+ "kk.WKP_NAME, "
			+ "aa.UPPER_LIMIT_CNT, "
			+ "aa.BASIC_M_LIMIT_TIME, "
			+ "aa.BASIC_M_ER_TIME, "
			+ "aa.BASIC_M_AL_TIME, "
			+ "aa.SP_M_LIMIT_TIME, "
			+ "aa.SP_M_ER_TIME, "
			+ "aa.SP_M_AL_TIME, "
			+ "aa.BASIC_Y_ER_TIME, "
			+ "aa.BASIC_Y_AL_TIME, "
			+ "aa.SP_Y_LIMIT_TIME, "
			+ "aa.SP_Y_ER_TIME, "
			+ "aa.SP_Y_AL_TIME, "
			+ "aa.MULTI_M_AVG_ER_TIME, "
			+ "aa.MULTI_M_AVG_AL_TIME "
			+ "FROM  "
			+ "KSRMT_36AGR_MGT_WKP aa "
			+ "JOIN BSYMT_WKP_INFO kk "
			+ "ON aa.WKP_ID = kk.WKP_ID "
			+ "JOIN BSYMT_WKP_CONFIG_2 hh "
			+ "ON hh.CID = kk.CID AND kk.WKP_HIST_ID = hh.WKP_HIST_ID AND  hh.END_DATE = '9999-12-31 00:00:00' "
			+ "WHERE aa.CID = ?cid AND aa.LABOR_SYSTEM_ATR = ?laborSystemAtr "
			+ "ORDER BY kk.WKP_CD ";

	private static final String SQL_EXPORT_SHEET_7_11 = "SELECT "
			+ "aa.CLS_CD, "
			+ "Case When kk.CLSNAME is NULL THEN 'マスタ未登録' ELSE kk.CLSNAME END CLSNAME, "
			+ "aa.UPPER_LIMIT_CNT, "
			+ "aa.BASIC_M_LIMIT_TIME, "
			+ "aa.BASIC_M_ER_TIME, "
			+ "aa.BASIC_M_AL_TIME, "
			+ "aa.SP_M_LIMIT_TIME, "
			+ "aa.SP_M_ER_TIME, "
			+ "aa.SP_M_AL_TIME, "
			+ "aa.BASIC_Y_ER_TIME, "
			+ "aa.BASIC_Y_AL_TIME, "
			+ "aa.SP_Y_LIMIT_TIME, "
			+ "aa.SP_Y_ER_TIME, "
			+ "aa.SP_Y_AL_TIME, "
			+ "aa.MULTI_M_AVG_ER_TIME, "
			+ "aa.MULTI_M_AVG_AL_TIME "
			+ "FROM  "
			+ "KSRMT_36AGR_MGT_CLS aa "
			+ "Left JOIN BSYMT_CLASSIFICATION kk ON aa.CLS_CD = kk.CLSCD AND kk.CID = ?cid "
			+ "WHERE aa.CID = ?cid AND aa.LABOR_SYSTEM_ATR = ?laborSystemAtr "
			+ "ORDER BY aa.CLS_CD ";

//	private static final String SQL_EXPORT_SHEET_4 = " WITH summary AS ("
//			+ " SELECT "
//			+ " 	kk.WKP_CD,"
//			+ " 	kk.WKP_NAME,"
//			+ " 	aa.ERROR_WEEK,"
//			+ " 	aa.ALARM_WEEK,"
//			+ " 	LIMIT_WEEK = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_WEEK"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_WEEKS,"
//			+ " 	aa.ALARM_TWO_WEEKS,"
//			+ " 	LIMIT_TWO_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_FOUR_WEEKS,"
//			+ " 	aa.ALARM_FOUR_WEEKS,"
//			+ " 	LIMIT_FOUR_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_FOUR_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_ONE_MONTH,"
//			+ " 	aa.ALARM_ONE_MONTH,"
//			+ " 	LIMIT_ONE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_ONE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_MONTH,"
//			+ " 	aa.ALARM_TWO_MONTH,"
//			+ " 	LIMIT_TWO_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_THREE_MONTH,"
//			+ " 	aa.ALARM_THREE_MONTH,"
//			+ " 	LIMIT_THREE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_THREE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_YEARLY,"
//			+ " 	aa.ALARM_YEARLY,"
//			+ " 	LIMIT_YEARLY = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_YEARLY"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	kk.HIERARCHY_CD, "
//			+ "		bb.UPPER_MONTH,"
//			+ "		bb.UPPER_MONTH_AVERAGE"
//			+ " FROM"
//			+ " 	KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_WKP bb "
//			+ " ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " JOIN BSYMT_WKP_INFO kk "
//			+ " ON bb.WKPCD = kk.WKP_ID"
//			+ " JOIN BSYMT_WKP_CONFIG_2 hh "
//			+ "	ON hh.CID = kk.CID AND hh.END_DATE = '9999-12-31 00:00:00' "
//			+ " WHERE"
//			+ " 	 bb.LABOR_SYSTEM_ATR = 0 AND kk.CID = ?cid"
//			+ " )"
//			+ " SELECT "
//			+ " s.WKP_CD as WKPCD,"
//			+ " s.WKP_NAME as WKP_NAME,"
//			+ " s.ERROR_WEEK,s.ALARM_WEEK,s.LIMIT_WEEK,"
//			+ " 			 s.ERROR_TWO_WEEKS,s.ALARM_TWO_WEEKS,s.LIMIT_TWO_WEEKS,"
//			+ " 			 s.ERROR_FOUR_WEEKS,s.ALARM_FOUR_WEEKS,s.LIMIT_FOUR_WEEKS,"
//			+ " 			 s.ERROR_ONE_MONTH,s.ALARM_ONE_MONTH,s.LIMIT_ONE_MONTH,"
//			+ " 			 s.ERROR_TWO_MONTH,s.ALARM_TWO_MONTH,s.LIMIT_TWO_MONTH,"
//			+ " 			 s.ERROR_THREE_MONTH,s.ALARM_THREE_MONTH,s.LIMIT_THREE_MONTH,"
//			+ " 			 s.ERROR_YEARLY,s.ALARM_YEARLY,s.LIMIT_YEARLY,"
//			+ "				 s.UPPER_MONTH, s.UPPER_MONTH_AVERAGE"
//			+ "   FROM summary s"
//			+ "	 ORDER BY  CASE WHEN (s.HIERARCHY_CD IS NULL OR s.END_DATE < '9999-12-31 00:00:00') THEN 1 ELSE 0 END ASC, s.HIERARCHY_CD  ";

//	private static final String SQL_EXPORT_SHEET_5 = " SELECT"
//			+ " 	bb.CLSCD,"
//			+ " 	Case When kk.CLSNAME is NULL THEN 'マスタ未登録' ELSE kk.CLSNAME END CLSNAME,"
//			+ " 	aa.ERROR_WEEK,"
//			+ " 	aa.ALARM_WEEK,"
//			+ " 	LIMIT_WEEK = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_WEEK"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_WEEKS,"
//			+ " 	aa.ALARM_TWO_WEEKS,"
//			+ " 	LIMIT_TWO_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_FOUR_WEEKS,"
//			+ " 	aa.ALARM_FOUR_WEEKS,"
//			+ " 	LIMIT_FOUR_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_FOUR_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_ONE_MONTH,"
//			+ " 	aa.ALARM_ONE_MONTH,"
//			+ " 	LIMIT_ONE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_ONE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_MONTH,"
//			+ " 	aa.ALARM_TWO_MONTH,"
//			+ " 	LIMIT_TWO_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_THREE_MONTH,"
//			+ " 	aa.ALARM_THREE_MONTH,"
//			+ " 	LIMIT_THREE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_THREE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ " 	aa.ERROR_YEARLY,"
//			+ " 	aa.ALARM_YEARLY,"
//			+ " 	LIMIT_YEARLY = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_YEARLY"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0"
//			+ " 	),"
//			+ "		bb.UPPER_MONTH,"
//			+ "		bb.UPPER_MONTH_AVERAGE"
//			+ " FROM"
//			+ " 	KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_CLS bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " Left JOIN BSYMT_CLASSIFICATION kk ON bb.CLSCD = kk.CLSCD AND kk.CID = ?cid "
//			+ " WHERE"
//			+ " 	bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 0 "
//			+ " ORDER BY bb.CLSCD";

//	private static final String SQL_EXPORT_SHEET_6 = "SELECT aa.ERROR_WEEK,aa.ALARM_WEEK,aa.LIMIT_WEEK, "
//			+ "aa.ERROR_TWO_WEEKS,aa.ALARM_TWO_WEEKS,aa.LIMIT_TWO_WEEKS, "
//			+ "aa.ERROR_FOUR_WEEKS,aa.ALARM_FOUR_WEEKS,aa.LIMIT_FOUR_WEEKS, "
//			+ "aa.ERROR_ONE_MONTH,aa.ALARM_ONE_MONTH,aa.LIMIT_ONE_MONTH, "
//			+ "aa.ERROR_TWO_MONTH,aa.ALARM_TWO_MONTH,aa.LIMIT_TWO_MONTH, "
//			+ "aa.ERROR_THREE_MONTH,aa.ALARM_THREE_MONTH,aa.LIMIT_THREE_MONTH, "
//			+ "aa.ERROR_YEARLY,aa.ALARM_YEARLY,aa.LIMIT_YEARLY, "
//			+ "bb.UPPER_MONTH, bb.UPPER_MONTH_AVERAGE "
//			+ "FROM  "
//			+ "KRCMT_36AGR_BASIC aa "
//			+ "JOIN  "
//			+ "KRCMT_36AGR_TIME_COM bb "
//			+ "ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID "
//			+ "WHERE bb.CID = ?1 and bb.LABOR_SYSTEM_ATR = 1";

//	private static final String SQL_EXPORT_SHEET_7 = " SELECT"
//			+ " bb.EMP_CTG_CODE,"
//			+ " Case When kk.NAME is NULL THEN 'マスタ未登録' ELSE kk.NAME END NAME,"
//			+ " aa.ERROR_WEEK,"
//			+ " aa.ALARM_WEEK,"
//			+ " LIMIT_WEEK = ("
//			+ " SELECT"
//			+ " aa.LIMIT_WEEK"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ " aa.ERROR_TWO_WEEKS,"
//			+ " aa.ALARM_TWO_WEEKS,"
//			+ " LIMIT_TWO_WEEKS = ("
//			+ " SELECT"
//			+ " aa.LIMIT_TWO_WEEKS"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ " aa.ERROR_FOUR_WEEKS,"
//			+ " aa.ALARM_FOUR_WEEKS,"
//			+ " LIMIT_FOUR_WEEKS = ("
//			+ " SELECT"
//			+ " aa.LIMIT_FOUR_WEEKS"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ " aa.ERROR_ONE_MONTH,"
//			+ " aa.ALARM_ONE_MONTH,"
//			+ " LIMIT_ONE_MONTH = ("
//			+ " SELECT"
//			+ " aa.LIMIT_ONE_MONTH"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ " aa.ERROR_TWO_MONTH,"
//			+ " aa.ALARM_TWO_MONTH,"
//			+ " LIMIT_TWO_MONTH = ("
//			+ " SELECT"
//			+ " aa.LIMIT_TWO_MONTH"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ " aa.ERROR_THREE_MONTH,"
//			+ " aa.ALARM_THREE_MONTH,"
//			+ " LIMIT_THREE_MONTH = ("
//			+ " SELECT"
//			+ " aa.LIMIT_THREE_MONTH"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ " aa.ERROR_YEARLY,"
//			+ " aa.ALARM_YEARLY,"
//			+ " LIMIT_YEARLY = ("
//			+ " SELECT"
//			+ " aa.LIMIT_YEARLY"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " ),"
//			+ "	bb.UPPER_MONTH,"
//			+ "	bb.UPPER_MONTH_AVERAGE"
//			+ " FROM"
//			+ " KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_EMP bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " LEFT JOIN BSYMT_EMPLOYMENT kk"
//			+ " ON bb.EMP_CTG_CODE = kk.CODE AND kk.CID = ?cid"
//			+ " WHERE"
//			+ " bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1 "
//			+ " ORDER BY bb.EMP_CTG_CODE";

//	private static final String SQL_EXPORT_SHEET_8 =  " WITH summary AS ("
//			+ " SELECT "
//			+ " 	kk.WKP_CD,"
//			+ " 	kk.WKP_NAME,"
//			+ " 	aa.ERROR_WEEK,"
//			+ " 	aa.ALARM_WEEK,"
//			+ " 	LIMIT_WEEK = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_WEEK"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_WEEKS,"
//			+ " 	aa.ALARM_TWO_WEEKS,"
//			+ " 	LIMIT_TWO_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_FOUR_WEEKS,"
//			+ " 	aa.ALARM_FOUR_WEEKS,"
//			+ " 	LIMIT_FOUR_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_FOUR_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_ONE_MONTH,"
//			+ " 	aa.ALARM_ONE_MONTH,"
//			+ " 	LIMIT_ONE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_ONE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_MONTH,"
//			+ " 	aa.ALARM_TWO_MONTH,"
//			+ " 	LIMIT_TWO_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_THREE_MONTH,"
//			+ " 	aa.ALARM_THREE_MONTH,"
//			+ " 	LIMIT_THREE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_THREE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_YEARLY,"
//			+ " 	aa.ALARM_YEARLY,"
//			+ " 	LIMIT_YEARLY = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_YEARLY"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	), "
//			+ " kk.HIERARCHY_CD,"
//			+ "	bb.UPPER_MONTH,"
//			+ "	bb.UPPER_MONTH_AVERAGE"
//			+ " FROM"
//			+ " 	KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_WKP bb "
//			+ " ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " JOIN BSYMT_WKP_INFO kk "
//			+ " ON bb.WKPCD = kk.WKP_ID"
//			+ "	JOIN BSYMT_WKP_CONFIG_2 hh "
//			+ "	ON hh.CID = kk.CID AND hh.END_DATE = '9999-12-31 00:00:00' "
//			+ " WHERE"
//			+ " 	 bb.LABOR_SYSTEM_ATR = 1 AND kk.CID = ?cid"
//			+ " )"
//			+ " SELECT "
//			+ " s.WKP_CD as WKPCD,"
//			+ " s.WKP_NAME as WKP_NAME,"
//			+ "				 s.ERROR_WEEK,s.ALARM_WEEK,s.LIMIT_WEEK,"
//			+ " 			 s.ERROR_TWO_WEEKS,s.ALARM_TWO_WEEKS,s.LIMIT_TWO_WEEKS,"
//			+ " 			 s.ERROR_FOUR_WEEKS,s.ALARM_FOUR_WEEKS,s.LIMIT_FOUR_WEEKS,"
//			+ " 			 s.ERROR_ONE_MONTH,s.ALARM_ONE_MONTH,s.LIMIT_ONE_MONTH,"
//			+ " 			 s.ERROR_TWO_MONTH,s.ALARM_TWO_MONTH,s.LIMIT_TWO_MONTH,"
//			+ " 			 s.ERROR_THREE_MONTH,s.ALARM_THREE_MONTH,s.LIMIT_THREE_MONTH,"
//			+ " 			 s.ERROR_YEARLY,s.ALARM_YEARLY,s.LIMIT_YEARLY,"
//			+ "				 s.UPPER_MONTH, s.UPPER_MONTH_AVERAGE"
//			+ "   FROM summary s"
//			+ "	 ORDER BY CASE WHEN (HIERARCHY_CD IS NULL OR s.END_DATE < '9999-12-31 00:00:00') THEN 1 ELSE 0 END ASC , HIERARCHY_CD";

//	private static final String SQL_EXPORT_SHEET_9 = " SELECT"
//			+ " 	bb.CLSCD,"
//			+ " 	Case When kk.CLSNAME is NULL THEN 'マスタ未登録' ELSE kk.CLSNAME END CLSNAME,"
//			+ " 	aa.ERROR_WEEK,"
//			+ " 	aa.ALARM_WEEK,"
//			+ " 	LIMIT_WEEK = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_WEEK"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_WEEKS,"
//			+ " 	aa.ALARM_TWO_WEEKS,"
//			+ " 	LIMIT_TWO_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_FOUR_WEEKS,"
//			+ " 	aa.ALARM_FOUR_WEEKS,"
//			+ " 	LIMIT_FOUR_WEEKS = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_FOUR_WEEKS"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_ONE_MONTH,"
//			+ " 	aa.ALARM_ONE_MONTH,"
//			+ " 	LIMIT_ONE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_ONE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_TWO_MONTH,"
//			+ " 	aa.ALARM_TWO_MONTH,"
//			+ " 	LIMIT_TWO_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_TWO_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_THREE_MONTH,"
//			+ " 	aa.ALARM_THREE_MONTH,"
//			+ " 	LIMIT_THREE_MONTH = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_THREE_MONTH"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ " 	aa.ERROR_YEARLY,"
//			+ " 	aa.ALARM_YEARLY,"
//			+ " 	LIMIT_YEARLY = ("
//			+ " 		SELECT"
//			+ " 			aa.LIMIT_YEARLY"
//			+ " 		FROM"
//			+ " 			KRCMT_36AGR_BASIC aa"
//			+ " 		JOIN KRCMT_36AGR_TIME_COM bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " 		WHERE"
//			+ " 			bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1"
//			+ " 	),"
//			+ "		bb.UPPER_MONTH,"
//			+ "		bb.UPPER_MONTH_AVERAGE"
//			+ " FROM"
//			+ " 	KRCMT_36AGR_BASIC aa"
//			+ " JOIN KRCMT_36AGR_TIME_CLS bb ON aa.BASIC_SETTING_ID = bb.BASIC_SETTING_ID"
//			+ " Left JOIN BSYMT_CLASSIFICATION kk ON bb.CLSCD = kk.CLSCD AND kk.CID = ?cid "
//			+ " WHERE"
//			+ " 	bb.CID = ?cid AND bb.LABOR_SYSTEM_ATR = 1 "
//			+ " ORDER BY bb.CLSCD";

	private static final String SQL_EXPORT_SHEET_12 =  " SELECT *,"
			+ " 	ROW_NUMBER () OVER (" 
			+ "  				PARTITION BY AA.SCD" 
			+ "  				ORDER BY" 
			+ "  					 AA.YM_K" 
			+ " 					,AA.Y_K_ORDER  " 
			+ "  			) AS rk" 
			+ "  FROM" 
			+ " (" 
			+ " SELECT Case When SCD_1 is NULL THEN SCD_2 ELSE SCD_1 END SCD," 
			+ " 			 Case When BUSINESS_NAME_1 is NULL THEN BUSINESS_NAME_2 ELSE BUSINESS_NAME_1 END BUSINESS_NAME ," 
			+ " 			 Case When YM_K is NULL THEN '999913' ELSE YM_K END YM_K," 
			+ " 			 ERROR_ONE_MONTH," 
			+ " 			 ALARM_ONE_MONTH," 
			+ " 			 Y_K," 
			+ " 			 Case When Y_K is NULL THEN '9999' ELSE Y_K END Y_K_ORDER," 
			+ " 			 ERROR_YEARLY," 
			+ " 			 ALARM_YEARLY" 
			+ "  FROM" 
			+ " (SELECT " 
			+ " 			cc.SCD as SCD_1," 
			+ "  			dd.BUSINESS_NAME as BUSINESS_NAME_1," 
			+ "  			aa.YM_K," 
			+ "  			aa.ERROR_ONE_MONTH," 
			+ "  			aa.ALARM_ONE_MONTH," 
			+ " 			ROW_NUMBER () OVER (" 
			+ "  				PARTITION BY cc.SCD" 
			+ "  				ORDER BY" 
			+ "  					cc.SCD," 
			+ "  					aa.YM_K " 
			+ "  			) AS ROW_NUMBER1" 
			+ " 	FROM" 
			+ "  			KRCMT_36AGR_MONTH aa" 
			+ "  		JOIN BSYMT_SYAIN cc ON aa.SID = cc.SID" 
			+ "  		JOIN BPSMT_PERSON dd ON cc.PID = dd.PID" 
			+ "  		WHERE" 
			+ "  		aa.YM_K >= ?startYM" 
			+ "  		AND aa.YM_K <= ?endYM" 
			+ "  		AND cc.CID = ?cid) A" 
			+ " " 
			+ " FULL JOIN" 
			+ " " 
			+ " (SELECT" 
			+ " 			cc.SCD as SCD_2," 
			+ "  			dd.BUSINESS_NAME as BUSINESS_NAME_2," 
			+ "  			bb.Y_K," 
			+ "  			bb.ERROR_YEARLY," 
			+ "  			bb.ALARM_YEARLY," 
			+ " 			ROW_NUMBER () OVER (" 
			+ "  				PARTITION BY cc.SCD" 
			+ "  				ORDER BY" 
			+ "  					cc.SCD," 
			+ " 					bb.Y_K " 
			+ "  			) AS ROW_NUMBER2" 
			+ " FROM" 
			+ "  		KRCMT_36AGR_YEAR bb " 
			+ "  		JOIN BSYMT_SYAIN cc ON bb.SID = cc.SID" 
			+ "  		JOIN BPSMT_PERSON dd ON cc.PID = dd.PID" 
			+ "  		WHERE" 
			+ "  			bb.Y_K >= ?startY" 
			+ "  		AND bb.Y_K <= ?endY" 
			+ "  		AND cc.CID = ?cid" 
			+ " ) B" 
			+ " " 
			+ " ON A.SCD_1 = B.SCD_2 AND A.ROW_NUMBER1 = B.ROW_NUMBER2" 
			+ " ) AA" 
			+ " ORDER BY AA.SCD, " 
			+ " AA.YM_K  " 
			+ " ,AA.Y_K_ORDER ";
	
	
	@Override
	public List<MasterData> getDataExportSheet1() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_1.toString()).setParameter(1, cid);
		try {
			Object[] data = (Object[]) query.getSingleResult();
			for (int i = 0; i <= 3; i++) {
				int islastDay = ((BigDecimal)data[4]).intValue();
				datas.add(toDataSheet1(data[i],i,islastDay));
			}
		} catch (Exception e) {
			for (int i = 0; i <= 3; i++) {
				datas.add(toDataEmptySheet1(i));
			}
			return datas;
		}
		return datas;
	}

	//sheet1
	private MasterData toDataEmptySheet1(int checkRow) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S1KMK008_80, MasterCellData.builder()
                .columnId(RegistTimeColumn.S1KMK008_80)
                .value(checkRow == 0 ? RegistTimeColumn.S1KMK008_82 : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
		data.put(RegistTimeColumn.HEADER_NONE1, MasterCellData.builder()
                .columnId(RegistTimeColumn.HEADER_NONE1)
                .value(checkRow == 0 ? RegistTimeColumn.S1KMK008_83 : checkRow == 1 ? RegistTimeColumn.S1KMK008_84 :
					checkRow == 2 ? I18NText.getText("KMK008_176") : checkRow == 3 ? I18NText.getText("KMK008_179") : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
		data.put(RegistTimeColumn.S1KMK008_81, MasterCellData.builder()
                .columnId(RegistTimeColumn.S1KMK008_81)
                .value("")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataSheet1(Object object,int check,int islastDay) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S1KMK008_80, MasterCellData.builder()
                .columnId(RegistTimeColumn.S1KMK008_80)
                .value(check == 0 ? RegistTimeColumn.S1KMK008_82 : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
		data.put(RegistTimeColumn.HEADER_NONE1, MasterCellData.builder()
                .columnId(RegistTimeColumn.HEADER_NONE1)
                .value(check == 0 ? RegistTimeColumn.S1KMK008_83 : check == 1 ? RegistTimeColumn.S1KMK008_84 :
					check == 2 ? I18NText.getText("KMK008_176") : check == 3 ? I18NText.getText("KMK008_179") : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());

		data.put(RegistTimeColumn.S1KMK008_81, MasterCellData.builder()
                .columnId(RegistTimeColumn.S1KMK008_81)
                .value(check == 0 ? EnumAdaptor.convertToValueName(EnumAdaptor.valueOf(((BigDecimal)object).intValue(), StartingMonthType.class)).getLocalizedName() :
						check == 1 ? EnumAdaptor.convertToValueName(EnumAdaptor.valueOf(( islastDay == 0 ? (BigDecimal)object : 31).intValue(), ClosingDateType.class)).getLocalizedName() :
						check == 2 ? ((BigDecimal)object).intValue() == 1 ? I18NText.getText("KMK008_177") : I18NText.getText("KMK008_178") :
						check == 3 ? ((BigDecimal)object).intValue() == 1 ? I18NText.getText("KMK008_182") : I18NText.getText("KMK008_181") : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
		return MasterData.builder().rowData(data).build();
	}

	//sheet2
	@Override
	public List<MasterData> getDataExportSheet2() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_2.toString()).setParameter(1, cid);
		try {
			Object[] data = (Object[]) query.getSingleResult();
			for (int i = 0; i < data.length; i++) {
				datas.add(toDataSheet2(data[i],i));
			}
		} catch (Exception e) {
			for (int i = 0; i <= 2; i++) {
				datas.add(toDataEmptySheet2(i));
			}
		}

		return datas;
	}

	private MasterData toDataEmptySheet2(int checkRow) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S2KMK008_80, MasterCellData.builder()
				.columnId(RegistTimeColumn.S2KMK008_80)
				.value(checkRow == 0 ? RegistTimeColumn.S1KMK008_82 : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.HEADER_NONE1, MasterCellData.builder()
				.columnId(RegistTimeColumn.HEADER_NONE1)
				.value(checkRow == 0 ? RegistTimeColumn.S2Com_Employment : checkRow == 1 ? RegistTimeColumn.S2Com_Workplace : checkRow == 2 ? RegistTimeColumn.S2Com_Class : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.S2KMK008_81, MasterCellData.builder()
				.columnId(RegistTimeColumn.S2KMK008_81)
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataSheet2(Object object,int check) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S2KMK008_80, MasterCellData.builder()
				.columnId(RegistTimeColumn.S2KMK008_80)
				.value(check == 0 ? RegistTimeColumn.S2KMK008_203 : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.HEADER_NONE1, MasterCellData.builder()
				.columnId(RegistTimeColumn.HEADER_NONE1)
				.value(check == 0 ? RegistTimeColumn.S2Com_Employment : check == 1 ? RegistTimeColumn.S2Com_Workplace : check == 2 ? RegistTimeColumn.S2Com_Class : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(RegistTimeColumn.S2KMK008_81, MasterCellData.builder()
				.columnId(RegistTimeColumn.S2KMK008_81)
				.value(EnumAdaptor.convertToValueName(EnumAdaptor.valueOf(((BigDecimal)object).intValue(),UseClassificationAtr.class)).getLocalizedName())
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		return MasterData.builder().rowData(data).build();
	}

	//sheet3
	@Override
	public List<MasterData> getDataExportSheet3() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_3.toString()).setParameter(1, cid);
		try {
			Boolean data = (Boolean) query.getSingleResult();
			BigDecimal value = new BigDecimal(Boolean.valueOf(data).compareTo(false));
			datas.add(toDataSheet3(value));
		} catch (Exception e) {
			for (int i = 0; i < 1; i++) {
				datas.add(toDataEmptySheet3());
			}
		}

		return datas;
	}

	private MasterData toDataEmptySheet3() {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S3KMK008_80, MasterCellData.builder()
				.columnId(RegistTimeColumn.S3KMK008_80)
				.value(RegistTimeColumn.S3KMK008_204)
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.HEADER_NONE1, MasterCellData.builder()
				.columnId(RegistTimeColumn.HEADER_NONE1)
				.value(RegistTimeColumn.S3Com_Workplace)
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.S3KMK008_81, MasterCellData.builder()
				.columnId(RegistTimeColumn.S3KMK008_81)
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataSheet3(Object object) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S3KMK008_80, MasterCellData.builder()
				.columnId(RegistTimeColumn.S3KMK008_80)
				.value(RegistTimeColumn.S3KMK008_204)
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.HEADER_NONE1, MasterCellData.builder()
				.columnId(RegistTimeColumn.HEADER_NONE1)
				.value(RegistTimeColumn.S3Com_Workplace)
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(RegistTimeColumn.S3KMK008_81, MasterCellData.builder()
				.columnId(RegistTimeColumn.S3KMK008_81)
				.value(EnumAdaptor.valueOf(((BigDecimal)object).intValue(),DoWork.class).description)
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		return MasterData.builder().rowData(data).build();
	}

	private String formatTime(int source) {
		int hourPart = (source / 60);
		int minutePart = source % 60;
		String result = String.format("%d:%02d", hourPart, minutePart);
		return result;
	}

	//sheet4
	@Override
	public List<MasterData> getDataExportSheet4() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 0;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_4_8.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr==1);
		try {
			Object[] data = (Object[]) query.getSingleResult();
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet4(data,i,j));
					if (j == 0){
						j = j+1;
					}else if (j == 7){
						j = j+2;
					}else {
						j = j+3;
					}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6 ; i++) {
				datas.add(toDataEmptySheet4(i));
			}
		}
		return datas;
	}

	private MasterData toDataEmptySheet4(int check) {
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataSheet4(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(1, 4, 9, 12).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(2, 5, 7, 10, 13).contains(checkExist == 7 ? 7 : ++checkExist) ? formatTime(((BigDecimal)objects[param == 12 || param == 7 ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(3, 6, 8, 11, 14).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
				.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[0]).intValue())) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		return MasterData.builder().rowData(data).build();
	}

	private String getTexresource(int type){
		String value = "";
		switch (type) {
			case 0:
				value = "KMK008_190";
				break;
			case 1 :
				value = "KMK008_191";
				break;
			case 2 :
				value = "KMK008_192";
				break;
			case 3 :
				value = "KMK008_193";
				break;
			case 4 :
				value = "KMK008_194";
				break;
			case 5 :
				value = "KMK008_195";
				break;
			case 6 :
				value = "KMK008_196";
				break;
			case 7 :
				value = "KMK008_197";
				break;
			case 8 :
				value = "KMK008_198";
				break;
			case 9 :
				value = "KMK008_199";
				break;
			case 10 :
				value = "KMK008_200";
				break;
			case 11 :
				value = "KMK008_201";
				break;
			case 12 :
				value = "KMK008_202";
				break;
			default:
				break;
		}
		return value;
	}

	private String getColumnCommon(int rownum) {
		String value = "";
		switch (rownum) {
			case 0:
				value = I18NText.getText(RegistTimeColumn.KMK008_162);
				break;
			case 1 :
				value = I18NText.getText(RegistTimeColumn.KMK008_96);
				break;
			case 2 :
				value = I18NText.getText(RegistTimeColumn.KMK008_206);
				break;
			case 3 :
				value = I18NText.getText(RegistTimeColumn.KMK008_99);
				break;
			case 4 :
				value = I18NText.getText(RegistTimeColumn.KMK008_207);
				break;
			case 5 :
				value = I18NText.getText(RegistTimeColumn.KMK008_170);
				break;
			default:
				break;
		}
		return value;
	}

	//sheet5
	@Override
	public List<MasterData> getDataExportSheet5() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 0;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_5_9.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr == 1);

		List<Object[]> data =  query.getResultList();
		try {
			for (Object[] objects : data) {
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet5(objects,i,j));
					 if (j == 9){
						j = j+2;
					}else {
						j = j+3;
					}
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6; i++) {
				datas.add(toDataEmptySheet5(i));
			}
		}

		return datas;
	}

	private MasterData toDataSheet5(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S5KMK008_100), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S5KMK008_100))
				.value(check == 0 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S5KMK008_101), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S5KMK008_101))
				.value(check == 0 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(3, 6, 11, 14).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(4, 7, 9, 12, 15).contains(checkExist == 9 ? 9 : ++checkExist) ? formatTime(((BigDecimal)objects[param == 14 || param == 9 ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(5, 8, 10, 13,16).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal) objects[2]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataEmptySheet5(int rownum) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S5KMK008_100), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S5KMK008_100))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S5KMK008_101), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S5KMK008_101))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(getColumnCommon(rownum))
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value("")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();

	}

	//sheet6
	public List<MasterData> getDataExportSheet6() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 0;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_6_10.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr  == 1);

		List<Object[]> data =  query.getResultList();
		try {
			for (Object[] objects : data) {
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet6(objects,i,j));
					if (j == 9){
						j = j+2;
					}else {
						j = j+3;
					}
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6; i++) {
				datas.add(toDataEmptySheet6(i));
			}
		}

		return datas;
	}

	private MasterData toDataSheet6(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S6KMK008_102), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S6KMK008_102))
				.value(check == 0 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S6KMK008_103), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S6KMK008_103))
				.value(check == 0 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(3, 6, 11, 14).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(4, 7, 9, 12, 15).contains(checkExist == 9 ? 9 : ++checkExist) ? formatTime(((BigDecimal)objects[(param == 14 || param == 9) ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(5, 8, 10, 13,16).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[2]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataEmptySheet6(int rownum) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S6KMK008_102), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S6KMK008_102))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S6KMK008_103), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S6KMK008_103))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(getColumnCommon(rownum))
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value("")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();

	}

	//sheet7
	@Override
	public List<MasterData> getDataExportSheet7() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 0;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_7_11.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr == 1);

		List<Object[]> data =  query.getResultList();
		try {
			for (Object[] objects : data) {
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet7(objects,i,j));
					if (j == 9){
						j = j+2;
					}else {
						j = j+3;
					}
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6; i++) {
				datas.add(toDataEmptySheet7(i));
			}
		}

		return datas;
	}

	private MasterData toDataSheet7(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S7KMK008_104), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S7KMK008_104))
				.value(check == 0 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S7KMK008_105), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S7KMK008_105))
				.value(check == 0 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(3, 6, 11, 14).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(4, 7, 9, 12, 15).contains(checkExist == 9 ? 9 : ++checkExist) ? formatTime(((BigDecimal)objects[(param == 14 || param == 9) ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(5, 8, 10, 13,16).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[2]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	private MasterData toDataEmptySheet7(int rownum) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S7KMK008_104), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S7KMK008_104))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S7KMK008_105), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.S7KMK008_105))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(getColumnCommon(rownum))
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value("")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value("")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();

	}

	//sheet8
	@Override
	public List<MasterData> getDataExportSheet8() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 1;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_4_8.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr == 1);
		try {
			Object[] data = (Object[]) query.getSingleResult();
			int j = 0 ;
			for (int i = 0; i < 6; i++) {
				datas.add(toDataSheet8(data,i,j));
				if (j == 0){
					j = j+1;
				}else if (j == 7){
					j = j+2;
				}else {
					j = j+3;
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6 ; i++) {
				datas.add(toDataEmptySheet4(i));
			}
		}
		return datas;
	}

	private MasterData toDataSheet8(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S8KMK008_89), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.S8KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S8KMK008_92), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.S8KMK008_92))
				.value(Arrays.asList(1, 4, 9, 12).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(2, 5, 7, 10, 13).contains(checkExist == 7 ? 7 : ++checkExist) ? formatTime(((BigDecimal)objects[param == 12 || param == 7 ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(3, 6, 8, 11, 14).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[0]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	//sheet9
	@Override
	public List<MasterData> getDataExportSheet9() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 1;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_5_9.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr == 1);

		@SuppressWarnings("unchecked")
		List<Object[]> data =  query.getResultList();
		try {
			for (Object[] objects : data) {
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet9(objects,i,j));
					if (j == 9){
						j = j+2;
					}else {
						j = j+3;
					}
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6; i++) {
				datas.add(toDataEmptySheet5(i));
			}
		}

		return datas;
	}

	private MasterData toDataSheet9(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S9KMK008_100), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S9KMK008_100))
				.value(check == 0 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S9KMK008_101), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S9KMK008_101))
				.value(check == 0 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(3, 6, 11, 14).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(4, 7, 9, 12, 15).contains(checkExist == 9 ? 9 : ++checkExist) ? formatTime(((BigDecimal)objects[(param == 14 || param == 9) || param == 9 ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(5, 8, 10, 13,16).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[2]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	//sheet10
	@Override
	public List<MasterData> getDataExportSheet10() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 1;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_6_10.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr == 1);

		List<Object[]> data =  query.getResultList();
		try {
			for (Object[] objects : data) {
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet10(objects,i,j));
					if (j == 9){
						j = j+2;
					}else {
						j = j+3;
					}
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6; i++) {
				datas.add(toDataEmptySheet6(i));
			}
		}

		return datas;
	}

	private MasterData toDataSheet10(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S10KMK008_102), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.S10KMK008_102))
				.value(check == 0 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S10KMK008_103), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.S10KMK008_103))
				.value(check == 0 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(3, 6, 11, 14).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(4, 7, 9, 12, 15).contains(checkExist == 9 ? 9 : ++checkExist) ? formatTime(((BigDecimal)objects[(param == 14 || param == 9) ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(5, 8, 10, 13,16).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[2]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	//sheet11
	@Override
	public List<MasterData> getDataExportSheet11() {
		List<MasterData> datas = new ArrayList<>();
		String cid = AppContexts.user().companyId();
		int laborSystemAtr = 1;
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_7_11.toString()).
				setParameter("cid", cid).
				setParameter("laborSystemAtr", laborSystemAtr == 1);

		List<Object[]> data =  query.getResultList();
		try {
			for (Object[] objects : data) {
				int j = 0 ;
				for (int i = 0; i < 6; i++) {
					datas.add(toDataSheet11(objects,i,j));
					if (j == 9){
						j = j+2;
					}else {
						j = j+3;
					}
				}
			}
		} catch (Exception e) {
			for (int i = 0; i < 6; i++) {
				datas.add(toDataEmptySheet7(i));
			}
		}

		return datas;
	}

	private MasterData toDataSheet11(Object[] objects,int check,int param) {
		int checkExist = param;
		Map<String, MasterCellData> data = new HashMap<>();
		data.put(I18NText.getText(RegistTimeColumn.S11KMK008_104), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.S11KMK008_104))
				.value(check == 0 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.S11KMK008_105), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.S11KMK008_105))
				.value(check == 0 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_89), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_89))
				.value(check == 0 ? I18NText.getText(RegistTimeColumn.KMK008_162) : check == 1 ? I18NText.getText(RegistTimeColumn.KMK008_96) :
						check == 2 ? I18NText.getText(RegistTimeColumn.KMK008_206) : check == 3 ? I18NText.getText(RegistTimeColumn.KMK008_99) :
								check == 4 ? I18NText.getText(RegistTimeColumn.KMK008_207) : check == 5 ? I18NText.getText(RegistTimeColumn.KMK008_170) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(I18NText.getText(RegistTimeColumn.KMK008_92), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_92))
				.value(Arrays.asList(3, 6, 11, 14).contains(checkExist) ? formatTime(((BigDecimal)objects[param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_90), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_90))
				.value(Arrays.asList(4, 7, 9, 12, 15).contains(checkExist == 9 ? 9 : ++checkExist) ? formatTime(((BigDecimal)objects[(param == 14 || param == 9) ? param : ++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_91), MasterCellData.builder()
				.columnId(I18NText.getText(RegistTimeColumn.KMK008_91))
				.value(Arrays.asList(5, 8, 10, 13,16).contains(++checkExist) ? formatTime(((BigDecimal)objects[++param]).intValue()) : "-")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(I18NText.getText(RegistTimeColumn.KMK008_205), MasterCellData.builder()
			.columnId(I18NText.getText(RegistTimeColumn.KMK008_205))
			.value(check == 0 ? I18NText.getText(getTexresource(((BigDecimal)objects[2]).intValue())) : "-")
			.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
			.build());
		return MasterData.builder().rowData(data).build();
	}

	//sheet12
	public List<MasterData> getDataExportSheet12(GeneralDate startDate, GeneralDate endDate) {
		List<MasterData> datas = new ArrayList<>();
		val startYM = startDate.yearMonth().v();
		val endYM = endDate.yearMonth().v();
		int startY = startDate.year();
		int endY = endDate.year();
		if(!endYM.toString().substring(4, endYM.toString().length()).equals(END_MONTH))
			endY = endY-1;
		String cid = AppContexts.user().companyId();
		Query query = getEntityManager().createNativeQuery(SQL_EXPORT_SHEET_12.toString()).
				setParameter("cid", cid).
				setParameter("startY", startY).
				setParameter("endY", endY).
				setParameter("startYM", startYM).
				setParameter("endYM", endYM);
		@SuppressWarnings("unchecked")
		List<Object[]> data =  query.getResultList();
		for (int i = 0; i < data.size(); i++) {
			datas.add(toDataSheet12(data.get(i)));
		}
		return datas;
	}

	private MasterData toDataSheet12(Object[] objects) {
		Map<String,MasterCellData> data = new HashMap<>();
		data.put(RegistTimeColumn.S12KMK008_106, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_106)
				.value(Integer.valueOf(objects[9].toString()) == 1 ? objects[0] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());
		data.put(RegistTimeColumn.S12KMK008_107, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_107)
				.value(Integer.valueOf(objects[9].toString()) == 1 ? objects[1] : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
				.build());

		data.put(RegistTimeColumn.S12KMK008_109, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_109)
				.value(  objects[2].toString().equals("999913") ? "" : objects[2] != null  ?  ((BigDecimal)objects[2]).toString().substring(0, 4) + "/" + ((BigDecimal)objects[2]).toString().substring(4, ((BigDecimal)objects[2]).toString().length()) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());

		data.put(RegistTimeColumn.S12KMK008_110, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_110)
				.value(objects[3] != null  ? formatTime(((BigDecimal)objects[3]).intValue()) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(RegistTimeColumn.S12KMK008_111, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_111)
				.value(objects[4] != null ? formatTime(((BigDecimal)objects[4]).intValue()) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());

		data.put(RegistTimeColumn.S12KMK008_112, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_112)
				.value(objects[5])
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(RegistTimeColumn.S12KMK008_113, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_113)
				.value(objects[7] != null  ? formatTime(((BigDecimal)objects[7]).intValue()) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());
		data.put(RegistTimeColumn.S12KMK008_114, MasterCellData.builder()
				.columnId(RegistTimeColumn.S12KMK008_114)
				.value(objects[8] != null  ? formatTime(((BigDecimal)objects[8]).intValue()) : "")
				.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
				.build());

		return MasterData.builder().rowData(data).build();
	}

}
