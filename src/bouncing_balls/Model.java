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
		balls = new Ball[3];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 20);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6,0.3, 15);
		balls[2] = new Ball(2.5 * width / 3, height * 0.5, -1.0, 1.0,0.25, 17);
	}

	void step(double deltaT) {

		for (Ball ball : balls) {

			if (isCollisionWithWall(ball)) {
				changeHorizontalDirection(ball);
				System.out.println("Horizontal velocity after wall collision: " + ball.vx);
			}

			if (isCollisionWithFloor(ball)) {
				handleFloorCollision(ball);
//				System.out.println("Velocity after collision with floor: " + ball.vy);

			} else if (isCollisionWithRoof(ball)) {
				handleRoofCollision(ball);
//				System.out.println("Velocity after collision with roof: " + ball.vy);
			}

			applyGravity(ball, deltaT);
//			System.out.println("After calculation velocity is: " + ball.vy);
			// compute new position according to the speed of the ball
			updatePosition(ball, deltaT);
		}

		handleBallOnBallCollision(balls);

	}

	private void handleBallOnBallCollision(Ball[] balls) {
		for (int i = 0; i < balls.length; i++) {
			Ball ball1 = balls[i];

			for (int j = 0; j < balls.length; j++) {
				if (j > i) {  // Don't repeat ball comparisons and don't compare a ball with itself
					Ball ball2 = balls[j];

					if (isBallCollision(ball1, ball2)) {
						bounceTimeBaby(ball1, ball2);
					}
				}

			}
		}
	}

	private boolean isBallCollision(Ball ball1, Ball ball2) {
		double distance = distanceBetweenBalls(ball1, ball2);

		// Returns true if distance between the centre of the two balls is less than the sum of their radii
		return distance < (ball1.radius + ball2.radius);
	}

	private void bounceTimeBaby(Ball ball1, Ball ball2) {
		preventBallsOverlapping(ball1, ball2);
		handleElasticCollision(ball1, ball2);
	}

	private void preventBallsOverlapping(Ball ball1, Ball ball2) {
		double overlapAmount = (ball1.radius + ball2.radius) - distanceBetweenBalls(ball1, ball2);
		double amountMove = overlapAmount / 2;

		separateInDirectionOfMovement(ball1, ball2, amountMove);
	}

	private void separateInDirectionOfMovement(Ball ball1, Ball ball2, double amountMove) {
		double normalX = xDistanceBetweenBalls(ball1, ball2) / distanceBetweenBalls(ball1, ball2);
		double normalY = yDistanceBetweenBalls(ball1, ball2) / distanceBetweenBalls(ball1, ball2);

		if (ball1OnLeft(ball1, ball2)) {
			fixPositionWhenBall1Left(ball1, ball2, amountMove, normalX);
		} else {
			fixPositionWhenBall2Left(ball1, ball2, amountMove, normalX);
		}

		if (ball1OnTop(ball1, ball2)) {
			fixPositionWhenBall1Top(ball1, ball2, amountMove, normalY);
		} else {
			fixPositionWhenBall2Top(ball1, ball2, amountMove, normalY);
		}
	}

	private void fixPositionWhenBall1Left(Ball ball1, Ball ball2, double amountMove, double normalX) {
		ball1.x -= (normalX * amountMove);
		ball2.x += (normalX * amountMove);
	}

	private void fixPositionWhenBall2Left(Ball ball1, Ball ball2, double amountMove, double normalX) {
		ball2.x -= (normalX * amountMove);
		ball1.x += (normalX * amountMove);
	}

	private void fixPositionWhenBall1Top(Ball ball1, Ball ball2, double amountMove, double normalY) {
		ball1.y += (normalY * amountMove);
		ball2.y -= (normalY * amountMove);
	}

	private void fixPositionWhenBall2Top(Ball ball1, Ball ball2, double amountMove, double normalY) {
		ball2.y += (normalY * amountMove);
		ball1.y -= (normalY * amountMove);
	}

	private boolean ball1OnLeft(Ball ball1, Ball ball2) {
		return ball1.x < ball2.x;
	}

	private boolean ball1OnTop(Ball ball1, Ball ball2) {
		return ball1.y > ball2.y;
	}

	private double distanceBetweenBalls(Ball ball1, Ball ball2) {
		// Computes the distance between two points (x1, y1) and (x2, y2)
		// using the Euclidian distance formula:  distance = sqrt(  (x2 - x1)^2 + (y2-y1)^2  )
		return Math.sqrt( square(ball2.x - ball1.x) + square(ball2.y - ball1.y)  );
	}

	private double xDistanceBetweenBalls(Ball ball1, Ball ball2) {
		return Math.abs(ball2.x - ball1.x);
	}

	private double yDistanceBetweenBalls(Ball ball1, Ball ball2) {
		return Math.abs(ball2.y - ball1.y);
	}

	private void handleElasticCollision(Ball ball1, Ball ball2) {
		// TODO: Tangential velocity unchanged?

		/*
			"u" represents velocity before collision, "v" represents velocity after collision
		*/

		// Conservation of momentum before collision
		// I = m1u1 + m2u2
		double momentumX = (ball1.weight * ball1.vx) + (ball2.weight * ball2.vx);
		double momentumY = (ball1.weight * ball1.vy) + (ball2.weight * ball2.vy);

		// Conservation of energy before collision
		// R = u2 − u1
		double relativeVelocityX = ball2.vx - ball1.vx;
		double relativeVelocityY = ball2.vy - ball1.vy;

		/* System of equations for after collision */
		//	(1) m1v1 + m2v2 = I
		//	(2) v2 − v1 = −R
		//  	v2 = v1 - R
		//  insert v2 into (1)
		//  	m1v1 + (m2(v1 - R)) = I
		//  	m1v1 + m2v1 - m2*R = I
		//  	v1(m1 + m2) - m2*R = I

		//  So,
		//  	v1 = I + m2*R / (m1 + m2)
		//  insert v1 into (2) and solve for v2
		//  	v2 = (I + m2*R / (m1 + m2)) - R
		//		v2 = I + m2*R - R(m1 + m2) / (m1 + m2)
		//		v2 = I - m1*R / (m1 + m2)

		double sumWeights = ball1.weight + ball2.weight;

		// v1 = I + m2*R / (m1 + m2)
		ball1.vx = (momentumX + (ball2.weight*relativeVelocityX)) / sumWeights;
		ball1.vy = (momentumY + (ball2.weight*relativeVelocityY)) / sumWeights;

		// v2 = I - m1*R / (m1 + m2)
		ball2.vx = (momentumX - (ball1.weight * relativeVelocityX)) / sumWeights;
		ball2.vy = (momentumY - (ball1.weight * relativeVelocityY)) / sumWeights;
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
