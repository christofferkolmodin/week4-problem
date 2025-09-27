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
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 20);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6,0.3, 15);
	}

	void step(double deltaT) {

		for (Ball ball : balls) {

			if (isCollisionWithWall(ball)) {
				changeHorizontalDirection(ball);
			}

			if (isCollisionWithFloor(ball)) {
				handleFloorCollision(ball);
				System.out.println("Velocity after collision with floor: " + ball.vy);

			} else if (isCollisionWithRoof(ball)) {
				handleRoofCollision(ball);
				System.out.println("Velocity after collision with roof: " + ball.vy);
			}

			applyGravity(ball, deltaT);
			System.out.println("After calculation velocity is: " + ball.vy);
			// compute new position according to the speed of the ball
			updatePosition(ball, deltaT);
		}

		handleBallOnBallCollision(balls);

	}

	private void handleBallOnBallCollision(Ball[] balls) {
		for (int i = 0; i < balls.length; i++) {
			for (int j = 0; j < balls.length; j++) {

				Ball ball1 = balls[i];
				Ball ball2 = balls[j];

				if (j > i) {  // Don't repeat ball comparisons
					if (isBallCollision(ball1, ball2)) {
						bounceTimeBaby(ball1, ball2);
					}
				}

			}
		}
	}

	private boolean isBallCollision(Ball ball1, Ball ball2) {
		double distance = distanceBetweenBalls(ball1, ball2);

		return distance < (ball1.radius + ball2.radius);
	}

	private void bounceTimeBaby(Ball ball1, Ball ball2) {
		conservationOfEnergy(ball1, ball2);
	}

	private double distanceBetweenBalls(Ball ball1, Ball ball2) {
		// Computes the distance between two points (x1, y1) and (x2, y2)
		// using the Euclidian distance formula:  distance = sqrt(  (x2 - x1)^2 + (y2-y1)^2  )
		return Math.sqrt( square(ball2.x - ball1.x) + square(ball2.y - ball1.y)  );
	}

	private void conservationOfEnergy(Ball ball1, Ball ball2) {
		// TODO: Fix this implementation

		//	m1v1 + m2v2 = I
		double momentumX = ball1.weight * ball1.vx + ball2.weight * ball2.vx;
		double momentumY = ball1.weight * ball1.vy + ball2.weight * ball2.vy;

		//	v2 − v1 = −R
		double relativeVelocityX = ball2.vx - ball1.vx;
		double relativeVelocityY = ball2.vy - ball1.vy;
	}

	private double square(double value) {
		return value * value;
	}


	private void applyGravity(Ball ball, double deltaT) {
		ball.vy += deltaT * gravity;
	}

	private void updatePosition(Ball ball, double deltaT) {
		ball.x += deltaT * ball.vx;
		ball.y += deltaT * ball.vy;
	}

	private void handleFloorCollision(Ball b) {
		preventOutOfBoundsFloor(b);
		changeVerticalDirection(b);
	}

	private void handleRoofCollision(Ball b) {
		preventOutOfBoundsRoof(b);
		changeVerticalDirection(b);
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
		double x, y, vx, vy, radius, weight;

		Ball(double x, double y, double vx, double vy, double r, double w) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.weight = w;
		}
	}
}
