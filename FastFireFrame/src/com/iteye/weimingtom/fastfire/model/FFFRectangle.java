package com.iteye.weimingtom.fastfire.model;

public class FFFRectangle {
	public int x;
	public int y;
	public int width;
	public int height;
	
    public FFFRectangle(int x, int y, int width, int height) {
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    }
    
    public FFFRectangle cloneRect() {
    	return new FFFRectangle(this.x, this.y, this.width, this.height);
    }
    
    public boolean isEmpty() {
    	return (width <= 0) || (height <= 0);
    }
    
    public void setRect(int x, int y, int width, int height) {
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    }
    
    public FFFRectangle intersection(FFFRectangle r) {
		int tx1 = this.x;
		int ty1 = this.y;
		int rx1 = r.x;
		int ry1 = r.y;
		long tx2 = tx1; 
		tx2 += this.width;
		long ty2 = ty1; 
		ty2 += this.height;
		long rx2 = rx1; 
		rx2 += r.width;
		long ry2 = ry1; 
		ry2 += r.height;
		if (tx1 < rx1) {
			tx1 = rx1;
		}
		if (ty1 < ry1) { 
			ty1 = ry1;
		}
		if (tx2 > rx2) { 
			tx2 = rx2;
		}
		if (ty2 > ry2) { 
			ty2 = ry2;
		}
		tx2 -= tx1;
		ty2 -= ty1;
		if (tx2 < Integer.MIN_VALUE) {
			tx2 = Integer.MIN_VALUE;
		}
		if (ty2 < Integer.MIN_VALUE) { 
			ty2 = Integer.MIN_VALUE;
		}
		return new FFFRectangle(tx1, ty1, (int) tx2, (int) ty2);
    }
    
    public FFFRectangle union(FFFRectangle r) {
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0) {
            return r.cloneRect();
        }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0) {
            return this.cloneRect();
        }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1) {
        	tx1 = rx1;
        }
        if (ty1 > ry1) {
        	ty1 = ry1;
        }
        if (tx2 < rx2) {
        	tx2 = rx2;
        }
        if (ty2 < ry2) {
        	ty2 = ry2;
        }
        tx2 -= tx1;
        ty2 -= ty1;
        if (tx2 > Integer.MAX_VALUE) {
        	tx2 = Integer.MAX_VALUE;
        }
        if (ty2 > Integer.MAX_VALUE) {
        	ty2 = Integer.MAX_VALUE;
        }
        return new FFFRectangle(tx1, ty1, (int) tx2, (int) ty2);
    }
}
