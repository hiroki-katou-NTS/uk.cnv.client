package nts.uk.ctx.at.schedule.infra.repository.shift.management.shiftPalette;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.schedule.dom.shift.management.shiftPalette.ShiftPaletteCombinations;
import nts.uk.ctx.at.schedule.dom.shift.management.shiftPalette.ShiftPaletteOrg;
import nts.uk.ctx.at.schedule.dom.shift.management.shiftPalette.ShiftPaletteOrgRepository;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteOrg;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteOrgCombi;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteOrgCombiDtl;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteOrgCombiDtlPk;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteOrgCombiPk;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteOrgPk;
import nts.uk.ctx.at.schedule.infra.repository.shift.management.ShifutoparettoWorkPlace;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author phongtq
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaShiftPaletteOrgRepository extends JpaRepository implements ShiftPaletteOrgRepository {

	private static final String SELECT;

	private static final String FIND_BY_PAGE;

	private static final String FIND_TO_DELETE;
	
	private static final String FIND_TO_DELETE_NEW;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a.CID, a.TARGET_UNIT, a.TARGET_ID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE,");
		builderString.append("b.POSITION, b.POSITION_NAME,");
		builderString.append("c.POSITION_ORDER, c.SHIFT_MASTER_CD");
		builderString.append(
				" FROM KSCMT_PALETTE_ORG a  JOIN KSCMT_PALETTE_ORG_COMBI b ON a.TARGET_UNIT = b.TARGET_UNIT AND a.TARGET_ID = b.TARGET_ID AND a.PAGE = b.PAGE ");
		builderString.append(
				" JOIN KSCMT_PALETTE_ORG_COMBI_DTL c ON b.TARGET_UNIT = c.TARGET_UNIT AND b.TARGET_ID = c.TARGET_ID AND b.PAGE = c.PAGE AND b.POSITION = c.POSITION ");
		SELECT = builderString.toString();

		builderString = new StringBuilder();
		builderString.append(SELECT);
		builderString.append("WHERE a.TARGET_UNIT = targetUnit AND a.TARGET_ID = 'targetId' AND a.PAGE = page");
		FIND_BY_PAGE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append(SELECT);
		builderString.append(" WHERE a.TARGET_ID = 'workplaceId' AND a.PAGE = page");
		FIND_TO_DELETE = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append(SELECT);
		builderString.append(" WHERE a.CID = 'cid' AND  a.TARGET_UNIT = targetUnit AND a.TARGET_ID = 'targetid' AND a.PAGE = 'page'");
		FIND_TO_DELETE_NEW = builderString.toString();

	}

	private static final String FIND_BY_TARGETID = "SELECT a.CID, a.TARGET_UNIT, a.TARGET_ID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE, b.POSITION, b.POSITION_NAME, c.POSITION_ORDER, c.SHIFT_MASTER_CD"
			+ " FROM KSCMT_PALETTE_ORG a  JOIN KSCMT_PALETTE_ORG_COMBI b ON a.TARGET_UNIT = b.TARGET_UNIT AND a.TARGET_ID = b.TARGET_ID AND a.PAGE = b.PAGE"
			+ " JOIN KSCMT_PALETTE_ORG_COMBI_DTL c ON b.TARGET_UNIT = c.TARGET_UNIT AND b.TARGET_ID = c.TARGET_ID AND b.PAGE = c.PAGE AND b.POSITION = c.POSITION"
			+ " WHERE a.TARGET_UNIT = targetUnit AND a.TARGET_ID = 'targetId'";
	
	private static final String FIND_BY_TARGETID_USE = "SELECT a.CID, a.TARGET_UNIT, a.TARGET_ID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE, b.POSITION, b.POSITION_NAME, c.POSITION_ORDER, c.SHIFT_MASTER_CD"
			+ " FROM KSCMT_PALETTE_ORG a  JOIN KSCMT_PALETTE_ORG_COMBI b ON a.TARGET_UNIT = b.TARGET_UNIT AND a.TARGET_ID = b.TARGET_ID AND a.PAGE = b.PAGE"
			+ " JOIN KSCMT_PALETTE_ORG_COMBI_DTL c ON b.TARGET_UNIT = c.TARGET_UNIT AND b.TARGET_ID = c.TARGET_ID AND b.PAGE = c.PAGE AND b.POSITION = c.POSITION"
			+ " WHERE a.TARGET_UNIT = targetUnit AND a.TARGET_ID = 'targetId' AND a.USE_ATR = 1";
	
	private static final String FIND_BY_CID = "SELECT a.CID, a.TARGET_UNIT, a.TARGET_ID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE, b.POSITION, b.POSITION_NAME, c.POSITION_ORDER, c.SHIFT_MASTER_CD"
			+ " FROM KSCMT_PALETTE_ORG a  JOIN KSCMT_PALETTE_ORG_COMBI b ON a.TARGET_UNIT = b.TARGET_UNIT AND a.TARGET_ID = b.TARGET_ID AND a.PAGE = b.PAGE"
			+ " JOIN KSCMT_PALETTE_ORG_COMBI_DTL c ON b.TARGET_UNIT = c.TARGET_UNIT AND b.TARGET_ID = c.TARGET_ID AND b.PAGE = c.PAGE AND b.POSITION = c.POSITION"
			+ " WHERE a.CID = 'cid'";

	@AllArgsConstructor
	@Getter
	private class FullShiftPalletsOrg {
		public String companyId;
		public int targetUnit;
		public String targetId;
		public int page;
		public String pageName;
		public boolean useAtr;
		public String note;
		public int position;
		public String positionName;
		public int positionOrder;
		public String shiftMasterCd;
	}

	@SneakyThrows
	private List<FullShiftPalletsOrg> createShiftPallets(ResultSet rs) {
		List<FullShiftPalletsOrg> listFullData = new ArrayList<>();
		while (rs.next()) {
			listFullData
					.add(new FullShiftPalletsOrg(rs.getString("CID"),
							rs.getString("TARGET_UNIT") != null ? Integer.valueOf(rs.getString("TARGET_UNIT")) : null,
							rs.getString("TARGET_ID"),
							rs.getString("PAGE") != null ? Integer.valueOf(rs.getString("PAGE")) : null,
							rs.getString("PAGE_NAME"), Integer.valueOf(rs.getString("USE_ATR")) == 1 ? true : false,
							rs.getString("NOTE"),
							rs.getString("POSITION") != null ? Integer.valueOf(rs.getString("POSITION")) : null,
							rs.getString("POSITION_NAME"), rs.getString("POSITION_ORDER") != null
									? Integer.valueOf(rs.getString("POSITION_ORDER")) : null,
							rs.getString("SHIFT_MASTER_CD")));
		}
		return listFullData;
	}

	private List<KscmtPaletteOrg> toEntity(List<FullShiftPalletsOrg> listFullJoin) {
		return listFullJoin.stream().collect(Collectors.groupingBy(FullShiftPalletsOrg::getPage)).entrySet().stream()
				.map(x -> {
					FullShiftPalletsOrg shiftPallets = x.getValue().get(0);
					KscmtPaletteOrgPk pk = new KscmtPaletteOrgPk(shiftPallets.getCompanyId(),
							shiftPallets.getTargetUnit(), shiftPallets.getTargetId(), shiftPallets.getPage());
					String pageName = shiftPallets.getPageName();
					boolean useAtr = shiftPallets.isUseAtr();
					String note = shiftPallets.getNote();
					List<KscmtPaletteOrgCombi> cmpCombis = x.getValue().stream()
							.collect(Collectors.groupingBy(FullShiftPalletsOrg::getPosition)).entrySet().stream()
							.map(y -> {
								KscmtPaletteOrgCombiPk combiPk = new KscmtPaletteOrgCombiPk(
										y.getValue().get(0).getCompanyId(), y.getValue().get(0).getTargetUnit(),
										y.getValue().get(0).getTargetId(), y.getValue().get(0).getPage(),
										y.getValue().get(0).getPosition());
								String positionName = y.getValue().get(0).getPositionName();
								List<KscmtPaletteOrgCombiDtl> cmpCombiDtls = y.getValue().stream()
										.collect(Collectors.groupingBy(FullShiftPalletsOrg::getPositionOrder))
										.entrySet().stream().map(z -> {
											KscmtPaletteOrgCombiDtlPk cmpCombiDtlPk = new KscmtPaletteOrgCombiDtlPk(
													z.getValue().get(0).getCompanyId(),
													z.getValue().get(0).getTargetUnit(),
													z.getValue().get(0).getTargetId(), z.getValue().get(0).getPage(),
													z.getValue().get(0).getPosition(),
													z.getValue().get(0).getPositionOrder());
											String shiftMasterCd = z.getValue().get(0).getShiftMasterCd();
											return new KscmtPaletteOrgCombiDtl(cmpCombiDtlPk, shiftMasterCd, null);
										}).collect(Collectors.toList());
								return new KscmtPaletteOrgCombi(combiPk, positionName, null, cmpCombiDtls);
							}).collect(Collectors.toList());
					return new KscmtPaletteOrg(pk, pageName, useAtr ? 1 : 0, note, cmpCombis);
				}).collect(Collectors.toList());
	}

	@Override
	public void add(ShiftPaletteOrg shiftPalletsOrg) {
		commandProxy().insert(KscmtPaletteOrg.fromDomain(shiftPalletsOrg));

	}

	@Override
	public void update(ShiftPaletteOrg shiftPalletsOrg) {

		Optional<KscmtPaletteOrg> getEntity = this
				.queryProxy().find(
						new KscmtPaletteOrgPk(AppContexts.user().companyId(),
								shiftPalletsOrg.getTargeOrg().getUnit().value,
								shiftPalletsOrg.getTargeOrg().getWorkplaceId().isPresent()
										? shiftPalletsOrg.getTargeOrg().getWorkplaceId().get()
										: shiftPalletsOrg.getTargeOrg().getWorkplaceGroupId().get(),
								shiftPalletsOrg.getPage()),
						KscmtPaletteOrg.class);

		if (getEntity.isPresent()) {
			getEntity.get().toEntity(shiftPalletsOrg);
			List<Integer> position = getEntity.get().orgCombis.stream().map(e -> e.pk.position)
					.collect(Collectors.toList());
			// delete position when right click choose 'delete'
			List<Integer> positionToDelete = getEntity.get().orgCombis.stream().map(e -> e.pk.position)
					.filter(item -> !shiftPalletsOrg.getShiftPallet().getCombinations().stream()
							.map(i -> i.getPositionNumber()).collect(Collectors.toList()).contains(item))
					.collect(Collectors.toList());
			List<ShiftPaletteCombinations> combinations = shiftPalletsOrg.getShiftPallet().getCombinations().stream()
					.filter(i -> !position.contains(i.getPositionNumber())).collect(Collectors.toList());
			List<ShifutoparettoWorkPlace> shifutoparettos = new ArrayList<>();
			List<KscmtPaletteOrgCombi> combis = combinations.stream()
					.map(i -> KscmtPaletteOrgCombi.fromOneDomain(i, getEntity.get().pk)).collect(Collectors.toList());
			shiftPalletsOrg.getShiftPallet().getCombinations().stream().forEach(i -> {
				shifutoparettos.addAll(i.getCombinations().stream()
						.map(o -> new ShifutoparettoWorkPlace(shiftPalletsOrg.getTargeOrg().getUnit().value,
								shiftPalletsOrg.getTargeOrg().getWorkplaceId().isPresent()
										? shiftPalletsOrg.getTargeOrg().getWorkplaceId().get()
										: shiftPalletsOrg.getTargeOrg().getWorkplaceGroupId().get(),
								shiftPalletsOrg.getPage(), i.getPositionNumber(), o.getOrder(), o.getShiftCode().v()))
						.collect(Collectors.toList()));
			});

			List<ShifutoparettoWorkPlace> shifutoparettoss = new ArrayList<>();
			List<ShifutoparettoWorkPlace> shifutoparettoToDelete = new ArrayList<>();
			Map<Integer, List<ShifutoparettoWorkPlace>> mapShifutoparetto = shifutoparettos.stream()
					.collect(Collectors.groupingBy(ShifutoparettoWorkPlace::getPosition));
			getEntity.get().orgCombis.stream().forEach(i -> {
				if (mapShifutoparetto.containsKey(i.pk.position)) {
					List<ShifutoparettoWorkPlace> shifutoparettoMap = mapShifutoparetto.get(i.pk.position);
					List<Integer> shipCodeFilter = i.orgCombiDtls.stream().map(e -> e.pk.positionOrder)
							.collect(Collectors.toList());
					shifutoparettoss.addAll(shifutoparettoMap.stream()
							.filter(o -> !shipCodeFilter.contains(o.positionOrder)).collect(Collectors.toList()));
					// when click 'clear' button, check to delete
					List<Integer> shipCodeFilterToDelete = shifutoparettoMap.stream().map(y -> y.positionOrder)
							.collect(Collectors.toList());
					shifutoparettoToDelete.addAll(
							i.orgCombiDtls.stream().filter(x -> !shipCodeFilterToDelete.contains(x.pk.positionOrder))
									.map(item -> new ShifutoparettoWorkPlace(i.pk.targetUnit, i.pk.targetId, i.pk.page,
											i.pk.position, item.pk.positionOrder, null))
									.collect(Collectors.toList()));
				}
			});
			List<Integer> positions = getEntity.get().orgCombis.stream().map(i -> i.pk.position)
					.collect(Collectors.toList());
			shifutoparettoss.addAll(
					shifutoparettos.stream().filter(i -> !positions.contains(i.position)).collect(Collectors.toList()));
			positionToDelete.stream().forEach(i -> {
				String combiDelete = "DELETE FROM KSCMT_PALETTE_ORG_COMBI WHERE CID = ? AND TARGET_UNIT = ? AND TARGET_ID = ? AND PAGE = ? AND POSITION = ? ";
				String dtlDelete = "DELETE FROM KSCMT_PALETTE_ORG_COMBI_DTL WHERE CID = ? AND TARGET_UNIT = ? AND TARGET_ID = ? AND PAGE = ? AND POSITION = ? ";

				try {
					PreparedStatement ps1 = this.connection().prepareStatement(combiDelete);
					ps1.setString(1, getEntity.get().pk.companyId);
					ps1.setInt(2, getEntity.get().pk.targetUnit);
					ps1.setString(3, getEntity.get().pk.targetId);
					ps1.setInt(4, shiftPalletsOrg.getPage());
					ps1.setInt(5, i);
					ps1.executeUpdate();

					PreparedStatement ps2 = this.connection().prepareStatement(dtlDelete);
					ps2.setString(1, getEntity.get().pk.companyId);
					ps2.setInt(2, getEntity.get().pk.targetUnit);
					ps2.setString(3, getEntity.get().pk.targetId);
					ps2.setInt(4, shiftPalletsOrg.getPage());
					ps2.setInt(5, i);
					ps2.executeUpdate();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});
			this.commandProxy().update(getEntity.get());
			List<KscmtPaletteOrgCombiDtl> cmpCombiDtls = shifutoparettoss
					.stream().map(i -> KscmtPaletteOrgCombiDtl.fromOneDomain(i.getTargetUnit(), i.getTargetId(),
							i.getPage(), i.getPosition(), i.getPositionOrder(), i.getShiftMasterCd()))
					.collect(Collectors.toList());
			if (!shifutoparettoToDelete.isEmpty()) {
				String dtlDelete = "DELETE FROM KSCMT_PALETTE_ORG_COMBI_DTL WHERE TARGET_UNIT = ? AND TARGET_ID = ? AND PAGE = ? AND POSITION = ? AND POSITION_ORDER = ?";
				shifutoparettoToDelete.stream().forEach(e -> {
					try {
						PreparedStatement ps = this.connection().prepareStatement(dtlDelete);
						ps.setInt(1, e.getTargetUnit());
						ps.setString(2, e.getTargetId());
						ps.setInt(3, e.getPage());
						ps.setInt(4, e.getPosition());
						ps.setInt(5, e.getPositionOrder());
						ps.executeUpdate();
					} catch (Exception ex) {
					}
				});
			}
			if (!combis.isEmpty()) {
				this.commandProxy().insertAll(combis);
			}
			if (!cmpCombiDtls.isEmpty()) {
				this.commandProxy().insertAll(cmpCombiDtls);
			}
		}

	}

	@Override
	public void delete(ShiftPaletteOrg shiftPalletsOrg) {
		String query = FIND_BY_PAGE;
		query = query.replaceFirst("targetUnit", String.valueOf(shiftPalletsOrg.getTargeOrg().getUnit().value));
		query = query.replaceFirst("targetId", shiftPalletsOrg.getTargeOrg().getWorkplaceId().get());
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			KscmtPaletteOrg kscmtPaletteOrg = toEntity(createShiftPallets(rs)).get(0);
			commandProxy().remove(KscmtPaletteOrg.class, kscmtPaletteOrg.pk);
			this.getEntityManager().flush();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Optional<ShiftPaletteOrg> findShiftPalletOrg(int targetUnit, String targetId, int page) {
		String query = FIND_BY_PAGE;
		query = query.replaceFirst("targetUnit", String.valueOf(targetUnit));
		query = query.replaceFirst("targetId", targetId);
		query = query.replaceFirst("page", String.valueOf(page));
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			List<ShiftPaletteOrg> shiftPalletsOrgs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());
			if (shiftPalletsOrgs.isEmpty())
				return Optional.empty();
			return Optional.of(shiftPalletsOrgs.get(0));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<ShiftPaletteOrg> findbyWorkPlaceId(int targetUnit, String workplaceId) {
		String query = FIND_BY_TARGETID;
		query = query.replaceFirst("targetUnit", String.valueOf(targetUnit));
		query = query.replaceFirst("targetId", workplaceId);

		try (PreparedStatement statement = this.connection().prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			List<ShiftPaletteOrg> palletsOrgs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());
			return palletsOrgs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ShiftPaletteOrg> findbyWorkPlaceIdUse(int targetUnit, String workplaceId) {
		if (workplaceId == null) {
			return new ArrayList<>();
		}
		String query = FIND_BY_TARGETID_USE;
		query = query.replaceFirst("targetUnit", String.valueOf(targetUnit));
		query = query.replaceFirst("targetId", workplaceId);

		try (PreparedStatement statement = this.connection().prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			List<ShiftPaletteOrg> palletsOrgs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());
			return palletsOrgs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ShiftPaletteOrg> findByCID(String cid) {
		String query = FIND_BY_CID;
		query = query.replaceFirst("cid", String.valueOf(cid));

		try (PreparedStatement statement = this.connection().prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			List<ShiftPaletteOrg> palletsOrgs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());
			return palletsOrgs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public void deleteByWorkPlaceId(String workplaceId, int page) {
		String query = FIND_TO_DELETE;
		query = query.replaceFirst("workplaceId", String.valueOf(workplaceId));
		query = query.replaceFirst("page", String.valueOf(page));
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			KscmtPaletteOrg kscmtPaletteOrg = toEntity(createShiftPallets(rs)).get(0);
			commandProxy().remove(KscmtPaletteOrg.class, kscmtPaletteOrg.pk);
			this.getEntityManager().flush();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

	}

	@Override
	public boolean exists(TargetOrgIdenInfor targeOrg, int page) {
		String targetId = targeOrg.getUnit() == TargetOrganizationUnit.WORKPLACE ? targeOrg.getWorkplaceId().get()
				: targeOrg.getWorkplaceGroupId().get();
		Optional<ShiftPaletteOrg> data = findShiftPalletOrg(targeOrg.getUnit().value, targetId, page);
		return data.isPresent();
	}

	@Override
	public void delete(TargetOrgIdenInfor targeOrg, int page) {
		String targetid = targeOrg.getUnit() == TargetOrganizationUnit.WORKPLACE?targeOrg.getWorkplaceId().get():targeOrg.getWorkplaceGroupId().get();
		String query = FIND_TO_DELETE_NEW;
		query = query.replaceFirst("cid", AppContexts.user().companyId());
		query = query.replaceFirst("targetUnit", String.valueOf(targeOrg.getUnit().value));
		query = query.replaceFirst("targetid", String.valueOf(targetid));
		query = query.replaceFirst("page", String.valueOf(page));
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			KscmtPaletteOrg kscmtPaletteOrg = toEntity(createShiftPallets(rs)).get(0);
			commandProxy().remove(KscmtPaletteOrg.class, kscmtPaletteOrg.pk);
			this.getEntityManager().flush();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}
