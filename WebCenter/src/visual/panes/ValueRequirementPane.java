package visual.panes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Procedure;
import model.ValueRequirement;
import core.enums.ValueType;

public class ValueRequirementPane extends GridPane {
	
	private Map<ValueRequirement, Procedure> saveMap;
	private BooleanProperty isDataValid;

	@SuppressWarnings("unchecked")
	public ValueRequirementPane(List<ValueRequirement> reqs) {
		super();
		
		isDataValid = new SimpleBooleanProperty(false);
		setAlignment(Pos.CENTER_RIGHT);
		
		saveMap = new HashMap<ValueRequirement, Procedure>();
		
		
		int i = 0;
		
		for (ValueRequirement req : reqs) {
			
			req.setOnError(throwable -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(throwable.getMessage());
				alert.showAndWait();
				isDataValid.set(false);
			});
			
			ValueType type = req.getValueType();
			
			switch (type) {
				case BOOLEAN:
					CheckBox checkbox = new CheckBox(req.getDisplayName());
					saveMap.put(req, () -> req.getSetValue().accept(checkbox.isSelected()));
					
					if (req.getValue() != null) {
						checkbox.setSelected((boolean) req.getValue());
					}
					
					add(checkbox, 1, i);
					break;
				case INTEGER:
					Label intLabel = new Label(req.getDisplayName() + ":");
					add(intLabel, 0, i);
					TextField intField = new TextField();
					saveMap.put(req, () -> {
						try {
							req.getSetValue().accept(Integer.parseInt(intField.getText()));
						} catch (NumberFormatException e) {
							createErrorDialog(req.getDisplayName(), "Please enter a valid number!");
						}
					});
					
					if (req.getValue() != null) {
						intField.setText(((Integer) req.getValue()) + "");
					}
					
					add(intField, 1, i);
					break;
				case STRING:
					Label stringLabel = new Label(req.getDisplayName() + ":");
					add(stringLabel, 0, i);
					TextField stringField = new TextField();
					saveMap.put(req, () -> req.getSetValue().accept(stringField.getText()));
					
					if (req.getValue() != null) {
						stringField.setText((String) req.getValue());
					}
					
					add(stringField, 1, i);
					break;
				case DECIMAL:
					Label decimalLabel = new Label(req.getDisplayName() + ":");
					add(decimalLabel, 0, i);
					TextField decimalField = new TextField();
					saveMap.put(req, () -> {
						try {
							req.getSetValue().accept(Double.parseDouble(decimalField.getText()));
						} catch (NumberFormatException e) {
							createErrorDialog(req.getDisplayName(), "Please enter a valid decimal number!");
						}
					});
					
					if (req.getValue() != null) {
						decimalField.setText(Double.toString((double) req.getValue()));
					}
					
					add(decimalField, 1, i);
					break;
				case DATE:
					Label dateLabel = new Label(req.getDisplayName() + ":");
					add(dateLabel, 0, i);
					DatePicker datePicker = new DatePicker(LocalDate.now());
					saveMap.put(req, () -> {
						if (datePicker.getValue() != null) {
							req.getSetValue().accept(datePicker.getValue());
						} else {
							createErrorDialog(req.getDisplayName(), "Please enter a valid date!");
						}
					});					
					
					if (req.getValue() != null) {
						datePicker.setValue((LocalDate) req.getValue());
					}
					
					add(datePicker, 1, i);
					break;
				case DECIMAL_LIST:
					Label decimalListLabel = new Label(req.getDisplayName() + ":");
					add(decimalListLabel, 0, i);
					ComboBox<Double> decimalComboBox = new ComboBox<Double>((ObservableList<Double>) req.getPopulate().get()); 
					saveMap.put(req, () -> req.getSetValue().accept(decimalComboBox.getSelectionModel().getSelectedItem()));
					
					if (req.getValue() != null) {
						decimalComboBox.getSelectionModel().select((Double) req.getValue());
					}
					
					add(decimalComboBox, 1, i);					
					break;
				case INTEGER_LIST:
					Label integerListLabel = new Label(req.getDisplayName() + ":");
					add(integerListLabel, 0, i);
					ComboBox<Integer> integerComboBox = new ComboBox<Integer>((ObservableList<Integer>) req.getPopulate().get()); 
					saveMap.put(req, () -> req.getSetValue().accept(integerComboBox.getSelectionModel().getSelectedItem()));
					
					if (req.getValue() != null) {
						integerComboBox.getSelectionModel().select((Integer) req.getValue());
					}
					
					add(integerComboBox, 1, i);
					break;
				case STRING_LIST:
					Label stringListLabel = new Label(req.getDisplayName() + ":");
					add(stringListLabel, 0, i);
					ComboBox<String> stringComboBox = new ComboBox<String>(FXCollections.observableList((List<String>) req.getPopulate().get())); 
					saveMap.put(req, () -> req.getSetValue().accept(stringComboBox.getSelectionModel().getSelectedItem()));
					
					if (req.getValue() != null) {
						stringComboBox.getSelectionModel().select((String) req.getValue());
					} else {
						stringComboBox.getSelectionModel().select(0);
					}
					
					add(stringComboBox, 1, i);		
					break;
				
				default:
					break;
			}
			
			i++;
		}
		

		
	}
	
	public boolean save() {
		isDataValid.set(true);
		
		for (ValueRequirement req : saveMap.keySet()) {
			saveMap.get(req).execute();
		}
		
		return isDataValid.get();
	}
	
	private void createErrorDialog(final String fieldName, final String errorText) {
		isDataValid.set(false);
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error saving");
		alert.setHeaderText("Error saving field \"" + fieldName + "\"");
		alert.setContentText(errorText);
		alert.showAndWait();
	}

}
