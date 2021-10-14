package part1.ui;

import akka.actor.typed.ActorRef;
import ass1.controller.Utils;
import part1.message.Message;
import part1.message.ProcessFileReqMessage;
import part1.message.StartTaskReqMessage;
import part1.message.UIInitializedMessage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serial;
import java.util.Map;

public class GraphicUI extends JFrame implements View  {
	@Serial
	private static final long serialVersionUID = 1L;
	private final ActorRef<Message> viewActor;
//	private JFileChooser directory;
//	private JFileChooser stopwords;
	private JTextField outputCount;
	private JTextArea outputTextArea;
	private JLabel misc;
	private JButton start;
	private JButton stop;

	public GraphicUI(ActorRef<Message> viewActor) {
		this.viewActor = viewActor;

		setView();
		display();

		viewActor.tell(new UIInitializedMessage(outputTextArea));
	}

	private void setView() {
		setTitle("PDF Words counter");
		setPreferredSize(new Dimension(400, 500));
	    pack();		

//		directory = new JFileChooser("Directory");
//		directory.setCurrentDirectory(new File("./data"));
//		directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		directory.setAcceptAllFileFilterUsed(false);
//
//		stopwords = new JFileChooser("Stopwords");
//		stopwords.setCurrentDirectory(new File("./data"));
//		stopwords.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		stopwords.setAcceptAllFileFilterUsed(false);

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
//		p1.add(directory);
		p1.add(Box.createVerticalStrut(10));
		p1.add(new JLabel("File parole ignorate"));
//		p1.add(stopwords);
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
				e -> {
					viewActor.tell(new StartTaskReqMessage("C:\\Users\\Fabio\\Desktop\\pcd_workspace\\pcd-assignment-03\\data",
							"C:\\Users\\Fabio\\Desktop\\pcd_workspace\\pcd-assignment-03\\data\\stopwords.txt"));
//							stopwords.getSelectedFile().getAbsolutePath(),
//							directory.getSelectedFile().getAbsolutePath()));
					start.setEnabled(false);
					stop.setEnabled(false);
				});
		stop.addActionListener(
				e -> {
//					observer.notifyTaskStopRequest();
					start.setEnabled(false);
					stop.setEnabled(false);
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
			outputCount.setEditable(true);
			misc.setText("Completed in " + String.format("%.2f", elapsedTime) + " seconds");
		});
	}

}
