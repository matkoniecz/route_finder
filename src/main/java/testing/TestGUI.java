package testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import bee.Hive;
import bee.roundtrip.Cycle;
import bee.roundtrip.Location;
import graph.Graph;
import osm_processing.OSMDataDownloader;

public class TestGUI extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243286810278326679L;
	//private JTextField beesTotal;
	private JTextField beesInactive;
	private JTextField beesActive;
	private JTextField beesScout;
	private JTextField visits;
	private JTextField cycles;
	
	private JTextArea feedback;

	public TestGUI(){
		this.setLayout(new GridLayout(1,2));
		
			JPanel left = new JPanel();
			left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));
			/*
				JPanel totalP = new JPanel(new BorderLayout());
					JLabel labelTotal = new JLabel("Bees total:");
					totalP.add(labelTotal,BorderLayout.WEST);
					beesTotal = new JTextField("10");
					beesTotal.setEditable(true);
					totalP.add(beesTotal,BorderLayout.CENTER);
					totalP.setMaximumSize(new Dimension(beesTotal.getMaximumSize().width,beesTotal.getPreferredSize().height));
				left.add(totalP);
				*/
				JPanel inactiveP = new JPanel(new BorderLayout());
					JLabel labelInactive = new JLabel("Inactive bees:");
					inactiveP.add(labelInactive, BorderLayout.WEST);
					beesInactive = new JTextField("2");
					beesInactive.setEditable(true);
					inactiveP.add(beesInactive, BorderLayout.CENTER);
					inactiveP.setMaximumSize(new Dimension(beesInactive.getMaximumSize().width,beesInactive.getPreferredSize().height));
				left.add(inactiveP);
				
				JPanel activeP = new JPanel(new BorderLayout());
					JLabel labelActive = new JLabel("Active bees:");
					activeP.add(labelActive, BorderLayout.WEST);
					beesActive = new JTextField("5");
					beesInactive.setEditable(true);
					activeP.add(beesActive, BorderLayout.CENTER);
					activeP.setMaximumSize(new Dimension(beesActive.getMaximumSize().width,beesActive.getPreferredSize().height));
				left.add(activeP);
				
				JPanel scoutP = new JPanel(new BorderLayout());
					JLabel labelScout = new JLabel("Scout bees:");
					scoutP.add(labelScout, BorderLayout.WEST);
					beesScout = new JTextField("3");
					beesScout.setEditable(true);
					scoutP.add(beesScout, BorderLayout.CENTER);
					scoutP.setMaximumSize(new Dimension(beesScout.getMaximumSize().width,beesScout.getPreferredSize().height));
				left.add(scoutP);
				
				JPanel visitsP = new JPanel(new BorderLayout());
					JLabel labelVisits = new JLabel("Max visits:");
					visitsP.add(labelVisits, BorderLayout.WEST);
					visits = new JTextField("100");
					visits.setEditable(true);
					visitsP.add(visits, BorderLayout.CENTER);
					visitsP.setMaximumSize(new Dimension(visits.getMaximumSize().width,visits.getPreferredSize().height));
				left.add(visitsP);
				
				JPanel cyclesP = new JPanel(new BorderLayout());
					JLabel labelCycles = new JLabel("Max cycles:");
					cyclesP.add(labelCycles, BorderLayout.WEST);
					cycles = new JTextField("500");
					cycles.setEditable(true);
					cyclesP.add(cycles, BorderLayout.CENTER);
					cyclesP.setMaximumSize(new Dimension(cycles.getMaximumSize().width,cycles.getPreferredSize().height));
				left.add(cyclesP);
				
				feedback = new JTextArea();
				feedback.setEditable(false);
				left.add(feedback);
					
			JButton testB = new JButton("Start");
			testB.setAlignmentX(CENTER_ALIGNMENT);
			testB.addActionListener(this);
			left.add(testB);
		
		JPanel right = new JPanel();
		ImageIcon img = new ImageIcon("pholder.png");
		right.add(new JLabel(img));
		
		this.add(left);
		this.add(right);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.pack();
		//this.setSize(400,400);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new TestGUI();
	}
	
	
	
	public void writeLine(final String text){
		feedback.append(text+"\n");
		feedback.update(feedback.getGraphics());
	}
	
	public void clearFeedback(){
		feedback.setText("");
		feedback.update(feedback.getGraphics());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		clearFeedback();
		
		//int totalB = Integer.parseInt(beesTotal.getText());
		int inactiveB = Integer.parseInt(beesInactive.getText());
		int activeB = Integer.parseInt(beesActive.getText());
		int scoutB = Integer.parseInt(beesScout.getText());
		int visitsNo = Integer.parseInt(visits.getText());
		int cyclesNo = Integer.parseInt(cycles.getText());
		try {
			writeLine("Fetching data...");
			
			OSMDataDownloader test = new OSMDataDownloader(50.05, 19.85, 0.05);
			
			writeLine("Calculating route...");
			
			long startTime = System.currentTimeMillis();
			
			Hive hive = new Hive(inactiveB+activeB+scoutB, inactiveB, activeB, scoutB, visitsNo, cyclesNo, new Location(test, 10.0));
			hive.Solve(false);
			
			long stopTime = System.currentTimeMillis();
			long elapsedTime = (stopTime - startTime)/1000;
			
			writeLine("Best solution quality: "+hive.bestMeasureOfQuality.toString());
			
			writeLine("Calculations took "+elapsedTime+" seconds");

			Graph best = ((Cycle) hive.bestSolution).ToGraph();
			String filename = "solution.html";
			best.generateLeafletHtmlView(filename);

			writeLine("Result saved to file " + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

}
