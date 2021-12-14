package nts.uk.ctx.sys.gateway.dom.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.employee.EmployeeInfoDtoImport;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.user.UserImportNew;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.RoleAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.adapter.RoleIndividualGrantAdapter;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.RoleImport;
import nts.uk.ctx.sys.gateway.dom.loginold.dto.RoleIndividualGrantImport;
import nts.uk.ctx.sys.gateway.dom.role.RoleType;
import nts.uk.ctx.sys.gateway.dom.stopbycompany.StopByCompany;
import nts.uk.ctx.sys.gateway.dom.stopbycompany.StopByCompanyRepository;
import nts.uk.ctx.sys.gateway.dom.stopbycompany.StopModeType;
import nts.uk.ctx.sys.gateway.dom.stopbycompany.SystemStatusType;
import nts.uk.ctx.sys.gateway.dom.stopbysystem.StopBySystem;
import nts.uk.ctx.sys.gateway.dom.stopbysystem.StopBySystemRepository;
import nts.uk.ctx.sys.shared.dom.employee.SysEmployeeAdapter;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class CollectCompanyListImpl implements CollectCompanyList {
	
	@Inject
	private RoleIndividualGrantAdapter roleIndGrantAdapter;
	
	@Inject
	private RoleAdapter roleAdapter;
	
	@Inject
	private UserAdapter userAdapter;
	
	@Inject
	private EmployeeInfoAdapter employeeInfoAdapter;
	
	@Inject
	private SysEmployeeAdapter employeeAdapter;
	
	@Inject
	private CompanyBsAdapter companyBsAdapter;
	
	@Inject
	private StopBySystemRepository repoStopSys;
	@Inject
	private StopByCompanyRepository repoStopCom;
	
	@Override
	public List<String> getCompanyList(String userID, String contractCd) {
		// ドメインモデル「ロール個人別付与」を取得する (get List RoleIndividualGrant)
		List<RoleIndividualGrantImport> roles = this.roleIndGrantAdapter.getByUserIDDateRoleType(userID, GeneralDate.today(),
				RoleType.COMPANY_MANAGER.value);

		List<RoleImport> roleImp = new ArrayList<>();

		if (!roles.isEmpty()) {
			// ドメインモデル「ロール」を取得する (Acquire domain model "role"
			roles.stream().map(roleItem -> {
				return roleImp.addAll(this.roleAdapter.getAllById(roleItem.getRoleId()));
			}).collect(Collectors.toList());
		}

		GeneralDate systemDate = GeneralDate.today();

		// ドメインモデル「ユーザ」を取得する get domain "User"
		Optional<UserImportNew> user = this.userAdapter.getByUserIDandDate(userID, systemDate);

		List<EmployeeInfoDtoImport> employees = new ArrayList<>();

		if (!user.get().getAssociatePersonId().get().isEmpty()) {
			employees.addAll(this.employeeInfoAdapter.getEmpInfoByPid(user.get().getAssociatePersonId().get()));

			employees = employees.stream()
				.filter(empItem -> !this.employeeAdapter.getStatusOfEmployee(empItem.getEmployeeId()).isDeleted())
				.collect(Collectors.toList());
		}

		// imported（権限管理）「会社」を取得する (imported (authority management) Acquire
		// "company") Request No.51
		List<CompanyBsImport> companys = this.companyBsAdapter.getAllCompany();

		List<String> companyIdAll = companys.stream().map(item -> {
			return item.getCompanyId();
		}).collect(Collectors.toList());

		List<String> lstCompanyId = new ArrayList<>();

		// merge duplicate companyId from lstRole and lstEm
		if (!roleImp.isEmpty()) {
			List<String> lstComp = new ArrayList<>();
			roleImp.forEach(role -> {
				if (role.getCompanyId() != null) {
					lstComp.add(role.getCompanyId());
				}
			});

			lstCompanyId.addAll(lstComp);
		}

		if (!employees.isEmpty()) {
			List<String> lstComp = new ArrayList<>();
			employees.forEach(emp -> {
				if (emp.getCompanyId() != null) {
					lstComp.add(emp.getCompanyId());
				}
			});

			lstCompanyId.addAll(lstComp);
		}

		lstCompanyId = lstCompanyId.stream().distinct().collect(Collectors.toList());

		// 取得した会社（List）から、会社IDのリストを抽出する (Extract the list of company IDs from
		// the acquired company (List))
		List<String> lstCompanyFinal = lstCompanyId.stream().filter(com -> companyIdAll.contains(com))
				.collect(Collectors.toList());
		//EA修正履歴 No.3031
		List<String> lstResult = this.checkStopUse(contractCd, lstCompanyFinal, userID);
		return lstResult;
	}
//	public List<String> checkStopUse2(String contractCd, List<String> lstCID) {
//		//ドメインモデル「システム全体の利用停止」を取得する (get domain [StopBySystem])
//		Optional<StopBySystem> stopSys = repoStopSys.findByKey(contractCd);
//		//取得できる
//		//ドメインモデル「システム全体の利用停止.システム利用状態」をチェックする (StopBySystem.systemStatus)
//		if(stopSys.isPresent() && stopSys.get().getSystemStatus().equals(SystemStatusType.STOP)){//「利用停止中」の場合
//			//アルゴリズム「権限(ロール)のチェック」を実行する
//			if(!this.checkRoleAuth2(stopSys.get().getStopMode())){//False：ログイン権限なし
//				return new ArrayList<>();
//			}
//		}
//		//ドメインモデル「会社単位の利用停止」を取得する
//		List<StopByCompany> lstCom = repoStopCom.getListComByContractCD(contractCd);
//		if(lstCom.isEmpty()){
//			return lstCID;
//		}
//		List<String> lstComStop = this.getLstComStopUse(lstCom);
//		List<String> result = new ArrayList<>();
//		for(String cID : lstCID){
//			if(!lstComStop.contains(cID)){
//				result.add(cID);
//			}
//		}
//		return result;
//	}
	/**
	 * @author hoatt
	 * 利用停止のチェック ver2
	 */
	//EA修正履歴 No.3032
	@Override
	public List<String> checkStopUse(String contractCd, List<String> lstCID, String userID) {
		//ドメインモデル「システム全体の利用停止」を取得する (get domain [StopBySystem])
//		　input.契約コード- contractCd
//		　システム利用状態＝「利用停止中」
		Optional<StopBySystem> stopSys = repoStopSys.findByCdStatus(contractCd, SystemStatusType.STOP.value);
		//取得できる
		if(stopSys.isPresent()){
			//アルゴリズム「権限(ロール)のチェック」を実行する
			return this.checkRoleAuth(stopSys.get().getStopMode(), lstCID, userID);
		}
		//取得できない
		//ドメインモデル「会社単位の利用停止」を取得する (get domain StopByCompany)
		List<StopByCompany> lstCom = repoStopCom.findByCdStatus(contractCd, SystemStatusType.STOP.value);
		if(lstCom.isEmpty()){//Iutput：会社ID（List） Before filter　を返す
			return lstCID;
		}
		List<String> lstComStop = lstCom.stream().map(c -> c.getContractCd() + "-" + c.getCompanyCd()).collect(Collectors.toList());
		//取得できる
		//Output：会社ID（List）に利用停止以外の会社を追加する
		List<String> result = new ArrayList<>();
//		ログイン者がログイン可能で停止されていない会社を抽出
		for(String cID : lstCID){
			if(!lstComStop.contains(cID)){
				result.add(cID);
			}
		}
		//取得したドメインモデル「会社単位の利用停止」から会社IDのリストを作成する
//		条件：
//		　・[会社単位の利用停止．利用停止モード]＝管理者モード
//		　・[会社単位の利用停止．会社ID]　IN　（INPUT．会社ID list）
//		output：
//		　会社ID List・・・（１）
		List<String> lst1 = lstCom.stream().filter(c -> c.getStopMode().equals(StopModeType.ADMIN_MODE))
				.map(d -> d.getContractCd() + "-" + d.getCompanyCd())
				.filter(e -> lstCID.contains(e))
				.collect(Collectors.toList());
		//アルゴリズム「権限(ロール)のチェック」を実行する
//		ログイン者がログイン可能かつ担当者モードで停止中の会社リストの中から、
//		ログイン者に管理権限がある会社のみ抽出する
//		パラメータ
//		　・会社単位の利用停止.利用停止モード＝管理者モード
//		　・会社ID（List）←（１）
//		　・ユーザID
//		output：
//		List＜会社ID＞・・・（２）
		List<String> lst2 = this.checkRoleAuth(StopModeType.ADMIN_MODE, lst1, userID);
		//取得したドメインモデル「会社単位の利用停止」から会社IDのリストを作成する
//		条件：
//		　・[会社単位の利用停止．利用停止モード]＝担当者モード
//		　・[会社単位の利用停止．会社ID]　IN　（INPUT．会社ID list）
//		output：
//		　会社ID List・・・（３）
		List<String> lst3 = lstCom.stream().filter(c -> c.getStopMode().equals(StopModeType.PERSON_MODE))
				.map(d -> d.getContractCd() + "-" + d.getCompanyCd())
				.filter(e -> lstCID.contains(e))
				.collect(Collectors.toList());
		//アルゴリズム「権限(ロール)のチェック」を実行する
//		ログイン者がログイン可能かつ担当者モードで停止中の会社リストの中から、
//		ログイン者に担当権限がある会社のみ抽出する
//		パラメータ
//		　・会社単位の利用停止.利用停止モード＝担当者モード
//		　・会社ID（List）←（３）
//		　・ユーザID
//		output：
//		List＜会社ID＞・・・（４）
		List<String> lst4 = this.checkRoleAuth(StopModeType.PERSON_MODE, lst3, userID);
		//Output：会社ID（List）に追加する
//		（２）と（４）をListに追加する
		result.addAll(lst2);
		result.addAll(lst4);
		//Output：会社ID（List）を返す
		return result;
	}
	/**
	 * 権限(ロール)のチェック ver2
	 * @param 利用停止モード　stopMode
	 * @param List＜会社ID＞ lstCID
	 * @param ユーザID userID
	 * @return List＜会社ID＞
	 */
	@Override
	public List<String> checkRoleAuth(StopModeType stopMode, List<String> lstCID, String userID){
		GeneralDate systemDate = GeneralDate.today();
		//ドメインモデル「ロール個人別付与」を取得する (Lấy DomainModel 「ロール個人別付与」)
//		条件：
//		ユーザID＝input．ユーザID（input．User ID）
//		ロール種類＝システム管理者
//		有効期間.From <= システム日付 <= 有効期間.To
		List<RoleIndividualGrantImport> lstRoleSysAd = roleIndGrantAdapter.getByUserIDDateRoleType(userID, systemDate, RoleType.SYSTEM_MANAGER.value);
		if(!lstRoleSysAd.isEmpty()){//システム管理者ロールの設定がある
			return lstCID;
		}
		//システム管理者ロールの設定がない
		//[利用停止モード]を判別 (phân biệt [mode stop use])
		List<String> lstResult = new ArrayList<>();
		if(stopMode.equals(StopModeType.ADMIN_MODE)){//管理者モードの場合
			for(String cID : lstCID){
				//ドメインモデル「ロール個人別付与」を取得する (Lấy DomainModel 「ロール個人別付与」)
//				条件：
//				ユーザID ＝ input．ユーザID（input．User ID）
//				ロール種類 ＝  会社管理者
//				会社ID ＝ ループ中の会社ID
//				有効期間.From <= システム日付 <= 有効期間.To
				Optional<RoleIndividualGrantImport> roleComAd = roleIndGrantAdapter.getByRoleType(userID, cID, RoleType.COMPANY_MANAGER.value, systemDate);
				if(roleComAd.isPresent()){
					lstResult.add(cID);
				}
			}
		}else{//担当者モードの場合
			for(String cID : lstCID){
				//ドメインモデル「ロール個人別付与」を取得する (Lấy DomainModel 「ロール個人別付与」)
//				条件：
//				ユーザID ＝ input．ユーザID（input．User ID）
//				ロール種類 ＜＞ グループ会社管理者
//				会社ID ＝ ループ中の会社ID
//				有効期間.From <= システム日付 <= 有効期間.To
				List<RoleIndividualGrantImport> lstRoleDifGr = roleIndGrantAdapter.getListDifRoleType(userID, cID, 
						RoleType.GROUP_COMAPNY_MANAGER.value, systemDate);
				if(!lstRoleDifGr.isEmpty()){
					lstResult.add(cID);
				}
			}
		}
		return lstResult;
	}
	/**
	 * @author hoatt
	 * 利用停止会社リストを取得する
	 * @param ドメインモデル「会社単位の利用停止」 - lstComStop
	 * @return 利用停止会社ID（List）
	 */
	@Override
	public List<String> getLstComStopUse(List<StopByCompany> lstComStop) {
		List<String> result = new ArrayList<>();
		//Input.ドメインモデル「会社単位の利用停止」をLoopする
		for(StopByCompany stopCom : lstComStop){
			//ドメインモデル「会社単位の利用停止.システム利用状態」をチェックする(check StopByCompany.systemStatus)
			if(stopCom.getSystemStatus().equals(SystemStatusType.STOP)){
				//アルゴリズム「権限(ロール)のチェック」を実行する
				if(this.checkRoleAuth2(stopCom.getStopMode())){//True：ログイン権限あり
					continue;
				}
				//会社IDを生成する 会社ID＝[会社単位の利用停止.契約コード]+"-"+[会社単位の利用停止.会社コード]
				//		ex) 000000000001-0001
				//Output：利用停止会社ID（List）に「会社ID」を追加する
				result.add(stopCom.getContractCd() + "-" + stopCom.getCompanyCd());
			}
		}
		return result;
	}
	private boolean checkRoleAuth2(StopModeType stopMode) {
		//[利用停止モード]を判別 (phân biệt [mode stop use])
		if(stopMode.equals(StopModeType.ADMIN_MODE)){
			//システム管理者ロールの設定があるか判別
			if(AppContexts.user().roles().forSystemAdmin() != null){
				return true;
			}
			//会社管理者ロールの設定があるか判別
			if(AppContexts.user().roles().forCompanyAdmin() != null){
				return true;
			}else{
				return false;
			}
		}else{
			//システム管理者ロールの設定があるか判別
			if(AppContexts.user().roles().forSystemAdmin() != null){
				return true;
			}
			//会社管理者ロールの設定があるか判別
			if(AppContexts.user().roles().forCompanyAdmin() != null){
				return true;
			}else{
				//リクエストリスト497を呼ぶ。：「ログイン者が担当者か判断する」で担当者ロールが存在するかを判別
				return roleAdapter.isEmpWhetherLoginerCharge();
			}
		}
	}

}
