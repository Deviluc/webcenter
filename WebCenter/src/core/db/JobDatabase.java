package core.db;

import core.job.Job;

public class JobDatabase extends Database<Job<?>> {
	
	private static JobDatabase db;
	
	private JobDatabase() {
		super();
	}
	
	public static JobDatabase getInstance() {
		if (db == null) {
			db = new JobDatabase();
		}
		
		return db;
	}

}
