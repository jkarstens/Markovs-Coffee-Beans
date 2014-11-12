import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MarkovsCoffeeBeans extends Applet implements ActionListener, ItemListener, Runnable{

	private Graphics dbGraphics;
	private Image dbImage;

	private Thread thread;

	private Image coffeeCan, blackHand, whiteHand;
	private CheckboxGroup cbg;
	private Checkbox[] handBoxes;
	private Label timeLabel, startWhiteLabel, startBlackLabel, graphLabel;
	private TextField timeField, startWhiteField, startBlackField;
	private Button startingNumbersButton, goButton, helpButton, answerButton;
	private Checkbox mysteryBeansBox;

	private Font buttonFont = new Font("Broadway",Font.BOLD,12);
	private Font endFont = new Font("Inkpen2 Script",Font.BOLD,30);
	private Font numberFont = new Font("Georgia",Font.PLAIN,15);

	private ArrayList<Boolean> beans;
	private Point[] bwValues;
	private int bwCounter;
	private BeanSelection[] selections; //to store for drawing
	private int selectionCounter;
	private boolean beanOneType, beanTwoType, replaceBean; //black = true, white = false
	private int randomBeanOne, randomBeanTwo;
	private int time, timeMultiplier; //time 1 = 1/50th sec
	private boolean go;
	private int whiteSweep, blackSweep; //for pie chart

	private String helpString, answerOneString, answerTwoString, winner;

	public void init(){

		setSize(600,450);
		setBackground(new Color(175,238,238));

		Toolkit tk = Toolkit.getDefaultToolkit();
		coffeeCan = tk.createImage("coffeecan.png");
		blackHand = tk.createImage("blackhand.png");
		whiteHand = tk.createImage("whitehand.png");

		cbg = new CheckboxGroup();
		handBoxes = new Checkbox[3];
		handBoxes[0] = new Checkbox("Black Hand",cbg,false);
		handBoxes[1] = new Checkbox("White Hand",cbg,false);
		handBoxes[2] = new Checkbox("No Hand",cbg,true);
		for(Checkbox cb : handBoxes) add(cb);

		mysteryBeansBox = new Checkbox("Mystery # of Beans!",false);
		mysteryBeansBox.addItemListener(this);
		add(mysteryBeansBox);

		timeLabel = new Label("Time: x");
		add(timeLabel);
		timeField = new TextField("1");
		add(timeField);

		startWhiteLabel = new Label("White Beans:");
		add(startWhiteLabel);
		startWhiteField = new TextField("50");
		add(startWhiteField);
		startBlackLabel = new Label("Black Beans:");
		add(startBlackLabel);
		startBlackField = new TextField("50");
		add(startBlackField);
		graphLabel = new Label("Black & White Beans in Coffee Can");
		add(graphLabel);

		startingNumbersButton = new Button("Randomize Starting Beans");
		startingNumbersButton.setFont(buttonFont);
		startingNumbersButton.addActionListener(this);
		add(startingNumbersButton);
		goButton = new Button("GO!");
		goButton.setFont(buttonFont);
		goButton.addActionListener(this);
		add(goButton);
		helpButton = new Button("HELP");
		helpButton.setFont(buttonFont);
		helpButton.addActionListener(this);
		add(helpButton);
		answerButton = new Button("SHOW ME WHY");
		answerButton.setFont(buttonFont);
		answerButton.addActionListener(this);
		add(answerButton);

		beans = new ArrayList<Boolean>();
		selectionCounter = 0;
		time = 0;
		timeMultiplier = 1;
		go = false;

		helpString = "You have a coffee can which contains an amount of black beans and an amount of white beans.\nSelect two beans at random. If they are the same color, remove them both and place a black bean in the can. (WW results in -2W, +1B and BB results in -2B, +1B)\n If they are different colors, remove the black bean and return the white bean (BW results in -1B).\nProve this process terminates with exactly one bean left.\nWhat can you deduce about the color of the last bean as a function of the inital number of black and white beans?\nYou can test your hypothesis by checking the 'Mystery Beans' box and randomizing the initial amounts, then presing 'GO!', and unchecking the box after the simulation has run to see if you were correct.\nYou can speed up time by typing in a multiplier in the 'Time: x' box. \nWARNING: Entering a time multiplier past 40 may result in a near light speed simulation. Consequently, the Applet will only have time to draw part (if any) of the beans, and not very well.\nThis will only affect the visual aspect; the results will not be affected.\nFor visual preferences, you may choose a black or white hand to pick the beans, or no hand at all.";
		answerOneString = "Every iteration, only one coffee bean is being removed from the can, in one of two ways.\n\n(1) Two white or two black coffee beans are selected and removed, and one black bean is added: (2-1) beans are removed.\n\nor\n\n(2) One black and one white bean are selected, and the black one is removed: 1 bean is removed.\n\nThe process goes on randomly selecting beans as long as there is more than one bean in the can, so the number of beans left in the can are, for example,\n n - (1) - (2-1) - (1) - (1) - (2-1) - ... until the equation reaches 1                n = inital number of beans.\nBecause it must be that exactly one bean is removed each iteration, the process must terminate with one bean left.\nWe can even say there will be exactly n-1 iterations because n - ( 1 x (n-1) ) leaves one bean in the can.";
		answerTwoString = "If the event of a black bean remaining is represented by the number '0' and the event of a white bean remaining is represented by the number '1', then we can predict the last bean by\n\n                                                                                                                                                   W % 2      \n\n(where W is initial amount of white beans, and % is modulus operand: gives remainder of first number divided by the second)\n If intial white beans are even, then a black bean will remain. If inital white beans are odd, then a white bean will remain.\nThis is because white beans can only be removed in pairs: never one at a time.\nIf there is an odd number of white beans to start, the number of white beans will eventually diminish to one (by WW), but that one can never be removed, as they only leave in pairs.\n(Remember event WW means remove WW and add B, event BB means remove B, and event BW means remove B.)\nEventually the black beans will slowly be removed one at a time, either by BW or BB, leaving one black and one white bean left, by which the black is of course removed and the white remains.\n\nWith an even number of initial white beans, the can will eventually diminish to either all black beans, all white beans (even), or an even number of white beans and a number of black beans.\nIf the can is all black beans, black remains as BB is replaced by B.\nIf the can is all white beans (must be an even number as they only leave in pairs), black beans take over by WW = -WW, +B, and the situation transforms into the third case, below:\nIf there is an even number of white beans and a number of black beans greater than or equal to zero,\none black bean must remain, because the situation will eventually deterioate into two (only number by which white is removed) white beans and one (only number by which black is removed) black bean.\nNow, if WW is selected, we are left with BB which turns into B. If BW is selected, we are left with WW, which turns into B.";
	}

	public void paint(Graphics g){

		for(int i=0; i<handBoxes.length; i++) handBoxes[i].setBounds(i*100,5,80,13);
		startBlackLabel.setBounds(290,2,75,20);
		startBlackField.setBounds(367,2,35,20);
		startWhiteLabel.setBounds(440,2,75,20);
		startWhiteField.setBounds(517,2,35,20);
		startingNumbersButton.setBounds(290,27,160,20);
		mysteryBeansBox.setBounds(460,30,120,13);
		graphLabel.setBounds(350,170,200,20);
		timeLabel.setBounds(0,30,45,10);
		timeField.setBounds(45,27,25,20);
		goButton.setBounds(90,27,30,20);
		helpButton.setBounds(140,27,40,20);
		answerButton.setBounds(490,420,100,20);

		Checkbox selected = cbg.getSelectedCheckbox();

		Image hand = null;

		if(selected == handBoxes[0]) hand = blackHand;
		if(selected == handBoxes[1]) hand = whiteHand;

		if((hand != null) && !go) g.drawImage(hand,50,190,this);

		if(go){

			if(selectionCounter < selections.length){

				int timeAddend = 0;

				while(true){

					if( (time+timeAddend) % 100 == 0 || timeAddend == timeMultiplier-1) break;
					else timeAddend++;
				}

				if( (time+timeAddend) % 100 == 0){

					updateGraph();

					if(time != 0) selectionCounter++;
				}

				if(time % 100 < 50){

					g.drawImage(hand,50,90+((50-time%100)*2),this);

					if(selectionCounter < selections.length-1){ //ah! fix it it seemed right before

						if(selections[selectionCounter] == BeanSelection.BW){

							g.setColor(Color.BLACK);
							g.fillOval(65,130+((50-time%100)*2),20,15);
							g.setColor(Color.WHITE);
							g.fillOval(90,130+((50-time%100)*2),20,15);
						}

						else{

							if(selections[selectionCounter] == BeanSelection.BB) g.setColor(Color.BLACK);
							else if(selections[selectionCounter] == BeanSelection.WW) g.setColor(Color.WHITE);

							g.fillOval(65,130+((50-time%100)*2),20,15);
							g.fillOval(90,130+((50-time%100)*2),20,15);
						}
					}
				}

				else{

					g.drawImage(hand,50,90+(((50+time)%100)*2),this);

					if(time > 50 && selectionCounter < selections.length){

						if(selections[selectionCounter] == BeanSelection.BW) g.setColor(Color.WHITE);
						else g.setColor(Color.BLACK);

						g.fillOval(78,130+(((50+time)%100)*2),20,15);
					}
				}
			}

			else{ //sim ended

				g.setColor(new Color(0,100,0));
				g.setFont(endFont);
				g.drawString("The last bean was " + winner,240,80);
				g.drawString("Click GO! to run again",240,130);

				startBlackField.setEditable(true);
				startWhiteField.setEditable(true);
				timeField.setEnabled(true);
				goButton.setEnabled(true);
				startingNumbersButton.setEnabled(true);
			}
		}

		g.drawImage(coffeeCan,20,200,this);

		if(bwValues != null) drawGraph(g);
	}

	public void update(Graphics g){

		if(dbImage == null){

			dbImage = createImage(this.getSize().width, this.getSize().height);
			dbGraphics = dbImage.getGraphics();
		}

		dbGraphics.setColor(getBackground());
		dbGraphics.fillRect(0,0,this.getSize().width,this.getSize().height);
		dbGraphics.setColor(getForeground());
		paint(dbGraphics);

		g.drawImage(dbImage,0,0,this);
	}

	public void actionPerformed(ActionEvent e){

		Object source = e.getSource();

		if(source == goButton){

			int b, w, t;

			try{

				b = Integer.parseInt(startBlackField.getText());
				w = Integer.parseInt(startWhiteField.getText());
				t = Integer.parseInt(timeField.getText());

				beans = new ArrayList<Boolean>();

				for(int i=0; i<b; i++) beans.add(true);
				for(int i=0; i<w; i++) beans.add(false);

				selectionCounter = 0;
				selections = new BeanSelection[beans.size()-1];

				bwValues = new Point[beans.size()];
				bwCounter = 0;
				bwValues[0] = new Point(b,w);
				bwCounter++;

				timeMultiplier = t;

				startBlackField.setEditable(false);
				startWhiteField.setEditable(false);
				timeField.setEnabled(false);
				goButton.setEnabled(false);
				startingNumbersButton.setEnabled(false);

				whiteSweep = 0;
				blackSweep = 0;

				int i = (int)(beans.size()*Math.random());
				int j = (int)(beans.size()*Math.random());
				while(j == i) j = (int)(beans.size()*Math.random());

				if( selectBeans(i,j) ) winner = "Black";
				else winner = "White";

				System.out.println(winner);

				selectionCounter = 0;
				bwCounter = 0;

				go = true; //show (simulate) how it happened after it happened for real
			}

			catch(NumberFormatException n){

				JOptionPane.showMessageDialog(null, "You must enter an integer value for the starting number of beans and time multiplier.", "Error. Simulation will not run.", JOptionPane.ERROR_MESSAGE);
			}
		}

		if(source == startingNumbersButton){

			int rb = (int)(100.0 * Math.random()) + 1; //or 1000...
			int rw = (int)(100.0 * Math.random()) + 1;

			startBlackField.setText(rb + "");
			startWhiteField.setText(rw + "");
		}

		if(source == helpButton){

			JOptionPane.showMessageDialog(null,helpString,"Help",JOptionPane.INFORMATION_MESSAGE);
		}

		if(source == answerButton){

			if( JOptionPane.showConfirmDialog(null,answerOneString,"Proof",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == 0){

				JOptionPane.showMessageDialog(null,answerTwoString,"The Bean Function",JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	public void itemStateChanged(ItemEvent e){

		Object source = e.getSource();

		if(source == mysteryBeansBox){

			if(mysteryBeansBox.getState()){

				startWhiteField.setBackground(Color.BLACK);
				startBlackField.setBackground(Color.BLACK);
			}

			else{

				startWhiteField.setBackground(Color.WHITE);
				startBlackField.setBackground(Color.WHITE);
			}
		}
	}

	public boolean selectBeans(int i, int j){

		if(beans.size() == 1) return beans.get(0);

		else{

			if(beans.get(i) == beans.get(j)){

				if(beans.get(i) == true){

					selections[selectionCounter] = BeanSelection.BB;
					bwValues[bwCounter] = new Point(bwValues[bwCounter-1].x-1, bwValues[bwCounter-1].y);
				}

				else{

					selections[selectionCounter] = BeanSelection.WW;
					bwValues[bwCounter] = new Point(bwValues[bwCounter-1].x+1, bwValues[bwCounter-1].y-2);
				}

				if(i > j){

					beans.remove(i);
					beans.remove(j);
				}

				else{

					beans.remove(j);
					beans.remove(i);
				}

				beans.add(true);
			}

			else{

				if(beans.get(i)) beans.remove(i);
				else if(beans.get(j)) beans.remove(j);

				selections[selectionCounter] = BeanSelection.BW;
				bwValues[bwCounter] = new Point(bwValues[bwCounter-1].x-1, bwValues[bwCounter-1].y);
			}

			int k = -1;
			int l = -1;

			if(beans.size() > 1){

				k = (int)(beans.size()*Math.random());
				l = (int)(beans.size()*Math.random());
				while(l == k) l = (int)(beans.size()*Math.random());

				selectionCounter++;
			}

			bwCounter++;

			return selectBeans(k,l);
		}
	}

	public void drawGraph(Graphics g){ //also draw scatter plot of winners black and white points total beans(x axis) vs original pct (y axis)

		g.setColor(Color.WHITE);
		g.fillArc(370,200,150,150,90,whiteSweep); //x,y,w,h,deg,deg
		g.setColor(Color.BLACK);
		g.fillArc(370,200,150,150,90,-blackSweep);
		g.setFont(numberFont);

		if(!mysteryBeansBox.getState() && bwCounter != bwValues.length-1){

			if(whiteSweep < 120) g.drawString(bwValues[bwCounter].y + "",400,275 - ( (120-whiteSweep)/2 +10));
			else g.drawString(bwValues[bwCounter].y + "",400,275);

			g.setColor(Color.WHITE);
			if(blackSweep < 120) g.drawString(bwValues[bwCounter].x + "",475,275 - ( (120-blackSweep)/2 +10));
			else g.drawString(bwValues[bwCounter].x + "",475,275);
		}

		int timeAddend = 0;

		while(true){

			if( (time+timeAddend) % 99 == 0 || timeAddend == timeMultiplier-1 ) break;
			else timeAddend++;
		}

		if((time+timeAddend) % 99 == 0 && time !=  0){

			if(bwCounter < bwValues.length-1) bwCounter++;
		}
	}

	public void updateGraph(){

		whiteSweep = (int) (( (bwValues[bwCounter].getY())/(bwValues[bwCounter].getX() + bwValues[bwCounter].getY()) ) * 360.0);
		blackSweep = 360 - whiteSweep;
	}

	public enum BeanSelection{

		BW, BB, WW
	}

	public void start(){

		if(thread == null){

			thread = new Thread(this);
			thread.start();
		}
	}

	public void run(){

		while(thread != null){

			repaint();

			try{

				Thread.sleep(20);
				if(go) time += timeMultiplier;
			}

			catch(InterruptedException e){
			}
		}
	}

	public void stop(){

		thread = null;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Markov's Coffee Beans");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().setBackground(Color.WHITE);
		Applet thisApplet = new MarkovsCoffeeBeans();
		frame.getContentPane().add(thisApplet, BorderLayout.CENTER);
		thisApplet.init();
		frame.setSize(thisApplet.getSize());
		thisApplet.start();
		frame.setVisible(true);
	}
}
