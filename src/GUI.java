import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.io.*;
import javax.swing.*;

public class GUI {
	
	public static ImageIcon fire = new ImageIcon("res/fire.png");
	public static ImageIcon mechanic = new ImageIcon("res/worker.png");
	public static ImageIcon toolbox = new ImageIcon("res/toolbox.png");
	public static ImageIcon mbot = new ImageIcon("res/mbot.png");
	public static Random rand = new Random();
	
	class Cell {
		boolean fire, toolbox;
		int weight, walls;
		Rectangle draw;
		
		public Cell(boolean f, boolean t, int w, Rectangle d, int walls) {
			fire = f;
			toolbox = t;
			draw = d;
			this.walls = walls;
			weight = w;
		}
		
		public Color getColor() {
			if(weight == 1) {
				return Color.white;
			}else if(weight == 2) {
				return Color.yellow;
			}else {
				return Color.red;
			}
		}
	}
	//should bring tools in the order in which they are ordered
	//1 is toolbox, 2 is mechanic, 3 is fire
	int current = 0;
	int fires = 0;
	int counter = 0;
	int rows = 10, cols = 10;
	Cell[][] cells = new Cell[rows][cols];
	public int mechanicX=rand.nextInt(rows), mechanicY=rand.nextInt(cols);
	ArrayList<Pair> currPath = new ArrayList<Pair>();
	//1 is going to toolbox, 2 is going to mechanic, 3 is going to fire
	int pathType = 0;
	int botX = 0, botY = 0;
	boolean carrying = false;
	Timer timer = new Timer();
	public GUI() {
		Scanner sc;
		try {
			sc = new Scanner(new File("res/factory.in"));
		}catch (Exception e) {
			sc = new Scanner(System.in);
		}
		JFrame frame = new JFrame();
		frame.setBounds(10, 10, 800, 700);
		rows = sc.nextInt();
		cols = sc.nextInt();
		JPanel panel = new JPanel();
		
		
		int pHeight = 60;
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				cells[i][j] = new Cell(false, false, 1, new Rectangle(j*frame.getWidth()/cols, i*(frame.getHeight()-pHeight)/rows, frame.getWidth()/cols, (frame.getHeight()-pHeight)/rows), sc.nextInt());
			}
		}
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				cells[i][j].weight = sc.nextInt();
			}
		}
		JPanel simul = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D)g.create();
				for(int i = 0; i < rows; i++) {
					for(int j = 0; j < cols; j++) {
						g2.setColor(cells[i][j].getColor());
						g2.fill(cells[i][j].draw);
						g2.setColor(Color.black);
						g2.setStroke(new BasicStroke(3));
						if(cells[i][j].walls%2 == 1)
							g2.drawLine((int)cells[i][j].draw.getX(), (int)cells[i][j].draw.getY(), (int)cells[i][j].draw.getX()+(int)cells[i][j].draw.getWidth(), (int)cells[i][j].draw.getY());
						if((cells[i][j].walls/2)%2 == 1)
							g2.drawLine((int)cells[i][j].draw.getX()+(int)cells[i][j].draw.getWidth(), (int)cells[i][j].draw.getY()+(int)cells[i][j].draw.getHeight(), (int)cells[i][j].draw.getX()+(int)cells[i][j].draw.getWidth(), (int)cells[i][j].draw.getY());
						if((cells[i][j].walls/4)%2 == 1)
							g2.drawLine((int)cells[i][j].draw.getX(), (int)cells[i][j].draw.getY()+(int)cells[i][j].draw.getHeight(), (int)cells[i][j].draw.getX()+(int)cells[i][j].draw.getWidth(), (int)cells[i][j].draw.getY()+(int)cells[i][j].draw.getHeight());
						if((cells[i][j].walls/8)%2 == 1)
							g2.drawLine((int)cells[i][j].draw.getX(), (int)cells[i][j].draw.getY(), (int)cells[i][j].draw.getX(), (int)cells[i][j].draw.getY()+(int)cells[i][j].draw.getHeight());
						//probably going to experiment with this
						int x = (int)cells[i][j].draw.getX()+(int)cells[i][j].draw.getWidth()/4;
						int y = (int)cells[i][j].draw.getY()+(int)cells[i][j].draw.getHeight()/4;
						if(cells[i][j].toolbox) {
							toolbox.paintIcon(this, g, x, y);
						}
						if(cells[i][j].fire) {
							fire.paintIcon(this, g, x, y);
						}
						if(i == mechanicX && j == mechanicY) {
							mechanic.paintIcon(this, g, x, y);
						}
						if(i == botX && j == botY) {
							mbot.paintIcon(this, g, x, y);
							if(carrying) {
								toolbox.paintIcon(this, g, x, y);
							}
						}
					}
				}
			}
		};
		Cursor fireC = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage("res/fire.png") , new Point(frame.getX(), frame.getY()), "fire");
		Cursor toolboxC = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage("res/toolbox.png") , new Point(frame.getX(), frame.getY()), "toolbox");
		Cursor mechanicC = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage("res/worker.png") , new Point(frame.getX(), frame.getY()), "mechanic");
		JButton button1 = new JButton("Box");
		JButton button2 = new JButton("Worker");
		JButton button3 = new JButton("Fire");
		JLabel currSetting = new JLabel("Current Setting: None");
		currSetting.setHorizontalAlignment(SwingConstants.CENTER);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		panel.setLayout(new GridLayout(1, 4));
		panel.add(button1);
		panel.add(button2);
		panel.add(button3);
		panel.add(currSetting);
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current = 1;
				currSetting.setText("Current Setting: Box");
				simul.setCursor(toolboxC);
			}
		});
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current = 2;
				currSetting.setText("Current Setting: Worker");
				simul.setCursor(mechanicC);
			}
		});
		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current = 3;
				currSetting.setText("Current Setting: Fire");
				simul.setCursor(fireC);
			}
		});
		frame.add(panel, BorderLayout.NORTH);
		frame.add(simul);
		simul.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			//should rerun pathfinding every time a new thing is put down
			@Override
			public void mousePressed(MouseEvent e) {
				int j = e.getX()*cols/frame.getWidth(), i = e.getY()*rows/(frame.getHeight()-pHeight);
				if(mechanicX == i && mechanicY == j) {
					return;
				}
				if(current == 1) {
					cells[i][j].toolbox = true;
				}
				if(current == 3) {
					if(!cells[i][j].fire)
						fires++;
					cells[i][j].fire = true;
				}
				if(current == 2) {
					mechanicX = i;
					mechanicY = j;
				}
				Pathfinder p = new Pathfinder();
				int[][] walls = new int[cells.length][cells[0].length];
				int[][] weights = new int[cells.length][cells[0].length];
				for(int r = 0; r < cells.length; r++) {
					for(int c = 0; c < cells[0].length; c++) {
						walls[r][c] = cells[r][c].walls;
						weights[r][c] = cells[r][c].weight;
					}
				}
				if(fires>0) {
					for(int r = 0; r < cells.length; r++) {
						for(int c = 0; c < cells[0].length; c++) {
							if(cells[r][c].fire) {
								ArrayList<Pair> path = p.pathfinder(walls, weights, botX, botY, r, c);
								if(currPath.size() > path.size() || pathType != 3) {
									currPath = path;
									pathType = 3;
								}
							}
						}
					}
				}else if(carrying){
					currPath = p.pathfinder(walls, weights, botX, botY, mechanicX, mechanicY);
					pathType = 2;
				}else {
					for(int r = 0; r < cells.length; r++) {
						for(int c = 0; c < cells[0].length; c++) {
							if(cells[r][c].toolbox) {
								ArrayList<Pair> path = p.pathfinder(walls, weights, botX, botY, r, c);
								if(currPath.size() > path.size() || pathType == 0) {
									currPath = path;
									pathType = 1;
								}
							}
						}
					}
				}
				/*
				for(Pair pair : currPath) {
					System.out.println(pair.a + " " + pair.b);
				}*/
				simul.paint(simul.getGraphics());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("BMS Robot Simulation");
		frame.setVisible(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			//move robot along path
			@Override
			public void run() {
				simul.paint(simul.getGraphics());
				if(currPath.size() > 0) {
					if(cells[botX][botY].weight*cells[botX][botY].weight*3/4 > counter) {
						counter++;
						return;
					}else {
						counter = 0;
					}
					botX = currPath.get(0).a;
					botY = currPath.get(0).b;
					currPath.remove(0);
				}else {
					if(pathType == 3) {
						cells[botX][botY].fire = false;
						fires--;
					}else if(pathType == 2) {
						carrying = false;
					}else if(pathType == 1) {
						carrying = true;
						cells[botX][botY].toolbox = false;
					}
					pathType = 0;
					Pathfinder p = new Pathfinder();
					int[][] walls = new int[cells.length][cells[0].length];
					int[][] weights = new int[cells.length][cells[0].length];
					for(int r = 0; r < cells.length; r++) {
						for(int c = 0; c < cells[0].length; c++) {
							walls[r][c] = cells[r][c].walls;
							weights[r][c] = cells[r][c].weight;
						}
					}
					if(fires>0) {
						for(int r = 0; r < cells.length; r++) {
							for(int c = 0; c < cells[0].length; c++) {
								if(cells[r][c].fire) {
									ArrayList<Pair> path = p.pathfinder(walls, weights, botX, botY, r, c);
									if(currPath.size() > path.size() || pathType != 3) {
										currPath = path;
										pathType = 3;
									}
								}
							}
						}
					}else if(carrying){
						currPath = p.pathfinder(walls, weights, botX, botY, mechanicX, mechanicY);
						pathType = 2;
					}else {
						for(int r = 0; r < cells.length; r++) {
							for(int c = 0; c < cells[0].length; c++) {
								if(cells[r][c].toolbox) {
									ArrayList<Pair> path = p.pathfinder(walls, weights, botX, botY, r, c);
									if(currPath.size() > path.size() || pathType == 0) {
										currPath = path;
										pathType = 1;
									}
								}
							}
						}
					}
				}
			}
			
		}, 0, 300);
	}
}
