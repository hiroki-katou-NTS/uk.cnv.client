package nts.uk.ctx.at.function.infra.repository.monthlycorrection.fixedformatmonthly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkType;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeRepository;
import nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly.KrcmtDisplayTimeItemRC;
import nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly.KrcmtMonthlyActualResultRC;
import nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly.KrcmtMonthlyActualResultRCPK;
import nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly.KrcmtMonthlyRecordWorkType;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaMonthlyRecordWorkTypeRepo extends JpaRepository implements MonthlyRecordWorkTypeRepository {

	private static final String GET_ALL_MON = "SELECT a FROM KrcmtMonthlyRecordWorkType a " + " WHERE a.krcmtMonthlyRecordWorkTypePK.companyID = :companyID ";

	private static final String GET_MON_BY_CODE = GET_ALL_MON + " AND a.krcmtMonthlyRecordWorkTypePK.businessTypeCode = :businessTypeCode";
	private static final String GET_MON_BY_LIST_CODE = GET_ALL_MON + " AND a.krcmtMonthlyRecordWorkTypePK.businessTypeCode IN :businessTypeCode";
	
	private static final String GET_BY_LISTCODE = "SELECT a FROM KrcmtMonthlyRecordWorkType a WHERE a.krcmtMonthlyRecordWorkTypePK.companyID = :companyId and a.krcmtMonthlyRecordWorkTypePK.businessTypeCode IN :listCode";

	@Override
	public List<MonthlyRecordWorkType> getAllMonthlyRecordWorkType(String companyID) {
		List<MonthlyRecordWorkType> data = this.queryProxy().query(GET_ALL_MON, KrcmtMonthlyRecordWorkType.class).setParameter("companyID", companyID).getList(c -> c.toDomain());
		return data;
	}

	@Override
	public Optional<MonthlyRecordWorkType> getMonthlyRecordWorkTypeByCode(String companyID, String businessTypeCode) {
		Optional<MonthlyRecordWorkType> data = this.queryProxy().query(GET_MON_BY_CODE, KrcmtMonthlyRecordWorkType.class).setParameter("companyID", companyID).setParameter("businessTypeCode", businessTypeCode).getSingle(c -> c.toDomain());
		return data;
	}
	@Override
	public List<MonthlyRecordWorkType> getMonthlyRecordWorkTypeByListCode(String companyID,
			List<String> businessTypeCodes) {
		List<KrcmtMonthlyRecordWorkType> resultList = new ArrayList<>();
		CollectionUtil.split(businessTypeCodes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(GET_MON_BY_LIST_CODE, KrcmtMonthlyRecordWorkType.class)
					.setParameter("companyID", companyID)
					.setParameter("businessTypeCode", subList)
					.getList());
		});
		return resultList.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}

	@Override
	public void addMonthlyRecordWorkType(MonthlyRecordWorkType monthlyRecordWorkType) {
		KrcmtMonthlyRecordWorkType newEntity = KrcmtMonthlyRecordWorkType.toEntity(monthlyRecordWorkType);
		this.commandProxy().insert(newEntity);
	}
		
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void updateMonthlyRecordWorkType(MonthlyRecordWorkType monthlyRecordWorkType) {
		KrcmtMonthlyRecordWorkType newEntity = KrcmtMonthlyRecordWorkType.toEntity(monthlyRecordWorkType);

		KrcmtMonthlyRecordWorkType updateEntity = this.queryProxy().query(GET_MON_BY_CODE, KrcmtMonthlyRecordWorkType.class).setParameter("companyID", monthlyRecordWorkType.getCompanyID())
				.setParameter("businessTypeCode", monthlyRecordWorkType.getBusinessTypeCode().v()).getSingle().get();

		List<KrcmtMonthlyActualResultRC> toInsertM = new ArrayList<>();
		List<KrcmtMonthlyActualResultRC> toUpdateC = new ArrayList<>();
		newEntity.listKrcmtMonthlyActualResultRC.stream().forEach(nE -> {
			boolean isExist = updateEntity.listKrcmtMonthlyActualResultRC.stream().filter(oE -> {
				return oE.krcmtMonthlyActualResultRCPK.businessTypeCode.equals(nE.krcmtMonthlyActualResultRCPK.businessTypeCode) && oE.krcmtMonthlyActualResultRCPK.companyID.equals(nE.krcmtMonthlyActualResultRCPK.companyID)
						&& oE.krcmtMonthlyActualResultRCPK.sheetNo == nE.krcmtMonthlyActualResultRCPK.sheetNo;
			}).findFirst().isPresent();
			if (isExist) {
				toUpdateC.add(nE);
			} else {
				toInsertM.add(nE);
			}
		});

		if (!toInsertM.isEmpty()) {
			commandProxy().insertAll(toInsertM);
			this.commandProxy().update(updateEntity);
		}

		if (!toUpdateC.isEmpty()) {
			if (toUpdateC.get(0).listKrcmtDisplayTimeItemRC.size() == 0) {
				this.commandProxy().remove(KrcmtMonthlyActualResultRC.class,new KrcmtMonthlyActualResultRCPK(
						toUpdateC.get(0).krcmtMonthlyActualResultRCPK.companyID,
						toUpdateC.get(0).krcmtMonthlyActualResultRCPK.businessTypeCode,
						toUpdateC.get(0).krcmtMonthlyActualResultRCPK.sheetNo
						));
			} else {
				toUpdateC.stream().forEach(nE -> {
					nE.sheetName = newEntity.listKrcmtMonthlyActualResultRC.stream().filter(oE -> {
						return oE.krcmtMonthlyActualResultRCPK.businessTypeCode.equals(nE.krcmtMonthlyActualResultRCPK.businessTypeCode) && oE.krcmtMonthlyActualResultRCPK.companyID.equals(nE.krcmtMonthlyActualResultRCPK.companyID)
								&& oE.krcmtMonthlyActualResultRCPK.sheetNo == nE.krcmtMonthlyActualResultRCPK.sheetNo;
					}).findFirst().get().sheetName;
				});

				// update list Item
				List<KrcmtDisplayTimeItemRC> newItem = toUpdateC.stream().map(c -> c.listKrcmtDisplayTimeItemRC).flatMap(List::stream).collect(Collectors.toList());
				List<KrcmtDisplayTimeItemRC> updateItem = updateEntity.listKrcmtMonthlyActualResultRC.stream().filter(c -> {
					return toUpdateC.stream().filter(n -> {
						return n.krcmtMonthlyActualResultRCPK.businessTypeCode.equals(c.krcmtMonthlyActualResultRCPK.businessTypeCode) && n.krcmtMonthlyActualResultRCPK.companyID.equals(c.krcmtMonthlyActualResultRCPK.companyID)
								&& n.krcmtMonthlyActualResultRCPK.sheetNo == c.krcmtMonthlyActualResultRCPK.sheetNo;
					}).findFirst().isPresent();
				}).map(c -> c.listKrcmtDisplayTimeItemRC).flatMap(List::stream).collect(Collectors.toList());

				List<KrcmtDisplayTimeItemRC> toRemove = new ArrayList<>();
				List<KrcmtDisplayTimeItemRC> toUpdate = new ArrayList<>();
				List<KrcmtDisplayTimeItemRC> toAdd = new ArrayList<>();

				// check add and update
				for (KrcmtDisplayTimeItemRC nItem : newItem) {
					boolean checkItem = false;
					for (KrcmtDisplayTimeItemRC uItem : updateItem) {
						if (nItem.krcmtDisplayTimeItemRCPK.businessTypeCode.equals(uItem.krcmtDisplayTimeItemRCPK.businessTypeCode) && nItem.krcmtDisplayTimeItemRCPK.companyID.equals(uItem.krcmtDisplayTimeItemRCPK.companyID)
								&& nItem.krcmtDisplayTimeItemRCPK.sheetNo == uItem.krcmtDisplayTimeItemRCPK.sheetNo && nItem.krcmtDisplayTimeItemRCPK.itemDisplay == uItem.krcmtDisplayTimeItemRCPK.itemDisplay) {
							checkItem = true;
							toUpdate.add(nItem);
						}
					}
					if (!checkItem) {
						toAdd.add(nItem);
					}

				}
				// check remove
				for (KrcmtDisplayTimeItemRC uItem : updateItem) {
					boolean checkItem = false;
					for (KrcmtDisplayTimeItemRC nItem : newItem) {
						if (nItem.krcmtDisplayTimeItemRCPK.businessTypeCode.equals(uItem.krcmtDisplayTimeItemRCPK.businessTypeCode) && nItem.krcmtDisplayTimeItemRCPK.companyID.equals(uItem.krcmtDisplayTimeItemRCPK.companyID)
								&& nItem.krcmtDisplayTimeItemRCPK.sheetNo == uItem.krcmtDisplayTimeItemRCPK.sheetNo && nItem.krcmtDisplayTimeItemRCPK.itemDisplay == uItem.krcmtDisplayTimeItemRCPK.itemDisplay) {
							checkItem = true;
							break;
						}
					}
					if (!checkItem) {
						toRemove.add(uItem);
					}

				}
				this.commandProxy().updateAll(toUpdateC);
				this.commandProxy().insertAll(toAdd);
				this.commandProxy().updateAll(toUpdate);
				this.commandProxy().removeAll(toRemove);
			}
		}
	}

	@Override
	public void copy(String companyId, String businessTypeCode, List<String> listBusinessTypeCode) {
		
		MonthlyRecordWorkType selectedDomain = this.getMonthlyRecordWorkTypeByCode(companyId, businessTypeCode).get();
		
		List<KrcmtMonthlyRecordWorkType> listEntity = this.queryProxy().query(GET_BY_LISTCODE, KrcmtMonthlyRecordWorkType.class)
				.setParameter("companyId", companyId)
				.setParameter("listCode", listBusinessTypeCode)
				.getList();
		
		if (!listEntity.isEmpty()) {
			this.commandProxy().removeAll(listEntity);
			this.getEntityManager().flush();
		}
		
		
//		List<KrcmtMonthlyRecordWorkType> copyList = listEntity.stream()
//						.map(e -> e.copyDomain(selectedDomain))
//						.map(e -> KrcmtMonthlyRecordWorkType.toEntity(e))
//						.collect(Collectors.toList());
		
		List<KrcmtMonthlyRecordWorkType> copyList = listBusinessTypeCode.stream()
				.map(e -> new MonthlyRecordWorkType(selectedDomain.getCompanyID(), new BusinessTypeCode(e), selectedDomain.getDisplayItem()))
				.map(e -> KrcmtMonthlyRecordWorkType.toEntity(e))
				.collect(Collectors.toList());
		
		this.commandProxy().insertAll(copyList);
		
	}

	

}
