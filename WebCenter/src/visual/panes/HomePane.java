package visual.panes;

import java.net.MalformedURLException;
import java.net.URL;

import core.lib.Misc;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class HomePane extends GridPane {

	public HomePane() throws MalformedURLException {
		super();
		ImageView headerView = new ImageView(Misc.loadImage("home-header.png"));
		headerView.setPreserveRatio(true);
		headerView.setFitWidth(950);
		add(headerView, 0, 0, 2, 1);
		
		WebView textView = new WebView();
		textView.getEngine().load(new URL("file://" + System.getProperty("user.dir") + "/resources/home-text.html").toExternalForm());
		add(textView, 0, 1, 2, 1);
		
		
		
		setMaxWidth(950);
	}

}
