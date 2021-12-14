package nts.uk.shr.infra.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.val;
import nts.arc.layer.infra.data.EntityManagerLoader;
import nts.gul.reflection.ReflectionUtil;

@ApplicationScoped
public class CloudEntityManagerLoader implements EntityManagerLoader{
	
	/**
	 * DefaultEntityManager of cloud
	 */
    @PersistenceContext(unitName = "UK")
    private EntityManager entityManager;

//    データソース増やす時のサンプル
//    @PersistenceContext(unitName = "UK1")
//    private EntityManager entityManager1;
    
    private Map<String, EntityManager> entityManagersMap;
    
    @PostConstruct
    public void init() {
    	//refrection使って、このクラスの@PersistenceContextアノが付いたフィールド集める。
    	val annoFields = ReflectionUtil.getStreamOfFieldsAnnotated(
    														CloudEntityManagerLoader.class,
    														ReflectionUtil.Condition.ALL,
    														PersistenceContext.class);
    	this.entityManagersMap = new HashMap<String, EntityManager>(); 
    	//@PersistenceContextマップ(unitname,value)作成
    	annoFields.forEach(field ->{
    		field.setAccessible(true);
    		try {
    			val fieldValue = ReflectionUtil.getFieldValue(field, this);
				entityManagersMap.put(field.getAnnotation(PersistenceContext.class).unitName(), 
													  (EntityManager)fieldValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    }
    
    @Override
    public EntityManager getEntityManager() {
    	String datasourceName = TenantLocatorService.getConnectedDataSource();
    	EntityManager em = entityManagersMap.get(datasourceName);
    	if (em == null) {
    		throw new RuntimeException("データソースに一致するマネージャが見つかりませんでした。");
    	}
    	return em;
    }
	
	@Override
	public void forAllDataSources(Consumer<EntityManager> process) {
		entityManagersMap.values().forEach(em -> {
			runner(() -> process.accept(em));
		});
	}

	@Override
	public void forDefaultDatasource(Consumer<EntityManager> process) {
//		setSessionVariable(unlimited);//ここでunlimitedを使うようにする
		process.accept(entityManager);
//		setSessionVariable(unlimited);//ここでunlimitedを使わないようにする(破棄)
	}

	@Override
	public void forTenantDatasource(String tenantCode, Consumer<EntityManager> process) {
		String datasource = TenantLocatorService.getDataSourceFor(tenantCode);
//		setSessionVariable();
		
		process.accept(entityManagersMap.get(datasource));
	}

	private void runner(Runnable runnable) {
//		setSessionVariable(unlimited);//ここでunlimitedを使うようにする
		runnable.run();
//		setSessionVariable(unlimited);//ここでunlimitedを使わないようにする(破棄)
	}
}

