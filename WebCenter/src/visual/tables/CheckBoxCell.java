package visual.tables;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import model.Proxy;

public class CheckBoxCell extends TableCell<Proxy, Boolean> {
    private final CheckBox checkbox;
    private boolean init, editable;
    
    public CheckBoxCell(final boolean editable) {
    	super();
    	checkbox = new CheckBox();
    	init = false;
    	this.editable = editable;
    }
    
    public void setOnAction(final EventHandler<ActionEvent> stateModifiedHandler) {
    	checkbox.setOnAction(stateModifiedHandler);
    }
    
    public CheckBox getCheckbox() {
    	return checkbox;
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
    	super.updateItem(item, empty);
    	
    	
    	if(!empty && item != null) {
	        checkbox.setAlignment(Pos.CENTER);
	        checkbox.setSelected(item);
	        setAlignment(Pos.CENTER);
	        setGraphic(checkbox);
	        
	        if (!init) {
	        	checkbox.setDisable(!editable);
	    		init = true;
	        }
    	}
    }
}
