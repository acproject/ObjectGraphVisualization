package ch.hsr.ogv.view;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import ch.hsr.ogv.util.GeometryUtil;
import ch.hsr.ogv.view.ArrowHead.ArrowHeadType;

/**
 * 
 * @author Simon Gwerder, Adrian Rieser
 *
 */
public class Arrow extends Group {

	private static final int INIT_WIDTH = 2;
	private double width = INIT_WIDTH;
	private double length;
	private double angle;

	private double gap = 3;

	private Point3D startPoint;
	private Point3D endPoint;

	private Box line;
	private ArrowHead head;
	private Color color = Color.BLACK;
	
	public Arrow(Point3D startPoint, Point3D endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.line = new Box(this.width, this.width, this.length);
		drawArrow();
	}
	
	public void drawBasedOnBoxes(PaneBox startBox, PaneBox endBox) {
		this.startPoint = startBox.getCenterPoint();
		this.endPoint = endBox.getCenterPoint();
		Point2D startIntersection = lineBoxIntersection(this.endPoint, startBox);
		Point2D endIntersection = lineBoxIntersection(this.startPoint, endBox);
		if(startIntersection != null) this.startPoint = new Point3D(startIntersection.getX(), this.startPoint.getY(), startIntersection.getY());
		if(endIntersection != null) this.endPoint = new Point3D(endIntersection.getX(), endPoint.getY(), endIntersection.getY());
		drawArrow();
	}

	public Arrow(PaneBox startBox, PaneBox endBox) {
		this.line = new Box(this.width, this.width, this.length);
		drawBasedOnBoxes(startBox, endBox);
	}
	
	private void drawArrow() {
		getTransforms().clear();
		this.length = this.startPoint.distance(this.endPoint);
		this.line.setDepth(this.length);
		
		setArrowHeadType(ArrowHeadType.OPEN);
		setColor(this.color);
		
		Point3D midPoint = this.startPoint.midpoint(this.endPoint);
		setTranslateXYZ(midPoint);
		
		double angle = GeometryUtil.getAngleBetweenXandZ(this.startPoint, this.endPoint);
		addRotate(angle);
	}

	private Point2D lineBoxIntersection(Point3D externalPoint, PaneBox box) {
		Point3D boxCenter = box.getCenterPoint();
		double halfWidth = box.getWidth() / 2;
		double halfHeight = box.getHeight() / 2;
		
		Point2D lineStart = new Point2D(externalPoint.getX(), externalPoint.getZ());
		Point2D lineEnd   = new Point2D(boxCenter.getX(), boxCenter.getZ());
		Point2D northEast  = new Point2D(boxCenter.getX() - halfWidth, boxCenter.getZ() + halfHeight);
		Point2D southEast  = new Point2D(boxCenter.getX() - halfWidth, boxCenter.getZ() - halfHeight);
		Point2D southWest  = new Point2D(boxCenter.getX() + halfWidth, boxCenter.getZ() - halfHeight);
		Point2D northWest  = new Point2D(boxCenter.getX() + halfWidth, boxCenter.getZ() + halfHeight);
		
		Point2D interEastHeight = GeometryUtil.lineIntersect(lineStart, lineEnd, northEast, southEast);
		Point2D interWestHeight = GeometryUtil.lineIntersect(lineStart, lineEnd, northWest, southWest);
		Point2D interNorthWidth = GeometryUtil.lineIntersect(lineStart, lineEnd, northEast, northWest);
		Point2D interSouthWidth = GeometryUtil.lineIntersect(lineStart, lineEnd, southEast, southWest);
		
		if(interEastHeight != null) return interEastHeight;
		if(interWestHeight != null) return interWestHeight;
		if(interNorthWidth != null) return interNorthWidth;
		if(interSouthWidth != null) return interSouthWidth;
		return null;
	}
	
	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		this.line.setMaterial(material);
	}

	public Color getColor() {
		return this.color;
	}

	public void setArrowHeadType(ArrowHeadType type) {
		getChildren().clear();
		getChildren().add(this.line);
		this.head = new ArrowHead(type, this.color);
		this.head.setTranslateZ(this.length / 2 + this.gap);
		getChildren().add(this.head);
	}

	public void setTranslateXYZ(Point3D point) {
		setTranslateXYZ(point.getX(), point.getY(), point.getZ());
	}

	public void setTranslateXYZ(double x, double y, double z) {
		getTransforms().add(new Translate(x, y, z));
	}

	public void addRotate(double degree) {
		getTransforms().add(new Rotate(degree, getTranslateX(), getTranslateY(), getTranslateZ(), Rotate.Y_AXIS));
	}

	public double getWidth() {
		return this.width;
	}

	public double getLength() {
		return this.length;
	}

	public double getAngle() {
		return this.angle;
	}

}
