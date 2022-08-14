/* Copyright (C) Starfyre Enterprises 2022. All rights reserved. */

package src.com.starfyre1.engine;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class IntegerFilter extends DocumentFilter {
	/*****************************************************************************
	 * Constants
	 ****************************************************************************/

	/*****************************************************************************
	 * Member Variables
	 ****************************************************************************/
	private static IntegerFilter	sInstance	= null;
	private int						mLimit		= -1;

	/*****************************************************************************
	 * Constructors
	 ****************************************************************************/
	private IntegerFilter() {

	}

	public IntegerFilter(int limit) {
		mLimit = limit;
	}

	/*****************************************************************************
	 * Methods
	 ****************************************************************************/
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.insert(offset, string);

		if (test(sb.toString())) {
			super.insertString(fb, offset, string, attr);
		} else {
			// warn the user and don't allow the insert
		}
	}

	private boolean test(String text) {
		try {
			int value = Integer.parseInt(text);
			if (mLimit > 0 && (value < 1 || value > mLimit)) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			if (text.isEmpty()) {
				return true;
			}
			return false;
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.replace(offset, offset + length, text);

		if (test(sb.toString())) {
			super.replace(fb, offset, length, text, attrs);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}

	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);

		if (test(sb.toString())) {
			super.remove(fb, offset, length);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}

	}

	/*****************************************************************************
	 * Setter's and Getter's
	 ****************************************************************************/
	public static IntegerFilter getFilterInstance() {
		if (sInstance == null) {
			sInstance = new IntegerFilter();
		}

		return sInstance;
	}

	/*****************************************************************************
	 * Serialization
	 ****************************************************************************/
}
