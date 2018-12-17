package nts.uk.ctx.at.schedule.app.export.horitotalcategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.HoriTotalCategory;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.TotalEvalOrder;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.primitives.TotalItemNo;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.repository.HoriTotalCategoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;

/**
 * 
 * @author Hoidd
 *
 */
@Stateless
@DomainID(value = "Schedule")
public class HoriTotalCategoryExportImpl implements MasterListData {
	
	@Inject
	private HoriTotalCategoryExcelRepo horiTotalCategoryExcelRepo;
	
	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn("コード", TextResource.localize("KML004_9"),
				ColumnTextAlign.CENTER, "", true));
		columns.add(new MasterHeaderColumn("名称", TextResource.localize("KML004_10"),
				ColumnTextAlign.CENTER, "", true));
		columns.add(new MasterHeaderColumn("メモ", TextResource.localize("KML004_11"),
				ColumnTextAlign.CENTER, "", true));
		columns.add(new MasterHeaderColumn("選択された対象項目", TextResource.localize("KML004_16"),
				ColumnTextAlign.CENTER, "", true));
		columns.add(new MasterHeaderColumn("回数集計集計設定", "回数集計集計設定",
				ColumnTextAlign.CENTER, "", true));
 
		return columns;
	}
	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		
		String companyId = AppContexts.user().companyId();
		List<HoriTotalExel> listHoriTotalExel = horiTotalCategoryExcelRepo.getAll(companyId);
		List<MasterData> datas = new ArrayList<>();
		if (CollectionUtil.isEmpty(listHoriTotalExel)) {
			throw new BusinessException("Msg_393");
		} else {
			listHoriTotalExel.stream().forEach(c -> {
				Map<String, Object> data = new HashMap<>();
				if(c.getCode()!=null){
				data.put("コード", c.getCode());
				}
				if(c.getName()!=null){
				data.put("名称", c.getName());
				}
				if(c.getMemo()!=null){
				data.put("メモ", c.getMemo());
				}
				List<ItemTotalExcel> listItemTotals = c.getListItemTotals();
				if(!CollectionUtil.isEmpty(listItemTotals)){
					Map<String, Object> dataChild = new HashMap<>();
					for(int i = 0; i < listItemTotals.size();i++){
						ItemTotalExcel itemTotalExcel =listItemTotals.get(i);
						if(i == 0 && itemTotalExcel!=null){
							data.put("選択された対象項目", itemTotalExcel.getNameItemTotal());
							data.put("回数集計集計設定",itemTotalExcel.getItemSets().toString());
							datas.add(new MasterData(data, null, "選択された対象項目"));
							
						}
						else if(itemTotalExcel!=null){
							dataChild.put("選択された対象項目", itemTotalExcel.getNameItemTotal());
							dataChild.put("回数集計集計設定",itemTotalExcel.getItemSets().toString());
							datas.add(new MasterData(dataChild, null, ""));
						}
						
					}
				}
			});
		}
		return datas;
	}
	
	@Override
	public String mainSheetName() {
		return "HoiDD";
	}
	
	
	
	
}
