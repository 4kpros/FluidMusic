package com.prosabdev.fluidmusic.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration
{
    final private int spanCount,spacing,spacing_top;
    final private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing_left, int spacing_top)
    {
        this.spanCount = spanCount;
        this.spacing = spacing_left;
        this.includeEdge = true;
        this.spacing_top=spacing_top;
    }
    public GridSpacingItemDecoration(int spanCount)
    {
        this.spanCount = spanCount;
        this.spacing = 5;
        this.includeEdge = true;
        this.spacing_top = 5;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        int position = parent.getChildAdapterPosition(view); // item phases_position
        int column = position % spanCount; // item column

        if (includeEdge)
        {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount)
            { // top edge
                outRect.top = spacing_top;
            }
            outRect.bottom = spacing_top; // item bottom
        }
        else
        {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount)
            {
                outRect.top = spacing_top; // item top
            }
        }
    }
}