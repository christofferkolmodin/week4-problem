package bouncing_balls;

/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */


class Model {
	double gravity = -9.82;
	double areaWidth, areaHeight;

	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0,0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0,0.3);
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		for (Ball ball : balls) {

			if (isCollisionWithWall(ball)) {
				changeHorizontalDirection(ball);
			}

			if (isCollisionWithFloor(ball)) {
				preventOutOfBoundsFloor(ball);
				changeVerticalDirection(ball);
				System.out.println("Velocity after collision with floor: " + ball.vy);

			} else if (isCollisionWithRoof(ball)) {
				preventOutOfBoundsRoof(ball);
				changeVerticalDirection(ball);
				System.out.println("Velocity after collision with roof: " + ball.vy);
			}

			applyGravity(ball, deltaT);
			System.out.println("After calculation velocity is: " + ball.vy);
			// compute new position according to the speed of the ball
			updatePosition(ball, deltaT);

		}
	}

	private void applyGravity(Ball ball, double deltaT) {
		ball.vy += deltaT * gravity;
	}

	private void updatePosition(Ball ball, double deltaT) {
		ball.x += deltaT * ball.vx;
		ball.y += deltaT * ball.vy;
	}

	private boolean isCollisionWithRoof(Ball b) {
		return b.y > areaHeight - b.radius;
	}

	private void preventOutOfBoundsRoof(Ball b) {
		b.y = areaHeight - b.radius;
	}

	private void preventOutOfBoundsFloor(Ball b) {
		b.y = 0 + b.radius;
	}

	private boolean isCollisionWithFloor(Ball b) {
		return b.y < b.radius;
	}

	private void changeVerticalDirection(Ball b) {
		b.vy *= -1;
	}

	private void changeHorizontalDirection(Ball b) {
		b.vx *= -1;
	}

	private boolean isCollisionWithWall(Ball b) {
		return b.x < b.radius || b.x > areaWidth - b.radius;
	}
	
	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, ay, radius;

		Ball(double x, double y, double vx, double vy, double ay, double r) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.ay = ay;
			this.radius = r;
		}
	}
}
