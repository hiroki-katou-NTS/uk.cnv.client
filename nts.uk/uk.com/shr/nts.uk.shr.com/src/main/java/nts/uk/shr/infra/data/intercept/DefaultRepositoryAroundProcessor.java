package nts.uk.shr.infra.data.intercept;

import javax.ejb.Stateless;
import javax.interceptor.InvocationContext;

import nts.arc.layer.infra.data.intercept.RepositoryAroundProcessor;
import nts.arc.layer.infra.data.log.RepositoryLogger;
import nts.arc.validate.Validatable;

@Stateless
public class DefaultRepositoryAroundProcessor implements RepositoryAroundProcessor {

	@Override
	public Object process(InvocationContext context) {
		
		// validate domain object if it is parameter of command (not query).
		// If return type is void, this method is probably command.
		if (context.getMethod().getReturnType().getName() == "void") {
			this.validateParameters(context.getParameters());
		}
		
		RepositoryLogger.entering(
				context.getMethod().getDeclaringClass().getName(),
				context.getMethod().getName());
		
		try {
			return context.proceed();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			RepositoryLogger.exited();
		}
	}

	private void validateParameters(Object[] parameters) {
		
		for (Object parameter : parameters) {
			if (parameter instanceof Validatable) {
				((Validatable) parameter).validate();
			}
		}
	}
	
}
