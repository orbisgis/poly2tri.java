package org.poly2tri;

import org.junit.Test;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.sets.PointSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test non-constrained delaunay
 * @author Nicolas Fortin, CNRS 2488
 */
public class TestDelaunay {
    private static TriangulationPoint mkPt(double x, double y) {
        return new TPoint(x, y);
    }
    private static TriangulationPoint mkPt(double x, double y, double z) {
        return new TPoint(x, y, z);
    }

    @Test
    public void testIssue7() {
        // (0 0, 0 1, 1 1, 2 1)
        PointSet pointSet = new PointSet(Arrays.asList(mkPt(0, 0),mkPt(0, 1),mkPt(1, 1),mkPt(2,1)));
        Poly2Tri.triangulate(pointSet);
        List<DelaunayTriangle> triangles = pointSet.getTriangles();
        assertEquals(2, triangles.size());
        assertEquals(new DelaunayTriangle(mkPt(1, 1),mkPt(0,1), mkPt(0, 0)), triangles.get(0));
        assertEquals(new DelaunayTriangle(mkPt(0, 0),mkPt(2,1), mkPt(1, 1)), triangles.get(1));
    }

    private PointSet get3DPoints(List<TriangulationPoint> pts) {
        return new PointSet(pts);
    }
    private List<TriangulationPoint> get3DPointsList() {
        return Arrays.asList(
                mkPt(12, 10, 2),  // 0
                mkPt(120, 10, 20),// 1
                mkPt(12, 100, 12),// 2
                mkPt(102, 100, 1),// 3
                mkPt(52, 100, 1), // 4
                mkPt(10, 50, 5),  // 5
                mkPt(50, 50, 1),  // 6
                mkPt(150, 50, 11),// 7
                mkPt(50, 150, 2), // 8
                mkPt(5, 50, 3),   // 9
                mkPt(5, 5, 10)    // 10
        );
    }

    /**
     * Fetch index on triangles points
     * @param p Point list
     * @param triangle Triangle points to search in p
     * @return String index
     */
    private String debugOrder(List<TriangulationPoint> p, DelaunayTriangle triangle) {
        // Fetch index of points
        int[] index = new int[]{p.indexOf(triangle.points[0]), p.indexOf(triangle.points[1]),
                p.indexOf(triangle.points[2])};
        return Arrays.toString(index);
    }

    @Test
    public void test3dPoints() {
        List<TriangulationPoint> p = get3DPointsList();
        PointSet pt = get3DPoints(p);
        Set<DelaunayTriangle> expectedTriangles = new HashSet<DelaunayTriangle>();
        expectedTriangles.addAll(Arrays.asList(
                new DelaunayTriangle(p.get(9),p.get(5),p.get(2)),
                new DelaunayTriangle(p.get(2),p.get(5),p.get(6)),
                new DelaunayTriangle(p.get(2),p.get(6),p.get(4)),
                new DelaunayTriangle(p.get(8),p.get(2),p.get(4)),
                new DelaunayTriangle(p.get(8),p.get(4),p.get(3)),
                new DelaunayTriangle(p.get(6),p.get(3),p.get(4)),
                new DelaunayTriangle(p.get(1),p.get(3),p.get(6)),
                new DelaunayTriangle(p.get(1),p.get(7),p.get(3)),
                new DelaunayTriangle(p.get(1),p.get(0),p.get(10)),
                new DelaunayTriangle(p.get(9),p.get(10),p.get(0)),
                new DelaunayTriangle(p.get(5),p.get(9),p.get(0)),
                new DelaunayTriangle(p.get(5),p.get(0),p.get(6)),
                new DelaunayTriangle(p.get(6),p.get(0),p.get(1))
                ));
        Poly2Tri.triangulate(pt);
        assertEquals(expectedTriangles.size(), pt.getTriangles().size());
        for(DelaunayTriangle tri : pt.getTriangles()) {
            assertTrue("Could not find "+tri, expectedTriangles.contains(tri));
        }
    }

    //@Test
    // Produced triangulation is not convex
    public void testConvexHullProblem() {
        TriangulationPoint[] pts = new TriangulationPoint[] {
                mkPt(4, 7),
                mkPt(3.2, 4.6),
                mkPt(4.1, 5.2),
                mkPt(6, 5)};
        PointSet ps = new PointSet(Arrays.asList(pts));
        Poly2Tri.triangulate(ps);
        assertEquals(3, ps.getTriangles().size());
    }
}
