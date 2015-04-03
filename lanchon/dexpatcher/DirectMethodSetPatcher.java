package lanchon.dexpatcher;

import java.util.Collection;

import org.jf.dexlib2.iface.Method;

import static lanchon.dexpatcher.Logger.Level.*;

public class DirectMethodSetPatcher extends MethodSetPatcher {

	private boolean staticConstructorFound;

	public DirectMethodSetPatcher(Logger logger, String baseLogPrefix, String logMemberType, PatcherAnnotation annotation) {
		super(logger, baseLogPrefix, logMemberType, annotation);
	}

	@Override
	public Collection<Method> run(Iterable<? extends Method> sourceSet, int sourceSetSizeHint,
			Iterable<? extends Method> patchSet, int patchSetSizeHint) {
		staticConstructorFound = false;
		Collection<Method> methods = super.run(sourceSet, sourceSetSizeHint, patchSet, patchSetSizeHint);
		if (staticConstructorAction != null && !staticConstructorFound) {
			log(ERROR, "static constructor not found");
		}
		return methods;
	}

	// Handlers

	@Override
	protected PatcherAnnotation getDefaultAnnotation(Method patch) {
		if ("<clinit>".equals(patch.getName()) &&		// performance optimization
				"<clinit>()V".equals(getId(patch))) {
			staticConstructorFound = true;
			if (staticConstructorAction != null) {
				return new PatcherAnnotation(staticConstructorAction, patch.getAnnotations());
			}
		}
		return super.getDefaultAnnotation(patch);
	}

}
