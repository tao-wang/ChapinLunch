package edu.chapin;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChapinLunchUI extends JFrame {
	private static final String STRING_BUTTON_EXPORT = "Summarize";
	private static final String STRING_BUTTON_LOADUS = "Load 6-12/PC Data";
	private static final String STRING_BUTTON_LOADLS = "Load 1-5 Data";
	private static final int UI_PADDING = 8;
	private static final int UI_COLS = 2;
	private static final int UI_ROWS = 3;
	private static final int WINDOW_HEIGHT = 240;
	private static final int WINDOW_WIDTH = 320;
	private static final String WINDOW_TITLE = "Chapin Lunch";
	private static final long serialVersionUID = -439187681523726864L;
	
	JButton loadLS;
	JButton loadUS;
	JButton export;
	JLabel statusLS;
	JLabel statusUS;
	JLabel statusExport;
	
	LunchData dataLS;
	LunchData dataUS;

	public ChapinLunchUI() {
		initUI();
		
		setTitle(WINDOW_TITLE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void initUI() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(UI_PADDING, UI_PADDING, UI_PADDING, UI_PADDING));
		panel.setLayout(new GridLayout(UI_ROWS, UI_COLS, UI_PADDING, UI_PADDING));
		
		loadLS = new JButton(STRING_BUTTON_LOADLS);
		loadUS = new JButton(STRING_BUTTON_LOADUS);
		export = new JButton(STRING_BUTTON_EXPORT);
		export.setEnabled(false);
		export.addActionListener(new ExportActionListener());
		
		LoadActionListener loadListener = new LoadActionListener();
		loadLS.addActionListener(loadListener);
		loadUS.addActionListener(loadListener);
		
		statusLS = new JLabel();
		statusUS = new JLabel();
		statusExport = new JLabel();
		
		panel.add(loadLS); panel.add(statusLS);
		panel.add(loadUS); panel.add(statusUS);
		panel.add(export); panel.add(statusExport);
		
		add(panel);
		pack();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				ChapinLunchUI app = new ChapinLunchUI();
				app.setVisible(true);
			}
			
		});

	}
	
	private class LoadActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int returnVal = fileChooser.showOpenDialog(ChapinLunchUI.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = fileChooser.getSelectedFile().getName();
				String filePath = fileChooser.getSelectedFile().getPath();
				
				try {
					if (event.getSource() == loadLS) {
						dataLS = new LunchData(filePath, LunchData.LSDATA);
						statusLS.setText(fileName);
					}
					else {
						dataUS = new LunchData(filePath, LunchData.USDATA);
						statusUS.setText(fileName);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (dataLS != null && dataUS != null) {
					export.setEnabled(true);
				}
				
			}
		}
		
	}
	
	private class ExportActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			try {
				Summarizer summarizer = new Summarizer(dataLS, dataUS);
				summarizer.summarize();
				statusExport.setText("Done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
