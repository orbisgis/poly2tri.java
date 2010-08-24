package org.poly2tri.examples.geotools;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;

import org.poly2tri.Poly2Tri;
import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.polygon.PolygonSet;
import org.poly2tri.transform.coordinate.CoordinateTransform;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ardor3d.example.ExampleBase;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.image.Image;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonClickedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.BlendState.BlendEquation;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.extension.Skybox;
import com.ardor3d.scenegraph.extension.Skybox.Face;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;

import com.google.inject.Inject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Hello world!
 *
 */
public class WorldExample extends P2TSimpleExampleBase
{
    private final static Logger logger = LoggerFactory.getLogger( WorldExample.class );

    private final static CoordinateTransform _wgs84 = new WGS84GeodeticTransform(100);
    
    private Node _worldNode;
    private Skybox _skybox;
    private final Matrix3 rotate = new Matrix3();
    private double angle = 0;
    private boolean _doRotate = true;

    /**
     * We use one PolygonSet for each country since countries can have islands 
     * and be composed of multiple polygons 
     */
    private ArrayList<PolygonSet> _countries = new ArrayList<PolygonSet>();
    
    @Inject
    public WorldExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    public static void main( String[] args )
        throws Exception
    {
        try
        {
            start(WorldExample.class);
        }
        catch( RuntimeException e )
        {
            logger.error( "WorldExample failed due to a runtime exception" );
        }
    }

    @Override
    protected void updateExample( ReadOnlyTimer timer )
    {
        if( _doRotate )
        {
            angle += timer.getTimePerFrame() * 10;
            angle %= 360;
            rotate.fromAngleNormalAxis(angle * MathUtils.DEG_TO_RAD, Vector3.UNIT_Z);
            _worldNode.setRotation(rotate);
        }        
    }
    
    @Override
    protected void initExample()
    {
        super.initExample();

        try
        {
            importShape(100);
        }
        catch( IOException e )
        {
            
        }
       
        _canvas.getCanvasRenderer().getCamera().setLocation(200, 200, 200);
        _canvas.getCanvasRenderer().getCamera().lookAt( 0, 0, 0, Vector3.UNIT_Z );
        
        _worldNode = new Node("shape");
//        _worldNode.setRenderState( new WireframeState() );
        _node.attachChild( _worldNode );

        buildSkyBox();
        
        Sphere seas = new Sphere("seas", Vector3.ZERO, 64, 64, 100.2f);
        seas.setDefaultColor( new ColorRGBA(0,0,0.5f,0.25f) );
        seas.getSceneHints().setRenderBucketType( RenderBucketType.Transparent );
        BlendState bs = new BlendState();
        bs.setBlendEnabled( true );
        bs.setEnabled( true );
        bs.setBlendEquationAlpha( BlendEquation.Max );
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        seas.setRenderState( bs );
        ZBufferState zb = new ZBufferState();
        zb.setEnabled( true );
        zb.setWritable( false );
        seas.setRenderState( zb );
        _worldNode.attachChild( seas );

        Sphere core = new Sphere("seas", Vector3.ZERO, 16, 16, 10f);
        core.getSceneHints().setLightCombineMode( LightCombineMode.Replace );
        MaterialState ms = new MaterialState();
        ms.setEmissive( new ColorRGBA(0.8f,0.2f,0,0.9f) );
        core.setRenderState( ms );
        _worldNode.attachChild( core );

        Mesh mesh;
        for( PolygonSet ps : _countries )
        {
            Poly2Tri.triangulate( ps );
            float value = 1-0.9f*(float)Math.random();
            for( Polygon p : ps.getPolygons() )
            {
                mesh = new Mesh();
                mesh.setDefaultColor( new ColorRGBA( value, value, value, 1.0f ) );
                _worldNode.attachChild( mesh );
              
                ArdorMeshMapper.updateTriangleMesh( mesh, p, _wgs84 );
            }
        }
    }
    
    protected void importShape( double rescale )
        throws IOException
    {
//        URL url = WorldExample.class.getResource( "/z5UKI.shp" );
        URL url = WorldExample.class.getResource( "/earth/countries.shp" );
        url.getFile();
        ShapefileDataStore ds = new ShapefileDataStore(url);
        FeatureSource featureSource = ds.getFeatureSource();

//        for( int i=0; i < ds.getTypeNames().length; i++)
//        {
//            System.out.println("ShapefileDataStore.typename=" + ds.getTypeNames()[i] );
//        }
        
        FeatureCollection fc = featureSource.getFeatures();
        
//        System.out.println( "featureCollection.ID=" + fc.getID() );
//        System.out.println( "featureCollection.schema=" + fc.getSchema() );
//        System.out.println( "featureCollection.Bounds[minX,maxX,minY,maxY]=[" 
//                            + fc.getBounds().getMinX() + "," +
//                            + fc.getBounds().getMaxX() + "," +
//                            + fc.getBounds().getMinY() + "," +
//                            + fc.getBounds().getMaxY() + "]" );
//        double width, height, xScale, yScale, scale, dX, dY;
//        width = fc.getBounds().getMaxX() - fc.getBounds().getMinX();
//        height = fc.getBounds().getMaxY() - fc.getBounds().getMinY();
//        dX = fc.getBounds().getMinX() + width/2;
//        dY = fc.getBounds().getMinY() + height/2;
//        xScale = rescale * 1f / width;
//        yScale = rescale * 1f / height;
//        scale = xScale < yScale ? xScale : yScale; 

        FeatureIterator fi;
        Feature f;
        GeometryAttribute geoAttrib;
        
        Polygon polygon;
        PolygonSet polygonSet;
        fi = fc.features();
        while( fi.hasNext() )
        {
            polygonSet = new PolygonSet();
            f = fi.next();
            geoAttrib = f.getDefaultGeometryProperty();
//            System.out.println( "Feature.Identifier:" + f.getIdentifier() );
//            System.out.println( "Feature.Name:" + f.getName() );
//            System.out.println( "Feature.Type:" + f.getType() );
//            System.out.println( "Feature.Descriptor:" + geoAttrib.getDescriptor() );
//            System.out.println( "GeoAttrib.Identifier=" + geoAttrib.getIdentifier() );
//            System.out.println( "GeoAttrib.Name=" + geoAttrib.getName() );
//            System.out.println( "GeoAttrib.Type.Name=" + geoAttrib.getType().getName() );
//            System.out.println( "GeoAttrib.Type.Binding=" + geoAttrib.getType().getBinding() );
//            System.out.println( "GeoAttrib.Value=" + geoAttrib.getValue() );
            if( geoAttrib.getType().getBinding() == MultiLineString.class )
            {
                MultiLineString mls = (MultiLineString)geoAttrib.getValue();
                Coordinate[] coords = mls.getCoordinates();
//                System.out.println( "MultiLineString.coordinates=" + coords.length );
                ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>(coords.length);
                for( int i=0; i<coords.length; i++)
                {
                    points.add( new PolygonPoint(coords[i].x,coords[i].y) );
//                    System.out.println( "[x,y]=[" + coords[i].x + "," + coords[i].y + "]" );
                }
                polygonSet.add( new Polygon(points) );
            }
            else if( geoAttrib.getType().getBinding() == MultiPolygon.class )
            {
                MultiPolygon mp = (MultiPolygon)geoAttrib.getValue();
//                System.out.println( "MultiPolygon.NumGeometries=" + mp.getNumGeometries() );
                for( int i=0; i<mp.getNumGeometries(); i++ )
                {
                    com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon)mp.getGeometryN(i);
                    polygon = buildPolygon( jtsPolygon );
                    polygonSet.add( polygon );                
                }
            }
            _countries.add( polygonSet );
        }                
    }
    
    private static Polygon buildPolygon( com.vividsolutions.jts.geom.Polygon jtsPolygon )
    {
        Polygon polygon;
        LinearRing shell;
        ArrayList<PolygonPoint> points;
//        Envelope envelope;
        
//        System.out.println( "MultiPolygon.points=" + jtsPolygon.getNumPoints() );                    
//        System.out.println( "MultiPolygon.NumInteriorRing=" + jtsPolygon.getNumInteriorRing() );
//        envelope = jtsPolygon.getEnvelopeInternal();
        shell = (LinearRing)jtsPolygon.getExteriorRing();
        Coordinate[] coords = shell.getCoordinates();
        points = new ArrayList<PolygonPoint>(coords.length);
        // Skipping last coordinate since JTD defines a shell as a LineString that start with 
        // same first and last coordinate
        for( int j=0; j<coords.length-1; j++)
        {
            points.add( new PolygonPoint(coords[j].x,coords[j].y) );
        }
        polygon = new Polygon(points);
        return polygon;        
    }
//    
//    private void refinePolygon()
//    {
//        
//    }

    /**
     * Builds the sky box.
     */
    private void buildSkyBox() 
    {
        _skybox = new Skybox("skybox", 300, 300, 300);

        try {
            SimpleResourceLocator sr2 = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource("org/poly2tri/examples/geotools/textures/"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, sr2);
        } catch (final URISyntaxException ex) {
            ex.printStackTrace();
        }

        final String dir = "";
        final Texture stars = TextureManager.load(dir + "stars.gif", 
                                                  Texture.MinificationFilter.Trilinear,
                                                  Image.Format.GuessNoCompression, true);
        _skybox.setTexture(Skybox.Face.North, stars);
        _skybox.setTexture(Skybox.Face.West, stars);
        _skybox.setTexture(Skybox.Face.South, stars);
        _skybox.setTexture(Skybox.Face.East, stars);
        _skybox.setTexture(Skybox.Face.Up, stars);
        _skybox.setTexture(Skybox.Face.Down, stars);
        _skybox.getTexture( Skybox.Face.North ).setWrap( WrapMode.Repeat );
        for( Face f : Face.values() )
        {
            FloatBufferData fbd = _skybox.getFace(f).getMeshData().getTextureCoords().get( 0 );
            fbd.getBuffer().clear();
            fbd.getBuffer().put( 0 ).put( 4 );
            fbd.getBuffer().put( 0 ).put( 0 );
            fbd.getBuffer().put( 4 ).put( 0 );
            fbd.getBuffer().put( 4 ).put( 4 );            
        }
        _node.attachChild( _skybox );
    }

    @Override
    public void registerInputTriggers()
    {
        super.registerInputTriggers();
        
        // SPACE - toggle models
        _logicalLayer.registerTrigger( new InputTrigger( new MouseButtonClickedCondition(MouseButton.RIGHT), new TriggerAction() {
            public void perform( final Canvas canvas, final TwoInputStates inputState, final double tpf )
            {
                _doRotate = _doRotate ? false : true;
            }
        } ) );  
    }

    
    /*
     * http://en.wikipedia.org/wiki/Longitude#Degree_length
     * http://www.colorado.edu/geography/gcraft/notes/datum/gif/llhxyz.gif
     * 
     * x (in m) = Latitude * 60 * 1852 
     * y (in m) = (PI/180) * cos(Longitude) * (637813.7^2 / sqrt( (637813.7 * cos(Longitude))^2 + (635675.23 * sin(Longitude))^2 ) ) 
     * z (in m) = Altitude
     * 
     * The 'quick and dirty' method (assuming the Earth is a perfect sphere):
     * 
     * x = longitude*60*1852*cos(latitude) 
     * y = latitude*60*1852
     * 
     * Latitude and longitude must be in decimal degrees, x and y are in meters.
     * The origin of the xy-grid is the intersection of the 0-degree meridian
     * and the equator, where x is positive East and y is positive North.
     * 
     * So, why the 1852? I'm using the (original) definition of a nautical mile
     * here: 1 nautical mile = the length of one arcminute on the equator (hence
     * the 60*1852; I'm converting the lat/lon degrees to lat/lon minutes).
     */
}
