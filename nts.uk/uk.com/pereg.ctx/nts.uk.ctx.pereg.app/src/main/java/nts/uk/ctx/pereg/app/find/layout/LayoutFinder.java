/**
 * 
 */
package nts.uk.ctx.pereg.app.find.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.pereg.app.find.common.InitDefaultValue;
import nts.uk.ctx.pereg.app.find.common.LayoutControlComBoBox;
import nts.uk.ctx.pereg.app.find.common.MappingFactory;
import nts.uk.ctx.pereg.app.find.common.StampCardLength;
import nts.uk.ctx.pereg.app.find.layout.dto.EmpMaintLayoutDto;
import nts.uk.ctx.pereg.app.find.layout.dto.SimpleEmpMainLayoutDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.ActionRole;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoClsDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoClsFinder;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoValueDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.definition.LayoutPersonInfoClsDefFinder;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefDto;
import nts.uk.ctx.pereg.app.find.processor.LayoutingProcessor;
import nts.uk.ctx.pereg.app.find.processor.PeregProcessor;
import nts.uk.ctx.pereg.dom.person.additemdata.category.EmInfoCtgDataRepository;
import nts.uk.ctx.pereg.dom.person.additemdata.category.EmpInfoCtgData;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemDataRepository;
import nts.uk.ctx.pereg.dom.person.info.category.IsFixed;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCategoryRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonEmployeeType;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.daterangeitem.DateRangeItem;
import nts.uk.ctx.pereg.dom.person.layout.IMaintenanceLayoutRepository;
import nts.uk.ctx.pereg.dom.person.layout.MaintenanceLayout;
import nts.uk.ctx.pereg.dom.person.layout.classification.ILayoutPersonInfoClsRepository;
import nts.uk.ctx.pereg.dom.person.layout.classification.LayoutItemType;
import nts.uk.ctx.pereg.dom.person.layout.classification.LayoutPersonInfoClassification;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.categor.PerInfoCtgData;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.categor.PerInfoCtgDataRepository;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.item.PerInfoItemDataRepository;
import nts.uk.ctx.pereg.dom.roles.auth.PersonInfoPermissionType;
import nts.uk.ctx.pereg.dom.roles.auth.category.PersonInfoAuthType;
import nts.uk.ctx.pereg.dom.roles.auth.category.PersonInfoCategoryAuth;
import nts.uk.ctx.pereg.dom.roles.auth.category.PersonInfoCategoryAuthRepository;
import nts.uk.ctx.pereg.dom.roles.auth.item.PersonInfoItemAuth;
import nts.uk.ctx.pereg.dom.roles.auth.item.PersonInfoItemAuthRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.dto.OptionalItemDataDto;
import nts.uk.shr.pereg.app.find.dto.PeregDto;

@Stateless
public class LayoutFinder {

	@Inject
	private EmployeeDataMngInfoRepository employeeDataRepo;

	@Inject
	private LayoutPersonInfoClsFinder clsFinder;

	@Inject
	private ILayoutPersonInfoClsRepository itemClsRepo;

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

	@Inject
	private LayoutPersonInfoClsDefFinder clsItemDefFinder;

	@Inject
	private InitDefaultValue initDefaultValue;
	
	@Inject
	private StampCardLength stampCardLength;
	
	@Inject
	private LayoutControlComBoBox layoutControlComboBox;
	
	public List<SimpleEmpMainLayoutDto> getSimpleLayoutList(String browsingEmpId) {

		String loginEmpId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		String roleId = AppContexts.user().roles().forPersonalInfo();
		// String roleId = "99900000-0000-0000-0000-000000000001";
		boolean selfBrowsing = loginEmpId.equals(browsingEmpId);

		List<MaintenanceLayout> simpleLayouts = layoutRepo.getAllMaintenanceLayout(companyId);
		
		List<String> layoutIdList = simpleLayouts.stream()
				.map(simpleLayout -> simpleLayout.getMaintenanceLayoutID()).collect(Collectors.toList());
		
		if (layoutIdList.isEmpty()) {
			return new ArrayList<>();
		}

		Map<String, List<LayoutPersonInfoClassification>> classItemMap = this.itemClsRepo.getAllByLayoutIdList(layoutIdList);
		
		

		Map<String, PersonInfoCategoryAuth> authCategoryMap = perInfoCtgAuthRepo.getByRoleId(roleId);
		
		Map<String, List<PersonInfoItemAuth>> authItemMap = perInfoItemAuthRepo.getByRoleId(roleId);
				
		List<SimpleEmpMainLayoutDto> acceptSplLayouts = new ArrayList<>();
		
		for (MaintenanceLayout simpleLayout : simpleLayouts) {

			List<LayoutPersonInfoClassification> itemClassList = classItemMap.get(simpleLayout.getMaintenanceLayoutID());
			
			if (itemClassList == null || itemClassList.isEmpty()) {
				continue;
			}

			if (haveAnItemAuth(itemClassList, authCategoryMap, authItemMap , selfBrowsing)) {
				
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
		GeneralDate standardDate = GeneralDate.legacyDate(layoutQuery.getStandardDate());
		String browsingEmpId = layoutQuery.getBrowsingEmpId();
		EmployeeDataMngInfo employee = employeeDataRepo.findByEmpId(browsingEmpId).get();
		String browsingPeronId = employee.getPersonId();

		// validate standard date
		// standardDate = validateStandardDate(cid, browsingEmpId, standardDate,
		// result);
		result.setStandardDate(standardDate);

		// check authority & get data
		boolean selfBrowsing = browsingEmpId.equals(AppContexts.user().employeeId());
		List<LayoutPersonInfoClsDto> itemClassList = this.clsFinder.getListClsDto(layoutQuery.getLayoutId());
		List<LayoutPersonInfoClsDto> authItemClasList = new ArrayList<>();
		String roleId = AppContexts.user().roles().forPersonalInfo();

		LoginUserContext loginUser = AppContexts.user();
		String loginEmpId = loginUser.employeeId();
		String employeeId = layoutQuery.getBrowsingEmpId();
		boolean isSelfAuth = loginEmpId.equals(employeeId);

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
		Map<String, List<LayoutPersonInfoClsDto>> classItemInCategoryMap = authItemClasList.stream()
				.filter(classItem -> classItem.getLayoutItemType() != LayoutItemType.SeparatorLine)
				.collect(Collectors.groupingBy(LayoutPersonInfoClsDto::getPersonInfoCategoryID));
		
		for (Entry<String, List<LayoutPersonInfoClsDto>> classItemsOfCategory : classItemInCategoryMap.entrySet()) {
			String categoryId = classItemsOfCategory.getKey();
			List<LayoutPersonInfoClsDto> classItemList = classItemsOfCategory.getValue();
			PersonInfoCategory perInfoCategory = perInfoCateRepo
					.getPerInfoCategory(categoryId, AppContexts.user().contractCode()).get();
			PeregQuery query = PeregQuery.createQueryLayout(perInfoCategory.getCategoryCode().v(),
					layoutQuery.getBrowsingEmpId(), browsingPeronId, standardDate);

			switch (perInfoCategory.getCategoryType()) {
			case SINGLEINFO:
			case CONTINUOUSHISTORY:
			case NODUPLICATEHISTORY:
			case DUPLICATEHISTORY:
			case CONTINUOUS_HISTORY_FOR_ENDDATE:
				// get data
				getDataforSingleItem(perInfoCategory, classItemList, standardDate, browsingPeronId, browsingEmpId,
						query);
				break;
			case MULTIINFO:
				getDataforListItem(perInfoCategory, classItemList.get(0), standardDate, browsingPeronId, browsingEmpId,
						query);
				break;
			default:
				break;
			}
			classItemList.forEach(classItem -> {
				checkActionRoleItemData(itemAuthMap.get(classItem.getPersonInfoCategoryID()), classItem, selfBrowsing);
			});
			
			PeregProcessor.setItemAuthForCategoryHistory(perInfoCtgAuthRepo, perInfoCategory, classItemList, roleId, isSelfAuth);

		}

		result.setClassificationItems(authItemClasList);
		return result;

	}

	private boolean haveAnItemAuth(List<LayoutPersonInfoClassification> itemClassList,
			Map<String, PersonInfoCategoryAuth> authCategoryMap, Map<String, List<PersonInfoItemAuth>> authItemMap,
			boolean selfBrowsing) {
		for (LayoutPersonInfoClassification itemClass : itemClassList) {

			if (itemClass.getLayoutItemType() == LayoutItemType.SeparatorLine) {
				continue;
			}

			String categoryId = itemClass.getPersonInfoCategoryID();
			
			// check authority with category
			PersonInfoCategoryAuth authCategory = authCategoryMap.get(categoryId);
			
			if (authCategory == null 
					|| (selfBrowsing && !authCategory.allowPersonRef())
					|| (!selfBrowsing && !authCategory.allowOtherRef())) {
				continue;
			}

			// check authority with items
			List<PersonInfoItemAuth> authItemList = authItemMap.get(categoryId);
			
			if (authItemList == null) {
				continue;
			}

			List<String> itemIdList = this.clsItemDefFinder.getItemDefineIds(itemClass.getLayoutID(),
					itemClass.getDispOrder().v());
			
			for (String itemId : itemIdList) {
				Optional<PersonInfoItemAuth> authItemOpt = authItemList.stream()
						.filter(auItem -> auItem.getPersonItemDefId().equals(itemId)).findFirst();
				if (!authItemOpt.isPresent()) {
					continue;
				}
				
				PersonInfoItemAuth authItem = authItemOpt.get();
				
				if ((selfBrowsing && authItem.seflDiplayItem()) || !selfBrowsing && authItem.otherDiplayItem()) {
					return true;
				}
				
			}
			
		}
		return false;
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

		if (authItems == null) {
			return new ArrayList<>();
		}

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

		cloneDefItemToValueItem(perInfoCategory, classItemList);

		GeneralDate comboBoxStandardDate = standardDate;

		if (perInfoCategory.getIsFixed() == IsFixed.FIXED) {
			// get domain data
			PeregDto peregDto = layoutingProcessor.findSingle(query);

			if (peregDto != null) {
				MappingFactory.mapListItemClass(peregDto, classItemList);

				Map<String, Object> itemValueMap = MappingFactory.getFullDtoValue(peregDto);
				List<String> standardDateItemCodes = Arrays.asList("IS00020", "IS00077", "IS00082", "IS00119","IS00781");
				for (String itemCode : standardDateItemCodes) {
					if (itemValueMap.containsKey(itemCode)) {
						comboBoxStandardDate = (GeneralDate) itemValueMap.get(itemCode);
						break;
					}
				}
			}

		} else {
			switch (perInfoCategory.getCategoryType()) {
			case SINGLEINFO:
				// authClassItem);
				getSingleOptionData(classItemList, perInfoCategory, query);
				break;
			case CONTINUOUSHISTORY:
			case NODUPLICATEHISTORY:
			case DUPLICATEHISTORY:
			case CONTINUOUS_HISTORY_FOR_ENDDATE:
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

		// For each classification item to get combo box list
		layoutControlComboBox.getComboBoxListForSelectionItems(employeeId, perInfoCategory, classItemList, comboBoxStandardDate);

		// set default value
		initDefaultValue.setDefaultValue(classItemList);

	}

	private void getDataforListItem(PersonInfoCategory perInfoCategory, LayoutPersonInfoClsDto classItem,
			GeneralDate standardDate, String personId, String employeeId, PeregQuery query) {
		
		classItem.setItems(new ArrayList<>());
		
		if (perInfoCategory.getIsFixed() == IsFixed.FIXED) {
			List<PeregDto> peregDtoList = layoutingProcessor.findList(query);
			
			if (peregDtoList.isEmpty()) {
				// nếu không có dữ liệu nào thì tự tạo một cái mới trên Server và trả về.
				classItem.getItems().addAll(convertDefItem(perInfoCategory, classItem.getListItemDf()));
				
			} else {
				for (PeregDto peregDto : peregDtoList) {

					List<LayoutPersonInfoValueDto> valueItemsOfPeregDto = convertPeregDtoToValueItemList(perInfoCategory,
							peregDto, classItem.getListItemDf());

					classItem.getItems().addAll(valueItemsOfPeregDto);
				}
			}
		} else {
			// get option category data
			getMultiOptionData(classItem, perInfoCategory, query);
		}
		
		classItem.setListItemDf(null);
		classItem.setPersonInfoCategoryCD(perInfoCategory.getCategoryCode().v());
		
		// special process with category CS00069 item IS00779. change string length
		stampCardLength.updateLength(perInfoCategory, Arrays.asList(classItem));

		layoutControlComboBox.getComboBoxListForSelectionItems(employeeId, perInfoCategory, Arrays.asList(classItem),
				GeneralDate.today());

	}

	private List<LayoutPersonInfoValueDto> convertPeregDtoToValueItemList(PersonInfoCategory perInfoCategory,
			PeregDto peregDto, List<PerInfoItemDefDto> itemDefList) {

		List<LayoutPersonInfoValueDto> items = new ArrayList<>();

		Map<String, Object> itemCodeValueMap = MappingFactory.getFullDtoValue(peregDto);

		for (PerInfoItemDefDto itemDef : itemDefList) {

			// initial basic information
			LayoutPersonInfoValueDto valueItem = LayoutPersonInfoValueDto.cloneFromItemDef(perInfoCategory, itemDef);
			Object value = itemCodeValueMap.get(valueItem.getItemCode());

			if (valueItem.isComboBoxItem()) {
				value = value == null ? null : value.toString();
			}

			// set value
			valueItem.setValue(value == null ? itemDef.getInitValue(): value);

			// set recordId
			valueItem.setRecordId(peregDto.getDomainDto().getRecordId());

			items.add(valueItem);
		}

		return items;
	}
	
	private void getSingleOptionData(List<LayoutPersonInfoClsDto> classItemList, PersonInfoCategory perInfoCategory,
			PeregQuery query) {
		// authClassItem);
		if (perInfoCategory.isPersonType()) {
			List<PerInfoCtgData> perInfoCtgDatas = perInCtgDataRepo.getByPerIdAndCtgId(query.getPersonId(),
					perInfoCategory.getPersonInfoCategoryId());
			if (!perInfoCtgDatas.isEmpty()) {
				String recordId = perInfoCtgDatas.get(0).getRecordId();
				List<OptionalItemDataDto> dataItems = perInItemDataRepo.getAllInfoItemByRecordId(recordId).stream()
						.map(x -> x.genToPeregDto()).collect(Collectors.toList());
				MappingFactory.matchOptionalItemData(recordId, classItemList, dataItems);
			}
		} else {
			List<EmpInfoCtgData> empInfoCtgDatas = empInCtgDataRepo.getByEmpIdAndCtgId(query.getEmployeeId(),
					perInfoCategory.getPersonInfoCategoryId());
			if (!empInfoCtgDatas.isEmpty()) {
				String recordId = empInfoCtgDatas.get(0).getRecordId();
				List<OptionalItemDataDto> dataItems = empInItemDataRepo.getAllInfoItemByRecordId(recordId).stream()
						.map(x -> x.genToPeregDto()).collect(Collectors.toList());
				MappingFactory.matchOptionalItemData(recordId, classItemList, dataItems);
			}

		}

	}
	
	private void getMultiOptionData(LayoutPersonInfoClsDto classItem, PersonInfoCategory perInfoCategory,
			PeregQuery query) {
		if (perInfoCategory.isPersonType()) {
			List<PerInfoCtgData> perInfoCtgDatas = perInCtgDataRepo.getByPerIdAndCtgId(query.getPersonId(),
					perInfoCategory.getPersonInfoCategoryId());
			
			if (perInfoCtgDatas.isEmpty()) {
				classItem.getItems().addAll(convertDefItem(perInfoCategory, classItem.getListItemDf()));
				return;
			} 

			for (PerInfoCtgData perInfoCtgData : perInfoCtgDatas) {
				
				// create new line data
				List<LayoutPersonInfoValueDto> valueItems = convertDefItem(perInfoCategory, classItem.getListItemDf());
				
				// get person option data and map
				String recordId = perInfoCtgData.getRecordId();
				List<OptionalItemDataDto> dataItems = perInItemDataRepo.getAllInfoItemByRecordId(recordId).stream()
						.map(x -> x.genToPeregDto()).collect(Collectors.toList());
				
				MappingFactory.matchDataToValueItems(recordId, valueItems, dataItems);
				
				classItem.getItems().addAll(valueItems);
			}

		} else {

			List<EmpInfoCtgData> empInfoCtgDatas = empInCtgDataRepo.getByEmpIdAndCtgId(query.getEmployeeId(),
					perInfoCategory.getPersonInfoCategoryId());
			
			if (empInfoCtgDatas.isEmpty()) {
				classItem.getItems().addAll(convertDefItem(perInfoCategory, classItem.getListItemDf()));
				return;
			} 
			
			for (EmpInfoCtgData empInfoCtgData : empInfoCtgDatas) {
				
				// create new line data
				List<LayoutPersonInfoValueDto> valueItems = convertDefItem(perInfoCategory, classItem.getListItemDf());
				
				
				// get employee option data and map
				String recordId = empInfoCtgData.getRecordId();
				List<OptionalItemDataDto> dataItems = empInItemDataRepo.getAllInfoItemByRecordId(recordId).stream()
						.map(x -> x.genToPeregDto()).collect(Collectors.toList());
				
				MappingFactory.matchDataToValueItems(recordId, valueItems, dataItems);
				
				classItem.getItems().addAll(valueItems);
			}

		}
	
	}
	
	private List<LayoutPersonInfoValueDto> convertDefItem(PersonInfoCategory perInfoCategory,
			List<PerInfoItemDefDto> listItemDf) {
		return listItemDf.stream().map(itemDef -> LayoutPersonInfoValueDto.cloneFromItemDef(perInfoCategory, itemDef))
				.collect(Collectors.toList());
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
			String recordId = perInfoCtgData.getRecordId();

			List<OptionalItemDataDto> dataItems = perInItemDataRepo.getAllInfoItemByRecordId(recordId).stream()
					.map(x -> x.genToPeregDto()).collect(Collectors.toList());

			Optional<OptionalItemDataDto> startDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoItemDefId().equals(startDateId)).findFirst();

			Optional<OptionalItemDataDto> endDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoItemDefId().equals(endDateId)).findFirst();

			if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
				if (stardardDate.afterOrEquals((GeneralDate) startDateOpt.get().getValue())
						&& stardardDate.beforeOrEquals((GeneralDate) endDateOpt.get().getValue())) {
					MappingFactory.matchOptionalItemData(recordId, classItemList, dataItems);
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
			String recordId = empInfoCtgData.getRecordId();
			List<OptionalItemDataDto> dataItems = empInItemDataRepo.getAllInfoItemByRecordId(recordId).stream()
					.map(x -> x.genToPeregDto()).collect(Collectors.toList());

			Optional<OptionalItemDataDto> startDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoItemDefId().equals(startDateId)).findFirst();
			Optional<OptionalItemDataDto> endDateOpt = dataItems.stream()
					.filter(column -> column.getPerInfoItemDefId().equals(endDateId)).findFirst();

			if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
				Object startDate = startDateOpt.get().getValue();
				Object endDate = endDateOpt.get().getValue();
				if(startDate != null && endDate != null) {
					if (stardardDate.afterOrEquals((GeneralDate) startDateOpt.get().getValue())
							&& stardardDate.beforeOrEquals((GeneralDate) endDateOpt.get().getValue())) {
						MappingFactory.matchOptionalItemData(recordId, classItemList, dataItems);
						break;
					}
				}

			}

		}
	}

	private void cloneDefItemToValueItem(PersonInfoCategory perInfoCategory,
			List<LayoutPersonInfoClsDto> classItemList) {
		for (LayoutPersonInfoClsDto classItem : classItemList) {
			List<LayoutPersonInfoValueDto> items = new ArrayList<>();
			for (PerInfoItemDefDto itemDef : classItem.getListItemDf()) {
				items.add(LayoutPersonInfoValueDto.cloneFromItemDef(perInfoCategory, itemDef));
			}
			classItem.setPersonInfoCategoryCD(perInfoCategory.getCategoryCode().v());
			classItem.setCtgType(perInfoCategory.getCategoryType().value);
			classItem.setListItemDf(null);
			classItem.setItems(items);
		}
	}

	private void checkActionRoleItemData(List<PersonInfoItemAuth> inforAuthItems, LayoutPersonInfoClsDto classItem,
			boolean selfBrowsing) {

		if (inforAuthItems == null) {
			return;
		}

		for (LayoutPersonInfoValueDto valueItem : classItem.getItems()) {

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

}
