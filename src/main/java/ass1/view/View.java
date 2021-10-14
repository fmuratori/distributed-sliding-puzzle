package ass1.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ass1.controller.Utils;
import ass1.controller.ViewObserver;

public class View extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField directory;
	private JTextField stopwords;
	private JTextField outputCount;
	private JTextArea outputTextArea;
	private JLabel misc;
	private JButton start;
	private JButton stop;
	private ViewObserver observer;
	
	public View(ViewObserver observer) {
		this.observer = observer;
		setView();
		display();
	}

	private void setView() {
		setTitle("PDF Words counter");
		setPreferredSize(new Dimension(400, 500));
	    pack();		

		directory = new JTextField("Directory");
		directory.setEditable(true);		
		directory.setText("./data");
		
		stopwords = new JTextField("Stopwords");
		stopwords.setEditable(true);		
		stopwords.setText("./data/stopwords.txt");
		
		outputCount = new JTextField("N");
		outputCount.setEditable(true);		
		outputCount.setText("10");
		
		outputTextArea = new JTextArea(5, 20);
		outputTextArea.setEditable(false);
		outputTextArea.setText("");
		
		start = new JButton("Start");
		start.setEnabled(true);
		
		stop  = new JButton("Stop");
		stop.setEnabled(false);
		
		misc = new JLabel("IDLE");
		
		Container cp = getContentPane();
		JPanel panel = new JPanel();
		
		Box p1 = new Box(BoxLayout.Y_AXIS);
		p1.add(new JLabel("Cartella"));
		p1.add(directory);
		p1.add(Box.createVerticalStrut(10));
		p1.add(new JLabel("File parole ignorate"));
		p1.add(stopwords);
		p1.add(Box.createVerticalStrut(10));
		p1.add(new JLabel("Numero risultati"));
		p1.add(outputCount);
		
		Box p2 = new Box(BoxLayout.X_AXIS);
		p2.add(start);
		p2.add(stop);
		
		Box p3 = new Box(BoxLayout.Y_AXIS);
		p3.add(p1);
		p3.add(Box.createVerticalStrut(10));
		p3.add(p2);
		p3.add(Box.createVerticalStrut(10));
		p3.add(misc);
		p3.add(Box.createVerticalStrut(10));
		p3.add(outputTextArea);
		
		panel.add(p3);
		cp.add(panel);

		start.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					observer.notifyTaskStartRequest(directory.getText(), stopwords.getText());
					start.setEnabled(false);
					stop.setEnabled(false);
				}
			});
		stop.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					observer.notifyTaskStopRequest();
					start.setEnabled(false);
					stop.setEnabled(false);
				}
			});
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void display() {
        SwingUtilities.invokeLater(() -> {
        	this.setVisible(true);
        });
    }

	public void handleStartingExecution() {
		SwingUtilities.invokeLater(() -> {
			misc.setText("Starting ...");
        });
	}

	public void handleStartedExecution() {
		SwingUtilities.invokeLater(() -> {
			misc.setText("Running ...");
			start.setEnabled(false);
			stop.setEnabled(true);
			directory.setEditable(false);
			stopwords.setEditable(false);
			outputCount.setEditable(false);
        });
	}
	
	public void handleUpdate(Map<String, Integer> dictionary, double progress) {
		SwingUtilities.invokeLater(() -> {
			outputTextArea.setText(Utils.stringifyMap(dictionary, Integer.parseInt(outputCount.getText())));
			misc.setText("Progress " + String.format("%.2f", progress*100) + "%");
		});
	}

	public void handleStoppingExecution() {
		SwingUtilities.invokeLater(() -> {
			misc.setText("Stopping ...");
			start.setEnabled(false);
			stop.setEnabled(false);
		});
	}

	public void handleCompletedExecution(Map<String, Integer> dictionary, double elapsedTime) {
		SwingUtilities.invokeLater(() -> {
			outputTextArea.setText(Utils.stringifyMap(dictionary, Integer.parseInt(outputCount.getText())));
			start.setEnabled(true);
			stop.setEnabled(false);
			directory.setEditable(true);
			stopwords.setEditable(true);
			outputCount.setEditable(true);
			misc.setText("Completed in " + String.format("%.2f", elapsedTime) + " seconds");
		});
	}

}
