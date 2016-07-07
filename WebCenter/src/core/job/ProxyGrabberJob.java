package core.job;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import core.enums.ValueType;
import model.Procedure;
import model.ValueRequirement;

public class ProxyGrabberJob extends BaseJob implements Job<ProxyGrabberJob>, Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -4014545467652896907L;
	private int id;
	private String name;
	
	
	private Integer maxThreads;

	public ProxyGrabberJob() {
		this.id = -1;
		setStartDate(LocalDate.now());
		setStartTime(LocalTime.now());
	}
	
	
	@Override
	public List<ValueRequirement> getRequirements() {
		List<ValueRequirement> reqs = new ArrayList<ValueRequirement>();
		
		ValueRequirement nameReq = new ValueRequirement("Name", ValueType.STRING, null);
		nameReq.setSetValue(s -> {
			if (s != null && !s.equals("")) {
				name = (String) s;
			} else {
				nameReq.getOnError().accept(new IllegalArgumentException("The name cannot be empty!"));
			}
		});
		nameReq.setValue(name);
		reqs.add(nameReq);
		
		ValueRequirement maxThreadsR = new ValueRequirement("Max threads", ValueType.INTEGER, null);
		maxThreadsR.setSetValue(i -> {
			if ((int) i > 0) {
				maxThreads = (int) i;
			} else {
				maxThreadsR.getOnError().accept(new IllegalArgumentException("The number of maximal threads used must be greater 0!"));
			}
		});
		maxThreadsR.setValue(maxThreads);
		reqs.add(maxThreadsR);
		
		reqs.addAll(getExecutionValueRequirements());
		
		
		return reqs;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LocalDateTime getNextExecution() {
		return calculateNextExecution();
	}

	@Override
	public void execute() {
		System.out.println("Running job " + name);
	}

	@Override
	public void setOnProgress(Consumer<Integer> onProgress) {
		this.onProgress = onProgress;		
	}


	@Override
	public void setId(int id) {
		this.id = id;
		
	}


	@Override
	public Job getEntry() {
		return this;
	}




}
