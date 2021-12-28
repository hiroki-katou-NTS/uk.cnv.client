package nts.uk.ctx.sys.assist.infra.repository.tablelist;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.assist.dom.category.TimeStore;
import nts.uk.ctx.sys.assist.dom.categoryfieldmt.HistoryDiviSion;
import nts.uk.ctx.sys.assist.dom.saveprotetion.SaveProtetion;
import nts.uk.ctx.sys.assist.dom.saveprotetion.SaveProtetionRepository;
import nts.uk.ctx.sys.assist.dom.tablelist.TableList;
import nts.uk.ctx.sys.assist.dom.tablelist.TableListRepository;
import nts.uk.ctx.sys.assist.infra.entity.tablelist.SspdtSaveTableList;
import nts.uk.ctx.sys.assist.infra.entity.tablelist.SspmtTableListPk;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.file.csv.CSVReportGenerator;
import nts.uk.shr.infra.file.csv.CsvReportWriter;

@Stateless
public class JpaTableListRepository extends JpaRepository implements TableListRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT t FROM SspdtSaveTableList t WHERE t.tableListPk.dataStorageProcessingId =:storeProcessingId";
	private static final String SELECT_COLUMN_NAME_MSSQL = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?tableName";
	private static final String SELECT_BY_SYSTEM_TYPE_AND_STORAGE_ID = "SELECT t FROM SspdtSaveTableList t "
			+ "	WHERE t.tableListPk.systemType =:systemType "
			+ "	AND t.tableListPk.dataStorageProcessingId =:dataStorageProcessingId	";
	private static final String SELECT_BY_SYSTEM_TYPE_AND_RECOVER_ID = "SELECT t FROM SspdtSaveTableList t "
			+ "	WHERE t.tableListPk.systemType =:systemType "
			+ "	AND t.tableListPk.dataStorageProcessingId =:dataStorageProcessingId	";
	private static final String COMPANY_CD = "0";
	private static final String EMPLOYEE_CD = "5";
	private static final String YEAR = "6";
	private static final String YEAR_MONTH = "7";
	private static final String YEAR_MONTH_DAY = "8";

	@Inject
	private SaveProtetionRepository saveProtetionRepo;

	@Inject
	private CSVReportGenerator generator;

	private static final String CSV_EXTENSION = ".csv";

	@Override
	public void add(TableList domain) {
		this.commandProxy().insert(SspdtSaveTableList.toEntity(domain));
	}

	@Override
	public void update(TableList domain) {
		this.commandProxy().update(SspdtSaveTableList.toEntity(domain));
	}

	@Override
	public void add2(TableList domain) {
		// Get entity by key
		Optional<SspdtSaveTableList> entityOpt = this
				.queryProxy().find(
						new SspmtTableListPk(domain.getCategoryId(), domain.getTableNo(),
								domain.getDataStorageProcessingId(), domain.getSystemType().value),
						SspdtSaveTableList.class);
		if (!entityOpt.isPresent()) {
			this.commandProxy().insert(SspdtSaveTableList.toEntity(domain));
		}
	}

	@Override
	public void update2(TableList domain) {
		// Get entity by key
		Optional<SspdtSaveTableList> entityOpt = this
				.queryProxy().find(
						new SspmtTableListPk(domain.getCategoryId(), domain.getTableNo(),
								domain.getDataStorageProcessingId(), domain.getSystemType().value),
						SspdtSaveTableList.class);
		if (entityOpt.isPresent()) {
			SspdtSaveTableList entity = entityOpt.get();
			updateEntity(entity, domain);
			this.commandProxy().update(entity);
		}
	}

	@Override
	public void remove(TableList domain) {
		// Get entity by key
		Optional<SspdtSaveTableList> entityOpt = this
				.queryProxy().find(
						new SspmtTableListPk(domain.getCategoryId(), domain.getTableNo(),
								domain.getDataStorageProcessingId(), domain.getSystemType().value),
						SspdtSaveTableList.class);
		if (entityOpt.isPresent()) {
			this.commandProxy().remove(SspdtSaveTableList.class, new SspmtTableListPk(domain.getCategoryId(),
					domain.getTableNo(), domain.getDataStorageProcessingId(), domain.getSystemType().value));
		}
	}

	@Override
	public List<TableList> getByOffsetAndNumber(String storeProcessingId, int offset, int number) {
		List<SspdtSaveTableList> listTable = this.getEntityManager()
				.createQuery(SELECT_ALL_QUERY_STRING, SspdtSaveTableList.class)
				.setParameter("storeProcessingId", storeProcessingId).setFirstResult(offset).setMaxResults(number)
				.getResultList();

		return listTable.stream().map(item -> item.toDomain()).collect(Collectors.toList());
	}

	@Override
	public List<TableList> getByProcessingId(String storeProcessingId) {
		return this.queryProxy().query(SELECT_ALL_QUERY_STRING, SspdtSaveTableList.class)
				.setParameter("storeProcessingId", storeProcessingId).getList(c -> c.toDomain());
	}

	private String getFieldAcq(List<String> allColumns, Optional<String> fieldName, String fieldAcqName) {
		String fieldAcq = fieldName.orElse("");
		if (!StringUtil.isNullOrEmpty(fieldAcq, true)) {
			if (allColumns.contains(fieldAcq)) {
				return " t." + fieldAcq + " AS " + fieldAcqName + ", ";
			} else {
				return " p." + fieldAcq + " AS " + fieldAcqName + ", ";
			}
		} else {
			return " '' AS " + fieldAcqName + ", ";
		}
	}

	@Override
	public void getDataDynamic(TableList tableList, List<String> targetEmployeesSid, List<String> headerCsv3,
			FileGeneratorContext generatorContext) {
		StringBuffer query = new StringBuffer("");
		// All Column
		List<String> columns = getAllColumnName(tableList.getTableEnglishName());
		// Select
		query.append("SELECT ");

		query.append(getFieldAcq(columns, tableList.getFieldAcqCid(), "H_CID"));
		query.append(getFieldAcq(columns, tableList.getFieldAcqEmployeeId(), "H_SID"));
		query.append(getFieldAcq(columns, tableList.getFieldAcqDateTime(), "H_DATE"));
		query.append(getFieldAcq(columns, tableList.getFieldAcqStartDate(), "H_DATE_START"));
		query.append(getFieldAcq(columns, tableList.getFieldAcqEndDate(), "H_DATE_END"));

		// All Column
		for (int i = 0; i < columns.size(); i++) {
			query.append(" t.").append(columns.get(i));
			if (i < columns.size() - 1) {
				query.append(",");
			}
		}

		// アルゴリズム「個人情報の保護」を実行する
		List<SaveProtetion> listSaveProtetion = saveProtetionRepo
				.getSaveProtection(Integer.valueOf(tableList.getCategoryId()), tableList.getTableNo());
		boolean saveProtectionByEmpCode = false;
		String couplePidItemName = "";
		if (tableList.getSurveyPreservation() == NotUseAtr.USE && !listSaveProtetion.isEmpty()) {
			for (int i = 0; i < listSaveProtetion.size(); i++) {
				String rePlaceCol = listSaveProtetion.get(i).getReplaceColumn().trim();
				String pidCol = listSaveProtetion.get(i).getCouplePidItemName().trim();
				String newValue = "";
				// Vì domain không tạo Enum nên phải fix code ngu
				if (listSaveProtetion.get(i).getCorrectClasscification() == 0) {
					newValue = "'' AS " + rePlaceCol;
				}
				if (listSaveProtetion.get(i).getCorrectClasscification() == 1) {
					saveProtectionByEmpCode = true;
					couplePidItemName = listSaveProtetion.get(i).getCouplePidItemName();
					newValue = " bdm.SCD AS " + rePlaceCol;
				}
				if (listSaveProtetion.get(i).getCorrectClasscification() == 2) {
					newValue = "0 AS " + rePlaceCol;
				}
				if (listSaveProtetion.get(i).getCorrectClasscification() == 3) {
					newValue = "'0' AS " + rePlaceCol;
				}

				String subString = "t." + rePlaceCol + ",";
				if (query.indexOf(subString) > 0) {
					query = new StringBuffer(query.toString().replaceAll("t." + rePlaceCol + ",", newValue + ","));
				} else {
					query = new StringBuffer(query.toString().replaceAll("t." + rePlaceCol, newValue));
				}

			}
		}

		// From
		query.append(" FROM ").append(tableList.getTableEnglishName()).append(" t");
		if (saveProtectionByEmpCode) {

			query.append(
					" LEFT JOIN (SELECT PID, MIN(SCD) AS SCD FROM BSYMT_SYAIN GROUP BY PID) bdm ON bdm.PID = t."
							+ couplePidItemName);

		}

		if (tableList.getHasParentTblFlg() == NotUseAtr.USE && tableList.getParentTblName().isPresent()) {
			// アルゴリズム「親テーブルをJOINする」を実行する
			query.append(" INNER JOIN ").append(tableList.getParentTblName().get()).append(" p ON ");

			String[] parentFields = { tableList.getFieldParent1().orElse(""), tableList.getFieldParent2().orElse(""),
					tableList.getFieldParent3().orElse(""), tableList.getFieldParent4().orElse(""),
					tableList.getFieldParent5().orElse(""), tableList.getFieldParent6().orElse(""),
					tableList.getFieldParent7().orElse(""), tableList.getFieldParent8().orElse(""),
					tableList.getFieldParent9().orElse(""), tableList.getFieldParent10().orElse("") };

			String[] childFields = { tableList.getFieldChild1().orElse(""), tableList.getFieldChild2().orElse(""),
					tableList.getFieldChild3().orElse(""), tableList.getFieldChild4().orElse(""),
					tableList.getFieldChild5().orElse(""), tableList.getFieldChild6().orElse(""),
					tableList.getFieldChild7().orElse(""), tableList.getFieldChild8().orElse(""),
					tableList.getFieldChild9().orElse(""), tableList.getFieldChild10().orElse("") };

			boolean isFirstOnStatement = true;
			for (int i = 0; i < parentFields.length; i++) {
				if (!StringUtil.isNullOrEmpty(parentFields[i], true) && !StringUtil.isNullOrEmpty(childFields[i], true)) {
					if (!isFirstOnStatement) {
						query.append(" AND ");
					}
					isFirstOnStatement = false;
					query.append("p." + parentFields[i] + "=" + "t." + childFields[i]);
				}
			}
		}

		String[] fieldKeyQuerys = { tableList.getFieldKeyQuery1().orElse(""), tableList.getFieldKeyQuery2().orElse(""),
				tableList.getFieldKeyQuery3().orElse(""), tableList.getFieldKeyQuery4().orElse(""),
				tableList.getFieldKeyQuery5().orElse(""), tableList.getFieldKeyQuery6().orElse(""),
				tableList.getFieldKeyQuery7().orElse(""), tableList.getFieldKeyQuery8().orElse(""),
				tableList.getFieldKeyQuery9().orElse(""), tableList.getFieldKeyQuery10().orElse("") };
		String[] clsKeyQuerys = { tableList.getClsKeyQuery1().orElse(""), tableList.getClsKeyQuery2().orElse(""),
				tableList.getClsKeyQuery3().orElse(""), tableList.getClsKeyQuery4().orElse(""),
				tableList.getClsKeyQuery5().orElse(""), tableList.getClsKeyQuery6().orElse(""),
				tableList.getClsKeyQuery7().orElse(""), tableList.getClsKeyQuery8().orElse(""),
				tableList.getClsKeyQuery9().orElse(""), tableList.getClsKeyQuery10().orElse("") };

		// Where
		query.append(" WHERE 1 = 1 ");

		List<Integer> companyCdIndexs = new ArrayList<Integer>();
		List<Integer> yearIndexs = new ArrayList<Integer>();
		List<Integer> yearMonthIndexs = new ArrayList<Integer>();
		List<Integer> yearMonthDayIndexs = new ArrayList<Integer>();
		for (int i = 0; i < clsKeyQuerys.length; i++) {
			if (clsKeyQuerys[i] == "")
				continue;
			switch (clsKeyQuerys[i]) {
			case COMPANY_CD:
				companyCdIndexs.add(i);
				break;

			case YEAR:
				yearIndexs.add(i);
				break;

			case YEAR_MONTH:
				yearMonthIndexs.add(i);
				break;

			case YEAR_MONTH_DAY:
				yearMonthDayIndexs.add(i);
				break;

			default:
				break;
			}
		}

		Map<String, Object> params = new HashMap<>();
		if (companyCdIndexs.size() > 0) {
			query.append(" AND ( ");
			boolean isFirstOrStatement = true;
			for (Integer index : companyCdIndexs) {
				if (!isFirstOrStatement) {
					query.append(" OR ");
				}
				isFirstOrStatement = false;
				if (columns.contains(fieldKeyQuerys[index])) {
					query.append(" t.");
					query.append(fieldKeyQuerys[index]);
				} else {
					query.append(" p.");
					query.append(fieldKeyQuerys[index]);
				}
				query.append(" = ?companyId ");
				params.put("companyId", AppContexts.user().companyId());
			}
			query.append(" ) ");
		}

		// 履歴区分を判別する。履歴なしの場合
		List<Integer> indexs = new ArrayList<Integer>();
		if (tableList.getHistoryCls() == HistoryDiviSion.NO_HISTORY) {
			switch (tableList.getRetentionPeriodCls()) {
			case ANNUAL:
				indexs = yearIndexs;
				break;
			case MONTHLY:
				indexs = yearMonthIndexs;
				break;
			case DAILY:
				indexs = yearMonthDayIndexs;
				break;

			default:
				break;
			}

			if (indexs.size() > 0) {
				query.append(" AND ( ");
				boolean isFirstOrStatement = true;
				for (int i = 0; i < indexs.size(); i++) {
					if (!isFirstOrStatement) {
						query.append(" OR ");
					}
					isFirstOrStatement = false;
					// Start Date
					if (columns.contains(fieldKeyQuerys[indexs.get(i)])) {
						query.append(" (t.");
						query.append(fieldKeyQuerys[indexs.get(i)]);
					} else {
						query.append(" (p.");
						query.append(fieldKeyQuerys[indexs.get(i)]);
					}

					query.append(" >= ?startDate ");
					query.append(" AND ");

					// End Date
					if (columns.contains(fieldKeyQuerys[indexs.get(i)])) {
						query.append(" t.");
						query.append(fieldKeyQuerys[indexs.get(i)]);
					} else {
						query.append(" p.");
						query.append(fieldKeyQuerys[indexs.get(i)]);
					}

					query.append(" <= ?endDate) ");

					// fix bug #103051
					if ((indexs.size() > 1) && (i == indexs.size() - 1)
							&& (tableList.getRetentionPeriodCls() == TimeStore.DAILY) && indexs.size() >= 2) {
						query.append(" OR ");
						// Start Date
						if (columns.contains(fieldKeyQuerys[indexs.get(i - 1)])) {
							query.append(" (t.");
							query.append(fieldKeyQuerys[indexs.get(i - 1)]);
						} else {
							query.append(" (p.");
							query.append(fieldKeyQuerys[indexs.get(i - 1)]);
						}

						query.append(" <= ?startDate ");
						query.append(" AND ");

						// End Date
						if (columns.contains(fieldKeyQuerys[indexs.get(i - 0)])) {
							query.append(" t.");
							query.append(fieldKeyQuerys[indexs.get(i - 0)]);
						} else {
							query.append(" p.");
							query.append(fieldKeyQuerys[indexs.get(i - 0)]);
						}

						isFirstOrStatement = true;

						query.append(" >= ?endDate) ");
					}

					switch (tableList.getRetentionPeriodCls()) {
					case DAILY:
						params.put("startDate",
								tableList.getSaveDateFrom().map(v -> v.concat(" 00:00:00")).orElse(null));
						params.put("endDate", tableList.getSaveDateTo().map(v -> v.concat(" 23:59:59")).orElse(null));
						break;
					case MONTHLY:
						params.put("startDate", Integer
								.valueOf(tableList.getSaveDateFrom().map(v -> v.replaceAll("\\/", "")).orElse(null)));
						params.put("endDate", Integer
								.valueOf(tableList.getSaveDateTo().map(v -> v.replaceAll("\\/", "")).orElse(null)));
						break;
					case ANNUAL:
						params.put("startDate", Integer.valueOf(tableList.getSaveDateFrom().orElse(null)));
						params.put("endDate", Integer.valueOf(tableList.getSaveDateTo().orElse(null)));
						break;

					default:
						break;
					}
				}
				query.append(" ) ");
			}
		}

		// 抽出条件キー固定 - fix bug #108094
		Optional<String> defaultCondKeyQuery = tableList.getDefaultCondKeyQuery();
		if (defaultCondKeyQuery.isPresent()) {
			if (defaultCondKeyQuery.get() != null && !"null".equals(defaultCondKeyQuery.get())
					&& !defaultCondKeyQuery.get().isEmpty()) {
				query.append(" AND " + tableList.getDefaultCondKeyQuery().get());
			}
		}
		for (int i = 0; i < clsKeyQuerys.length; i++) {
			if (EMPLOYEE_CD.equals(clsKeyQuerys[i]) && !targetEmployeesSid.isEmpty()) {
				if (tableList.getHasParentTblFlg() == NotUseAtr.USE) {
					query.append(" AND p." + fieldKeyQuerys[i] + " IN (?listTargetSid) ");
				} else {
					query.append(" AND t." + fieldKeyQuerys[i] + " IN (?listTargetSid) ");
				}
			}
		}

		// Order By
		query.append(" ORDER BY H_CID, H_SID, H_DATE, H_DATE_START");
		String querySql = query.toString();

		if (!targetEmployeesSid.isEmpty() && query.toString().contains("?listTargetSid")) {

			List<String> lSid = new ArrayList<>();
			CollectionUtil.split(targetEmployeesSid, 100, subIdList -> {
				lSid.add(subIdList.toString().replaceAll("\\[", "\\'").replaceAll("\\]", "\\'").replaceAll(", ",
						"\\', '"));
			});

			CsvReportWriter csv = generator.generate(generatorContext, AppContexts.user().companyId()
					+ tableList.getCategoryName() + tableList.getTableJapaneseName() + CSV_EXTENSION, headerCsv3,
					"UTF-8");

			for (String sid : lSid) {

				int offset = 0;

				Query queryString = getEntityManager().createNativeQuery(querySql);

				if (query.toString().contains("?listTargetSid")) {
					queryString = getEntityManager().createNativeQuery(querySql.replaceAll("\\?listTargetSid", sid));
				}
				for (Entry<String, Object> entry : params.entrySet()) {
					queryString.setParameter(entry.getKey(), entry.getValue());
				}

				List<Object[]> listObjs = new ArrayList<>();

				while ((listObjs = queryString.setFirstResult(offset).setMaxResults(10000).getResultList())
						.size() > 0) {
					this.getEntityManager().clear(); // 一次キャッシュのクリア
					offset += listObjs.size();
					listObjs.forEach(objects -> {
						Map<String, Object> rowCsv = new HashMap<>();
						int i = 0;
						for (String columnName : headerCsv3) {
							if (objects[i] instanceof BigDecimal) {

								BigDecimal value = ((BigDecimal) objects[i]); // the
																				// value
																				// you
																				// get
								rowCsv.put(columnName,
										objects[i] != null ? "\"" + value.toPlainString().replaceAll("\n", "\r\n")
												.replaceAll("\"", "\u00A0").replaceAll("'", "''") + "\"" : "");

							} else {
								rowCsv.put(columnName,
										objects[i] != null ? "\"" + String.valueOf(objects[i]).replaceAll("\n", "\r\n")
												.replaceAll("\"", "\u00A0").replaceAll("'", "''") + "\"" : "");
							}
							i++;
						}
						csv.writeALine(rowCsv);
					});
				}
			}
			csv.destroy();
		} else {
			Query queryString = getEntityManager().createNativeQuery(querySql);
			for (Entry<String, Object> entry : params.entrySet()) {
				queryString.setParameter(entry.getKey(), entry.getValue());
			}

			CsvReportWriter csv = generator.generate(generatorContext, AppContexts.user().companyId()
					+ tableList.getCategoryName() + tableList.getTableJapaneseName() + CSV_EXTENSION, headerCsv3,
					"UTF-8");

			List<Object[]> listObj = queryString.getResultList();
			listObj.forEach(objects -> {
				Map<String, Object> rowCsv = new HashMap<>();
				int i = 0;
				for (String columnName : headerCsv3) {
					if (objects[i] instanceof BigDecimal) {

						BigDecimal value = ((BigDecimal) objects[i]); // the
																		// value
																		// you
																		// get

						rowCsv.put(columnName,
								objects[i] != null
										? "\"" + value.toPlainString().replaceAll("\n", "\r\n")
												.replaceAll("\"", "\u00A0").replaceAll("'", "''") + "\""
										: "");

					} else {

						rowCsv.put(columnName,
								objects[i] != null
										? "\"" + String.valueOf(objects[i]).replaceAll("\n", "\r\n")
												.replaceAll("\"", "\u00A0").replaceAll("'", "''") + "\""
										: "");

					}
					i++;
				}
				csv.writeALine(rowCsv);
			});
			csv.destroy();
			listObj.clear();
		}
	}

	@Override
	public List<String> getAllColumnName(String tableName) {
		Connection connection = this.getEntityManager().unwrap(java.sql.Connection.class);
		try {
			String dbType = connection.getMetaData().getDatabaseProductName();
			if (dbType.equals("PostgreSQL")) {
				tableName = tableName.toLowerCase();
			}
			List<?> columns = this.getEntityManager().createNativeQuery(SELECT_COLUMN_NAME_MSSQL)
					.setParameter("tableName", tableName).getResultList();
			if (columns == null || columns.isEmpty()) {
				return Collections.emptyList();
			}
			return columns.stream().map(item -> {
				return String.valueOf(item).toUpperCase();
			}).collect(Collectors.toList());
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	private void updateEntity(SspdtSaveTableList entity, TableList domain) {
		entity.tableListPk.categoryId = domain.getCategoryId();
		entity.tableListPk.tableNo = domain.getTableNo();
		entity.tableListPk.dataStorageProcessingId = domain.getDataStorageProcessingId();
		entity.categoryName = domain.getCategoryName();
		entity.dataRecoveryProcessId = domain.getDataRecoveryProcessId().orElse(null);
		entity.tableJapaneseName = domain.getTableJapaneseName();
		entity.tableEnglishName = domain.getTableEnglishName();
		entity.fieldAcqCid = domain.getFieldAcqCid().orElse(null);
		entity.fieldAcqDateTime = domain.getFieldAcqDateTime().orElse(null);
		entity.fieldAcqEmployeeId = domain.getFieldAcqEmployeeId().orElse(null);
		entity.fieldAcqEndDate = domain.getFieldAcqEndDate().orElse(null);
		entity.fieldAcqStartDate = domain.getFieldAcqStartDate().orElse(null);
		entity.patternCode = domain.getPatternCode();
		entity.saveSetName = domain.getSaveSetName();
		entity.saveForm = domain.getSaveForm();
		entity.saveDateFrom = domain.getSaveDateFrom().orElse(null);
		entity.saveDateTo = domain.getSaveDateTo().orElse(null);
		entity.storageRangeSaved = domain.getStorageRangeSaved().value;
		entity.retentionPeriodCls = domain.getRetentionPeriodCls().value;
		entity.internalFileName = domain.getInternalFileName();
		entity.anotherComCls = domain.getAnotherComCls().value;
		entity.referenceYear = domain.getReferenceYear().orElse(null);
		entity.referenceMonth = domain.getReferenceMonth().orElse(null);
		entity.compressedFileName = domain.getCompressedFileName();
		entity.fieldChild1 = domain.getFieldChild1().orElse(null);
		entity.fieldChild2 = domain.getFieldChild2().orElse(null);
		entity.fieldChild3 = domain.getFieldChild3().orElse(null);
		entity.fieldChild4 = domain.getFieldChild4().orElse(null);
		entity.fieldChild5 = domain.getFieldChild5().orElse(null);
		entity.fieldChild6 = domain.getFieldChild6().orElse(null);
		entity.fieldChild7 = domain.getFieldChild7().orElse(null);
		entity.fieldChild8 = domain.getFieldChild8().orElse(null);
		entity.fieldChild9 = domain.getFieldChild9().orElse(null);
		entity.fieldChild10 = domain.getFieldChild10().orElse(null);
		entity.historyCls = domain.getHistoryCls().value;
		entity.canNotBeOld = domain.getCanNotBeOld().orElse(null);
		entity.selectionTargetForRes = domain.getSelectionTargetForRes().orElse(null);
		entity.clsKeyQuery1 = domain.getClsKeyQuery1().orElse(null);
		entity.clsKeyQuery2 = domain.getClsKeyQuery2().orElse(null);
		entity.clsKeyQuery3 = domain.getClsKeyQuery3().orElse(null);
		entity.clsKeyQuery4 = domain.getClsKeyQuery4().orElse(null);
		entity.clsKeyQuery5 = domain.getClsKeyQuery5().orElse(null);
		entity.clsKeyQuery6 = domain.getClsKeyQuery6().orElse(null);
		entity.clsKeyQuery7 = domain.getClsKeyQuery7().orElse(null);
		entity.clsKeyQuery8 = domain.getClsKeyQuery8().orElse(null);
		entity.clsKeyQuery9 = domain.getClsKeyQuery9().orElse(null);
		entity.clsKeyQuery10 = domain.getClsKeyQuery10().orElse(null);
		entity.fieldKeyQuery1 = domain.getFieldKeyQuery1().orElse(null);
		entity.fieldKeyQuery2 = domain.getFieldKeyQuery2().orElse(null);
		entity.fieldKeyQuery3 = domain.getFieldKeyQuery3().orElse(null);
		entity.fieldKeyQuery4 = domain.getFieldKeyQuery4().orElse(null);
		entity.fieldKeyQuery5 = domain.getFieldKeyQuery5().orElse(null);
		entity.fieldKeyQuery6 = domain.getFieldKeyQuery6().orElse(null);
		entity.fieldKeyQuery7 = domain.getFieldKeyQuery7().orElse(null);
		entity.fieldKeyQuery8 = domain.getFieldKeyQuery8().orElse(null);
		entity.fieldKeyQuery9 = domain.getFieldKeyQuery9().orElse(null);
		entity.fieldKeyQuery10 = domain.getFieldKeyQuery10().orElse(null);
		entity.defaultCondKeyQuery = domain.getDefaultCondKeyQuery().orElse(null);
		entity.fieldDate1 = domain.getFieldDate1().orElse(null);
		entity.fieldDate2 = domain.getFieldDate2().orElse(null);
		entity.fieldDate3 = domain.getFieldDate3().orElse(null);
		entity.fieldDate4 = domain.getFieldDate4().orElse(null);
		entity.fieldDate5 = domain.getFieldDate5().orElse(null);
		entity.fieldDate6 = domain.getFieldDate6().orElse(null);
		entity.fieldDate7 = domain.getFieldDate7().orElse(null);
		entity.fieldDate8 = domain.getFieldDate8().orElse(null);
		entity.fieldDate9 = domain.getFieldDate9().orElse(null);
		entity.fieldDate10 = domain.getFieldDate10().orElse(null);
		entity.fieldDate11 = domain.getFieldDate11().orElse(null);
		entity.fieldDate12 = domain.getFieldDate12().orElse(null);
		entity.fieldDate13 = domain.getFieldDate13().orElse(null);
		entity.fieldDate14 = domain.getFieldDate14().orElse(null);
		entity.fieldDate15 = domain.getFieldDate15().orElse(null);
		entity.fieldDate16 = domain.getFieldDate16().orElse(null);
		entity.fieldDate17 = domain.getFieldDate17().orElse(null);
		entity.fieldDate18 = domain.getFieldDate18().orElse(null);
		entity.fieldDate19 = domain.getFieldDate19().orElse(null);
		entity.fieldDate20 = domain.getFieldDate20().orElse(null);
		entity.filedKeyUpdate1 = domain.getFiledKeyUpdate1().orElse(null);
		entity.filedKeyUpdate2 = domain.getFiledKeyUpdate2().orElse(null);
		entity.filedKeyUpdate3 = domain.getFiledKeyUpdate3().orElse(null);
		entity.filedKeyUpdate4 = domain.getFiledKeyUpdate4().orElse(null);
		entity.filedKeyUpdate5 = domain.getFiledKeyUpdate5().orElse(null);
		entity.filedKeyUpdate6 = domain.getFiledKeyUpdate6().orElse(null);
		entity.filedKeyUpdate7 = domain.getFiledKeyUpdate7().orElse(null);
		entity.filedKeyUpdate8 = domain.getFiledKeyUpdate8().orElse(null);
		entity.filedKeyUpdate9 = domain.getFiledKeyUpdate9().orElse(null);
		entity.filedKeyUpdate10 = domain.getFiledKeyUpdate10().orElse(null);
		entity.filedKeyUpdate11 = domain.getFiledKeyUpdate11().orElse(null);
		entity.filedKeyUpdate12 = domain.getFiledKeyUpdate12().orElse(null);
		entity.filedKeyUpdate13 = domain.getFiledKeyUpdate13().orElse(null);
		entity.filedKeyUpdate14 = domain.getFiledKeyUpdate14().orElse(null);
		entity.filedKeyUpdate15 = domain.getFiledKeyUpdate15().orElse(null);
		entity.filedKeyUpdate16 = domain.getFiledKeyUpdate16().orElse(null);
		entity.filedKeyUpdate17 = domain.getFiledKeyUpdate17().orElse(null);
		entity.filedKeyUpdate18 = domain.getFiledKeyUpdate18().orElse(null);
		entity.filedKeyUpdate19 = domain.getFiledKeyUpdate19().orElse(null);
		entity.filedKeyUpdate20 = domain.getFiledKeyUpdate20().orElse(null);
		entity.screenRetentionPeriod = domain.getScreenRetentionPeriod().orElse(null);
		entity.supplementaryExplanation = domain.getSupplementaryExplanation().orElse(null);
		entity.parentTblJpName = domain.getParentTblJpName().orElse(null);
		entity.hasParentTblFlg = domain.getHasParentTblFlg().value;
		entity.parentTblName = domain.getParentTblName().orElse(null);
		entity.fieldParent1 = domain.getFieldParent1().orElse(null);
		entity.fieldParent2 = domain.getFieldParent2().orElse(null);
		entity.fieldParent3 = domain.getFieldParent3().orElse(null);
		entity.fieldParent4 = domain.getFieldParent4().orElse(null);
		entity.fieldParent5 = domain.getFieldParent5().orElse(null);
		entity.fieldParent6 = domain.getFieldParent6().orElse(null);
		entity.fieldParent7 = domain.getFieldParent7().orElse(null);
		entity.fieldParent8 = domain.getFieldParent8().orElse(null);
		entity.fieldParent9 = domain.getFieldParent9().orElse(null);
		entity.fieldParent10 = domain.getFieldParent10().orElse(null);
		entity.surveyPreservation = domain.getSurveyPreservation().value;
	}

	@Override
	public boolean isPresent(TableList domain) {
		// Get entity by key
		Optional<SspdtSaveTableList> entityOpt = this
				.queryProxy().find(
						new SspmtTableListPk(domain.getCategoryId(), domain.getTableNo(),
								domain.getDataStorageProcessingId(), domain.getSystemType().value),
						SspdtSaveTableList.class);
		return entityOpt.isPresent() ? true : false;
	}

	@Override
	public List<TableList> getBySystemTypeAndStorageId(int systemType, String StorageId) {
		return this.queryProxy().query(SELECT_BY_SYSTEM_TYPE_AND_STORAGE_ID, SspdtSaveTableList.class)
				.setParameter("systemType", systemType).setParameter("dataStorageProcessingId", StorageId)
				.getList(c -> c.toDomain());
	}

	@Override
	public List<TableList> getBySystemTypeAndRecoverId(int systemType, String id) {
		return this.queryProxy().query(SELECT_BY_SYSTEM_TYPE_AND_RECOVER_ID, SspdtSaveTableList.class)
				.setParameter("systemType", systemType).setParameter("dataStorageProcessingId", id)
				.getList(c -> c.toDomain());
	}

}
