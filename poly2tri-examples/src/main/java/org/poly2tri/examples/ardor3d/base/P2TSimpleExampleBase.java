package org.poly2tri.examples.ardor3d.base;

import java.net.URISyntaxException;

import org.lwjgl.opengl.Display;

import com.ardor3d.example.ExampleBase;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;
import com.google.inject.Inject;

public abstract class P2TSimpleExampleBase extends ExampleBase
{
    protected Node _node;
    protected Quad _logotype;

    protected int _width,_height;

    @Inject
    public P2TSimpleExampleBase( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {
        _canvas.setVSyncEnabled( true );

        _canvas.getCanvasRenderer().getCamera().setLocation(0, 0, 65);

        _width = Display.getDisplayMode().getWidth();
        _height = Display.getDisplayMode().getHeight();
        
        _root.getSceneHints().setLightCombineMode( LightCombineMode.Off );

        _node = new Node();
        _node.getSceneHints().setLightCombineMode( LightCombineMode.Off );
//        _node.setRenderState( new WireframeState() );
        _root.attachChild( _node );        
        
        try {
            SimpleResourceLocator srl = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource("org/poly2tri/examples/data/"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL, srl);
            SimpleResourceLocator sr2 = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource("org/poly2tri/examples/textures/"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, sr2);
        } catch (final URISyntaxException ex) {
            ex.printStackTrace();
        }
        
        _logotype = new Quad("box", 128, 128 );
        _logotype.setTranslation( 74, _height - 74, 0 );
        _logotype.getSceneHints().setRenderBucketType( RenderBucketType.Ortho );
        BlendState bs = new BlendState();
        bs.setBlendEnabled( true );
        bs.setEnabled( true );
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        _logotype.setRenderState( bs );
        TextureState ts = new TextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.load("poly2tri_logotype_256x256.png", 
                      Texture.MinificationFilter.Trilinear,
                      Format.GuessNoCompression, true));
        _logotype.setRenderState(ts);
        _root.attachChild( _logotype );

    }
}
