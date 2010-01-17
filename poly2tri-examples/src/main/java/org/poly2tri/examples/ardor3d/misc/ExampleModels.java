package org.poly2tri.examples.ardor3d.misc;

public enum ExampleModels
{
    Test            ("test.dat",1,0,0,true),
    Two             ("2.dat",1,0,0,true),
    Debug           ("debug.dat",1,0,0,false),
    Debug2          ("debug2.dat",1,0,0,false),
    Bird            ("bird.dat",1,0,0,false),
    Custom          ("funny.dat",1,0,0,false),
    Diamond         ("diamond.dat",1,0,0,false),
    Dude            ("dude.dat",1,-0.1,0,true),
    Nazca_heron     ("nazca_heron.dat",1.3,0,0.35,false),
    Nazca_monkey    ("nazca_monkey.dat",1,0,0,false),
    Star            ("star.dat",1,0,0,false),
    Strange         ("strange.dat",1,0,0,true),
    Tank            ("tank.dat",1.3,0,0,true);
    
    private final static String m_basePath = "org/poly2tri/examples/data/"; 
    private String m_filename;
    private double m_scale;
    private double m_x;
    private double m_y;
    private boolean _invertedYAxis;
    
    ExampleModels( String filename, double scale, double x, double y, boolean invertedY )
    {
        m_filename = filename;
        m_scale = scale;
        m_x = x;
        m_y = y;
        _invertedYAxis = invertedY;
    }
    
    public String getFilename()
    {
        return m_basePath + m_filename;
    }

    public double getScale()
    {
        return m_scale;
    }
    
    public double getX()
    {
        return m_x;
    }
    
    public double getY()
    {
        return m_y;
    }

    public boolean invertedYAxis()
    {
        return _invertedYAxis;
    }
}