package model;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import core.enums.ValueType;

@SuppressWarnings("rawtypes")
public class ValueRequirement {
	
	private final String displayName;
	private final ValueType valueType;
	private Object value;
	private Consumer<Object> setValue;
	private Consumer<Throwable> onError;
	private Supplier<Object> populate;

	public ValueRequirement(final String displayName, final ValueType valueType, final Consumer<Object> setValue) {
		this.displayName = displayName;
		this.valueType = valueType;
		this.setValue = setValue;
	}
	
	public void setOnError(Consumer<Throwable> onError) {
		this.onError = onError;
	}
	
	public void setPopulate(Supplier<Object> populate) {
		this.populate = populate;
	}
	
	public void setSetValue(Consumer<Object> setValue) {
		this.setValue = setValue;
	}
	
	public void setValue(final Object value) {
		this.value = value;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ValueType getValueType() {
		return valueType;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Consumer<Object> getSetValue() {
		return setValue;
	}
	
	public Consumer<Throwable> getOnError() {
		return onError;
	}
	
	public Supplier<Object> getPopulate() {
		return populate;
	}
	

}
