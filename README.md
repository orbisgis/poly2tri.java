# Poly2Tri

Contribution to poly2tri project:
https://github.com/greenm01/poly2tri.java

This is a temporary fork, in order to add various features.



## A 2D constrained Delaunay triangulation library

**Based on the paper "Sweep-line algorithm for constrained Delaunay triangulation" by V. Domiter and and B. Zalik**

Officially supported langs: [C++](https://github.com/zzzzrrr/poly2tri), [Java](https://github.com/greenm01/poly2tri.java)

<p>Community based langs (unsupported):<br>
  * <a href="https://github.com/zzzzrrr/poly2tri.as3">AS3</a><br>
  * <a href="https://github.com/Paul-Browne/poly2tri-c">C</a><br>
  * <a href="https://github.com/zzzzrrr/poly2tri.cs">C#</a>, <a href="https://github.com/MaulingMonkey/poly2tri-cs">C#(basic)</a><br>
  * <a href="https://github.com/zzzzrrr/poly2tri.golang">Go</a><br>
  * <a href="https://github.com/nerik/poly2trihx">Haxe</a><br>
  * <a href="https://github.com/r3mi/poly2tri.js">Javascript</a><br>
  * <a href="https://github.com/zzzzrrr/poly2tri.python">Python</a>, <a href="https://github.com/davidcarne/poly2tri.python">Python3</a><br>
  * <a href="https://github.com/mieko/rbpoly2tri">Ruby</a></p>

<p>Try it out online: click <a href="http://r3mi.github.io/poly2tri.js/">me</a> or <a href="http://nerik.github.io/poly2trihx/">me</a>!</p>

Video
<a href="http://www.youtube.com/watch?v=Bt1TYzzr2Rg">poly2tri-java</a></p>

<p>If you want to triangulate complex or weak polygons you will need to prepare your data
with a polygon clipping library like:<br>
<a href="http://sourceforge.net/projects/polyclipping/">Clipper</a> (C++, C#, Delphi)<br>
<a href="http://sourceforge.net/projects/jsclipper/">Javascript Clipper</a><br>
<a href="https://github.com/dmac100/clipper">Java Clipper</a></p></markdown-widget>
</div>
  
<a href="http://opensource.org/licenses/BSD-3-Clause"><b>BSD 3-Clause License</b></a>


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
