package de.frohwerk.ipm.core;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.sap.conn.jco.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Defaults.defaultValue;
import static com.google.common.base.Strings.nullToEmpty;
import static de.frohwerk.ipm.reflect.Annotations.annotation;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class JcoServiceFactory {
    public JcoServiceFactory(final JCoDestination destination) throws JCoException {
        this.destination = destination;
        this.repository = destination.getRepository();
    }

    public <T> T createService(final Class<T> serviceType) throws JCoException {
        final String namespace = ofNullable(serviceType.getAnnotation(JcoNamespace.class)).map(ns -> ns.value()).orElse("");
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{serviceType}, (proxy, method, args) -> {
            if ("toString".equals(method.getName())) {
                return serviceType.getName() + "[" + Stream.of(serviceType.getMethods()).map(m -> m.getName()).collect(joining(", ")) + "]";
            }
            final String functionName = annotation(method, JcoFunction.class).value();
            final String qualifiedName = namespace.isEmpty() ? functionName : namespace + "_" + functionName;
            final JCoFunction function = repository.getFunction(qualifiedName);
            // Handle method arguments
            final Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                handleParameter(function, parameters[i], args[i]);
            }
            // Call the functions
            function.execute(destination);
            // Handle results
            return handleResult(function, method);
        });
    }

    private Object handleResult(JCoFunction function, Method method) throws ReflectiveOperationException {
        if (method.isAnnotationPresent(JcoTableResult.class)) {
            final JcoTableResult tableResult = annotation(method, JcoTableResult.class);
            final Class<?> resultType = tableResult.type();
            final JCoTable resultTable = function.getTableParameterList().getTable(tableResult.parameter());
            final List<Object> returnValue = new ArrayList<>();
            if (!resultTable.isEmpty()) do {
                returnValue.add(createResultFromRecord(resultType, resultTable));
            } while (resultTable.nextRow());
            return returnValue;
        } else if (method.isAnnotationPresent(JcoExportResult.class)) {
            final JcoExportResult exportResult = annotation(method, JcoExportResult.class);
            if (method.isAnnotationPresent(JcoProperty.class)) {
                final JCoTable table = function.getTableParameterList().getTable(exportResult.parameter());
                if (table.isEmpty()) return defaultValue(method.getReturnType());
                return extractValue(table, annotation(method, JcoProperty.class).value(), method.getReturnType());
            } else {
                final Class<?> resultType = method.getReturnType();
                final JCoRecord record = exportResult.parameter().isEmpty() ? function.getExportParameterList() : function.getExportParameterList().getStructure(exportResult.parameter());
                return createResultFromRecord(resultType, record);
            }
        } else {
            throw new IllegalArgumentException("Method " + method.getName() + "has no JcoResult annotation");
        }
    }

    private Object createResultFromRecord(Class<?> resultType, JCoRecord record) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Object returnValue = resultType.newInstance();
        final ImmutableList<Field> jcoPropertyFields = FluentIterable.from(resultType.getDeclaredFields()).filter(field -> field.isAnnotationPresent(JcoProperty.class)).toList();
        for (final Field field : jcoPropertyFields) {
            final JcoProperty jcoProperty = annotation(field, JcoProperty.class);
            final Method setter = resultType.getMethod(accessor("set", field), field.getType());
            setter.invoke(returnValue, extractValue(record, jcoProperty.value(), field.getType()));
        }
        return returnValue;
    }

    private void handleParameter(final JCoFunction function, final Parameter parameter, final Object arg) {
        final Class<?> type = parameter.getType();
        if (parameter.isAnnotationPresent(JcoImportParameter.class)) {
            final String parameterName = annotation(parameter, JcoImportParameter.class).value();
            function.getImportParameterList().setValue(parameterName, arg);
        } else if (parameter.isAnnotationPresent(JcoTableCriteria.class)) {
            final JcoTableCriteria tableCriteria = annotation(parameter, JcoTableCriteria.class);
            // TODO Handle non-table parameters (like import parameters)
            final JCoTable importTable = function.getTableParameterList().getTable(tableCriteria.parameter());
            // START Iterate criterias...
            // TODO Handle structure parameters, those do not allow multiple rows...
            final Criteria[] criteria = criterias(type, arg); // TODO Type checking, validation
            for (Criteria criterion : criteria) {
                importTable.appendRow();
                importTable.setValue("SIGN", criterion.sign);
                importTable.setValue("OPTION", criterion.option.toString());
                importTable.setValue(tableCriteria.field() + "_LOW", criterion.lowerBound);
                if (nonNull(criterion.upperBound)) {
                    importTable.setValue(tableCriteria.field() + "_HIGH", criterion.lowerBound);
                }
            }
        } else {
            throw new IllegalArgumentException("Parameter type " + type.getSimpleName() + "has no JcoParameter annotation");
        }
    }

    private Object extractValue(final JCoRecord record, final String name, final Class<?> type) {
        if (type.equals(boolean.class)) {
            return record.getInt(name) > 0;
        } else if (type.equals(double.class)) {
            return Double.parseDouble(nullToEmpty(record.getString(name)).replace(',', '.'));
        } else if (type.equals(float.class)) {
            return Float.parseFloat(nullToEmpty(record.getString(name)).replace(',', '.'));
        } else if (type.equals(int.class)) {
            return record.getInt(name);
        } else if (type.equals(char.class)) {
            return record.getChar(name);
        } else if (type.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) type, record.getString(name));
        } else {
            return record.getValue(name);
        }
    }

    private String accessor(final String type, final Field field) {
        final StringBuilder accessorName = new StringBuilder(field.getName());
        accessorName.setCharAt(0, Character.toUpperCase(accessorName.charAt(0)));
        accessorName.insert(0, type);
        return accessorName.toString();
    }

    private Criteria[] criterias(final Class<?> parameterType, final Object arg) {
        if (parameterType.isArray()) return (Criteria[]) arg;
        return new Criteria[]{(Criteria) arg};
    }

    private final JCoDestination destination;
    private final JCoRepository repository;

    private static final Splitter querySplitter = Splitter.on(' ').omitEmptyStrings().trimResults();
}
