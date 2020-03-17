package nts.uk.ctx.hr.shared.dom.databeforereflecting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.hr.shared.dom.databeforereflecting.DataBeforeReflectingPerInfo;
import nts.uk.ctx.hr.shared.dom.databeforereflecting.DataBeforeReflectingRepository;

@Stateless
public class DataBeforeReflectingPerInfoService {

	@Inject
	private DataBeforeReflectingRepository repo;

	// 個人情報反映前データの取得
	public List<DataBeforeReflectingPerInfo> getDataBeforeReflectPerInfo(String cid, Integer workId, List<String> listPid,
			Optional<Boolean> includReflected, Optional<String> sortByColumnName, Optional<String> orderType) {

		// ドメイン [個人情報反映前データ] を取得する (Get domain "Data before reflecting personal
		// information/data trước khi phản ánh thông tin cá nhân")
		List<DataBeforeReflectingPerInfo> listDataBeforeReflectPerInfo = repo.getData(cid, workId, listPid,
				includReflected, sortByColumnName, orderType);

		return listDataBeforeReflectPerInfo;
	}
	
	// 個人情報反映前データの追加
	public void addDataBeforeReflectingPerInfo (DataBeforeReflectingPerInfo domain) {
		List<DataBeforeReflectingPerInfo> listDomain = new ArrayList<DataBeforeReflectingPerInfo>();
		listDomain.add(domain);
		repo.addData(listDomain);
	}

	public void updateDataBeforeReflectingPerInfo(DataBeforeReflectingPerInfo domain) {
		List<DataBeforeReflectingPerInfo> listDomain = new ArrayList<DataBeforeReflectingPerInfo>();
		listDomain.add(domain);
		repo.updateData(listDomain);
	}

	public void removeDataBeforeReflectingPerInfo(String histId) {
		repo.deleteDataByHistId(histId);
	}
	
	//業務承認の有無チェック
	public List<String> checkForBusinessApp(String cid, String employeeId){
		List<Integer> workIds = repo.getWorkId(cid, employeeId);
		return workIds.stream().map(c-> this.convertToProgramId(c)).collect(Collectors.toList());
	}
	
	//業務IDをプログラムIDへ変換する
	public String convertToProgramId(Integer workId){
		if(workId == 1) {
			return "JCM007";
		}else if(workId == 2) {
			return "JCM008";
		}else if(workId == 3) {
			return "JCM006";
		}else{
			return null;
		}
	}
	
}
