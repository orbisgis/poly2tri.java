Since there are no Input validation of the data given for triangulation you need to think about this.

1. Poly2Tri do not support multiple points with exact same Coordinate. (This is an issue when using constraints/polygons)
   a. So if you have a cyclic function that generates points make sure you don't add the same Coordinate twice.
   b. If you are given input and aren't sure same point exist twice you need to check for this yourself.
      Poly2Tri does a simple check if first and last point is same when you create a polygon but that is all the input 
      validation.
      
2. Poly2Tri do not support intersecting constraints(yet)
   a. This means only simple polygons are supported
   
   Getting Started:
   
   1. The first thing to do after you have downloaded the project with Mercurial is to compile and download
      the third party libraries.
      a. Make sure you have Maven2 installed. http://maven.apache.org/
      b. Just move to the root directory with a command line tool and write: 
         mvn install
         This should compile and fetch all 3rd party libraries needed. 