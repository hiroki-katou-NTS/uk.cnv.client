package nts.uk.ctx.sys.log.infra.repository.datacorrectionlog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.log.dom.datacorrectionlog.DataCorrectionLogRepository;
import nts.uk.ctx.sys.log.infra.entity.datacorrectionlog.SrcdtDataCorrection;
import nts.uk.ctx.sys.log.infra.entity.datacorrectionlog.SrcdtDataCorrectionLogPk;
import nts.uk.shr.com.security.audittrail.correction.content.DataCorrectionLog;
import nts.uk.shr.com.security.audittrail.correction.content.TargetDataType;
import nts.uk.shr.com.security.audittrail.correction.processor.DataCorrectionLogWriter;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class JpaDataCorrectionLogRepository extends JpaRepository
		implements DataCorrectionLogRepository, DataCorrectionLogWriter {

	@Override
	public List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId,
			DatePeriod datePeriod) {
		if (targetDataType == null || listEmployeeId.isEmpty())
			return Collections.emptyList();
		Connection con = this.getEntityManager().unwrap(Connection.class);
//		String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd";
		List<DataCorrectionLog> resultList = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String listEmp = "(";
			for(int i = 0; i < subList.size(); i++){
				listEmp += "'"+ subList.get(i) +"',";
			}
			// remove last , in string and add )
			listEmp = listEmp.substring(0, listEmp.length() - 1) + ")";
			String query = "SELECT OPERATION_ID, USER_ID, TARGET_DATA_TYPE, ITEM_ID, USER_NAME, SID, YMD_KEY, YM_KEY, Y_KEY, STRING_KEY, CORRECTION_ATTR, ITEM_NAME, RAW_VALUE_BEFORE_ID, VIEW_VALUE_BEFORE, RAW_VALUE_AFTER_ID, VIEW_VALUE_AFTER, VALUE_DATA_TYPE, SHOW_ORDER, NOTE"
					+ " FROM SRCDT_DATA_CORRECTION "
					+ " WHERE TARGET_DATA_TYPE = "+ "'" + targetDataType.value + "'" 
					+ " AND SID IN " + listEmp 
					+ " AND YMD_KEY >= " + "'" + datePeriod.start() + "'" 
					+ " AND YMD_KEY <= " + "'" + datePeriod.end() + "'";
			try {
				ResultSet rs = con.createStatement().executeQuery(query);
				while (rs.next()) {
					String operationId = rs.getString("OPERATION_ID");
					String userId = rs.getString("USER_ID");
					String sId = rs.getString("SID");
					String userName = rs.getString("USER_NAME");
					int targetDtType = rs.getInt("TARGET_DATA_TYPE");
					String stringKey = rs.getString("STRING_KEY");
					int correctionAttr = rs.getInt("CORRECTION_ATTR");
					String itemId = rs.getString("ITEM_ID");
					String itemName = rs.getString("ITEM_NAME");
					String viewValueBefore = rs.getString("VIEW_VALUE_BEFORE");
					String viewValueAfter = rs.getString("VIEW_VALUE_AFTER");
					String rawValueBefore = rs.getString("RAW_VALUE_BEFORE_ID");
					String rawValueAfter = rs.getString("RAW_VALUE_AFTER_ID");
					int showOrder = rs.getInt("SHOW_ORDER");
					String note = rs.getString("NOTE");
					GeneralDate ymdKey = GeneralDate.fromString(rs.getString("YMD_KEY"), "yyyy-MM-dd");
					Integer ymKey = Integer.valueOf(rs.getInt("YM_KEY"));
					Integer yKey = Integer.valueOf(rs.getInt("Y_KEY"));
					Integer valueType = Integer.valueOf(rs.getInt("VALUE_DATA_TYPE"));
					
					SrcdtDataCorrectionLogPk pk = new SrcdtDataCorrectionLogPk(operationId, userId, targetDtType,
							itemId, ymdKey);
					SrcdtDataCorrection srcdtDataCorrectionLog = new SrcdtDataCorrection(pk, userName, sId, ymKey,
							yKey, stringKey, correctionAttr, itemName, rawValueBefore, viewValueBefore, rawValueAfter,
							viewValueAfter, valueType, showOrder, note);
					
					resultList.add(srcdtDataCorrectionLog.toDomainToView());
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return resultList;
	}

	@Override
	public List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId,
			YearMonthPeriod ymPeriod) {
		if (targetDataType == null || listEmployeeId.isEmpty())
			return Collections.emptyList();
		String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId "
				+ "AND a.ymKey >= :startYm AND a.ymKey <= :endYm";
		List<DataCorrectionLog> resultList = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
					.setParameter("targetDataType", targetDataType.value).setParameter("listEmpId", subList)
					.setParameter("startYm", ymPeriod.start().v()).setParameter("endYm", ymPeriod.end().v())
					.getList(c -> c.toDomainToView()));
		});
		return resultList;
	}

	@Override
	public List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId,
			Year yearStart, Year yearEnd) {
		if (targetDataType == null || listEmployeeId.isEmpty())
			return Collections.emptyList();
		String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId "
				+ "AND a.yKey >= :startY AND a.yKey <= :endY";
		List<DataCorrectionLog> resultList = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
					.setParameter("targetDataType", targetDataType.value).setParameter("listEmpId", subList)
					.setParameter("startY", yearStart.getValue()).setParameter("endY", yearEnd.getValue())
					.getList(c -> c.toDomainToView()));
		});
		return resultList;
	}
	
	private static Comparator<SrcdtDataCorrection> SORT_BY_FOUR_FIELDS = (o1, o2) -> {
		int tmp = o1.employeeId.compareTo(o2.employeeId);
		if (tmp != 0) return tmp;
		tmp = o1.pk.ymdKey.compareTo(o2.pk.ymdKey);
		if (tmp != 0) return tmp;
		tmp = o1.ymKey.compareTo(o2.ymKey);
		if (tmp != 0) return tmp;
		return o1.yKey.compareTo(o2.yKey);
	};

	@Override
	public List<DataCorrectionLog> findByTargetAndDate(String operationId, List<String> listEmployeeId,
			DatePeriod period, TargetDataType targetDataType) {
		List<SrcdtDataCorrection> entities;
		if (targetDataType == null) {
			if (listEmployeeId == null || listEmployeeId.isEmpty()) {
				if (period.start() == null) {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					return this.queryProxy().query(query, SrcdtDataCorrection.class)
							.setParameter("operationId", operationId).getList(c -> c.toDomainToView());
				} else {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					return this.queryProxy().query(query, SrcdtDataCorrection.class)
							.setParameter("operationId", operationId).setParameter("startYmd", period.start())
							.setParameter("endYmd", period.end()).getList(c -> c.toDomainToView());
				}
			} else {
				if (period.start() == null) {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.employeeId IN :listEmpId ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					entities = new ArrayList<>();
					CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
						entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
							.setParameter("operationId", operationId).setParameter("listEmpId", subList)
							.getList());
					});
					entities.sort(SORT_BY_FOUR_FIELDS);
					return entities.stream().map(c -> c.toDomainToView()).collect(Collectors.toList());
				} else {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.employeeId IN :listEmpId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					entities = new ArrayList<>();
					CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
						entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
								.setParameter("operationId", operationId).setParameter("listEmpId", subList)
								.setParameter("startYmd", period.start()).setParameter("endYmd", period.end())
								.getList());
					});
					entities.sort(SORT_BY_FOUR_FIELDS);
					return entities.stream().map(c -> c.toDomainToView()).collect(Collectors.toList());
				}
			}
		} else {
			if (listEmployeeId == null || listEmployeeId.isEmpty()) {
				if (period.start() == null) {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.pk.targetDataType = :targetDataType ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					return this.queryProxy().query(query, SrcdtDataCorrection.class)
							.setParameter("operationId", operationId)
							.setParameter("targetDataType", targetDataType.value).getList(c -> c.toDomainToView());
				} else {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.pk.targetDataType = :targetDataType AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					return this.queryProxy().query(query, SrcdtDataCorrection.class)
							.setParameter("operationId", operationId)
							.setParameter("targetDataType", targetDataType.value)
							.setParameter("startYmd", period.start()).setParameter("endYmd", period.end())
							.getList(c -> c.toDomainToView());
				}
			} else {
				if (period.start() == null) {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					entities = new ArrayList<>();
					CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
						entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
							.setParameter("operationId", operationId)
							.setParameter("targetDataType", targetDataType.value)
							.setParameter("listEmpId", subList).getList());
					});
					entities.sort(SORT_BY_FOUR_FIELDS);
					return entities.stream().map(c -> c.toDomainToView()).collect(Collectors.toList());
				} else {
					String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId = :operationId AND a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey";
					entities = new ArrayList<>();
					CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
						entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
								.setParameter("operationId", operationId)
								.setParameter("targetDataType", targetDataType.value)
								.setParameter("listEmpId", subList).setParameter("startYmd", period.start())
								.setParameter("endYmd", period.end()).getList());
					});
					entities.sort(SORT_BY_FOUR_FIELDS);
					return entities.stream().map(c -> c.toDomainToView()).collect(Collectors.toList());
				}
			}
		}

	}

	@Override
	public void save(List<DataCorrectionLog> dataCorrectionLog) {
		List<SrcdtDataCorrection> entities = dataCorrectionLog.stream()
				.map(x -> SrcdtDataCorrection.fromDomain(x)).collect(Collectors.toList());
		this.commandProxy().insertAll(entities);
	}
	
	private static Comparator<SrcdtDataCorrection> SORT_BY_FIVE_FIELDS = (o1, o2) -> {
		int tmp = o1.employeeId.compareTo(o2.employeeId);
		if (tmp != 0) return tmp;
		tmp = o1.pk.ymdKey.compareTo(o2.pk.ymdKey);
		if (tmp != 0) return tmp;
		tmp = o1.ymKey.compareTo(o2.ymKey);
		if (tmp != 0) return tmp;
		tmp = o1.yKey.compareTo(o2.yKey);
		if (tmp != 0) return tmp;
		return o1.showOrder - o2.showOrder;
	};

	@Override
	public List<DataCorrectionLog> findByTargetAndDate(List<String> operationIds, List<String> listEmployeeId,
			DatePeriod period, TargetDataType targetDataType) {
		List<DataCorrectionLog> results = new ArrayList<>();
		
		if (operationIds.isEmpty())
			return results;
		List<DataCorrectionLog> entities = new ArrayList<>();
		
		if (this.database().is(DatabaseProduct.MSSQLSERVER)) {
			// SQLServer
			String sql = "select OPERATION_ID, USER_ID , TARGET_DATA_TYPE,  ITEM_ID, YMD_KEY," // primary Key
					+ " SID, YM_KEY, Y_KEY, STRING_KEY, CORRECTION_ATTR,  ITEM_NAME, VIEW_VALUE_BEFORE, VIEW_VALUE_AFTER, VALUE_DATA_TYPE, SHOW_ORDER" // other
					+ " from SRCDT_DATA_CORRECTION" //table name
					+ " with(index(SRCDI_DATA_CORRECTION_LOG2))";//hint
			//TODO：ログ照会レスポンス改善
			//一時対応として、出力するログ自体を減らすため、修正区分を指定
			String correctAttrOneCondition = " and CORRECTION_ATTR = '0'";
//			String correctAttrOneCondition = "";

			
			if (targetDataType == null) {
				if (listEmployeeId == null || listEmployeeId.isEmpty()) {
					if (period.start() == null) {
//						sql += " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
						sql +=  " where OPERATION_ID in @operationIds "// condition
							+   correctAttrOneCondition
							+	" order by YMD_KEY, SID,  SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
								  	.paramString("operationIds", subIdList)
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
								  																			rec.getString("USER_ID"), 
								  																			rec.getInt("TARGET_DATA_TYPE"),
								  																			rec.getString("ITEM_ID"),
								  																			rec.getGeneralDate("YMD_KEY")), 
								  												"",
								  												rec.getString("SID"),
								  												rec.getInt("YM_KEY"), 
								  												rec.getInt("Y_KEY"),
								  												"", 
								  												rec.getInt("CORRECTION_ATTR"), 
								  												rec.getString("ITEM_NAME"), 
								  												"", 
								  												rec.getString("VIEW_VALUE_BEFORE"), 
								  												"", 
								  												rec.getString("VIEW_VALUE_AFTER"), 
								  												rec.getInt("VALUE_DATA_TYPE"), 
								  												rec.getInt("SHOW_ORDER"), 
								  												"").toDomainToViewJDBC();}));
						});
						return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection 
//						a WHERE a.pk.operationId IN :operationIds ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//									.setParameter("operationIds", subIdList).getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
						
					}
					else {
						sql +=  " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds"// condition
							+   correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID,  SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll( new NtsStatement(executeSQL, this.jdbcProxy())
								  .paramString("operationIds", subIdList)
								  .paramDate("startYmd", period.start())
								  .paramDate("endYmd", period.end())
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
												rec.getString("USER_ID"), 
												rec.getInt("TARGET_DATA_TYPE"),
												rec.getString("ITEM_ID"),
												rec.getGeneralDate("YMD_KEY")), 
					"",
					rec.getString("SID"),
					rec.getInt("YM_KEY"), 
					rec.getInt("Y_KEY"),
					"", 
					rec.getInt("CORRECTION_ATTR"), 
					rec.getString("ITEM_NAME"), 
					"", 
					rec.getString("VIEW_VALUE_BEFORE"), 
					"", 
					rec.getString("VIEW_VALUE_AFTER"), 
					rec.getInt("VALUE_DATA_TYPE"), 
					rec.getInt("SHOW_ORDER"), 
					"").toDomainToViewJDBC();}));
					});
					return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection 
//						a WHERE a.pk.operationId IN :operationIds AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//									.setParameter("operationIds", subIdList).setParameter("startYmd", period.start())
//									.setParameter("endYmd", period.end()).getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					}
				}
				else {
					if (period.start() == null) {
						sql += " where OPERATION_ID in @operationIds and SID in @listEmployeeId"
							+  correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
								+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
									.paramString("operationIds", subIdList)
									.paramString("listEmployeeId", listEmployeeId)
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
												rec.getString("USER_ID"), 
												rec.getInt("TARGET_DATA_TYPE"),
												rec.getString("ITEM_ID"),
												rec.getGeneralDate("YMD_KEY")), 
												"",
												rec.getString("SID"),
												rec.getInt("YM_KEY"), 
												rec.getInt("Y_KEY"),
												"", 
												rec.getInt("CORRECTION_ATTR"), 
												rec.getString("ITEM_NAME"), 
												"", 
												rec.getString("VIEW_VALUE_BEFORE"), 
												"", 
												rec.getString("VIEW_VALUE_AFTER"), 
												rec.getInt("VALUE_DATA_TYPE"), 
												rec.getInt("SHOW_ORDER"), 
												"").toDomainToViewJDBC();}));
							});
							return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId IN :operationIds 
//						AND a.employeeId IN :listEmpId ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//								.setParameter("operationIds", subIdList).setParameter("listEmpId", listEmployeeId)
//								.getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					} else {
						sql += " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds and SID in @listEmployeeId"
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  correctAttrOneCondition
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll( new NtsStatement(executeSQL, this.jdbcProxy())
									  .paramDate("startYmd", period.start())
									  .paramDate("endYmd", period.end())
									  .paramString("operationIds", subIdList)
									  .paramString("listEmployeeId", listEmployeeId)
									  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
													rec.getString("USER_ID"), 
													rec.getInt("TARGET_DATA_TYPE"),
													rec.getString("ITEM_ID"),
													rec.getGeneralDate("YMD_KEY")), 
													"",
													rec.getString("SID"),
													rec.getInt("YM_KEY"), 
													rec.getInt("Y_KEY"),
													"", 
													rec.getInt("CORRECTION_ATTR"), 
													rec.getString("ITEM_NAME"), 
													"", 
													rec.getString("VIEW_VALUE_BEFORE"), 
													"", 
													rec.getString("VIEW_VALUE_AFTER"), 
													rec.getInt("VALUE_DATA_TYPE"), 
													rec.getInt("SHOW_ORDER"), 
													"").toDomainToViewJDBC();}));
							});
							return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.operationId IN :operationIds 
//						AND a.employeeId IN :listEmpId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd 
//								ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//								.setParameter("operationIds", subIdList).setParameter("listEmpId", listEmployeeId)
//								.setParameter("startYmd", period.start()).setParameter("endYmd", period.end())
//								.getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					}
				}
			}
			else {
				if (listEmployeeId == null || listEmployeeId.isEmpty()) {
					if (period.start() == null) {
						sql += " where OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType"
							+  correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql;
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
							return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection a 
//						WHERE a.pk.operationId IN :operationIds AND a.pk.targetDataType = :targetDataType 
//						ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//								.setParameter("operationIds", subIdList)
//								.setParameter("targetDataType", targetDataType.value).getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					} else {
						sql += " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType"
							+  correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramDate("startYmd", period.start())
							  .paramDate("endYmd", period.end())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .getList(rec ->	
							  {
							  	return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();
							  }));
							});
							return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection a 
//						WHERE a.pk.operationId IN :operationIds AND a.pk.targetDataType = :targetDataType 
//						AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd 
//						ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//								.setParameter("operationIds", subIdList)
//								.setParameter("targetDataType", targetDataType.value)
//								.setParameter("startYmd", period.start()).setParameter("endYmd", period.end())
//								.getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					}
				} else {
					if (period.start() == null) {
						sql += " where OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType and SID in @listEmployeeId"
							+  correctAttrOneCondition
//						    +  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .paramString("listEmployeeId", listEmployeeId)
							  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
							return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection a "
//						WHERE a.pk.operationId IN :operationIds AND a.pk.targetDataType = :targetDataType 
//						AND a.employeeId IN :listEmpId ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//								.setParameter("operationIds", subIdList)
//								.setParameter("targetDataType", targetDataType.value)
//								.setParameter("listEmpId", listEmployeeId).getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					} else {
						sql += " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType and SID in @listEmployeeId"
							+  correctAttrOneCondition
//							 + " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							 + " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramDate("startYmd", period.start())
							  .paramDate("endYmd", period.end())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .paramString("listEmployeeId", listEmployeeId)
							  	.getList(rec -> {
							  		return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
							return entities;
//						String query = "SELECT a FROM SrcdtDataCorrection a 
//						WHERE a.pk.operationId IN :operationIds AND a.pk.targetDataType = :targetDataType 
//						AND a.employeeId IN :listEmpId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd 
//						ORDER BY a.employeeId, a.pk.ymdKey, a.ymKey, a.yKey,a.showOrder";
//						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
//							entities.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
//								.setParameter("operationIds", subIdList)
//								.setParameter("targetDataType", targetDataType.value)
//								.setParameter("listEmpId", listEmployeeId).setParameter("startYmd", period.start())
//								.setParameter("endYmd", period.end()).getList());
//						});
//						entities.sort(SORT_BY_FIVE_FIELDS);
//						results.addAll(entities.stream().map(c -> c.toDomain()).collect(Collectors.toList()));
					}
				}
			}
		} else {
			throw new RuntimeException("未実装です");
		}
//		return results;
	}

	@Override
	public List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId,
			YearMonth ym, GeneralDate ymd) {
		if (targetDataType == null || listEmployeeId.isEmpty())
			return Collections.emptyList();
		String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId AND a.pk.ymdKey = :startYmd AND a.ymKey = :endYm";
		List<DataCorrectionLog> resultList = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(query, SrcdtDataCorrection.class)
				.setParameter("targetDataType", targetDataType.value).setParameter("listEmpId", subList)
				.setParameter("startYmd", ymd).setParameter("endYm", ym)
				.getList(c -> c.toDomainToView()));
		});
		return resultList;
	}

	/**
	 * findByTargetAndDateScreenF(
								operationIdSubList, logParams.getListTagetEmployeeId(), datePeriodTaget,targetDataType);
	 */
	@Override
	public List<DataCorrectionLog> findByTargetAndDateScreenF(List<String> operationIds, List<String> listEmployeeId,
			DatePeriod period, TargetDataType targetDataType) {
		List<DataCorrectionLog> results = new ArrayList<>();
		if (operationIds.isEmpty())
			return results;
		List<DataCorrectionLog> entities = new ArrayList<>();
		
		if (this.database().is(DatabaseProduct.MSSQLSERVER)) {
			// SQLServer
			String sql = "select top 1000 OPERATION_ID, USER_ID , TARGET_DATA_TYPE,  ITEM_ID, YMD_KEY," // primary Key
					+ " SID, YM_KEY, Y_KEY, STRING_KEY, CORRECTION_ATTR,  ITEM_NAME, VIEW_VALUE_BEFORE, VIEW_VALUE_AFTER, VALUE_DATA_TYPE, SHOW_ORDER" // other
					+ " from SRCDT_DATA_CORRECTION" //table name
					+ " with(index(SRCDI_DATA_CORRECTION_LOG2))";//hint
			//TODO：ログ照会レスポンス改善
			//一時対応として、出力するログ自体を減らすため、修正区分を指定
			String correctAttrOneCondition = " and CORRECTION_ATTR = '0'";
			if (targetDataType == null) {
				if (listEmployeeId == null || listEmployeeId.isEmpty()) {
					if (period.start() == null) {
//						sql += " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
						sql +=  " where OPERATION_ID in @operationIds "// condition
							+   correctAttrOneCondition
							+	" order by YMD_KEY, SID,  SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
								  	.paramString("operationIds", subIdList)
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
								  																			rec.getString("USER_ID"), 
								  																			rec.getInt("TARGET_DATA_TYPE"),
								  																			rec.getString("ITEM_ID"),
								  																			rec.getGeneralDate("YMD_KEY")), 
								  												"",
								  												rec.getString("SID"),
								  												rec.getInt("YM_KEY"), 
								  												rec.getInt("Y_KEY"),
								  												"", 
								  												rec.getInt("CORRECTION_ATTR"), 
								  												rec.getString("ITEM_NAME"), 
								  												"", 
								  												rec.getString("VIEW_VALUE_BEFORE"), 
								  												"", 
								  												rec.getString("VIEW_VALUE_AFTER"), 
								  												rec.getInt("VALUE_DATA_TYPE"), 
								  												rec.getInt("SHOW_ORDER"), 
								  												"").toDomainToViewJDBC();}));
						});
						return entities;
						
					}
					else {
						sql +=  " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds"// condition
							+   correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID,  SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll( new NtsStatement(executeSQL, this.jdbcProxy())
								  .paramString("operationIds", subIdList)
								  .paramDate("startYmd", period.start())
								  .paramDate("endYmd", period.end())
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
												rec.getString("USER_ID"), 
												rec.getInt("TARGET_DATA_TYPE"),
												rec.getString("ITEM_ID"),
												rec.getGeneralDate("YMD_KEY")), 
					"",
					rec.getString("SID"),
					rec.getInt("YM_KEY"), 
					rec.getInt("Y_KEY"),
					"", 
					rec.getInt("CORRECTION_ATTR"), 
					rec.getString("ITEM_NAME"), 
					"", 
					rec.getString("VIEW_VALUE_BEFORE"), 
					"", 
					rec.getString("VIEW_VALUE_AFTER"), 
					rec.getInt("VALUE_DATA_TYPE"), 
					rec.getInt("SHOW_ORDER"), 
					"").toDomainToViewJDBC();}));
					});
					return entities;
					}
				}
				else {
					if (period.start() == null) {
						sql += " where OPERATION_ID in @operationIds and SID in @listEmployeeId"
							+  correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
								+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmployeeId -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
									.paramString("operationIds", subIdList)
									.paramString("listEmployeeId", subEmployeeId)
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
												rec.getString("USER_ID"), 
												rec.getInt("TARGET_DATA_TYPE"),
												rec.getString("ITEM_ID"),
												rec.getGeneralDate("YMD_KEY")), 
												"",
												rec.getString("SID"),
												rec.getInt("YM_KEY"), 
												rec.getInt("Y_KEY"),
												"", 
												rec.getInt("CORRECTION_ATTR"), 
												rec.getString("ITEM_NAME"), 
												"", 
												rec.getString("VIEW_VALUE_BEFORE"), 
												"", 
												rec.getString("VIEW_VALUE_AFTER"), 
												rec.getInt("VALUE_DATA_TYPE"), 
												rec.getInt("SHOW_ORDER"), 
												"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					} else {
						sql += " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds and SID in @listEmployeeId"
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  correctAttrOneCondition
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmployeeId -> {
							entities.addAll( new NtsStatement(executeSQL, this.jdbcProxy())
									  .paramDate("startYmd", period.start())
									  .paramDate("endYmd", period.end())
									  .paramString("operationIds", subIdList)
									  .paramString("listEmployeeId", subEmployeeId)
									  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
													rec.getString("USER_ID"), 
													rec.getInt("TARGET_DATA_TYPE"),
													rec.getString("ITEM_ID"),
													rec.getGeneralDate("YMD_KEY")), 
													"",
													rec.getString("SID"),
													rec.getInt("YM_KEY"), 
													rec.getInt("Y_KEY"),
													"", 
													rec.getInt("CORRECTION_ATTR"), 
													rec.getString("ITEM_NAME"), 
													"", 
													rec.getString("VIEW_VALUE_BEFORE"), 
													"", 
													rec.getString("VIEW_VALUE_AFTER"), 
													rec.getInt("VALUE_DATA_TYPE"), 
													rec.getInt("SHOW_ORDER"), 
													"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					}
				}
			}
			else {
				if (listEmployeeId == null || listEmployeeId.isEmpty()) {
					if (period.start() == null) {
						sql += " where OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType"
							+  correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql;
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
							return entities;
					} else {
						sql += " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType"
							+  correctAttrOneCondition
//							+  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramDate("startYmd", period.start())
							  .paramDate("endYmd", period.end())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .getList(rec ->	
							  {
							  	return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();
							  }));
							});
							return entities;
					}
				} else {
					if (period.start() == null) {
						sql += " where OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType and SID in @listEmployeeId"
							+  correctAttrOneCondition
//						    +  " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							+  " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmployeeId -> {
							
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .paramString("listEmployeeId", subEmployeeId)
							  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					} else {
						sql += " where YMD_KEY between @startYmd and @endYmd and OPERATION_ID in @operationIds and TARGET_DATA_TYPE = @targetDataType and SID in @listEmployeeId"
							+  correctAttrOneCondition
//							 + " order by SID, YMD_KEY, YM_KEY, Y_KEY, SHOW_ORDER";
							 + " order by YMD_KEY, SID, SHOW_ORDER";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
								CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmpList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramDate("startYmd", period.start())
							  .paramDate("endYmd", period.end())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .paramString("listEmployeeId", subEmpList)
							  	.getList(rec -> {
							  		return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					}
				}
			}
		} else {
			throw new RuntimeException("未実装です");
		}
	}

	@Override
	public List<DataCorrectionLog> findByTargetAndDateRefactors(List<String> operationIds, List<String> listEmployeeId,
			DatePeriod period, TargetDataType targetDataType, int offset, int limit) {
		this.getEntityManager().clear();
		List<DataCorrectionLog> results = new ArrayList<>();
		if (operationIds.isEmpty())
			return results;
		List<DataCorrectionLog> entities = new ArrayList<>();
		
		if (this.database().is(DatabaseProduct.MSSQLSERVER)) {
			// SQLServer
			String sql = "SELECT OPERATION_ID, USER_ID , TARGET_DATA_TYPE,  ITEM_ID, YMD_KEY," // primary Key
					+ " SID, YM_KEY, Y_KEY, STRING_KEY, CORRECTION_ATTR,  ITEM_NAME, VIEW_VALUE_BEFORE, VIEW_VALUE_AFTER, VALUE_DATA_TYPE, SHOW_ORDER" // other
					+ " FROM SRCDT_DATA_CORRECTION" //table name
					+ " WITH(INDEX(SRCDI_DATA_CORRECTION_LOG2))";//hint
			//TODO：ログ照会レスポンス改善
			//一時対応として、出力するログ自体を減らすため、修正区分を指定
			String correctAttrOneCondition = " AND CORRECTION_ATTR = '0'";

			
			if (targetDataType == null) {
				if (listEmployeeId == null || listEmployeeId.isEmpty()) {
					if (period.start() == null) {
						sql +=  " WHERE OPERATION_ID IN @operationIds "// condition
							+   correctAttrOneCondition
							+	" ORDER BY YMD_KEY, SID,  SHOW_ORDER"
							+ 	" OFFSET " + offset + " ROWS"
							+ 	" FETCH FIRST " + limit + " ROWS ONLY";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
								  	.paramString("operationIds", subIdList)
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
								  																			rec.getString("USER_ID"), 
								  																			rec.getInt("TARGET_DATA_TYPE"),
								  																			rec.getString("ITEM_ID"),
								  																			rec.getGeneralDate("YMD_KEY")), 
								  												"",
								  												rec.getString("SID"),
								  												rec.getInt("YM_KEY"), 
								  												rec.getInt("Y_KEY"),
								  												"", 
								  												rec.getInt("CORRECTION_ATTR"), 
								  												rec.getString("ITEM_NAME"), 
								  												"", 
								  												rec.getString("VIEW_VALUE_BEFORE"), 
								  												"", 
								  												rec.getString("VIEW_VALUE_AFTER"), 
								  												rec.getInt("VALUE_DATA_TYPE"), 
								  												rec.getInt("SHOW_ORDER"), 
								  												"").toDomainToViewJDBC();}));
						});
						return entities;
						
					}
					else {
						sql +=  " WHERE YMD_KEY BETWEEN @startYmd AND @endYmd AND OPERATION_ID IN @operationIds"// condition
							+   correctAttrOneCondition
							+  	" ORDER BY YMD_KEY, SID,  SHOW_ORDER"
							+ 	" OFFSET " + offset + " ROWS"
							+ 	" FETCH FIRST " + limit + " ROWS ONLY";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll( new NtsStatement(executeSQL, this.jdbcProxy())
								  .paramString("operationIds", subIdList)
								  .paramDate("startYmd", period.start())
								  .paramDate("endYmd", period.end())
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
												rec.getString("USER_ID"), 
												rec.getInt("TARGET_DATA_TYPE"),
												rec.getString("ITEM_ID"),
												rec.getGeneralDate("YMD_KEY")), 
					"",
					rec.getString("SID"),
					rec.getInt("YM_KEY"), 
					rec.getInt("Y_KEY"),
					"", 
					rec.getInt("CORRECTION_ATTR"), 
					rec.getString("ITEM_NAME"), 
					"", 
					rec.getString("VIEW_VALUE_BEFORE"), 
					"", 
					rec.getString("VIEW_VALUE_AFTER"), 
					rec.getInt("VALUE_DATA_TYPE"), 
					rec.getInt("SHOW_ORDER"), 
					"").toDomainToViewJDBC();}));
					});
					return entities;
					}
				}
				else {
					if (period.start() == null) {
						sql += " WHERE OPERATION_ID IN @operationIds AND SID IN @listEmployeeId"
							+  correctAttrOneCondition
							+  " ORDER BY YMD_KEY, SID, SHOW_ORDER"
							+  " OFFSET " + offset + " ROWS"
							+  " FETCH FIRST " + limit + " ROWS ONLY";						
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmpList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
									.paramString("operationIds", subIdList)
									.paramString("listEmployeeId", subEmpList)
								  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
												rec.getString("USER_ID"), 
												rec.getInt("TARGET_DATA_TYPE"),
												rec.getString("ITEM_ID"),
												rec.getGeneralDate("YMD_KEY")), 
												"",
												rec.getString("SID"),
												rec.getInt("YM_KEY"), 
												rec.getInt("Y_KEY"),
												"", 
												rec.getInt("CORRECTION_ATTR"), 
												rec.getString("ITEM_NAME"), 
												"", 
												rec.getString("VIEW_VALUE_BEFORE"), 
												"", 
												rec.getString("VIEW_VALUE_AFTER"), 
												rec.getInt("VALUE_DATA_TYPE"), 
												rec.getInt("SHOW_ORDER"), 
												"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					} else {
						sql += " WHERE YMD_KEY BETWEEN @startYmd AND @endYmd AND OPERATION_ID IN @operationIds AND SID IN @listEmployeeId"
							+  correctAttrOneCondition
							+  " ORDER BY YMD_KEY, SID, SHOW_ORDER"
							+  " OFFSET " + offset + " ROWS"
							+  " FETCH FIRST " + limit + " ROWS ONLY";		
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmpIdList -> {
							entities.addAll( new NtsStatement(executeSQL, this.jdbcProxy())
									  .paramDate("startYmd", period.start())
									  .paramDate("endYmd", period.end())
									  .paramString("operationIds", subIdList)
									  .paramString("listEmployeeId", subEmpIdList)
									  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
													rec.getString("USER_ID"), 
													rec.getInt("TARGET_DATA_TYPE"),
													rec.getString("ITEM_ID"),
													rec.getGeneralDate("YMD_KEY")), 
													"",
													rec.getString("SID"),
													rec.getInt("YM_KEY"), 
													rec.getInt("Y_KEY"),
													"", 
													rec.getInt("CORRECTION_ATTR"), 
													rec.getString("ITEM_NAME"), 
													"", 
													rec.getString("VIEW_VALUE_BEFORE"), 
													"", 
													rec.getString("VIEW_VALUE_AFTER"), 
													rec.getInt("VALUE_DATA_TYPE"), 
													rec.getInt("SHOW_ORDER"), 
													"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					}
				}
			}
			else {
				if (listEmployeeId == null || listEmployeeId.isEmpty()) {
					if (period.start() == null) {
						sql += " WHERE OPERATION_ID IN @operationIds AND TARGET_DATA_TYPE = @targetDataType"
							+  correctAttrOneCondition
							+  " ORDER BY YMD_KEY, SID, SHOW_ORDER"
							+  " OFFSET " + offset + " ROWS"
							+  " FETCH FIRST " + limit + " ROWS ONLY";
						final String executeSQL = sql;
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramString("operationIds", operationIds)
							  .paramInt("targetDataType", targetDataType.value)
							  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
							return entities;
					} else {
						sql += " WHERE YMD_KEY BETWEEN @startYmd AND @endYmd AND OPERATION_ID IN @operationIds AND TARGET_DATA_TYPE = @targetDataType"
							+  correctAttrOneCondition
							+  " ORDER BY YMD_KEY, SID, SHOW_ORDER"
							+  " OFFSET " + offset + " ROWS"
							+  " FETCH FIRST " + limit + " ROWS ONLY";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramDate("startYmd", period.start())
							  .paramDate("endYmd", period.end())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .getList(rec ->	
							  {
							  	return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();
							  }));
							});
							return entities;
					}
				} else {
					if (period.start() == null) {
						sql += " WHERE OPERATION_ID IN @operationIds AND TARGET_DATA_TYPE = @targetDataType AND SID IN @listEmployeeId"
							+  correctAttrOneCondition
							+  " ORDER BY YMD_KEY, SID, SHOW_ORDER"
							+  " OFFSET " + offset + " ROWS"
							+  " FETCH FIRST " + limit + " ROWS ONLY";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmpList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .paramString("listEmployeeId", subEmpList)
							  	.getList(rec -> {return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
							});
							return entities;
					} else {
						sql += " WHERE YMD_KEY BETWEEN @startYmd AND @endYmd AND OPERATION_ID IN @operationIds AND TARGET_DATA_TYPE = @targetDataType AND SID IN @listEmployeeId"
							+  correctAttrOneCondition
							+ " ORDER BY YMD_KEY, SID, SHOW_ORDER"
							+  " OFFSET " + offset + " ROWS"
							+  " FETCH FIRST " + limit + " ROWS ONLY";
						final String executeSQL = sql; 
						CollectionUtil.split(operationIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
							CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subEmpIdList -> {
							entities.addAll(new NtsStatement(executeSQL, this.jdbcProxy())
							  .paramDate("startYmd", period.start())
							  .paramDate("endYmd", period.end())
							  .paramString("operationIds", subIdList)
							  .paramInt("targetDataType", targetDataType.value)
							  .paramString("listEmployeeId", subEmpIdList)
							  	.getList(rec -> {
							  		return new SrcdtDataCorrection(new SrcdtDataCorrectionLogPk(rec.getString("OPERATION_ID"), 
											rec.getString("USER_ID"), 
											rec.getInt("TARGET_DATA_TYPE"),
											rec.getString("ITEM_ID"),
											rec.getGeneralDate("YMD_KEY")), 
											"",
											rec.getString("SID"),
											rec.getInt("YM_KEY"), 
											rec.getInt("Y_KEY"),
											"", 
											rec.getInt("CORRECTION_ATTR"), 
											rec.getString("ITEM_NAME"), 
											"", 
											rec.getString("VIEW_VALUE_BEFORE"), 
											"", 
											rec.getString("VIEW_VALUE_AFTER"), 
											rec.getInt("VALUE_DATA_TYPE"), 
											rec.getInt("SHOW_ORDER"), 
											"").toDomainToViewJDBC();}));
							});
						});
							return entities;
					}
				}
			}
		} else {
			throw new RuntimeException("未実装です");
		}
	}

	@Override
	public List<DataCorrectionLog> getAllLogDataByYM(TargetDataType targetDataType, List<String> listEmployeeId,
			YearMonth ym, GeneralDate ymd) {
		if (targetDataType == null || listEmployeeId.isEmpty())
			return Collections.emptyList();
		Connection con = this.getEntityManager().unwrap(Connection.class);
//		String query = "SELECT a FROM SrcdtDataCorrection a WHERE a.pk.targetDataType = :targetDataType AND a.employeeId IN :listEmpId AND a.pk.ymdKey >= :startYmd AND a.pk.ymdKey <= :endYmd";
		List<DataCorrectionLog> resultList = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String listEmp = "(";
			for(int i = 0; i < subList.size(); i++){
				listEmp += "'"+ subList.get(i) +"',";
			}
			// remove last , in string and add )
			listEmp = listEmp.substring(0, listEmp.length() - 1) + ")";
			String query = "SELECT OPERATION_ID, USER_ID, TARGET_DATA_TYPE, ITEM_ID, USER_NAME, SID, YMD_KEY, YM_KEY, Y_KEY, STRING_KEY, CORRECTION_ATTR, ITEM_NAME, RAW_VALUE_BEFORE_ID, VIEW_VALUE_BEFORE, RAW_VALUE_AFTER_ID, VIEW_VALUE_AFTER, VALUE_DATA_TYPE, SHOW_ORDER, NOTE"
					+ " FROM SRCDT_DATA_CORRECTION "
					+ " WHERE TARGET_DATA_TYPE = "+ "'" + targetDataType.value + "'" 
					+ " AND SID IN " + listEmp 
					+ " AND YMD_KEY = " + "'" + ymd + "'" 
					+ " AND YM_KEY = " + "'" + ym.v() + "'";
			try {
				ResultSet rs = con.createStatement().executeQuery(query);
				while (rs.next()) {
					String operationId = rs.getString("OPERATION_ID");
					String userId = rs.getString("USER_ID");
					String sId = rs.getString("SID");
					String userName = rs.getString("USER_NAME");
					int targetDtType = rs.getInt("TARGET_DATA_TYPE");
					String stringKey = rs.getString("STRING_KEY");
					int correctionAttr = rs.getInt("CORRECTION_ATTR");
					String itemId = rs.getString("ITEM_ID");
					String itemName = rs.getString("ITEM_NAME");
					String viewValueBefore = rs.getString("VIEW_VALUE_BEFORE");
					String viewValueAfter = rs.getString("VIEW_VALUE_AFTER");
					String rawValueBefore = rs.getString("RAW_VALUE_BEFORE_ID");
					String rawValueAfter = rs.getString("RAW_VALUE_AFTER_ID");
					int showOrder = rs.getInt("SHOW_ORDER");
					String note = rs.getString("NOTE");
					GeneralDate ymdKey = GeneralDate.fromString(rs.getString("YMD_KEY"), "yyyy-MM-dd");
					Integer ymKey = Integer.valueOf(rs.getInt("YM_KEY"));
					Integer yKey = Integer.valueOf(rs.getInt("Y_KEY"));
					Integer valueType = Integer.valueOf(rs.getInt("VALUE_DATA_TYPE"));
					
					SrcdtDataCorrectionLogPk pk = new SrcdtDataCorrectionLogPk(operationId, userId, targetDtType,
							itemId, ymdKey);
					SrcdtDataCorrection srcdtDataCorrectionLog = new SrcdtDataCorrection(pk, userName, sId, ymKey,
							yKey, stringKey, correctionAttr, itemName, rawValueBefore, viewValueBefore, rawValueAfter,
							viewValueAfter, valueType, showOrder, note);
					
					resultList.add(srcdtDataCorrectionLog.toDomainToView());
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return resultList;
	}
		
	String GET_LOG_BASIC = "SELECT OPERATION_ID, USER_ID, TARGET_DATA_TYPE, ITEM_ID, USER_NAME, SID, YMD_KEY, YM_KEY, Y_KEY, STRING_KEY, CORRECTION_ATTR, ITEM_NAME, RAW_VALUE_BEFORE_ID, VIEW_VALUE_BEFORE, RAW_VALUE_AFTER_ID, VIEW_VALUE_AFTER, VALUE_DATA_TYPE, SHOW_ORDER, NOTE"
			+ " FROM SRCDT_DATA_CORRECTION "
			+ " WHERE TARGET_DATA_TYPE = @type" 
			+ " AND SID = @sid" 
			 +" AND YMD_KEY = @ymd"
			+ " AND ITEM_ID = @itemId" ;
	
	@Override
	public List<DataCorrectionLog> getInfoLog(String sid, GeneralDate targetDate, Integer itemId, TargetDataType type) {
		return new NtsStatement(GET_LOG_BASIC, this.jdbcProxy()).paramInt("type", type.value).paramString("sid", sid)
				.paramDate("ymd", targetDate).paramInt("itemId", itemId).getList(x -> convertRsToDomain(x));
	}
	
	private DataCorrectionLog convertRsToDomain(NtsResultRecord rs) {
		String operationId = rs.getString("OPERATION_ID");
		String userId = rs.getString("USER_ID");
		String sId = rs.getString("SID");
		String userName = rs.getString("USER_NAME");
		int targetDtType = rs.getInt("TARGET_DATA_TYPE");
		String stringKey = rs.getString("STRING_KEY");
		int correctionAttr = rs.getInt("CORRECTION_ATTR");
		String itemId = rs.getString("ITEM_ID");
		String itemName = rs.getString("ITEM_NAME");
		String viewValueBefore = rs.getString("VIEW_VALUE_BEFORE");
		String viewValueAfter = rs.getString("VIEW_VALUE_AFTER");
		String rawValueBefore = rs.getString("RAW_VALUE_BEFORE_ID");
		String rawValueAfter = rs.getString("RAW_VALUE_AFTER_ID");
		int showOrder = rs.getInt("SHOW_ORDER");
		String note = rs.getString("NOTE");
		GeneralDate ymdKey = GeneralDate.fromString(rs.getString("YMD_KEY"), "yyyy-MM-dd");
		Integer ymKey = Integer.valueOf(rs.getInt("YM_KEY"));
		Integer yKey = Integer.valueOf(rs.getInt("Y_KEY"));
		Integer valueType = rs.getInt("VALUE_DATA_TYPE") == null ? null : Integer.valueOf(rs.getInt("VALUE_DATA_TYPE"));

		SrcdtDataCorrectionLogPk pk = new SrcdtDataCorrectionLogPk(operationId, userId, targetDtType, itemId, ymdKey);
		SrcdtDataCorrection srcdtDataCorrectionLog = new SrcdtDataCorrection(pk, userName, sId, ymKey, yKey, stringKey,
				correctionAttr, itemName, rawValueBefore, viewValueBefore, rawValueAfter, viewValueAfter, valueType,
				showOrder, note);
		return srcdtDataCorrectionLog.toDomainToView();
	}
}
