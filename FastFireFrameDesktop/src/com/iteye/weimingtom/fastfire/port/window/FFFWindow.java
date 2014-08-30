package com.iteye.weimingtom.fastfire.port.window;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.iteye.weimingtom.fastfire.model.FFFKey;
import com.iteye.weimingtom.fastfire.model.FFFPoint;
import com.iteye.weimingtom.fastfire.model.FFFRectangle;
import com.iteye.weimingtom.fastfire.port.file.FFFLog;
import com.iteye.weimingtom.fastfire.port.image.FFFImage;

public class FFFWindow extends Panel implements Runnable, KeyListener,
	MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = 480;
	private static final String WINDOW_TITLE = "FFFWindow";
	
	private Frame frame;
	private Graphics bufGraph;
	private Image bufImage;
	private Thread drawThread;
	private long prevTime;
	
	private String title;
	private int canvasWidth, canvasHeight, tickPerSecond;
	private boolean isStopped = false;
	private long tick = 0;
	private boolean enableTick = true;
	
	private FFFWindowAdapter adapter;
	
	public FFFWindow() {
		this.title = WINDOW_TITLE;
		this.canvasWidth = WINDOW_WIDTH;
		this.canvasHeight = WINDOW_HEIGHT;
		this.tickPerSecond = 24;
		setPreferredSize(new Dimension(this.canvasWidth, this.canvasHeight));
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		frame = new Frame();
		frame.add(this);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isStopped = true;
				if (drawThread != null) {
					try {
						drawThread.join(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				onExit();
				System.exit(0);	
			}
		});
		frame.setTitle(title);
	}

	public void setAdapter(FFFWindowAdapter adapter) {
		this.adapter = adapter;
		if (this.adapter != null) {
			this.adapter.setWindow(this);
		}
	}
	
	public void start() {
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		this.requestFocus(); //listen for keyboard event
		bufImage = this.createImage(canvasWidth, canvasHeight);
		bufGraph = bufImage.getGraphics();
		bufGraph.clearRect(0, 0, canvasWidth, canvasHeight);
		drawThread = new Thread(this);
		drawThread.start();	
	}
	
	@Override
	public void run() {
		onInit();
		while (true) {
			if (isStopped) {
				break;
			}
			long curTime = System.currentTimeMillis();
			if (curTime - (1000 / tickPerSecond) > this.prevTime) {
				this.prevTime = curTime;
				if (enableTick) {
					if (this.adapter != null) {
						this.adapter.onIdle(0); //FIXME:
					}
				}
				tick++;
			}
			//onDraw(bufGraph);
			//onPaint(); //FIXME: unnecessary!!!
			repaint();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.exit(0);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		//g.drawImage(bufImage, 0, 0, this);
		double x = 0;
		double y = 0;
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();

		int ow;
		int oh;
		int ox;
		int oy;
		if (w > 0 && h > 0 && bufImage != null) {
			if ((double)WINDOW_WIDTH / w > (double)WINDOW_HEIGHT / h) {
				oh = ((int)((double)WINDOW_HEIGHT / WINDOW_WIDTH * w));
                ow = (int)(w);
            } else {
            	ow = ((int)((double)WINDOW_WIDTH / WINDOW_HEIGHT * h));
                oh = (int)(h);
            }
			ox = (int)(x + (double)w / 2 - (double)ow / 2);
			oy = (int)(y + (double)h / 2 - (double)oh / 2);
			g.drawImage(bufImage,
				ox, oy, ox + ow, oy + oh,
				0, 0, bufImage.getWidth(this), bufImage.getHeight(this),  
				this);
		}
	}
	
	public void setEnableTick(boolean enableTick) {
		this.enableTick = enableTick;
	}
	
	public void setFrameTitle(String title) {
		if (this.frame != null) {
			this.frame.setTitle(title);
		}
	}
	
	public Graphics getBufGraph() {
		return this.bufGraph;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			if (adapter != null) {
				adapter.onKeyDown(FFFKey.ENTER);
			}
			break;
			
		case KeyEvent.VK_SPACE:
			if (adapter != null) {
				adapter.onKeyDown(FFFKey.SPACE);
			}
			break;
			
		case KeyEvent.VK_ESCAPE:
			if (adapter != null) {
				adapter.onKeyDown(FFFKey.ESCAPE);
			}
			break;
			
		case KeyEvent.VK_UP:
			if (adapter != null) {
				adapter.onKeyDown(FFFKey.UP);
			}
			break;
			
		case KeyEvent.VK_DOWN:
			if (adapter != null) {
				adapter.onKeyDown(FFFKey.DOWN);
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (adapter != null) {
			adapter.onLButtonDown(calcPoint(e.getX(), e.getY()));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (adapter != null) {
			adapter.onLButtonUp(calcPoint(e.getX(), e.getY()));
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (adapter != null) {
			adapter.onMouseMove(calcPoint(e.getX(), e.getY()));
		}
	}
	
	protected void onInit() {
		if (adapter != null) {
			adapter.onCreate();
		}
	}
	
	protected void onExit() {
		if (adapter != null) {
			adapter.onDestroy();
		}
	}
	
	public void messageBox(String str) {
		FFFLog.trace("NOTE:[messageBox] -> " + str);
	}
	
	public void draw(FFFImage image, FFFRectangle rect) {
		int x = rect.x;
		int y = rect.y;
		int ox = x;
		int oy = y;
		int w = rect.width;
		int h = rect.height;
		this.getBufGraph().drawImage(image.mImage, 
			ox, oy, ox + w, oy + h, 
			 x,  y,  x + w,  y + h, 
			null);
	}
	
	private FFFPoint calcPoint(int mx, int my) {
		FFFPoint result = null;
		double x = 0;
		double y = 0;
		double w = (double)this.getWidth();
		double h = (double)this.getHeight();

		int ow;
		int oh;
		int ox;
		int oy;
		if (w > 0 && h > 0) {
			if ((double)WINDOW_WIDTH / w > (double)WINDOW_HEIGHT / h) {
				oh = ((int)((double)WINDOW_HEIGHT / WINDOW_WIDTH * w));
                ow = (int)(w);
            } else {
            	ow = ((int)((double)WINDOW_WIDTH / WINDOW_HEIGHT * h));
                oh = (int)(h);
            }
			ox = (int)(x + (double)w / 2 - (double)ow / 2);
			oy = (int)(y + (double)h / 2 - (double)oh / 2);
			
			double scale = (double)WINDOW_WIDTH / (double)ow;
			int rx = (int)((mx - ox) * scale);
			int ry = (int)((my - oy) * scale);
			result = new FFFPoint(rx, ry);
		}
		return result;
	}
}
