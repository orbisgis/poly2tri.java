package org.poly2tri.examples.ardor3d.misc;

public enum ExampleSets
{
    Example1        ("example1.dat",1,0,0,true),
    Example2        ("example2.dat",1,0,0,true),
    Example3        ("example3.dat",1,0,0,false),
    Example4        ("example4.dat",1,0,0,false);
    
    private final static String m_basePath = "org/poly2tri/examples/data/pointsets/"; 
    private String m_filename;
    private double m_scale;
    private double m_x;
    private double m_y;
    private boolean _invertedYAxis;
    
    ExampleSets( String filename, double scale, double x, double y, boolean invertedY )
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