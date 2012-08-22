package com.gambo.web.client;

import java.util.ArrayList;
import java.util.List;

import com.gambo.core.Mmu;
import com.gambo.core.Mmu.Inputs;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class WebInput implements KeyDownHandler, KeyUpHandler {
	protected Mmu mmu;
	
	private List<Integer> pressed = new ArrayList<Integer>();

	public WebInput(Mmu mmu) {
		this.mmu = mmu;
	}
	
	private static native void log(String str) /*-{
		console.log(str);
	}-*/;

	@Override
	public void onKeyDown(KeyDownEvent event) {
		NativeEvent e = event.getNativeEvent();
		int code = e.getKeyCode();
		
		if (pressed.contains(code)) return;

	    switch (code) {
	    case 'X':
	    	mmu.inputs.pressedButton(Inputs.BTN_B);
            break;
	    case 'Z':
	    	mmu.inputs.pressedButton(Inputs.BTN_A);
            break;
	    case KeyCodes.KEY_SHIFT:
	    	mmu.inputs.pressedButton(Inputs.BTN_SELECT);
            break;
	    case KeyCodes.KEY_ENTER:
	    	mmu.inputs.pressedButton(Inputs.BTN_START);
            break;
	    case KeyCodes.KEY_RIGHT:
	    	mmu.inputs.pressedDpad(Inputs.DPD_RIGHT);
            break;
	    case KeyCodes.KEY_LEFT:
	    	mmu.inputs.pressedDpad(Inputs.DPD_LEFT);
            break;
	    case KeyCodes.KEY_UP:
	    	mmu.inputs.pressedDpad(Inputs.DPD_UP);
            break;
	    case KeyCodes.KEY_DOWN:
	    	mmu.inputs.pressedDpad(Inputs.DPD_DOWN);
	    }
	    
	    pressed.add(code); // need to track presses ourselves because keyPress sucks
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		NativeEvent e = event.getNativeEvent();
		int code = e.getKeyCode();
		
		pressed.remove(new Integer(code));
		
	    switch (code) {
	    case 'X':
	    	mmu.inputs.unpressedButton(Inputs.BTN_B);
            break;
	    case 'Z':
	    	mmu.inputs.unpressedButton(Inputs.BTN_A);
            break;
	    case KeyCodes.KEY_SHIFT:
	    	mmu.inputs.unpressedButton(Inputs.BTN_SELECT);
            break;
	    case KeyCodes.KEY_ENTER:
	    	mmu.inputs.unpressedButton(Inputs.BTN_START);
            break;
	    case KeyCodes.KEY_RIGHT:
	    	mmu.inputs.unpressedDpad(Inputs.DPD_RIGHT);
            break;
	    case KeyCodes.KEY_LEFT:
	    	mmu.inputs.unpressedDpad(Inputs.DPD_LEFT);
            break;
	    case KeyCodes.KEY_UP:
	    	mmu.inputs.unpressedDpad(Inputs.DPD_UP);
            break;
	    case KeyCodes.KEY_DOWN:
	    	mmu.inputs.unpressedDpad(Inputs.DPD_DOWN);
	    }
	}
}
