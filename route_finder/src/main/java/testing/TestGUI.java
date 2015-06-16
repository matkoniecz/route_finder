package testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bee.Hive;
import bee.trip.Location;
import osm_processing.OSMDataDownloader;

public class TestGUI extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243286810278326679L;
	private JTextField beesInactive;
	private JTextField beesActive;
	private JTextField beesScout;
	private JTextField visits;
	private JTextField cycles;
	
	private static final String[] testCases = {"CyclesTest","VisitsTest","BeesTotalTest"};
	private JComboBox<String> testCasesBox;
	
	private JScrollPane feedbackPane;
	private JTextArea feedback;

	public TestGUI(){
		this.setLayout(new GridLayout(1,2));
		
			JPanel left = new JPanel();
			left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));
			
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
				
				JPanel testP = new JPanel(new BorderLayout());
					testCasesBox = new JComboBox<String>(testCases);
					testP.add(testCasesBox,BorderLayout.CENTER);
					testP.setMaximumSize(new Dimension(testCasesBox.getMaximumSize().width,testCasesBox.getPreferredSize().height));
				left.add(testP);
				
				feedback = new JTextArea();
				feedback.setEditable(false);
				feedbackPane = new JScrollPane();
				feedbackPane.setViewportView(feedback);
				left.add(feedbackPane);
					
				JPanel buttonP = new JPanel();
				buttonP.setLayout(new BoxLayout(buttonP,BoxLayout.X_AXIS));
				
			JButton testB = new JButton("Start");
			testB.setAlignmentX(CENTER_ALIGNMENT);
			testB.addActionListener(this);
			buttonP.add(testB);
			
			JButton startTest = new JButton("Run test case");
			startTest.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
						Thread t;
						switch((String) testCasesBox.getSelectedItem()){
						case "CyclesTest":
							t = new Thread(new Runnable(){

								@Override
								public void run() {
									try {
										testCycles();
									} catch (IOException e) {
										
										e.printStackTrace();
									}
								}
								
							});
							t.start();
							break;
						case "VisitsTest":
							t = new Thread(new Runnable(){

								@Override
								public void run() {
									try {
										testVisits();
									} catch (IOException e) {
										
										e.printStackTrace();
									}
								}
								
							});
							t.start();
							break;
						case "BeesTotalTest":
							t = new Thread(new Runnable(){

								@Override
								public void run() {
									try {
										testBeesTotal();
									} catch (IOException e) {
										
										e.printStackTrace();
									}
								}
								
							});
							t.start();
							break;
						default:
							writeLine("sorry,nothing");
							break;
						}
					
				}

				
				
			});
			buttonP.add(startTest);
			left.add(buttonP);
		
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

	protected void testBeesTotal() throws IOException {
		int inactiveB = Integer.parseInt(beesInactive.getText());
		int activeB = Integer.parseInt(beesActive.getText());
		int scoutB = Integer.parseInt(beesScout.getText());
		int visitsNo = Integer.parseInt(visits.getText());
		int cyclesNo = Integer.parseInt(cycles.getText());
		
		File fout = File.createTempFile("BeesTotalTest", ".csv",new File(Paths.get("").toAbsolutePath().toString()));
		PrintWriter writer = new PrintWriter(fout);
		
		writer.println("Inactive,Active,Scout,Visits,Cycles,Quality,Cycle_found,Time(s)");
		clearFeedback();
		writeLine("Fetching data...");
		
		OSMDataDownloader test = new OSMDataDownloader(50.05, 19.85, 0.05);
		
		long startTime,stopTime,elapsedTime;
		Hive hive;
		for(int i=1;i<=50;i++){
			writeLine("Calculating route with bees population multiplied by "+i);
			startTime = System.currentTimeMillis();
			hive = new Hive(inactiveB*i+activeB*i+scoutB*i, inactiveB, activeB, scoutB, visitsNo, cyclesNo, new Location(test, 10.0));
			hive.Solve(false);
			stopTime = System.currentTimeMillis();
			elapsedTime = (stopTime - startTime)/1000;
			
			writer.println(inactiveB*i+","+activeB*i+","+scoutB*i+","+visitsNo+","+cyclesNo+","+hive.bestMeasureOfQuality+","+hive.bestSolutionCycle+","+elapsedTime);
			
		}
		writeLine("Test is done.");
		
		writer.close();
	}

	public static void main(String[] args) {
		new TestGUI();
	}
	
	
	
	public void writeLine(final String text){
		feedback.append(text+"\n");
		feedback.update(feedback.getGraphics());
		feedback.revalidate();
		feedback.repaint();
		feedbackPane.revalidate();
		feedbackPane.repaint();
	}
	
	public void clearFeedback(){
		feedback.setText("");
		feedback.update(feedback.getGraphics());
		feedbackPane.revalidate();
		feedbackPane.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		clearFeedback();
		
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
			writeLine("Best solution found in "+hive.bestSolutionCycle+" cycle");
			
			writeLine("Calculations took "+elapsedTime+" seconds");
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	private void testVisits() throws IOException {
		int inactiveB = Integer.parseInt(beesInactive.getText());
		int activeB = Integer.parseInt(beesActive.getText());
		int scoutB = Integer.parseInt(beesScout.getText());
		int visitsNo;
		int cyclesNo = Integer.parseInt(cycles.getText());
		
		File fout = File.createTempFile("VisitsTest", ".csv",new File(Paths.get("").toAbsolutePath().toString()));
		PrintWriter writer = new PrintWriter(fout);
		
		writer.println("Inactive,Active,Scout,Visits,Cycles,Quality,Cycle_found,Time(s)");
		clearFeedback();
		writeLine("Fetching data...");
		
		OSMDataDownloader test = new OSMDataDownloader(50.05, 19.85, 0.05);
		
		long startTime,stopTime,elapsedTime;
		Hive hive;
		for(visitsNo=1;visitsNo<=1000;visitsNo++){
			writeLine("Calculating route with "+visitsNo+" max visits");
			startTime = System.currentTimeMillis();
			hive = new Hive(inactiveB+activeB+scoutB, inactiveB, activeB, scoutB, visitsNo, cyclesNo, new Location(test, 10.0));
			hive.Solve(false);
			stopTime = System.currentTimeMillis();
			elapsedTime = (stopTime - startTime)/1000;
			
			writer.println(inactiveB+","+activeB+","+scoutB+","+visitsNo+","+cyclesNo+","+hive.bestMeasureOfQuality+","+hive.bestSolutionCycle+","+elapsedTime);
			
		}
		writeLine("Test is done.");
		
		writer.close();
	}

	private void testCycles() throws IOException {
		int inactiveB = Integer.parseInt(beesInactive.getText());
		int activeB = Integer.parseInt(beesActive.getText());
		int scoutB = Integer.parseInt(beesScout.getText());
		int visitsNo = Integer.parseInt(visits.getText());
		int cyclesNo;
		
		File fout = File.createTempFile("CyclesTest", ".csv",new File(Paths.get("").toAbsolutePath().toString()));
		PrintWriter writer = new PrintWriter(fout);
		
		writer.println("Inactive,Active,Scout,Visits,Cycles,Quality,Cycle_found,Time(s)");
		clearFeedback();
		writeLine("Fetching data...");
		
		OSMDataDownloader test = new OSMDataDownloader(50.05, 19.85, 0.05);
		
		long startTime,stopTime,elapsedTime;
		Hive hive;
		for(cyclesNo=10;cyclesNo<=500;cyclesNo+=10){
			writeLine("Calculating route with "+cyclesNo+" cycles");
			startTime = System.currentTimeMillis();
			hive = new Hive(inactiveB+activeB+scoutB, inactiveB, activeB, scoutB, visitsNo, cyclesNo, new Location(test, 10.0));
			hive.Solve(false);
			stopTime = System.currentTimeMillis();
			elapsedTime = (stopTime - startTime)/1000;
			
			writer.println(inactiveB+","+activeB+","+scoutB+","+visitsNo+","+cyclesNo+","+hive.bestMeasureOfQuality+","+hive.bestSolutionCycle+","+elapsedTime);
			
		}
		writeLine("Test is done.");
		
		writer.close();
	}
}
