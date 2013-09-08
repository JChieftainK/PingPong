public interface PaddleInterface {
	//Find the name of AI
	public String getPaddleName();
	
	//get the direction to move the paddle and color
	public int getDirection();
	public Color getPaddleColor();
	
	//get all ball information
	public void ballDirection(double pi);
	public void ballVelocity(double dx);
	public void ballLocation(int x, int y);
	
	//set side sends -1, 0, 1 and paddle color
	public void setSide(int x);
	public void setPaddleColor(Color paddleColor);
	
	//reset the paddle to given y
	public void reset(int y);
}