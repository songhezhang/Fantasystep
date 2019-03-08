package com.fantasystep.component.layout;

import com.fantasystep.component.utils.LayoutUtil;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class GridContainerLayout extends AbtractContainerLayout
{

	public enum Frame
	{
		LEFT( 0 ), MIDDLE( 1 ), RIGHT( 2 );

		private int	index;

		private Frame( int index )
		{
			setIndex( index );
		}

		public int getIndex()
		{
			return index;
		}

		public void setIndex( int index )
		{
			this.index = index;
		}

	}

	private static final long	serialVersionUID	= -5038854192619590891L;

	private GridLayout			grid;
	private float				widthColLeft;
	private float				widthColMiddle;
	private float				widthColRight;

	public GridContainerLayout()
	{
		this( 0.20f, 0.70f, 0.10f );
	}

	public GridContainerLayout( float widthColLeft, float widthColMiddle, float widthColRight )
	{
		super();
		this.widthColLeft = widthColLeft;
		this.widthColMiddle = widthColMiddle;
		this.widthColRight = widthColRight;
		bindLayout();
	}

	public void addBodyComponent( Component component, Alignment alignment, Frame frame )
	{
		addBodyComponent( component, alignment, frame, null );
	}

	public void addBodyComponent( Component component, Alignment alignment, Frame frame, boolean isScrollable, boolean isTransparent )
	{
		addBodyComponent( LayoutUtil.addScrollablePanel( component, isTransparent ), alignment, frame, null );
	}

	public void addBodyComponent( Component component, Alignment alignment, Frame frame, boolean isScrollable, Integer height )
	{
		addBodyComponent( LayoutUtil.addScrollablePanel( component, height ), alignment, frame, height );
	}

	public void addBodyComponent( Component component, Alignment alignment, Frame frame, Integer height )
	{

		grid.addComponent( component, frame.getIndex(), 0 );
		grid.setComponentAlignment( component, alignment );

		if( height != null )
			grid.setHeight( height + "px" );

	}

	@Override
	public AbstractLayout getBodyContainer()
	{
		if( grid == null )
		{
			grid = new GridLayout( 3, 1 );
			grid.setWidth( "100%" );
			grid.setHeight( "100%" );
			grid.setSpacing( true );
			grid.setColumnExpandRatio( 0, widthColLeft );
			grid.setColumnExpandRatio( 1, widthColMiddle );
			grid.setColumnExpandRatio( 2, widthColRight );
			grid.setRowExpandRatio( 0, 1f );
			grid.setSizeFull();
		}
		return grid;
	}

	public Component getLeftFrame()
	{
		return grid.getComponent( Frame.LEFT.getIndex(), 0 );
	}

	public Component getMiddleFrame()
	{
		return grid.getComponent( Frame.MIDDLE.getIndex(), 0 );
	}

	public Component getRightFrame()
	{
		return grid.getComponent( Frame.RIGHT.getIndex(), 0 );
	}

}
