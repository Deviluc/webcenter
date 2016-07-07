package core.job;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import core.enums.ValueType;
import model.Procedure;
import model.ValueRequirement;

public class BaseJob implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4042996984811561848L;

	public static enum Execution {
		MINUTES, HOURS, DAYS, WEEKS
	}
	
	private LocalDate startDate;
	private LocalTime startTime;
	
	private LocalDateTime lastExec;
	
	private Execution executionMode;
	private int execEvery;
	
	protected Consumer<Integer> onProgress;
	
	public void setStartDate(final LocalDate startDate) {
		this.startDate = startDate;
	}
	
	public void setStartTime(final LocalTime startTime) {
		this.startTime = startTime;
	}
	
	public void setExecutionMode(final Execution executionMode) {
		this.executionMode = executionMode;
	}
	
	public void setExecEvery(final int execEvery) {
		this.execEvery = execEvery;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}
	
	public LocalTime getStartTime() {
		return startTime;
	}
	
	public Execution getExecutionMode() {
		return executionMode;
	}
	
	public int getExecEvery() {
		return execEvery;
	}
	
	public LocalDateTime calculateNextExecution() {
		
		if (lastExec == null) {
			return startDate.atTime(startTime);
		} else {
			LocalDateTime exec = lastExec;
			switch (executionMode) {
			case MINUTES:
				exec = exec.plusMinutes(execEvery);
				break;
			case HOURS:
				exec = exec.plusHours(execEvery);
				break;
			case DAYS:
				exec = exec.plusDays(execEvery);
				break;
			case WEEKS:
				exec = exec.plusWeeks(execEvery);
				break;
			default:
				break;
			}
			
			return exec;
		}
	}
	
	public LocalDateTime getLastExecution() {
		return lastExec;
	}
	
	protected List<ValueRequirement> getExecutionValueRequirements() {
		List<ValueRequirement> reqs = new ArrayList<ValueRequirement>();
		
		ValueRequirement executionModeR = new ValueRequirement("Execution mode", ValueType.STRING_LIST, i -> executionMode = Execution.valueOf((String) i));
		executionModeR.setPopulate(() -> {
			List<String> result = new ArrayList<String>();
			Arrays.asList(Execution.values()).forEach(v -> result.add(((Execution) v).name()));
			return result;
		});
		executionModeR.setValue(executionMode != null ? executionMode.name() : null);
		reqs.add(executionModeR);

		ValueRequirement execEveryR = new ValueRequirement("Execute every", ValueType.INTEGER, null);
		execEveryR.setSetValue(i -> {
			if ((int) i > 0) {
				execEvery = (int) i;
			} else {
				execEveryR.getOnError().accept(new IllegalArgumentException("The value for \"Execute every\" must be greater 0!"));
			}
		});
		execEveryR.setValue(execEvery);
		reqs.add(execEveryR);

		ValueRequirement startDateR = new ValueRequirement("First execution date", ValueType.DATE, i -> startDate = (LocalDate) i);
		startDateR.setPopulate(() -> startDate);
		startDateR.setValue(startDate);
		reqs.add(startDateR);
		
		ValueRequirement startTimeR = new ValueRequirement("Time", ValueType.STRING, null);
		startTimeR.setSetValue(i -> {
			try {
				startTime = LocalTime.parse((String) i);
			} catch (Exception e) {
				startTimeR.getOnError().accept(new IllegalArgumentException("You must enter a valid time for \"Start time\"!"));
			}
		});
		startTimeR.setValue(startTime.toString());
		reqs.add(startTimeR);
		
		return reqs;
	}
	
	public void setExecuted() {
		lastExec = LocalDateTime.now();
	}
	
	public BaseJob clone() throws CloneNotSupportedException {
		return (BaseJob) super.clone();
	}

}
