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
import nts.uk.ctx.at.schedule.dom.shift.management.shiftPalette.ShiftPaletteCom;
import nts.uk.ctx.at.schedule.dom.shift.management.shiftPalette.ShiftPaletteComRepository;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteCmp;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteCmpCombi;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteCmpCombiDtl;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteCmpCombiDtlPk;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteCmpCombiPk;
import nts.uk.ctx.at.schedule.infra.entity.shift.management.KscmtPaletteCmpPk;
import nts.uk.ctx.at.schedule.infra.repository.shift.management.Shifutoparetto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author phongtq
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaShiftPaletteComRepository extends JpaRepository implements ShiftPaletteComRepository {

	private static final String SELECT;

	private static final String FIND_BY_PAGE;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a.CID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE,");
		builderString.append("b.POSITION, b.POSITION_NAME,");
		builderString.append("c.POSITION_ORDER, c.SHIFT_MASTER_CD");
		builderString.append(
				" FROM KSCMT_PALETTE_CMP a JOIN KSCMT_PALETTE_CMP_COMBI b ON a.CID = b.CID AND a.PAGE = b.PAGE ");
		builderString.append("  JOIN KSCMT_PALETTE_CMP_COMBI_DTL c ON a.CID = c.CID AND a.PAGE = c.PAGE AND b.POSITION = c.POSITION ");
		SELECT = builderString.toString();

		builderString = new StringBuilder();
		builderString.append(SELECT);
		builderString.append("WHERE a.CID = 'companyId' AND a.PAGE = page");
		FIND_BY_PAGE = builderString.toString();

	}

	private static final String FIND_BY_COMPANY = "SELECT a.CID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE,"
			+ " b.POSITION, b.POSITION_NAME," + " c.POSITION_ORDER, c.SHIFT_MASTER_CD"
			+ " FROM KSCMT_PALETTE_CMP a JOIN KSCMT_PALETTE_CMP_COMBI b ON a.CID = b.CID AND a.PAGE = b.PAGE"
			+ " JOIN KSCMT_PALETTE_CMP_COMBI_DTL c ON b.CID = c.CID AND b.PAGE = c.PAGE AND b.POSITION = c.POSITION"
			+ " WHERE a.CID = 'companyId'";

	private static final String FIND_BY_COMPANY_USE = "SELECT a.CID, a.PAGE, a.PAGE_NAME, a.USE_ATR, a.NOTE,"
			+ " b.POSITION, b.POSITION_NAME," + " c.POSITION_ORDER, c.SHIFT_MASTER_CD"
			+ " FROM KSCMT_PALETTE_CMP a JOIN KSCMT_PALETTE_CMP_COMBI b ON a.CID = b.CID AND a.PAGE = b.PAGE"
			+ " JOIN KSCMT_PALETTE_CMP_COMBI_DTL c ON b.CID = c.CID AND b.PAGE = c.PAGE AND b.POSITION = c.POSITION"
			+ " WHERE a.CID = 'companyId' AND a.USE_ATR = 1 ";
	
	@AllArgsConstructor
	@Getter
	private class FullShiftPallets {
		public String companyId;
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
	private List<FullShiftPallets> createShiftPallets(ResultSet rs) {
		List<FullShiftPallets> listFullData = new ArrayList<>();
		while (rs.next()) {
			listFullData
					.add(new FullShiftPallets(rs.getString("CID"), Integer.valueOf(rs.getString("PAGE")),
							rs.getString("PAGE_NAME"), Integer.valueOf(rs.getString("USE_ATR")) == 1 ? true : false,
							rs.getString("NOTE"),
							rs.getString("POSITION") != null ? Integer.valueOf(rs.getString("POSITION")) : null,
							rs.getString("POSITION_NAME"), rs.getString("POSITION_ORDER") != null
									? Integer.valueOf(rs.getString("POSITION_ORDER")) : null,
							rs.getString("SHIFT_MASTER_CD")));
		}
		return listFullData;
	}

	private List<KscmtPaletteCmp> toEntity(List<FullShiftPallets> listFullJoin) {
		return listFullJoin.stream().collect(Collectors.groupingBy(FullShiftPallets::getPage)).entrySet().stream()
				.map(x -> {
					FullShiftPallets shiftPallets = x.getValue().get(0);
					KscmtPaletteCmpPk pk = new KscmtPaletteCmpPk(shiftPallets.getCompanyId(), shiftPallets.getPage());
					String pageName = shiftPallets.getPageName();
					boolean useAtr = shiftPallets.isUseAtr();
					String note = shiftPallets.getNote();
					List<KscmtPaletteCmpCombi> cmpCombis = x.getValue().stream()
							.collect(Collectors.groupingBy(FullShiftPallets::getPosition)).entrySet().stream()
							.map(y -> {
								KscmtPaletteCmpCombiPk combiPk = new KscmtPaletteCmpCombiPk(
										y.getValue().get(0).getCompanyId(), y.getValue().get(0).getPage(),
										y.getValue().get(0).getPosition());
								String positionName = y.getValue().get(0).getPositionName();
								List<KscmtPaletteCmpCombiDtl> cmpCombiDtls = y.getValue().stream()
										.collect(Collectors.groupingBy(FullShiftPallets::getPositionOrder)).entrySet()
										.stream().map(z -> {
											KscmtPaletteCmpCombiDtlPk cmpCombiDtlPk = new KscmtPaletteCmpCombiDtlPk(
													z.getValue().get(0).getCompanyId(), z.getValue().get(0).getPage(),
													z.getValue().get(0).getPosition(),
													z.getValue().get(0).getPositionOrder());
											String shiftMasterCd = z.getValue().get(0).getShiftMasterCd();
											return new KscmtPaletteCmpCombiDtl(cmpCombiDtlPk, shiftMasterCd, null);
										}).collect(Collectors.toList());
								return new KscmtPaletteCmpCombi(combiPk, positionName, null, cmpCombiDtls);
							}).collect(Collectors.toList());
					return new KscmtPaletteCmp(pk, pageName, useAtr ? 1 : 0, note, cmpCombis);
				}).collect(Collectors.toList());
	}

	@Override
	public void add(ShiftPaletteCom shiftPalletsCom) {
		commandProxy().insert(KscmtPaletteCmp.fromDomain(shiftPalletsCom));
	}

	@Override
	public void update(ShiftPaletteCom shiftPalletsCom) {

		Optional<KscmtPaletteCmp> getEntity = this.queryProxy().find(
				new KscmtPaletteCmpPk(AppContexts.user().companyId(), shiftPalletsCom.getPage()),
				KscmtPaletteCmp.class);

		if (getEntity.isPresent()) {
			getEntity.get().toEntity(shiftPalletsCom);
			List<Integer> position = getEntity.get().cmpCombis.stream().map(e -> e.pk.position)
					.collect(Collectors.toList());
			// delete position when right click choose 'delete'
			List<Integer> positionToDelete = getEntity.get().cmpCombis.stream().map(e -> e.pk.position)
					.filter(item -> !shiftPalletsCom.getShiftPallet().getCombinations().stream()
							.map(i -> i.getPositionNumber()).collect(Collectors.toList()).contains(item))
					.collect(Collectors.toList());
			List<ShiftPaletteCombinations> combinations = shiftPalletsCom.getShiftPallet().getCombinations().stream()
					.filter(i -> !position.contains(i.getPositionNumber())).collect(Collectors.toList());
			List<Shifutoparetto> shifutoparettos = new ArrayList<>();
			List<KscmtPaletteCmpCombi> combis = combinations.stream()
					.map(i -> KscmtPaletteCmpCombi.fromOneDomain(i, getEntity.get().pk)).collect(Collectors.toList());
			shiftPalletsCom.getShiftPallet().getCombinations().stream().forEach(i -> {
				shifutoparettos
						.addAll(i
								.getCombinations().stream().map(o -> new Shifutoparetto(i.getPositionNumber(),
										o.getOrder(), o.getShiftCode().v(), shiftPalletsCom.getPage()))
								.collect(Collectors.toList()));
			});
			List<Shifutoparetto> shifutoparettoss = new ArrayList<>();
			List<Shifutoparetto> shifutoparettoToDelete = new ArrayList<>();
			Map<Integer, List<Shifutoparetto>> mapShifutoparetto = shifutoparettos.stream()
					.collect(Collectors.groupingBy(Shifutoparetto::getPositionNumber));
			getEntity.get().cmpCombis.stream().forEach(i -> {
				if (mapShifutoparetto.containsKey(i.pk.position)) {
					List<Shifutoparetto> shifutoparettoMap = mapShifutoparetto.get(i.pk.position);
					List<Integer> shipCodeFilter = i.cmpCombiDtls.stream().map(e -> e.pk.positionOrder)
							.collect(Collectors.toList());
					shifutoparettoss.addAll(shifutoparettoMap.stream().filter(o -> !shipCodeFilter.contains(o.order))
							.collect(Collectors.toList()));
					// when click 'clear' button, check to delete
					List<Integer> shipCodeFilterToDelete = shifutoparettoMap.stream().map(y -> y.order)
							.collect(Collectors.toList());
					shifutoparettoToDelete.addAll(i.cmpCombiDtls.stream()
							.filter(x -> !shipCodeFilterToDelete.contains(x.pk.positionOrder))
							.map(item -> new Shifutoparetto(i.pk.position, item.pk.positionOrder, null, i.pk.page))
							.collect(Collectors.toList()));
				}
			});

			List<Integer> positions = getEntity.get().cmpCombis.stream().map(i -> i.pk.position)
					.collect(Collectors.toList());
			shifutoparettoss.addAll(shifutoparettos.stream().filter(i -> !positions.contains(i.positionNumber))
					.collect(Collectors.toList()));
			positionToDelete.stream().forEach(i -> {
				String combiDelete = "DELETE FROM KSCMT_PALETTE_CMP_COMBI WHERE CID = ? AND PAGE = ? AND POSITION = ? ";
				String dtlDelete = "DELETE FROM KSCMT_PALETTE_CMP_COMBI_DTL WHERE CID = ? AND PAGE = ? AND POSITION = ? ";

				try {
					PreparedStatement ps1 = this.connection().prepareStatement(combiDelete);
					ps1.setString(1, shiftPalletsCom.getCompanyId());
					ps1.setInt(2, shiftPalletsCom.getPage());
					ps1.setInt(3, i);
					ps1.executeUpdate();

					PreparedStatement ps2 = this.connection().prepareStatement(dtlDelete);
					ps2.setString(1, shiftPalletsCom.getCompanyId());
					ps2.setInt(2, shiftPalletsCom.getPage());
					ps2.setInt(3, i);
					ps2.executeUpdate();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});
			this.commandProxy().update(getEntity.get());
			List<KscmtPaletteCmpCombiDtl> cmpCombiDtls = shifutoparettoss.stream().map(i -> KscmtPaletteCmpCombiDtl
					.fromOneDomain(i.getPage(), i.getPositionNumber(), i.getOrder(), i.getShiftCode()))
					.collect(Collectors.toList());
			if (!shifutoparettoToDelete.isEmpty()) {
				String dtlDelete = "DELETE FROM KSCMT_PALETTE_CMP_COMBI_DTL WHERE PAGE = ? AND POSITION = ? AND POSITION_ORDER = ?";
				shifutoparettoToDelete.stream().forEach(e -> {
					try {
						PreparedStatement ps = this.connection().prepareStatement(dtlDelete);
						ps.setInt(1, e.getPage());
						ps.setInt(2, e.getPositionNumber());
						ps.setInt(3, e.getOrder());
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
	public void delete(ShiftPaletteCom shiftPalletsCom) {

		String query = FIND_BY_PAGE;
		query = query.replaceFirst("companyId", shiftPalletsCom.getCompanyId());
		query = query.replaceFirst("page", String.valueOf(shiftPalletsCom.getPage()));
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			KscmtPaletteCmp kscmtPaletteCmp = toEntity(createShiftPallets(rs)).get(0);
			commandProxy().remove(KscmtPaletteCmp.class, kscmtPaletteCmp.pk);
			this.getEntityManager().flush();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

	}

	@Override
	public Optional<ShiftPaletteCom> findShiftPallet(String companyId, int page) {
		String query = FIND_BY_PAGE;
		query = query.replaceFirst("companyId", companyId);
		query = query.replaceFirst("page", String.valueOf(page));
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			List<ShiftPaletteCom> palletsComs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());

			if (palletsComs.isEmpty())
				return Optional.empty();
			return Optional.of(palletsComs.get(0));

		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<ShiftPaletteCom> findShiftPallet(String companyId) {
		String query = FIND_BY_COMPANY;
		query = query.replaceFirst("companyId", companyId);
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			List<ShiftPaletteCom> palletsComs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());
			return palletsComs;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<ShiftPaletteCom> findShiftPalletUse(String companyId) {
		String query = FIND_BY_COMPANY_USE;
		query = query.replaceFirst("companyId", companyId);
		try (PreparedStatement stmt = this.connection().prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			List<ShiftPaletteCom> palletsComs = toEntity(createShiftPallets(rs)).stream().map(x -> x.toDomain())
					.collect(Collectors.toList());
			return palletsComs;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean isDuplicateRoleSetCd(String companyId, int page, int position) {
		KscmtPaletteCmpCombiPk pk = new KscmtPaletteCmpCombiPk(companyId, page, position);
		return this.queryProxy().find(pk, KscmtPaletteCmpCombi.class).isPresent();
	}

	@SneakyThrows
	@Override
	public void deleteByPage(String companyID, int page) {
		String cmpDelete = "DELETE FROM KSCMT_PALETTE_CMP WHERE CID = ? AND PAGE = ?";
		String combiDelete = "DELETE FROM KSCMT_PALETTE_CMP_COMBI WHERE CID = ? AND PAGE = ?";
		String dtlDelete = "DELETE FROM KSCMT_PALETTE_CMP_COMBI_DTL WHERE CID = ? AND PAGE = ?";
		PreparedStatement ps1 = this.connection().prepareStatement(cmpDelete);
		ps1.setString(1, companyID);
		ps1.setInt(2, page);
		ps1.executeUpdate();

		PreparedStatement ps2 = this.connection().prepareStatement(combiDelete);
		ps2.setString(1, companyID);
		ps2.setInt(2, page);
		ps2.executeUpdate();

		PreparedStatement ps3 = this.connection().prepareStatement(dtlDelete);
		ps3.setString(1, companyID);
		ps3.setInt(2, page);
		ps3.executeUpdate();

	}


	@Override
	public boolean exists(String companyID, int page) {
		return findShiftPallet(companyID, page).isPresent();
	}

}
