package nts.uk.ctx.pereg.app.find.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.pereg.app.find.common.InitDefaultValue;
import nts.uk.ctx.pereg.app.find.common.LayoutControlComBoBox;
import nts.uk.ctx.pereg.app.find.common.MappingFactory;
import nts.uk.ctx.pereg.app.find.common.StampCardLength;
import nts.uk.ctx.pereg.app.find.layout.dto.EmpMainCategoryDto;
import nts.uk.ctx.pereg.app.find.layout.dto.EmpMaintLayoutDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.ActionRole;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.GridEmpBody;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.GridLayoutPersonInfoClsDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoClsDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.LayoutPersonInfoValueDto;
import nts.uk.ctx.pereg.app.find.person.category.PerInfoCategoryFinder;
import nts.uk.ctx.pereg.app.find.person.category.PerInfoCtgFullDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefForLayoutDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefForLayoutFinder;
import nts.uk.ctx.pereg.dom.person.additemdata.category.EmInfoCtgDataRepository;
import nts.uk.ctx.pereg.dom.person.additemdata.category.EmpInfoCtgData;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemData;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemDataRepository;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCategoryRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonEmployeeType;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.daterangeitem.DateRangeItem;
import nts.uk.ctx.pereg.dom.person.info.item.PerInfoItemDefRepositoty;
import nts.uk.ctx.pereg.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.ctx.pereg.dom.person.info.setitem.SetItem;
import nts.uk.ctx.pereg.dom.person.layout.classification.LayoutItemType;
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
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.pereg.app.find.PeregEmpInfoQuery;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.PeregQueryByListEmp;
import nts.uk.shr.pereg.app.find.dto.GridPeregBySidDto;
import nts.uk.shr.pereg.app.find.dto.GridPeregDto;
import nts.uk.shr.pereg.app.find.dto.OptionalItemDataDto;
import nts.uk.shr.pereg.app.find.dto.PeregDto;

@Stateless
public class PeregProcessor {

	@Inject
	private PerInfoCategoryFinder perInfoCategoryFinder;

	@Inject
	private PerInfoItemDefForLayoutFinder itemForLayoutFinder;

	@Inject
	private EmInfoCtgDataRepository empInCtgDataRepo;

	@Inject
	private EmpInfoItemDataRepository empInfoItemDataRepository;

	@Inject
	private PerInfoItemDataRepository perInfoItemDataRepository;

	@Inject
	private PerInfoCtgDataRepository perInCtgDataRepo;

	@Inject
	private LayoutingProcessor layoutingProcessor;

	@Inject
	private PerInfoCategoryRepositoty perInfoCtgRepositoty;

	@Inject
	private PerInfoItemDefRepositoty perItemRepo;

	@Inject
	private PersonInfoItemAuthRepository itemAuthRepo;

	@Inject
	private InitDefaultValue initDefaultValue;

	@Inject
	private StampCardLength stampCardLength;

	@Inject
	private LayoutControlComBoBox layoutControlComboBox;

	@Inject
	private PersonInfoCategoryAuthRepository categoryAuthRepo;

	/**
	 * get person information category and it's children (Hiển thị category và
	 * danh sách tab category con của nó)
	 * 
	 * @param ctgId
	 * @return list PerCtgInfo: cha va danh sach con
	 */
	public List<PerInfoCtgFullDto> getCtgTab(String ctgId) {
		String contractCode = AppContexts.user().contractCode();
		String companyId = AppContexts.user().companyId();
		PersonInfoCategory perInfoCtg = perInfoCtgRepositoty.getPerInfoCategory(ctgId, contractCode).get();
		List<PersonInfoCategory> lstPerInfoCtg = new ArrayList<>();
		lstPerInfoCtg = perInfoCtgRepositoty.getPerInfoCtgByParentCdWithOrder(perInfoCtg.getCategoryCode().v(),
				contractCode, companyId, true);
		lstPerInfoCtg.add(0, perInfoCtg);
		return lstPerInfoCtg.stream()
				.map(x -> new PerInfoCtgFullDto(x.getPersonInfoCategoryId(), x.getCategoryCode().v(),
						x.getCategoryParentCode().v(), x.getCategoryName().v(), x.getPersonEmployeeType().value,
						x.getIsAbolition().value, x.getCategoryType().value, x.getIsFixed().value))
				.collect(Collectors.toList());
	}

	public EmpMaintLayoutDto getCategoryDetail(PeregQuery query) {
		// app context
		LoginUserContext loginUser = AppContexts.user();
		String contractCode = loginUser.contractCode();
		String loginEmpId = loginUser.employeeId();
		String roleId = loginUser.roles().forPersonalInfo();

		String employeeId = query.getEmployeeId();
		String categoryId = query.getCategoryId();
		boolean isSelfAuth = loginEmpId.equals(employeeId);

		// get category
		PersonInfoCategory perInfoCtg = perInfoCtgRepositoty.getPerInfoCategory(categoryId, contractCode).get();

		// get PerInfoItemDefForLayoutDto
		// check per info auth
		if (!perInfoCategoryFinder.checkCategoryAuth(query, perInfoCtg, roleId)) {
			return new EmpMaintLayoutDto();
		}

		// map PersonInfoItemDefinition →→ PerInfoItemDefForLayoutDto
		List<PerInfoItemDefForLayoutDto> lstPerInfoItemDefForLayout = getPerItemDefForLayout(perInfoCtg, contractCode,
				roleId, isSelfAuth);
		if (lstPerInfoItemDefForLayout.isEmpty()) {
			return new EmpMaintLayoutDto();
		}

		List<LayoutPersonInfoClsDto> classItemList = getDataClassItemList(query, perInfoCtg, lstPerInfoItemDefForLayout,
				roleId, isSelfAuth);

		// set default value
		initDefaultValue.setDefaultValue(classItemList);

		// special process with category CS00069 item IS00779. change string
		// length
		int digit = stampCardLength.getDigitOfStamp();
		stampCardLength.updateLength(perInfoCtg, classItemList, digit);

		return new EmpMaintLayoutDto(classItemList);
	}

	/**
	 * get full thông tin của nhân viên
	 * 
	 * @param empInfos
	 * @param categories
	 * @return
	 */
	public Map<String, List<GridLayoutPersonInfoClsDto>> getFullCategoryDetailByListEmp(
			List<PeregEmpInfoQuery> empInfos, Map<PersonInfoCategory, List<PersonInfoItemDefinition>> categories) {
		Map<String, List<GridLayoutPersonInfoClsDto>> classItemList = getDataClassItemListForCps013(empInfos,
				categories);
		List<PersonInfoCategory> categoryLst = new ArrayList<>(categories.keySet());
		int digit = stampCardLength.getDigitOfStamp();
		classItemList.entrySet().stream().forEach(f -> {
			Optional<PersonInfoCategory> categoryOpt = categoryLst.stream()
					.filter(c -> c.getCategoryCode().v().equals(f.getKey())).findFirst();
			f.getValue().forEach(i -> {
				// set default value
				initDefaultValue.setDefaultValue(i.getLayoutDtos());
				// special process with category CS00069 item IS00779. change
				// string length
				if (categoryOpt.isPresent()) {
					stampCardLength.updateLength(categoryOpt.get(), i.getLayoutDtos(), digit);
				}

			});

		});
		return classItemList;
	}

	/**
	 * Processor for layout in cps003
	 * 
	 * @param query
	 * @return
	 */
	public List<EmpMainCategoryDto> getCategoryDetailByListEmp(PeregQueryByListEmp query, boolean isFinder) {
		// app context
		LoginUserContext loginUser = AppContexts.user();

		GeneralDate today = GeneralDate.today();

		String categoryId = query.getCategoryId(),
				// loginEmpId = loginUser.employeeId(),
				contractCode = loginUser.contractCode(), roleId = loginUser.roles().forPersonalInfo();

		List<String> employeeIds = query.getEmpInfos().stream().map(m -> m.getEmployeeId())
				.collect(Collectors.toList());

		// get category
		PersonInfoCategory perInfoCtg = perInfoCtgRepositoty.getPerInfoCategory(categoryId, contractCode).orElse(null);

		// if not has category, return null list
		if (perInfoCtg == null) {
			return new ArrayList<>();
		}

		// get perInfoCtgAuth
		Optional<PersonInfoCategoryAuth> ctgAuthOpt = categoryAuthRepo.getDetailPersonCategoryAuthByPId(roleId,
				perInfoCtg.getPersonInfoCategoryId());

		// EA修正履歴3679 未来履歴 ：#Msg_1574, 過去履歴 ：#Msg_1575
		if (perInfoCtg.isHistoryCategory()) {
			if (!query.getStandardDate().equals(today)) {
				PersonInfoCategoryAuth ctgAuth = ctgAuthOpt.get();
				if ((ctgAuth.getSelfFutureHisAuth() == PersonInfoAuthType.HIDE
						|| ctgAuth.getOtherFutureHisAuth() == PersonInfoAuthType.HIDE)
						&& today.before(query.getStandardDate())) {
					throw new BusinessException("Msg_1574");
				}

				if ((ctgAuth.getSelfPastHisAuth() == PersonInfoAuthType.HIDE
						|| ctgAuth.getOtherPastHisAuth() == PersonInfoAuthType.HIDE)
						&& query.getStandardDate().before(today)) {
					throw new BusinessException("Msg_1575");
				}

			}
		}

		/**
		 * Get permision of current user for category (one by one employee) key:
		 * employeeId value: has or not has permision
		 */
		HashMap<String, Boolean> permisions = perInfoCategoryFinder.checkCategoryMultiAuth(employeeIds, perInfoCtg,
				roleId);

		// if not has permision, return null list
		if (permisions.size() == 0) {
			return new ArrayList<>();
		}

		// get item for self and other (self map key is true)
		HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems = getPerItemDefForLayout(perInfoCtg, contractCode,
				isFinder, roleId);

		List<GridLayoutPersonInfoClsDto> classItemList = getDataClassItemListForGrid(query, perInfoCtg, perItems);

		int digit = stampCardLength.getDigitOfStamp();
		classItemList.stream().forEach(f -> {
			// set default value
			initDefaultValue.setDefaultValue(f.getLayoutDtos());

			// special process with category CS00069 item IS00779. change string
			// length
			stampCardLength.updateLength(perInfoCtg, f.getLayoutDtos(), digit);
		});

		return classItemList.stream().map(cls -> {
			List<GridEmpBody> items = cls.getLayoutDtos().stream().flatMap(f -> f.getItems().stream())
					.map(m -> new GridEmpBody(m.getItemCode(), m.getItemParentCode(), m.getActionRole(), m.getValue(),
							m.getTextValue(), m.getRecordId(), m.getLstComboBoxValue()))
					.collect(Collectors.toList());

			return new EmpMainCategoryDto(cls.getEmployeeId(), items);
		}).collect(Collectors.toList());
	}

	public List<LayoutPersonInfoClsDto> getDataClassItemList(PeregQuery query, PersonInfoCategory perInfoCtg,
			List<PerInfoItemDefForLayoutDto> lstPerInfoItemDef, String roleId, boolean isSelf) {

		// combo-box sẽ lấy dựa theo các ngày startDate của từng category
		GeneralDate comboBoxStandardDate = GeneralDate.today();

		List<LayoutPersonInfoClsDto> classItemList = creatClassItemList(lstPerInfoItemDef, perInfoCtg);

		if (perInfoCtg.isFixed()) {
			PeregDto peregDto = layoutingProcessor.findSingle(query);

			if (peregDto != null) {
				// map data
				MappingFactory.mapListItemClass(peregDto, classItemList);

				Map<String, Object> itemValueMap = MappingFactory.getFullDtoValue(peregDto);
				List<String> standardDateItemCodes = Arrays.asList("IS00020", "IS00077", "IS00082", "IS00119",
						"IS00781");

				for (String itemCode : standardDateItemCodes) {
					if (itemValueMap.containsKey(itemCode)) {
						comboBoxStandardDate = (GeneralDate) itemValueMap.get(itemCode);
						break;
					}
				}

				// check quyền category với bản thân|người khác
				// lịch sử tương lai|quá khứ
				// rồi check theo từng record xem nó là
				// bản ghi thuộc quá khứ|tương lai
				// bằng cách kiểm tra value của item startdate và endDate =>
				// isFuture
				// và set lại quyền cho item đó
				setItemAuthForCategoryHistory(categoryAuthRepo, perInfoCtg, classItemList, roleId, isSelf);

			}

		} else {
			switch (perInfoCtg.getCategoryType()) {
			case SINGLEINFO:
				setOptionDataSingleCategory(perInfoCtg, classItemList, query);
				break;
			default:
				setOptionDataWithRecordId(perInfoCtg, classItemList, query);
				break;
			}
		}

		// get Combo-Box List
		layoutControlComboBox.getComboBoxListForSelectionItems(query.getEmployeeId(), perInfoCtg, classItemList,
				comboBoxStandardDate);

		return classItemList;
	}

	/*
	 * Tính xem đang xem tương lai hay quá khứ
	 */
	private static Boolean getFuture(LayoutPersonInfoClsDto param) {
		Boolean historyReatime = null;
		// Item code của start date lịch sử liên tục
		List<String> start_hist_list = Arrays.asList("IS00026", "IS00066", "IS00071", "IS00077", "IS00082", "IS00119",
				"IS00781", "IS00784", "IS00788", "IS00792", "IS00796", "IS01078", "IS01091", "IS00087", "IS00102",
				"IS01016", "IS00781");
		// Item code của end date lịch sử liên tục
		List<String> end_hist_list = Arrays.asList("IS00027", "IS00067", "IS00072", "IS00078", "IS00083", "IS00120",
				"IS00782", "IS00785", "IS00789", "IS00793", "IS00797", "IS01079", "IS01092", "IS00088", "IS00103",
				"IS01017", "IS00782");

		// Item code của start date lịch sử không liên tục
		List<String> start_hist_not_reatime_list = Arrays.asList("IS00087", "IS00102", "IS01016");

		// Item code của end date lịch sử không liên tục
		List<String> end_hist_not_reatime_list = Arrays.asList("IS00088", "IS00103", "IS01017");

		GeneralDate startdate = null;
		GeneralDate enddate = null;
		GeneralDate today = GeneralDate.today();
		Boolean isFuture = null;// null là xem hiện tại
		
		for(int i = 0 ; i <= param.getItems().size() - 1; i++) {
			LayoutPersonInfoValueDto item = param.getItems().get(i);
			
			if (start_hist_list.contains(item.getItemCode())) {
				startdate = (GeneralDate) item.getValue();
				enddate = (GeneralDate) param.getItems().get(i+1).getValue();
				historyReatime = true;
			}
			
			if (end_hist_list.contains(item.getItemCode())) {
				enddate = (GeneralDate) item.getValue();
				historyReatime = true;
			}
			
			if (start_hist_not_reatime_list.contains(item.getItemCode())) {
				startdate = (GeneralDate) item.getValue();
				historyReatime = false;
			}
			
			if (end_hist_not_reatime_list.contains(item.getItemCode())) {
				enddate = (GeneralDate) item.getValue();
				historyReatime = false;
			}
		}
		if (startdate != null && enddate != null) {
			if (historyReatime) {
				if (today.before(startdate)) {
					return true;
				}
				if (today.after(enddate)) {
					return false;
				}
			} else {
				// startDate < today < endDate => hiện tại
				if (today.before(startdate) && today.after(enddate)) {
					return null;
				}

				// startDate < endDate < today => đang xem record quá khứ
				// today < startDate < endDate => đang xem record tương lai
				if (today.before(enddate) && today.before(startdate)) {
					return true;
				}
				if (today.after(startdate) && today.after(enddate)) {
					return false;
				}
			}
		}

		return isFuture;
	}

	/*
	 * Tính xem đang xem tương lai hay quá khứ của lịch sử không liên tục
	 */
	private static Boolean getFutureisHistoryNotReatime(Object dateValueStart, Object dateValueEnd) {
		GeneralDate startdate = GeneralDate.today();
		GeneralDate enddate = GeneralDate.today();
		GeneralDate today = GeneralDate.today();
		Boolean isFutureNotReatime = null;// null là xem hiện tại

		if (dateValueStart != null) {
			startdate = (GeneralDate) dateValueStart;
		}
		if (dateValueEnd != null) {
			enddate = (GeneralDate) dateValueEnd;
		}

		// startDate < today < endDate => hiện tại
		if (today.before(startdate) && today.after(enddate)) {
			return isFutureNotReatime;
		}

		// startDate < endDate < today => đang xem record quá khứ
		// today < startDate < endDate => đang xem record tương lai
		if (today.before(enddate) && today.before(startdate)) {
			return true;
		}
		if (today.after(startdate) && today.after(enddate)) {
			return false;
		}

		return isFutureNotReatime;
	}

	// key categoryCodem, List<GridLayoutPersonInfoClsDto> -> list này bao gồm
	// nhiều thông tin của một nhân viên
	private Map<String, List<GridLayoutPersonInfoClsDto>> getDataClassItemListForCps013(
			List<PeregEmpInfoQuery> empInfos, Map<PersonInfoCategory, List<PersonInfoItemDefinition>> categories) {
		// List<PerInfoItemDefForLayoutDto> perItems
		Map<String, List<GridLayoutPersonInfoClsDto>> result = new HashMap<>();
		categories.entrySet().stream().forEach(c -> {
			if (c.getKey().isFixed()) {
				// List<PerInfoItemDefForLayoutDto> perItems
				List<PerInfoItemDefForLayoutDto> perItems = getPerItemDefForLayoutForCps013(c.getKey(), c.getValue());
				PeregQueryByListEmp query = PeregQueryByListEmp.createQueryLayout(c.getKey().getPersonInfoCategoryId(),
						c.getKey().getCategoryCode().v(), null, empInfos);
				List<GridPeregBySidDto> peregDtoLst = layoutingProcessor.getAllDataBySid(query);
				if (!CollectionUtil.isEmpty(peregDtoLst)) {
					List<GridLayoutPersonInfoClsDto> resultsSync = new ArrayList<>();
					peregDtoLst.stream().forEach(m -> {
						m.getPeregDto().stream().forEach(d -> {
							// combo-box sẽ lấy dựa theo các ngày startDate của
							// từng category
							GeneralDate comboBoxStandardDate = GeneralDate.today();

							GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(m.getEmployeeId(),
									m.getPersonId(), creatClassItemList(perItems, c.getKey()));

							MappingFactory.mapListItemClassCPS003(d, dto.getLayoutDtos());

							Map<String, Object> itemValueMap = MappingFactory.getFullDtoValue(d);

							List<String> standardDateItemCodes = Arrays.asList("IS00020", "IS00077", "IS00082",
									"IS00119", "IS00781");

							for (String itemCode : standardDateItemCodes) {
								if (itemValueMap.containsKey(itemCode)) {
									comboBoxStandardDate = (GeneralDate) itemValueMap.get(itemCode);
									break;
								}
							}

							// get Combo-Box List
							layoutControlComboBox.getComboBoxListForSelectionItems(m.getEmployeeId(), c.getKey(),
									dto.getLayoutDtos(), comboBoxStandardDate);

							resultsSync.add(dto);
						});
					});

					result.put(c.getKey().getCategoryCode().v(), resultsSync);
				}
			} else {
				// TODO
				System.out.println("Chưa làm");
			}
		});
		return result;
	}

	private List<GridLayoutPersonInfoClsDto> getDataClassItemListForGrid(PeregQueryByListEmp query,
			PersonInfoCategory perInfoCtg, HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems) {
		String selfEmployeeId = AppContexts.user().employeeId();

		if (perInfoCtg.isFixed()) {
			List<GridPeregDto> peregDtoLst = layoutingProcessor.findAllData(query);

			if (!CollectionUtil.isEmpty(peregDtoLst)) {

				List<GridLayoutPersonInfoClsDto> resultsSync = Collections.synchronizedList(new ArrayList<>());

				peregDtoLst.stream().forEach(m -> {
					// combo-box sẽ lấy dựa theo các ngày startDate của từng
					// category
					GeneralDate comboBoxStandardDate = query.getStandardDate();
					if (m == null)
						return;
					GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(m.getEmployeeId(), m.getPersonId(),
							m.getEmployeeId().equals(selfEmployeeId)
									? creatClassItemList(perItems.get(true), perInfoCtg)
									: creatClassItemList(perItems.get(false), perInfoCtg));

					MappingFactory.mapListItemClassCPS003(m.getPeregDto(), dto.getLayoutDtos());

					Map<String, Object> itemValueMap = MappingFactory.getFullDtoValue(m.getPeregDto());

					List<String> standardDateItemCodes = Arrays.asList("IS00020", "IS00077", "IS00082", "IS00119",
							"IS00781");

					for (String itemCode : standardDateItemCodes) {
						if (itemValueMap.containsKey(itemCode)) {
							comboBoxStandardDate = (GeneralDate) itemValueMap.get(itemCode);
							break;
						}
					}

					// get Combo-Box List theo ngày trên màn hình
					layoutControlComboBox.getComboBoxListForSelectionItems(m.getEmployeeId(), perInfoCtg,
							dto.getLayoutDtos(), comboBoxStandardDate);

					resultsSync.add(dto);
				});

				return new ArrayList<>(resultsSync);
			}
		} else { // for optional category
			switch (perInfoCtg.getCategoryType()) {
			case SINGLEINFO:
				return setOptionDataSingleCategory(perInfoCtg, query, selfEmployeeId, perItems);
			default:
				return setOptionDataWithRecordId(perInfoCtg, query, selfEmployeeId, perItems);
			}
		}
		return new ArrayList<>();
	}

	private List<GridLayoutPersonInfoClsDto> setOptionDataSingleCategory(PersonInfoCategory perInfoCtg,
			PeregQueryByListEmp query, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems) {
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		if (perInfoCtg.isEmployeeType()) {
			// list sids
			List<String> sids = query.getEmpInfos().stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
			// employee option data
			Map<String, List<EmpInfoCtgData>> empInfoCtgDatas = empInCtgDataRepo
					.getBySidsAndCtgId(sids, perInfoCtg.getPersonInfoCategoryId()).stream()
					.collect(Collectors.groupingBy(c -> c.getEmployeeId()));
			if (empInfoCtgDatas.size() < sids.size()) {
				sids.stream().forEach(c -> {
					List<EmpInfoCtgData> empInfoCtgData = empInfoCtgDatas.get(c);
					if (CollectionUtil.isEmpty(empInfoCtgData)) {
						empInfoCtgDatas.put(c, new ArrayList<>());
					}
				});
			}
			if (!empInfoCtgDatas.isEmpty()) {
				List<GridLayoutPersonInfoClsDto> empResult = getAndMapEmpOptionItem(
						new ArrayList<>(empInfoCtgDatas.keySet()), query, selfEmployeeId, perItems, perInfoCtg);
				result.addAll(empResult);
			}
		} else {
			// list sids
			List<String> pids = query.getEmpInfos().stream().map(c -> c.getInfoId()).collect(Collectors.toList());
			// person option data
			Map<String, List<PerInfoCtgData>> perInfoCtgDatas = perInCtgDataRepo
					.getAllByPidsAndCtgId(pids, perInfoCtg.getPersonInfoCategoryId()).stream()
					.collect(Collectors.groupingBy(c -> c.getPersonId()));
			if (perInfoCtgDatas.size() < pids.size()) {
				pids.stream().forEach(c -> {
					List<PerInfoCtgData> perInfoCtgData = perInfoCtgDatas.get(c);
					if (CollectionUtil.isEmpty(perInfoCtgData)) {
						perInfoCtgDatas.put(c, new ArrayList<>());
					}
				});
			}
			if (!perInfoCtgDatas.isEmpty()) {
				List<GridLayoutPersonInfoClsDto> perResult = getAndMapPerOptionItem(
						new ArrayList<>(perInfoCtgDatas.keySet()), query, selfEmployeeId, perItems, perInfoCtg);
				result.addAll(perResult);
			}
		}

		query.getEmpInfos().forEach(c -> {
			List<GridLayoutPersonInfoClsDto> empLstOpt = result.stream()
					.filter(i -> i.getEmployeeId().equals(c.getEmployeeId())).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(empLstOpt)) {
				result.add(new GridLayoutPersonInfoClsDto(c.getEmployeeId(), c.getPersonId(), new ArrayList<>()));
			}
		});

		return result;
	}

	private List<GridLayoutPersonInfoClsDto> setOptionDataWithRecordId(PersonInfoCategory perInfoCtg,
			PeregQueryByListEmp query, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems) {

		List<GridLayoutPersonInfoClsDto> result = getHistInfoCtgByCtgIdAndSid(query, selfEmployeeId, perItems,
				perInfoCtg);
		return result;

	}

	private List<GridLayoutPersonInfoClsDto> getAndMapEmpOptionItem(List<String> recordIds, PeregQueryByListEmp query,
			String selfEmployeeId, HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems,
			PersonInfoCategory perInfoCtg) {
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		Map<String, List<OptionalItemDataDto>> empOptionItemDatas = this.empInfoItemDataRepository
				.getAllInfoItemByRecordId(recordIds).stream().map(c -> c.genToPeregDto())
				.collect(Collectors.groupingBy(x -> x.getRecordId()));
		if (empOptionItemDatas.size() < recordIds.size()) {
			recordIds.stream().forEach(c -> {
				List<OptionalItemDataDto> empOptionItemData = empOptionItemDatas.get(c);
				if (CollectionUtil.isEmpty(empOptionItemData)) {
					empOptionItemDatas.put(c, new ArrayList<>());
				}

			});
		}
		empOptionItemDatas.entrySet().forEach(c -> {
			Optional<PeregEmpInfoQuery> empInfo = query.getEmpInfos().stream()
					.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
			if (empInfo.isPresent()) {
				GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(empInfo.get().getEmployeeId(),
						empInfo.get().getPersonId(),
						empInfo.get().getEmployeeId().equals(selfEmployeeId)
								? creatClassItemList(perItems.get(true), perInfoCtg)
								: creatClassItemList(perItems.get(false), perInfoCtg));
				MappingFactory.matchOptionalItemData(c.getKey(), dto.getLayoutDtos(), new ArrayList<>(c.getValue()));
				// get Combo-Box List theo ngày trên màn hình
				layoutControlComboBox.getComboBoxListForSelectionItems(empInfo.get().getEmployeeId(), perInfoCtg,
						dto.getLayoutDtos(), query.getStandardDate());
				result.add(dto);
			}
		});
		return result;
	}

	private List<GridLayoutPersonInfoClsDto> getAndMapPerOptionItem(List<String> recordIds, PeregQueryByListEmp query,
			String selfEmployeeId, HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems,
			PersonInfoCategory perInfoCtg) {
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		List<OptionalItemDataDto> itemDataLst = perInfoItemDataRepository.getAllInfoItemByRecordId(recordIds).stream()
				.map(c -> c.genToPeregDto()).collect(Collectors.toList());
		;
		Map<String, List<OptionalItemDataDto>> perOptionItemDatas = itemDataLst.stream()
				.collect(Collectors.groupingBy(x -> x.getRecordId()));
		if (perOptionItemDatas.size() < recordIds.size()) {
			recordIds.stream().forEach(c -> {
				List<OptionalItemDataDto> empOptionItemData = perOptionItemDatas.get(c);
				if (CollectionUtil.isEmpty(empOptionItemData)) {
					perOptionItemDatas.put(c, new ArrayList<>());
				}

			});
		}
		perOptionItemDatas.entrySet().forEach(c -> {
			Optional<PeregEmpInfoQuery> empInfo = query.getEmpInfos().stream()
					.filter(emp -> emp.getPersonId().equals(c.getKey())).findFirst();
			if (empInfo.isPresent()) {
				GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(empInfo.get().getEmployeeId(),
						empInfo.get().getPersonId(),
						empInfo.get().getEmployeeId().equals(selfEmployeeId)
								? creatClassItemList(perItems.get(true), perInfoCtg)
								: creatClassItemList(perItems.get(false), perInfoCtg));
				MappingFactory.matchOptionalItemData(c.getKey(), dto.getLayoutDtos(), new ArrayList<>(c.getValue()));
				// get Combo-Box List theo ngày trên màn hình
				layoutControlComboBox.getComboBoxListForSelectionItems(empInfo.get().getEmployeeId(), perInfoCtg,
						dto.getLayoutDtos(), query.getStandardDate());
				result.add(dto);
			}
		});
		return result;
	}

	public List<LayoutPersonInfoClsDto> creatClassItemList(List<PerInfoItemDefForLayoutDto> lstClsItem,
			PersonInfoCategory perInfoCtg) {
		return lstClsItem.stream().map(item -> {
			LayoutPersonInfoClsDto layoutPerInfoClsDto = new LayoutPersonInfoClsDto();

			layoutPerInfoClsDto.setPersonInfoCategoryID(item.getPerInfoCtgId());
			layoutPerInfoClsDto.setPersonInfoCategoryCD(item.getPerInfoCtgCd());
			layoutPerInfoClsDto.setLayoutItemType(LayoutItemType.ITEM);
			layoutPerInfoClsDto.setClassName(item.getItemName());
			layoutPerInfoClsDto.setDispOrder(item.getDispOrder());
			layoutPerInfoClsDto.getItems().add(LayoutPersonInfoValueDto.initData(item, perInfoCtg));

			if (item.getItemTypeState().getItemType() != 2) {
				item.getLstChildItemDef().forEach(childItem -> {
					layoutPerInfoClsDto.setDispOrder(childItem.getDispOrder());
					layoutPerInfoClsDto.getItems().add(LayoutPersonInfoValueDto.initData(childItem, perInfoCtg));
				});
			}

			return layoutPerInfoClsDto;
		}).collect(Collectors.toList());
	}

	private void setOptionDataSingleCategory(PersonInfoCategory perInfoCtg, List<LayoutPersonInfoClsDto> classItemList,
			PeregQuery query) {
		if (perInfoCtg.isEmployeeType()) {
			// employee option data
			List<EmpInfoCtgData> empInfoCtgDatas = empInCtgDataRepo.getByEmpIdAndCtgId(query.getEmployeeId(),
					perInfoCtg.getPersonInfoCategoryId());

			if (!empInfoCtgDatas.isEmpty()) {
				String recordId = empInfoCtgDatas.get(0).getRecordId();
				getAndMapEmpOptionItem(recordId, classItemList);
			}
		} else {
			// person option data
			List<PerInfoCtgData> perInfoCtgDatas = perInCtgDataRepo.getByPerIdAndCtgId(query.getPersonId(),
					perInfoCtg.getPersonInfoCategoryId());

			if (!perInfoCtgDatas.isEmpty()) {
				String recordId = perInfoCtgDatas.get(0).getRecordId();
				getAndMapPerOptionItem(recordId, classItemList);
			}
		}

	}

	private void setOptionDataWithRecordId(PersonInfoCategory perInfoCtg, List<LayoutPersonInfoClsDto> classItemList,
			PeregQuery query) {
		if (perInfoCtg.isEmployeeType()) {
			// employee option data
			getAndMapEmpOptionItem(query.getInfoId(), classItemList);
		} else {
			// person option data
			getAndMapPerOptionItem(query.getInfoId(), classItemList);
		}

	}

	private void getAndMapEmpOptionItem(String recordId, List<LayoutPersonInfoClsDto> classItemList) {
		List<OptionalItemDataDto> empOptionItemData = empInfoItemDataRepository.getAllInfoItemByRecordId(recordId)
				.stream().map(x -> x.genToPeregDto()).collect(Collectors.toList());

		MappingFactory.matchOptionalItemData(recordId, classItemList, empOptionItemData);
	}

	private void getAndMapPerOptionItem(String recordId, List<LayoutPersonInfoClsDto> classItemList) {
		List<OptionalItemDataDto> perOptionItemData = perInfoItemDataRepository.getAllInfoItemByRecordId(recordId)
				.stream().map(x -> x.genToPeregDto()).collect(Collectors.toList());

		MappingFactory.matchOptionalItemData(recordId, classItemList, perOptionItemData);
	}

	/**
	 * dùng cho cps013
	 * 
	 * @param category
	 * @param contractCode
	 * @param roleId
	 * @return
	 */
	private List<PerInfoItemDefForLayoutDto> getPerItemDefForLayoutForCps013(PersonInfoCategory category,
			List<PersonInfoItemDefinition> fullItemDefinitionList) {

		List<PersonInfoItemDefinition> parentItemDefinitionList = fullItemDefinitionList.stream()
				.filter(item -> item.haveNotParentCode()).collect(Collectors.toList());

		List<PerInfoItemDefForLayoutDto> ỉtemLst = new ArrayList<>();

		for (int i = 0; i < parentItemDefinitionList.size(); i++) {
			PersonInfoItemDefinition itemDefinition = parentItemDefinitionList.get(i);

			PerInfoItemDefForLayoutDto itemDto = itemForLayoutFinder.createItemLayoutDto(category, itemDefinition, i,
					ActionRole.EDIT);

			// get and convert childrenItems
			List<PerInfoItemDefForLayoutDto> childrenItems = itemForLayoutFinder
					.getChildrenItems(fullItemDefinitionList, category, itemDefinition, i, ActionRole.EDIT);

			itemDto.setLstChildItemDef(childrenItems);

			ỉtemLst.add(itemDto);
		}
		return ỉtemLst;
	}

	public HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> getPerItemDefForLayout(PersonInfoCategory category,
			String contractCode, boolean isFinder, String roleId) {
		// get per info item def with order
		List<PersonInfoItemDefinition> fullItemDefinitionList = perItemRepo
				.getAllItemDefByCategoryId(category.getPersonInfoCategoryId(), contractCode);

		List<PersonInfoItemDefinition> parentItemDefinitionList = fullItemDefinitionList.stream()
				.filter(item -> item.haveNotParentCode()).collect(Collectors.toList());

		List<PerInfoItemDefForLayoutDto> lstSelf = new ArrayList<>(), lstOther = new ArrayList<>();

		PersonInfoCategoryAuth categoryAuthOpt = this.categoryAuthRepo
				.getDetailPersonCategoryAuthByPId(roleId, category.getPersonInfoCategoryId()).orElse(null);

		Map<String, PersonInfoItemAuth> mapItemAuth = itemAuthRepo
				.getAllItemAuth(roleId, category.getPersonInfoCategoryId()).stream()
				.collect(Collectors.toMap(e -> e.getPersonItemDefId(), e -> e));
		if (isFinder == true) {
			for (int i = 0; i < parentItemDefinitionList.size(); i++) {
				PersonInfoItemDefinition itemDefinition = parentItemDefinitionList.get(i);

				// check authority
				PersonInfoItemAuth personInfoItemAuth = mapItemAuth.get(itemDefinition.getPerInfoItemDefId());

				if (personInfoItemAuth == null) {
					continue;
				}

				PersonInfoAuthType selfRole = personInfoItemAuth.getSelfAuth(),
						otherRole = personInfoItemAuth.getOtherAuth();
				if (categoryAuthOpt == null) {
					return new HashMap<>();
				}
				if (selfRole != PersonInfoAuthType.HIDE
						&& categoryAuthOpt.getAllowPersonRef() != PersonInfoPermissionType.NO) {
					// convert item-definition to layoutDto
					ActionRole role = selfRole == PersonInfoAuthType.REFERENCE ? ActionRole.VIEW_ONLY : ActionRole.EDIT;

					PerInfoItemDefForLayoutDto itemDto = itemForLayoutFinder.createItemLayoutDto(category,
							itemDefinition, i, role);

					// get and convert childrenItems
					List<PerInfoItemDefForLayoutDto> childrenItems = itemForLayoutFinder
							.getChildrenItems(fullItemDefinitionList, category, itemDefinition, i, role);

					itemDto.setLstChildItemDef(childrenItems);

					lstSelf.add(itemDto);
				}

				if (otherRole != PersonInfoAuthType.HIDE
						&& categoryAuthOpt.getAllowOtherRef() != PersonInfoPermissionType.NO) {
					// convert item-definition to layoutDto
					// ActionRole role = otherRole ==
					// PersonInfoAuthType.REFERENCE ?
					// ActionRole.VIEW_ONLY : ActionRole.EDIT;
					ActionRole role = otherRole == PersonInfoAuthType.REFERENCE ? ActionRole.VIEW_ONLY
							: ActionRole.EDIT;

					PerInfoItemDefForLayoutDto itemDto = itemForLayoutFinder.createItemLayoutDto(category,
							itemDefinition, i, role);

					// get and convert childrenItems
					List<PerInfoItemDefForLayoutDto> childrenItems = itemForLayoutFinder
							.getChildrenItems(fullItemDefinitionList, category, itemDefinition, i, role);

					itemDto.setLstChildItemDef(childrenItems);

					lstOther.add(itemDto);
				}

			}

		} else {
			for (int i = 0; i < parentItemDefinitionList.size(); i++) {
				PersonInfoItemDefinition itemDefinition = parentItemDefinitionList.get(i);

				// check authority
				PersonInfoItemAuth personInfoItemAuth = mapItemAuth.get(itemDefinition.getPerInfoItemDefId());

				if (personInfoItemAuth == null) {
					continue;
				}

				if (categoryAuthOpt == null) {
					return new HashMap<>();
				}
				ActionRole role = ActionRole.EDIT;

				PerInfoItemDefForLayoutDto itemDtoSelf = itemForLayoutFinder.createItemLayoutDto(category,
						itemDefinition, i, role);

				// get and convert childrenItems
				List<PerInfoItemDefForLayoutDto> childrenItemsSelf = itemForLayoutFinder
						.getChildrenItems(fullItemDefinitionList, category, itemDefinition, i, role);

				itemDtoSelf.setLstChildItemDef(childrenItemsSelf);

				lstSelf.add(itemDtoSelf);

				PerInfoItemDefForLayoutDto itemDtoOther = itemForLayoutFinder.createItemLayoutDto(category,
						itemDefinition, i, role);

				// get and convert childrenItems
				List<PerInfoItemDefForLayoutDto> childrenItemsOther = itemForLayoutFinder
						.getChildrenItems(fullItemDefinitionList, category, itemDefinition, i, role);

				itemDtoOther.setLstChildItemDef(childrenItemsOther);

				lstOther.add(itemDtoOther);
			}
		}

		return new HashMap<Boolean, List<PerInfoItemDefForLayoutDto>>() {
			private static final long serialVersionUID = 1L;
			{
				put(true, lstSelf);
				put(false, lstOther);
			}
		};
	}

	private List<PerInfoItemDefForLayoutDto> getPerItemDefForLayout(PersonInfoCategory category, String contractCode,
			String roleId, boolean isSelf) {

		// get per info item def with order
		List<PersonInfoItemDefinition> fullItemDefinitionList = perItemRepo
				.getAllItemDefByCategoryId(category.getPersonInfoCategoryId(), contractCode);

		List<PersonInfoItemDefinition> parentItemDefinitionList = fullItemDefinitionList.stream()
				.filter(item -> item.haveNotParentCode()).collect(Collectors.toList());

		List<PerInfoItemDefForLayoutDto> lstReturn = new ArrayList<>();

		Map<String, PersonInfoItemAuth> mapItemAuth = itemAuthRepo
				.getAllItemAuth(roleId, category.getPersonInfoCategoryId()).stream()
				.collect(Collectors.toMap(e -> e.getPersonItemDefId(), e -> e));

		for (int i = 0; i < parentItemDefinitionList.size(); i++) {
			PersonInfoItemDefinition itemDefinition = parentItemDefinitionList.get(i);

			// check authority
			PersonInfoItemAuth personInfoItemAuth = mapItemAuth.get(itemDefinition.getPerInfoItemDefId());

			if (personInfoItemAuth == null) {
				continue;
			}

			PersonInfoAuthType roleOfItem = isSelf ? personInfoItemAuth.getSelfAuth()
					: personInfoItemAuth.getOtherAuth();

			if (roleOfItem == PersonInfoAuthType.HIDE) {
				continue;
			}

			// convert item-definition to layoutDto
			ActionRole role = roleOfItem == PersonInfoAuthType.REFERENCE ? ActionRole.VIEW_ONLY : ActionRole.EDIT;

			PerInfoItemDefForLayoutDto itemDto = itemForLayoutFinder.createItemLayoutDto(category, itemDefinition, i,
					role);

			// get and convert childrenItems
			List<PerInfoItemDefForLayoutDto> childrenItems = itemForLayoutFinder
					.getChildrenItems(fullItemDefinitionList, category, itemDefinition, i, role);

			itemDto.setLstChildItemDef(childrenItems);

			lstReturn.add(itemDto);
		}

		return lstReturn;
	}

	// check quyền category lich su :
	// 1. nếu quyền ở quá khứ = 参照のみ thì set all item = 参照のみ
	// 2. nếu quyền ở tương lai = 参照のみ thì set all item = 参照のみ
	public static void setItemAuthForCategoryHistory(PersonInfoCategoryAuthRepository categoryAuthRepoParam,
			PersonInfoCategory category, List<LayoutPersonInfoClsDto> classItemList, String roleId,
			boolean isSelfAuth) {

		// get perInfoCtgAuth
		Optional<PersonInfoCategoryAuth> perInfoCtgAuth = categoryAuthRepoParam.getDetailPersonCategoryAuthByPId(roleId,
				category.getPersonInfoCategoryId());

		if (!perInfoCtgAuth.isPresent()) {
			return;
		}
		
		Boolean isFuture = null;
		
		for (LayoutPersonInfoClsDto layout : classItemList) {
			isFuture = getFuture(layout);
			if (isFuture != null) {
				break;
			}
		}

		// đi từng layout
		for (LayoutPersonInfoClsDto layout : classItemList) {
			// for items của classItemList
			for (LayoutPersonInfoValueDto item : layout.getItems()) {
				// kiểm tra actionRole của item nếu thằng nào là EDIT thì mới
				// thực hiện step dưới
				if (isFuture != null) {
					switch (category.getCategoryType()) {
					case CONTINUOUSHISTORY:
					case NODUPLICATEHISTORY:
					case DUPLICATEHISTORY:
					case CONTINUOUS_HISTORY_FOR_ENDDATE:
						if (isSelfAuth) {
							if (isFuture
									&& perInfoCtgAuth.get().getSelfFutureHisAuth() == PersonInfoAuthType.REFERENCE) {
								// bản thân (Tương lai)
								// set lại quyền ActionRole cho item là
								// VIEW_ONLY
								item.setActionRole(ActionRole.VIEW_ONLY);
							} else if (!isFuture
									&& perInfoCtgAuth.get().getSelfPastHisAuth() == PersonInfoAuthType.REFERENCE) {
								// bản thân (quá khứ)
								// set lại quyền ActionRole cho item là
								// VIEW_ONLY
								item.setActionRole(ActionRole.VIEW_ONLY);
							}

						} else {
							if (isFuture
									&& perInfoCtgAuth.get().getOtherFutureHisAuth() == PersonInfoAuthType.REFERENCE) {
								// người khác (Tương lai)
								// set lại quyền ActionRole cho item là
								// VIEW_ONLY
								item.setActionRole(ActionRole.VIEW_ONLY);
							} else if (!isFuture
									&& perInfoCtgAuth.get().getOtherPastHisAuth() == PersonInfoAuthType.REFERENCE) {
								// người khác (Quá khứ)
								// set lại quyền ActionRole cho item là
								// VIEW_ONLY
								item.setActionRole(ActionRole.VIEW_ONLY);
							}
						}
						break;
					default:
						break;
					}
				}
			}
		}

	}

	/**
	 * cps003
	 * 
	 * @param query
	 * @param perInfoCtg
	 * @return
	 */
	public List<GridLayoutPersonInfoClsDto> getHistInfoCtgByCtgIdAndSid(PeregQueryByListEmp query,
			String selfEmployeeId, HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems,
			PersonInfoCategory perInfoCtg) {
		String contractCode = AppContexts.user().contractCode();
		if (perInfoCtg.isSingleCategory()) {
			// SINGLE CATEGORY
			return new ArrayList<>();
		}
		// get combo-box object
		List<PersonInfoItemDefinition> lstItemDef = perItemRepo
				.getAllPerInfoItemDefByCategoryId(perInfoCtg.getPersonInfoCategoryId(), contractCode);

		if (perInfoCtg.isMultiCategory()) {
			// MULTI CATEGORY
			return getListOfMultiCategoryCps003(lstItemDef, query, selfEmployeeId, perItems, perInfoCtg);
		} else {
			// HISTORY CATEGORY
			return getListOfHistoryCategoryCps003(query, perInfoCtg, lstItemDef, selfEmployeeId, perItems);
		}
	}

	/**
	 * optional cps003
	 * 
	 * @param lstItemDef
	 * @param query
	 * @param selfEmployeeId
	 * @param perItems
	 * @param perInfoCtg
	 * @return
	 */
	private List<GridLayoutPersonInfoClsDto> getListOfMultiCategoryCps003(List<PersonInfoItemDefinition> lstItemDef,
			PeregQueryByListEmp query, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems, PersonInfoCategory perInfoCtg) {
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		PersonInfoItemDefinition firstItem = lstItemDef.get(0);
		if (perInfoCtg.isEmployeeType()) {
			List<GridLayoutPersonInfoClsDto> employees = getInfoItemEmployeeTypeCps003(query,
					firstItem.getPerInfoItemDefId(), selfEmployeeId, perItems, perInfoCtg);
			if (!employees.isEmpty()) {
				result.addAll(employees);
			}
		} else {
			List<GridLayoutPersonInfoClsDto> persons = getInfoItemPersonTypeCps003(query,
					firstItem.getPerInfoItemDefId(), selfEmployeeId, perItems, perInfoCtg);
			if (!persons.isEmpty()) {
				result.addAll(persons);
			}
		}

		return result;
	}

	/**
	 * optional cps003
	 * 
	 * @param query
	 * @param itemId
	 * @param selfEmployeeId
	 * @param perItems
	 * @param perInfoCtg
	 * @return
	 */
	private List<GridLayoutPersonInfoClsDto> getInfoItemEmployeeTypeCps003(PeregQueryByListEmp query, String itemId,
			String selfEmployeeId, HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems,
			PersonInfoCategory perInfoCtg) {
		List<String> sids = query.getEmpInfos().stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		Map<String, List<EmpInfoCtgData>> empInfoCtgDatas = empInCtgDataRepo
				.getBySidsAndCtgId(sids, query.getCategoryId()).stream()
				.collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		if (empInfoCtgDatas.size() < sids.size()) {
			sids.stream().forEach(c -> {
				List<EmpInfoCtgData> empInfoCtgData = empInfoCtgDatas.get(c);
				if (CollectionUtil.isEmpty(empInfoCtgData)) {
					empInfoCtgDatas.put(c, new ArrayList<>());
				}
			});

		}
		empInfoCtgDatas.entrySet().forEach(c -> {
			if (CollectionUtil.isEmpty(c.getValue())) {
				Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
						.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
				if (empInfoQueryOpt.isPresent()) {
					GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
							empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
							empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
									? creatClassItemList(perItems.get(true), perInfoCtg)
									: creatClassItemList(perItems.get(false), perInfoCtg));
					MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
					// get Combo-Box List theo ngày trên màn hình
					layoutControlComboBox.getComboBoxListForSelectionItems(empInfoQueryOpt.get().getEmployeeId(),
							perInfoCtg, dto.getLayoutDtos(), query.getStandardDate());
					result.add(dto);
				}
			} else {
				List<String> recordIds = c.getValue().stream().map(ctg -> ctg.getRecordId()).distinct()
						.collect(Collectors.toList());
				Map<String, List<EmpInfoItemData>> itemDatas = empInfoItemDataRepository
						.getAllInfoItemByRecordId(recordIds).stream()
						.collect(Collectors.groupingBy(i -> i.getRecordId()));
				Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
						.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
				if (empInfoQueryOpt.isPresent() && !itemDatas.isEmpty()) {
					itemDatas.entrySet().forEach(item -> {
						GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
								empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
								empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
										? creatClassItemList(perItems.get(true), perInfoCtg)
										: creatClassItemList(perItems.get(false), perInfoCtg));
						MappingFactory.matchOptionalItemData(item.getKey(), dto.getLayoutDtos(),
								item.getValue().stream().map(i -> i.genToPeregDto()).collect(Collectors.toList()));
						// get Combo-Box List theo ngày trên màn hình
						layoutControlComboBox.getComboBoxListForSelectionItems(empInfoQueryOpt.get().getEmployeeId(),
								perInfoCtg, dto.getLayoutDtos(), query.getStandardDate());
						result.add(dto);
					});
				}
			}

		});

		query.getEmpInfos().forEach(c -> {
			List<GridLayoutPersonInfoClsDto> empLstOpt = result.stream()
					.filter(i -> i.getEmployeeId().equals(c.getEmployeeId())).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(empLstOpt)) {
				result.add(new GridLayoutPersonInfoClsDto(c.getEmployeeId(), c.getPersonId(), new ArrayList<>()));
			}
		});

		return result;
	}

	/**
	 * optional cps003
	 * 
	 * @param query
	 * @param itemId
	 * @param selfEmployeeId
	 * @param perItems
	 * @param perInfoCtg
	 * @return
	 */
	private List<GridLayoutPersonInfoClsDto> getInfoItemPersonTypeCps003(PeregQueryByListEmp query, String itemId,
			String selfEmployeeId, HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems,
			PersonInfoCategory perInfoCtg) {
		List<String> pids = query.getEmpInfos().stream().map(c -> c.getPersonId()).collect(Collectors.toList());
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		Map<String, List<PerInfoCtgData>> perInfoCtgDatas = perInCtgDataRepo
				.getAllByPidsAndCtgId(pids, query.getCategoryId()).stream()
				.collect(Collectors.groupingBy(c -> c.getPersonId()));
		if (perInfoCtgDatas.size() < pids.size()) {
			pids.stream().forEach(c -> {
				List<PerInfoCtgData> perInfoCtgData = perInfoCtgDatas.get(c);
				if (CollectionUtil.isEmpty(perInfoCtgData)) {
					perInfoCtgDatas.put(c, new ArrayList<>());
				}
			});
		}
		perInfoCtgDatas.entrySet().forEach(c -> {
			if (CollectionUtil.isEmpty(c.getValue())) {
				Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
						.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
				if (empInfoQueryOpt.isPresent()) {
					GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
							empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
							empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
									? creatClassItemList(perItems.get(true), perInfoCtg)
									: creatClassItemList(perItems.get(false), perInfoCtg));
					MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
					// get Combo-Box List theo ngày trên màn hình
					layoutControlComboBox.getComboBoxListForSelectionItems(empInfoQueryOpt.get().getEmployeeId(),
							perInfoCtg, dto.getLayoutDtos(), query.getStandardDate());
					result.add(dto);
				}
			} else {
				List<String> recordIds = c.getValue().stream().map(i -> i.getRecordId()).collect(Collectors.toList());
				Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
						.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
				Map<String, List<PersonInfoItemData>> itemDatas = perInfoItemDataRepository
						.getAllInfoItemByRecordId(recordIds).stream()
						.collect(Collectors.groupingBy(i -> i.getRecordId()));
				if (empInfoQueryOpt.isPresent() && !itemDatas.isEmpty()) {
					itemDatas.entrySet().forEach(item -> {
						GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
								empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
								empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
										? creatClassItemList(perItems.get(true), perInfoCtg)
										: creatClassItemList(perItems.get(false), perInfoCtg));
						MappingFactory.matchOptionalItemData(item.getKey(), dto.getLayoutDtos(),
								item.getValue().stream().map(i -> i.genToPeregDto()).collect(Collectors.toList()));
						// get Combo-Box List theo ngày trên màn hình
						layoutControlComboBox.getComboBoxListForSelectionItems(empInfoQueryOpt.get().getEmployeeId(),
								perInfoCtg, dto.getLayoutDtos(), query.getStandardDate());
						result.add(dto);
					});
				}
			}
		});
		query.getEmpInfos().forEach(c -> {
			List<GridLayoutPersonInfoClsDto> empLstOpt = result.stream()
					.filter(i -> i.getEmployeeId().equals(c.getEmployeeId())).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(empLstOpt)) {
				result.add(new GridLayoutPersonInfoClsDto(c.getEmployeeId(), c.getPersonId(), new ArrayList<>()));
			}
		});
		return result;
	}

	/**
	 * getListOfHistoryCategoryCps003
	 * 
	 * @param query
	 * @param perInfoCtg
	 * @param lstItemDef
	 * @return
	 */
	private List<GridLayoutPersonInfoClsDto> getListOfHistoryCategoryCps003(PeregQueryByListEmp query,
			PersonInfoCategory perInfoCtg, List<PersonInfoItemDefinition> lstItemDef, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems) {

		String categoryId = query.getCategoryId();

		PersonInfoItemDefinition period = getPeriodItem(query.getCategoryCode(), categoryId, lstItemDef);

		List<GridLayoutPersonInfoClsDto> result = getInfoListHistTypeCps003(perInfoCtg, query, period, selfEmployeeId,
				perItems);

		return result;
	}

	private List<GridLayoutPersonInfoClsDto> getInfoListHistTypeCps003(PersonInfoCategory perInfoCtg,
			PeregQueryByListEmp query, PersonInfoItemDefinition period, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems) {

		List<String> timePerInfoItemDefIds = ((SetItem) period.getItemTypeState()).getItems();

		return perInfoCtg.getPersonEmployeeType() == PersonEmployeeType.EMPLOYEE
				? getHistInfoEmployeeType(timePerInfoItemDefIds, query, selfEmployeeId, perItems, perInfoCtg)
				: getHistInfoPersonType(timePerInfoItemDefIds, query, selfEmployeeId, perItems, perInfoCtg);

	}

	/**
	 * optional cps003
	 * 
	 * @param timePerInfoItemDefIds
	 * @param query
	 * @param selfEmployeeId
	 * @param perItems
	 * @param perInfoCtg
	 * @return
	 */
	private List<GridLayoutPersonInfoClsDto> getHistInfoEmployeeType(List<String> timePerInfoItemDefIds,
			PeregQueryByListEmp query, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems, PersonInfoCategory perInfoCtg) {
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();
		// get EmpInfoCtgData to get record id
		List<String> sids = query.getEmpInfos().stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		// employee option data
		Map<String, List<EmpInfoCtgData>> empInfoCtgDatas = empInCtgDataRepo
				.getBySidsAndCtgId(sids, query.getCategoryId()).stream()
				.collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		if (empInfoCtgDatas.size() < sids.size()) {
			sids.stream().forEach(c -> {
				List<EmpInfoCtgData> empInfoCtgData = empInfoCtgDatas.get(c);
				if (CollectionUtil.isEmpty(empInfoCtgData)) {
					empInfoCtgDatas.put(c, new ArrayList<>());
				}
			});
		}
		// lấy recordId
		if (empInfoCtgDatas.size() == 0) {

		}
		// muc dích lấy ra DatePeroid
		empInfoCtgDatas.entrySet().forEach(c -> {
			if (CollectionUtil.isEmpty(c.getValue())) {
				Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
						.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
				if (empInfoQueryOpt.isPresent()) {
					GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
							empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
							empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
									? creatClassItemList(perItems.get(true), perInfoCtg)
									: creatClassItemList(perItems.get(false), perInfoCtg));

					MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
					// get Combo-Box List theo ngày trên màn hình
					layoutControlComboBox.getComboBoxListForSelectionItems(empInfoQueryOpt.get().getEmployeeId(),
							perInfoCtg, dto.getLayoutDtos(), query.getStandardDate());
					result.add(dto);
				}

			} else {
				for (EmpInfoCtgData empInfoCtgData : c.getValue()) {
					// get option text
					List<GeneralDate> optionText = new ArrayList<>();
					List<EmpInfoItemData> lstEmpInfoCtgItemData = empInfoItemDataRepository
							.getAllInfoItemByRecordId(empInfoCtgData.getRecordId());
					if (lstEmpInfoCtgItemData.size() != 0) {
						for (EmpInfoItemData itemData : lstEmpInfoCtgItemData) {
							if (timePerInfoItemDefIds.contains(itemData.getPerInfoDefId())) {
								optionText.add(itemData.getDataState().getDateValue() == null ? GeneralDate.max()
										: itemData.getDataState().getDateValue());
							}
						}
						sortDate(optionText);
						if (optionText.size() > 0) {
							DatePeriod datePeriod = new DatePeriod((GeneralDate) optionText.get(0),
									(GeneralDate) optionText.get(1));
							if (datePeriod.start().beforeOrEquals(query.getStandardDate()) == true
									&& datePeriod.end().afterOrEquals(query.getStandardDate()) == true) {
								List<OptionalItemDataDto> optionalItemDataDto = lstEmpInfoCtgItemData.stream()
										.map(i -> i.genToPeregDto()).collect(Collectors.toList());
								Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
										.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
								if (!CollectionUtil.isEmpty(optionalItemDataDto) && empInfoQueryOpt.isPresent()) {
									GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
											empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
											empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
													? creatClassItemList(perItems.get(true), perInfoCtg)
													: creatClassItemList(perItems.get(false), perInfoCtg));
									MappingFactory.matchOptionalItemData(empInfoCtgData.getRecordId(),
											dto.getLayoutDtos(), optionalItemDataDto);
									// get Combo-Box List theo ngày trên màn
									// hình
									layoutControlComboBox.getComboBoxListForSelectionItems(
											empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
											datePeriod.start());
									result.add(dto);
								}
							} else {
								Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
										.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
								if (empInfoQueryOpt.isPresent()) {
									GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
											empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
											empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
													? creatClassItemList(perItems.get(true), perInfoCtg)
													: creatClassItemList(perItems.get(false), perInfoCtg));

									MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
									// get Combo-Box List theo ngày trên màn
									// hình
									layoutControlComboBox.getComboBoxListForSelectionItems(
											empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
											query.getStandardDate());
									result.add(dto);
								}
							}
						} else {
							Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
									.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
							if (empInfoQueryOpt.isPresent()) {
								GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
										empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
										empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
												? creatClassItemList(perItems.get(true), perInfoCtg)
												: creatClassItemList(perItems.get(false), perInfoCtg));

								MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
								// get Combo-Box List theo ngày trên màn hình
								layoutControlComboBox.getComboBoxListForSelectionItems(
										empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
										query.getStandardDate());
								result.add(dto);
							}
						}

					} else {
						Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
								.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
						if (empInfoQueryOpt.isPresent()) {
							GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
									empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
									empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
											? creatClassItemList(perItems.get(true), perInfoCtg)
											: creatClassItemList(perItems.get(false), perInfoCtg));

							MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
							// get Combo-Box List theo ngày trên màn hình
							layoutControlComboBox.getComboBoxListForSelectionItems(
									empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
									query.getStandardDate());
							result.add(dto);
						}
					}

				}
			}

		});

		query.getEmpInfos().forEach(c -> {
			List<GridLayoutPersonInfoClsDto> empLstOpt = result.stream()
					.filter(i -> i.getEmployeeId().equals(c.getEmployeeId())).collect(Collectors.toList());
			if (CollectionUtil.isEmpty(empLstOpt)) {
				result.add(new GridLayoutPersonInfoClsDto(c.getEmployeeId(), c.getPersonId(), new ArrayList<>()));
			}
		});
		return result;
	}

	/**
	 * optional cps003
	 * 
	 * @param timePerInfoItemDefIds
	 * @param query
	 * @param selfEmployeeId
	 * @param perItems
	 * @param perInfoCtg
	 * @return
	 */
	private List<GridLayoutPersonInfoClsDto> getHistInfoPersonType(List<String> timePerInfoItemDefIds,
			PeregQueryByListEmp query, String selfEmployeeId,
			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems, PersonInfoCategory perInfoCtg) {
		List<GridLayoutPersonInfoClsDto> result = new ArrayList<>();

		List<String> pids = query.getEmpInfos().stream().map(c -> c.getPersonId()).collect(Collectors.toList());
		// get EmpInfoCtgData to get record id
		Map<String, List<PerInfoCtgData>> lstPerInfoCtgData = perInCtgDataRepo
				.getAllByPidsAndCtgId(pids, query.getCategoryId()).stream()
				.collect(Collectors.groupingBy(c -> c.getPersonId()));
		if (lstPerInfoCtgData.size() < pids.size()) {
			pids.stream().forEach(c -> {
				List<PerInfoCtgData> perInfoCtgData = lstPerInfoCtgData.get(c);
				if (CollectionUtil.isEmpty(perInfoCtgData)) {
					lstPerInfoCtgData.put(c, new ArrayList<>());
				}
			});

		}
		// get lst item data and filter base on item def
		List<PersonInfoItemData> lstValidItemData = new ArrayList<>();
		lstPerInfoCtgData.entrySet().forEach(c -> {
			if (CollectionUtil.isEmpty(c.getValue())) {
				Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
						.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
				if (empInfoQueryOpt.isPresent()) {
					GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
							empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
							empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
									? creatClassItemList(perItems.get(true), perInfoCtg)
									: creatClassItemList(perItems.get(false), perInfoCtg));
					MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
					// get Combo-Box List theo ngày trên màn hình
					layoutControlComboBox.getComboBoxListForSelectionItems(empInfoQueryOpt.get().getEmployeeId(),
							perInfoCtg, dto.getLayoutDtos(), query.getStandardDate());
					result.add(dto);
				}
			} else {
				for (PerInfoCtgData perInfoCtgData : c.getValue()) {
					// get option value value combo box
					// get option text
					List<GeneralDate> optionText = new ArrayList<>();
					List<PersonInfoItemData> lstPerInfoCtgItemData = perInfoItemDataRepository
							.getAllInfoItemByRecordId(perInfoCtgData.getRecordId());
					if (lstPerInfoCtgItemData.size() != 0) {
						for (PersonInfoItemData itemData : lstValidItemData) {
							if (timePerInfoItemDefIds.contains(itemData.getPerInfoItemDefId())) {
								optionText.add(itemData.getDataState().getDateValue() == null ? GeneralDate.max()
										: itemData.getDataState().getDateValue());
							}
						}
						sortDate(optionText);
						if (optionText.size() > 0) {
							DatePeriod datePeriod = new DatePeriod((GeneralDate) optionText.get(0),
									(GeneralDate) optionText.get(1));
							if (datePeriod.start().beforeOrEquals(query.getStandardDate()) == true
									&& datePeriod.end().afterOrEquals(query.getStandardDate()) == true) {
								List<OptionalItemDataDto> optionalItemDataDto = lstPerInfoCtgItemData.stream()
										.map(i -> i.genToPeregDto()).collect(Collectors.toList());
								Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
										.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
								if (CollectionUtil.isEmpty(optionalItemDataDto) && empInfoQueryOpt.isPresent()) {
									GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
											empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
											empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
													? creatClassItemList(perItems.get(true), perInfoCtg)
													: creatClassItemList(perItems.get(false), perInfoCtg));
									MappingFactory.matchOptionalItemData(perInfoCtgData.getRecordId(),
											dto.getLayoutDtos(), optionalItemDataDto);
									// get Combo-Box List theo ngày trên màn
									// hình
									layoutControlComboBox.getComboBoxListForSelectionItems(
											empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
											query.getStandardDate());
									result.add(dto);
								}
							} else {
								Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
										.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
								if (empInfoQueryOpt.isPresent()) {
									GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
											empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
											empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
													? creatClassItemList(perItems.get(true), perInfoCtg)
													: creatClassItemList(perItems.get(false), perInfoCtg));
									MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
									// get Combo-Box List theo ngày trên màn
									// hình
									layoutControlComboBox.getComboBoxListForSelectionItems(
											empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
											query.getStandardDate());
									result.add(dto);
								}
							}
						} else {
							Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
									.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
							if (empInfoQueryOpt.isPresent()) {
								GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
										empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
										empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
												? creatClassItemList(perItems.get(true), perInfoCtg)
												: creatClassItemList(perItems.get(false), perInfoCtg));
								MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
								// get Combo-Box List theo ngày trên màn hình
								layoutControlComboBox.getComboBoxListForSelectionItems(
										empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
										query.getStandardDate());
								result.add(dto);
							}
						}

					} else {
						Optional<PeregEmpInfoQuery> empInfoQueryOpt = query.getEmpInfos().stream()
								.filter(emp -> emp.getEmployeeId().equals(c.getKey())).findFirst();
						if (empInfoQueryOpt.isPresent()) {
							GridLayoutPersonInfoClsDto dto = new GridLayoutPersonInfoClsDto(
									empInfoQueryOpt.get().getEmployeeId(), empInfoQueryOpt.get().getPersonId(),
									empInfoQueryOpt.get().getEmployeeId().equals(selfEmployeeId)
											? creatClassItemList(perItems.get(true), perInfoCtg)
											: creatClassItemList(perItems.get(false), perInfoCtg));
							MappingFactory.matchOptionalItemData(null, dto.getLayoutDtos(), new ArrayList<>());
							// get Combo-Box List theo ngày trên màn hình
							layoutControlComboBox.getComboBoxListForSelectionItems(
									empInfoQueryOpt.get().getEmployeeId(), perInfoCtg, dto.getLayoutDtos(),
									query.getStandardDate());
							result.add(dto);
						}
					}
				}
			}

		});

		return result;
	}

	/**
	 * optional cps003
	 * 
	 * @param categoryCode
	 * @param categoryId
	 * @param lstItemDef
	 * @return
	 */
	private PersonInfoItemDefinition getPeriodItem(String categoryCode, String categoryId,
			List<PersonInfoItemDefinition> lstItemDef) {
		if (categoryCode.equals("CS00003")) {
			return lstItemDef.get(0);
		}

		DateRangeItem dateRangeItem = perInfoCtgRepositoty.getDateRangeItemByCategoryId(categoryId).get();
		return lstItemDef.stream().filter(x -> x.getPerInfoItemDefId().equals(dateRangeItem.getDateRangeItemId()))
				.findFirst().get();
	}

	/**
	 * optional cps003 sort asc
	 * 
	 * @param optionText
	 */
	private void sortDate(List<GeneralDate> optionText) {
		optionText.sort((a, b) -> {
			GeneralDate start = a;
			GeneralDate end = b;
			return start.compareTo(end);
		});
	}

}
