package nts.uk.ctx.exio.dom.exo.condset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.exio.dom.exo.category.ExOutCtg;
import nts.uk.ctx.exio.dom.exo.category.ExOutCtgRepository;
import nts.uk.ctx.exio.dom.exo.commonalgorithm.AcquisitionExOutSetting;
import nts.uk.ctx.exio.dom.exo.commonalgorithm.AcquisitionExternalOutputCategory;
import nts.uk.ctx.exio.dom.exo.commonalgorithm.RegisterMode;
import nts.uk.ctx.exio.dom.exo.commonalgorithm.RegistrationCondDetails;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.AwDataFormatSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.CharacterDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.DateFormatSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.InstantTimeDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.NumberDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.TimeDataFmSetting;
import nts.uk.ctx.exio.dom.exo.outcnddetail.ConditionSettingCd;
import nts.uk.ctx.exio.dom.exo.outcnddetail.OutCndDetail;
import nts.uk.ctx.exio.dom.exo.outcnddetail.OutCndDetailItem;
import nts.uk.ctx.exio.dom.exo.outcnddetail.OutCndDetailRepository;
import nts.uk.ctx.exio.dom.exo.outcnddetail.SearchCodeList;
import nts.uk.ctx.exio.dom.exo.outputitem.CategoryItem;
import nts.uk.ctx.exio.dom.exo.outputitem.ConditionSettingCode;
import nts.uk.ctx.exio.dom.exo.outputitem.StandardOutputItem;
import nts.uk.ctx.exio.dom.exo.outputitem.StandardOutputItemRepository;
import nts.uk.ctx.exio.dom.exo.outputitemorder.StandardOutputItemOrder;
import nts.uk.ctx.exio.dom.exo.outputitemorder.StandardOutputItemOrderRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class StdOutputCondSetService {

	@Inject
	private StdOutputCondSetRepository stdOutputCondSetRepository;

	@Inject
	private StandardOutputItemRepository stdOutputItemRepository;

	@Inject
	private StandardOutputItemOrderRepository standardOutputItemOrderRepository;

	@Inject
	private OutCndDetailRepository outCndDetailRepository;

	@Inject
	private AcquisitionExOutSetting mAcquisitionExOutSetting;

	@Inject
	private RegistrationCondDetails registrationCondDetails;

	@Inject
	private ExOutCtgRepository mExOutCtgRepository;
	
	@Inject
	private AcquisitionExternalOutputCategory acquisitionExternalOutputCategory;

	@Inject
	private OutputPeriodSettingRepository outputPeriodSettingRepository;
	
	// Screen T
	public Map<String, String> excuteCopy(String copyDestinationCode, String destinationName, String conditionSetCd,
			boolean overwite) {
		Map<String, String> resultExvuteCopy = new HashMap<>();
		String cid = AppContexts.user().companyId();
		Optional<StdOutputCondSet> stdOutputCondSet = stdOutputCondSetRepository.getStdOutputCondSetById(cid,
				copyDestinationCode);
		if (stdOutputCondSet.isPresent()) {
			if (overwite) {
				resultExvuteCopy.put("result", "OK");
				resultExvuteCopy.put("overwrite", "TO");
			} else {
				throw new BusinessException("Msg_3");
			}
		} else {
			resultExvuteCopy.put("result", "OK");
			resultExvuteCopy.put("overwrite", "DONOT");
		}
		resultExvuteCopy.put("copyDestinationCode", copyDestinationCode);
		resultExvuteCopy.put("destinatioName", destinationName);
		return resultExvuteCopy;
	}
	// ******

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param
	 */
	private void registerOutputItemSortOrder(List<StandardOutputItemOrder> listStandardOutputItemOrder, String cndSetCd,
			RegisterMode mode, int standType) {
		StandardOutputItemOrder standardOutputItemOrder;
		String cid = AppContexts.user().companyId();
		if (standType == StandardAtr.STANDARD.value) {
		    for (int i = 0; i < listStandardOutputItemOrder.size(); i++) {
				standardOutputItemOrder = new StandardOutputItemOrder(cid,
						listStandardOutputItemOrder.get(i).getOutputItemCode().v(), cndSetCd, i+1);
				standardOutputItemOrderRepository.add(standardOutputItemOrder);
			}
		}
	}

	public void registerOutputSet(RegisterMode mode, int standType, StdOutputCondSet stdOutputCondSet,
			List<StandardOutputItemOrder> listStandardOutputItemOrder) {
		if (outputSetRegisConfir(mode, standType, stdOutputCondSet.getCompanyId(), stdOutputCondSet.getAutoExecution().value,
				stdOutputCondSet.getConditionSetCode().v())) {
			updateOutputCndSet(stdOutputCondSet, standType, mode);
		}
		if (listStandardOutputItemOrder != null && !listStandardOutputItemOrder.isEmpty()) {
			registerOutputItemSortOrder(listStandardOutputItemOrder, stdOutputCondSet.getConditionSetCode().v(), mode,
					standType);
		}
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param cid
	 * @param condSetCd
	 */
	public void remove(String cid, String condSetCd) {
		stdOutputItemRepository.remove(cid, condSetCd);
		standardOutputItemOrderRepository.remove(cid, condSetCd);
		outCndDetailRepository.remove(cid, condSetCd);
		stdOutputCondSetRepository.remove(cid, condSetCd);
	}

	// ??????????????????????????????
	private boolean outputSetRegisConfir(RegisterMode mode, int standType, String cId, int autoExecution,
			String cndSetCd) {
		if (mode == RegisterMode.NEW && standType == StandardAtr.STANDARD.value && checkExist(cId, cndSetCd)) {
			throw new BusinessException("Msg_3");
		}
		if (autoExecution == NotUseAtr.NOT_USE.value) {
			throw new BusinessException("Msg_677");
		}
		return true;
	}

	private boolean checkExist(String cid, String cndSetCd) {
		Optional<StdOutputCondSet> stdOutputCondSet = stdOutputCondSetRepository.getStdOutputCondSetById(cid, cndSetCd);
		return stdOutputCondSet.isPresent();
	}

	// ??????????????????????????????
	private void updateOutputCndSet(StdOutputCondSet stdOutputCondSet, int standType, RegisterMode mode) {
		if (standType == StandardAtr.STANDARD.value) {
		    if (mode == RegisterMode.NEW) {
		        stdOutputCondSetRepository.add(stdOutputCondSet);
		    } else {
		        stdOutputCondSetRepository.update(stdOutputCondSet); 
		    }
		}
	}

	// ??????????????????????????????
	private void outputSettingCopy(String cndSetCode, int standType, StdOutputCondSet copyParams) {

		String cId = copyParams.getCompanyId();
		Optional<OutCndDetail> outCndDetail = Optional.empty();

		// ??????????????????????????????_??????
		List<StandardOutputItem> listStdOutputItem = outputAcquisitionItemList(cId, cndSetCode);

		// ????????????????????????????????????_??????---
		List<StandardOutputItemOrder> listStdOutputItemOrder = outputAcquisitionItemOrderList(cId, cndSetCode);

		// ??????????????????????????????
		outCndDetail = outCndDetailRepository.getOutCndDetailById(cId, cndSetCode);
		outCndDetail.ifPresent(i -> i.setListOutCndDetailItem(outputAcquisitionConditionList(cndSetCode)));
		
		// ????????????????????????????????????????????????????????????
		Optional<OutputPeriodSetting> oOutputPeriodSetting = this.outputPeriodSettingRepository.findById(cId, cndSetCode);

		// ????????????????????????????????????????????????????????????
		changeContent(listStdOutputItem, copyParams.getConditionSetCode().v(), outCndDetail, listStdOutputItemOrder);

		// ????????????????????????????????????
		copyExecutionRegistration(copyParams, standType, listStdOutputItem, listStdOutputItemOrder, outCndDetail, oOutputPeriodSetting,
				cndSetCode);

	}

	// ????????????????????????????????????_??????
	private List<StandardOutputItemOrder> outputAcquisitionItemOrderList(String cId, String cndSetCode) {
		return standardOutputItemOrderRepository.getStandardOutputItemOrderByCidAndSetCd(cId, cndSetCode);
	}

	// ????????????????????????
	public void copy(int standType, String cndSetCode, StdOutputCondSet copyParams) {
		if (standType == StandardAtr.STANDARD.value) {
			outputSettingCopy(cndSetCode, standType, copyParams);
		}
	}

	// ??????????????????????????????_??????
	private List<StandardOutputItem> outputAcquisitionItemList(String cId, String cndSetCode) {
		return stdOutputItemRepository.getStdOutItemByCidAndSetCd(cId, cndSetCode);
	}

	// ??????????????????????????????
	private List<OutCndDetailItem> outputAcquisitionConditionList(String conditionSettingCd) {
		Optional<OutCndDetail> cndDetailOtp = mAcquisitionExOutSetting.getExOutCond(conditionSettingCd, null,
				StandardAtr.STANDARD, false, null);
		if (cndDetailOtp.isPresent()) {
			return cndDetailOtp.get().getListOutCndDetailItem();
		}
		return Collections.emptyList();
	}

	// ????????????????????????????????????
	private void copyExecutionRegistration(StdOutputCondSet copyParams, int standType,
			List<StandardOutputItem> listStdOutputItem, List<StandardOutputItemOrder> listStdOutputItemOrder,
			Optional<OutCndDetail> outCndDetail, Optional<OutputPeriodSetting> oOutputPeriodSetting, String cndSetCode) {
		RegisterMode mode = RegisterMode.NEW;
		String newCndSetCode = copyParams.getConditionSetCode().v();
		if (checkExist(copyParams.getCompanyId(), newCndSetCode)) {
			this.remove(copyParams.getCompanyId(), newCndSetCode);
		}
		// ??????????????????????????????
		updateOutputCndSet(copyParams, standType, mode);

		// ??????????????????????????????
		registrationOutputItem(listStdOutputItem, listStdOutputItemOrder, mode, cndSetCode, newCndSetCode);

		// ??????????????????????????????
		registrationCondDetails.algorithm(outCndDetail, StandardAtr.STANDARD, mode);
		
		// ????????????????????????????????????????????????????????????
		if (oOutputPeriodSetting.isPresent()) {
			String cid = AppContexts.user().companyId();
			this.copyOutputPeriodSetting(mode, cid, newCndSetCode, oOutputPeriodSetting.get());
		}
	}

	// ????????????????????????????????????????????????????????????
	private void changeContent(List<StandardOutputItem> listStdOutputItem, String cndSetCode,
			Optional<OutCndDetail> outCndDetail, List<StandardOutputItemOrder> listStdOutputItemOrder) {
		if (outCndDetail.isPresent()) {
			outCndDetail.get().setConditionSettingCd(new ConditionSettingCd(cndSetCode));
			for (OutCndDetailItem outCndDetailItem : outCndDetail.get().getListOutCndDetailItem()) {
				outCndDetailItem.setConditionSettingCd(new ConditionSettingCd(cndSetCode));
				for (SearchCodeList searchCodeList : outCndDetailItem.getListSearchCodeList()) {
					searchCodeList.setConditionSetCode(new ExternalOutputConditionCode(cndSetCode));
				}
			}
		}
		for (StandardOutputItem standardOutputItem : listStdOutputItem) {
			standardOutputItem.setConditionSettingCode(new ConditionSettingCode(cndSetCode));
			for (CategoryItem categoryItem : standardOutputItem.getCategoryItems()) {
				categoryItem.setConditionSettingCode(new ConditionSettingCode(cndSetCode));
			}
		}
		for (StandardOutputItemOrder stdOutputItemOrder : listStdOutputItemOrder) {
			stdOutputItemOrder.setConditionSettingCode(new ConditionSettingCode(cndSetCode));
		}

	}

	// ??????????????????????????????_??????
	private void registrationOutputItem(List<StandardOutputItem> listStdOutputItem,
			List<StandardOutputItemOrder> listStdOutputItemOrder, RegisterMode mode, String cndSetCode,
			String newCndSetCode) {
		String cid = AppContexts.user().companyId();
		if (listStdOutputItem != null && !listStdOutputItem.isEmpty()) {
			stdOutputItemRepository.add(listStdOutputItem);
		}
		if (listStdOutputItemOrder != null && !listStdOutputItemOrder.isEmpty())
			standardOutputItemOrderRepository.add(listStdOutputItemOrder);

		List<AwDataFormatSetting> listAwData = stdOutputItemRepository.getAwDataFormatSetting(cid, cndSetCode);
		listAwData.forEach(item -> {
			item.setConditionSettingCode(new ConditionSettingCode(newCndSetCode));
			stdOutputItemRepository.register(item);
		});
		List<DateFormatSetting> listDate = stdOutputItemRepository.getDateFormatSetting(cid, cndSetCode);
		listDate.forEach(item -> {
			item.setConditionSettingCode(new ConditionSettingCode(newCndSetCode));
			stdOutputItemRepository.register(item);
		});
		List<InstantTimeDataFmSetting> listInstantTime = stdOutputItemRepository.getInstantTimeDataFmSetting(cid,
				cndSetCode);
		listInstantTime.forEach(item -> {
			item.setConditionSettingCode(new ConditionSettingCode(newCndSetCode));
			stdOutputItemRepository.register(item);
		});
		List<NumberDataFmSetting> listNumber = stdOutputItemRepository.getNumberDataFmSetting(cid, cndSetCode);
		listNumber.forEach(item -> {
			item.setConditionSettingCode(new ConditionSettingCode(newCndSetCode));
			stdOutputItemRepository.register(item);
		});
		List<CharacterDataFmSetting> listCharacter = stdOutputItemRepository.getCharacterDataFmSetting(cid, cndSetCode);
		listCharacter.forEach(item -> {
			item.setConditionSettingCode(new ConditionSettingCode(newCndSetCode));
			stdOutputItemRepository.register(item);
		});
		List<TimeDataFmSetting> listTime = stdOutputItemRepository.getTimeDataFmSetting(cid, cndSetCode);
		listTime.forEach(item -> {
			item.setConditionSettingCode(new ConditionSettingCode(newCndSetCode));
			stdOutputItemRepository.register(item);
		});

	}
	
	// ????????????????????????????????????????????????????????????
	private void copyOutputPeriodSetting(RegisterMode mode, String cId, String cndSetCode, OutputPeriodSetting sourceDomain) {
		OutputPeriodSetting targetDomain = OutputPeriodSetting.cloneFromDomain(cId, cndSetCode, sourceDomain);
		if (mode.equals(RegisterMode.NEW)) {
			this.outputPeriodSettingRepository.add(targetDomain);
		} else if (mode.equals(RegisterMode.UPDATE)) {
			this.outputPeriodSettingRepository.update(cId, cndSetCode, targetDomain);
		}
	}

	// ????????????
	//??????????????????????????????????????????
	public List<StdOutputCondSet> getListStandardOutputItem(String cndSetCd,String roleId) {
		List<String> listRoleId = new ArrayList<>();
		listRoleId.add(roleId);
		List<StdOutputCondSet> data = mAcquisitionExOutSetting.getExOutSetting("", StandardAtr.STANDARD, cndSetCd);
		List<ExOutCtg> listExOutCtg =acquisitionExternalOutputCategory.getExternalOutputCategoryList(listRoleId);
		List<StdOutputCondSet> arrTemp = new ArrayList<StdOutputCondSet>();
		String userID = AppContexts.user().userId();
		List<StandardOutputItem> listStandarOutItem = new ArrayList<StandardOutputItem>();
		if (data.isEmpty()) { 
			throw new BusinessException("Msg_754");
		}
		for (StdOutputCondSet temp : data) {
			boolean checkId =  false;
			for(ExOutCtg exOutCtg :listExOutCtg) {
				if(temp.getCategoryId().equals(exOutCtg.getCategoryId())) {
					checkId = true;
					break;
				}
			}
			if(checkId) {
				listStandarOutItem = mAcquisitionExOutSetting.getExOutItemList(temp.getConditionSetCode().toString(),
						userID, "", StandardAtr.STANDARD, true);
				if (!listStandarOutItem.isEmpty()) {
					arrTemp.add(temp);
				}
			}
		}
		if (arrTemp.isEmpty()) {
			throw new BusinessException("Msg_754");
		}


		return arrTemp;
	}

	// ??????????????????????????????
	public List<StandardOutputItem> outputAcquisitionItemList(String condSetCd, String userId, String outItemCd,
			StandardAtr standardType, boolean isAcquisitionMode) {
		return mAcquisitionExOutSetting.getExOutItemList(condSetCd, userId, outItemCd, standardType, isAcquisitionMode);
	}

	// ????????????????????????
	public Optional<ExOutCtg> getExOutCtg(int catelogyId) {
		return mExOutCtgRepository.getExOutCtgByCtgId(catelogyId);
	}

}
