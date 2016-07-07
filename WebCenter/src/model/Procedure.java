package model;

import java.io.Serializable;

@FunctionalInterface
public interface Procedure extends Serializable {
	
	void execute();
	
}
