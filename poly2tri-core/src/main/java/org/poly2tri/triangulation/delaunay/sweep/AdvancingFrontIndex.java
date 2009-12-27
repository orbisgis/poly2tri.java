package org.poly2tri.triangulation.delaunay.sweep;

public class AdvancingFrontIndex<A>
{
    IndexNode<A> _root;
    
    public AdvancingFrontIndex( int depth )
    {
        if( depth > 5 ) depth = 5;
        _root = createIndex( depth );
    }
    
    private IndexNode<A> createIndex( int n )
    {
        IndexNode<A> node = null;
        if( n > 0 )
        {
            node = new IndexNode<A>();
            node.bigger = createIndex( n-1 );
            node.smaller = createIndex( n-1 );
        }
        return node;
    }

    public A fetchAndRemoveIndex( A key )
    {
        return null;
    }
    
    public A fetchAndInsertIndex( A key )
    {
        return null;
    }

    class IndexNode<A>
    {
        A value;
        IndexNode<A> smaller;
        IndexNode<A> bigger;
        double range;
    }
}
