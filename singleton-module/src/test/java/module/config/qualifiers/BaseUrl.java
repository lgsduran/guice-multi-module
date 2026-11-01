package module.config.qualifiers;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// Se o seu projeto usa o ecossistema mais moderno (Jakarta), use jakarta.inject.Qualifier
// Se usa bibliotecas mais antigas, mantenha o javax.inject.Qualifier.
import jakarta.inject.Qualifier; 

@Qualifier
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface BaseUrl {}