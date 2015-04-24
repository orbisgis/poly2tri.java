package org.poly2tri.triangulation;

import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

/**
 * Minimum angle quality evaluator
 * @author Nicolas Fortin
 */
public class MinAngleQualityEvaluator implements QualityEvaluator {
    private double minAngle;

    public MinAngleQualityEvaluator(double minAngle) {
        this.minAngle = minAngle;
    }

    @Override
    public boolean isPoorQualityTriangle(DelaunayTriangle triangle) {
        return triangle.getSmalledNonConstrainedAngle() < minAngle;
    }
}
