package nts.uk.file.at.infra.otpitem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountUnit;
import nts.uk.ctx.at.shared.dom.common.numberrounding.NumberRounding;
import nts.uk.ctx.at.shared.dom.common.numberrounding.NumberUnit;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.optitem.EmpConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.CalculationAtr;
import nts.uk.file.at.app.export.otpitem.CalFormulasItemColumn;
import nts.uk.file.at.app.export.otpitem.CalFormulasItemRepository;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

@Stateless
public class CalFormulasItemImpl extends JpaRepository implements CalFormulasItemRepository {

    private static String LANG_JA = "ja";
    private static final String GET_EXPORT_EXCEL_ONE;
    private static final String GET_EMPLOYMENT_SETTING = "SELECT oi.OPTIONAL_ITEM_NO, ec1.EMP_CD, emp1.NAME " +
            "FROM KRCMT_ANYV oi " +
            "  LEFT JOIN KRCMT_ANYF_COND_EMP ec1 ON oi.CID = ec1.CID AND oi.OPTIONAL_ITEM_NO = ec1.OPTIONAL_ITEM_NO " +
            "  LEFT JOIN BSYMT_EMPLOYMENT emp1 ON ec1.CID = emp1.CID AND ec1.EMP_CD = emp1.CODE " +
            "WHERE oi.EMP_CONDITION_ATR = 1 AND oi.CID = ?companyId";
    private static final String GET_DAILY_ATTENDANCE_NAMES = "SELECT " +
            "  oi.OPTIONAL_ITEM_NO, " +
            "  cis1.FORMULA_ID, " +
            "  (CASE cis1.OPERATOR " +
            "   WHEN 0 THEN '+ ' " +
            "   WHEN 1 THEN '- ' " +
            "   WHEN 2 THEN '* ' " +
            "   WHEN 3 THEN '/ ' END) AS OPERATOR, " +
            "  dai.ATTENDANCE_ITEM_NAME " +
            "FROM KRCMT_ANYV oi " +
            "  JOIN KRCMT_ANYF oif ON oi.CID = oif.CID AND oi.OPTIONAL_ITEM_NO = oif.OPTIONAL_ITEM_NO " +
            "  JOIN KRCMT_ANYF_ITEM_SELECT cis1 ON oi.CID = cis1.CID AND oi.OPTIONAL_ITEM_NO = cis1.OPTIONAL_ITEM_NO AND cis1.FORMULA_ID = oif.FORMULA_ID " +
            "  JOIN KRCMT_DAY_ATTENDANCE_ITEM dai ON oi.CID = dai.CID AND cis1.ATTENDANCE_ITEM_ID = dai.ATTENDANCE_ITEM_ID " +
            "  JOIN KRCMT_ANYF_SORT fd1 ON cis1.OPTIONAL_ITEM_NO = fd1.OPTIONAL_ITEM_NO AND cis1.CID = fd1.CID AND cis1.FORMULA_ID = fd1.FORMULA_ID " +
            "WHERE " +
            "  oi.CID = ?companyId " +
            "  AND oi.CALC_ATR = 1 " +
            "  AND oif.CALC_ATR = 0 " +
            "  AND oi.PERFORMANCE_ATR = 1 " +
            "ORDER BY fd1.DISPORDER, dai.DISPLAY_NUMBER";
    private static final String GET_MONTHLY_ATTENDANCE_NAMES = "SELECT " +
            "  oi.OPTIONAL_ITEM_NO, " +
            "  cis1.FORMULA_ID, " +
            "  (CASE cis1.OPERATOR " +
            "   WHEN 0 THEN '+ ' " +
            "   WHEN 1 THEN '- ' " +
            "   WHEN 2 THEN '* ' " +
            "   WHEN 3 THEN '/ ' END) AS OPERATOR, " +
            "  mai.M_ATD_ITEM_NAME " +
            "FROM KRCMT_ANYV oi " +
            "  JOIN KRCMT_ANYF oif ON oi.CID = oif.CID AND oi.OPTIONAL_ITEM_NO = oif.OPTIONAL_ITEM_NO " +
            "  JOIN KRCMT_ANYF_ITEM_SELECT cis1 ON oi.CID = cis1.CID AND oi.OPTIONAL_ITEM_NO = cis1.OPTIONAL_ITEM_NO AND cis1.FORMULA_ID = oif.FORMULA_ID " +
            "  JOIN KRCMT_MON_ATTENDANCE_ITEM mai ON mai.CID = cis1.CID AND mai.M_ATD_ITEM_ID = cis1.ATTENDANCE_ITEM_ID " +
            "  JOIN KRCMT_ANYF_SORT fd1 ON cis1.OPTIONAL_ITEM_NO = fd1.OPTIONAL_ITEM_NO AND cis1.CID = fd1.CID AND cis1.FORMULA_ID = fd1.FORMULA_ID " +
            "WHERE " +
            "  oi.CID = ?companyId " +
            "  AND oi.CALC_ATR = 1 " +
            "  AND oif.CALC_ATR = 0 " +
            "  AND oi.PERFORMANCE_ATR = 0 " +
            "ORDER BY fd1.DISPORDER, mai.DISP_NO";

    static {
        StringBuilder exportSQL = new StringBuilder();
        exportSQL.append("SELECT ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.OPTIONAL_ITEM_NO ELSE NULL END) AS OPTIONAL_ITEM_NO, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.OPTIONAL_ITEM_NAME ELSE NULL END) AS OPTIONAL_ITEM_NAME, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.CALC_ATR_OPT ELSE NULL END) AS CALC_ATR_OPT, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.OPTIONAL_ITEM_ATR ELSE NULL END) AS OPTIONAL_ITEM_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.UNIT_OF_OPTIONAL_ITEM ELSE NULL END) AS UNIT_OF_OPTIONAL_ITEM, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.USAGE_ATR ELSE NULL END) AS USAGE_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.PERFORMANCE_ATR ELSE NULL END) AS PERFORMANCE_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.EMP_CONDITION_ATR ELSE NULL END) AS EMP_CONDITION_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.NAME ELSE NULL END) AS NAME, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.UPPER_LIMIT_ATR ELSE NULL END) AS UPPER_LIMIT_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.UPPER_RANGE_DAY ELSE NULL END) AS UPPER_RANGE_DAY, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.UPPER_RANGE_MON ELSE NULL END) AS UPPER_RANGE_MON, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.LOWER_LIMIT_ATR ELSE NULL END) AS LOWER_LIMIT_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.LOWER_RANGE_DAY ELSE NULL END) AS LOWER_RANGE_DAY, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.LOWER_RANGE_MON ELSE NULL END) AS LOWER_RANGE_MON, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.ITEM_DESCRIP ELSE NULL END) AS ITEM_DESCRIP, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.ITEM_NOTE ELSE NULL END) AS ITEM_NOTE, ");
        exportSQL.append("  RESULT_FINAL.SYMBOL, ");
        exportSQL.append("  RESULT_FINAL.FORMULA_ATR, ");
        exportSQL.append("  RESULT_FINAL.FORMULA_NAME, ");
        exportSQL.append("  RESULT_FINAL.CALC_ATR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.PERFORMANCE_ATR = 1 THEN RESULT_FINAL.DAY_ROUNDING_UNIT ELSE NULL END) AS DAY_ROUNDING_UNIT, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.PERFORMANCE_ATR = 1 THEN RESULT_FINAL.DAY_ROUNDING ELSE NULL END) AS DAY_ROUNDING, ");
        exportSQL.append("  RESULT_FINAL.MON_ROUNDING_UNIT, ");
        exportSQL.append("  RESULT_FINAL.MON_ROUNDING, ");
        exportSQL.append("  RESULT_FINAL.CALC_ATR_2, ");
        exportSQL.append("  RESULT_FINAL.FORMULA_ID, ");
        exportSQL.append("  RESULT_FINAL.ATTENDANCE_ITEM_2, ");
        exportSQL.append("  RESULT_FINAL.FORMULAR_FROM_FORMULAR, ");
        exportSQL.append("  RESULT_FINAL.FORMULAR, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.INPUT_WITH_CHECKBOX ELSE NULL END) AS INPUT_WITH_CHECKBOX, ");
        exportSQL.append("  (CASE WHEN RESULT_FINAL.ROW_NUMBER = 1 THEN RESULT_FINAL.INPUT_UNIT ELSE NULL END) AS INPUT_UNIT ");
        exportSQL.append("FROM (SELECT ");
        exportSQL.append("        RESULT_TOTAL.DISPORDER, ");
        exportSQL.append("        RESULT_TOTAL.OPTIONAL_ITEM_NO, ");
        exportSQL.append("        RESULT_TOTAL.OPTIONAL_ITEM_NAME, ");
        exportSQL.append("        RESULT_TOTAL.OPTIONAL_ITEM_ATR, ");
        exportSQL.append("        RESULT_TOTAL.UNIT_OF_OPTIONAL_ITEM, ");
        exportSQL.append("        RESULT_TOTAL.USAGE_ATR, ");
        exportSQL.append("        RESULT_TOTAL.CALC_ATR_OPT, ");
        exportSQL.append("        RESULT_TOTAL.PERFORMANCE_ATR, ");
        exportSQL.append("        RESULT_TOTAL.INPUT_WITH_CHECKBOX, ");
        exportSQL.append("        RESULT_TOTAL.EMP_CONDITION_ATR, ");
        exportSQL.append("        RESULT_TOTAL.NAME, ");
        exportSQL.append("        RESULT_TOTAL.UPPER_LIMIT_ATR, ");
        exportSQL.append("        RESULT_TOTAL.UPPER_RANGE_DAY, ");
        exportSQL.append("        RESULT_TOTAL.UPPER_RANGE_MON, ");
        exportSQL.append("        RESULT_TOTAL.LOWER_LIMIT_ATR, ");
        exportSQL.append("        RESULT_TOTAL.LOWER_RANGE_DAY, ");
        exportSQL.append("        RESULT_TOTAL.LOWER_RANGE_MON, ");
        exportSQL.append("        RESULT_TOTAL.INPUT_UNIT, ");
        exportSQL.append("        RESULT_TOTAL.ITEM_DESCRIP, ");
        exportSQL.append("        RESULT_TOTAL.ITEM_NOTE, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.SYMBOL ELSE NULL END) AS SYMBOL, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.FORMULA_ATR ELSE NULL END) AS FORMULA_ATR, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.FORMULA_NAME ELSE NULL END) AS FORMULA_NAME, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.CALC_ATR ELSE NULL END) AS CALC_ATR, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.DAY_ROUNDING_UNIT ELSE NULL END) AS DAY_ROUNDING_UNIT, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.DAY_ROUNDING ELSE NULL END) AS DAY_ROUNDING, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.MON_ROUNDING_UNIT ELSE NULL END) AS MON_ROUNDING_UNIT, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.MON_ROUNDING ELSE NULL END) AS MON_ROUNDING, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.CALC_ATR_2 ELSE NULL END) AS CALC_ATR_2, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.FORMULA_ID ELSE NULL END) AS FORMULA_ID, ");
        exportSQL.append("        (CASE WHEN RESULT_TOTAL.USAGE_ATR = 1 THEN RESULT_TOTAL.ATTENDANCE_ITEM_2 ELSE NULL END) AS ATTENDANCE_ITEM_2, ");
        exportSQL.append("        ROW_NUMBER() ");
        exportSQL.append("        OVER (PARTITION BY RESULT_TOTAL.OPTIONAL_ITEM_NO ORDER BY RESULT_TOTAL.DISPORDER ASC, RESULT_TOTAL.OPTIONAL_ITEM_NO ASC) AS ROW_NUMBER, ");
        exportSQL.append("        RESULT_TOTAL.FORMULAR_FROM_FORMULAR, ");
        exportSQL.append("        RESULT_TOTAL.FORMULAR ");
        exportSQL.append("      FROM (SELECT ");
        exportSQL.append("              RESULT_LEFT.OPTIONAL_ITEM_NO, ");
        exportSQL.append("              RESULT_LEFT.OPTIONAL_ITEM_NAME, ");
        exportSQL.append("              RESULT_LEFT.OPTIONAL_ITEM_ATR, ");
        exportSQL.append("              RESULT_LEFT.UNIT_OF_OPTIONAL_ITEM, ");
        exportSQL.append("              RESULT_LEFT.USAGE_ATR, ");
        exportSQL.append("              RESULT_LEFT.CALC_ATR_OPT, ");
        exportSQL.append("              RESULT_LEFT.PERFORMANCE_ATR, ");
        exportSQL.append("              RESULT_LEFT.INPUT_WITH_CHECKBOX, ");
        exportSQL.append("              RESULT_LEFT.EMP_CONDITION_ATR, ");
        exportSQL.append("              RESULT_LEFT.NAME, ");
        exportSQL.append("              RESULT_RIGHT.UPPER_LIMIT_ATR, ");
        exportSQL.append("              RESULT_RIGHT.UPPER_RANGE_DAY, ");
        exportSQL.append("              RESULT_RIGHT.UPPER_RANGE_MON, ");
        exportSQL.append("              RESULT_RIGHT.LOWER_LIMIT_ATR, ");
        exportSQL.append("              RESULT_RIGHT.LOWER_RANGE_DAY, ");
        exportSQL.append("              RESULT_RIGHT.LOWER_RANGE_MON, ");
        exportSQL.append("              RESULT_RIGHT.INPUT_UNIT, ");
        exportSQL.append("              RESULT_RIGHT.ITEM_DESCRIP, ");
        exportSQL.append("              RESULT_RIGHT.ITEM_NOTE, ");
        exportSQL.append("              RESULT_RIGHT.SYMBOL, ");
        exportSQL.append("              RESULT_RIGHT.FORMULA_ATR, ");
        exportSQL.append("              RESULT_RIGHT.FORMULA_NAME, ");
        exportSQL.append("              RESULT_RIGHT.CALC_ATR, ");
        exportSQL.append("              RESULT_RIGHT.DAY_ROUNDING_UNIT, ");
        exportSQL.append("              RESULT_RIGHT.DAY_ROUNDING, ");
        exportSQL.append("              RESULT_RIGHT.MON_ROUNDING_UNIT, ");
        exportSQL.append("              RESULT_RIGHT.MON_ROUNDING, ");
        exportSQL.append("              RESULT_RIGHT.CALC_ATR_2, ");
        exportSQL.append("              RESULT_RIGHT.FORMULA_ID, ");
        exportSQL.append("              RESULT_RIGHT.ATTENDANCE_ITEM_2, ");
        exportSQL.append("              RESULT_RIGHT.DISPORDER, ");
        exportSQL.append("              RESULT_RIGHT.FORMULAR_FROM_FORMULAR, ");
        exportSQL.append("              RESULT_RIGHT.FORMULAR ");
        exportSQL.append("            FROM (SELECT ");
        exportSQL.append("                    RESULT_ONE.OPTIONAL_ITEM_NO, ");
        exportSQL.append("                    RESULT_ONE.OPTIONAL_ITEM_NAME, ");
        exportSQL.append("                    RESULT_ONE.OPTIONAL_ITEM_ATR, ");
        exportSQL.append("                    RESULT_ONE.UNIT_OF_OPTIONAL_ITEM, ");
        exportSQL.append("                    RESULT_ONE.USAGE_ATR, ");
        exportSQL.append("                    RESULT_ONE.CALC_ATR AS CALC_ATR_OPT, ");
        exportSQL.append("                    RESULT_ONE.PERFORMANCE_ATR, ");
        exportSQL.append("                    RESULT_ONE.INPUT_WITH_CHECKBOX, ");
        exportSQL.append("                    RESULT_ONE.EMP_CONDITION_ATR, ");
        exportSQL.append("                    RESULT_ONE.NAME ");
        exportSQL.append("                  FROM (SELECT ");
        exportSQL.append("                          oi.OPTIONAL_ITEM_NO, ");
        exportSQL.append("                          oi.OPTIONAL_ITEM_NAME, ");
        exportSQL.append("                          oi.OPTIONAL_ITEM_ATR, ");
        exportSQL.append("                          oi.UNIT_OF_OPTIONAL_ITEM, ");
        exportSQL.append("                          oi.USAGE_ATR, ");
        exportSQL.append("                          oi.CALC_ATR, ");
        exportSQL.append("                          oi.PERFORMANCE_ATR, ");
        exportSQL.append("                          oi.INPUT_WITH_CHECKBOX, ");
        exportSQL.append("                          oi.EMP_CONDITION_ATR, ");
        exportSQL.append("                          NULL AS NAME ");
        exportSQL.append("                        FROM KRCMT_ANYV oi ");
        exportSQL.append("                        WHERE oi.CID = ?companyId) AS RESULT_ONE ");
        exportSQL.append("                  GROUP BY RESULT_ONE.OPTIONAL_ITEM_NO, RESULT_ONE.OPTIONAL_ITEM_NAME, RESULT_ONE.OPTIONAL_ITEM_ATR, ");
        exportSQL.append("                    RESULT_ONE.UNIT_OF_OPTIONAL_ITEM, RESULT_ONE.USAGE_ATR, RESULT_ONE.CALC_ATR, ");
        exportSQL.append("                    RESULT_ONE.PERFORMANCE_ATR, ");
        exportSQL.append("                    RESULT_ONE.INPUT_WITH_CHECKBOX, RESULT_ONE.EMP_CONDITION_ATR, RESULT_ONE.NAME) RESULT_LEFT LEFT JOIN ");
        exportSQL.append("              (SELECT * ");
        exportSQL.append("               FROM (SELECT ");
        exportSQL.append("                       oi.OPTIONAL_ITEM_NO, ");
        exportSQL.append("                       crr.UPPER_LIMIT_ATR, ");
        exportSQL.append("                       (CASE oi.OPTIONAL_ITEM_ATR ");
        exportSQL.append("                        WHEN 0 THEN crr.UPPER_DAY_TIME_RANGE ");
        exportSQL.append("                        WHEN 1 THEN crr.UPPER_DAY_NUMBER_RANGE ");
        exportSQL.append("                        WHEN 2 THEN crr.UPPER_DAY_AMOUNT_RANGE END) AS UPPER_RANGE_DAY, ");
        exportSQL.append("                       (CASE oi.OPTIONAL_ITEM_ATR ");
        exportSQL.append("                        WHEN 0 THEN crr.UPPER_MON_TIME_RANGE ");
        exportSQL.append("                        WHEN 1 THEN crr.UPPER_MON_NUMBER_RANGE ");
        exportSQL.append("                        WHEN 2 THEN crr.UPPER_MON_AMOUNT_RANGE END) AS UPPER_RANGE_MON, ");
        exportSQL.append("                       crr.LOWER_LIMIT_ATR, ");
        exportSQL.append("                       (CASE oi.OPTIONAL_ITEM_ATR ");
        exportSQL.append("                        WHEN 0 THEN crr.LOWER_DAY_TIME_RANGE ");
        exportSQL.append("                        WHEN 1 THEN crr.LOWER_DAY_NUMBER_RANGE ");
        exportSQL.append("                        WHEN 2 THEN crr.LOWER_DAY_AMOUNT_RANGE END) AS LOWER_RANGE_DAY, ");
        exportSQL.append("                       (CASE oi.OPTIONAL_ITEM_ATR ");
        exportSQL.append("                        WHEN 0 THEN crr.LOWER_MON_TIME_RANGE ");
        exportSQL.append("                        WHEN 1 THEN crr.LOWER_MON_NUMBER_RANGE ");
        exportSQL.append("                        WHEN 2 THEN crr.LOWER_MON_AMOUNT_RANGE END) AS LOWER_RANGE_MON, ");
        exportSQL.append("                       (CASE oi.OPTIONAL_ITEM_ATR ");
        exportSQL.append("                        WHEN 0 ");
        exportSQL.append("                          THEN CASE crr.INPUT_UNIT_TIME ");
        exportSQL.append("                               WHEN 0 THEN 1 ");
        exportSQL.append("                               WHEN 1 THEN 5 ");
        exportSQL.append("                               WHEN 2 THEN 10 ");
        exportSQL.append("                               WHEN 3 THEN 15 ");
        exportSQL.append("                               WHEN 4 THEN 30 ");
        exportSQL.append("                               WHEN 5 THEN 60 END ");
        exportSQL.append("                        WHEN 1 ");
        exportSQL.append("                          THEN CASE crr.INPUT_UNIT_NUMBER ");
        exportSQL.append("                               WHEN 0 THEN 0.01 ");
        exportSQL.append("                               WHEN 1 THEN 0.1 ");
        exportSQL.append("                               WHEN 2 THEN 0.5 ");
        exportSQL.append("                               WHEN 3 THEN 1 END ");
        exportSQL.append("                        WHEN 2 ");
        exportSQL.append("                          THEN CASE crr.INPUT_UNIT_AMOUNT ");
        exportSQL.append("                               WHEN 0 THEN 1 ");
        exportSQL.append("                               WHEN 1 THEN 10 ");
        exportSQL.append("                               WHEN 2 THEN 100 ");
        exportSQL.append("                               WHEN 3 THEN 1000 ");
        exportSQL.append("                               WHEN 4 THEN 10000 END END ");
        exportSQL.append("                       ) AS INPUT_UNIT, ");
        exportSQL.append("                       oi.ITEM_DESCRIP, ");
        exportSQL.append("                       oi.ITEM_NOTE, ");
        exportSQL.append("                       oif.SYMBOL, ");
        exportSQL.append("                       oif.FORMULA_ATR, ");
        exportSQL.append("                       oif.FORMULA_NAME, ");
        exportSQL.append("                       oif.CALC_ATR, ");
        exportSQL.append("                       (CASE oif.FORMULA_ATR ");
        exportSQL.append("                        WHEN 0 THEN fr_day.TIME_ROUNDING_UNIT ");
        exportSQL.append("                        WHEN 1 THEN fr_day.NUMBER_ROUNDING_UNIT ");
        exportSQL.append("                        WHEN 2 THEN fr_day.AMOUNT_ROUNDING_UNIT END ");
        exportSQL.append("                       ) AS DAY_ROUNDING_UNIT, ");
        exportSQL.append("                       (CASE oif.FORMULA_ATR ");
        exportSQL.append("                        WHEN 0 THEN fr_day.TIME_ROUNDING ");
        exportSQL.append("                        WHEN 1 THEN fr_day.NUMBER_ROUNDING ");
        exportSQL.append("                        WHEN 2 THEN fr_day.AMOUNT_ROUNDING END ");
        exportSQL.append("                       ) AS DAY_ROUNDING, ");
        exportSQL.append("                       (CASE oif.FORMULA_ATR ");
        exportSQL.append("                        WHEN 0 THEN fr_month.TIME_ROUNDING_UNIT ");
        exportSQL.append("                        WHEN 1 THEN fr_month.NUMBER_ROUNDING_UNIT ");
        exportSQL.append("                        WHEN 2 THEN fr_month.AMOUNT_ROUNDING_UNIT END ");
        exportSQL.append("                       ) AS MON_ROUNDING_UNIT, ");
        exportSQL.append("                       (CASE oif.FORMULA_ATR ");
        exportSQL.append("                        WHEN 0 THEN fr_month.TIME_ROUNDING ");
        exportSQL.append("                        WHEN 1 THEN fr_month.NUMBER_ROUNDING ");
        exportSQL.append("                        WHEN 2 THEN fr_month.AMOUNT_ROUNDING END ");
        exportSQL.append("                       ) AS MON_ROUNDING, ");
        exportSQL.append("                       oif.CALC_ATR AS CALC_ATR_2, ");
        exportSQL.append("                       fd.DISPORDER, ");
        exportSQL.append("                       oif.FORMULA_ID, ");
        exportSQL.append("                       (CASE WHEN oif.CALC_ATR = 1 ");
        exportSQL.append("                         THEN CONCAT( ");
        exportSQL.append("                             (CASE fs.LEFT_SET_METHOD ");
        exportSQL.append("                              WHEN 1 THEN CAST(fs.LEFT_INPUT_VAL AS VARCHAR) ");
        exportSQL.append("                              WHEN 0 THEN CAST((SELECT koiff.SYMBOL FROM KRCMT_ANYF koiff ");
        exportSQL.append("                                   WHERE koiff.OPTIONAL_ITEM_NO = oi.OPTIONAL_ITEM_NO AND fs.LEFT_FORMULA_ITEM_ID = koiff.FORMULA_ID) ");
        exportSQL.append("                                   AS VARCHAR) ");
        exportSQL.append("                              END), ");
        exportSQL.append("                             (CASE fs.OPERATOR ");
        exportSQL.append("                              WHEN 0 THEN ' + ' ");
        exportSQL.append("                              WHEN 1 THEN ' - ' ");
        exportSQL.append("                              WHEN 2 THEN ' * ' ");
        exportSQL.append("                              WHEN 3 THEN ' / ' ");
        exportSQL.append("                              END), ");
        exportSQL.append("                             (CASE fs.RIGHT_SET_METHOD ");
        exportSQL.append("                              WHEN 1 THEN CAST(fs.RIGHT_INPUT_VAL AS VARCHAR) ");
        exportSQL.append("                              WHEN 0 THEN CAST((SELECT koiff.SYMBOL FROM KRCMT_ANYF koiff ");
        exportSQL.append("                                   WHERE koiff.OPTIONAL_ITEM_NO = oi.OPTIONAL_ITEM_NO AND fs.RIGHT_FORMULA_ITEM_ID = koiff.FORMULA_ID) ");
        exportSQL.append("                                   AS VARCHAR) ");
        exportSQL.append("                              END) ");
        exportSQL.append("                         ) ");
        exportSQL.append("                        ELSE NULL ");
        exportSQL.append("                        END) AS ATTENDANCE_ITEM_2, ");
        exportSQL.append("                       fs.MINUS_SEGMENT AS FORMULAR_FROM_FORMULAR, ");
        exportSQL.append("                       cis.MINUS_SEGMENT AS FORMULAR ");
        exportSQL.append("                     FROM KRCMT_ANYV oi ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF_RESULT_RANGE crr ON oi.CID = crr.CID AND oi.OPTIONAL_ITEM_NO = crr.OPTIONAL_ITEM_NO ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF_ROUND fr_day ON oi.CID = fr_day.CID AND oi.OPTIONAL_ITEM_NO = fr_day.OPTIONAL_ITEM_NO AND fr_day.ROUNDING_ATR = 1 ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF_ROUND fr_month ON oi.CID = fr_month.CID AND oi.OPTIONAL_ITEM_NO = fr_month.OPTIONAL_ITEM_NO AND fr_month.ROUNDING_ATR = 2 AND fr_day.FORMULA_ID = fr_month.FORMULA_ID ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF oif ON oi.CID = oif.CID AND oi.OPTIONAL_ITEM_NO = oif.OPTIONAL_ITEM_NO AND fr_day.FORMULA_ID = oif.FORMULA_ID ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF_SORT fd ON oif.OPTIONAL_ITEM_NO = fd.OPTIONAL_ITEM_NO AND oif.CID = fd.CID AND oif.FORMULA_ID = fd.FORMULA_ID ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF_ITEM_SELECT cis ON fd.FORMULA_ID = cis.FORMULA_ID AND fd.OPTIONAL_ITEM_NO = cis.OPTIONAL_ITEM_NO AND cis.CID = fd.CID ");
        exportSQL.append("                       LEFT JOIN KRCMT_ANYF_DETAIL fs ON fd.FORMULA_ID = fs.FORMULA_ID AND fd.OPTIONAL_ITEM_NO = fs.OPTIONAL_ITEM_NO ");
        exportSQL.append("                     WHERE oi.CID = ?companyId ");
        exportSQL.append("                    ) AS RESULT_END ");
        exportSQL.append("               GROUP BY OPTIONAL_ITEM_NO, ");
        exportSQL.append("                 SYMBOL, FORMULA_ATR, FORMULA_NAME, ");
        exportSQL.append("                 CALC_ATR, FORMULA_ID, ATTENDANCE_ITEM_2, ");
        exportSQL.append("                 UPPER_LIMIT_ATR, UPPER_RANGE_DAY, UPPER_RANGE_MON, ");
        exportSQL.append("                 LOWER_LIMIT_ATR, LOWER_RANGE_DAY, LOWER_RANGE_MON, ");
        exportSQL.append("                 INPUT_UNIT, ITEM_DESCRIP, ITEM_NOTE, ");
        exportSQL.append("                 DAY_ROUNDING_UNIT, DAY_ROUNDING, MON_ROUNDING_UNIT, ");
        exportSQL.append("                 MON_ROUNDING, UPPER_LIMIT_ATR, CALC_ATR_2, ");
        exportSQL.append("                 DISPORDER, FORMULAR_FROM_FORMULAR, FORMULAR ");
        exportSQL.append("              ) AS RESULT_RIGHT ");
        exportSQL.append("                ON RESULT_LEFT.OPTIONAL_ITEM_NO = RESULT_RIGHT.OPTIONAL_ITEM_NO) AS RESULT_TOTAL) AS RESULT_FINAL ");
        exportSQL.append("ORDER BY RESULT_FINAL.OPTIONAL_ITEM_NO ASC");
        GET_EXPORT_EXCEL_ONE = exportSQL.toString();
    }
    @Override
    public List<MasterData> getDataTableExport(String companyId, String langId) {
        List<MasterData> datas = new ArrayList<>();
        Query mainQuery;
        if (langId.equals(LANG_JA)) {
            mainQuery = getEntityManager().createNativeQuery(GET_EXPORT_EXCEL_ONE)
                    .setParameter("companyId", companyId);
        } else {
            mainQuery = getEntityManager().createNativeQuery(CalFormulasItemLang.GET_EXPORT_EXCEL_LANG.toString())
                    .setParameter("companyId", companyId)
                    .setParameter("langId", langId);
        }
        List<Object[]> mainData = mainQuery.getResultList();

        Query empSetQuery = getEntityManager().createNativeQuery(GET_EMPLOYMENT_SETTING)
                .setParameter("companyId", companyId);
        List<Object[]> empSetData = empSetQuery.getResultList();

        Query dayAttQuery = getEntityManager().createNativeQuery(GET_DAILY_ATTENDANCE_NAMES)
                .setParameter("companyId", companyId);
        List<Object[]> dayAttData = dayAttQuery.getResultList();

        Query monthAttQuery = getEntityManager().createNativeQuery(GET_MONTHLY_ATTENDANCE_NAMES)
                .setParameter("companyId", companyId);
        List<Object[]> monthAttData = monthAttQuery.getResultList();

        Integer optionalItemNo = null;
        Integer optionalItemAtr = null;
        Integer optionalItemUse = null;
        Integer formulaAtr = null;
        Integer performanceAtr = null;
        for (Object[] objects : mainData) {
            if (!Objects.isNull(objects[0])) {
                optionalItemNo = ((BigDecimal) objects[0]).intValue();
            }
            if (!Objects.isNull(objects[3])) {
                optionalItemAtr = ((BigDecimal) objects[3]).intValue();
            }
            if (!Objects.isNull(objects[5])) {
                optionalItemUse = ((BigDecimal) objects[5]).intValue();
            }
            if (!Objects.isNull(objects[6])) {
                performanceAtr = ((BigDecimal) objects[6]).intValue();
            }
            if (!Objects.isNull(objects[18])) {
                formulaAtr = ((BigDecimal) objects[18]).intValue();
            }
            if (ObjectUtils.anyNotNull(objects) && optionalItemUse == 1) {
                Integer finalOptionalItemNo = optionalItemNo;
                List<Object[]> attendanceData = performanceAtr == 1
                        ? dayAttData.stream().filter(i -> ((BigDecimal) i[0]).intValue() == finalOptionalItemNo).collect(Collectors.toList())
                        : monthAttData.stream().filter(i -> ((BigDecimal) i[0]).intValue() == finalOptionalItemNo).collect(Collectors.toList());
                datas.add(dataContentTable(
                        objects,
                        optionalItemAtr,
                        formulaAtr,
                        langId,
                        empSetData.stream().filter(i -> ((BigDecimal) i[0]).intValue() == finalOptionalItemNo).collect(Collectors.toList()),
                        attendanceData
                ));
            }
        }
        return datas;
    }

    private MasterData dataContentTable(Object[] object, Integer optionalItemAtr, Integer formulaAtr, String langId, List<Object[]> empSetData, List<Object[]> attendanceData) {
        Map<String, MasterCellData> data = new HashMap<>();
        // A6_1
        data.put(CalFormulasItemColumn.KMK002_76, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_76)
                .value(object[0] != null ? (BigDecimal) object[0] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_2
        data.put(CalFormulasItemColumn.KMK002_77, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_77)
                .value(object[1] != null ? (String) object[1] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_25
        data.put(CalFormulasItemColumn.KMK002_110, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_110)
                .value(object[5] != null ? ((BigDecimal) object[5]).intValue() == 1 ? TextResource.localize("KMK002_103") : TextResource.localize("KMK002_104") : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_3
        data.put(CalFormulasItemColumn.KMK002_78, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_78)
                .value(object[3] != null ? EnumAdaptor.valueOf(((BigDecimal) object[3]).intValue(), OptionalItemAtr.class).description : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_4
        data.put(CalFormulasItemColumn.KMK002_79, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_79)
                .value(object[4] != null ? (String) object[4] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_30
        data.put(CalFormulasItemColumn.KMK002_132, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_132)
                .value(object[30] != null && (object[30] instanceof BigDecimal ? ((BigDecimal) object[30]).intValue() == 1 : (Boolean) object[30]) ? "○" : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_5
        data.put(CalFormulasItemColumn.KMK002_80, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_80)
                .value(object[2] != null ? ((BigDecimal) object[2]).intValue() == 1 ? TextResource.localize("KMK002_14") : TextResource.localize("KMK002_15") : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_6
        data.put(CalFormulasItemColumn.KMK002_81, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_81)
                .value(object[6] != null ? EnumAdaptor.valueOf(((BigDecimal) object[6]).intValue(), PerformanceAtr.class).description : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_7
        data.put(CalFormulasItemColumn.KMK002_82, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_82)
                .value(object[7] != null ? EnumAdaptor.valueOf(((BigDecimal) object[7]).intValue(), EmpConditionAtr.class).description : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_8
        data.put(CalFormulasItemColumn.KMK002_83, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_83)
                .value(object[0] == null || empSetData.isEmpty() ? "" : StringUtils.join(
                        empSetData.stream().map(i -> i[1] + (i[2] == null ? "??????" : (String) i[2])).collect(Collectors.toList()),
                        ",")
                ).style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_9
        data.put(CalFormulasItemColumn.KMK002_84, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_84)
                .value(object[9] != null ? (object[9] instanceof BigDecimal ? ((BigDecimal) object[9]).intValue() == 1 : (Boolean) object[9]) ? "○" : "-" : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_10
        // A6_26
        // Upper value day
        switch (optionalItemAtr) {
            case 0:
                data.put(CalFormulasItemColumn.KMK002_85, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_85)
                        .value(object[10] != null ? formatTime(((BigDecimal) object[10]).intValue()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                data.put(CalFormulasItemColumn.KMK002_111, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_111)
                        .value(object[11] != null ? formatTime(((BigDecimal) object[11]).intValue()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                break;
            case 1:
                data.put(CalFormulasItemColumn.KMK002_85, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_85)
                        .value(object[10] != null ? formatNumber(((BigDecimal) object[10]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                data.put(CalFormulasItemColumn.KMK002_111, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_111)
                        .value(object[11] != null ? formatNumber(((BigDecimal) object[11]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                break;
            case 2:
                data.put(CalFormulasItemColumn.KMK002_85, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_85)
                        .value(object[10] != null ? formatAmount(((BigDecimal) object[10]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                data.put(CalFormulasItemColumn.KMK002_111, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_111)
                        .value(object[11] != null ? formatAmount(((BigDecimal) object[11]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                break;
        }

        // A6_11
        data.put(CalFormulasItemColumn.KMK002_86, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_86)
                .value(object[12] != null ? (object[12] instanceof BigDecimal ? ((BigDecimal) object[12]).intValue() == 1 : (Boolean) object[12]) ? "○" : "-" : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_12
        // A6_27
        // Lower value
        switch (optionalItemAtr) {
            case 0:
                data.put(CalFormulasItemColumn.KMK002_87, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_87)
                        .value(object[13] != null ? formatTime(((BigDecimal) object[13]).intValue()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                data.put(CalFormulasItemColumn.KMK002_112, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_112)
                        .value(object[14] != null ? formatTime(((BigDecimal) object[14]).intValue()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                break;
            case 1:
                data.put(CalFormulasItemColumn.KMK002_87, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_87)
                        .value(object[13] != null ? formatNumber(((BigDecimal) object[13]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                data.put(CalFormulasItemColumn.KMK002_112, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_112)
                        .value(object[14] != null ? formatNumber(((BigDecimal) object[14]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                break;
            case 2:
                data.put(CalFormulasItemColumn.KMK002_87, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_87)
                        .value(object[13] != null ? formatAmount(((BigDecimal) object[13]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                data.put(CalFormulasItemColumn.KMK002_112, MasterCellData.builder()
                        .columnId(CalFormulasItemColumn.KMK002_112)
                        .value(object[14] != null ? formatAmount(((BigDecimal) object[14]).toString()) : "")
                        .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                        .build());
                break;
        }
        // A6_31
        data.put(CalFormulasItemColumn.KMK002_170, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_170)
                .value(object[31] != null ? object[31].toString() : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT))
                .build());
        // A6_28
        data.put(CalFormulasItemColumn.KMK002_113, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_113)
                .value(object[15] != null ? (String) object[15] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());

        // A6_29
        data.put(CalFormulasItemColumn.KMK002_114, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_114)
                .value(object[16] != null ? (String) object[16] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());

        // A6_13
        data.put(CalFormulasItemColumn.KMK002_88, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_88)
                .value(object[17] != null ? (String) object[17] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_14
        data.put(CalFormulasItemColumn.KMK002_89, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_89)
                .value(object[18] != null ? EnumAdaptor.valueOf(((BigDecimal) object[18]).intValue(), OptionalItemAtr.class).description : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_15
        data.put(CalFormulasItemColumn.KMK002_90, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_90)
                .value(object[19] != null ? (String) object[19] : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());
        // A6_16
        data.put(CalFormulasItemColumn.KMK002_91, MasterCellData.builder()
                .columnId(CalFormulasItemColumn.KMK002_91)
                .value(object[20] != null ? EnumAdaptor.valueOf(((BigDecimal) object[20]).intValue(), CalculationAtr.class).description : "")
                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                .build());

        if (formulaAtr != null) {
            switch (formulaAtr) {
                case 0:
                    data.put(CalFormulasItemColumn.KMK002_93, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_93)
                            .value(object[22] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[22]).intValue(), Rounding.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 1:
                    data.put(CalFormulasItemColumn.KMK002_93, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_93)
                            .value(object[22] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[22]).intValue(), NumberRounding.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 2:
                    data.put(CalFormulasItemColumn.KMK002_93, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_93)
                            .value(object[22] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[22]).intValue(), AmountRounding.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
            }

            // A7_18
            switch (formulaAtr) {
                case 0:
                    data.put(CalFormulasItemColumn.KMK002_92, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_92)
                            .value(object[21] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[21]).intValue(), Unit.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 1:
                    data.put(CalFormulasItemColumn.KMK002_92, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_92)
                            .value(object[21] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[21]).intValue(), NumberUnit.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 2:
                    data.put(CalFormulasItemColumn.KMK002_92, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_92)
                            .value(object[21] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[21]).intValue(), AmountUnit.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
            }

            // A7_19
            switch (formulaAtr) {
                case 0:
                    data.put(CalFormulasItemColumn.KMK002_95, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_95)
                            .value(object[24] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[24]).intValue(), Rounding.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 1:
                    data.put(CalFormulasItemColumn.KMK002_95, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_95)
                            .value(object[24] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[24]).intValue(), NumberRounding.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 2:
                    data.put(CalFormulasItemColumn.KMK002_95, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_95)
                            .value(object[24] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[24]).intValue(), AmountRounding.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
            }

            // A7_20
            switch (formulaAtr) {
                case 0:
                    data.put(CalFormulasItemColumn.KMK002_94, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_94)
                            .value(object[24] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[23]).intValue(), Unit.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 1:
                    data.put(CalFormulasItemColumn.KMK002_94, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_94)
                            .value(object[24] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[23]).intValue(), NumberUnit.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 2:
                    data.put(CalFormulasItemColumn.KMK002_94, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_94)
                            .value(object[24] != null ? TextResource.localize(EnumAdaptor.valueOf(((BigDecimal) object[23]).intValue(), AmountUnit.class).nameId) : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
            }
        } else {
            data.put(CalFormulasItemColumn.KMK002_93, MasterCellData.builder()
                    .columnId(CalFormulasItemColumn.KMK002_93)
                    .value("")
                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                    .build());
            data.put(CalFormulasItemColumn.KMK002_92, MasterCellData.builder()
                    .columnId(CalFormulasItemColumn.KMK002_92)
                    .value("")
                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                    .build());
            data.put(CalFormulasItemColumn.KMK002_95, MasterCellData.builder()
                    .columnId(CalFormulasItemColumn.KMK002_95)
                    .value("")
                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                    .build());
            data.put(CalFormulasItemColumn.KMK002_94, MasterCellData.builder()
                    .columnId(CalFormulasItemColumn.KMK002_94)
                    .value("")
                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                    .build());
        }

        if (object[25] != null) {
            switch (((BigDecimal) object[25]).intValue()) {
                case 0:
                    String formularItem = StringUtils.join(
                            attendanceData.stream().filter(i -> object[26] != null && ((String) object[26]).equals(i[1])).map(i -> (String) i[2] + i[3]).collect(Collectors.toList()),
                            " "
                    );
                    String value = formularItem.startsWith(CalFormulasItemColumn.add_Operator) && formularItem.length() > 2 ?
                            formularItem.substring(2, formularItem.length()) : formularItem;

//				String valueTest1 = MessageFormat.format(value,'1');
                    data.put(CalFormulasItemColumn.KMK002_97,
                            MasterCellData.builder().columnId(CalFormulasItemColumn.KMK002_97)
                                    .value(formatName(value))
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());

                    // Minus
                    data.put(CalFormulasItemColumn.KMK002_96, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_96)
                            .value(object[29] != null ? ((BigDecimal) object[29]).intValue() == 1 ? "○" : "-" : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
                case 1:
                    data.put(CalFormulasItemColumn.KMK002_97,
                            MasterCellData.builder().columnId(CalFormulasItemColumn.KMK002_97)
                                    .value(object[27] != null ? ((String) object[27]) : "")
                                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());

                    // Minus
                    data.put(CalFormulasItemColumn.KMK002_96, MasterCellData.builder()
                            .columnId(CalFormulasItemColumn.KMK002_96)
                            .value(object[28] != null ? ((BigDecimal) object[28]).intValue() == 1 ? "○" : "-" : "")
                            .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                            .build());
                    break;
            }
        } else {
            data.put(CalFormulasItemColumn.KMK002_96, MasterCellData.builder()
                    .columnId(CalFormulasItemColumn.KMK002_96)
                    .value("")
                    .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
                    .build());
            data.put(CalFormulasItemColumn.KMK002_97, MasterCellData.builder().columnId(CalFormulasItemColumn.KMK002_97)
                    .value("").style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
        }

//		if(!langId.equals(LANG_JA)) {
//		    data.put(CalFormulasItemColumn.KMK002_100, MasterCellData.builder()
//	                .columnId(CalFormulasItemColumn.KMK002_100)
//	                .value(object[30] != null ? object[30].toString(): "")
//	                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
//	                .build());
//		}else {
//			data.put(CalFormulasItemColumn.KMK002_100, MasterCellData.builder()
//	                .columnId(CalFormulasItemColumn.KMK002_100)
//	                .value("")
//	                .style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT))
//	                .build());
//		}
        return MasterData.builder().rowData(data).build();
    }

    private String formatName(String name) {
        while (name.indexOf("{#") >= 0) {
            int startLocation = name.indexOf("{");
            int endLocation = name.indexOf("}");
            name = name.replace(name.substring(startLocation, endLocation + 1),
                    TextResource.localize(name.substring(startLocation + 2, endLocation)));
        }
        return name;
    }

    private String formatTime(int source) {
        int regularized = Math.abs(source);
        int hourPart = (regularized / 60);
        int minutePart = regularized % 60;
        String resultString = StringUtils.join(StringUtil.padLeft(String.valueOf(hourPart), 1, '0'), ":",
                StringUtil.padLeft(String.valueOf(minutePart), 2, '0'));
        if (source < 0) {
            resultString = "-" + resultString;
        }
        return resultString;
    }

    private String formatNumber(String number) {
        double numberParse = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,##0.0");
        String resultString = formatter.format(numberParse);
        return resultString;
    }

    private String formatAmount(String amount) {
        double amountParse = Double.parseDouble(amount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amountParse);
    }
}