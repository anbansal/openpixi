package org.openpixi.pixi.physics.collision.Algorithms;

import java.util.ArrayList;

import org.openpixi.pixi.physics.Particle2D;
import org.openpixi.pixi.physics.collision.detectors.*;
import org.openpixi.pixi.physics.collision.util.Pair;
import org.openpixi.pixi.physics.force.Force;
import org.openpixi.pixi.physics.solver.Solver;

public class TransformationMatrix extends CollisionAlgorithm{
	
	//private Detector det;
	
	public TransformationMatrix() {
		
		super();
	}
	
	public void doCollision(Particle2D p1, Particle2D p2) {
		
		//distance between the particles
		double distance = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
		
		//finding the unit distance vector
		double dnX = (p1.x - p2.x) / distance;
		double dnY = (p1.y - p2.y) / distance;
		
		//finding the tangential vector;
		double dtX = dnY;
		//double dtY = - dtX;
		
		//finding the minimal distance if the ball are overlapping
		double minDistanceX = dnX * (p1.radius + p2.radius - distance);
		double minDistanceY = dnY * (p1.radius + p2.radius - distance);
		
		//moving the balls if they are overlapping (if not, the minimal distance is equal to zero)
		p1.x += minDistanceX * p2.mass / (p1.mass + p2.mass);
		p1.y += minDistanceY * p2.mass / (p1.mass + p2.mass);
		p2.x -= minDistanceX * p1.mass / (p1.mass + p2.mass);
		p2.y -= minDistanceY * p1.mass / (p1.mass + p2.mass);
		
		//double convert = Math.PI / 180;
		double phi = 0.0;
		
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		
		//finding the angle between the normal coordinate system and the system, where the x - axis is the collision line
		if(dx == 0.)
			phi = Math.PI;
		else
			phi = Math.atan(dy / dx);
		
		//double v1 = Math.sqrt(p1.vx * p1.vx + p1.vy * p1.vy);
		//double v2 = Math.sqrt(p2.vx * p2.vx + p2.vy * p2.vy);
		
		//double theta1 = Math.atan(p1.vy / p1.vx);
		//double theta2 = Math.atan(p2.vy / p2.vx);
		
		//calculating the velocities in the new coordinate system
		//double v1xNewCoor = v1 * Math.cos(theta1 - phi);
		//double v1yNewCoor = v1 * Math.sin(theta1 - phi);
		//double v2xNewCoor = v2 * Math.cos(theta2 - phi);
		//double v2yNewCoor = v2 * Math.sin(theta2 - phi);
		double v1xNewCoor = p1.vx * Math.cos(phi) + p1.vy * Math.sin(phi);
		double v1yNewCoor = - p1.vx * Math.sin(phi) + p1.vy * Math.cos(phi);
		double v2xNewCoor = p2.vx * Math.cos(phi) + p2.vy * Math.sin(phi);
		double v2yNewCoor = - p2.vx * Math.sin(phi) + p2.vy * Math.sin(phi);
		
		//calculating the new velocities in the new coordinate system
		//http://en.wikipedia.org/wiki/Elastic_collision
		double newv1xNewCoor = ((p1.mass - p2.mass) * v1xNewCoor + 2 * p2.mass * v2xNewCoor) / (p1.mass + p2.mass);
		double newv2xNewCoor = (2 * p1.mass * v1xNewCoor + (p2.mass - p1.mass) * v2xNewCoor) / (p1.mass + p2.mass);
		
		//going in the old coordinate system, do not forget that the y coordinates in the new coordinate system have not changed
		//also I am using here that cos(pi + x) = - sin(x) & sin(pi + x) = cos(x)
		p1.vx = newv1xNewCoor * Math.cos(phi) + v1yNewCoor * Math.cos(phi + Math.PI);
		p1.vy = newv1xNewCoor * Math.sin(phi) + v1yNewCoor * Math.sin(phi + Math.PI);
		p2.vx = newv2xNewCoor * Math.cos(phi) + v2yNewCoor * Math.cos(phi + Math.PI);
		p2.vy = newv2xNewCoor * Math.sin(phi) + v2yNewCoor * Math.sin(phi + Math.PI);
	}
	
	public void collide(ArrayList<Pair<Particle2D, Particle2D>> pairs, Force f, Solver s, double step) {
		
		for(int i = 0; i < pairs.size(); i++) {
			Particle2D p1 = (Particle2D) pairs.get(i).getFirst();
			Particle2D p2 = (Particle2D) pairs.get(i).getSecond();
		
			double distance = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
			if(distance <= (p1.radius + p2.radius)) {
				s.complete(p1, f, step);
				s.complete(p2, f, step);
				doCollision(p1, p2);
				System.out.println("Collision! -> " + distance);
				s.prepare(p1, f, step);
				s.prepare(p2, f, step);
			}
		}
	}
}
