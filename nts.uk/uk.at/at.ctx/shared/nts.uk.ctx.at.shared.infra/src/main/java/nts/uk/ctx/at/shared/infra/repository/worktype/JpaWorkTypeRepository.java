/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeInfor;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSet;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkType;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkTypePK;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkTypeSet;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkTypeSetPK;
import nts.uk.ctx.at.shared.infra.entity.worktype.worktypedisporder.KshmtWorkTypeOrder;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
public class JpaWorkTypeRepository extends JpaRepository implements WorkTypeRepository {

	/** use lesser value for nested split WHERE IN parameters to make sure total parameters < 2100 */
	private static final int SPLIT_650 = 650;
	
	private static final String SELECT_FROM_WORKTYPE = "SELECT c FROM KshmtWorkType c";

	private static final String SELECT_ALL_WORKTYPE = SELECT_FROM_WORKTYPE
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId";
	
	private static final String SELECT_ALL_WORKTYPE_BY_CID = SELECT_FROM_WORKTYPE
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.deprecateAtr = 0"
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";

	private static final String SELECT_WORKTYPE_BY_LEAVE_ABSENCE = SELECT_FROM_WORKTYPE
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.oneDayAtr=12"
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";

	private static final String SELECT_ALL_NOT_DEPRECATED_WORKTYPE = "SELECT c FROM KshmtWorkType c"
			+ " LEFT JOIN KshmtWorkTypeOrder o ON c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.deprecateAtr = 0"
			+ " ORDER BY CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC, c.kshmtWorkTypePK.workTypeCode ASC";

	private static final String SELECT_ALL_CODE_AND_NAME_OF_WORKTYPE = "SELECT c.kshmtWorkTypePK.workTypeCode, c.name FROM KshmtWorkType c"
			+ " LEFT JOIN KshmtWorkTypeOrder o ON c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.deprecateAtr = 0"
			+ " ORDER BY CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC, c.kshmtWorkTypePK.workTypeCode ASC";
	
	private static final String SELECT_CODE_AND_NAME_BY_WORKTYPE_CODE = "SELECT c.kshmtWorkTypePK.workTypeCode, c.name FROM KshmtWorkType c"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.kshmtWorkTypePK.workTypeCode IN :listWorktypeCode";
	
	private static final String SELECT_FROM_WORKTYPESET = "SELECT a FROM KshmtWorkTypeSet a WHERE a.kshmtWorkTypeSetPK.companyId = :companyId"
			+ " AND a.kshmtWorkTypeSetPK.workTypeCode = :workTypeCode";
	
	private static final String SELECT_BY_ID_CLASSIFICATION = "SELECT a FROM KshmtWorkTypeSet a WHERE a.kshmtWorkTypeSetPK.companyId = :companyId"
			+ " AND a.deprecateAtr = :deprecateAtr"
			+ " AND a.worktypeAtr = :worktypeAtr"
			+ " AND a.oneDayAtr = :oneDayAtr";

	private static final String SELECT_FROM_WORKTYPESET_CLOSE_ATR_DEPRECATE_ATR = "SELECT a FROM KshmtWorkTypeSet a LEFT JOIN KshmtWorkType c"
			+ " ON a.kshmtWorkTypeSetPK.companyId = c.kshmtWorkTypePK.companyId AND a.kshmtWorkTypeSetPK.workTypeCode = c.kshmtWorkTypePK.workTypeCode"
			+ " WHERE a.kshmtWorkTypeSetPK.companyId = :companyId AND a.closeAtr = :closeAtr AND c.deprecateAtr = :deprecateAtr "
			+ " AND (c.oneDayAtr = 12 OR c.oneDayAtr = 13) " //fix bug 102299
			+ " ORDER BY a.kshmtWorkTypeSetPK.workTypeCode";
	
	private static final String SELECT_FROM_WORKTYPESET_CLOSURE = "SELECT a FROM KshmtWorkTypeSet a LEFT JOIN KshmtWorkType c"
			+ " ON a.kshmtWorkTypeSetPK.companyId = c.kshmtWorkTypePK.companyId AND a.kshmtWorkTypeSetPK.workTypeCode = c.kshmtWorkTypePK.workTypeCode"
			+ " WHERE a.kshmtWorkTypeSetPK.companyId = :companyId AND a.closeAtr = :closeAtr AND c.deprecateAtr = :deprecateAtr "
			+ " AND c.oneDayAtr = 13 "
			+ " ORDER BY a.kshmtWorkTypeSetPK.workTypeCode";

	private static final String SELECT_WORKTYPE = SELECT_FROM_WORKTYPE + " WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.kshmtWorkTypePK.workTypeCode IN :lstPossible";
	
	private static final String SELECT_WORKTYPE_ATR = SELECT_FROM_WORKTYPE + " WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.deprecateAtr = :deprecateAtr";

	private static final String FIND_NOT_DEPRECATED_BY_LIST_CODE = SELECT_FROM_WORKTYPE
			+ " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.kshmtWorkTypePK.workTypeCode IN :codes AND c.deprecateAtr = 0"
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";

	private static final String FIND_ALL_BY_LIST_CODE = SELECT_FROM_WORKTYPE
			+ " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.kshmtWorkTypePK.workTypeCode IN :codes "
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";
	
	private static final String FIND_NOT_DEPRECATED = SELECT_FROM_WORKTYPE + " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId" + " AND c.deprecateAtr = 0"
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";
	
	private static final String FIND_WORKTYPE = SELECT_FROM_WORKTYPE + " WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.worktypeAtr = 0 AND c.oneDayAtr = 2" + " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";

	private static final String DELETE_WORKTYPE_SET = "DELETE FROM KshmtWorkTypeSet c "
			+ "WHERE c.kshmtWorkTypeSetPK.companyId =:companyId "
			+ "AND c.kshmtWorkTypeSetPK.workTypeCode =:workTypeCode ";

	private static final String FIND_ATTENDANCE_WORKTYPE = SELECT_FROM_WORKTYPE + " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId" + " AND c.deprecateAtr = 0"
			+ " AND ((c.worktypeAtr = 0 AND c.oneDayAtr = 0)" + " OR (c.worktypeAtr = 0 AND c.oneDayAtr = 7)"
			+ " OR (c.worktypeAtr = 0 AND c.oneDayAtr = 10))" + " ORDER BY o.dispOrder ASC";

	private static final String FIND_HOLIDAY_WORKTYPE = SELECT_FROM_WORKTYPE + " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId" + " AND c.deprecateAtr = 0"
			+ " AND (c.worktypeAtr = 0 AND c.oneDayAtr = 1)" + " ORDER BY o.dispOrder ASC";

	private static final String FIND_LEAVE_SYSTEM_WORKTYPE = SELECT_FROM_WORKTYPE + " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId" + " AND c.deprecateAtr = 0"
			+ " AND ((c.worktypeAtr = 0 AND c.oneDayAtr = 0)" + " OR (c.worktypeAtr = 0 AND c.oneDayAtr = 7)"
			+ " OR (c.worktypeAtr = 0 AND c.oneDayAtr = 11))" + " ORDER BY o.dispOrder ASC";

	private static final String FIND_WORKTYPE_BY_DEPRECATE = SELECT_FROM_WORKTYPE
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId" + " AND c.kshmtWorkTypePK.workTypeCode = :workTypeCd"
			+ " AND c.deprecateAtr = 0";
	//hoatt - kaf006
	private static final String FIND_FOR_APP_HD = "SELECT c,o.dispOrder FROM KshmtWorkType c"
			+ " LEFT JOIN KshmtWorkTypeOrder o"
			+ " ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode"
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.kshmtWorkTypePK.workTypeCode IN :lstWorkTypeCD"
			+ " AND c.deprecateAtr = :deprecateAtr"
			+ " AND ((c.worktypeAtr = 0 AND c.oneDayAtr IN :hdType)"
			+ " OR (c.worktypeAtr = 1 AND c.morningAtr IN :hdType AND c.afternoonAtr IN :hdType))"
			+ " ORDER BY o.dispOrder ASC";
	// laitv - ksu001
	private static final String SELECT_ALL_WORKTYPE_WITHORDER_NOTABOLISH = 
			  " SELECT c FROM KshmtWorkType c "
			+ " LEFT JOIN KshmtWorkTypeOrder o "
			+ " ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode "
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId AND c.deprecateAtr = 0 "
			+ " ORDER BY CASE WHEN o.dispOrder IS NULL THEN 2147483647 ELSE o.dispOrder END, o.dispOrder ASC , c.kshmtWorkTypePK.workTypeCode ASC ";
	
	// findWorkType(java.lang.String, java.lang.Integer, java.util.List,
	// java.util.List)
	private static final String FIND_WORKTYPE_ALLDAY_AND_HALFDAY;
	private static final String FIND_WORKTYPE_ONEDAY;
	private static final String FIND_WORKTYPE_FOR_SHOTING;
	private static final String FIND_WORKTYPE_FOR_PAUSE;
	private static final String FIND_WORKTYPE_FOR_HOLIDAY_APP_TYPE;
	private static final String FIND_WORKTYPE_BY_LIST_WORKTYPECODES;
	private static final String FIND_WORKTYPE_AllDAY_HALFDAY_BY_CODES;
	private static final String FIND_BY_CODES;
	private static final String SELECT_WORKTYPE_AND_ORDER;
	private static final String SELECT_WORKTYPE_ALL_ORDER;
	private static final String SELECT_WORKTYPE_WITH_NO_MASTER_AND_ORDER;
	private static final String SELECT_NOT_REMOVE_WORKTYPE_AND_ORDER;
	
	static {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeInfor.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.kshmtWorkTypePK.workTypeCode IN :lstPossible ");
		SELECT_WORKTYPE_AND_ORDER = stringBuilder.toString();
	}
	
	static{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeInfor.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.kshmtWorkTypePK.workTypeCode = :wkTypeCd ");
		SELECT_WORKTYPE_WITH_NO_MASTER_AND_ORDER = stringBuilder.toString();
	}
	
	static {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeInfor.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.deprecateAtr = 0 ");
		stringBuilder.append("ORDER BY CASE WHEN o.dispOrder IS NULL THEN 1 ELSE 0 END, o.dispOrder ASC ");
		SELECT_WORKTYPE_ALL_ORDER = stringBuilder.toString();
	}
	
	static {
		StringBuilder builder = new StringBuilder();
		builder.append(SELECT_FROM_WORKTYPE);
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = :abolishAtr");
		builder.append(" AND c.oneDayAtr IN :oneDayAtrs");
		builder.append(" AND (c.morningAtr IN :morningAtrs");
		builder.append(" OR c.afternoonAtr IN :afternoonAtrs)");
		builder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		FIND_WORKTYPE_ALLDAY_AND_HALFDAY = builder.toString();
	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append(SELECT_FROM_WORKTYPE);
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = :abolishAtr");
		builder.append(" AND c.oneDayAtr = :oneDayAtr");
		builder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		FIND_WORKTYPE_ONEDAY = builder.toString();
	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c,o.dispOrder FROM KshmtWorkType c");
		builder.append(" LEFT JOIN KshmtWorkTypeOrder o");
		builder.append(" ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode");
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = 0");
		builder.append(" AND ( ");
		builder.append(" (c.worktypeAtr = 0 AND c.oneDayAtr = 7)");
		builder.append(" OR (c.worktypeAtr= 1 AND c.morningAtr = 7 AND c.afternoonAtr IN (1,2,3,4,5,6,9) )");
		builder.append(" OR(c.worktypeAtr= 1 AND c.morningAtr IN (1,2,3,4,5,6,9)) AND c.afternoonAtr = 7  ");
		builder.append(" OR(c.worktypeAtr= 1 AND c.morningAtr = 7 AND c.afternoonAtr = 0)");
		builder.append(" OR(c.worktypeAtr= 1 AND c.morningAtr = 0 AND c.afternoonAtr = 7)");
		builder.append(")");
		builder.append(" ORDER BY o.dispOrder ASC");
		FIND_WORKTYPE_FOR_SHOTING = builder.toString();

	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append(SELECT_ALL_WORKTYPE);
		builder.append(" AND c.deprecateAtr = 0");
		builder.append(" AND ( ");
		builder.append(" (c.worktypeAtr = 0 AND c.oneDayAtr IN :oneDayAtrs)");
		builder.append(" OR (c.worktypeAtr= 1 AND c.morningAtr = :morningAtr AND c.afternoonAtr IN :afternoonAtrs )");
		builder.append(" OR(c.worktypeAtr= 1 AND c.afternoonAtr = :afternoonAtr AND c.morningAtr IN :morningAtrs )");
		builder.append(")");
		builder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		FIND_WORKTYPE_FOR_HOLIDAY_APP_TYPE = builder.toString();

	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c,o.dispOrder FROM KshmtWorkType c");
		builder.append(" LEFT JOIN KshmtWorkTypeOrder o");
		builder.append(" ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode");
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = 0");
		builder.append(" AND ( ");
		builder.append(" (c.worktypeAtr = 0 AND c.oneDayAtr = 8)");
		builder.append(" OR(c.worktypeAtr= 1 AND c.morningAtr = 8 AND c.afternoonAtr = 0)");
		builder.append(" OR(c.worktypeAtr= 1 AND c.morningAtr = 0 AND c.afternoonAtr = 8)");
		builder.append(" OR (c.worktypeAtr= 1 AND c.morningAtr = 8 AND c.afternoonAtr IN (1,2,3,4,5,6,9) )");
		builder.append(" OR(c.worktypeAtr= 1 AND c.morningAtr IN (1,2,3,4,5,6,9)) AND c.afternoonAtr = 8 ");
		builder.append(")");
		builder.append(" ORDER BY o.dispOrder ASC");
		FIND_WORKTYPE_FOR_PAUSE = builder.toString();

	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c,o.dispOrder FROM KshmtWorkType c");
		builder.append(" LEFT JOIN KshmtWorkTypeOrder o");
		builder.append(" ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode");
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = 0");
		builder.append(" AND c.kshmtWorkTypePK.workTypeCode IN :workTypeCodes");
		builder.append(" AND c.worktypeAtr = 1 AND ( c.morningAtr IN :morningAtrs OR c.afternoonAtr IN :afternoonAtrs )");
		builder.append(" ORDER BY o.dispOrder ASC");
		FIND_WORKTYPE_BY_LIST_WORKTYPECODES = builder.toString();

	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append(SELECT_ALL_WORKTYPE);
		builder.append(" AND c.deprecateAtr = 0");
		builder.append(" AND c.kshmtWorkTypePK.workTypeCode IN :workTypeCodes AND ( ");
		builder.append(" (c.worktypeAtr = 0 AND c.oneDayAtr IN :oneDayAtrs)");
		builder.append(" OR (c.worktypeAtr = 1 AND c.morningAtr IN :morningAtrs AND c.afternoonAtr IN :afternoonAtrs )");
		builder.append(")");
		builder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		FIND_WORKTYPE_AllDAY_HALFDAY_BY_CODES = builder.toString();

	}
	static {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT c,o.dispOrder FROM KshmtWorkType c");
		builder.append(" LEFT JOIN KshmtWorkTypeOrder o");
		builder.append(" ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode");
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = :abolishAtr");
		builder.append(" AND c.kshmtWorkTypePK.workTypeCode IN :workTypeCodes ");
		builder.append(" AND c.worktypeAtr = :worktypeAtr ");
		builder.append(" ORDER BY o.dispOrder ASC");
		FIND_BY_CODES = builder.toString();

	}
	static {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT NEW " + WorkTypeInfor.class.getName());
		stringBuilder.append(
				"(c.kshmtWorkTypePK.workTypeCode, c.name, c.abbreviationName, c.symbolicName, c.deprecateAtr, c.memo, c.worktypeAtr, c.oneDayAtr, c.morningAtr, c.afternoonAtr, c.calculatorMethod, o.dispOrder) ");
		stringBuilder.append("FROM KshmtWorkType c LEFT JOIN KshmtWorkTypeOrder o ");
		stringBuilder.append(
				"ON c.kshmtWorkTypePK.companyId = o.kshmtWorkTypeDispOrderPk.companyId AND c.kshmtWorkTypePK.workTypeCode = o.kshmtWorkTypeDispOrderPk.workTypeCode ");
		stringBuilder.append("WHERE c.kshmtWorkTypePK.companyId = :companyId ");
		stringBuilder.append("AND c.kshmtWorkTypePK.workTypeCode IN :lstPossible ");
		stringBuilder.append("AND c.deprecateAtr = 0 ");
		SELECT_NOT_REMOVE_WORKTYPE_AND_ORDER = stringBuilder.toString();
	}

	private static WorkType toDomain(KshmtWorkType entity) {
		return toDomain(entity, entity.kshmtWorkTypeOrder, entity.worktypeSetList);
	}
	
	private static WorkType toDomain(KshmtWorkType entity, KshmtWorkTypeOrder order, List<KshmtWorkTypeSet> set) {
		val domain = WorkType.createSimpleFromJavaType(entity.kshmtWorkTypePK.companyId,
				entity.kshmtWorkTypePK.workTypeCode, entity.symbolicName, entity.name, entity.abbreviationName,
				entity.memo, entity.worktypeAtr, entity.oneDayAtr, entity.morningAtr, entity.afternoonAtr,
				entity.deprecateAtr, entity.calculatorMethod);
		if (order != null) {
			domain.setDisplayOrder(order.dispOrder);
		}

		if (set != null) {
			domain.setWorkTypeSet(set.stream().map(x -> toDomainWorkTypeSet(x)).collect(Collectors.toList()));
		}
		return domain;
	}

	private static WorkTypeSet toDomainWorkTypeSet(KshmtWorkTypeSet entity) {
		val domain = WorkTypeSet.createSimpleFromJavaType(entity.kshmtWorkTypeSetPK.companyId,
				entity.kshmtWorkTypeSetPK.workTypeCode, entity.kshmtWorkTypeSetPK.workAtr, entity.digestPublicHd,
				entity.hodidayAtr, entity.countHoliday, entity.closeAtr, entity.sumAbsenseNo, entity.sumSpHolidayNo,
				entity.timeLeaveWork, entity.attendanceTime, entity.genSubHoliday, entity.dayNightTimeAsk);
		return domain;
	}

	private static KshmtWorkType toEntity(WorkType domain) {
		val entity = new KshmtWorkType();

		entity.kshmtWorkTypePK = new KshmtWorkTypePK(domain.getCompanyId(), domain.getWorkTypeCode().v());
		entity.abbreviationName = domain.getAbbreviationName().v();
		entity.afternoonAtr = domain.getDailyWork().getAfternoon().value;
		entity.calculatorMethod = domain.getCalculateMethod().value;
		entity.deprecateAtr = domain.getDeprecate().value;
		entity.memo = domain.getMemo().v();
		entity.morningAtr = domain.getDailyWork().getMorning().value;
		entity.name = domain.getName().v();
		entity.oneDayAtr = domain.getDailyWork().getOneDay().value;
		entity.symbolicName = domain.getSymbolicName().v();
		entity.worktypeAtr = domain.getDailyWork().getWorkTypeUnit().value;

		return entity;
	}

	private static KshmtWorkTypeSet toEntityWorkTypeSet(WorkTypeSet domain) {
		val entity = new KshmtWorkTypeSet(
				new KshmtWorkTypeSetPK(domain.getCompanyId(), domain.getWorkTypeCd().v(), domain.getWorkAtr().value),
				domain.getDigestPublicHd().value, domain.getHolidayAtr().value, domain.getCountHodiday().value,
				domain.getCloseAtr() != null ? domain.getCloseAtr().value : null, domain.getSumAbsenseNo(),
				domain.getSumSpHodidayNo(), domain.getTimeLeaveWork().value, domain.getAttendanceTime().value,
				domain.getGenSubHodiday().value, domain.getDayNightTimeAsk().value);
		return entity;
	}

	private List<String> toListCodeAndName(Object[] obj) {
		List<String> lstCodeAndName = new ArrayList<>();
		lstCodeAndName.add(obj[0].toString());
		lstCodeAndName.add(obj[1].toString());
		return lstCodeAndName;
	}
	
	@Override
	public List<WorkType> getPossibleWorkType(String companyId, List<String> lstPossible) {
		List<WorkType> datas = new ArrayList<WorkType>();
		CollectionUtil.split(lstPossible, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subPossibleList -> {
			datas.addAll(
					this.queryProxy().query(SELECT_WORKTYPE, KshmtWorkType.class).setParameter("companyId", companyId)
							.setParameter("lstPossible", subPossibleList).getList(x -> toDomain(x)));
		});
		return datas;
	}
	
	@Override
	public List<WorkType> findWorkByDeprecate(String companyId, int deprecateAtr) {
		return this.queryProxy().query(SELECT_WORKTYPE_ATR, KshmtWorkType.class)
				.setParameter("companyId", companyId).setParameter("deprecateAtr", deprecateAtr)
				.getList(x -> toDomain(x));
	}
	
	@Override
	public List<WorkTypeInfor> getPossibleWorkTypeAndOrder(String companyId, List<String> lstPossible) {
		if(CollectionUtil.isEmpty(lstPossible)){
			return Collections.emptyList();
		}
		List<WorkTypeInfor> resultList = new ArrayList<>();
		CollectionUtil.split(lstPossible, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_WORKTYPE_AND_ORDER, WorkTypeInfor.class)
								.setParameter("companyId", companyId)
								.setParameter("lstPossible", subList)
								.getList());
		});
		List<WorkTypeInfor> lstOrder = resultList.stream().filter(c -> c.getDispOrder() != null).collect(Collectors.toList());
		List<WorkTypeInfor> lstNotOrder = resultList.stream().filter(c -> c.getDispOrder() == null).collect(Collectors.toList());
		Collections.sort(lstOrder, Comparator.comparing(WorkTypeInfor:: getDispOrder));
		Collections.sort(lstNotOrder, Comparator.comparing(WorkTypeInfor:: getWorkTypeCode));
		List<WorkTypeInfor> lstSort = new ArrayList<>();
		lstSort.addAll(lstOrder);
		lstSort.addAll(lstNotOrder);
		Collections.sort(lstSort, Comparator.comparing(WorkTypeInfor:: getWorkTypeCode));
		return lstSort;
	}
	
	@Override
	public List<WorkTypeInfor> getPossibleWorkTypeWithNoMasterAndOrder(String companyId, List<String> lstPossible) {
		if(CollectionUtil.isEmpty(lstPossible)){
			return Collections.emptyList();
		}
		List<WorkTypeInfor> resultList = new ArrayList<>();
		
		lstPossible.forEach(wkTypeCd -> {

			WorkTypeInfor wkType;

			Optional<WorkTypeInfor> optWkInfo = this.queryProxy().query(SELECT_WORKTYPE_WITH_NO_MASTER_AND_ORDER, WorkTypeInfor.class)
					.setParameter("companyId", companyId).setParameter("wkTypeCd", wkTypeCd).getSingle();
			if (!StringUtil.isNullOrEmpty(wkTypeCd, true)) {
				if (optWkInfo.isPresent()) {

					wkType = optWkInfo.get();
				} else {
					wkType = new WorkTypeInfor(wkTypeCd, TextResource.localize("KAL003_120"), "", "", 0, "", 0, 0, 0, 0,
							0, null, Collections.emptyList());
				}

				resultList.add(wkType);
			}

		});
		List<WorkTypeInfor> lstOrder = resultList.stream().filter(c -> c.getDispOrder() != null).collect(Collectors.toList());
		List<WorkTypeInfor> lstNotOrder = resultList.stream().filter(c -> c.getDispOrder() == null).collect(Collectors.toList());
		Collections.sort(lstOrder, Comparator.comparing(WorkTypeInfor:: getDispOrder));
		Collections.sort(lstNotOrder, Comparator.comparing(WorkTypeInfor:: getWorkTypeCode));
		List<WorkTypeInfor> lstSort = new ArrayList<>();
		lstSort.addAll(lstOrder);
		lstSort.addAll(lstNotOrder);
		return lstSort;
	}
	
	@Override
	public List<WorkTypeInfor> findAllByOrder(String companyId) {
		return this.queryProxy().query(SELECT_WORKTYPE_ALL_ORDER, WorkTypeInfor.class).setParameter("companyId", companyId).getList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<WorkType> getPossibleWorkTypeV2(String companyId, List<String> lstPossible) {
		StringBuilder builder = new StringBuilder("SELECT c, o, s FROM KshmtWorkType c ");
		builder.append(" LEFT JOIN c.kshmtWorkTypeOrder o");
		builder.append(" LEFT JOIN c.worktypeSetList s");
//		builder.append(" LEFT JOIN workTypeLanguage l");
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.kshmtWorkTypePK.workTypeCode IN :lstPossible");
		List<Object[]> datas = new ArrayList<Object[]>();
		CollectionUtil.split(lstPossible, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subPossibleList -> {
			datas.addAll(
					this.queryProxy().query(builder.toString(), Object[].class).setParameter("companyId", companyId)
							.setParameter("lstPossible", subPossibleList).getList());
		});
		return datas.stream().collect(Collectors.groupingBy(d -> (KshmtWorkType) d[0], Collectors.toList()))
				.entrySet().stream().map(d -> {
					KshmtWorkType tw = d.getKey();
					KshmtWorkTypeOrder order = d.getValue().stream().filter(o -> o[1] != null)
							.findFirst().map(o -> (KshmtWorkTypeOrder) o[1]) .orElse(null);
					List<KshmtWorkTypeSet> set = d.getValue().stream().filter(o -> o[2] != null)
							.distinct().map(o -> (KshmtWorkTypeSet) o[2]).collect(Collectors.toList());
			return toDomain(tw, order, set);
		}).collect(Collectors.toList());
	}
	
	@Override
	public List<WorkType> findByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE, KshmtWorkType.class).setParameter("companyId", companyId)
				.getList(c -> toDomain(c));
	}

	@Override
	public List<WorkType> findByCompanyIdAndLeaveAbsence(String companyId) {
		return this.queryProxy().query(SELECT_WORKTYPE_BY_LEAVE_ABSENCE, KshmtWorkType.class)
				.setParameter("companyId", companyId).getList(c -> toDomain(c));
	}

	@Override
	public List<WorkType> findNotDeprecateByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_ALL_NOT_DEPRECATED_WORKTYPE, KshmtWorkType.class)
				.setParameter("companyId", companyId).getList(c -> toDomain(c));
	}

	@Override
	public List<WorkTypeSet> findWorkTypeSet(String companyId, String workTypeCode) {
		return this.queryProxy().query(SELECT_FROM_WORKTYPESET, KshmtWorkTypeSet.class)
				.setParameter("companyId", companyId).setParameter("workTypeCode", workTypeCode)
				.getList(x -> toDomainWorkTypeSet(x));
	}

	@Override
	public List<WorkTypeSet> findWorkTypeSetCloseAtrDeprecateAtr(String companyId, int closeAtr, int deprecateAtr) {
		return this.queryProxy().query(SELECT_FROM_WORKTYPESET_CLOSE_ATR_DEPRECATE_ATR, KshmtWorkTypeSet.class)
				.setParameter("companyId", companyId).setParameter("closeAtr", closeAtr).setParameter("deprecateAtr", deprecateAtr)
				.getList(x -> toDomainWorkTypeSet(x));
	}
	
	@Override
	public List<WorkTypeSet> findWorkTypeByClosure(String companyId, int closeAtr, int deprecateAtr) {
		return this.queryProxy().query(SELECT_FROM_WORKTYPESET_CLOSURE, KshmtWorkTypeSet.class)
				.setParameter("companyId", companyId).setParameter("closeAtr", closeAtr).setParameter("deprecateAtr", deprecateAtr)
				.getList(x -> toDomainWorkTypeSet(x));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<WorkType> findByPK(String companyId, String workTypeCd) {
		return this.queryProxy().find(new KshmtWorkTypePK(companyId, workTypeCd), KshmtWorkType.class)
				.map(x -> toDomain(x));
	}
	
	@Override
	public Optional<WorkType> findNoAbolishByPK(String companyId, String workTypeCd) {
		return this.queryProxy().find(new KshmtWorkTypePK(companyId, workTypeCd), KshmtWorkType.class).filter(x -> {
			return x.deprecateAtr == 0;
		})
				.map(x -> toDomain(x));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository#findNotDeprecated(
	 * java.lang.String)
	 */
	@Override
	public List<WorkType> findNotDeprecated(String companyId) {
		return this.queryProxy().query(FIND_NOT_DEPRECATED, KshmtWorkType.class).setParameter("companyId", companyId)
				.getList(c -> toDomain(c));
	}
	
	@Override
	public List<WorkType> findWorkTypeByCondition(String companyId) {
		return this.queryProxy().query(FIND_WORKTYPE, KshmtWorkType.class).setParameter("companyId", companyId)
				.getList(c -> toDomain(c));
	}
	
	@Override
	public boolean findWorkTypeRecord(String companyId, String workTypeCode) {
		try (PreparedStatement statement = this.connection().prepareStatement(
				"select Count(*) from KSHMT_WKTP"
				+ " where CID = ? and CD = ?")) {
			
			statement.setString(1, companyId);
			statement.setString(2,workTypeCode);
			ResultSet result = statement.executeQuery();
			while(result.next()){
				if(result.getInt(1) > 0){
					return true;
				}
            }
			
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository#
	 * findNotDeprecatedByListCode(java.lang.String, java.util.List)
	 */
	@Override
	public List<WorkType> findNotDeprecatedByListCode(String companyId, List<String> codes) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_NOT_DEPRECATED_BY_LIST_CODE, KshmtWorkType.class)
								.setParameter("companyId", companyId)
								.setParameter("codes", subList)
								.getList(c -> toDomain(c)));
		});
		resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}
	
	@Override
	public List<WorkType> findAllByListCode(String companyId, List<String> codes) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_ALL_BY_LIST_CODE, KshmtWorkType.class)
								.setParameter("companyId", companyId)
								.setParameter("codes", subList)
								.getList(c -> toDomain(c)));
		});
		resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository#
	 * findWorkType(java.lang.String, java.lang.Integer, java.util.List,
	 * java.util.List)
	 */
	@Override
	public List<WorkType> findWorkType(String companyId, int abolishAtr, List<Integer> allDayAtrs,
			List<Integer> halfAtrs) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(allDayAtrs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstAllDay -> {
			CollectionUtil.split(halfAtrs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstHalfDays -> {
				resultList.addAll(this.queryProxy().query(FIND_WORKTYPE_ALLDAY_AND_HALFDAY, KshmtWorkType.class)
									.setParameter("companyId", companyId).setParameter("abolishAtr", abolishAtr)
									.setParameter("oneDayAtrs", lstAllDay).setParameter("morningAtrs", lstHalfDays)
									.setParameter("afternoonAtrs", lstHalfDays).getList(c -> toDomain(c)));
			});
		});
		resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}

	/**
	 * Insert workType to KSHMT_WKTP
	 */
	@Override
	public void add(WorkType workType) {
		this.commandProxy().insert(toEntity(workType));
	}

	@Override
	public void update(WorkType workType) {
		KshmtWorkTypePK key = new KshmtWorkTypePK(workType.getCompanyId(), workType.getWorkTypeCode().v());
		KshmtWorkType entity = this.queryProxy().find(key, KshmtWorkType.class).get();
		entity.kshmtWorkTypePK = key;
		entity.symbolicName = workType.getSymbolicName().v();
		entity.name = workType.getName().v();
		entity.abbreviationName = workType.getAbbreviationName().v();
		entity.memo = workType.getMemo().v();
		entity.deprecateAtr = workType.getDeprecate().value;
		entity.worktypeAtr = workType.getDailyWork().getWorkTypeUnit().value;
		entity.oneDayAtr = workType.getDailyWork().getOneDay().value;
		entity.morningAtr = workType.getDailyWork().getMorning().value;
		entity.afternoonAtr = workType.getDailyWork().getAfternoon().value;
		entity.calculatorMethod = workType.getCalculateMethod().value;
		this.commandProxy().update(entity);
	}

	@Override
	public void remove(String companyId, String workTypeCd) {
		KshmtWorkTypePK key = new KshmtWorkTypePK(companyId, workTypeCd);
		this.commandProxy().remove(KshmtWorkType.class, key);
	}

	@Override
	public void addWorkTypeSet(WorkTypeSet workTypeSet) {
		this.commandProxy().insert(toEntityWorkTypeSet(workTypeSet));
	}

	@Override
	public void removeWorkTypeSet(String companyId, String workTypeCd) {
		this.getEntityManager().createQuery(DELETE_WORKTYPE_SET).setParameter("companyId", companyId)
				.setParameter("workTypeCode", workTypeCd).executeUpdate();
	}

	@Override
	public List<WorkType> findWorkOneDay(String companyId, int abolishAtr, int oneDayAtr) {
		return this.queryProxy().query(FIND_WORKTYPE_ONEDAY, KshmtWorkType.class).setParameter("companyId", companyId)
				.setParameter("abolishAtr", abolishAtr).setParameter("oneDayAtr", oneDayAtr).getList(x -> toDomain(x));
	}

	private final String FIND_WORKTYPE_BY_ATR_AND_ONEDAY = "SELECT c FROM KshmtWorkType c WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ " AND c.deprecateAtr = :abolishAtr AND c.worktypeAtr = :worktypeAtr AND c.oneDayAtr = :oneDayAtr "
			+ "ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";
	@Override
	public List<WorkType> findWorkOneDay(String companyId, int abolishAtr, int worktypeAtr, int oneDay) {
		return this.queryProxy().query(FIND_WORKTYPE_BY_ATR_AND_ONEDAY, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("abolishAtr", abolishAtr)
				.setParameter("worktypeAtr", worktypeAtr)
				.setParameter("oneDayAtr", oneDay)
				.getList(x -> toDomain(x));
	}
	
	

	@Override
	public List<List<String>> findCodeAndNameOfWorkTypeByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_ALL_CODE_AND_NAME_OF_WORKTYPE, Object[].class)
				.setParameter("companyId", companyId).getList(c -> toListCodeAndName(c));
	}

	public List<WorkType> getAcquiredAttendanceWorkTypes(String companyId) {
		return this.queryProxy().query(FIND_ATTENDANCE_WORKTYPE, KshmtWorkType.class)
				.setParameter("companyId", companyId).getList(x -> toDomain(x));
	}

	@Override
	public List<WorkType> getAcquiredHolidayWorkTypes(String companyId) {
		return this.queryProxy().query(FIND_HOLIDAY_WORKTYPE, KshmtWorkType.class).setParameter("companyId", companyId)
				.getList(x -> toDomain(x));
	}

	@Override
	public List<WorkType> getAcquiredLeaveSystemWorkTypes(String companyId) {
		return this.queryProxy().query(FIND_LEAVE_SYSTEM_WORKTYPE, KshmtWorkType.class)
				.setParameter("companyId", companyId).getList(x -> toDomain(x));
	}

	@Override
	public Optional<WorkType> findByDeprecated(String companyId, String workTypeCd) {
		return this.queryProxy().query(FIND_WORKTYPE_BY_DEPRECATE, KshmtWorkType.class)
				.setParameter("companyId", companyId).setParameter("workTypeCd", workTypeCd)
				.getSingle(x -> toDomain(x));
	}

	@Override
	public List<WorkType> findWorkTypeForShorting(String companyId) {
		return this.queryProxy().query(FIND_WORKTYPE_FOR_SHOTING, Object[].class)
				.setParameter("companyId", companyId).getList(x -> toDomainWithDispOrder(x));

	}

	@Override
	public List<WorkType> findWorkTypeForPause(String companyId) {
		return this.queryProxy().query(FIND_WORKTYPE_FOR_PAUSE,  Object[].class)
				.setParameter("companyId", companyId).getList(x -> toDomainWithDispOrder(x));
	}

	@Override
	public List<WorkType> findWorkTypeForAppHolidayAppType(String companyId,List<Integer> allDayAtrs, List<Integer> mornings,List<Integer> afternoons,Integer morning,Integer afternoon) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(allDayAtrs, SPLIT_650, lstAll -> {
			CollectionUtil.split(mornings, SPLIT_650, lstMorning -> {
				CollectionUtil.split(afternoons, SPLIT_650, lstAfternoon -> {
					resultList.addAll(this.queryProxy().query(FIND_WORKTYPE_FOR_HOLIDAY_APP_TYPE, KshmtWorkType.class)
										.setParameter("companyId", companyId)
										.setParameter("oneDayAtrs", lstAll)
										.setParameter("morningAtr", morning)
										.setParameter("afternoonAtrs", lstAfternoon)
										.setParameter("afternoonAtr", afternoon)
										.setParameter("morningAtrs", lstMorning)
										.getList(x -> toDomain(x)));
				});
			});
		});
		resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}

	@Override
	public List<WorkType> findWorkTypeForHalfDay(String companyId, List<Integer> halfDay, List<String> workTypeCodes) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(halfDay, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstHalf -> {
			CollectionUtil.split(workTypeCodes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstCodes -> {
				resultList.addAll(this.queryProxy().query(FIND_WORKTYPE_BY_LIST_WORKTYPECODES, Object[].class)
				.setParameter("companyId", companyId)
				.setParameter("workTypeCodes", lstCodes)
				.setParameter("morningAtrs", lstHalf)
				.setParameter("afternoonAtrs", lstHalf).getList(x -> toDomainWithDispOrder(x)));
			});
		});
		///resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}

	@Override
	public List<WorkType> findWorkTypeForAllDayAndHalfDay(String companyId, List<Integer> halfDay,
			List<String> workTypeCodes, List<Integer> oneDays) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(halfDay, SPLIT_650, lstHalf -> {
			CollectionUtil.split(workTypeCodes, SPLIT_650, lstCodes -> {
				CollectionUtil.split(oneDays, SPLIT_650, lstAll -> {
					resultList.addAll(this.queryProxy().query(FIND_WORKTYPE_AllDAY_HALFDAY_BY_CODES, KshmtWorkType.class)
										.setParameter("companyId", companyId)
										.setParameter("workTypeCodes", lstCodes)
										.setParameter("oneDayAtrs", lstAll)
										.setParameter("morningAtrs", lstHalf)
										.setParameter("afternoonAtrs", lstHalf).getList(x -> toDomain(x)));
				});
			});
		});
		resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}

	@Override
	public List<WorkType> findWorkTypeByCodes(String companyId, List<String> workTypeCodes, int abolishAtr,
			int worktypeAtr) {
		List<WorkType> resultList = new ArrayList<>();
		CollectionUtil.split(workTypeCodes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_CODES, Object[].class)
								.setParameter("companyId", companyId)
								.setParameter("workTypeCodes", subList)
								.setParameter("abolishAtr", abolishAtr)
								.setParameter("worktypeAtr", worktypeAtr).getList(x -> toDomainWithDispOrder(x)));
		});
		//resultList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return resultList;
	}
	
	private WorkType toDomainWithDispOrder(Object[] object) {
		KshmtWorkType entity = (KshmtWorkType) object[0];
		Integer order = object[1] != null ? Integer.valueOf(object[1].toString()) : null;
		
		WorkType domain = toDomain(entity);
		if (order != null) {
			domain.setDisplayOrder(order);
		}
		return domain;
	}

	@Override
	public Map<String, String> getCodeNameWorkType(String companyId, List<String> listWorktypeCode) {
		List<Object[]> listObject = new ArrayList<>();
		CollectionUtil.split(listWorktypeCode, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listObject.addAll(this.queryProxy().query(SELECT_CODE_AND_NAME_BY_WORKTYPE_CODE, Object[].class)
								.setParameter("companyId", companyId)
								.setParameter("listWorktypeCode", subList)
								.getList());
		});
		return listObject.stream().collect(Collectors.toMap(x -> String.valueOf(x[0]), x -> String.valueOf(x[1])));
	}

	@Override
	public List<WorkType> findForAppHdKAF006(String companyId, List<String> lstWorkTypeCD, int deprecateAtr,
			List<Integer> hdType) {
		return this.queryProxy().query(FIND_FOR_APP_HD, Object[].class)
				.setParameter("companyId", companyId)
				.setParameter("lstWorkTypeCD", lstWorkTypeCD)
				.setParameter("deprecateAtr", deprecateAtr)
				.setParameter("hdType", hdType)
				.getList(c -> toDomainWithDispOrder(c));
	}

	@Override
	public List<String> findWorkTypeCodeOneDay(String companyId, int abolishAtr, int worktypeAtr, int oneDay) {
		return this.queryProxy().query(FIND_WORKTYPE_BY_ATR_AND_ONEDAY, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("abolishAtr", abolishAtr)
				.setParameter("worktypeAtr", worktypeAtr)
				.setParameter("oneDayAtr", oneDay)
				.getList(x -> x.kshmtWorkTypePK.workTypeCode);
	}
	
	@Override
	public List<WorkTypeInfor> getNotRemoveWorkType(String companyId, List<String> lstPossible) {
		if(CollectionUtil.isEmpty(lstPossible)){
			return Collections.emptyList();
		}
		List<WorkTypeInfor> resultList = new ArrayList<>();
		CollectionUtil.split(lstPossible, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_NOT_REMOVE_WORKTYPE_AND_ORDER, WorkTypeInfor.class)
								.setParameter("companyId", companyId)
								.setParameter("lstPossible", subList)
								.getList());
		});
		List<WorkTypeInfor> lstOrder = resultList.stream().filter(c -> c.getDispOrder() != null).collect(Collectors.toList());
		List<WorkTypeInfor> lstNotOrder = resultList.stream().filter(c -> c.getDispOrder() == null).collect(Collectors.toList());
		Collections.sort(lstOrder, Comparator.comparing(WorkTypeInfor:: getDispOrder));
		Collections.sort(lstNotOrder, Comparator.comparing(WorkTypeInfor:: getWorkTypeCode));
		List<WorkTypeInfor> lstSort = new ArrayList<>();
		lstSort.addAll(lstOrder);
		lstSort.addAll(lstNotOrder);
		return lstSort;
	}

	@Override
	public List<WorkType> findListByCid(String companyId) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.getList(x -> toDomain(x));
	}

	private static final String SELECT_ALL_WORKTYPE_BY_LIST_CODE = SELECT_FROM_WORKTYPE
			+ " WHERE c.kshmtWorkTypePK.companyId = :companyId "
			+ "AND c.kshmtWorkTypePK.workTypeCode IN :workTypeCodes ";
	
	@Override
	public List<WorkType> findByCidAndWorkTypeCodes(String companyId, List<String> workTypeCodes) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE_BY_LIST_CODE, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("workTypeCodes", workTypeCodes)
				.getList(x -> toDomain(x));
	}
	
	private static final String SELECT_HOLIDAY_WORK_TYPE = "SELECT c FROM KshmtWorkType c JOIN c.worktypeSetList w WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ "　AND c.deprecateAtr = :abolishAtr AND c.worktypeAtr = :worktypeAtr AND c.oneDayAtr = :oneDayAtr"
			+ "　AND w.hodidayAtr = :hodidayAtr"
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";
	@Override
	public List<WorkType> findHolidayWorkType(String companyId, int abolishAtr, int worktypeAtr, int workTypeClf,
			int holiday) {
		
		return this.queryProxy().query(SELECT_HOLIDAY_WORK_TYPE, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("abolishAtr", abolishAtr)
				.setParameter("worktypeAtr", worktypeAtr)
				.setParameter("oneDayAtr", workTypeClf)
				.setParameter("hodidayAtr", holiday)
				.getList(x -> toDomain(x));
	}
	
	private static final String SELECT_HOLIDAY_WORK_TYPE_CLO = "SELECT c FROM KshmtWorkType c JOIN c.worktypeSetList w WHERE c.kshmtWorkTypePK.companyId = :companyId"
			+ "　AND c.deprecateAtr = :deprecateAtr AND c.worktypeAtr = :worktypeAtr AND c.oneDayAtr = :oneDayAtr"
			+ "　AND w.closeAtr = :closeAtr"
			+ " ORDER BY c.kshmtWorkTypePK.workTypeCode ASC";
	@Override
	public List<WorkType> findHolidayWorkTypeClo(String companyId, int deprecateAtr, int worktypeAtr, int workTypeClf,
			int closeAtr) {
		
		return this.queryProxy().query(SELECT_HOLIDAY_WORK_TYPE_CLO, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("deprecateAtr", deprecateAtr)
				.setParameter("worktypeAtr", worktypeAtr)
				.setParameter("oneDayAtr", workTypeClf)
				.setParameter("closeAtr", closeAtr)
				.getList(x -> toDomain(x));
	}
	

	private static final String SELECT_ALL_FOR_KAF008;
	static {
		StringBuilder builder = new StringBuilder();
		builder.append(SELECT_FROM_WORKTYPE);
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = :deprecateAtr");
		builder.append(" AND c.worktypeAtr = :worktypeAtr");
		builder.append(" AND c.oneDayAtr IN :hdType");
		builder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_ALL_FOR_KAF008 = builder.toString();
	}

	public List<WorkType> findForAppKAF008(String companyId, int deprecateAtr, int worktypeAtr, List<Integer> hdType){
		return this.queryProxy().query(SELECT_ALL_FOR_KAF008, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("deprecateAtr", deprecateAtr)
				.setParameter("worktypeAtr", worktypeAtr)
				.setParameter("hdType", hdType)
				.getList(x -> toDomain(x));
	}

	private static final String SELECT_BY_DEPRECATE_AND_WORK_ATR;
	static {
		StringBuilder builder = new StringBuilder();
		builder.append(SELECT_FROM_WORKTYPE);
		builder.append(" WHERE c.kshmtWorkTypePK.companyId = :companyId");
		builder.append(" AND c.deprecateAtr = :deprecateAtr");
		builder.append(" AND c.worktypeAtr = :worktypeAtr");
		builder.append(" ORDER BY c.kshmtWorkTypePK.workTypeCode ASC");
		SELECT_BY_DEPRECATE_AND_WORK_ATR = builder.toString();
	}

	public List<WorkType> findByDepreacateAtrAndWorkTypeAtr(String companyId, int deprecateAtr, int worktypeAtr) {
		return this.queryProxy().query(SELECT_BY_DEPRECATE_AND_WORK_ATR, KshmtWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("deprecateAtr", deprecateAtr)
				.setParameter("worktypeAtr", worktypeAtr)
				.getList(x -> toDomain(x));
	};
	
	@Override
	public List<WorkType> getAllWorkTypeNotAbolished(String companyId) {
		return this.queryProxy().query(SELECT_ALL_WORKTYPE_WITHORDER_NOTABOLISH, KshmtWorkType.class).setParameter("companyId", companyId)
				.getList(c -> toDomain(c));
	}
}
