/* Copyright (C) Starfyre Enterprises 2022. All rights reserved. */
package src.com.starfyre1.startup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import src.com.starfyre1.engine.IntegerFilter;

public class TreasureGenerator implements ActionListener, DocumentListener {

	/*
	 * Type Copper 		Silver 		Gold 		Gems 		Jewelry 	Magic
	 * I 	50% 2D6 	50% 1D6 	10% 1D5 	- 			- 			1%
	 * II 	50% 3D6 	50% 2D6 	10% 1D8 	10% 1D3 	- 			2%
	 * III 	- 			- 			- 			15% 1D6 	25% 1D3 	3%
	 * IV 	75% 1D100 	75% 3D10 	50% 2D8 	20% 2D6 	20% 1D4 	5%
	 * V 	50% 2D100 	75% 1D100 	50% 2D12 	50% 2D8 	10% 1D6 	15%
	 * VI 	25% 1D100 	30% 1D100 	40% 1D20 	50% 3D10 	25% 1D8 	10%
	 * VII 	- 			50% 2D8 	50% 1D12 	25% 1D6 	10% 1D3 	12%
	 * VIII - 			- 			50% 2D12 	35% 2D6 	30% 1D10 	15%
	 * IX 	75% 8D100 	75% 6D100 	75% 1D100 	50% 5D6 	50% 3D10 	25%
	 * X 	50% 1D100 	50% 1D100 	50% 5D12 	50% 2D20 	50% 2D6 	25%
	 * XI 	- 			- 			50% 4D10 	50% 2D20 	50% 1D10 	15%
	 * XII 	- 			50% 2D100 	50% 8D10 	75% 2D20 	50% 2D6 	15%
	 *
	 * ** All the amounts shown are Per Monster Encountered, so if you meet two Urak-Hai, you would roll twice on TypeII. **
	 */

	/*
	 * Gems Table:
	 * Roll 1D100 for each Gem that you have, then look them up on the chart below.
	 * Roll 	Value of Gem: 			Jewelry pieces are made of precious metal
	 * 01-10 	10 Silver Pieces 		worth 1D100 X 1D8 Silver Pieces. They will
	 * 11-20	50 Silver Pieces 		will also have 1D6 Gems embedded into them.
	 * 21-50	100 Silver Pieces
	 * 51-80	500 Silver Pieces 		Note: 1 GP = 10 SP = 100 CP.
	 * 81-90	1000 Silver Pieces*
	 * 91-00	5000 Silver Pieces**
	 *
	 * 		*5% chance that this Gem is Magical, or has some spell cast upon it.
	 * 		**10% chance that this Gem is Magical, or has some spell cast upon it.
	 *
	 * To find out how to make a Magic Item, or to roll up a Random Magical Item, See "The Magic System”:
	 */

	/*****************************************************************************
	 * Constants
	 ****************************************************************************/
	private static final String			TITLE			= "Treasure Generator";																																																													//$NON-NLS-1$
	private static final String			VERSION			= " V1.0.3";																																																															//$NON-NLS-1$
	private static final String[]		LABELS			= { "CP", "SP", "GP", "Gems", "Jewelry", "Magic Items" };																																																				//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	private static final String[]		GEMS_VALUES		= { "10 SP", "50 SP", "100 SP", "500 SP", "1000 SP*", "5000 SP**" };																																																	//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	private static final int[][]		ODDS			= { { 50, 50, 10, 0, 0, 1 }, { 50, 50, 10, 10, 0, 2 }, { 0, 0, 0, 15, 25, 3 },																																															//
					{ 75, 75, 50, 20, 20, 5 }, { 50, 75, 50, 50, 10, 15 }, { 25, 30, 40, 50, 25, 10 },																																																							//
					{ 0, 50, 50, 25, 10, 12 }, { 0, 0, 50, 35, 30, 15 }, { 75, 75, 75, 50, 50, 25 },																																																							//
					{ 50, 50, 50, 50, 50, 25 }, { 0, 0, 50, 50, 50, 15 }, { 0, 50, 50, 75, 50, 15 } };																																																							//

	private static final String[][]		AMOUNT			= { { "2D6", "1D6", "1D5", "0", "0", "-1" }, { "3D6", "2D6", "1D8", "1D3", "0", "-1" },																																													// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
					{ "0", "0", "0", "1D6", "1D3", "-1" }, { "1D100", "3D10", "2D8", "2D6", "1D4", "-1" },																																																						// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
					{ "2D100", "1D100", "2D12", "2D8", "1D6", "-1" }, { "1D100", "1D100", "1D20", "3D10", "1D8", "-1" },																																																		// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
					{ "0", "2D8", "1D12", "1D6", "1D3", "-1" }, { "0", "0", "2D12", "2D6", "1D10", "-1" },																																																						// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
					{ "8D100", "6D100", "1D100", "5D6", "3D10", "-1" }, { "1D100", "1D100", "5D12", "2D20", "2D6", "-1" },																																																		// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
					{ "0", "0", "4D10", "2D20", "1D10", "-1" }, { "0", "2D100", "8D10", "2D20", "2D6", "-1" } };																																																				//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

	private static final String			DIVIDER			= new String("\n    =================================================================================================================\n\n");																															//$NON-NLS-1$
	private static final Dimension		WINDOW_SIZE		= new Dimension(1000, 1000);
	private static final JButton		GENERATE_BUTTON	= new JButton("Generate");																																																												//$NON-NLS-1$
	private static final JButton		CLEAR_BUTTON	= new JButton("Clear");																																																													//$NON-NLS-1$

	/*****************************************************************************
	 * Member Variables
	 ****************************************************************************/
	private static JFrame				mFrame;
	private static Random				mRandom			= new Random();
	private static TreasureGenerator	mInstance;

	private JTextArea					mResultsView;
	private JTextField					mEncountersEntry;
	private JTextField					mTeirEntry;
	// only used for Jewelry with mounted gems
	private int							mGemValue;

	/*****************************************************************************
	 * Constructors
	 ****************************************************************************/
	private TreasureGenerator() {
		mInstance = this;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception ex) {
					System.err.println(ex);
				}
				mFrame = new JFrame(TITLE + VERSION);
				mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

				JPanel display = entryDisplay();

				mFrame.add(display);
				mFrame.setSize(WINDOW_SIZE);
				mFrame.setVisible(true);
			}
		});
	}

	public static void main(String[] args) {
		new TreasureGenerator();
	}

	/*****************************************************************************
	 * Methods
	 ****************************************************************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("Generate")) { //$NON-NLS-1$
			int number = Integer.parseInt(mEncountersEntry.getText().trim());
			int tier = Integer.parseInt(mTeirEntry.getText().trim());
			mResultsView.append(treasure(tier, number));
		} else if (command.equals("Clear")) { //$NON-NLS-1$
			mResultsView.setText(""); //$NON-NLS-1$
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		buttonUpdate();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		buttonUpdate();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// nothing to do
	}

	private void buttonUpdate() {
		GENERATE_BUTTON.setEnabled(!(mEncountersEntry.getText().isBlank() || mTeirEntry.getText().isBlank()));
		CLEAR_BUTTON.setEnabled(mResultsView.getDocument().getLength() > 0);
	}

	private JPanel entryDisplay() {
		JPanel outerWrapper = new JPanel(new BorderLayout());
		outerWrapper.setBorder(new CompoundBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED), new EtchedBorder(EtchedBorder.RAISED)), new EtchedBorder(EtchedBorder.LOWERED)));

		JPanel upperWrapper = new JPanel();
		BoxLayout bl = new BoxLayout(upperWrapper, BoxLayout.Y_AXIS);
		upperWrapper.setLayout(bl);

		JLabel encounters = new JLabel("Monsters Encountered (1-500)", SwingConstants.RIGHT); //$NON-NLS-1$
		mEncountersEntry = new JTextField(5);
		AbstractDocument document = (AbstractDocument) mEncountersEntry.getDocument();
		document.setDocumentFilter(new IntegerFilter(500));
		document.addDocumentListener(this);
		JPanel wrapper = new JPanel();
		wrapper.add(encounters);
		wrapper.add(mEncountersEntry);
		upperWrapper.add(wrapper);

		JLabel teir = new JLabel("Treasure Tier (1-12)", SwingConstants.RIGHT); //$NON-NLS-1$
		mTeirEntry = new JTextField(5);
		document = (AbstractDocument) mTeirEntry.getDocument();
		document.setDocumentFilter(new IntegerFilter(12));
		document.addDocumentListener(this);
		wrapper = new JPanel();
		wrapper.add(teir);
		wrapper.add(mTeirEntry);
		upperWrapper.add(wrapper);
		Dimension size = encounters.getPreferredSize();
		Dimension temp = teir.getPreferredSize();
		if (temp.getWidth() > size.getWidth()) {
			size.setSize(temp);
		}
		encounters.setPreferredSize(size);
		teir.setPreferredSize(size);
		upperWrapper.add(Box.createVerticalGlue());

		GENERATE_BUTTON.addActionListener(mInstance);
		GENERATE_BUTTON.setEnabled(false);
		CLEAR_BUTTON.addActionListener(mInstance);
		CLEAR_BUTTON.setEnabled(false);
		wrapper = new JPanel();
		wrapper.add(GENERATE_BUTTON);
		wrapper.add(CLEAR_BUTTON);
		upperWrapper.add(wrapper);

		mResultsView = new JTextArea();
		mResultsView.setMinimumSize(new Dimension(400, 400));
		mResultsView.getDocument().addDocumentListener(this);

		JScrollPane scrollPane = new JScrollPane(mResultsView);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		outerWrapper.add(upperWrapper, BorderLayout.NORTH);
		outerWrapper.add(scrollPane, BorderLayout.CENTER);

		return outerWrapper;

	}

	/*
	 *  @param tier a value from 1 - 12
	 *  @param type a value from 0 - 5 (cp, sp, gp, gem, jewelry, magic)
	 */
	private String treasure(int tier, int number) {
		StringBuilder display = new StringBuilder();

		for (int i = 0; i < 6; i++) {
			int value = 0;
			String temp = AMOUNT[tier - 1][i];
			int percent = ODDS[tier - 1][i];
			for (int j = 0; j < number; j++) {
				if (temp.equals("-1")) { //$NON-NLS-1$
					int chance = rollDice(1, 100);
					if (percent >= chance) {
						value++;
					}
				} else if (!temp.equals("0")) { //$NON-NLS-1$
					int chance = rollDice(1, 100);
					if (percent >= chance) {
						int dice = Integer.parseInt(temp.substring(0, 1));
						int die = Integer.parseInt(temp.substring(2));
						value += rollDice(dice, die);
					}
				}
			}

			display.append(value + " " + LABELS[i]); //$NON-NLS-1$
			if (i == 3 && value > 0) {
				display.append("\n" + gems(value, false)); //$NON-NLS-1$
			} else if (i == 4 && value > 0) {
				display.append("\n" + jewelry(value)); //$NON-NLS-1$
			} else if (i == 5 && value > 0) {
				display.append("\n" + magic(value)); //$NON-NLS-1$
			}
			display.append("\n"); //$NON-NLS-1$
		}
		display.append(DIVIDER);

		return display.toString();
	}

	StringBuilder mAmount;

	private String gems(int count, boolean fromJewelry) {
		mAmount = new StringBuilder();
		int[] gems = { 0, 0, 0, 0, 0, 0 };
		mGemValue = 0;

		for (int i = 0; i < count; i++) {
			int roll = rollDice(1, 100);
			if (roll < 11) {
				// Value 10 SP
				gems[0]++;
				mGemValue += 10;
			} else if (roll < 21) {
				// Value 50 SP
				gems[1]++;
				mGemValue += 50;
			} else if (roll < 51) {
				// Value 100 SP
				gems[2]++;
				mGemValue += 100;
			} else if (roll < 81) {
				// Value 500 SP
				gems[3]++;
				mGemValue += 500;
			} else if (roll < 91) {
				// Value 1000 SP
				gems[4]++;
				mGemValue += 1000;
			} else {
				// Value 5000 SP
				gems[5]++;
				mGemValue += 5000;
			}
		}

		for (int i = 0; i < gems.length; i++) {
			int gem = gems[i];
			if (gem > 0) {
				if (i == 4) {
					// 5% is magical or has spell cast on it
					mAmount.append(magicGems(gem, 95, fromJewelry));
				} else if (i == 5) {
					// 10% is magical or has spell cast on it
					mAmount.append(magicGems(gem, 90, fromJewelry));
				} else {
					mAmount.append((fromJewelry ? "\n\t" : "") + "\t[" + gem + " Gems = " + GEMS_VALUES[i] + "]" + (fromJewelry ? "" : "\n")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				}
			}
		}

		return mAmount.toString();
	}

	private String magicGems(int count, int percentage, boolean fromJewelry) {
		String text = ""; //$NON-NLS-1$
		String other = ""; //$NON-NLS-1$
		for (int i = 0; i < count; i++) {
			int roll = rollDice(1, 100);
			if (roll > percentage) {
				count--;
				String charges = charges();
				String magicArea = magicArea();
				String powers = itemPowers();

				text += "\n\t\t[1 Gem = " + GEMS_VALUES[percentage == 95 ? 4 : 5] + "] [Charging Type: " + charges + "] [Spell Area: " + magicArea + "] [Spells: " + powers + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}
		}
		if (count > 0) {
			if (percentage == 95) {
				other = (fromJewelry ? "\n\t" : "") + "\t[" + count + " Gems = " + GEMS_VALUES[4] + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			} else {
				String addNewLine = ""; //$NON-NLS-1$
				System.out.println(other);
				if (!other.isEmpty()) {
					String test = other.substring(other.length() - 2);
					System.out.println("::" + test + "::"); //$NON-NLS-1$ //$NON-NLS-2$
					if (test.equals("]\t")) { //$NON-NLS-1$
						addNewLine = "\n"; //$NON-NLS-1$
					}
				}
				other = addNewLine + (fromJewelry ? "\n\t" : "") + "\t[" + count + " Gems = " + GEMS_VALUES[5] + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}
		}
		return other + text + (fromJewelry ? "" : "\n"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String jewelry(int count) {
		StringBuilder amount = new StringBuilder();

		for (int i = 0; i < count; i++) {
			int value = rollDice(1, 100) * rollDice(1, 8);
			int mountedGems = rollDice(1, 6);
			String gems = gems(mountedGems, true);
			value += mGemValue;
			amount.append("\n\t{" + value + " SP Jewelry"); //$NON-NLS-1$ //$NON-NLS-2$
			amount.append((value < 100 ? "\t" : "") + gems); //$NON-NLS-1$ //$NON-NLS-2$
			amount.append(" }\n"); //$NON-NLS-1$
		}

		return amount.toString();
	}

	private String magic(int count) {
		StringBuilder amount = new StringBuilder();

		for (int i = 0; i < count; i++) {
			int value = rollDice(1, 100);
			amount.append("\t{"); //$NON-NLS-1$
			if (value < 21) {
				amount.append(magicWeapon());
			} else if (value < 41) {
				amount.append(magicArmor());
			} else if (value < 81) {
				amount.append(miscMagic());
			} else if (value < 91) {
				amount.append(magicWeapon());
				amount.append("    AND    "); //$NON-NLS-1$
				amount.append(miscMagic());
			} else {
				amount.append(magicArmor());
				amount.append("    AND    "); //$NON-NLS-1$
				amount.append(miscMagic());
			}
			amount.append(" }\n"); //$NON-NLS-1$
		}
		return amount.toString();
	}

	private String magicWeapon() {
		int value = rollDice(1, 100);

		if (value < 26) {
			return "[" + getMetal() + " Longsword with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (value < 36) {
			return "[" + getMetal() + " Broadsword with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (value < 41) {
			return "[" + getMetal() + " Katana with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (value < 51) {
			return "[" + getMetal() + " Saber with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (value < 56) {
			return "[" + getMetal() + " Claymore with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (value < 66) {
			return "[" + getMetal() + " Bastard Sword with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (value < 76) {
			return "[" + getMetal() + " Two-Handed Sword with " + runes() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return miscWeapon();
	}

	private String getMetal() {
		int value = rollDice(1, 100);

		if (value < 51) {
			return "Borang"; //$NON-NLS-1$
		} else if (value < 71) {
			return "Ardacer"; //$NON-NLS-1$
		} else if (value < 86) {
			return "Ethru"; //$NON-NLS-1$
		} else if (value < 96) {
			return "Ithilnar"; //$NON-NLS-1$
		} else if (value < 100) {
			return "Mithril"; //$NON-NLS-1$
		}
		return "Eog"; //$NON-NLS-1$
	}

	private String miscWeapon() {
		int value = rollDice(1, 100);

		if (value < 6) {
			return "Mace (Light)"; //$NON-NLS-1$
		} else if (value < 11) {
			return "Mace (Heavy)"; //$NON-NLS-1$
		} else if (value < 21) {
			return "War Hammer"; //$NON-NLS-1$
		} else if (value < 26) {
			return "Wakazashi"; //$NON-NLS-1$
		} else if (value < 36) {
			return "Large Axe"; //$NON-NLS-1$
		} else if (value < 46) {
			return "Great Axe"; //$NON-NLS-1$
		} else if (value < 61) {
			return "Staff"; //$NON-NLS-1$
		} else if (value < 66) {
			return "Scythe"; //$NON-NLS-1$
		} else if (value < 71) {
			return "Whip"; //$NON-NLS-1$
		} else if (value < 76) {
			return "Shuriken"; //$NON-NLS-1$
		} else if (value < 81) {
			return "Spear"; //$NON-NLS-1$
		} else if (value < 86) {
			return "Throwing Spikes"; //$NON-NLS-1$
		} else if (value < 91) {
			return "Bow *"; //$NON-NLS-1$
		} else if (value < 96) {
			return "Bow Composite **"; //$NON-NLS-1$
		}
		return "Crossbow ***"; //$NON-NLS-1$
	}

	private String runes() {
		int value = rollDice(1, 100);
		if (value < 51) {
			return "Rune of Combat I"; //$NON-NLS-1$
		} else if (value < 81) {
			return "Rune of Combat II"; //$NON-NLS-1$
		} else if (value < 93) {
			return "Rune of Combat III"; //$NON-NLS-1$
		} else if (value < 98) {
			return "Rune of Combat IV"; //$NON-NLS-1$
		} else if (value < 100) {
			return "Rune of Combat V"; //$NON-NLS-1$
		}
		return " Holy Sword"; //$NON-NLS-1$
	}

	private String magicArmor() {
		int value = rollDice(1, 100);
		String type;
		if (value < 5) {
			type = "Heavy Cloth"; //$NON-NLS-1$
		} else if (value < 9) {
			type = getMetal() + " Ring / Cloth"; //$NON-NLS-1$
		} else if (value < 11) {
			type = getMetal() + " Laminate"; //$NON-NLS-1$
		} else if (value < 16) {
			type = "Leather"; //$NON-NLS-1$
		} else if (value < 30) {
			type = getMetal() + " Studded Leather"; //$NON-NLS-1$
		} else if (value < 45) {
			type = getMetal() + " Ring Mail"; //$NON-NLS-1$
		} else if (value < 55) {
			type = getMetal() + " Scale Mail"; //$NON-NLS-1$
		} else if (value < 70) {
			type = getMetal() + " Chain Mail"; //$NON-NLS-1$
		} else if (value < 80) {
			type = getMetal() + " Banded Mail"; //$NON-NLS-1$
		} else if (value < 90) {
			type = getMetal() + " Plate Mail"; //$NON-NLS-1$
		} else {
			type = getMetal() + " Field Plate"; //$NON-NLS-1$
		}

		String spell = defensiveSpell();

		return "[" + type + " w/ " + spell + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private String defensiveSpell() {
		int value = rollDice(1, 100);
		if (value < 21) {
			return "Shield I"; //$NON-NLS-1$
		} else if (value < 31) {
			return "Shield II"; //$NON-NLS-1$
		} else if (value < 36) {
			return "Shield III"; //$NON-NLS-1$
		} else if (value < 39) {
			return "Shield IV"; //$NON-NLS-1$
		} else if (value < 41) {
			return "Shield V"; //$NON-NLS-1$
		} else if (value < 51) {
			return "Absorb I"; //$NON-NLS-1$
		} else if (value < 56) {
			return "Absorb II"; //$NON-NLS-1$
		} else if (value < 61) {
			return "Absorb III"; //$NON-NLS-1$
		} else if (value < 71) {
			return "Protection I"; //$NON-NLS-1$
		} else if (value < 76) {
			return "Protection II"; //$NON-NLS-1$
		} else if (value < 81) {
			return "Protection III"; //$NON-NLS-1$
		} else if (value < 86) {
			return "Protection IV"; //$NON-NLS-1$
		} else if (value < 89) {
			return "Protection V"; //$NON-NLS-1$
		} else if (value < 90) {
			return "Protection VI"; //$NON-NLS-1$
		} else if (value < 91) {
			return "Protection VII"; //$NON-NLS-1$
		} else if (value < 92) {
			return "Protection: Aura"; //$NON-NLS-1$
		} else if (value < 93) {
			return "Protection: Animals"; //$NON-NLS-1$
		} else if (value < 94) {
			return "Protection: Charms"; //$NON-NLS-1$
		} else if (value < 95) {
			return "Protection: Cold"; //$NON-NLS-1$
		} else if (value < 96) {
			return "Protection: Fire"; //$NON-NLS-1$
		} else if (value < 97) {
			return "Protection: Dark"; //$NON-NLS-1$
		} else if (value < 98) {
			return "Protection: Demons"; //$NON-NLS-1$
		} else if (value < 99) {
			return "Protection: Fear"; //$NON-NLS-1$
		} else if (value < 100) {
			return "Protection: Lightning"; //$NON-NLS-1$
		}
		return "Protection: Missiles"; //$NON-NLS-1$
	}

	private String miscMagic() {
		int value = rollDice(1, 100);
		String type;

		if (value < 18) {
			type = "Ring"; //$NON-NLS-1$
		} else if (value < 26) {
			type = "Medallion"; //$NON-NLS-1$
		} else if (value < 31) {
			type = "Cloak / Robe"; //$NON-NLS-1$
		} else if (value < 36) {
			type = "Wand"; //$NON-NLS-1$
		} else if (value < 51) {
			type = "Staff"; //$NON-NLS-1$
		} else if (value < 57) {
			type = "Book / Scroll"; //$NON-NLS-1$
		} else if (value < 60) {
			type = "Helm"; //$NON-NLS-1$
		} else if (value < 76) {
			type = "Gem / Jewelry"; //$NON-NLS-1$
			// DW Jewelry 75% Gem 25%
		} else if (value < 86) {
			type = "Weapon"; //$NON-NLS-1$
		} else if (value < 91) {
			type = "Armor"; //$NON-NLS-1$
		} else {
			type = "Miscellaneous (GM's Decision)"; //$NON-NLS-1$
		}

		String charges = charges();
		String magicArea = magicArea();
		String powers = itemPowers();

		return "[Misc: " + type + " [Charging Type: " + charges + "] [Spell Area: " + magicArea + "] [Spells: " + powers + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	}

	private String charges() {
		int value = rollDice(1, 100);
		if (value < 31) {
			return "Continually Functioning"; //$NON-NLS-1$
		} else if (value < 71) {
			return "User Recharge"; //$NON-NLS-1$
		}
		return "Recharges @ 5 Mana / hour"; //$NON-NLS-1$
	}

	private String magicArea() {
		int value = rollDice(1, 100);

		if (value < 6) {
			return "Earth"; //$NON-NLS-1$
		} else if (value < 10) {
			return "Air"; //$NON-NLS-1$
		} else if (value < 14) {
			return "Fire"; //$NON-NLS-1$
		} else if (value < 19) {
			return "Water"; //$NON-NLS-1$
		} else if (value < 26) {
			return "Natural Lore"; //$NON-NLS-1$
		} else if (value < 37) {
			return "Arcane Lore"; //$NON-NLS-1$
		} else if (value < 42) {
			return "Shadow"; //$NON-NLS-1$
		} else if (value < 48) {
			return "Control"; //$NON-NLS-1$
		} else if (value < 56) {
			return "Necromancy"; //$NON-NLS-1$
		} else if (value < 61) {
			return "Adon"; //$NON-NLS-1$
		} else if (value < 66) {
			return "Pelon"; //$NON-NLS-1$
		} else if (value < 71) {
			return "Narius"; //$NON-NLS-1$
		} else if (value < 76) {
			return "Mistress Night"; //$NON-NLS-1$
		} else if (value < 81) {
			return "Tarn"; //$NON-NLS-1$
		} else if (value < 86) {
			return "Lorrell"; //$NON-NLS-1$
		} else if (value < 91) {
			return "Wynd"; //$NON-NLS-1$
		} else if (value < 94) {
			return "Ryelle"; //$NON-NLS-1$
		} else if (value < 95) {
			return "Tarot"; //$NON-NLS-1$
		} else if (value < 96) {
			return "Chauntil"; //$NON-NLS-1$
		} else if (value < 97) {
			return "Talon"; //$NON-NLS-1$
		} else if (value < 98) {
			return "Orn"; //$NON-NLS-1$
		} else if (value < 99) {
			return "Sarn"; //$NON-NLS-1$
		} else if (value < 100) {
			return "Nerese"; //$NON-NLS-1$
		}
		return "Thaer"; //$NON-NLS-1$

	}

	private String itemPowers() {
		int value = rollDice(1, 100);

		if (value < 6) {
			return "Cursed [" + curse() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 36) {
			return "1 Spell (Power " + (rollDice(1, 3) - 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 51) {
			return "1 Spell (Power " + rollDice(1, 3) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 58) {
			return "1 Spell (Power " + (rollDice(1, 3) + 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 68) {
			return "1 Spell (Power " + (rollDice(1, 6) - 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 76) {
			return "1 Spell (Power " + rollDice(1, 6) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 81) {
			return "1 Spell (Power " + (rollDice(1, 6) + 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 85) {
			return "2 Spell (Power " + (rollDice(1, 6) - 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 87) {
			return "2 Spell (Power " + rollDice(1, 6) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 89) {
			return "3 Spell (Power " + (rollDice(1, 3) - 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 91) {
			return "3 Spell (Power " + (rollDice(1, 6) - 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 93) {
			return "1 Spell (Power " + (rollDice(1, 6) + 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 95) {
			return "1 Spell (Power " + (rollDice(1, 6) + 2) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value < 97) {
			return "2 Spell (Power " + (rollDice(1, 6) + 2) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return itemPowers() + "    AND    " + itemPowers(); //$NON-NLS-1$

	}

	private String curse() {
		int value = rollDice(1, 100);

		if (value < 11) {
			return "Save Vs. Magic or take 2D20 Points of Damage when touched."; //$NON-NLS-1$
		} else if (value < 21) {
			return "Save Vs. Stress or go insane for 1D100 Days."; //$NON-NLS-1$
		} else if (value < 31) {
			return "This item has been enchanted with an \" Entity of Evil \" spell."; //$NON-NLS-1$
		} else if (value < 41) {
			return "Character touching this item is the recipient of a \" Call to Law/Chaos\" spell."; //$NON-NLS-1$
		} else if (value < 56) {
			return "Take 1D8+2 Hit Points of Damage when touched."; //$NON-NLS-1$
		} else if (value < 61) {
			return "Take 2D8 Hit Points of Damage when the item is activated."; //$NON-NLS-1$
		} else if (value < 66) {
			return "Causes Blackouts (as per Spell Failure Chart) 20% chance per hour."; //$NON-NLS-1$
		} else if (value < 69) {
			return "Character touching this item is the recipient of a \" Level Loss I “spell."; //$NON-NLS-1$
		} else if (value < 76) {
			return "Save Vs. Magic or be the recipient of \" Sleep Eternal \" spell."; //$NON-NLS-1$
		} else if (value < 86) {
			return "Lose (1) point of a random requisite permanently."; //$NON-NLS-1$
		} else if (value < 91) {
			return "Causes the character with this in their posession to go \" Berserk \", as per the Warrior section, whenever in \" Hand - to - Hand \" combat."; //$NON-NLS-1$
		} else if (value < 96) {
			return "Lowers your Armor Rating by 15% when this item is worn."; //$NON-NLS-1$
		}
		return "When you are wearing this item, if you are hit, you will take DOUBLE damage, and an automatic CRITICAL HIT."; //$NON-NLS-1$
	}

	private int rollDice(int numOfDie, int sizeOfDie) {
		int value = 0;
		for (int i = 0; i < numOfDie; i++) {
			value += (int) (mRandom.nextDouble() * sizeOfDie + 1);
		}
		return value;
	}

	/*****************************************************************************
	 * Setter's and Getter's
	 ****************************************************************************/

	/*****************************************************************************
	 * Serialization
	 ****************************************************************************/

}
