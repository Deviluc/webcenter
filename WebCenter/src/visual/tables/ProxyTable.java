package visual.tables;

import java.util.ArrayList;
import java.util.List;

import core.db.ProxyDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Proxy;
import model.ProxyFilter;

public class ProxyTable extends TableView<Proxy> {
	
	private final TableView<Proxy> table;
	
	@SuppressWarnings("unchecked")
	public ProxyTable() {
		super();
		
		table = this;
		
		TableColumn<Proxy, Boolean> columnSelected = new TableColumn<Proxy, Boolean>("Selected");
		columnSelected.setCellValueFactory(new PropertyValueFactory<Proxy, Boolean>("isSelected"));
		columnSelected.setCellFactory(new Callback<TableColumn<Proxy,Boolean>, TableCell<Proxy,Boolean>>() {
			
			@Override
			public TableCell<Proxy, Boolean> call(TableColumn<Proxy, Boolean> param) {
				CheckBoxCell cell = new CheckBoxCell(true);
				cell.setOnAction(event -> table.getItems().get(cell.getIndex()).setIsSelected(cell.getCheckbox().isSelected()));
				
				return cell;
			}
		});
		columnSelected.setEditable(true);
		
		TableColumn<Proxy, Boolean> columnWorking = new TableColumn<Proxy, Boolean>("Working");
		columnWorking.setCellValueFactory(new PropertyValueFactory<Proxy, Boolean>("isWorking"));
		columnWorking.setCellFactory(new Callback<TableColumn<Proxy,Boolean>, TableCell<Proxy,Boolean>>() {

			@Override
			public TableCell<Proxy, Boolean> call(TableColumn<Proxy, Boolean> param) {
				return new CheckBoxCell(false);
			}
		});
		columnWorking.setEditable(false);
		
		TableColumn<Proxy, Boolean> columnModifiedContent = new TableColumn<Proxy, Boolean>("Content\nmodified");
		columnModifiedContent.setCellValueFactory(new PropertyValueFactory<Proxy, Boolean>("contentModified"));
		columnModifiedContent.setCellFactory(new Callback<TableColumn<Proxy,Boolean>, TableCell<Proxy,Boolean>>() {

			@Override
			public TableCell<Proxy, Boolean> call(TableColumn<Proxy, Boolean> param) {
				return new CheckBoxCell(false);
			}
		});
		columnModifiedContent.setEditable(false);
		
		TableColumn<Proxy, String> columnIp = new TableColumn<Proxy, String>("IP");
		columnIp.setCellValueFactory(new PropertyValueFactory<Proxy, String>("ip"));
		columnIp.setPrefWidth(150d);
		columnIp.setEditable(false);
		
		TableColumn<Proxy, String> columnPort = new TableColumn<Proxy, String>("Port");
		columnPort.setCellValueFactory(new PropertyValueFactory<Proxy, String>("port"));
		columnPort.setEditable(false);
		
		TableColumn<Proxy, Integer> columnAnonymity = new TableColumn<Proxy, Integer>("Anonymity");
		columnAnonymity.setCellValueFactory(new PropertyValueFactory<Proxy, Integer>("anonymity"));
		columnAnonymity.setPrefWidth(100d);
		columnAnonymity.setEditable(false);
		
		TableColumn<Proxy, Integer> columnTimeout = new TableColumn<Proxy, Integer>("Timeout\n  in ms");
		columnTimeout.setCellValueFactory(new PropertyValueFactory<Proxy, Integer>("timeout"));
		columnTimeout.setEditable(false);

		table.getColumns().addAll(columnSelected, columnWorking, columnModifiedContent, columnIp, columnPort, columnAnonymity, columnTimeout);
		
		ProxyDatabase proxyDB = ProxyDatabase.getInstance();
		ObservableList<Proxy> proxyList = FXCollections.observableArrayList(proxyDB.getCollection());
		table.setItems(proxyList);
		
	}
	
	public Pane getControlPane() {
		GridPane pane = new GridPane();
		
		//Design
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(10));
		pane.setStyle("-fx-border-color: gray;");
		
		HBox titleHBox = new HBox();
		titleHBox.setAlignment(Pos.CENTER);
		
		Text filterTitle = new Text("Filtering");
		filterTitle.setFont(Font.font("comic sans ms"));
		
		titleHBox.getChildren().add(filterTitle);
		pane.add(titleHBox, 0, 0, 2, 1);
		
		CheckBox checkBoxWorking = new CheckBox("working");
		checkBoxWorking.setAllowIndeterminate(true);
		checkBoxWorking.setIndeterminate(true);
		pane.add(checkBoxWorking, 0, 1);
		
		CheckBox checkBoxModifiedContent = new CheckBox("modified content");
		checkBoxModifiedContent.setAllowIndeterminate(true);
		checkBoxModifiedContent.setIndeterminate(true);
		pane.add(checkBoxModifiedContent, 1, 1);
		
		Label labelMaxTimeout = new Label("max timeout:");
		labelMaxTimeout.setTooltip(new Tooltip("in ms"));
		pane.add(labelMaxTimeout, 0, 2);
		
		TextField textFieldMaxTimeout = new TextField();
		textFieldMaxTimeout.setPrefWidth(50);
		pane.add(textFieldMaxTimeout, 1, 2);
		
		Label labelMaxAnonymity = new Label("max anonymity:");
		pane.add(labelMaxAnonymity, 0, 3);
		
		ComboBox<Integer> comboBoxMaxAnonymity = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3));
		comboBoxMaxAnonymity.setValue(3);
		pane.add(comboBoxMaxAnonymity, 1, 3);
		
		Button buttonFilter = new Button("Filter");
		buttonFilter.setAlignment(Pos.BOTTOM_RIGHT);
		pane.add(buttonFilter, 1, 4);
		
		//Logic
		buttonFilter.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				ProxyFilter filter = new ProxyFilter();
				
				if (!checkBoxWorking.isIndeterminate()) {
					filter.setWorking(checkBoxWorking.isSelected());
				}
				
				if (!checkBoxModifiedContent.isIndeterminate()) {
					filter.setModifiedContent(checkBoxModifiedContent.isSelected());
				}
				
				try {
					filter.setMaxTimeout(Integer.parseInt(textFieldMaxTimeout.getText()));
				} catch (Exception e) {
					
				}
				
				filter.setMaxAnonymity(comboBoxMaxAnonymity.getValue());
				
				table.setItems(FXCollections.observableList(ProxyDatabase.getInstance().searchProxies(filter)));
			}
		});
		
		
		return pane;
	}
	
	public int getEntryCount() {
		return table.getItems().size();
	}
	
	public List<Proxy> getSelected() {
		List<Proxy> proxies = new ArrayList<Proxy>();
		
		for (Proxy proxy : table.getItems()) {
			if (proxy.getIsSelected()) {
				proxies.add(proxy);
			}
		}
		
		return proxies;
	}
	

}
