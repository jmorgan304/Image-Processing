public class Pixel {

	private float red;
	private float green;
	private float blue;
	private int colorInt;
	public static final int RED = -65536;
	public static final int GREEN = -16711936;
	public static final int BLUE = -16776961;
	
	Pixel(int pixel){
		int r =(pixel >> 16) & 0xFF;
		int g = (pixel >> 8) & 0xFF;
		int b = (pixel) & 0xFF; 
		this.colorInt = pixel;
		this.setRed((float) r/255);
		this.setGreen((float) g/255);
		this.setBlue((float) b/255);
	}
	
	
	//generates an integer value based on floating point (RGB)
	//for instance black would be getIntColor(0.0f, 0.0f, 0.0f)
	public static int getIntColor(float re, float gr, float bl){
		int r = (int)re*255;
		int g = (int)gr*255;
		int b = (int)bl*255;
		int result = 0;
		result = result | r << 16;
		result = result | g << 8;
		result = result | b;
		result = result | 255 << 24;
				
		return result;
	}
	
	//returns integer value of the pixel color
	int getIntValue(){
		return colorInt;
	}


	public float getGreen() {
		return green;
	}
	
	private float clamp(float val){
		if (val >1.0f)
			return 1.0f;
		else if(val <0.0f)
			return 0.0f;
		
		return val;
	}

	public void setGreen(float green) {
		
		this.green = clamp(green);
	}


	public float getRed() {
		return red;
	}


	public void setRed(float red) {
		this.red = clamp(red);
	}


	public float getBlue() {
		return blue;
	}


	private void setBlue(float blue) {
		this.blue = clamp(blue);
	}
	
}
