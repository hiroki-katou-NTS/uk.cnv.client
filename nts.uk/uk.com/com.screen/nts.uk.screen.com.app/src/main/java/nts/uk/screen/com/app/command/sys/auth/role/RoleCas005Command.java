package nts.uk.screen.com.app.command.sys.auth.role;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.com.context.AppContexts;

@Getter
@Setter
@NoArgsConstructor
public class RoleCas005Command {
	/**
	 * class Role : ロール
	 */
	
	// Id
	
	private String roleId;

	// コード RoleCode
	private String roleCode;

	// ロール種類 RoleType
	private int roleType;

	// 参照範囲 EmployeeReferenceRange
	private int employeeReferenceRange;

	// ロール名称 RoleName
	private String name;

	// 契約コード ContractCode
	private String contractCode;

	// 担当区分 RoleAtr
	private int assignAtr;

	// 会社ID
	private String companyId;
	
	/**
	 * Class RoleByRoleTies : 担当ロール別紐付け
	 */
	
	private String webMenuCd;
	
	/**
	 * Class : 就業ロール
	 */
	//スケジュール画面社員参照
	private int scheduleEmployeeRef;
	//予約画面社員参照
	private int bookEmployeeRef; 
	//代行者指定時社員参照 
	private  int employeeRefSpecAgent;
	//在席照会社員参照
	private int presentInqEmployeeRef; 
	//未来日参照許可 
	private int futureDateRefPermit;
	/**
	 * class WorkPlaceAuthority : 所属職場権限
	 */
	private List<WorkPlaceAuthorityCommand> listWorkPlaceAuthority;
	
	public RoleCas005Command(String roleId, String roleCode, int roleType, int employeeReferenceRange, String name,
			String contractCode, int assignAtr, String companyId, String webMenuCd, int scheduleEmployeeRef,
			int bookEmployeeRef, int employeeRefSpecAgent, int presentInqEmployeeRef, int futureDateRefPermit,
			List<WorkPlaceAuthorityCommand> listWorkPlaceAuthority) {
		super();
		this.roleId = roleId;
		this.roleCode = roleCode;
		this.roleType = roleType;
		this.employeeReferenceRange = employeeReferenceRange;
		this.name = name;
		this.contractCode = AppContexts.user().contractCode();
		this.assignAtr = assignAtr;
		this.companyId = AppContexts.user().companyId();
		this.webMenuCd = webMenuCd;
		this.scheduleEmployeeRef = scheduleEmployeeRef;
		this.bookEmployeeRef = bookEmployeeRef;
		this.employeeRefSpecAgent = employeeRefSpecAgent;
		this.presentInqEmployeeRef = presentInqEmployeeRef;
		this.futureDateRefPermit = futureDateRefPermit;
		this.listWorkPlaceAuthority = listWorkPlaceAuthority;
	}
	
}
