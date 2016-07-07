package visual.panes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import visual.tables.ProxyTable;

public class ProxyDbPane extends GridPane {

	public ProxyDbPane() {
		Text title = new Text("Proxy Database");
		title.setFont(Font.font("comic sans ms"));
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(title);
		add(hbox, 0, 0);
		
		ProxyTable table = new ProxyTable();
		
		add(table, 0, 1);
		
		HBox infoBox = new HBox(10);
		infoBox.setAlignment(Pos.BOTTOM_RIGHT);
		Label entryCountLabel = new Label("Total proxies: " + table.getEntryCount());
		
		infoBox.getChildren().add(entryCountLabel);
		add(infoBox, 0, 2);
		
		add(table.getControlPane(), 1, 1);
		
		setHgap(10);
		setVgap(10);		
	}

}
