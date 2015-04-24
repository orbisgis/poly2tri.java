package org.poly2tri;

import org.junit.Test;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Nicolas Fortin
 */
public class TestRefinement {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRefinement.class);

    @Test
    public void testQualityPoly1() throws IOException {
        Polygon poly = TestConstrainedDelaunay.polygonFromFile(TestRefinement.class.getResource("poly1.dat"));
        Poly2Tri.triangulate(poly);
        // Check minimum quality obtained through refinement
        double minAngle = Double.MAX_VALUE;
        for(DelaunayTriangle triangle : poly.getTriangles()) {
            minAngle = Math.min(minAngle, triangle.getSmalledNonConstrainedAngle());
        }
        System.out.println("Min angle :"+minAngle);
    }
}
