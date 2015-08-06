package com.forter.contracts.validation;

import java.lang.reflect.Type;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

/**
 * Base class for {@link ValidatedValueUnwrapper}s based on ClassMate's type resolver.
 * A clone of {@link org.hibernate.validator.internal.engine.valuehandling}
 * Cloned in order to change the constructor access modifier to public
 */
public abstract class TypeResolverBasedValueUnwrapper<T> extends ValidatedValueUnwrapper<T> {

	private final Class<?> clazz;
	private final TypeResolver typeResolver;

	public TypeResolverBasedValueUnwrapper(TypeResolutionHelper typeResolutionHelper) {
		this.typeResolver = typeResolutionHelper.getTypeResolver();
		clazz = resolveSingleTypeParameter( typeResolver, this.getClass(), ValidatedValueUnwrapper.class );
	}

	@Override
	public Type getValidatedValueType(Type valueType) {
		return resolveSingleTypeParameter( typeResolver, valueType, clazz );
	}

	/**
	 * Resolves the single type parameter of the given target class, using the given sub-type.
	 */
	private static Class<?> resolveSingleTypeParameter(TypeResolver typeResolver, Type subType, Class<?> target) {
		ResolvedType resolvedType = typeResolver.resolve( subType );
		return resolvedType.typeParametersFor( target ).get( 0 ).getErasedType();
	}
}
