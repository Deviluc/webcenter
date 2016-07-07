package core.job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;


import core.db.DatabaseEntry;
import model.Procedure;
import model.ValueRequirement;

public interface Job<T> extends DatabaseEntry<Job<T>> {
		
	public int getId();
	
	public String getName();
	
	public LocalDateTime getLastExecution();
	
	public LocalDateTime getNextExecution();
	
	public void execute();
	
	public void setOnProgress(Consumer<Integer> function);
	
	public void setExecuted();
	
	public List<ValueRequirement> getRequirements();
	
}
