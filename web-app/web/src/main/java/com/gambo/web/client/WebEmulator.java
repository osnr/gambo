package com.gambo.web.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

public class WebEmulator extends Composite {
	private Canvas canvas;

	public WebEmulator() {
		
		DockPanel dockPanel = new DockPanel();
		initWidget(dockPanel);
		
		canvas = Canvas.createIfSupported();
		
		if (canvas == null) {
			dockPanel.add(new Label(GamboWeb.UPGRADE_MSG));
			return;
		}
		
		canvas.setCoordinateSpaceWidth(160);
		canvas.setCoordinateSpaceHeight(144);
		canvas.setSize("160px", "144px");
		
		dockPanel.add(canvas, DockPanel.CENTER);
		dockPanel.setCellHorizontalAlignment(canvas, HasHorizontalAlignment.ALIGN_CENTER);
		
		HTML html = new HTML("<p>Works best in Chrome. Click on the Game Boy screen to control.</p>\n" +
                                     "<p>Controls: (key -> Game Boy button)</p>\n" +
                                     "<ul>\n" +
                                     "<li>z -> A</li>\n" +
                                     "<li>x -> B</li>\n" + 
                                     "<li>Enter -> START</li>\n" +
                                     "<li>Shift -> SELECT</li>\n" +
                                     "<li>Arrow keys -> D-PAD</li>\n" +
                                     "</ul>\n" +
                                     "Written by <a href=\"http://rsnous.com\">Omar Rizwan</a> and " +
                                     "<a href=\"https://github.com/tnecniv\">Vincent Pacelli</a> " +
                                     "(<a href=\"https://github.com/osnr/gambo\">source code</a>).", true);
		dockPanel.add(html, DockPanel.SOUTH);
		html.setWidth("400px");
	}

	public Canvas getCanvas() {
		return canvas;
	}
}
