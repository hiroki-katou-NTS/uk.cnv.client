/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.grant.roleindividual;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.shared.dom.user.User;

/**
 * ロール個人別付与
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.付与.ロール個人別付与.ロール個人別付与
 * The Class RoleIndividualGrant.
 */
@Getter
@Setter
@AllArgsConstructor
public class RoleIndividualGrant extends AggregateRoot {

	/** The user id. */
	// ユーザID
	private String userId;
	
	/** The company id. */
	// 会社ID
	private String companyId;
	
	/** The role type. */
	// ロール種類
	private RoleType roleType;

	/** The role id. */
	// ロールID
	private String roleId;
	
	/** The valid period. */
	// 有効期間
	private DatePeriod validPeriod;
	
	public static RoleIndividualGrant createFromJavaType(String userId, String roleId, String companyId,int roleType, GeneralDate validPeriodStart,GeneralDate validPeriodEnd) {
		return new RoleIndividualGrant(userId,
			companyId,
			EnumAdaptor.valueOf(roleType, RoleType.class),
			roleId,
			new  DatePeriod(validPeriodStart, validPeriodEnd) );
	}
	
	/**
	 * ロールから作成する
	 * @param role ロール
	 * @param grantTargetUser 付与対象ユーザ
	 * @param grantTargetCompany 付与対象会社
	 * @param validPeriod 有効期間
	 * @return
	 */
	private static RoleIndividualGrant createFromRole( Role role
			,	String grantTargetUser
			,	String grantTargetCompany
			,	DatePeriod validPeriod) {
		
		return new RoleIndividualGrant(
						grantTargetUser
					,	grantTargetCompany
					,	role.getRoleType()
					,	role.getRoleId()
					,	validPeriod
						);
	}
	
	/**
	 *  システム管理者ロールの付与情報を作成する
	 * @param require
	 * @param grantTargetUser 付与対象ユーザ
	 * @param validPeriod 有効期間
	 * @return
	 */
	public static RoleIndividualGrant createGrantInfoOfSystemMananger( Require require
			,	String grantTargetUser
			,	DatePeriod validPeriod) {
		
		val role = require.getRoleByRoleType( RoleType.SYSTEM_MANAGER );
		
		return createFromRole( role, grantTargetUser, role.getCompanyId(), validPeriod);
	}
	
	/**
	 * 会社管理者ロールの付与情報を作成する	
	 * @param require
	 * @param grantTargetUser 付与対象ユーザ
	 * @param grantTargetCompany 付与対象会社
	 * @param validPeriod 有効期間
	 * @return
	 */
	public static RoleIndividualGrant createGrantInfoOfCompanyManager( Require require
			,	String grantTargetUser
			,	String grantTargetCompany
			,	DatePeriod validPeriod ) {
		
		val role = require.getRoleByRoleType( RoleType.COMPANY_MANAGER );
		
		return createFromRole( role, grantTargetUser, grantTargetCompany, validPeriod );
		
	}
	
	/**
	 * 正常な状態かチェックする
	 * @param require
	 */
	public void checkStatusNormal( Require require ) {
		
		if(!this.roleType.isManagerRole() ) {
			throw new RuntimeException("this role is not manager role!!!");
		}
		
		val user = require.getUser( this.userId );
		
		if( !user.isPresent() ) {
			throw new BusinessException( "Msg_2210", this.userId );
		}
		
		if( user.get().isDefaultUser() ) {
			throw new RuntimeException("this user is default user!!!");
		}
		
		if( !this.getCorrectedValidPeriodByUserInfo(require).isPresent() ) {
			throw new BusinessException( "Msg_2211" );
		}
	}
	
	/**
	 * ユーザ情報から補正した有効期間を取得する
	 * @param require
	 * @return
	 */
	public Optional< DatePeriod > getCorrectedValidPeriodByUserInfo( Require require ){
		
		val user = require.getUser( this.userId );
		
		if( !user.isPresent() || user.get().getExpirationDate().before( this.validPeriod.start()) ){
			return Optional.empty();
		}
		
		val endDate = this.validPeriod.end().before(user.get().getExpirationDate()) ? this.validPeriod.end()
				: user.get().getExpirationDate();
		
		return Optional.of( new DatePeriod( this.validPeriod.start() , endDate ) );
	}
	

	public static interface Require{
		
		/**
		 * ロール種類からロールを取得する( ロール種類 )
		 * @param roleType ロール種類
		 * @return
		 */
		Role getRoleByRoleType( RoleType roleType );
		
		/**
		 * ユーザを取得する
		 * @param userId ユーザID
		 * @return
		 */
		Optional< User > getUser( String userId );
	}
}
