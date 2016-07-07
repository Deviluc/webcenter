package visual.panes;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import core.db.JobDatabase;
import core.job.Job;
import core.job.JobManager;
import core.lib.Misc;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.DisplayValue;
import visual.dialogs.ValueDialog;

@SuppressWarnings("rawtypes")
public class JobPane extends GridPane {
	
	
	private final TableView<Job> table;

	@SuppressWarnings("unchecked")
	public JobPane() {
		super();
		
		Text title = new Text("Jobs");
		title.setFont(Font.font("comic sans ms"));
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(title);
		add(hbox, 0, 0);
		
		table = new TableView<Job>();
		
		TableColumn<Job, Integer> idCol = new TableColumn<Job, Integer>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<Job, Integer>("Id"));
		idCol.setPrefWidth(50d);
		
		TableColumn<Job, String> nameCol = new TableColumn<Job, String>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<Job, String>("Name"));
		nameCol.setPrefWidth(150d);
		
		TableColumn<Job, LocalDateTime> nextExecCol = new TableColumn<Job, LocalDateTime>("Next execution");
		nextExecCol.setCellValueFactory(new PropertyValueFactory<Job, LocalDateTime>("NextExecution"));
		nextExecCol.setPrefWidth(225d);
		
		TableColumn<Job, HBox> actionCol = new TableColumn<Job, HBox>("Actions");
		
		actionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Job,HBox>, ObservableValue<HBox>>() {
			
			
			@Override
			public ObservableValue<HBox> call(CellDataFeatures<Job, HBox> param) {
				HBox box = new HBox();
				Button buttonEdit = new Button("Edit");
				buttonEdit.setOnAction(e -> {
					if (createJobDetailsDialog(param.getValue())) {
						JobDatabase.getInstance().update(param.getValue());
					}
				});
				
				Button buttonExecute = new Button("Run now");
				buttonExecute.setOnAction(e -> JobManager.getInstance().executeJob(param.getValue()));
				
				Button buttonDelete = new Button("Delete");
				buttonDelete.setOnAction(e -> {
					JobDatabase.getInstance().delete(param.getValue());
					refreshTable();
				});
				
				box.getChildren().addAll(buttonEdit, buttonExecute, buttonDelete);
				
				return new ObservableValue<HBox>() {

					@Override
					public void addListener(InvalidationListener listener) {}

					@Override
					public void removeListener(InvalidationListener listener) {}

					@Override
					public void addListener(ChangeListener<? super HBox> listener) {}

					@Override
					public void removeListener(ChangeListener<? super HBox> listener) {}

					@Override
					public HBox getValue() {
						return box;
					}
				};
			}
		});
		actionCol.setPrefWidth(300d);
		
		table.getColumns().addAll(idCol, nameCol, nextExecCol, actionCol);
		table.getItems().addAll(JobDatabase.getInstance().getCollection());
		
		JobDatabase.getInstance().addChangeListener(() -> refreshTable());
		
		add(table, 0, 1);
		
		Button newJobButton = new Button("New Job");
		newJobButton.setOnAction(e -> createNewJobAlert());
		
		add(newJobButton, 0, 2);
	}
	
	private boolean createJobDetailsDialog(final Job job) {		
		ValueDialog<Job> detailsDialog = new ValueDialog<>("Edit Job", "Please fill in the required values.", job.getRequirements(), job);
		Optional<Job> result = detailsDialog.showAndWait();
		
		return result.isPresent();
	}
	
	private void createNewJobAlert() {
		Map<Integer, Map<String, String>> jobTypes = Misc.loadResourceEnumeration("jobs");
		List<DisplayValue<Map<String, String>>> jobTypesList = new ArrayList<DisplayValue<Map<String, String>>>();
		
		for (Integer id : jobTypes.keySet()) {
			Map<String, String> jobType = jobTypes.get(id);
			jobTypesList.add(new DisplayValue<Map<String, String>>(jobType.get("name"), jobType));
		}
		
		ChoiceDialog<DisplayValue<Map<String, String>>> dialog = new ChoiceDialog<DisplayValue<Map<String, String>>>(jobTypesList.get(0), jobTypesList);
		dialog.showAndWait().ifPresent(dp -> {
			String className = ((DisplayValue<Map<String, String>>) dp).get().get("className");
			Class<?> clazz;
			try {
				clazz = Class.forName("core.job." + className);
				Job job = (Job) clazz.getConstructor().newInstance();
				if (createJobDetailsDialog(job)) {
					JobDatabase.getInstance().create(job);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}
	
	private void refreshTable() {
		table.getItems().clear();
		table.getItems().addAll(JobDatabase.getInstance().getCollection());
	}

}
