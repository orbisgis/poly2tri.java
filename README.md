# Poly2Tri

Contribution to poly2tri project:
https://github.com/greenm01/poly2tri.java

This is a temporary fork, in order to add various features.

## How to use

```java
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    // Prepare input data
    Polygon polygon = new Polygon(Arrays.asList(new PolygonPoint(0, 0, 0),
      new PolygonPoint(10, 0, 1),new PolygonPoint(10, 10, 2),new PolygonPoint(0, 10, 3)));
    // Launch tessellation  
    Poly2Tri.triangulate(polygon);
    // Gather triangles
    List<DelaunayTriangle> triangles = polygon.getTriangles();
  }
}
```
