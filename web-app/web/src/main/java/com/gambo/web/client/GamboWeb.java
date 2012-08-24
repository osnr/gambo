package com.gambo.web.client;

import com.gambo.core.Mmu;
import com.gambo.web.util.Base64;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GamboWeb implements EntryPoint {
	static final String UPGRADE_MSG = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	static final int WIDTH = 160;
	static final int HEIGHT = 144;
	
	RootPanel rootPanel;
	Canvas canvas;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		
		LayoutPanel layoutPanel = new LayoutPanel();
		rootPanel.add(layoutPanel, 0, 0);
		layoutPanel.setSize("100%", "100%");
		
		WebEmulator webEmulator = new WebEmulator();
		webEmulator.setStyleName("emulator");
		layoutPanel.add(webEmulator);
		
		canvas = webEmulator.getCanvas();

        requestRom("game.gb.b64");
	}
	
	protected void requestRom(String path) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, path);
		
		try {
			builder.sendRequest(null, new GetRomCallback());
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	public void loadRom(byte[] buf) {
	    Mmu mmu = new Mmu(buf);
	    
	    WebDisplay display = new WebDisplay(mmu, canvas);
	    
	    WebInput input = new WebInput(mmu);
	    
	    rootPanel.addDomHandler(input, KeyDownEvent.getType());
	    rootPanel.addDomHandler(input, KeyUpEvent.getType());
	    
	    final WebCpu cpu = new WebCpu(mmu, display);

	    try {
	    	cpu.emulate(0x100);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.err.println("Program counter: " + Integer.toHexString(cpu.getPc()));
	    }
    }
	
	public static byte[] toBytes(String s) {
		byte[] bytes = new byte[s.length()];
		
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}
		
		return bytes;
	}
	
	class GetRomCallback implements RequestCallback {
		@Override
		public void onResponseReceived(Request request, Response response) {
			String romText = response.getText();
			if (romText.endsWith("\n")) {
				romText = romText.substring(0, romText.length() - 1); // remove ending \n
			}
			
			byte[] rom;
			rom = GamboWeb.toBytes(Base64.decode(romText));

			loadRom(rom);
		}

		@Override
		public void onError(Request request, Throwable exception) {
			Window.alert("Failed to load the ROM.");
		}
	}
}
