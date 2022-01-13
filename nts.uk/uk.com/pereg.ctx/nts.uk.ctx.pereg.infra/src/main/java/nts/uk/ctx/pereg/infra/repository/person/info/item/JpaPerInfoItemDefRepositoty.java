package nts.uk.ctx.pereg.infra.repository.person.info.item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.pereg.dom.person.info.dateitem.DateItem;
import nts.uk.ctx.pereg.dom.person.info.item.ItemBasicInfo;
import nts.uk.ctx.pereg.dom.person.info.item.ItemType;
import nts.uk.ctx.pereg.dom.person.info.item.PerInfoItemDefRepositoty;
import nts.uk.ctx.pereg.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.ctx.pereg.dom.person.info.item.PersonInfoItemDefinitionSimple;
import nts.uk.ctx.pereg.dom.person.info.numericitem.NumericItem;
import nts.uk.ctx.pereg.dom.person.info.order.PerInfoItemDefOrder;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.CodeNameReferenceType;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.EnumReferenceCondition;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.MasterReferenceCondition;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.NumericButton;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.ReadOnly;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.ReadOnlyButton;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.ReferenceTypeState;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.ReferenceTypes;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.RelatedCategory;
import nts.uk.ctx.pereg.dom.person.info.selectionitem.SelectionItem;
import nts.uk.ctx.pereg.dom.person.info.singleitem.DataTypeState;
import nts.uk.ctx.pereg.dom.person.info.singleitem.SingleItem;
import nts.uk.ctx.pereg.dom.person.info.stringitem.StringItem;
import nts.uk.ctx.pereg.dom.person.info.timeitem.TimeItem;
import nts.uk.ctx.pereg.dom.person.info.timepointitem.TimePointItem;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtCtg;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtPerInfoCtgPK;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtItem;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtItemCommon;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtPerInfoItemCmPK;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtItemSort;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtPerInfoItemPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class JpaPerInfoItemDefRepositoty extends JpaRepository implements PerInfoItemDefRepositoty {

	private final static String SPECIAL_ITEM_CODE = "IO";

	private final static String SELECT_COMMON_FIELD = String.join(" ", "SELECT i.ppemtPerInfoItemPK.perInfoItemDefId,",
			"i.itemCd, i.itemName, i.abolitionAtr, i.requiredAtr,",
			"ic.itemParentCd, ic.systemRequiredAtr, ic.requireChangabledAtr, ic.fixedAtr, ic.itemType,",
			"ic.dataType, ic.timeItemMin, ic.timeItemMax, ic.timepointItemMin, ic.timepointItemMax, ic.dateItemType,",
			"ic.stringItemType, ic.stringItemLength, ic.stringItemDataType, ic.numericItemMin, ic.numericItemMax, ic.numericItemAmountAtr,",
			"ic.numericItemMinusAtr, ic.numericItemDecimalPart, ic.numericItemIntegerPart,",
			"ic.selectionItemRefType, ic.selectionItemRefCode, i.perInfoCtgId, ic.relatedCategoryCode, ic.resourceId, ic.canAbolition, i.initValue");

	private final static String JOIN_COMMON_TABLE = String.join(" ",
			"FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd INNER JOIN PpemtItemSort io",
			"ON io.ppemtPerInfoItemPK.perInfoItemDefId = i.ppemtPerInfoItemPK.perInfoItemDefId AND io.perInfoCtgId = i.perInfoCtgId");

	private final static String SELECT_NO_WHERE = String.join(" ", SELECT_COMMON_FIELD, " ,io.disporder ", JOIN_COMMON_TABLE);
	
	private final static String COMMON_CONDITION = "ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND ic.itemParentCd IS NULL ORDER BY io.disporder";
	
	private final static String CONDITION_CPS013 = "ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND i.abolitionAtr = 0 ORDER BY io.disporder";
	
	private final static String CONDITION_FOR_007008 = "ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId IN :lstPerInfoCategoryId AND i.abolitionAtr = 0 AND (ic.itemParentCd IS NULL OR ic.itemParentCd = '')  ORDER BY io.disporder";
	
	private final static String CONDITION = "ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND i.abolitionAtr = 0  ORDER BY io.disporder";

	private final static String SELECT_ITEMS_BY_CATEGORY_ID_QUERY = String.join(" ", SELECT_NO_WHERE, "WHERE",
			COMMON_CONDITION);
	
	private final static String SELECT_ALL_ITEM_BY_LIST_CATEGORY_ID_QUERY = String.join(" ", SELECT_NO_WHERE, "WHERE",
			CONDITION_FOR_007008);
	
	private final static String SELECT_ALL_ITEMS_BY_CATEGORY_ID = String.join(" ", SELECT_NO_WHERE, "WHERE",
			CONDITION);
	
	private final static String SELECT_ITEMS_BY_CATEGORY_ID_QUERY_CPS013 = String.join(" ", SELECT_NO_WHERE, "WHERE",
			CONDITION_CPS013);

	private final static String SELECT_ITEM_BY_CTG_WITH_AUTH = String.join(" ", SELECT_NO_WHERE,
			"INNER JOIN PpemtRoleItemAuth au",
			"ON i.ppemtPerInfoItemPK.perInfoItemDefId = au.ppemtPersonItemAuthPk.personItemDefId",
			"AND i.perInfoCtgId = au.ppemtPersonItemAuthPk.personInfoCategoryAuthId",
			"WHERE i.abolitionAtr = 0 AND au.ppemtPersonItemAuthPk.roleId = :roleId");

	private final static String SELECT_ITEMS_BY_CATEGORY_ID_WITHOUT_SETITEM_QUERY = String.join(" ",
			SELECT_NO_WHERE,
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND ic.itemParentCd IS NULL ",
			"ORDER BY io.disporder");

	private final static String SELECT_ITEMS_BY_CATEGORY_ID_WITHOUT_ABOLITION_AND_SETITEM_QUERY = String.join(" ",
			SELECT_NO_WHERE,
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND ic.itemParentCd IS NULL AND i.abolitionAtr = 0",
			"ORDER BY io.disporder");

	private final static String SELECT_ITEM_BY_ITEM_ID_QUERY = String.join(" ",
			SELECT_COMMON_FIELD,
			"FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd",
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.ppemtPerInfoItemPK.perInfoItemDefId = :perInfoCtgId");

	private final static String SELECT_ITEMS_BY_LIST_ITEM_ID_QUERY = String.join(" ",
			SELECT_NO_WHERE,
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.ppemtPerInfoItemPK.perInfoItemDefId IN :listItemDefId",
			"ORDER BY io.disporder");

	private final static String SELECT_ITEMS_BY_LIST_ITEM_ID_QUERY_2 = String.join(" ",
			SELECT_NO_WHERE,
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.ppemtPerInfoItemPK.perInfoItemDefId IN :listItemDefId AND i.abolitionAtr = 0 AND c.abolitionAtr = 0 ",
			"ORDER BY io.disporder");

	private final static String SELECT_ITEMS_NAME_QUERY = String.join(" ",
			"SELECT i.itemName, io.disporder FROM PpemtItem i",
			"INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd INNER JOIN PpemtItemSort io",
			"ON io.ppemtPerInfoItemPK.perInfoItemDefId = i.ppemtPerInfoItemPK.perInfoItemDefId AND io.perInfoCtgId = i.perInfoCtgId",
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND ic.itemParentCd IS NULL ",
			"ORDER BY io.disporder");

	private final static String SELECT_GET_ITEM_CODE_LASTEST_QUERY = String.join(" ",
			"SELECT i.ppemtPerInfoItemCmPK.itemCd FROM PpemtItemCommon i",
			"WHERE i.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.ppemtPerInfoItemCmPK.categoryCd = :categoryCd",
			"ORDER BY i.ppemtPerInfoItemCmPK.itemCd DESC");

	private final static String SELECT_GET_DISPORDER_ITEM_QUERY = String.join(" ",
			"SELECT od.disporder FROM PpemtItemSort od",
			"WHERE od.perInfoCtgId = :perInfoCtgId ORDER BY od.disporder DESC");

	private final static String SELECT_ALL_ITEM_ORDER_BY_CTGID_QUERY = String.join(" ",
			"SELECT o FROM PpemtItemSort o WHERE o.perInfoCtgId = :perInfoCtgId");

	private final static String SELECT_ITEM_ORDER_BY_ITEM_ID_QUERY = String.join(" ",
			"SELECT o FROM PpemtItemSort o WHERE o.ppemtPerInfoItemPK.perInfoItemDefId = :perInfoItemDefId");

	private final static String SELECT_ITEM_DISPORDER_BY_KEY_QUERY = String.join(" ",
			"SELECT o.disporder FROM PpemtItemSort o",
			"WHERE o.perInfoCtgId = :perInfoCtgId AND o.ppemtPerInfoItemPK.perInfoItemDefId = :perInfoItemDefId");

	private final static String SELECT_CHILD_ITEM_IDS = String.join(" ",
			"SELECT DISTINCT i.ppemtPerInfoItemPK.perInfoItemDefId FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon c ON i.itemCd = c.ppemtPerInfoItemCmPK.itemCd",
			"WHERE c.ppemtPerInfoItemCmPK.contractCd = :contractCd",
			"AND c.itemParentCd = :itemParentCd AND i.perInfoCtgId = :perInfoCtgId");

	private final static String SELECT_REQUIRED_ITEMS_IDS = String.join(" ",
			"SELECT DISTINCT i.ppemtPerInfoItemPK.perInfoItemDefId FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon c ON i.itemCd = c.ppemtPerInfoItemCmPK.itemCd",
			"WHERE c.ppemtPerInfoItemCmPK.contractCd = :contractCd AND c.systemRequiredAtr = 1 AND i.abolitionAtr = 0",
			"AND i.perInfoCtgId IN (SELECT g.ppemtPerInfoCtgPK.perInfoCtgId FROM PpemtCtg g WHERE g.cid = :companyId)");
	private final static String SELECT_DEFAULT_ITEM_NAME_BY_ITEMS_CODE = String.join(" ", "SELECT pi.itemName",
			"FROM PpemtItem pi INNER JOIN PpemtCtg pc",
			"ON pi.perInfoCtgId = pc.ppemtPerInfoCtgPK.perInfoCtgId",
			"WHERE pc.categoryCd = :categoryCd AND pi.itemCd = :itemCd AND pc.cid= :cid");

	private final static String SELECT_ITEMS_BY_LIST_CTG_ID_QUERY = String.join(" ", "SELECT i FROM PpemtItem i",
			"WHERE i.itemCd = :itemCd AND i.perInfoCtgId IN :perInfoCtgIds");

	private final static String SELECT_CHECK_ITEM_NAME_QUERY = String.join(" ", "SELECT i.itemName",
			"FROM PpemtItem i WHERE i.perInfoCtgId = :perInfoCtgId AND i.itemName = :itemName",
			"AND i.ppemtPerInfoItemPK.perInfoItemDefId != :perInfoItemDefId");

	private final static String COUNT_ITEMS_IN_CATEGORY = String.join(" ", "SELECT COUNT(i.perInfoCtgId)",
			"FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"WHERE c.cid = :companyId AND i.perInfoCtgId = :perInfoCtgId");

	private final static String COUNT_ITEMS_IN_CATEGORY_NO812 = String.join(" ", "SELECT COUNT(i.perInfoCtgId)",
			"FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd",
			"WHERE c.cid = :companyId AND i.perInfoCtgId = :perInfoCtgId AND i.abolitionAtr = 0",
			"AND ic.dataType != 9 AND ic.dataType != 10");

	private final static String SELECT_PER_ITEM_BY_CTG_ID_AND_ORDER = "SELECT i "
			+ " FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtItemSort io"
			+ " ON i.ppemtPerInfoItemPK.perInfoItemDefId = io.ppemtPerInfoItemPK.perInfoItemDefId "
			+ " WHERE c.cid = :companyId AND i.perInfoCtgId = :perInfoCtgId ORDER BY io.disporder ASC";

	private final static String SELECT_PERINFOITEM_BYCTGID = "SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.itemName,"
			+ " CASE WHEN (ci.ppestEmployeeCopySettingItemPk.perInfoItemDefId) IS NOT NULL  THEN 'True' ELSE 'False' END AS alreadyCopy "
			+ " FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpestEmployeeCopySettingItem ci ON i.ppemtPerInfoItemPK.perInfoItemDefId = ci.ppestEmployeeCopySettingItemPk.perInfoItemDefId"
			+ " WHERE c.cid = :companyId AND i.perInfoCtgId = :perInfoCtgId";
	// vinhpx: end

	private final static String SELECT_NOT_FIXED_ITEMS_BY_CATEGORY_ID_QUERY = String.join(" ",
			SELECT_NO_WHERE,
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId = :perInfoCtgId AND ic.itemParentCd IS NULL AND ic.fixedAtr = 0",
			"ORDER BY io.disporder");

	private final static String SEL_ITEM_BY_SELECTIONS = String.join(" ", "SELECT c FROM PpemtItemCommon c",
			"WHERE c.selectionItemRefCode =:selectionItemId");

	private final static String SEL_ITEM_USED = String.join(" ",
			"SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.perInfoCtgId FROM PpemtCtg c",
			"INNER JOIN PpemtItem i", "ON c.ppemtPerInfoCtgPK.perInfoCtgId = i.perInfoCtgId",
			"INNER JOIN PpemtItemCommon io",
			"ON c.categoryCd = io.ppemtPerInfoItemCmPK.categoryCd AND i.itemCd = io.ppemtPerInfoItemCmPK.itemCd",
			"WHERE i.perInfoCtgId IN :perInfoCtgId  AND i.abolitionAtr = 0 AND io.itemParentCd IS NULL");

	// sonnlb start
	private final static String SEL_REQUIRED_ITEM_BY_CTG = String.join(" ", "SELECT i.itemCd,i.itemName",
			JOIN_COMMON_TABLE, "WHERE c.categoryCd IN :categoryCds", "AND c.cid= :companyId", "AND ic.itemType = 2 ",
			"AND ic.systemRequiredAtr = 1", "AND ic.ppemtPerInfoItemCmPK.contractCd = :contractCd",
			"AND i.abolitionAtr = 0");

	// sonnlb end

	// lanlt start
	private final static String SELECT_REQUIRED_ITEMS_ID_BY_CID = String.join(" ",
			"SELECT DISTINCT i.ppemtPerInfoItemPK.perInfoItemDefId FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon c ON i.itemCd = c.ppemtPerInfoItemCmPK.itemCd",
			"WHERE c.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.requiredAtr = 1 AND i.abolitionAtr = 0",
			"AND i.perInfoCtgId IN (SELECT g.ppemtPerInfoCtgPK.perInfoCtgId FROM PpemtCtg g WHERE g.cid = :companyId)");

	private final static String SELECT_REQUIRED_ITEMS_ID_BY_CTG_ID = String.join(" ",
			"SELECT DISTINCT i.ppemtPerInfoItemPK.perInfoItemDefId FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon c ON i.itemCd = c.ppemtPerInfoItemCmPK.itemCd",
			"WHERE c.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.requiredAtr = 1 AND i.abolitionAtr = 0",
			"AND i.perInfoCtgId =:perInfoCtgId");

	// lanlt end
	private final static String SELECT_SIMPLE_ITEM_DEF = String.join(" ",
			"SELECT i.itemCd, i.itemName , i.abolitionAtr FROM PpemtItem i",
			"JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"WHERE c.categoryCd = :ctgCd and c.cid = :cid");

	private final static String SELECT_ITEMS_ID_BY_CTG_ID = String.join(" ",
			"SELECT DISTINCT i.ppemtPerInfoItemPK.perInfoItemDefId FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon c ON i.itemCd = c.ppemtPerInfoItemCmPK.itemCd",
			"INNER JOIN PpemtCtg p ON i.perInfoCtgId = p.ppemtPerInfoCtgPK.perInfoCtgId ",
			"AND c.ppemtPerInfoItemCmPK.categoryCd = p.categoryCd ",
			"INNER JOIN PpemtCtgCommon pc ON c.ppemtPerInfoItemCmPK.categoryCd = pc.ppemtPerInfoCtgCmPK.categoryCd",
			"AND c.ppemtPerInfoItemCmPK.contractCd = pc.ppemtPerInfoCtgCmPK.contractCd",
			"WHERE i.abolitionAtr = 0 AND c.ppemtPerInfoItemCmPK.categoryCd =:ctgCode",
			"AND p.abolitionAtr = 0 AND p.cid =:cid");

	private final static String SELECT_ITEM_BY_CTGID_AND_COMID = String.join(" ",
			"SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.itemName, i.itemCd FROM PpemtItem i",
			"JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"WHERE c.categoryCd = :ctgCd AND c.cid = :cid");

	private final static String SELECT_ITEM_NAME_QUERY = String.join(" ",
			"SELECT i.itemName FROM PpemtItem i",
			"INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd",
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND c.cid =:cid AND c.categoryCd =:categoryCd AND i.itemCd =:itemCd ");
	
	private final static String SELECT_ITEM_DF_ID_QUERY = String.join(" ",
			"SELECT i.ppemtPerInfoItemPK.perInfoItemDefId FROM PpemtItem i",
			"INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd",
			"WHERE i.perInfoCtgId =:categoryId AND i.itemCd =:itemCd ");
	
	private final static String SELECT_ITEM_CD_BY_ITEM_ID_QUERY = String.join(" ",
			"SELECT distinct i.itemCd",
			"FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon ic ON  i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd ",
			"WHERE  ic.ppemtPerInfoItemCmPK.contractCd =:contractCd and i.ppemtPerInfoItemPK.perInfoItemDefId IN :listItemDefId ");
	
	private final static String GET_LIST_ITEM_FOR_CPS002B = String.join(" ",
			"SELECT distinct i.itemCd",
			"FROM PpemtItem i",
			"INNER JOIN PpemtItemCommon ic ON  i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd ",
			"WHERE  ic.ppemtPerInfoItemCmPK.contractCd =:contractCd and i.ppemtPerInfoItemPK.perInfoItemDefId IN :listItemDefId and i.abolitionAtr = 0 ");
	
	private final static String SELECT_ITEM_CD_BY_ITEM_CD_QUERY = String.join(" ",
			"SELECT distinct ic.ppemtPerInfoItemCmPK.itemCd",
			"FROM PpemtItem i INNER JOIN  PpemtItemCommon ic  ON i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd ",
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd =:contractCd AND  (ic.ppemtPerInfoItemCmPK.itemCd IN :itemCdLst OR ic.itemParentCd IN :itemCdLst) ");
	
	private final static String SELECT_CHILD_ITEMS_BY_ITEM_CD_QUERY = String.join(" ",
			 SELECT_NO_WHERE,
			"WHERE c.cid =:cid  and ic.ppemtPerInfoItemCmPK.contractCd =:contractCd AND ic.itemType = 2 AND ic.ppemtPerInfoItemCmPK.categoryCd IN :ctgLst AND  (ic.ppemtPerInfoItemCmPK.itemCd IN :itemCdLst OR ic.itemParentCd IN :itemCdLst) ",
			"ORDER BY io.disporder");
	
	private final static String SEL_ITEM_EVENT = String.join(" ",
			"SELECT i.ppemtPerInfoItemPK.perInfoItemDefId, i.perInfoCtgId FROM  PpemtItem i",
			"WHERE i.perInfoCtgId IN :perInfoCtgId   AND i.itemCd IN :itemCd");

	private final static String SELECT_ALL_ITEMS_BY_ITEM_CD_QUERY = String.join(" ",
			SELECT_NO_WHERE,
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd =:contractCd AND  (ic.ppemtPerInfoItemCmPK.itemCd IN :itemCdLst OR ic.itemParentCd IN :itemCdLst) ",
			" AND ic.ppemtPerInfoItemCmPK.categoryCd =:categoryCd AND i.perInfoCtgId =:ctgId",
			"ORDER BY io.disporder");
	
	private final static String SELECT_ALL_DISORDER__BY_CTC_ID_QUERY = String.join(" ",
			"SELECT  o FROM PpemtItemSort o WHERE o.perInfoCtgId =:perInfoCtgId ");
	
	private final static String SELECT_ITEMDF_BY_CTGCD_ITEMCD_CID = String.join(" ",
			SELECT_COMMON_FIELD,
			"FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd",
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND c.categoryCd = :categoryCd AND c.cid = :cid AND i.itemCd = :itemCd");


	private final static String CONDITION_FOR_ALL_REQUIREDITEM_BY_LIST_CATEGORY_ID = "ic.ppemtPerInfoItemCmPK.contractCd = :contractCd "
			+ "AND i.perInfoCtgId IN :lstPerInfoCategoryId AND i.abolitionAtr = 0 "
			+ "AND i.requiredAtr = 1  ORDER BY io.disporder";

	
	private final static String SELECT_COMMON_FIELD_BYCATEGORYCD = String.join(" ", "SELECT i.ppemtPerInfoItemPK.perInfoItemDefId,",
			"i.itemCd, i.itemName, i.abolitionAtr, i.requiredAtr,",
			"ic.itemParentCd, ic.systemRequiredAtr, ic.requireChangabledAtr, ic.fixedAtr, ic.itemType,",
			"ic.dataType, ic.timeItemMin, ic.timeItemMax, ic.timepointItemMin, ic.timepointItemMax, ic.dateItemType,",
			"ic.stringItemType, ic.stringItemLength, ic.stringItemDataType, ic.numericItemMin, ic.numericItemMax, ic.numericItemAmountAtr,",
			"ic.numericItemMinusAtr, ic.numericItemDecimalPart, ic.numericItemIntegerPart,",
			"ic.selectionItemRefType, ic.selectionItemRefCode, i.perInfoCtgId, ic.relatedCategoryCode, ic.resourceId, ic.canAbolition, c.categoryCd");

	private final static String SELECT_NO_WHERE_BYCATEGORYCODE = String.join(" ", SELECT_COMMON_FIELD_BYCATEGORYCD, " ,io.disporder ", JOIN_COMMON_TABLE);
	
	private final static String SELECT_ALL_REQUIREDITEM_BY_LIST_CATEGORY_ID = String.join(" ", SELECT_NO_WHERE_BYCATEGORYCODE, "WHERE",
			CONDITION_FOR_ALL_REQUIREDITEM_BY_LIST_CATEGORY_ID);
	
	private final static String SELECT_REQUIRED_ITEM = "SELECT i.itemCd, i.perInfoCtgId, i.itemName, i.ppemtPerInfoItemPK.perInfoItemDefId, i.requiredAtr, i.abolitionAtr  "
			+ " FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd"
			+ " AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd "
			+ " AND i.contractCd = ic.ppemtPerInfoItemCmPK.contractCd"
			+ " AND c.contractCd = ic.ppemtPerInfoItemCmPK.contractCd"
			+ " WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND i.perInfoCtgId IN :lstPerInfoCategoryId AND ic.itemType <> 1";
	
	private final static String SEL_ITEM_ID_BY_CTG_CD_AND_ITEM_CD = "SELECT DISTINCT i.ppemtPerInfoItemPK.perInfoItemDefId"
			+ " FROM PpemtItem i"
			+ " WHERE i.itemCd IN :itemCd"
			+ " AND i.perInfoCtgId IN (SELECT DISTINCT c.ppemtPerInfoCtgPK.perInfoCtgId FROM  PpemtCtg c WHERE c.cid =:cid  AND  c.categoryCd IN :categoryCd)";
	private final static String SELECT_ITEMDF_BY_CTGCD_ITEMCDS_CID = String.join(" ",
			SELECT_COMMON_FIELD,
			"FROM PpemtItem i INNER JOIN PpemtCtg c ON i.perInfoCtgId = c.ppemtPerInfoCtgPK.perInfoCtgId",
			"INNER JOIN PpemtItemCommon ic ON c.categoryCd = ic.ppemtPerInfoItemCmPK.categoryCd",
			"AND i.itemCd = ic.ppemtPerInfoItemCmPK.itemCd",
			"WHERE ic.ppemtPerInfoItemCmPK.contractCd = :contractCd AND c.categoryCd = :categoryCd AND c.cid = :cid AND i.itemCd IN :itemCd");

	
	private static Comparator<Object[]> SORT_BY_DISPORDER = (o1, o2) -> {
		return ((int) o1[32]) - ((int) o2[32]); // index 31 for [disporder] 
	};
	
	@Override
	public List<PersonInfoItemDefinition> getAllPerInfoItemDefByCategoryId(String perInfoCtgId, String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_BY_CATEGORY_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					List<String> items = getChildIds(contractCd, perInfoCtgId, String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}
	
	//Function get List Category Combobox CPS007
	@Override
	public Map<String, List<Object[]>> getAllPerInfoItemDefByListCategoryId(List<String> lstPerInfoCategoryId,
			String contractCd) {
		List<Object[]> lstObj = new ArrayList<>();
		CollectionUtil.split(lstPerInfoCategoryId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstObj.addAll(this.queryProxy().query(SELECT_ALL_ITEM_BY_LIST_CATEGORY_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("lstPerInfoCategoryId", subList).getList());
		});
		lstObj.sort(SORT_BY_DISPORDER);
		
		// groupBy categoryId 
		Map<String, List<Object[]>> result = lstObj.stream().collect(Collectors.groupingBy(x -> String.valueOf(x[27])));
		
		return result;
	}
	
	@Override
	public List<PersonInfoItemDefinition> getAllItemDefByCategoryId(String perInfoCtgId, String contractCd) {
		return this.queryProxy().query(SELECT_ALL_ITEMS_BY_CATEGORY_ID, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					return createDomainFromEntity1(i, new ArrayList<>());
				});
	}

	@Override
	public Optional<PersonInfoItemDefinition> getPerInfoItemDefById(String perInfoItemDefId, String contractCd) {
		return this.queryProxy().query(SELECT_ITEM_BY_ITEM_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoItemDefId).getSingle(i -> {
					List<String> items = getChildIds(contractCd, String.valueOf(i[27]), String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public List<PersonInfoItemDefinition> getPerInfoItemDefByListId(List<String> listItemDefId, String contractCd) {
		List<Object[]> entities = new ArrayList<>();
		CollectionUtil.split(listItemDefId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			entities.addAll(this.queryProxy().query(SELECT_ITEMS_BY_LIST_ITEM_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("listItemDefId", subList).getList());
		});
		entities.sort(SORT_BY_DISPORDER);
		return entities.stream().map(i -> {
					List<String> items = getChildIds(contractCd, String.valueOf(i[27]), String.valueOf(i[1]));
					return createDomainFromEntity1(i, items);
				}).collect(Collectors.toList());
	}

	@Override
	public List<PersonInfoItemDefinition> getPerInfoItemDefByListIdv2(List<String> listItemDefId, String contractCd) {
		List<Object[]> entities = new ArrayList<>();
		CollectionUtil.split(listItemDefId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			entities.addAll(this.queryProxy()
					.query(SELECT_ITEMS_BY_LIST_ITEM_ID_QUERY_2, Object[].class).setParameter("contractCd", contractCd)
					.setParameter("listItemDefId", subList).getList());
		});
		if (CollectionUtil.isEmpty(entities)) return Collections.emptyList();
		entities.sort(SORT_BY_DISPORDER);
		return entities.stream().map(i -> {
					List<String> items = getChildIds(contractCd, String.valueOf(i[27]), String.valueOf(i[1]));
					return createDomainFromEntity1(i, items);
				}).collect(Collectors.toList());
	}

	@Override
	public List<String> getPerInfoItemsName(String perInfoCtgId, String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_NAME_QUERY, Object[].class).setParameter("contractCd", contractCd)
				.setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					return i[0].toString();
				});
	}

	@Override
	public String addPerInfoItemDefRoot(PersonInfoItemDefinition perInfoItemDef, String contractCd, String ctgCode) {
		PpemtItemCommon perInfoItemCm = createPerInfoItemDefCmFromDomain(perInfoItemDef, contractCd, ctgCode);
		this.commandProxy().insert(perInfoItemCm);
		this.commandProxy().insert(createPerInfoItemDefFromDomain(perInfoItemDef));
		addOrderItemRoot(perInfoItemDef.getPerInfoItemDefId(), perInfoItemDef.getPerInfoCategoryId());
		return perInfoItemDef.getPerInfoItemDefId();
	}

	@Override
	public List<String> addPerInfoItemDefByCtgIdList(PersonInfoItemDefinition perInfoItemDef,
			List<String> perInfoCtgId) {
		List<String> perInfoItemDefIds = new ArrayList<>();
		this.commandProxy().insertAll(perInfoCtgId.stream().map(i -> {
			PpemtItem item = createPerInfoItemDefFromDomainWithCtgId(perInfoItemDef, i);
			perInfoItemDefIds.add(item.ppemtPerInfoItemPK.perInfoItemDefId);
			addOrderItemRoot(item.ppemtPerInfoItemPK.perInfoItemDefId, i);
			return item;
		}).collect(Collectors.toList()));
		return perInfoItemDefIds;
	}

	@Override
	public boolean checkItemNameIsUnique(String perInfoCtgId, String newItemName, String perInfoItemDefId) {
		List<String> itemNames = this.queryProxy().query(SELECT_CHECK_ITEM_NAME_QUERY, String.class)
				.setParameter("perInfoCtgId", perInfoCtgId).setParameter("itemName", newItemName)
				.setParameter("perInfoItemDefId", perInfoItemDefId).getList();
		if (itemNames == null || itemNames.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void updatePerInfoItemDefRoot(PersonInfoItemDefinition perInfoItemDef, String contractCd) {
		PpemtPerInfoCtgPK perInfoCtgPK = new PpemtPerInfoCtgPK(perInfoItemDef.getPerInfoCategoryId());

		PpemtCtg perInfoCtg = this.queryProxy().find(perInfoCtgPK, PpemtCtg.class).orElse(null);

		if (perInfoCtg == null) {
			return;
		}

		PpemtPerInfoItemCmPK perInfoItemCmPK = new PpemtPerInfoItemCmPK(contractCd, perInfoCtg.categoryCd,
				perInfoItemDef.getItemCode().v());

		PpemtItemCommon itemCmOld = this.queryProxy().find(perInfoItemCmPK, PpemtItemCommon.class).orElse(null);

		if (itemCmOld == null) {
			return;
		}

		PpemtItemCommon itemCmNew = createPerInfoItemDefCmFromDomain(perInfoItemDef, contractCd,
				perInfoCtg.categoryCd);

		itemCmNew.setInsCcd(itemCmOld.getInsCcd());
		itemCmNew.setInsDate(itemCmOld.getInsDate());
		itemCmNew.setInsScd(itemCmOld.getInsScd());
		itemCmNew.setInsPg(itemCmOld.getInsPg());
		itemCmNew.setUpdDate(itemCmOld.getUpdDate());
		itemCmNew.setUpdCcd(itemCmOld.getUpdCcd());
		itemCmNew.setUpdScd(itemCmOld.getUpdScd());
		itemCmNew.setUpdPg(itemCmOld.getUpdPg());

		this.commandProxy().update(itemCmNew);

		PpemtPerInfoItemPK perInfoItemPK = new PpemtPerInfoItemPK(perInfoItemDef.getPerInfoItemDefId());
		PpemtItem perInfoItem = this.queryProxy().find(perInfoItemPK, PpemtItem.class).orElse(null);

		if (perInfoItem == null) {
			return;
		}

		perInfoItem.itemName = perInfoItemDef.getItemName().v();

		this.commandProxy().update(perInfoItem);
	}

	@Override
	public void removePerInfoItemDef(List<String> perInfoCtgIds, String categoryCd, String contractCd,
			String itemCode) {
		List<PpemtItem> listItem = new ArrayList<>();
		CollectionUtil.split(perInfoCtgIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listItem.addAll(this.queryProxy().query(SELECT_ITEMS_BY_LIST_CTG_ID_QUERY, PpemtItem.class)
					.setParameter("itemCd", itemCode)
					.setParameter("perInfoCtgIds", subList)
					.getList());
		});
		this.commandProxy().removeAll(listItem);
		PpemtPerInfoItemCmPK perInfoItemCmPK = new PpemtPerInfoItemCmPK(contractCd, categoryCd, itemCode);
		this.commandProxy().remove(PpemtItemCommon.class, perInfoItemCmPK);
		List<PpemtPerInfoItemPK> listPK = listItem.stream().map(item -> {
			return item.ppemtPerInfoItemPK;
		}).collect(Collectors.toList());
		this.commandProxy().removeAll(PpemtItemSort.class, listPK);
	}

	@Override
	public String getPerInfoItemCodeLastest(String contractCd, String categoryCd) {
		List<String> itemCodeLastest = this.getEntityManager()
				.createQuery(SELECT_GET_ITEM_CODE_LASTEST_QUERY, String.class).setParameter("contractCd", contractCd)
				.setParameter("categoryCd", categoryCd).getResultList();
		return itemCodeLastest.stream().filter(i -> i.contains(SPECIAL_ITEM_CODE)).findFirst().orElse(null);
	}

	@Override
	public List<PerInfoItemDefOrder> getPerInfoItemDefOrdersByCtgId(String perInfoCtgId) {
		return this.queryProxy().query(SELECT_ALL_ITEM_ORDER_BY_CTGID_QUERY, PpemtItemSort.class)
				.setParameter("perInfoCtgId", perInfoCtgId).getList(o -> createPerInfoItemDefOrderFromEntity(o));
	}

	@Override
	public int getItemDispOrderBy(String perInfoCtgId, String perInfoItemDefId) {
		return this.queryProxy().query(SELECT_ITEM_DISPORDER_BY_KEY_QUERY, Integer.class)
				.setParameter("perInfoCtgId", perInfoCtgId).setParameter("perInfoItemDefId", perInfoItemDefId)
				.getSingle().orElse(0);
	}

	@Override
	public List<String> getRequiredIds(String contractCd, String companyId) {
		return queryProxy().query(SELECT_REQUIRED_ITEMS_IDS, String.class).setParameter("contractCd", contractCd)
				.setParameter("companyId", companyId).getList();
	}

	private List<String> getChildIds(String contractCd, String perInfoCtgId, String parentCode) {
		return queryProxy().query(SELECT_CHILD_ITEM_IDS, String.class).setParameter("contractCd", contractCd)
				.setParameter("itemParentCd", parentCode).setParameter("perInfoCtgId", perInfoCtgId).getList();
	}

	private void addOrderItemRoot(String perInfoItemDefId, String perInfoCtgId) {
		int newdisOrderLastest = getDispOrderLastestItemOfCtg(perInfoCtgId) + 1;
		this.commandProxy().insert(createItemOrder(perInfoItemDefId, perInfoCtgId, newdisOrderLastest));
	}

	private int getDispOrderLastestItemOfCtg(String perInfoCtgId) {
		List<Integer> dispOrderLastests = this.getEntityManager()
				.createQuery(SELECT_GET_DISPORDER_ITEM_QUERY, Integer.class).setParameter("perInfoCtgId", perInfoCtgId)
				.setMaxResults(1).getResultList();
		if (dispOrderLastests != null && !dispOrderLastests.isEmpty()) {
			return dispOrderLastests.get(0);
		}
		return 0;
	}

	private PpemtItemSort createItemOrder(String perInfoItemDefId, String perInfoCtgId, int dispOrder) {
		PpemtPerInfoItemPK perInfoItemPK = new PpemtPerInfoItemPK(perInfoItemDefId);
		return new PpemtItemSort(perInfoItemPK, perInfoCtgId, dispOrder, dispOrder);
	}

	private PpemtItemSort createItemOrder(String perInfoItemDefId, String perInfoCtgId, int dispOrder,
			int displayOrder) {
		PpemtPerInfoItemPK perInfoItemPK = new PpemtPerInfoItemPK(perInfoItemDefId);
		return new PpemtItemSort(perInfoItemPK, perInfoCtgId, dispOrder, displayOrder);
	}

	private PersonInfoItemDefinition createDomainFromEntity(Object[] i, List<String> items) {
		String perInfoItemDefId = String.valueOf(i[0]);
		String itemCode = String.valueOf(i[1]);
		String itemName = String.valueOf(i[2]);
		int isAbolition = Integer.parseInt(String.valueOf(i[3]));
		int isRequired = Integer.parseInt(String.valueOf(i[4]));
		String itemParentCode = (i[5] == null) ? null : String.valueOf(i[5]);
		int systemRequired = Integer.parseInt(String.valueOf(i[6]));
		int requireChangable = Integer.parseInt(String.valueOf(i[7]));
		int isFixed = Integer.parseInt(String.valueOf(i[8]));
		int itemType = Integer.parseInt(String.valueOf(i[9]));
		BigDecimal dataType = i[10] == null ? null : new BigDecimal(String.valueOf(i[10]));
		BigDecimal timeItemMin = i[11] == null ? null : new BigDecimal(String.valueOf(i[11]));
		BigDecimal timeItemMax = i[12] == null ? null : new BigDecimal(String.valueOf(i[12]));
		BigDecimal timepointItemMin = i[13] == null ? null : new BigDecimal(String.valueOf(i[13]));
		BigDecimal timepointItemMax = i[14] == null ? null : new BigDecimal(String.valueOf(i[14]));
		BigDecimal dateItemType = i[15] == null ? null : new BigDecimal(String.valueOf(i[15]));
		BigDecimal stringItemType = i[16] == null ? null : new BigDecimal(String.valueOf(i[16]));
		BigDecimal stringItemLength = i[17] == null ? null : new BigDecimal(String.valueOf(i[17]));
		BigDecimal stringItemDataType = i[18] == null ? null : new BigDecimal(String.valueOf(i[18]));
		BigDecimal numericItemMin = i[19] == null ? null : new BigDecimal(String.valueOf(i[19]));
		BigDecimal numericItemMax = i[20] == null ? null : new BigDecimal(String.valueOf(i[20]));
		BigDecimal numericItemAmount = i[21] == null ? null : new BigDecimal(String.valueOf(i[21]));
		BigDecimal numericItemMinus = i[22] == null ? null : new BigDecimal(String.valueOf(i[22]));
		BigDecimal numericItemDecimalPart = i[23] == null ? null : new BigDecimal(String.valueOf(i[23]));
		BigDecimal numericItemIntegerPart = i[24] == null ? null : new BigDecimal(String.valueOf(i[24]));
		BigDecimal selectionItemRefType = i[25] == null ? null : new BigDecimal(String.valueOf(i[25]));
		String selectionItemRefCode = i[26] == null ? "" : String.valueOf(i[26]);
		String perInfoCategoryId = String.valueOf(i[27]);

		String relatedCategoryCode = String.valueOf(i[28]);
		String resourceId = i[29] == null ? null : String.valueOf(i[29]);
		int canAbolition = Integer.parseInt(String.valueOf(i[30]));
		String initValue = i[31] == null ? null : String.valueOf(i[31]);

		return PersonInfoItemDefinition.createNewPersonInfoItemDefinition(perInfoItemDefId, perInfoCategoryId,
				itemParentCode, itemCode, itemName, isAbolition, isFixed, isRequired, systemRequired, requireChangable,
				resourceId, itemType, dataType, stringItemLength, stringItemDataType, stringItemType, numericItemMinus,
				numericItemAmount, numericItemIntegerPart, numericItemDecimalPart, numericItemMin, numericItemMax,
				dateItemType, timeItemMax, timeItemMin, timepointItemMin, timepointItemMax, selectionItemRefType,
				selectionItemRefCode, relatedCategoryCode, canAbolition, items, initValue);
	}

	private PersonInfoItemDefinition createDomainFromEntity1(Object[] i, List<String> items) {
		String perInfoItemDefId = String.valueOf(i[0]);
		String itemCode = String.valueOf(i[1]);
		String itemName = String.valueOf(i[2]);
		int isAbolition = Integer.parseInt(String.valueOf(i[3]));
		int isRequired = Integer.parseInt(String.valueOf(i[4]));
		String itemParentCode = (i[5] == null) ? null : String.valueOf(i[5]);
		int systemRequired = Integer.parseInt(String.valueOf(i[6]));
		int requireChangable = Integer.parseInt(String.valueOf(i[7]));
		int isFixed = Integer.parseInt(String.valueOf(i[8]));
		int itemType = Integer.parseInt(String.valueOf(i[9]));
		BigDecimal dataType = i[10] == null ? null : new BigDecimal(String.valueOf(i[10]));
		BigDecimal timeItemMin = i[11] == null ? null : new BigDecimal(String.valueOf(i[11]));
		BigDecimal timeItemMax = i[12] == null ? null : new BigDecimal(String.valueOf(i[12]));
		BigDecimal timepointItemMin = i[13] == null ? null : new BigDecimal(String.valueOf(i[13]));
		BigDecimal timepointItemMax = i[14] == null ? null : new BigDecimal(String.valueOf(i[14]));
		BigDecimal dateItemType = i[15] == null ? null : new BigDecimal(String.valueOf(i[15]));
		BigDecimal stringItemType = i[16] == null ? null : new BigDecimal(String.valueOf(i[16]));
		BigDecimal stringItemLength = i[17] == null ? null : new BigDecimal(String.valueOf(i[17]));
		BigDecimal stringItemDataType = i[18] == null ? null : new BigDecimal(String.valueOf(i[18]));
		BigDecimal numericItemMin = i[19] == null ? null : new BigDecimal(String.valueOf(i[19]));
		BigDecimal numericItemMax = i[20] == null ? null : new BigDecimal(String.valueOf(i[20]));
		BigDecimal numericItemAmount = i[21] == null ? null : new BigDecimal(String.valueOf(i[21]));
		BigDecimal numericItemMinus = i[22] == null ? null : new BigDecimal(String.valueOf(i[22]));
		BigDecimal numericItemDecimalPart = i[23] == null ? null : new BigDecimal(String.valueOf(i[23]));
		BigDecimal numericItemIntegerPart = i[24] == null ? null : new BigDecimal(String.valueOf(i[24]));
		BigDecimal selectionItemRefType = i[25] == null ? null : new BigDecimal(String.valueOf(i[25]));
		String selectionItemRefCode = i[26] == null ? "" : String.valueOf(i[26]);
		String perInfoCategoryId = String.valueOf(i[27]);

		String relatedCategoryCode = String.valueOf(i[28]);
		String resourceId = i[29] == null ? null : String.valueOf(i[29]);
		int canAbolition = Integer.parseInt(String.valueOf(i[30]));
		String initValue = i[31] == null ? null : String.valueOf(i[31]);

		return PersonInfoItemDefinition.createNewPersonInfoItemDefinition(perInfoItemDefId, perInfoCategoryId,
				itemParentCode, itemCode, itemName, isAbolition, isFixed, isRequired, systemRequired, requireChangable,
				resourceId, itemType, dataType, stringItemLength, stringItemDataType, stringItemType, numericItemMinus,
				numericItemAmount, numericItemIntegerPart, numericItemDecimalPart, numericItemMin, numericItemMax,
				dateItemType, timeItemMax, timeItemMin, timepointItemMin, timepointItemMax, selectionItemRefType,
				selectionItemRefCode, relatedCategoryCode, canAbolition, items, initValue);
	}

	private PersonInfoItemDefinition toDomain(Object[] i) {
		return PersonInfoItemDefinition.createFromJavaType(String.valueOf(i[1]), String.valueOf(i[0]));
	}

	private PersonInfoItemDefinition toDomainWithCodeAndName(Object[] i) {
		return PersonInfoItemDefinition.createFromEntityWithCodeAndName(String.valueOf(i[0]), String.valueOf(i[1]),
				Integer.parseInt(i[2].toString()));
	}

	private PpemtItem createPerInfoItemDefFromDomain(PersonInfoItemDefinition perInfoItemDef) {
		PpemtPerInfoItemPK perInfoItemPK = new PpemtPerInfoItemPK(perInfoItemDef.getPerInfoItemDefId());
		return new PpemtItem(perInfoItemPK, perInfoItemDef.getPerInfoCategoryId(),
				perInfoItemDef.getItemCode().v(), perInfoItemDef.getItemName().v(),
				perInfoItemDef.getIsAbolition().value, perInfoItemDef.getIsRequired().value,
				perInfoItemDef.getInitValue() == null? null:
				(perInfoItemDef.getInitValue().isPresent()? perInfoItemDef.getInitValue().get().toString() : null));
	}

	private PpemtItem createPerInfoItemDefFromDomainWithCtgId(PersonInfoItemDefinition perInfoItemDef,
			String perInfoCtgId) {
		PpemtPerInfoItemPK perInfoItemPK = new PpemtPerInfoItemPK(IdentifierUtil.randomUniqueId());
		return new PpemtItem(perInfoItemPK, perInfoCtgId, perInfoItemDef.getItemCode().v(),
				perInfoItemDef.getItemName().v(), perInfoItemDef.getIsAbolition().value,
				perInfoItemDef.getIsRequired().value, perInfoItemDef.getInitValue() == null? null:
					(perInfoItemDef.getInitValue().isPresent()? perInfoItemDef.getInitValue().get().toString() : null));
	}

	private PpemtItemCommon createPerInfoItemDefCmFromDomain(PersonInfoItemDefinition perInfoItemDef,
			String contractCd, String ctgCode) {
		PpemtPerInfoItemCmPK perInfoItemCmPK = new PpemtPerInfoItemCmPK(contractCd, ctgCode,
				perInfoItemDef.getItemCode().v());

		int itemType = perInfoItemDef.getItemTypeState().getItemType().value;
		BigDecimal dataType = null;
		BigDecimal timeItemMin = null;
		BigDecimal timeItemMax = null;
		BigDecimal timepointItemMin = null;
		BigDecimal timepointItemMax = null;
		BigDecimal dateItemType = null;
		BigDecimal stringItemType = null;
		BigDecimal stringItemLength = null;
		BigDecimal stringItemDataType = null;
		BigDecimal numericItemMin = null;
		BigDecimal numericItemMax = null;
		BigDecimal numericItemAmountAtr = null;
		BigDecimal numericItemMinusAtr = null;
		BigDecimal numericItemDecimalPart = null;
		BigDecimal numericItemIntegerPart = null;
		BigDecimal selectionItemRefType = null;
		String selectionItemRefCode = null;
		String relatedCategoryCode = null;
		String resourceId = perInfoItemDef.getResourceId() != null
				? (perInfoItemDef.getResourceId().isPresent() ? perInfoItemDef.getResourceId().get() : null)
				: null;

		if (itemType == ItemType.SINGLE_ITEM.value) {
			SingleItem singleItem = (SingleItem) perInfoItemDef.getItemTypeState();
			DataTypeState dataTypeState = singleItem.getDataTypeState();
			dataType = BigDecimal.valueOf(dataTypeState.getDataTypeValue().value);
			switch (dataTypeState.getDataTypeValue()) {
			case STRING:
				StringItem stringItem = (StringItem) dataTypeState;
				stringItemType = new BigDecimal(stringItem.getStringItemType().value);
				stringItemLength = new BigDecimal(stringItem.getStringItemLength().v());
				stringItemDataType = new BigDecimal(stringItem.getStringItemDataType().value);
				break;
			case NUMERIC:
				NumericItem numericItem = (NumericItem) dataTypeState;
				numericItemMin = numericItem.getNumericItemMin() != null ? numericItem.getNumericItemMin().v() : null;
				numericItemMax = numericItem.getNumericItemMax() != null ? numericItem.getNumericItemMax().v() : null;
				numericItemAmountAtr = new BigDecimal(numericItem.getNumericItemAmount().value);
				numericItemMinusAtr = new BigDecimal(numericItem.getNumericItemMinus().value);
				numericItemDecimalPart = numericItem.getDecimalPart() == null? null: new BigDecimal(numericItem.getDecimalPart().v());
				numericItemIntegerPart = new BigDecimal(numericItem.getIntegerPart().v());
				break;
			case DATE:
				DateItem dateItem = (DateItem) dataTypeState;
				dateItemType = new BigDecimal(dateItem.getDateItemType().value);
				break;
			case TIME:
				TimeItem timeItem = (TimeItem) dataTypeState;
				timeItemMin = new BigDecimal(timeItem.getMin().v());
				timeItemMax = new BigDecimal(timeItem.getMax().v());
				break;
			case TIMEPOINT:
				TimePointItem timePointItem = (TimePointItem) dataTypeState;
				timepointItemMin = new BigDecimal(timePointItem.getTimePointItemMin().v());
				timepointItemMax = new BigDecimal(timePointItem.getTimePointItemMax().v());
				break;
			case SELECTION:
				SelectionItem selectionItem = (SelectionItem) dataTypeState;
				ReferenceTypeState rtypeState = selectionItem.getReferenceTypeState();
				selectionItemRefType = new BigDecimal(rtypeState.getReferenceType().value);
				if (rtypeState.getReferenceType() == ReferenceTypes.DESIGNATED_MASTER) {
					MasterReferenceCondition masterref = (MasterReferenceCondition) rtypeState;
					selectionItemRefCode = masterref.getMasterType().v();
				} else if (rtypeState.getReferenceType() == ReferenceTypes.CODE_NAME) {
					CodeNameReferenceType codeNameRef = (CodeNameReferenceType) rtypeState;
					selectionItemRefCode = codeNameRef.getTypeCode().v();
				} else {
					EnumReferenceCondition enumRef = (EnumReferenceCondition) rtypeState;
					selectionItemRefCode = enumRef.getEnumName().v();
				}
				break;
			case SELECTION_RADIO:
				// SelectionRadio selectionRadio = (SelectionRadio) dataTypeState;
				break;
			case SELECTION_BUTTON:
				// SelectionButton selectionButton = (SelectionButton) dataTypeState;
				break;
			case READONLY:
				ReadOnly readOnly = (ReadOnly) dataTypeState;
				selectionItemRefCode = readOnly.getReadText().v();
				break;
			case RELATE_CATEGORY:
				RelatedCategory relatedCtg = (RelatedCategory) dataTypeState;
				relatedCategoryCode = relatedCtg.getRelatedCtgCode().v();
				break;
			case NUMBERIC_BUTTON:
				NumericButton numericButton = (NumericButton) dataTypeState;
				numericItemMin = numericButton.getNumericItemMin() != null ? numericButton.getNumericItemMin().v()
						: null;
				numericItemMax = numericButton.getNumericItemMax() != null ? numericButton.getNumericItemMax().v()
						: null;
				numericItemAmountAtr = new BigDecimal(numericButton.getNumericItemAmount().value);
				numericItemMinusAtr = new BigDecimal(numericButton.getNumericItemMinus().value);
				numericItemDecimalPart = new BigDecimal(numericButton.getDecimalPart().v());
				numericItemIntegerPart = new BigDecimal(numericButton.getIntegerPart().v());
				break;
			case READONLY_BUTTON:
				ReadOnlyButton readOnlyButton = (ReadOnlyButton) dataTypeState;
				selectionItemRefCode = readOnlyButton.getReadText().v();
				break;
			}
		}
		String itemParentCode = (perInfoItemDef.getItemParentCode() == null
				|| perInfoItemDef.getItemParentCode().v().isEmpty()) ? null : perInfoItemDef.getItemParentCode().v();

		return new PpemtItemCommon(perInfoItemCmPK, itemParentCode, perInfoItemDef.getSystemRequired().value,
				perInfoItemDef.getRequireChangable().value, perInfoItemDef.getIsFixed().value, itemType, dataType,
				timeItemMin, timeItemMax, timepointItemMin, timepointItemMax, dateItemType, stringItemType,
				stringItemLength, stringItemDataType, numericItemMin, numericItemMax, numericItemAmountAtr,
				numericItemMinusAtr, numericItemDecimalPart, numericItemIntegerPart, selectionItemRefType,
				selectionItemRefCode, relatedCategoryCode, resourceId, perInfoItemDef.isCanAbolition() == true? 1: 0);
	}
	// Sonnlb Code start

	
	private PerInfoItemDefOrder toDomainItemOrder(PpemtItemSort entity) {
		return new PerInfoItemDefOrder(String.valueOf(entity.ppemtPerInfoItemPK.perInfoItemDefId),
				String.valueOf(entity.perInfoCtgId), Integer.parseInt(String.valueOf(entity.disporder)),
				Integer.parseInt(String.valueOf(entity.displayOrder)));
	}
	
	@Override
	public void updatePerInfoItemDef(PersonInfoItemDefinition perInfoItemDef) {
		this.commandProxy().update(createPerInfoItemDefFromDomain(perInfoItemDef));
	}

	@Override
	public String getItemDefaultName(String categoryCd, String itemCd) {
		String cid = AppContexts.user().contractCode() + "-0000";
		return queryProxy().query(SELECT_DEFAULT_ITEM_NAME_BY_ITEMS_CODE, String.class)
				.setParameter("categoryCd", categoryCd).setParameter("itemCd", itemCd).setParameter("cid", cid).getSingleOrNull();
	}

	private PerInfoItemDefOrder createPerInfoItemDefOrderFromEntity(PpemtItemSort order) {
		return PerInfoItemDefOrder.createFromJavaType(order.ppemtPerInfoItemPK.perInfoItemDefId, order.perInfoCtgId,
				order.disporder, order.displayOrder);
	}

	@Override
	public List<PersonInfoItemDefinition> getAllPerInfoItemDefByCategoryIdWithoutSetItem(String perInfoCtgId,
			String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_BY_CATEGORY_ID_WITHOUT_SETITEM_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					List<String> items = getChildIds(contractCd, perInfoCtgId, String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public List<PersonInfoItemDefinition> getAllPerInfoItemDefByCategoryIdWithoutAbolition(String perInfoCtgId,
			String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_BY_CATEGORY_ID_WITHOUT_ABOLITION_AND_SETITEM_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					List<String> items = getChildIds(contractCd, perInfoCtgId, String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public void UpdateOrderItem(PerInfoItemDefOrder itemOrder) {
		this.commandProxy().update(createItemOrder(itemOrder.getPerInfoItemDefId(), itemOrder.getPerInfoCtgId(),
				itemOrder.getDispOrder().v(), itemOrder.getDisplayOrder().v()));
	}

	@Override
	public Optional<PerInfoItemDefOrder> getPerInfoItemDefOrdersByItemId(String perInfoItemDefId) {
		return this.queryProxy().query(SELECT_ITEM_ORDER_BY_ITEM_ID_QUERY, PpemtItemSort.class)
				.setParameter("perInfoItemDefId", perInfoItemDefId)
				.getSingle(o -> createPerInfoItemDefOrderFromEntity(o));
	}

	@Override
	public List<PersonInfoItemDefinitionSimple> getRequiredItemFromCtgCdLst(String contractCd, String companyId,
			List<String> categoryCds) {
		List<PersonInfoItemDefinitionSimple> results = new ArrayList<>();
		CollectionUtil.split(categoryCds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(SEL_REQUIRED_ITEM_BY_CTG, Object[].class).setParameter("contractCd", contractCd)
				.setParameter("companyId", companyId).setParameter("categoryCds", subList).getList().stream()
				.map(x -> toSimpleItem(x)).collect(Collectors.toList()));
		});
		return results;
	}

	// Sonnlb Code end

	private PersonInfoItemDefinitionSimple toSimpleItem(Object[] i) {
		return new PersonInfoItemDefinitionSimple(i[0].toString(), i[1].toString());
	}

	@Override
	public int countPerInfoItemDefInCategory(String perInfoCategoryId, String companyId) {
		Optional<Long> a = this.queryProxy().query(COUNT_ITEMS_IN_CATEGORY, Long.class)
				.setParameter("companyId", companyId).setParameter("perInfoCtgId", perInfoCategoryId).getSingle();
		return a.isPresent() ? a.get().intValue() : 0;
	}

	@Override
	public int countPerInfoItemDefInCategoryNo812(String perInfoCategoryId, String companyId) {
		Optional<Long> a = this.queryProxy().query(COUNT_ITEMS_IN_CATEGORY_NO812, Long.class)
				.setParameter("companyId", companyId).setParameter("perInfoCtgId", perInfoCategoryId).getSingle();
		return a.isPresent() ? a.get().intValue() : 0;
	}

	@Override
	public List<PersonInfoItemDefinition> getPerInfoItemByCtgIdAndOrder(String perInfoCategoryId, String companyId,
			String contractCd) {
		return this.queryProxy().query(SELECT_PER_ITEM_BY_CTG_ID_AND_ORDER, Object[].class)
				.setParameter("companyId", companyId).setParameter("perInfoCtgId", perInfoCategoryId).getList(i -> {
					return PersonInfoItemDefinition.createFromEntityMap(String.valueOf(i[0]), perInfoCategoryId,
							String.valueOf(i[1]));
				});
	}

	@Override
	public List<PersonInfoItemDefinition> getPerInfoItemByCtgId(String perInfoCategoryId, String companyId,
			String contractCode) {
		return this.queryProxy().query(SELECT_PERINFOITEM_BYCTGID, Object[].class).setParameter("companyId", companyId)
				.setParameter("perInfoCtgId", perInfoCategoryId).getList(i -> {
					return PersonInfoItemDefinition.createFromEntityMap(String.valueOf(i[0]), perInfoCategoryId,
							String.valueOf(i[1]));
				});
	}

	// vinhpx end
	/**
	 * getNotFixedPerInfoItemDefByCategoryId
	 * 
	 * @param perInfoCategoryId
	 * @param contractCd
	 * @return
	 */
	@Override
	public List<PersonInfoItemDefinition> getNotFixedPerInfoItemDefByCategoryId(String perInfoCtgId,
			String contractCd) {
		return this.queryProxy().query(SELECT_NOT_FIXED_ITEMS_BY_CATEGORY_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCtgId).getList(i -> {
					List<String> items = getChildIds(contractCd, perInfoCtgId, String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public boolean checkExistedSelectionItemId(String selectionItemId) {
		List<PpemtItemCommon> itemCm = this.queryProxy().query(SEL_ITEM_BY_SELECTIONS, PpemtItemCommon.class)
				.setParameter("selectionItemId", selectionItemId).getList();
		return itemCm.size() > 0;
	}

	@Override
	public List<PersonInfoItemDefinition> getAllItemUsedByCtgId(List<String> ctgId) {
		List<PersonInfoItemDefinition> results = new ArrayList<>();
		CollectionUtil.split(ctgId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(SEL_ITEM_USED, Object[].class).setParameter("perInfoCtgId", subList)
				.getList(i -> toDomain(i)));
		});
		return results;
	}

	@Override
	public List<PersonInfoItemDefinition> getAllItemByCtgWithAuth(String perInfoCategoryId, String contractCd,
			String roleId, boolean isSelfRef) {
		String query = String.join(" ", SELECT_ITEM_BY_CTG_WITH_AUTH, "AND",
				(isSelfRef ? " au.selfAuthType != 1 " : " au.otherPersonAuthType != 1 "), "AND", COMMON_CONDITION);

		return this.queryProxy().query(query, Object[].class).setParameter("contractCd", contractCd)
				.setParameter("perInfoCtgId", perInfoCategoryId).setParameter("roleId", roleId).getList(i -> {
					List<String> items = getChildIds(contractCd, perInfoCategoryId, String.valueOf(i[1]));
					return createDomainFromEntity1(i, items);
				});
	}

	@Override
	public List<String> getAllRequiredIds(String contractCd, String companyId) {
		return this.queryProxy().query(SELECT_REQUIRED_ITEMS_ID_BY_CID, String.class)
				.setParameter("contractCd", contractCd).setParameter("companyId", companyId).getList();
	}

	@Override
	public List<String> getAllRequiredIdsByCtgId(String contract, String ctgId) {
		return this.queryProxy().query(SELECT_REQUIRED_ITEMS_ID_BY_CTG_ID, String.class)
				.setParameter("contractCd", contract).setParameter("perInfoCtgId", ctgId).getList();
	}

	@Override
	public List<PersonInfoItemDefinition> getPerInfoItemByCtgCd(String ctgCd, String companyId) {
		return this.queryProxy().query(SELECT_SIMPLE_ITEM_DEF, Object[].class).setParameter("ctgCd", ctgCd)
				.setParameter("cid", companyId).getList(x -> toDomainWithCodeAndName(x));
	}

	@Override
	public List<String> getAllItemIdsByCtgCode(String cid, String ctgId) {
		return this.queryProxy().query(SELECT_ITEMS_ID_BY_CTG_ID, String.class).setParameter("cid", cid)
				.setParameter("ctgCode", ctgId).getList();
	}

	@Override
	public void updateItemDefNameAndAbolition(List<PersonInfoItemDefinition> lst, String companyId) {
		lst.forEach(x -> {
			Optional<PpemtItem> entityOpt = this.queryProxy()
					.find(new PpemtPerInfoItemPK(x.getPerInfoItemDefId()), PpemtItem.class);
			if (entityOpt.isPresent()) {
				PpemtItem entity = entityOpt.get();
				if (x.getIsAbolition() != null) {
					entity.abolitionAtr = x.getIsAbolition().value;
				}
				if (x.getItemName() != null) {
					entity.itemName = x.getItemName().v();
				}
				this.commandProxy().update(entity);
			}
		});
	}

	@Override
	public List<PersonInfoItemDefinition> getItemDefByCtgCdAndComId(String perInfoCtgCd, String CompanyId) {
		return this.queryProxy().query(SELECT_ITEM_BY_CTGID_AND_COMID, Object[].class)
				.setParameter("ctgCd", perInfoCtgCd).setParameter("cid", CompanyId)
				.getList(x -> PersonInfoItemDefinition.createDomainWithNameAndAbolition(x[0].toString(),
						x[1].toString(), x[2].toString()));
	}

	@Override
	public List<PersonInfoItemDefinition> getItemLstByListId(List<String> listItemDefId, String contractCd, String companyId, List<String> categoryCodeLst) {
		List<String> itemCodeAll = new ArrayList<>();
		List<String> itemCodeChilds = new ArrayList<>(); 
		CollectionUtil.split(listItemDefId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			itemCodeChilds.addAll(this.queryProxy().query(SELECT_ITEM_CD_BY_ITEM_ID_QUERY, String.class)
				.setParameter("contractCd", contractCd).setParameter("listItemDefId", subList).getList());
		});
		if(!itemCodeChilds.isEmpty()) {
			List<String> itemCodeChildChilds = this.queryProxy().query(SELECT_ITEM_CD_BY_ITEM_CD_QUERY, String.class)
					.setParameter("contractCd", contractCd).setParameter("itemCdLst", itemCodeChilds).getList();
			itemCodeAll.addAll(itemCodeChilds);
			if(!itemCodeChildChilds.isEmpty()) 
				itemCodeAll.addAll(itemCodeChildChilds);
		}
		if(!itemCodeAll.isEmpty()) {
			List<Object[]> results = new ArrayList<>();
			CollectionUtil.split(categoryCodeLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstCat -> {
				CollectionUtil.split(itemCodeAll, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstCodes -> {
					results.addAll(this.queryProxy().query(SELECT_CHILD_ITEMS_BY_ITEM_CD_QUERY, Object[].class)
					.setParameter("contractCd", contractCd)
					.setParameter("itemCdLst", lstCodes)
					.setParameter("cid", companyId)
					.setParameter("ctgLst", lstCat)
					.getList());
				});
			});
			results.sort(SORT_BY_DISPORDER);
			return results.stream().map(c -> {return createDomainFromEntity1(c, null);}).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public String getItemName(String contractCode, String companyId, String categoryCode, String itemCode) {
		return this.queryProxy()
				.query(SELECT_ITEM_NAME_QUERY, String.class)
				.setParameter("contractCd", contractCode)
				.setParameter("cid", companyId)
				.setParameter("categoryCd", companyId)
				.setParameter("itemCd", itemCode)
				.getSingle().orElse("not itemName");
	}
	
	@Override
	public String getItemDfId(String categoryId, String itemCode) {
		return this.queryProxy()
				.query(SELECT_ITEM_DF_ID_QUERY, String.class)
				.setParameter("categoryId", categoryId)
				.setParameter("itemCd", itemCode)
				.getSingle().orElse("not itemName");
	}

	@Override

	public List<PersonInfoItemDefinition> getAllItemId(List<String> ctgIdLst, List<String> itemCodeLst) {
		List<PersonInfoItemDefinition> itemIdLst = new ArrayList<>();
		CollectionUtil.split(ctgIdLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, ctgIds -> {
			CollectionUtil.split(itemCodeLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, codes -> {
				itemIdLst.addAll(this.queryProxy().query(SEL_ITEM_EVENT, Object[].class)
									.setParameter("perInfoCtgId", ctgIds)
									.setParameter("itemCd", codes)
									.getList(c -> toDomain(c)));
			});
		});
		return itemIdLst;
	}
	

	@Override
	public void updateAbolitionItem(List<PersonInfoItemDefinition>itemLst){
		itemLst.stream().forEach(c ->{
			Optional<PpemtItem> entityOpt = this.queryProxy()
					.find(new PpemtPerInfoItemPK(c.getPerInfoItemDefId()), PpemtItem.class);
			if (entityOpt.isPresent()) {
				PpemtItem entity = entityOpt.get();
				if (c.getIsAbolition() != null) {
					entity.abolitionAtr = c.getIsAbolition().value;
				}
				this.commandProxy().update(entity);
			}
		});
	}

	public List<PersonInfoItemDefinition> getItemLstByListId(List<String> listItemDefId, String ctgId,
			String categoryCd, String contractCd) {
		List<String> itemCodeAll = new ArrayList<>();
		List<String> itemCodeChilds = new ArrayList<>();
		CollectionUtil.split(listItemDefId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			itemCodeChilds.addAll(this.queryProxy().query(SELECT_ITEM_CD_BY_ITEM_ID_QUERY, String.class)
				.setParameter("contractCd", contractCd).setParameter("listItemDefId", subList).getList());
		});
		if (!itemCodeChilds.isEmpty()) {
			List<String> itemCodeChildChilds = this.queryProxy().query(SELECT_ITEM_CD_BY_ITEM_CD_QUERY, String.class)
					.setParameter("contractCd", contractCd).setParameter("itemCdLst", itemCodeChilds).getList();
			itemCodeAll.addAll(itemCodeChilds);
			if (!itemCodeChildChilds.isEmpty())
				itemCodeAll.addAll(itemCodeChildChilds);
		}
		if (!itemCodeAll.isEmpty()) {
			List<Object[]> entities = new ArrayList<>();
			CollectionUtil.split(itemCodeAll, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				entities.addAll(this.queryProxy().query(SELECT_ALL_ITEMS_BY_ITEM_CD_QUERY, Object[].class)
					.setParameter("contractCd", contractCd)
					.setParameter("itemCdLst", subList)
					.setParameter("categoryCd", categoryCd)
					.setParameter("ctgId", ctgId)
					.getList());
			});
			entities.sort(SORT_BY_DISPORDER);
			return entities.stream().map(c -> { return createDomainFromEntity1(c, null); }).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public void updateOrderItem(List<PerInfoItemDefOrder> itemOrder) {
		List<PpemtItemSort> entityLst = itemOrder.stream().map(c -> {
			return createItemOrder(c.getPerInfoItemDefId(), c.getPerInfoCtgId(), c.getDispOrder().v(),
					c.getDisplayOrder().v());
		}).collect(Collectors.toList());
		if (entityLst.size() > 0) {
			this.commandProxy().updateAll(entityLst);
		}
	}

	@Override
	public List<PerInfoItemDefOrder> getItemOrderByCtgId(String ctgId) {
		return this.queryProxy().query(SELECT_ALL_DISORDER__BY_CTC_ID_QUERY, PpemtItemSort.class)
				.setParameter("perInfoCtgId", ctgId).getList( c -> toDomainItemOrder(c));
	}

	@Override
	public Optional<PersonInfoItemDefinition> getPerInfoItemDefByCtgCdItemCdCid(String categoryCode, String itemCd, String cid , String contractCd) {
		return this.queryProxy().query(SELECT_ITEMDF_BY_CTGCD_ITEMCD_CID, Object[].class)
				.setParameter("categoryCd", categoryCode)
				.setParameter("contractCd", contractCd)
				.setParameter("itemCd", itemCd)
				.setParameter("cid", cid).getSingle(i -> {
					List<String> items = getChildIds(contractCd, String.valueOf(i[27]), String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public Map<String, List<PersonInfoItemDefinition>> getByListCategoryIdWithoutAbolition(List<String> lstPerInfoCategoryId,
			String contractCd) {
		List<Object[]> lstObj = new ArrayList<>();
		CollectionUtil.split(lstPerInfoCategoryId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstObj.addAll(this.queryProxy().query(SELECT_ALL_REQUIREDITEM_BY_LIST_CATEGORY_ID, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("lstPerInfoCategoryId", subList).getList());
		});
		lstObj.sort( (o1, o2) -> {
			return ((int) o1[32]) - ((int) o2[32]); // index 32 for [disporder] 
		});
		
		// groupBy categoryId 
		Map<String, List<Object[]>> perInfoItemDefByList = lstObj.stream().collect(Collectors.groupingBy(x -> String.valueOf(x[31])));
		
		// Map to List
		Map<String, List<PersonInfoItemDefinition>> result = perInfoItemDefByList.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> {
					List<Object[]> listItem = e.getValue();
					return listItem.stream().map(item-> {
						return createDomainFromEntity(item, getChildIds(AppContexts.user().contractCode(),
								String.valueOf(item[27]), String.valueOf(item[1])));
					}).collect(Collectors.toList());
					
				}));
		
		return result;
	}

	@Override
	public Map<String, List<ItemBasicInfo>> getItemCDByListCategoryIdWithAbolition(List<String> lstPerInfoCategoryId,
			String contractCd) {
		
		Map<String, List<ItemBasicInfo>> result =  new HashMap<>();
		
		
		if (lstPerInfoCategoryId.isEmpty()){
			return result;
		}
		
		List<Object[]> lstObj = new ArrayList<>();
		CollectionUtil.split(lstPerInfoCategoryId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstObj.addAll(this.queryProxy().query(SELECT_REQUIRED_ITEM, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("lstPerInfoCategoryId", subList).getList());
		});
		
		// groupBy categoryId 
		Map<String, List<Object[]>> perInfoItemDefByList = lstObj.stream().collect(Collectors.groupingBy(x -> String.valueOf(x[1])));
		
		// Map to List
		result = perInfoItemDefByList.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> {
					List<Object[]> listItem = e.getValue();
					return listItem.stream()
							.map(x -> new ItemBasicInfo(String.valueOf(x[0]), String.valueOf(x[2]),
									String.valueOf(x[3]), Integer.parseInt(String.valueOf(x[4])), Integer.parseInt(String.valueOf(x[5]))))
							.collect(Collectors.toList());
				}));

		return result;
	}

	/**
	 * Lấy ra danh sách item trong combobox của màn CPS002.B
	 */
	@Override
	public List<PersonInfoItemDefinition> getItemLstByListIdForCPS002B(List<String> listItemDefId, String contractCd,
			String companyId, List<String> categoryCodeLst) {
		
//		List<PersonInfoItemDefinition> result = new ArrayList<>();
		List<String> itemCodeAll = new ArrayList<>();
		List<String> itemCodeChilds = new ArrayList<>();
		CollectionUtil.split(listItemDefId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			itemCodeChilds.addAll(this.queryProxy().query(GET_LIST_ITEM_FOR_CPS002B, String.class)
				.setParameter("contractCd", contractCd).setParameter("listItemDefId", subList).getList());
		});
		if(!itemCodeChilds.isEmpty()) {
			List<String> itemCodeChildChilds = new ArrayList<>();
			CollectionUtil.split(itemCodeChilds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				itemCodeChildChilds.addAll(this.queryProxy().query(SELECT_ITEM_CD_BY_ITEM_CD_QUERY, String.class)
					.setParameter("contractCd", contractCd).setParameter("itemCdLst", subList).getList());
			});
			itemCodeAll.addAll(itemCodeChilds);
			if(!itemCodeChildChilds.isEmpty()) 
				itemCodeAll.addAll(itemCodeChildChilds);
		}
		if(!itemCodeAll.isEmpty()) {
			List<PersonInfoItemDefinition> lstItemDf = new ArrayList<>();
			CollectionUtil.split(categoryCodeLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				lstItemDf.addAll(this.queryProxy().query(SELECT_CHILD_ITEMS_BY_ITEM_CD_QUERY, Object[].class)
					.setParameter("contractCd", contractCd)
					.setParameter("itemCdLst", itemCodeAll)
					.setParameter("cid", companyId)
					.setParameter("ctgLst", subList)
					.getList(c -> {return createDomainFromEntity1(c, null);}));
			});
			return lstItemDf.stream().filter(i -> i.getIsAbolition().value == 0).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> getAllItemIdsByCtgCodeAndItemCd(String cid, List<String> ctgCodes, List<String> itemCds) {
		List<String> itemIds = this.getEntityManager()
				.createQuery(SEL_ITEM_ID_BY_CTG_CD_AND_ITEM_CD, String.class)
				.setParameter("itemCd", itemCds)
				.setParameter("cid", cid)
				.setParameter("categoryCd", ctgCodes)
				.getResultList();
		return itemIds;

	}

	/**
	 * Query get nameItem for CPS003F
	 */
	@Override
	public Map<String, String> getNamesByCodes(List<String> itemCodes) {
		Map<String, String> mapValues = new HashMap<String, String>();
		String NATIV_SQL = String.join(" ", "SELECT DISTINCT it.ITEM_CD, it.ITEM_NAME",
				"FROM PPEMT_ITEM it",
				"LEFT JOIN PPEMT_CTG ctg", 
				"ON it.PER_INFO_CTG_ID = ctg.PER_INFO_CTG_ID",
				"WHERE ctg.CID = '{cid}' AND it.ITEM_CD LIKE 'IS%' AND ITEM_CD IN ('{iids}')"); //IS00020', 'IS00279', 'IS00253
		
		NATIV_SQL = NATIV_SQL.replaceAll("\\{cid\\}", AppContexts.user().companyId()).replaceAll("\\{iids\\}", String.join("','", itemCodes));
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = getEntityManager().createNativeQuery(NATIV_SQL).getResultList();
		
		result.forEach(f -> {
			mapValues.put(f[0].toString(), f[1].toString());
		});
		
		return mapValues;
	}
	
	@Override
	public List<PersonInfoItemDefinition> getAllPerInfoItemDefByCategoryIdCPS013(String perInfoCategoryId,
			String contractCd) {
		return this.queryProxy().query(SELECT_ITEMS_BY_CATEGORY_ID_QUERY_CPS013, Object[].class)
				.setParameter("contractCd", contractCd)
				.setParameter("perInfoCtgId", perInfoCategoryId).getList(i -> {
					List<String> items = getChildIds(contractCd, perInfoCategoryId, String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public List<PersonInfoItemDefinition> getItemsByCtgCdItemCdsCid(String categoryCode, List<String> itemCds,
			String cid, String contractCd) {
		return this.queryProxy().query(SELECT_ITEMDF_BY_CTGCD_ITEMCDS_CID, Object[].class)
				.setParameter("categoryCd", categoryCode)
				.setParameter("contractCd", contractCd)
				.setParameter("itemCd", itemCds)
				.setParameter("cid", cid).getList(i -> {
					List<String> items = getChildIds(contractCd, String.valueOf(i[27]), String.valueOf(i[1]));
					return createDomainFromEntity(i, items);
				});
	}

	@Override
	public List<PersonInfoItemDefinition> findByIDandIsAbolition(String perInfoCtgId, int abolitionAtr) {
		// TODO Auto-generated method stub
		return null;
	}
}

