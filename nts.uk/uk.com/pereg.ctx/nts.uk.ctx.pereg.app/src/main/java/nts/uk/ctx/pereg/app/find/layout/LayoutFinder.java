/**
 * 
 */
package nts.uk.ctx.pereg.app.find.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.employeeinfo.Employee;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeRepository;
import nts.uk.ctx.bs.employee.dom.employeeinfo.JobEntryHistory;
import nts.uk.ctx.pereg.app.find.common.ComboBoxRetrieveFactory;
import nts.uk.ctx.pereg.app.find.common.MappingFactory;
import nts.uk.ctx.pereg.app.find.layout.dto.EmpMaintLayoutDto;
import nts.uk.ctx.pereg.app.find.layout.dto.SimpleEmpMainLayoutDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.ActionRole;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoClsDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoClsFinder;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoValueDto;
import nts.uk.ctx.pereg.app.find.person.info.item.DataTypeStateDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefDto;
import nts.uk.ctx.pereg.app.find.person.info.item.SelectionItemDto;
import nts.uk.ctx.pereg.app.find.processor.LayoutingProcessor;
import nts.uk.ctx.pereg.dom.person.additemdata.category.EmInfoCtgDataRepository;
import nts.uk.ctx.pereg.dom.person.additemdata.category.EmpInfoCtgData;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemData;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemDataRepository;
import nts.uk.ctx.pereg.dom.person.info.category.IsFixed;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCategoryRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonEmployeeType;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.daterangeitem.DateRangeItem;
import nts.uk.ctx.pereg.dom.person.info.singleitem.DataTypeValue;
import nts.uk.ctx.pereg.dom.person.layout.IMaintenanceLayoutRepository;
import nts.uk.ctx.pereg.dom.person.layout.MaintenanceLayout;
import nts.uk.ctx.pereg.dom.person.layout.classification.ILayoutPersonInfoClsRepository;
import nts.uk.ctx.pereg.dom.person.layout.classification.LayoutItemType;
import nts.uk.ctx.pereg.dom.person.layout.classification.LayoutPersonInfoClassification;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.categor.PerInfoCtgData;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.categor.PerInfoCtgDataRepository;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.item.PerInfoItemDataRepository;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.item.PersonInfoItemData;
import nts.uk.ctx.pereg.dom.roles.auth.PersonInfoPermissionType;
import nts.uk.ctx.pereg.dom.roles.auth.category.PersonInfoAuthType;
import nts.uk.ctx.pereg.dom.roles.auth.category.PersonInfoCategoryAuth;
import nts.uk.ctx.pereg.dom.roles.auth.category.PersonInfoCategoryAuthRepository;
import nts.uk.ctx.pereg.dom.roles.auth.item.PersonInfoItemAuth;
import nts.uk.ctx.pereg.dom.roles.auth.item.PersonInfoItemAuthRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.dto.PeregDto;

@Stateless
public class LayoutFinder {

	@Inject
	private LayoutPersonInfoClsFinder clsFinder;

	@Inject
	private ILayoutPersonInfoClsRepository itemClsRepo;

	@Inject
	private EmployeeRepository employeeRepository;

	@Inject
	private PersonInfoItemAuthRepository perInfoItemAuthRepo;

	@Inject
	private PersonInfoCategoryAuthRepository perInfoCtgAuthRepo;

	@Inject
	private PerInfoCategoryRepositoty perInfoCateRepo;

	@Inject
	private LayoutingProcessor layoutingProcessor;

	@Inject
	private PerInfoCtgDataRepository perInCtgDataRepo;

	@Inject
	private PerInfoItemDataRepository perInItemDataRepo;

	@Inject
	private EmInfoCtgDataRepository empInCtgDataRepo;

	@Inject
	private EmpInfoItemDataRepository empInItemDataRepo;

	@Inject
	private IMaintenanceLayoutRepository layoutRepo;

	public List<SimpleEmpMainLayoutDto> getSimpleLayoutList(String browsingEmpId) {

		String loginEmpId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		String roleId = AppContexts.user().roles().forCompanyAdmin();
		// String roleId = "99900000-0000-0000-0000-000000000001";
		boolean selfBrowsing = loginEmpId.equals(browsingEmpId);

		List<MaintenanceLayout> simpleLayouts = layoutRepo.getAllMaintenanceLayout(companyId);

		Map<String, PersonInfoCategoryAuth> mapCategoryAuth = perInfoCtgAuthRepo.getAllCategoryAuthByRoleId(roleId)
				.stream().collect(Collectors.toMap(e -> e.getPersonInfoCategoryAuthId(), e -> e));

		List<SimpleEmpMainLayoutDto> acceptSplLayouts = new ArrayList<>();
		for (MaintenanceLayout simpleLayout : simpleLayouts) {

			if (haveAnItemAuth(simpleLayout.getMaintenanceLayoutID(), mapCategoryAuth, selfBrowsing)) {
				acceptSplLayouts.add(SimpleEmpMainLayoutDto.fromDomain(simpleLayout));
			}

		}
		return acceptSplLayouts;
	}

	/**
	 * @param layoutQuery
	 * @return
	 */
	public EmpMaintLayoutDto getLayout(LayoutQuery layoutQuery) {
		EmpMaintLayoutDto result = new EmpMaintLayoutDto();
		// query properties
		GeneralDate stardardDate = GeneralDate.legacyDate(layoutQuery.getStandardDate());
		String browsingEmpId = layoutQuery.getBrowsingEmpId();

		Employee employee = employeeRepository.findBySid(AppContexts.user().companyId(), browsingEmpId).get();
		String browsingPeronId = employee.getPId();
		// validate standard date
		validateStandardDate(stardardDate, employee, result);

		// check authority & get data
		boolean selfBrowsing = browsingEmpId.equals(AppContexts.user().employeeId());
		List<LayoutPersonInfoClsDto> itemClassList = this.clsFinder.getListClsDto(layoutQuery.getLayoutId());
		List<LayoutPersonInfoClsDto> authItemClasList = new ArrayList<>();
		String roleId = AppContexts.user().roles().forCompanyAdmin();

		Set<String> setCategories = itemClassList.stream().map(classItem -> classItem.getPersonInfoCategoryID())
				.collect(Collectors.toSet());
		List<String> categoryIdList = new ArrayList<>(setCategories);

		Map<String, PersonInfoCategoryAuth> categoryAuthMap = perInfoCtgAuthRepo.getByRoleIdAndCategories(roleId,
				categoryIdList);
		Map<String, List<PersonInfoItemAuth>> itemAuthMap = perInfoItemAuthRepo.getByRoleIdAndCategories(roleId,
				categoryIdList);

		// FILTER CLASS ITEMS WITH AUTHORITY
		for (LayoutPersonInfoClsDto classItem : itemClassList) {

			// if item is separator line, do not check
			if (classItem.getLayoutItemType() == LayoutItemType.SeparatorLine) {
				authItemClasList.add(classItem);
			} else if (isClassItemIsAccepted(categoryAuthMap.get(classItem.getPersonInfoCategoryID()), selfBrowsing)) {
				// check author of each definition in class-item
				List<PersonInfoItemAuth> inforAuthItems = itemAuthMap.get(classItem.getPersonInfoCategoryID());
				List<PerInfoItemDefDto> dataInfoItems = validateAuthItem(inforAuthItems, classItem.getListItemDf(),
						selfBrowsing);
				// if definition-items is empty, will NOT show this class-item
				if (dataInfoItems.isEmpty()) {
					continue;
				}
				classItem.setListItemDf(dataInfoItems);
				authItemClasList.add(classItem);
			}
		}

		// GET DATA WITH EACH CATEGORY
		Map<String, List<LayoutPersonInfoClsDto>> classItemInCategoryMap = new HashMap<>();
		for (LayoutPersonInfoClsDto classItem : authItemClasList) {
			if (classItem.getLayoutItemType() != LayoutItemType.SeparatorLine) {
				if (classItem.getLayoutItemType() == LayoutItemType.ITEM) {
					List<LayoutPersonInfoClsDto> classItemList = classItemInCategoryMap
							.get(classItem.getPersonInfoCategoryID());
					if (classItemList == null) {
						classItemList = new ArrayList<>();
						classItemInCategoryMap.put(classItem.getPersonInfoCategoryID(), classItemList);
					}
					classItemList.add(classItem);
				} else {
					
				}
			}

		}

		classItemInCategoryMap.forEach((categoryId, classItemList) -> {
			PersonInfoCategory perInfoCategory = perInfoCateRepo
					.getPerInfoCategory(categoryId, AppContexts.user().contractCode()).get();
			PeregQuery query = new PeregQuery(perInfoCategory.getCategoryCode().v(), layoutQuery.getBrowsingEmpId(),
					browsingPeronId, stardardDate);
			// get data
			getDataforSingleItem(perInfoCategory, classItemList, stardardDate, browsingPeronId, browsingEmpId, query);

			classItemList.forEach(classItem -> {
				checkActionRoleItemData(itemAuthMap.get(classItem.getPersonInfoCategoryID()), classItem, selfBrowsing);
			});

		});

		result.setClassificationItems(removeDuplicateSeparator(authItemClasList));
		return result;

	}

	private boolean haveAnItemAuth(String layoutId, Map<String, PersonInfoCategoryAuth> mapCategoryAuth,
			boolean selfBrowsing) {
		List<LayoutPersonInfoClassification> itemClassList = this.itemClsRepo.getAllByLayoutId(layoutId);
		for (LayoutPersonInfoClassification itemClass : itemClassList) {
			if (itemClass.getLayoutItemType() == LayoutItemType.SeparatorLine) {
				continue;
			}
			PersonInfoCategoryAuth categoryAuth = mapCategoryAuth.get(itemClass.getPersonInfoCategoryID());
			if (categoryAuth == null) {
				continue;
			}
			if (selfBrowsing && categoryAuth.getAllowPersonRef() == PersonInfoPermissionType.YES) {
				return true;
			}
			if (!selfBrowsing && categoryAuth.getAllowOtherRef() == PersonInfoPermissionType.YES) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param stardardDate
	 * @param employee
	 * @param result
	 */
	private void validateStandardDate(GeneralDate stardardDate, Employee employee, EmpMaintLayoutDto result) {
		if (employee.getHistoryWithReferDate(stardardDate).isPresent()) {
			result.setStandardDate(stardardDate);
		} else {
			Optional<JobEntryHistory> hitoryOption = employee.getHistoryBeforeReferDate(stardardDate);
			if (hitoryOption.isPresent()) {
				stardardDate = hitoryOption.get().getRetirementDate();
			} else {
				hitoryOption = employee.getHistoryAfterReferDate(stardardDate);
				if (hitoryOption.isPresent()) {
					stardardDate = hitoryOption.get().getJoinDate();
				}
			}
		}
	}

	/**
	 * @param roleId
	 * @param item
	 * @param selfBrowsing
	 *            Target: check author of person who login with class-item
	 * @return
	 */
	private boolean isClassItemIsAccepted(PersonInfoCategoryAuth personCategoryAuth, boolean selfBrowsing) {
		if (personCategoryAuth == null) {
			return false;
		}
		if (selfBrowsing && personCategoryAuth.getAllowPersonRef() == PersonInfoPermissionType.YES) {
			return true;
		}
		if (!selfBrowsing && personCategoryAuth.getAllowOtherRef() == PersonInfoPermissionType.YES) {
			return true;
		}
		return false;
	}

	/**
	 * @param authItems
	 * @param listItemDef
	 * @param selfBrowsing
	 * @return Target: check author of person who login with each
	 *         definition-items in class-item
	 */
	private List<PerInfoItemDefDto> validateAuthItem(List<PersonInfoItemAuth> authItems,
			List<PerInfoItemDefDto> listItemDef, boolean selfBrowsing) {
		List<PerInfoItemDefDto> dataInfoItems = new ArrayList<>();
		for (PerInfoItemDefDto itemDef : listItemDef) {
			Optional<PersonInfoItemAuth> authItemOpt = authItems.stream()
					.filter(p -> p.getPersonItemDefId().equals(itemDef.getId())).findFirst();
			if (authItemOpt.isPresent()) {
				if (selfBrowsing && authItemOpt.get().getSelfAuth() != PersonInfoAuthType.HIDE) {
					dataInfoItems.add(itemDef);
				} else if (!selfBrowsing && authItemOpt.get().getOtherAuth() != PersonInfoAuthType.HIDE) {
					dataInfoItems.add(itemDef);
				}
			}
		}
		return dataInfoItems;

	}

	/**
	 * @param perInfoCategory
	 * @param authClassItem
	 * @param standardDate
	 * @param personId
	 * @param employeeId
	 * @param query
	 */
	private void getDataforSingleItem(PersonInfoCategory perInfoCategory, List<LayoutPersonInfoClsDto> classItemList,
			GeneralDate standardDate, String personId, String employeeId, PeregQuery query) {

		cloneDefItemToValueItem(perInfoCategory.getCategoryCode().v(), classItemList);

		if (perInfoCategory.getIsFixed() == IsFixed.FIXED) {
			// get domain data
			PeregDto peregDto = layoutingProcessor.findSingle(query);

			if (peregDto != null) {
				MappingFactory.mapListItemClass(peregDto, classItemList);
			}
		} else {
			switch (perInfoCategory.getCategoryType()) {
			case SINGLEINFO:
				// authClassItem);
				if (perInfoCategory.getPersonEmployeeType() == PersonEmployeeType.PERSON) {
					List<PerInfoCtgData> perInfoCtgDatas = perInCtgDataRepo.getByPerIdAndCtgId(personId,
							perInfoCategory.getPersonInfoCategoryId());
					if (!perInfoCtgDatas.isEmpty()) {
						PerInfoCtgData perInfoCtgData = perInfoCtgDatas.get(0);
						List<PersonInfoItemData> dataItems = perInItemDataRepo
								.getAllInfoItemByRecordId(perInfoCtgData.getRecordId());
						matchPersDataForSingleClsItem(classItemList, dataItems);
					}
				} else {
					EmpInfoCtgData perInfoCtgData = empInCtgDataRepo
							.getEmpInfoCtgDataBySIdAndCtgId(employeeId, perInfoCategory.getPersonInfoCategoryId())
							.get();
					List<EmpInfoItemData> dataItems = empInItemDataRepo
							.getAllInfoItemByRecordId(perInfoCtgData.getRecordId());
					matchEmpDataForDefItems(classItemList, dataItems);
				}
				break;
			case CONTINUOUSHISTORY:
			case NODUPLICATEHISTORY:
				if (perInfoCategory.getPersonEmployeeType() == PersonEmployeeType.PERSON) {
					// person history
					getPersDataHistoryType(perInfoCategory.getPersonInfoCategoryId(), classItemList, personId,
							standardDate);
				} else {
					// employee history
					getEmpDataHistoryType(perInfoCategory.getPersonInfoCategoryId(), classItemList, employeeId,
							standardDate);
				}
				break;
			default:
				break;
			}

		}

		// getComboBox
		classItemList.forEach(classItem -> {
			for (Object item : classItem.getItems()) {
				LayoutPersonInfoValueDto valueItem = (LayoutPersonInfoValueDto) item;
				DataTypeStateDto itemDataTypeSate = valueItem.getItem();
				if ( itemDataTypeSate != null && itemDataTypeSate.getDataTypeValue() == DataTypeValue.SELECTION.value) {
					SelectionItemDto selectionItemDto = (SelectionItemDto) valueItem.getItem();
					valueItem.setLstComboBoxValue(ComboBoxRetrieveFactory.getComboBox(selectionItemDto, standardDate));
				}
			}
		});

	}

	/**
	 * @param classItemList
	 * @param dataItems
	 *            Target: map optional data with definition item. Person case
	 */
	private void matchPersDataForSingleClsItem(List<LayoutPersonInfoClsDto> classItemList,
			List<PersonInfoItemData> dataItems) {
		for (LayoutPersonInfoClsDto classItem : classItemList) {
			for (Object item : classItem.getItems()) {
				LayoutPersonInfoValueDto valueItem = (LayoutPersonInfoValueDto) item;
				for (PersonInfoItemData dataItem : dataItems) {
					if (valueItem.getItemCode().equals(dataItem.getItemCode().v())) {
						valueItem.setValue(dataItem.getDataState().getValue());
					}
				}
			}
		}

	}

	/**
	 * @param authClassItem
	 * @param dataItems
	 *            Target: map optional data with definition item. employee case
	 */
	private void matchEmpDataForDefItems(List<LayoutPersonInfoClsDto> classItemList, List<EmpInfoItemData> dataItems) {
		for (LayoutPersonInfoClsDto classItem : classItemList) {
			for (Object item : classItem.getItems()) {
				LayoutPersonInfoValueDto valueItem = (LayoutPersonInfoValueDto) item;
				for (EmpInfoItemData dataItem : dataItems) {
					if (valueItem.getItemCode().equals(dataItem.getItemCode().v())) {
						valueItem.setValue(dataItem.getDataState().getValue());
					}
				}
			}
		}
	}

	/**
	 * @param perInfoCategoryId
	 * @param authClassItem
	 * @param personId
	 * @param stardardDate
	 *            Target: get data with history case. Person case
	 */
	private void getPersDataHistoryType(String perInfoCategoryId, List<LayoutPersonInfoClsDto> classItemList,
			String personId, GeneralDate stardardDate) {
		DateRangeItem dateRangeItem = perInfoCateRepo.getDateRangeItemByCtgId(perInfoCategoryId);
		List<PerInfoCtgData> perInfoCtgDatas = perInCtgDataRepo.getByPerIdAndCtgId(personId, perInfoCategoryId);
		String startDateId = dateRangeItem.getStartDateItemId();
		String endDateId = dateRangeItem.getEndDateItemId();
		for (PerInfoCtgData perInfoCtgData : perInfoCtgDatas) {
			List<PersonInfoItemData> dataItems = perInItemDataRepo
					.getAllInfoItemByRecordId(perInfoCtgData.getRecordId());

			Optional<PersonInfoItemData> startDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoItemDefId().equals(startDateId)).findFirst();

			Optional<PersonInfoItemData> endDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoItemDefId().equals(endDateId)).findFirst();

			if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
				if (stardardDate.after(startDateOpt.get().getDataState().getDateValue())
						&& stardardDate.before(endDateOpt.get().getDataState().getDateValue())) {
					matchPersDataForSingleClsItem(classItemList, dataItems);
					break;
				}
			}

		}
	}

	/**
	 * @param perInfoCategoryId
	 * @param classItemList
	 * @param employeeId
	 * @param stardardDate
	 *            Target: get data with history case. Employee case
	 */
	private void getEmpDataHistoryType(String perInfoCategoryId, List<LayoutPersonInfoClsDto> classItemList,
			String employeeId, GeneralDate stardardDate) {
		DateRangeItem dateRangeItem = perInfoCateRepo.getDateRangeItemByCtgId(perInfoCategoryId);
		List<EmpInfoCtgData> empInfoCtgDatas = empInCtgDataRepo.getByEmpIdAndCtgId(employeeId, perInfoCategoryId);
		String startDateId = dateRangeItem.getStartDateItemId();
		String endDateId = dateRangeItem.getEndDateItemId();

		for (EmpInfoCtgData empInfoCtgData : empInfoCtgDatas) {
			List<EmpInfoItemData> dataItems = empInItemDataRepo.getAllInfoItemByRecordId(empInfoCtgData.getRecordId());

			Optional<EmpInfoItemData> startDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoDefId().equals(startDateId)).findFirst();
			Optional<EmpInfoItemData> endDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoDefId().equals(endDateId)).findFirst();

			if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
				if (stardardDate.after(startDateOpt.get().getDataState().getDateValue())
						&& stardardDate.before(endDateOpt.get().getDataState().getDateValue())) {
					matchEmpDataForDefItems(classItemList, dataItems);
					break;
				}
			}

		}
	}

	private void cloneDefItemToValueItem(String categoryCode, List<LayoutPersonInfoClsDto> classItemList) {
		for (LayoutPersonInfoClsDto classItem : classItemList) {
			List<Object> items = new ArrayList<>();
			for (PerInfoItemDefDto itemDef : classItem.getListItemDf()) {
				items.add(LayoutPersonInfoValueDto.cloneFromItemDef(categoryCode, itemDef));
			}
			classItem.setItems(items);
		}
	}

	private void checkActionRoleWithData(PersonInfoCategory perInfoCategory, PersonInfoCategoryAuth personCategoryAuth,
			List<PersonInfoItemAuth> inforAuthItems, LayoutPersonInfoClsDto authClassItem, GeneralDate standardDate,
			boolean selfBrowsing) {
		switch (perInfoCategory.getCategoryType()) {
		case MULTIINFO:
			List<Object> mulSeigoItemsData = new ArrayList<>();
			for (Object mulItem : authClassItem.getItems()) {
				@SuppressWarnings("unchecked")
				List<LayoutPersonInfoValueDto> mulRowData = (List<LayoutPersonInfoValueDto>) mulItem;
				List<LayoutPersonInfoValueDto> mulActionRoleRowData = checkAndSetActionRole(mulRowData, inforAuthItems,
						selfBrowsing);
				mulSeigoItemsData.add(mulActionRoleRowData);
			}
			authClassItem.setItems(mulSeigoItemsData);
			break;
		case CONTINUOUSHISTORY:
		case NODUPLICATEHISTORY:
		case DUPLICATEHISTORY:
		case CONTINUOUS_HISTORY_FOR_ENDDATE:
			DateRangeItem dateRangeItem = perInfoCateRepo
					.getDateRangeItemByCtgId(perInfoCategory.getPersonInfoCategoryId());
			String startDateId = dateRangeItem.getStartDateItemId();
			String endDateId = dateRangeItem.getEndDateItemId();
			List<Object> seigoItemsData = new ArrayList<>();
			for (Object item : authClassItem.getItems()) {
				@SuppressWarnings("unchecked")
				List<LayoutPersonInfoValueDto> rowData = (List<LayoutPersonInfoValueDto>) item;
				Optional<LayoutPersonInfoValueDto> startDateOpt = rowData.stream()
						.filter(column -> column.getItemDefId().equals(startDateId)).findFirst();
				Optional<LayoutPersonInfoValueDto> endDateOpt = rowData.stream()
						.filter(column -> column.getItemDefId().equals(endDateId)).findFirst();

				if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
					PersonInfoAuthType auth = PersonInfoAuthType.UPDATE;
					if (standardDate.after((GeneralDate) endDateOpt.get().getValue())) {
						// past
						auth = selfBrowsing ? personCategoryAuth.getSelfPastHisAuth()
								: personCategoryAuth.getOtherPastHisAuth();
					} else if (standardDate.before((GeneralDate) startDateOpt.get().getValue())) {
						// future
						auth = selfBrowsing ? personCategoryAuth.getSelfFutureHisAuth()
								: personCategoryAuth.getOtherFutureHisAuth();
					}
					switch (auth) {
					case REFERENCE:
						rowData.forEach(element -> element.setActionRole(ActionRole.VIEW_ONLY));
						seigoItemsData.add(rowData);
						break;
					case UPDATE:
						List<LayoutPersonInfoValueDto> actionRoleRowData = checkAndSetActionRole(rowData,
								inforAuthItems, selfBrowsing);
						seigoItemsData.add(actionRoleRowData);
						break;
					case HIDE:
						// do NOT add to authItemsData
						break;
					}
				}
			}
			authClassItem.setItems(seigoItemsData);
			break;
		default:
			break;
		}
	}

	private List<LayoutPersonInfoValueDto> checkAndSetActionRole(List<LayoutPersonInfoValueDto> rowData,
			List<PersonInfoItemAuth> inforAuthItems, boolean selfBrowsing) {
		List<LayoutPersonInfoValueDto> actionRoleRowData = new ArrayList<>();
		for (LayoutPersonInfoValueDto element : rowData) {
			Optional<PersonInfoItemAuth> authItemOpt = inforAuthItems.stream()
					.filter(authItem -> authItem.getPersonItemDefId().equals(element.getItemDefId())).findFirst();
			if (authItemOpt.isPresent()) {
				PersonInfoAuthType auth = selfBrowsing ? authItemOpt.get().getSelfAuth()
						: authItemOpt.get().getOtherAuth();
				switch (auth) {
				case REFERENCE:
					element.setActionRole(ActionRole.VIEW_ONLY);
					actionRoleRowData.add(element);
					break;
				case UPDATE:
					element.setActionRole(ActionRole.EDIT);
					actionRoleRowData.add(element);
					break;
				case HIDE:
					// do NOT add to actionRoleRowData
					break;
				}
			}
		}
		return actionRoleRowData;
	}

	private void checkActionRoleItemData(List<PersonInfoItemAuth> inforAuthItems, LayoutPersonInfoClsDto classItem,
			boolean selfBrowsing) {

		for (Object item : classItem.getItems()) {
			LayoutPersonInfoValueDto valueItem = (LayoutPersonInfoValueDto) item;

			Optional<PersonInfoItemAuth> authItemOpt = inforAuthItems.stream()
					.filter(p -> p.getPersonItemDefId().equals(valueItem.getItemDefId())).findFirst();

			if (authItemOpt.isPresent()) {
				ActionRole actionrole;
				if (selfBrowsing) {
					actionrole = EnumAdaptor.valueOf(authItemOpt.get().getSelfAuth().value, ActionRole.class);
				} else {
					actionrole = EnumAdaptor.valueOf(authItemOpt.get().getOtherAuth().value, ActionRole.class);
				}
				valueItem.setActionRole(actionrole);
			}
		}

	}

	private List<LayoutPersonInfoClsDto> removeDuplicateSeparator(List<LayoutPersonInfoClsDto> classItemList) {
		List<LayoutPersonInfoClsDto> authItemClasList1 = new ArrayList<>();
		for (int i = 0; i < classItemList.size(); i++) {
			if (i == 0) {
				authItemClasList1.add(classItemList.get(i));
			} else {
				boolean notAcceptElement = classItemList.get(i).getLayoutItemType() == LayoutItemType.SeparatorLine
						&& classItemList.get(i - 1).getLayoutItemType() == LayoutItemType.SeparatorLine;
				if (!notAcceptElement) {
					authItemClasList1.add(classItemList.get(i));
				}
			}
		}
		return authItemClasList1;
	}

}
