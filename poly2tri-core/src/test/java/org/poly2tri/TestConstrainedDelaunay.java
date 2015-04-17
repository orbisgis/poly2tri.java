package org.poly2tri;

import org.junit.Test;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;

/**
 * Test constrained mesh generation
 * @author Nicolas Fortin, CNRS 2488
 */
public class TestConstrainedDelaunay {

    private static PolygonPoint mkPt(double x, double y) {
        return new PolygonPoint(x, y);
    }
    private static PolygonPoint mkPt(double x, double y, double z) {
        return new PolygonPoint(x, y, z);
    }

    private static  List<PolygonPoint> pointsFromFile(URL dataUrl, MathContext mathContext) throws IOException {
        List<PolygonPoint> polygonPointList = new ArrayList<PolygonPoint>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataUrl.getFile()));
        try {
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                    double x = new BigDecimal(stringTokenizer.nextToken()).round(mathContext).doubleValue();
                    double y = new BigDecimal(stringTokenizer.nextToken()).round(mathContext).doubleValue();
                    if(stringTokenizer.hasMoreTokens()) {
                        double z = new BigDecimal(stringTokenizer.nextToken()).round(mathContext).doubleValue();
                        polygonPointList.add(mkPt(x, y, z));
                    } else {
                        polygonPointList.add(mkPt(x, y));
                    }
                }
            } finally {
                bufferedReader.close();
            }
            return polygonPointList;
        } finally {
            bufferedReader.close();
        }
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
        List<PolygonPoint> pts = pointsFromFile(TestConstrainedDelaunay.class.getResource("poly1.dat"), MathContext.DECIMAL64);
        Polygon polygon = new Polygon(pts);
        Poly2Tri.triangulate(polygon);
        assertEquals(122, polygon.getTriangles().size());
    }

}
