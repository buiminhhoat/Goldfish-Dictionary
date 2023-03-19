package com.goldfish_dictionary.recycler_view_adapter;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
