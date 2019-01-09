package nts.uk.file.at.infra.worktype;


import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.file.at.app.export.worktype.PreparationBeforeApplyRepository;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Stateless
public class JpaPreparationBeforeApplyRepository extends JpaRepository implements PreparationBeforeApplyRepository {
	
	@Override
	public List<Object[]> getChangePerInforDefinitionToExport(String cid) {
		List<Object[]> resultQuery = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT   temp.ROW_NUMBER, ");
		sql.append("           temp.CLOSURE_ID,");
		sql.append("           temp.CLOSURE_NAME,");
		sql.append("           temp.USE_ATR,");
		sql.append("           temp.DEADLINE_CRITERIA,");
		sql.append("           temp.DEADLINE,");
		sql.append("           asg.BASE_DATE_FLG, ");
		sql.append("           asg.APP_CONTENT_CHANGE_FLG,");
		sql.append("           temp.MHI_SUBJECT,");
		sql.append("           temp.MHI_CONTENT,");
		sql.append("           temp.MOI_SUBJECT,");
		sql.append("           temp.MOI_CONTENT,");
		sql.append("           temp.IS_CONCURRENTLY,");
		sql.append("           temp.PRINCIPAL_APPROVAL_FLG,");
		sql.append("           temp.CONTENT,");
		sql.append("           temp.MAIL_TITLE,");
		sql.append("           temp.MAIL_BODY,");
		sql.append("           asg.APP_REASON_DISP_ATR,");
		sql.append("           asg.DISPLAY_PRE_POST_FLG,");
		sql.append("           pas.ROW_NUMBER,");
		sql.append("           pas.APP_TYPE,");
		sql.append("           asg.ROW_NUMBER,");
		sql.append("           asg.OT_RESTRICT_PRE_DAY,");
		sql.append("           asg.RETRICT_PRE_METHOD_CHECK_FLG,");
		sql.append("           asg.PRE_OTWORK_TIME,");
		sql.append("           asg.NORMAL_OT_TIME,");
		sql.append("           asg.RETRICT_PRE_TIMEDAY,");
		sql.append("           asg.APP_TYPE,");
		sql.append("           asg.RETRICT_PRE_USE_FLG,");
		sql.append("           asg.RETRICT_PRE_DAY,");
		sql.append("           asg.RETRICT_POST_ALLOW_FUTURE_FLG,");
		sql.append("           asg.SEND_MAIL_WHEN_REGISTER_FLG,");
		sql.append("           asg.SEND_MAIL_WHEN_APPROVAL_FLG,");
		sql.append("           asg.DISPLAY_REASON_FLG,");
		sql.append("           asg.PRE_POST_CAN_CHANGE_FLG,");
		sql.append("           adn.ROW_NUMBER,");
		sql.append("           adn.APP_TYPE,");
		sql.append("           adn.DISP_NAME,");
		sql.append("           dr.ROW_NUMBER,");
		sql.append("           dr.TYPE_OF_LEAVE_APP,");
		sql.append("           dr.DISPLAY_FIXED_REASON,");
		sql.append("           dr.DISPLAY_APP_REASON,");
		sql.append("           asg.REQUIRE_APP_REASON_FLG,");
		sql.append("           asg.APP_OVERTIME_NIGHT_FLG,");
		sql.append("           asg.APP_ACT_CONFIRM_FLG,");
		sql.append("           asg.APP_ACT_MONTH_CONFIRM_FLG,");
		sql.append("           asg.APP_END_WORK_FLG,");
		sql.append("           asg.APP_ACT_LOCK_FLG,");
		sql.append("           asg.MANUAL_SEND_MAIL_ATR,");
		sql.append("     		temp.URL_EMBEDDED,");
		sql.append("			asg.PRE_POST_INIT_ATR");
		sql.append("       FROM  ");
		sql.append("        (SELECT ");
		sql.append("            ROW_NUMBER() OVER (ORDER BY clo.CLOSURE_ID ASC) AS ROW_NUMBER,");
		sql.append("            clo.CLOSURE_ID,  ");
		sql.append("            cloh.CLOSURE_NAME,");
		sql.append("            clo.CID,");
		sql.append("            ad.USE_ATR,");
		sql.append("            ad.DEADLINE_CRITERIA,");
		sql.append("            ad.DEADLINE,");
		sql.append("            ue.URL_EMBEDDED,");
		sql.append("            mhi.SUBJECT AS MHI_SUBJECT,");
		sql.append("            mhi.CONTENT AS MHI_CONTENT,");
		sql.append("            moi.SUBJECT AS MOI_SUBJECT,");
		sql.append("            moi.CONTENT AS MOI_CONTENT,");
		sql.append("            jas.IS_CONCURRENTLY,");
		sql.append("            was.PRINCIPAL_APPROVAL_FLG,");
		sql.append("            ate.CONTENT,");
		sql.append("            krm.MAIL_TITLE,");
		sql.append("            krm.MAIL_BODY");
		sql.append("          FROM ");
		sql.append("            (SELECT ROW_NUMBER() OVER (ORDER BY CLOSURE_ID ASC) AS ROW_NUMBER,");
		sql.append("             CLOSURE_ID, ");
		sql.append("             CID, ");
		sql.append("             CLOSURE_MONTH");
		sql.append("          FROM KCLMT_CLOSURE ");
		sql.append("          WHERE CID = ?cid) clo");
		sql.append("          INNER JOIN  KCLMT_CLOSURE_HIST cloh ");
		sql.append("               ON clo.CID = cloh.CID ");
		sql.append("               AND clo.CLOSURE_ID = cloh.CLOSURE_ID ");
		sql.append("               AND clo.CLOSURE_MONTH <= cloh.END_YM");
		sql.append("               AND clo.CLOSURE_MONTH >= cloh.STR_YM");
		sql.append("          FULL JOIN ");
		sql.append("               (SELECT CID, USE_ATR, DEADLINE_CRITERIA, DEADLINE, CLOSURE_ID");
		sql.append("               FROM KRQST_APP_DEADLINE ");
		sql.append("               WHERE CID = ?cid) ad");
		sql.append("               ON clo.CID = ad.CID AND clo.CLOSURE_ID = ad.CLOSURE_ID");
		sql.append("          FULL JOIN ");
		sql.append("               (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   URL_EMBEDDED");
		sql.append("               FROM KRQST_URL_EMBEDDED ");
		sql.append("               WHERE CID = ?cid) ue ");
		sql.append("               ON ue.ROW_NUMBER = clo.ROW_NUMBER");
		sql.append("          FULL JOIN ");
		sql.append("               (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   CONTENT");
		sql.append("               FROM KRQMT_APPROVAL_TEMPLATE ");
		sql.append("               WHERE CID = ?cid) ate ");
		sql.append("               ON ate.ROW_NUMBER = clo.ROW_NUMBER");
		sql.append("          FULL JOIN ");
		sql.append("               (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   SUBJECT, ");
		sql.append("                   CONTENT ");
		sql.append("               FROM KRQMT_MAIL_HD_INSTRUCTION ");
		sql.append("               WHERE CID = ?cid) mhi ");
		sql.append("               ON mhi.ROW_NUMBER = clo.ROW_NUMBER");
		sql.append("          FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   SUBJECT,");
		sql.append("                   CONTENT ");
		sql.append("               FROM KRQMT_MAIL_OT_INSTRUCTION");
		sql.append("               WHERE CID = ?cid) moi");
		sql.append("               ON moi.ROW_NUMBER = clo.ROW_NUMBER");
		sql.append("          FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   MAIL_TITLE, ");
		sql.append("                   MAIL_BODY");
		sql.append("               FROM KRQST_REMAND_MAIL");
		sql.append("               WHERE CID = ?cid) krm");
		sql.append("               ON krm.ROW_NUMBER = clo.ROW_NUMBER");
		sql.append("          FULL JOIN ");
		sql.append("               (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   IS_CONCURRENTLY");
		sql.append("               FROM WWFST_JOB_ASSIGN_SET ");
		sql.append("               WHERE CID = ?cid) jas ");
		sql.append("               ON jas.ROW_NUMBER = clo.ROW_NUMBER");
		sql.append("          FULL JOIN ");
		sql.append("               (SELECT ROW_NUMBER() OVER (ORDER BY CID) AS ROW_NUMBER,");
		sql.append("                   PRINCIPAL_APPROVAL_FLG");
		sql.append("               FROM WWFST_APPROVAL_SETTING ");
		sql.append("               WHERE CID = ?cid)was ");
		sql.append("               ON was.ROW_NUMBER = clo.ROW_NUMBER   ");
		sql.append("            ) temp");
		sql.append("           FULL JOIN ");
		sql.append("                (SELECT APP_TYPE, ");
		sql.append("                    ROW_NUMBER() OVER (ORDER BY APP_TYPE ) AS ROW_NUMBER");
		sql.append("                FROM KRQST_PROXY_APP_SET");
		sql.append("                WHERE  CID = ?cid) pas ");
		sql.append("                ON pas.ROW_NUMBER = temp.ROW_NUMBER");
		sql.append("           FULL JOIN ");
		sql.append("                ( SELECT CID,");
		sql.append("                     APP_TYPE ,");
		sql.append("                     DISP_NAME, ");
		sql.append("                     ROW_NUMBER() OVER (ORDER BY APP_TYPE ) AS ROW_NUMBER");
		sql.append("                 FROM KRQMT_APP_DISP_NAME");
		sql.append("                 WHERE APP_TYPE != 5  AND CID = ?cid");
		sql.append("                ) adn ");
		sql.append("             ON adn.ROW_NUMBER = temp.ROW_NUMBER");
		sql.append("           FULL JOIN (SELECT TYPE_OF_LEAVE_APP,");
		sql.append("                 DISPLAY_FIXED_REASON,");
		sql.append("                 ROW_NUMBER() OVER (ORDER BY TYPE_OF_LEAVE_APP ) AS ROW_NUMBER,");
		sql.append("                 DISPLAY_APP_REASON");
		sql.append("                 FROM KRQST_DISPLAY_REASON ");
		sql.append("                 WHERE CID = ?cid) ");
		sql.append("                 dr ON dr.ROW_NUMBER = temp.ROW_NUMBER");
		sql.append("      FULL JOIN ");
		sql.append("       (SELECT ");
		sql.append("          ROW_NUMBER() OVER (ORDER BY APP_TYPE ) AS ROW_NUMBER,");
		sql.append("          BASE_DATE_FLG, ");
		sql.append("                   APP_CONTENT_CHANGE_FLG, ");
		sql.append("                   APP_REASON_DISP_ATR, ");
		sql.append("                   DISPLAY_PRE_POST_FLG, ");
		sql.append("                   REQUIRE_APP_REASON_FLG, ");
		sql.append("                   APP_OVERTIME_NIGHT_FLG,");
		sql.append("                   APP_ACT_CONFIRM_FLG,");
		sql.append("                   APP_ACT_MONTH_CONFIRM_FLG,");
		sql.append("                   APP_END_WORK_FLG,");
		sql.append("                   APP_ACT_LOCK_FLG,");
		sql.append("                   MANUAL_SEND_MAIL_ATR,");
		sql.append("           OT_RESTRICT_PRE_DAY,");
		sql.append("           RETRICT_PRE_METHOD_CHECK_FLG,");
		sql.append("           PRE_OTWORK_TIME,");
		sql.append("           NORMAL_OT_TIME,");
		sql.append("           RETRICT_PRE_TIMEDAY,");
		sql.append("           APP_TYPE,");
		sql.append("           RETRICT_PRE_USE_FLG,");
		sql.append("           RETRICT_PRE_DAY,");
		sql.append("           RETRICT_POST_ALLOW_FUTURE_FLG,");
		sql.append("           SEND_MAIL_WHEN_REGISTER_FLG,");
		sql.append("           SEND_MAIL_WHEN_APPROVAL_FLG,");
		sql.append("           DISPLAY_REASON_FLG,");
		sql.append("           PRE_POST_CAN_CHANGE_FLG,");
		sql.append("		   PRE_POST_INIT_ATR");
		sql.append("        FROM");
		sql.append("           (SELECT ");
		sql.append("               CID,");
		sql.append("               BASE_DATE_FLG, ");
		sql.append("               APP_CONTENT_CHANGE_FLG, ");
		sql.append("               APP_REASON_DISP_ATR, ");
		sql.append("               DISPLAY_PRE_POST_FLG, ");
		sql.append("               REQUIRE_APP_REASON_FLG, ");
		sql.append("               APP_OVERTIME_NIGHT_FLG,");
		sql.append("               APP_ACT_CONFIRM_FLG,");
		sql.append("               APP_ACT_MONTH_CONFIRM_FLG,");
		sql.append("               APP_END_WORK_FLG,");
		sql.append("               APP_ACT_LOCK_FLG,");
		sql.append("               MANUAL_SEND_MAIL_ATR");
		sql.append("           FROM KRQST_APPLICATION_SETTING ");
		sql.append("           WHERE CID = ?cid) ag");
		sql.append("          INNER JOIN ");
		sql.append("               ( SELECT CID,");
		sql.append("                    OT_RESTRICT_PRE_DAY,");
		sql.append("                    RETRICT_PRE_METHOD_CHECK_FLG,");
		sql.append("                    PRE_OTWORK_TIME,");
		sql.append("                    NORMAL_OT_TIME,");
		sql.append("                    RETRICT_PRE_TIMEDAY,");
		sql.append("                    APP_TYPE,");
		sql.append("                    RETRICT_PRE_USE_FLG,");
		sql.append("                    RETRICT_PRE_DAY,");
		sql.append("                    RETRICT_POST_ALLOW_FUTURE_FLG,");
		sql.append("                    SEND_MAIL_WHEN_REGISTER_FLG,");
		sql.append("                    SEND_MAIL_WHEN_APPROVAL_FLG,");
		sql.append("                    DISPLAY_REASON_FLG,");
		sql.append("                    PRE_POST_CAN_CHANGE_FLG,");
		sql.append("					PRE_POST_INIT_ATR");
		sql.append("                FROM KRQST_APP_TYPE_DISCRETE");
		sql.append("                WHERE APP_TYPE NOT IN (3, 5, 8, 11, 12, 13, 14) AND  CID = ?cid");
		sql.append("                ) ad ON ag.CID = ad.CID ) asg");
		sql.append("         ON asg.ROW_NUMBER = temp.ROW_NUMBER");

		try {
			resultQuery = (List<Object[]>) this.getEntityManager().createNativeQuery(sql.toString())
					.setParameter("cid", cid)
					.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
		return resultQuery;
	}
	@Override
	public List<Object[]> getExtraData(String cid) {
		List<Object[]> resultQuery = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("      aos.WORKTYPE_PERMISSION_FLAG, ");
		sql.append("      aos.FLEX_EXCESS_USE_SET_ATR,");
		sql.append("      rac.BONUS_TIME_DISPLAY_ATR,");
		sql.append("      rac.DIVERGENCE_REASON_FORM_ATR,");
		sql.append("      rac.DIVERGENCE_REASON_INPUT_ATR,");
		sql.append("      rac.PERFORMANCE_DISPLAY_ATR,");
		sql.append("      rac.PRE_DISPLAY_ATR,");
		sql.append("      rac.CAL_OVERTIME_DISPLAY_ATR,");
		sql.append("      rac.EXTRATIME_DISPLAY_ATR,");
		sql.append("      rac.PRE_EXCESS_DISPLAY_SETTING,");
		sql.append("      rac.PERFORMANCE_EXCESS_ATR,");
		sql.append("      was.OVERRIDE_SET,");
		sql.append("      rac.EXTRATIME_EXCESS_ATR,");
		sql.append("      rac.APP_DATE_CONTRADICTION_ATR,");
		sql.append("      ar.APP_TYPE,");
		sql.append("      ar.REASON_TEMP,");
		sql.append("      ar.DEFAULT_FLG,");
		sql.append("      hae.WRK_HOURS_USE_ATR,");
		sql.append("      hae.DISPLAY_UNSELECT,");
		sql.append("      hae.CHANGE_WRK_HOURS,");
		sql.append("      hae.USE_TIME_YEAR_HD,");
		sql.append("      hae.USE_60H_HOLIDAY,");
		sql.append("      hae.USE_TIME_GENER_HD,");
		sql.append("      hae.ACTUAL_DISPLAY_ATR,");
		sql.append("      hae.APPLI_DATE_CONTRA_ATR,");
		sql.append("      hae.CONCHECK_OUTLEGAL_LAW,");
		sql.append("      hae.CONCHECK_DATE_RELEASEDATE,");
		sql.append("      hae.CKUPER_LIMIT_HALFDAY_HD,");
		sql.append("      hae.REGIS_NUM_YEAR_OFF,");
		sql.append("      hae.REGIS_SHORT_LOST_HD,");
		sql.append("      hae.REGIS_SHORT_RESER_YEAR,");
		sql.append("      hae.REGIS_LACK_PUBLIC_HD,");
		sql.append("      hae.REGIS_INSUFF_NUMOFREST,");
		sql.append("      hae.PRIORITY_DIGESTION_ATR,");
		sql.append("      hae.YEAR_HD_NAME,");
		sql.append("      hae.OBSTACLE_NAME,");
		sql.append("      hae.ABSENTEEISM_NAME,");
		sql.append("      hae.SPECIAL_VACATION_NAME,");
		sql.append("      hae.YEAR_RESIG_NAME,");
		sql.append("      hae.HD_NAME,");
		sql.append("      hae.TIME_DIGEST_NAME,");
		sql.append("      hae.FURIKYU_NAME,");
		sql.append("      awc.INIT_DISPLAY_WORKTIME,");
		sql.append("      awc.WORK_CHANGE_TIME_ATR,");
		sql.append("      awc.EXCLUDE_HOLIDAY,");
		sql.append("      awc.COMMENT_CONTENT1,");
		sql.append("      awc.COMMENT_FONT_COLOR1,");
		sql.append("      awc.COMMENT_FONT_WEIGHT1,");
		sql.append("      awc.COMMENT_CONTENT2,");
		sql.append("      awc.COMMENT_FONT_COLOR2,");
		sql.append("      awc.COMMENT_FONT_WEIGHT2,");
		sql.append("      awc.DISPLAY_RESULT_ATR,");
		sql.append("      gbd.WORK_TYPE,");
		sql.append("      gbd.WORK_CHANGE_FLG,");
		sql.append("      gbd.COMMENT_CONTENT1,");
		sql.append("       gbd.COMMENT_FONT_COLOR1,");
		sql.append("      gbd.COMMENT_FONT_WEIGHT1,");
		sql.append("      gbd.COMMENT_CONTENT2,");
		sql.append("      gbd.COMMENT_FONT_COLOR2,");
		sql.append("      gbd.COMMENT_FONT_WEIGHT2,");
		sql.append("      gbd.PERFORMANCE_DISPLAY_ATR,");
		sql.append("      gbd.CONTRADITION_CHECK_ATR,");
		sql.append("      gbd.LATE_LEAVE_EARLY_SETTING_ATR,");
		sql.append("      rac.APP_TYPE,");
		sql.append("      was.TYPES_OF_PAID_LEAVE,");
		sql.append("      was.WORK_CHANGE_SET,");
		sql.append("      was.TIME_INITIAL_DISP,");
		sql.append("      wrs.CHECK_UPLIMIT_HALFDAY_HD,");
		sql.append("      was.CHECK_NO_HD_TIME,");
		sql.append("      was.CALCULATION_STAMP_MISS,");
		sql.append("      wrs.DEFERRED_WORKTIME_SELECT,");
		sql.append("      wrs.PERMISSION_DIVISION,");
		sql.append("      wrs.DEFERRED_COMMENT,");
		sql.append("      wrs.DEFERRED_LETTER_COLOR,");
		sql.append("      wrs.DEFERRED_BOLD,");
		sql.append("      wrs.PICKUP_COMMENT,");
		sql.append("      wrs.PICKUP_LETTER_COLOR,");
		sql.append("      wrs.PICKUP_BOLD,");
		sql.append("      wrs.USE_ATR,");
		sql.append("      wrs.SIMUL_APPLI_REQUIRE,");
		sql.append("      wrs.APPLI_DATE_CONTRAC,");
		sql.append("      wrs.LETTER_SUPER_LEAVE,");
		sql.append("      acs.SHOW_WKP_NAME_BELONG,");
		sql.append("      asg.APP_REASON_DISP_ATR,");
		sql.append("      asg.WARNING_DATE_DISP_ATR,");
		sql.append("      asg.OT_ADVANCE_DISP_ATR,");
		sql.append("      asg.HW_ADVANCE_DISP_ATR,");
		sql.append("      asg.ADVANCE_EXCESS_MESS_DISP_ATR,");
		sql.append("      asg.OT_ACTUAL_DISP_ATR,");
		sql.append("      asg.HW_ACTUAL_DISP_ATR,");
		sql.append("      asg.ACTUAL_EXCESS_MESS_DISP_ATR,");
		sql.append("      asg.REFLEC_TIMEOF_SCHEDULED,");
		sql.append("      asg.PRIORITY_TIME_REFLECT_FLG,");
		sql.append("      asg.ATTENDENT_TIME_REFLECT_FLG,");
		sql.append("      asg.SCHE_REFLECT_FLG,");
		sql.append("      asg.CLASSIFY_SCHE_ACHIEVE_SAMEATR,");
		sql.append("      asg.SCHEDULE_CONFIRM_ATR,");
		sql.append("      aos.POST_TYPESIFT_REFLECT_FLG,");
		sql.append("      aos.REST_ATR,");
		sql.append("      asg.ACHIEVEMENT_CONFIRM_ATR,");
		sql.append("      aos.PRE_TYPE_SIFT_REFLECT_FLG,");
		sql.append("      aos.POST_BREAK_REFLECT_FLG,");
		sql.append("      was.REST_TIME,");
		sql.append("      was.WORK_TIME,");
		sql.append("      was.BREAK_TIME,");
		sql.append("      aos.PRIORITY_STAMP_SET_ATR,");
		sql.append("      aos.PRE_OVERTIME_REFLECT_FLG,");
		sql.append("      aos.POST_WORKTIME_REFLECT_FLG");
		sql.append("   FROM ");
		sql.append("      (SELECT ROW_NUMBER() OVER (ORDER BY DISPORDER ASC) AS ROW_NUMBER,");
		sql.append("            APP_TYPE,");
		sql.append("            REASON_TEMP,");
		sql.append("            DEFAULT_FLG");
		sql.append("            FROM KRQST_APP_REASON");
		sql.append("            WHERE CID = ?cid");
		sql.append("            ) ar");
		sql.append("   FULL JOIN          ");
		sql.append("      (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("          CID, ");
		sql.append("          WORKTYPE_PERMISSION_FLAG, ");
		sql.append("          FLEX_EXCESS_USE_SET_ATR,");
		sql.append("          POST_TYPESIFT_REFLECT_FLG,");
		sql.append("          REST_ATR,");
		sql.append("          PRE_TYPE_SIFT_REFLECT_FLG,");
		sql.append("          POST_BREAK_REFLECT_FLG,");
		sql.append("          PRIORITY_STAMP_SET_ATR,");
		sql.append("          PRE_OVERTIME_REFLECT_FLG,");
		sql.append("          POST_WORKTIME_REFLECT_FLG");
		sql.append("      FROM KRQST_APP_OVERTIME_SET ");
		sql.append("      WHERE CID = ?cid  ");
		sql.append("      ) aos ");
		sql.append("     ON ar.ROW_NUMBER = aos.ROW_NUMBER");
		sql.append("   FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY APP_TYPE ASC) AS ROW_NUMBER,");
		sql.append("            APP_TYPE,");
		sql.append("            BONUS_TIME_DISPLAY_ATR,");
		sql.append("            DIVERGENCE_REASON_FORM_ATR,");
		sql.append("            DIVERGENCE_REASON_INPUT_ATR,");
		sql.append("            PERFORMANCE_DISPLAY_ATR,");
		sql.append("            PRE_DISPLAY_ATR,");
		sql.append("            CAL_OVERTIME_DISPLAY_ATR,");
		sql.append("            EXTRATIME_DISPLAY_ATR,");
		sql.append("            PRE_EXCESS_DISPLAY_SETTING,");
		sql.append("            PERFORMANCE_EXCESS_ATR,");
		sql.append("            EXTRATIME_EXCESS_ATR,");
		sql.append("            APP_DATE_CONTRADICTION_ATR");
		sql.append("        FROM KRQST_OT_REST_APP_COM_SET ");
		sql.append("        WHERE CID = ?cid   ");
		sql.append("        ) rac ");
		sql.append("     ON rac.ROW_NUMBER = ar.ROW_NUMBER ");
		sql.append("   FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("        OVERRIDE_SET,");
		sql.append("        TYPES_OF_PAID_LEAVE,");
		sql.append("        WORK_CHANGE_SET,");
		sql.append("        TIME_INITIAL_DISP,");
		sql.append("        CHECK_NO_HD_TIME,");
		sql.append("        CALCULATION_STAMP_MISS,");
		sql.append("        REST_TIME,");
		sql.append("        WORK_TIME,");
		sql.append("        BREAK_TIME");
		sql.append("        FROM KRQST_WITHDRAWAL_APP_SET ");
		sql.append("        WHERE CID = ?cid");
		sql.append("        )was ");
		sql.append("     ON was.ROW_NUMBER = ar.ROW_NUMBER");
		sql.append("   FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("            INIT_DISPLAY_WORKTIME,");
		sql.append("            WORK_CHANGE_TIME_ATR,");
		sql.append("            EXCLUDE_HOLIDAY,");
		sql.append("            COMMENT_CONTENT1,");
		sql.append("            COMMENT_FONT_COLOR1,");
		sql.append("            COMMENT_FONT_WEIGHT1,");
		sql.append("            COMMENT_CONTENT2,");
		sql.append("            COMMENT_FONT_COLOR2,");
		sql.append("            COMMENT_FONT_WEIGHT2,");
		sql.append("            DISPLAY_RESULT_ATR");
		sql.append("        FROM KRQST_APP_WORK_CHANGE_SET");
		sql.append("        WHERE CID = ?cid) awc");
		sql.append("     ON awc.ROW_NUMBER = ar.ROW_NUMBER");
		sql.append("   FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("        WRK_HOURS_USE_ATR,");
		sql.append("        DISPLAY_UNSELECT,");
		sql.append("        CHANGE_WRK_HOURS,");
		sql.append("        USE_TIME_YEAR_HD,");
		sql.append("        USE_60H_HOLIDAY,");
		sql.append("        USE_TIME_GENER_HD,");
		sql.append("        ACTUAL_DISPLAY_ATR,");
		sql.append("        APPLI_DATE_CONTRA_ATR,");
		sql.append("        CONCHECK_OUTLEGAL_LAW,");
		sql.append("        CONCHECK_DATE_RELEASEDATE,");
		sql.append("        CKUPER_LIMIT_HALFDAY_HD,");
		sql.append("        REGIS_NUM_YEAR_OFF,");
		sql.append("        REGIS_SHORT_LOST_HD,");
		sql.append("        REGIS_SHORT_RESER_YEAR,");
		sql.append("        REGIS_LACK_PUBLIC_HD,");
		sql.append("        REGIS_INSUFF_NUMOFREST,");
		sql.append("        PRIORITY_DIGESTION_ATR,");
		sql.append("        YEAR_HD_NAME,");
		sql.append("        OBSTACLE_NAME,");
		sql.append("        ABSENTEEISM_NAME,");
		sql.append("        SPECIAL_VACATION_NAME,");
		sql.append("        YEAR_RESIG_NAME,");
		sql.append("        HD_NAME,");
		sql.append("        TIME_DIGEST_NAME,");
		sql.append("        FURIKYU_NAME");
		sql.append("      FROM KRQST_HD_APP_SET");
		sql.append("      WHERE CID = ?cid");
		sql.append("        ) hae");
		sql.append("    ON hae.ROW_NUMBER = ar.ROW_NUMBER");
		sql.append(" FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("          WORK_TYPE,");
		sql.append("          WORK_CHANGE_FLG,");
		sql.append("          COMMENT_CONTENT1,");
		sql.append("          COMMENT_FONT_COLOR1,");
		sql.append("          COMMENT_FONT_WEIGHT1,");
		sql.append("          COMMENT_CONTENT2,");
		sql.append("          COMMENT_FONT_COLOR2,");
		sql.append("          COMMENT_FONT_WEIGHT2,");
		sql.append("          PERFORMANCE_DISPLAY_ATR,");
		sql.append("          CONTRADITION_CHECK_ATR,");
		sql.append("          LATE_LEAVE_EARLY_SETTING_ATR");
		sql.append("        FROM KRQST_GO_BACK_DIRECT_SET");
		sql.append("        WHERE CID = ?cid) gbd");
		sql.append("     ON gbd.ROW_NUMBER = ar.ROW_NUMBER");
		sql.append(" FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("          CHECK_UPLIMIT_HALFDAY_HD,");
		sql.append("          DEFERRED_WORKTIME_SELECT,");
		sql.append("          PERMISSION_DIVISION,");
		sql.append("          PICKUP_COMMENT,");
		sql.append("          PICKUP_LETTER_COLOR,");
		sql.append("          DEFERRED_BOLD,");
		sql.append("          DEFERRED_COMMENT,");
		sql.append("          DEFERRED_LETTER_COLOR,");
		sql.append("          PICKUP_BOLD,");
		sql.append("          USE_ATR,");
		sql.append("          SIMUL_APPLI_REQUIRE,");
		sql.append("          APPLI_DATE_CONTRAC,");
		sql.append("          LETTER_SUPER_LEAVE");
		sql.append("       FROM KRQST_WITHDRAWAL_REQ_SET");
		sql.append("       WHERE CID = ?cid) wrs");
		sql.append("     ON wrs.ROW_NUMBER = ar.ROW_NUMBER ");
		sql.append("  FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("           SHOW_WKP_NAME_BELONG");
		sql.append("       FROM KRQST_APP_COMMON_SET");
		sql.append("       WHERE CID = ?cid) acs");
		sql.append("     ON acs.ROW_NUMBER = ar.ROW_NUMBER  ");
		sql.append("  FULL JOIN (SELECT ROW_NUMBER() OVER (ORDER BY CID ASC) AS ROW_NUMBER,");
		sql.append("           APP_REASON_DISP_ATR,");
		sql.append("           WARNING_DATE_DISP_ATR,");
		sql.append("           OT_ADVANCE_DISP_ATR,");
		sql.append("           HW_ADVANCE_DISP_ATR,");
		sql.append("           ADVANCE_EXCESS_MESS_DISP_ATR,");
		sql.append("           OT_ACTUAL_DISP_ATR,");
		sql.append("           HW_ACTUAL_DISP_ATR,");
		sql.append("           ACTUAL_EXCESS_MESS_DISP_ATR,");
		sql.append("           REFLEC_TIMEOF_SCHEDULED,");
		sql.append("           PRIORITY_TIME_REFLECT_FLG,");
		sql.append("           ATTENDENT_TIME_REFLECT_FLG,");
		sql.append("           SCHE_REFLECT_FLG,");
		sql.append("           CLASSIFY_SCHE_ACHIEVE_SAMEATR,");
		sql.append("           SCHEDULE_CONFIRM_ATR,");
		sql.append("           ACHIEVEMENT_CONFIRM_ATR");
		sql.append("       FROM KRQST_APPLICATION_SETTING");
		sql.append("       WHERE CID = ?cid) asg");
		sql.append("     ON asg.ROW_NUMBER = ar.ROW_NUMBER");

		try {
			resultQuery = (List<Object[]>) this.getEntityManager().createNativeQuery(sql.toString())
					.setParameter("cid", cid)
					.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
		return resultQuery;
	}

	private java.sql.Date conVertToDateSQL(String baseDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		java.sql.Date sqlDate = null;
		try {
			date = format.parse(baseDate);
			sqlDate = new java.sql.Date(date.getTime());
		} catch (ParseException e) {
			return null;
		}
		return sqlDate;
	}

	@Override
	public List<Object[]> getJob(String cid, String baseDate){
		List<Object[]> resultQuery = null;
		StringBuilder selectJob = new StringBuilder();
		selectJob.append("SELECT k.JOB_CD, w.JOB_NAME, k.SEARCH_SET_FLG ");
		selectJob.append("FROM (");
		selectJob.append("SELECT HIST_ID, JOB_ID, CID ");
		selectJob.append("FROM BSYMT_JOB_HIST ");
		selectJob.append("WHERE END_DATE >= ?baseDate AND CID = ?cid) h ");
		selectJob.append("INNER JOIN (SELECT JOB_NAME, JOB_ID, HIST_ID, CID  ");
		selectJob.append("FROM BSYMT_JOB_INFO ) w  ");
		selectJob.append("ON w.HIST_ID = h.HIST_ID AND w.JOB_ID = h.JOB_ID AND w.CID = h.CID ");
		selectJob.append("RIGHT JOIN (SELECT  i.JOB_CD, i.JOB_ID, i.CID, j.SEARCH_SET_FLG ");
		selectJob.append("FROM (SELECT DISTINCT JOB_ID, JOB_CD, CID ");
		selectJob.append("FROM BSYMT_JOB_INFO WHERE CID = ?cid) i ");
		selectJob.append("INNER JOIN WWFST_JOBTITLE_SEARCH_SET j ON j.CID = i.CID AND j.JOB_ID = i.JOB_ID ) k ");
		selectJob.append("ON w.JOB_ID = k.JOB_ID AND k.CID = w.CID ");
		selectJob.append("ORDER BY k.JOB_CD ");
		try {
			resultQuery = (List<Object[]>) this.getEntityManager().createNativeQuery(selectJob.toString())
					.setParameter("cid", cid)
					.setParameter("baseDate", this.conVertToDateSQL(baseDate))
					.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
		return resultQuery;
	}
}
