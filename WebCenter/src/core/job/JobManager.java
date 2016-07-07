package core.job;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import core.db.JobDatabase;
import model.Procedure;

public class JobManager {
	
	private final Thread managerThread;
	private static JobManager manager;
	private final ManagerRunnable managerRun;

	private JobManager() {
		managerRun = new ManagerRunnable();
		managerThread = new Thread(managerRun);
		managerThread.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> managerRun.stop()));
	}
	
	public static JobManager getInstance() {
		if (manager == null) {
			manager = new JobManager();
		}
		
		return manager;
	}
	
	public void executeJob(final Job<?> job) {
		job.setExecuted();
		
		new Thread(() -> {
			job.execute();
			onJobFinish(job);
		}).start();		
	}
	
	private void onJobFinish(final Job job) {
		job.setOnProgress(null);
		JobDatabase.getInstance().update(job);
		
	}
	
	private class ManagerRunnable implements Runnable {
		
		private boolean running, sleeping;
		private final JobDatabase db;
		private List<Job<?>> jobList;
		
		public ManagerRunnable() {
			db = JobDatabase.getInstance();
			db.addChangeListener(() -> refreshExecutions());
			
			jobList = new ArrayList<Job<?>>(db.getCollection());
			
			refreshExecutions();
		}

		@Override
		public void run() {
			running = true;
			
			while (running) {
				
				sortJobList();
				
				if (jobList.size() > 0) {
					LocalDateTime now = LocalDateTime.now();
					
					LocalDateTime nextExec = jobList.get(0).getNextExecution();
					
					long diff = nextExec.atZone(ZoneId.of("America/Los_Angeles")).toInstant().toEpochMilli() - now.atZone(ZoneId.of("America/Los_Angeles")).toInstant().toEpochMilli();
					
					System.out.println("Waiting " + (diff / 1000) + " seconds...");
					
					if (diff > 0) {
						try {
							sleeping = true;
							Thread.sleep(diff);
							sleeping = false;
						} catch (InterruptedException e) {
							sleeping = false;
							continue;
						}
					}
					
					final Job<?> job = jobList.get(0);
					executeJob(job);
					
				} else {
					try {
						sleeping = true;
						Thread.sleep(60 * 1000);
						sleeping = false;
					} catch (InterruptedException e) {
						sleeping = false;
						continue;
					}
				}
			}
			
		}
		
		private void refreshExecutions() {
			jobList = new ArrayList<Job<?>>(db.getCollection());
			sortJobList();
			
			if (sleeping) {
				managerThread.interrupt();
			}
		}
		
		private void sortJobList() {
			Collections.sort(jobList, (a, b) -> a.getNextExecution().compareTo(b.getNextExecution()));
		}
		
		public void stop() {
			running = false;
			
			if (sleeping) {
				managerThread.interrupt();
			}
		}
		
	}

}
