package org.poly2tri;

import org.junit.Test;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.primitives.Point;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.sets.ConstrainedPointSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

/**
 * Test constrained mesh generation
 * @author Nicolas Fortin, CNRS 2488
 */
public class TestConstrainedDelaunay {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConstrainedDelaunay.class);

    private static PolygonPoint mkPt(double x, double y) {
        return new PolygonPoint(x, y);
    }
    private static PolygonPoint mkPt(double x, double y, double z) {
        return new PolygonPoint(x, y, z);
    }

    private static void pointsFromFile(URL dataUrl, MathContext mathContext, List<PolygonPoint> outerRing, List<ArrayList<PolygonPoint>> holes) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataUrl.getFile()));
        List<PolygonPoint> polygonPointList = outerRing;
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.isEmpty()) {
                    ArrayList<PolygonPoint> hole = new ArrayList<PolygonPoint>();
                    holes.add(hole);
                    polygonPointList = hole;
                } else {
                    StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                    double x = new BigDecimal(stringTokenizer.nextToken()).round(mathContext).doubleValue();
                    double y = new BigDecimal(stringTokenizer.nextToken()).round(mathContext).doubleValue();
                    if (stringTokenizer.hasMoreTokens()) {
                        double z = new BigDecimal(stringTokenizer.nextToken()).round(mathContext).doubleValue();
                        polygonPointList.add(mkPt(x, y, z));
                    } else {
                        polygonPointList.add(mkPt(x, y));
                    }
                }
            }
        } finally {
            bufferedReader.close();
        }
    }

    private ConstrainedPointSet LineSegsFromFile(URL file) throws IOException {
        List<PolygonPoint> outerRing = new ArrayList<PolygonPoint>();
        List<ArrayList<PolygonPoint>> holes = new ArrayList<ArrayList<PolygonPoint>>();
        pointsFromFile(file, MathContext.DECIMAL64, outerRing, holes);
        if(outerRing.size() % 2 != 0) {
            throw new IOException("Line segments size should be even");
        }
        Map<PolygonPoint, Integer> mergedPoints = new HashMap<PolygonPoint, Integer>(outerRing.size());
        int[] segments = new int[outerRing.size()];
        int index = 0;
        List<TriangulationPoint> pts = new ArrayList<TriangulationPoint>(outerRing.size());
        for(int rangeBegin = 0; rangeBegin < outerRing.size() - 1; rangeBegin++) {
            Integer firstRef = mergedPoints.get(outerRing.get(rangeBegin));
            if(firstRef == null) {
                firstRef = index++;
                mergedPoints.put(outerRing.get(rangeBegin), firstRef);
                pts.add(outerRing.get(rangeBegin));
            }
            segments[rangeBegin] = firstRef;
            firstRef = mergedPoints.get(outerRing.get(rangeBegin + 1));
            if(firstRef == null) {
                firstRef = index++;
                mergedPoints.put(outerRing.get(rangeBegin + 1), firstRef);
                pts.add(outerRing.get(rangeBegin + 1));
            }
            segments[rangeBegin + 1] = firstRef;
        }
        return new ConstrainedPointSet(pts, segments);
    }

    private Polygon polygonFromFile(URL file) throws IOException {
        List<PolygonPoint> outerRing = new ArrayList<PolygonPoint>();
        List<ArrayList<PolygonPoint>> holes = new ArrayList<ArrayList<PolygonPoint>>();
        pointsFromFile(file, MathContext.DECIMAL64, outerRing, holes);
        Polygon polygon = new Polygon(outerRing);
        for(List<PolygonPoint> hole : holes) {
            polygon.addHole(new Polygon(hole));
        }
        return polygon;
    }

    /**
     * Add WKT text of points
     * @param stringBuilder String to add to
     * @param pts Input pts
     */
    public static void addPts(StringBuilder stringBuilder, Point... pts) {
        AtomicBoolean first = new AtomicBoolean(true);
        for(Point pt : pts) {
            if(!first.getAndSet(false)) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(pt.getX());
            stringBuilder.append(" ");
            stringBuilder.append(pt.getY());
            stringBuilder.append(" ");
            stringBuilder.append(pt.getZ());
        }
    }

    /**
     * Convert triangles list into wkt form for debugging purpose
     * @param polygon Polygon
     * @return String WKT
     */
    public static String toWKT(Polygon polygon) {
        StringBuilder stringBuilder = new StringBuilder("POLYGON((");
        List<TriangulationPoint> pts = polygon.getPoints();
        addPts(stringBuilder, pts.toArray(new Point[pts.size()]));
        // Close linestring
        stringBuilder.append(", ");
        addPts(stringBuilder, pts.get(0));
        stringBuilder.append(")");
        if(!polygon.getHoles().isEmpty()) {
            for(Polygon poly : polygon.getHoles()) {
                stringBuilder.append(", (");
                List<TriangulationPoint> pts2 = poly.getPoints();
                addPts(stringBuilder, pts2.toArray(new Point[pts2.size()]));
                // Close linestring
                stringBuilder.append(", ");
                addPts(stringBuilder, pts2.get(0));
                stringBuilder.append(")");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    /**
     * Convert triangles list into wkt form for debugging purpose
     * @param triangles Triangle array
     * @return String WKT
     */
    public static String toWKT(List<DelaunayTriangle> triangles) {
        StringBuilder stringBuilder = new StringBuilder("MULTIPOLYGON(");
        AtomicBoolean first = new AtomicBoolean(true);
        for(DelaunayTriangle triangle : triangles) {
            if(!first.getAndSet(false)) {
                stringBuilder.append(",");
            }
            stringBuilder.append("((");
            TriangulationPoint[] pts = triangle.points;
            addPts(stringBuilder, pts);
            // Close linestring
            stringBuilder.append(", ");
            addPts(stringBuilder, pts[0]);
            stringBuilder.append("))");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    /**
     * Check for precision issue https://code.google.com/p/poly2tri/issues/detail?id=97
     * "crash with attached polygon"
     * Effectively crash with {@link java.math.MathContext#DECIMAL128}. However Poly2Tri is working with double,
     * then it is ok with double rounding.
     * @throws IOException
     */
    @Test
    public void testPolygonTessellation() throws IOException {
        Polygon polygon = polygonFromFile(TestConstrainedDelaunay.class.getResource("poly1.dat"));
        Poly2Tri.triangulate(polygon);
        assertEquals(122, polygon.getTriangles().size());
    }

    /**
     * Check "Banana" polygon. Polygon with a hole where hole point touch outer ring point.
     * @throws IOException
     */
    @Test
    public void testPolygonHoleTouchTessellation() throws IOException {
        Polygon polygon = polygonFromFile(TestConstrainedDelaunay.class.getResource("poly2.dat"));
        Poly2Tri.triangulate(polygon);
        //LOGGER.info(toWKT(polygon));
        //LOGGER.info(toWKT(polygon.getTriangles()));
        assertEquals(7, polygon.getTriangles().size());
    }

    /**
     * Check convex hull triangulation delaunay
     * @throws IOException
     */
    @Test
    public void testLineConstraints() throws IOException {
        ConstrainedPointSet segs = LineSegsFromFile(TestConstrainedDelaunay.class.getResource("linesegs1.dat"));
        Poly2Tri.triangulate(segs);
        //LOGGER.info(toWKT(segs.getTriangles()));
        assertEquals(8, segs.getTriangles().size());
    }
    /**
     * Check convex hull triangulation delaunay
     * @throws IOException
     */
    @Test
    public void testLineConstraints2() throws IOException {
        ConstrainedPointSet segs = LineSegsFromFile(TestConstrainedDelaunay.class.getResource("linesegs2.dat"));
        Poly2Tri.triangulate(segs);
        //LOGGER.info(toWKT(segs.getTriangles()));
        assertEquals(7, segs.getTriangles().size());
    }
}
