package visual.dialogs;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import model.ValueRequirement;
import visual.panes.ValueRequirementPane;

public class ValueDialog<T> extends Dialog<T> {

	public ValueDialog(final String title, final String headerText, final List<ValueRequirement> valueRequirements, final T value) {
		setTitle(title);
		setHeaderText(headerText);
		setResizable(false);
		
		ValueRequirementPane pane = new ValueRequirementPane(valueRequirements);
		getDialogPane().setContent(pane);
		
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		ButtonType saveButton = new ButtonType("Save", ButtonData.APPLY);
		getDialogPane().getButtonTypes().addAll(cancelButton, saveButton);
		
		setResultConverter(b -> {
			return (b == saveButton) ? value : null;
		});
		
		getDialogPane().lookupButton(saveButton).addEventFilter(ActionEvent.ACTION, event -> {
			if (!pane.save()) event.consume();
		});
	}

}
