package com.princedragons.geoexpert;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class GeoTextView extends TextView
{
	    public GeoTextView(Context context, AttributeSet attrs, int defStyle) 
	    {
	        super(context, attrs, defStyle);
	        init();
	    }

	    public GeoTextView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        init();
	    }

	    public GeoTextView(Context context) {
	        super(context);
	        init();
	    }

	    private void init() {
	        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
	                                               "Geo.ttf");
	        setTypeface(tf);
	    }
}
